package dao;

import dbcontext.DBContext;
import interfa.IExamSetDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.ExamSet;
import model.Question;

public class ExamSetDAO extends DBContext implements IExamSetDAO {

    /**
     * Lấy tổng số lượng đề thi trong hệ thống.
     * Hàm này đếm tất cả các bản ghi trong bảng ExamSets.
     * 
     * @return Tổng số lượng đề thi, trả về 0 nếu không có dữ liệu hoặc có lỗi
     */
    public int getTotalExamSets() {
        String sql = "SELECT COUNT(*) AS total FROM ExamSets";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return 0;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException ex) {
        }
        return 0;
    }

    /**
     * Lấy danh sách tất cả đề thi với phân trang, kèm thông tin danh mục.
     * Hàm này JOIN với ExamQuestions, Questions và QuestionCategories để lấy
     * category_id và category_name.
     * Sắp xếp theo exam_set_id tăng dần.
     * 
     * @param offset Số lượng bản ghi cần bỏ qua (dùng cho phân trang)
     * @param limit  Số lượng bản ghi tối đa cần lấy
     * @return Danh sách các đối tượng ExamSet có kèm categoryId và categoryName,
     *         danh sách rỗng nếu không có dữ liệu
     */
    @Override
    public List<ExamSet> getExamSets(int offset, int limit) {
        List<ExamSet> examSets = new ArrayList<>();
        String sql = """
                SELECT es.exam_set_id, es.exam_name, es.total_questions, es.duration_minutes, es.passing_score,
                       MIN(q.category_id) AS category_id, MIN(qc.category_name) AS category_name
                FROM ExamSets es
                LEFT JOIN ExamQuestions eq ON es.exam_set_id = eq.exam_set_id
                LEFT JOIN Questions q ON eq.question_id = q.question_id
                LEFT JOIN QuestionCategories qc ON q.category_id = qc.category_id
                GROUP BY es.exam_set_id, es.exam_name, es.total_questions, es.duration_minutes, es.passing_score
                ORDER BY es.exam_set_id
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """;
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return examSets;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ExamSet examSet = new ExamSet();
                examSet.setExamSetId(rs.getInt("exam_set_id"));
                examSet.setExamName(rs.getString("exam_name"));
                examSet.setTotalQuestions(rs.getInt("total_questions"));
                examSet.setDurationMinutes(rs.getInt("duration_minutes"));
                examSet.setPassingScore(rs.getInt("passing_score"));
                int catId = rs.getInt("category_id");
                if (!rs.wasNull()) {
                    examSet.setCategoryId(catId);
                }
                examSet.setCategoryName(rs.getString("category_name"));
                examSets.add(examSet);
            }
        } catch (SQLException ex) {
        }
        return examSets;
    }

    /**
     * Thêm một đề thi mới vào hệ thống.
     * Hàm này tạo một bản ghi ExamSet mới với các thông tin cơ bản.
     * Sau khi tạo đề thi, cần gọi addExamQuestions() để thêm câu hỏi vào đề thi.
     * 
     * @param examName        Tên đề thi
     * @param totalQuestions  Tổng số câu hỏi trong đề thi
     * @param durationMinutes Thời gian làm bài (tính bằng phút)
     * @param passingScore    Điểm đỗ tối thiểu
     * @return true nếu thêm thành công, false nếu có lỗi
     */
    @Override
    public boolean addExamSet(String examName, int totalQuestions, int durationMinutes, int passingScore) {
        String sql = "INSERT INTO ExamSets (exam_name, total_questions, duration_minutes, passing_score) VALUES (?, ?, ?, ?)";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return false;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, examName);
            ps.setInt(2, totalQuestions);
            ps.setInt(3, durationMinutes);
            ps.setInt(4, passingScore);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException ex) {
        }
        return false;
    }

    /**
     * Xóa một đề thi và tất cả bài thi liên quan.
     * Hàm này sử dụng transaction để đảm bảo tính toàn vẹn dữ liệu:
     * 1. Xóa tất cả bài thi (UserExams) của đề thi này
     * 2. Xóa đề thi (ExamSets)
     * Nếu có lỗi ở bất kỳ bước nào, sẽ rollback toàn bộ.
     * 
     * @param examSetId ID của đề thi cần xóa
     * @return true nếu xóa thành công, false nếu có lỗi
     */
    @Override
    public boolean deleteExamSet(int examSetId) {
        String deleteUserExams = "DELETE FROM UserExams WHERE exam_set_id = ?";
        String deleteExamSet = "DELETE FROM ExamSets WHERE exam_set_id = ?";
        Connection conn = null;
        try {
            conn = getConnection();
            if (conn == null) {
                return false;
            }
            conn.setAutoCommit(false);

            PreparedStatement psUserExams = conn.prepareStatement(deleteUserExams);
            psUserExams.setInt(1, examSetId);
            psUserExams.executeUpdate();

            PreparedStatement psExamSet = conn.prepareStatement(deleteExamSet);
            psExamSet.setInt(1, examSetId);
            int affected = psExamSet.executeUpdate();

            conn.commit();
            return affected > 0;
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                }
            }
        }
        return false;
    }

    /**
     * Lấy thông tin chi tiết của một đề thi theo ID, kèm thông tin danh mục.
     * Hàm này JOIN với ExamQuestions, Questions và QuestionCategories để lấy
     * category_id và category_name.
     * 
     * @param examSetId ID của đề thi cần lấy thông tin
     * @return Đối tượng ExamSet có kèm categoryId và categoryName, null nếu không
     *         tìm thấy hoặc có lỗi
     */
    @Override
    public ExamSet getExamSetById(int examSetId) {
        String sql = """
                SELECT es.exam_set_id, es.exam_name, es.total_questions, es.duration_minutes, es.passing_score,
                       MIN(q.category_id) AS category_id, MIN(qc.category_name) AS category_name
                FROM ExamSets es
                LEFT JOIN ExamQuestions eq ON es.exam_set_id = eq.exam_set_id
                LEFT JOIN Questions q ON eq.question_id = q.question_id
                LEFT JOIN QuestionCategories qc ON q.category_id = qc.category_id
                WHERE es.exam_set_id = ?
                GROUP BY es.exam_set_id, es.exam_name, es.total_questions, es.duration_minutes, es.passing_score
                """;
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return null;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, examSetId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ExamSet examSet = new ExamSet();
                examSet.setExamSetId(rs.getInt("exam_set_id"));
                examSet.setExamName(rs.getString("exam_name"));
                examSet.setTotalQuestions(rs.getInt("total_questions"));
                examSet.setDurationMinutes(rs.getInt("duration_minutes"));
                examSet.setPassingScore(rs.getInt("passing_score"));
                int catId = rs.getInt("category_id");
                if (!rs.wasNull()) {
                    examSet.setCategoryId(catId);
                }
                examSet.setCategoryName(rs.getString("category_name"));
                return examSet;
            }
        } catch (SQLException ex) {
        }
        return null;
    }

    /**
     * Lấy danh sách tất cả câu hỏi trong một đề thi, sắp xếp theo thứ tự.
     * Hàm này JOIN ExamQuestions với Questions và QuestionCategories để lấy đầy đủ
     * thông tin câu hỏi.
     * Sắp xếp theo question_order và question_id để đảm bảo thứ tự hiển thị đúng.
     * 
     * @param examSetId ID của đề thi cần lấy danh sách câu hỏi
     * @return Danh sách các đối tượng Question trong đề thi, danh sách rỗng nếu
     *         không có câu hỏi
     */
    @Override
    public List<Question> getQuestionsByExamSet(int examSetId) {
        List<Question> questions = new ArrayList<>();
        String sql = """
                SELECT q.question_id, q.category_id, q.question_text, q.question_image, q.explanation, q.is_critical, eq.question_order,
                       qc.category_name
                FROM ExamQuestions eq
                JOIN Questions q ON eq.question_id = q.question_id
                LEFT JOIN QuestionCategories qc ON q.category_id = qc.category_id
                WHERE eq.exam_set_id = ?
                ORDER BY eq.question_order, q.question_id
                """;
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return questions;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, examSetId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Question question = new Question();
                question.setQuestionId(rs.getInt("question_id"));
                question.setCategoryId(rs.getInt("category_id"));
                question.setQuestionText(rs.getString("question_text"));
                question.setQuestionImage(rs.getString("question_image"));
                question.setExplanation(rs.getString("explanation"));
                question.setCritical(rs.getBoolean("is_critical"));
                questions.add(question);
            }
        } catch (SQLException ex) {
        }
        return questions;
    }

    /**
     * Lấy ID của đề thi vừa được tạo gần nhất.
     * Hàm này lấy exam_set_id lớn nhất trong bảng ExamSets (giả định ID tự tăng).
     * Dùng để lấy ID của đề thi vừa tạo để thêm câu hỏi vào đề thi đó.
     * 
     * @return ID của đề thi mới nhất, trả về 0 nếu không có đề thi nào hoặc có lỗi
     */
    @Override
    public int getLastInsertedExamSetId() {
        String sql = "SELECT MAX(exam_set_id) AS last_id FROM ExamSets";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return 0;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("last_id");
            }
        } catch (SQLException ex) {
        }
        return 0;
    }

    /**
     * Thêm danh sách câu hỏi vào một đề thi.
     * Hàm này sử dụng batch insert để thêm nhiều câu hỏi cùng lúc vào bảng
     * ExamQuestions.
     * Mỗi câu hỏi được gán một question_order tăng dần từ 1.
     * 
     * @param examSetId ID của đề thi cần thêm câu hỏi
     * @param questions Danh sách các đối tượng Question cần thêm vào đề thi
     * @return true nếu thêm thành công, false nếu danh sách rỗng hoặc có lỗi
     */
    @Override
    public boolean addExamQuestions(int examSetId, List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            return false;
        }
        String sql = "INSERT INTO ExamQuestions (exam_set_id, question_id, question_order) VALUES (?, ?, ?)";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return false;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            int order = 1;
            for (Question q : questions) {
                ps.setInt(1, examSetId);
                ps.setInt(2, q.getQuestionId());
                ps.setInt(3, order++);
                ps.addBatch();
            }
            ps.executeBatch();
            return true;
        } catch (SQLException ex) {
        }
        return false;
    }

    /**
     * Lấy danh sách đề thi theo danh mục với phân trang.
     * Hàm này tìm các đề thi có chứa ít nhất một câu hỏi thuộc danh mục được chỉ
     * định.
     * Sử dụng EXISTS để kiểm tra sự tồn tại của câu hỏi thuộc danh mục trong đề
     * thi.
     * 
     * @param categoryId ID của danh mục câu hỏi cần lọc
     * @param offset     Số lượng bản ghi cần bỏ qua (dùng cho phân trang)
     * @param limit      Số lượng bản ghi tối đa cần lấy
     * @return Danh sách các đối tượng ExamSet thuộc danh mục, danh sách rỗng nếu
     *         không có
     */
    @Override
    public List<ExamSet> getExamSetsByCategory(int categoryId, int offset, int limit) {
        List<ExamSet> examSets = new ArrayList<>();
        String sql = """
                SELECT DISTINCT es.exam_set_id, es.exam_name, es.total_questions, es.duration_minutes, es.passing_score
                FROM ExamSets es
                WHERE EXISTS (
                    SELECT 1 FROM ExamQuestions eq
                    JOIN Questions q ON eq.question_id = q.question_id
                    WHERE eq.exam_set_id = es.exam_set_id AND q.category_id = ?
                )
                ORDER BY es.exam_set_id
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """;
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return examSets;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryId);
            ps.setInt(2, offset);
            ps.setInt(3, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ExamSet examSet = new ExamSet();
                examSet.setExamSetId(rs.getInt("exam_set_id"));
                examSet.setExamName(rs.getString("exam_name"));
                examSet.setTotalQuestions(rs.getInt("total_questions"));
                examSet.setDurationMinutes(rs.getInt("duration_minutes"));
                examSet.setPassingScore(rs.getInt("passing_score"));
                examSets.add(examSet);
            }
        } catch (SQLException ex) {
        }
        return examSets;
    }

    /**
     * Lấy tổng số lượng đề thi theo danh mục.
     * Hàm này đếm số đề thi có chứa ít nhất một câu hỏi thuộc danh mục được chỉ
     * định.
     * Dùng để tính tổng số trang khi phân trang đề thi theo danh mục.
     * 
     * @param categoryId ID của danh mục câu hỏi cần đếm
     * @return Tổng số lượng đề thi thuộc danh mục, trả về 0 nếu không có hoặc có
     *         lỗi
     */
    @Override
    public int getTotalExamSetsByCategory(int categoryId) {
        String sql = """
                SELECT COUNT(DISTINCT es.exam_set_id) AS total
                FROM ExamSets es
                WHERE EXISTS (
                    SELECT 1 FROM ExamQuestions eq
                    JOIN Questions q ON eq.question_id = q.question_id
                    WHERE eq.exam_set_id = es.exam_set_id AND q.category_id = ?
                )
                """;
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return 0;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException ex) {
        }
        return 0;
    }
}
