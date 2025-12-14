package dao;

import dbcontext.DBContext;
import interfa.IUserExamDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.UserExam;

public class UserExamDAO extends DBContext implements IUserExamDAO {

    /**
     * Lấy tổng số lượng bài thi của tất cả người dùng trong hệ thống.
     * Hàm này đếm tất cả các bản ghi trong bảng UserExams, bao gồm cả bài thi đã
     * hoàn thành và đang làm.
     * 
     * @return Tổng số lượng bài thi, trả về 0 nếu không có dữ liệu hoặc có lỗi
     */
    @Override
    public int getTotalUserExams() {
        String sql = "SELECT COUNT(*) AS total FROM UserExams";
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
     * Lấy danh sách tất cả bài thi với phân trang, sắp xếp theo user_exam_id giảm
     * dần (mới nhất trước).
     * Hàm này trả về danh sách bài thi của tất cả người dùng, dùng cho trang quản
     * trị xem kết quả thi.
     * 
     * @param offset Số lượng bản ghi cần bỏ qua (dùng cho phân trang)
     * @param limit  Số lượng bản ghi tối đa cần lấy
     * @return Danh sách các đối tượng UserExam, danh sách rỗng nếu không có dữ liệu
     */
    @Override
    public List<UserExam> getUserExams(int offset, int limit) {
        List<UserExam> list = new ArrayList<>();
        String sql = "SELECT * FROM UserExams ORDER BY user_exam_id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return list;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserExam ue = mapUserExam(rs);
                list.add(ue);
            }
        } catch (SQLException ex) {
        }
        return list;
    }

    /**
     * Lấy danh sách bài thi kèm thông tin người dùng và tên đề thi với phân trang.
     * Hàm này JOIN với bảng Users và ExamSets để lấy thêm username, email và
     * exam_name.
     * Sắp xếp theo user_exam_id giảm dần (bài thi mới nhất trước).
     * 
     * @param offset Số lượng bản ghi cần bỏ qua (dùng cho phân trang)
     * @param limit  Số lượng bản ghi tối đa cần lấy
     * @return Danh sách các đối tượng UserExam có kèm username, email và examName,
     *         danh sách rỗng nếu không có dữ liệu
     */
    @Override
    public List<UserExam> getUserExamsWithInfo(int offset, int limit) {
        List<UserExam> list = new ArrayList<>();
        String sql = """
                SELECT ue.*, u.username, u.email, es.exam_name
                FROM UserExams ue
                JOIN Users u ON ue.user_id = u.user_id
                JOIN ExamSets es ON ue.exam_set_id = es.exam_set_id
                ORDER BY ue.user_exam_id DESC
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """;
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return list;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserExam ue = mapUserExam(rs);
                ue.setUsername(rs.getString("username"));
                ue.setEmail(rs.getString("email"));
                ue.setExamName(rs.getString("exam_name"));
                list.add(ue);
            }
        } catch (SQLException ex) {
        }
        return list;
    }

    /**
     * Lấy tổng số lượng bài thi của một người dùng cụ thể.
     * Hàm này đếm tất cả bài thi (bao gồm cả đã hoàn thành và đang làm) của user.
     * 
     * @param userId ID của người dùng cần đếm bài thi
     * @return Tổng số lượng bài thi của user, trả về 0 nếu không có hoặc có lỗi
     */
    @Override
    public int getTotalUserExamsByUser(int userId) {
        String sql = "SELECT COUNT(*) AS total FROM UserExams WHERE user_id = ?";
        try {
            Connection conn = getConnection();
            if (conn == null)
                return 0;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt("total");
        } catch (SQLException ex) {
        }
        return 0;
    }

    /**
     * Lấy tổng số lượng bài thi đã đỗ của một người dùng.
     * Hàm này đếm các bài thi có is_passed = 1 (đã đạt điểm đỗ) của user.
     * 
     * @param userId ID của người dùng cần đếm số bài đỗ
     * @return Tổng số lượng bài thi đã đỗ, trả về 0 nếu không có hoặc có lỗi
     */
    @Override
    public int getPassedUserExamsByUser(int userId) {
        String sql = "SELECT COUNT(*) AS total FROM UserExams WHERE user_id = ? AND is_passed = 1";
        try {
            Connection conn = getConnection();
            if (conn == null)
                return 0;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt("total");
        } catch (SQLException ex) {
        }
        return 0;
    }

    /**
     * Lấy điểm số của bài thi gần nhất của một người dùng.
     * Hàm này lấy total_score của bài thi có user_exam_id lớn nhất (bài thi mới
     * nhất).
     * 
     * @param userId ID của người dùng cần lấy điểm
     * @return Điểm số (Integer) của bài thi gần nhất, null nếu user chưa làm bài
     *         thi nào
     */
    @Override
    public Integer getLastScoreByUser(int userId) {
        String sql = """
                SELECT TOP 1 total_score
                FROM UserExams
                WHERE user_id = ?
                ORDER BY user_exam_id DESC
                """;
        try {
            Connection conn = getConnection();
            if (conn == null)
                return null;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (Integer) rs.getObject("total_score");
            }
        } catch (SQLException ex) {
        }
        return null;
    }

    /**
     * Bắt đầu một bài thi mới cho người dùng.
     * Hàm này tạo một bản ghi UserExam mới với status = 'IN_PROGRESS' và start_time
     * = thời gian hiện tại.
     * Sử dụng OUTPUT INSERTED để lấy user_exam_id vừa được tạo.
     * 
     * @param userId    ID của người dùng bắt đầu làm bài
     * @param examSetId ID của đề thi cần làm
     * @return ID của bài thi vừa tạo (user_exam_id), trả về 0 nếu có lỗi
     */
    @Override
    public int startExam(int userId, int examSetId) {
        String sql = "INSERT INTO UserExams (user_id, exam_set_id, start_time, status) OUTPUT INSERTED.user_exam_id VALUES (?, ?, GETDATE(), 'IN_PROGRESS')";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return 0;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, examSetId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userExamId = rs.getInt("user_exam_id");
                return userExamId;
            }
        } catch (SQLException ex) {
        }
        return 0;
    }

    /**
     * Lưu hoặc cập nhật câu trả lời của người dùng cho một câu hỏi trong bài thi.
     * Hàm này kiểm tra xem đã có câu trả lời cho câu hỏi này chưa:
     * - Nếu chưa có: INSERT câu trả lời mới
     * - Nếu đã có: UPDATE câu trả lời hiện tại
     * 
     * @param userExamId ID của bài thi
     * @param questionId ID của câu hỏi
     * @param answerId   ID của đáp án người dùng chọn (có thể null nếu chưa chọn)
     * @param isCorrect  true nếu đáp án đúng, false nếu sai, null nếu chưa chọn
     * @return true nếu lưu thành công, false nếu có lỗi
     */
    @Override
    public boolean saveUserAnswer(int userExamId, int questionId, Integer answerId, Boolean isCorrect) {
        // Kiểm tra xem đã có câu trả lời chưa
        String checkSql = "SELECT COUNT(*) AS cnt FROM UserAnswers WHERE user_exam_id = ? AND question_id = ?";
        String updateSql = "UPDATE UserAnswers SET answer_id = ?, is_correct = ? WHERE user_exam_id = ? AND question_id = ?";
        String insertSql = "INSERT INTO UserAnswers (user_exam_id, question_id, answer_id, is_correct) VALUES (?, ?, ?, ?)";

        try {
            Connection conn = getConnection();
            if (conn == null) {
                return false;
            }

            // Kiểm tra xem đã có record chưa
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, userExamId);
            checkPs.setInt(2, questionId);
            ResultSet rs = checkPs.executeQuery();
            boolean exists = false;
            if (rs.next()) {
                exists = rs.getInt("cnt") > 0;
            }
            rs.close();
            checkPs.close();

            PreparedStatement ps;
            if (exists) {
                // Update
                ps = conn.prepareStatement(updateSql);
                ps.setObject(1, answerId);
                ps.setObject(2, isCorrect);
                ps.setInt(3, userExamId);
                ps.setInt(4, questionId);
            } else {
                // Insert
                ps = conn.prepareStatement(insertSql);
                ps.setInt(1, userExamId);
                ps.setInt(2, questionId);
                ps.setObject(3, answerId);
                ps.setObject(4, isCorrect);
            }

            int result = ps.executeUpdate();
            ps.close();
            return result > 0;
        } catch (SQLException ex) {
        }
        return false;
    }

    /**
     * Nộp bài thi và cập nhật kết quả cuối cùng.
     * Hàm này cập nhật bài thi với:
     * - end_time: Thời gian kết thúc (thời gian hiện tại)
     * - total_score: Tổng điểm
     * - correct_answers: Số câu đúng
     * - wrong_answers: Số câu sai
     * - is_passed: Đã đỗ hay chưa
     * - status: Chuyển từ 'IN_PROGRESS' sang 'COMPLETED'
     * 
     * @param userExamId     ID của bài thi cần nộp
     * @param totalScore     Tổng điểm đạt được
     * @param correctAnswers Số câu trả lời đúng
     * @param wrongAnswers   Số câu trả lời sai
     * @param isPassed       true nếu đạt điểm đỗ, false nếu không đạt
     * @return true nếu nộp bài thành công, false nếu có lỗi
     */
    @Override
    public boolean submitExam(int userExamId, int totalScore, int correctAnswers, int wrongAnswers, boolean isPassed) {
        String sql = "UPDATE UserExams SET end_time = GETDATE(), total_score = ?, correct_answers = ?, wrong_answers = ?, is_passed = ?, status = 'COMPLETED' WHERE user_exam_id = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return false;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, totalScore);
            ps.setInt(2, correctAnswers);
            ps.setInt(3, wrongAnswers);
            ps.setBoolean(4, isPassed);
            ps.setInt(5, userExamId);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException ex) {
        }
        return false;
    }

    /**
     * Lấy thông tin chi tiết của một bài thi theo ID.
     * Hàm này trả về đầy đủ thông tin bài thi bao gồm: user_id, exam_set_id,
     * start_time, end_time,
     * total_score, correct_answers, wrong_answers, is_passed, status.
     * 
     * @param userExamId ID của bài thi cần lấy thông tin
     * @return Đối tượng UserExam nếu tìm thấy, null nếu không tìm thấy hoặc có lỗi
     */
    @Override
    public UserExam getUserExamById(int userExamId) {
        String sql = "SELECT * FROM UserExams WHERE user_exam_id = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return null;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userExamId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUserExam(rs);
            }
        } catch (SQLException ex) {
        }
        return null;
    }

    /**
     * Lấy bài thi đang làm dở của người dùng cho một đề thi cụ thể.
     * Hàm này tìm bài thi có status = 'IN_PROGRESS' của user cho đề thi này.
     * Nếu có nhiều bài thi đang làm, lấy bài thi mới nhất (user_exam_id lớn nhất).
     * Dùng để cho phép user tiếp tục làm bài thi đã bắt đầu trước đó.
     * 
     * @param userId    ID của người dùng
     * @param examSetId ID của đề thi
     * @return Đối tượng UserExam đang làm dở, null nếu không có bài thi đang làm
     *         hoặc có lỗi
     */
    @Override
    public UserExam getInProgressExam(int userId, int examSetId) {
        String sql = "SELECT TOP 1 * FROM UserExams WHERE user_id = ? AND exam_set_id = ? AND status = 'IN_PROGRESS' ORDER BY user_exam_id DESC";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return null;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, examSetId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUserExam(rs);
            }
        } catch (SQLException ex) {
        }
        return null;
    }

    /**
     * Lấy danh sách bài thi đã hoàn thành của một người dùng với phân trang.
     * Hàm này chỉ lấy các bài thi có status = 'COMPLETED', JOIN với ExamSets để lấy
     * tên đề thi.
     * Sắp xếp theo end_time giảm dần (bài thi mới hoàn thành nhất trước).
     * 
     * @param userId ID của người dùng cần lấy lịch sử bài thi
     * @param offset Số lượng bản ghi cần bỏ qua (dùng cho phân trang)
     * @param limit  Số lượng bản ghi tối đa cần lấy
     * @return Danh sách các đối tượng UserExam đã hoàn thành có kèm examName, danh
     *         sách rỗng nếu không có
     */
    @Override
    public List<UserExam> getUserExamsByUser(int userId, int offset, int limit) {
        List<UserExam> list = new ArrayList<>();
        String sql = """
                SELECT ue.*, es.exam_name
                FROM UserExams ue
                JOIN ExamSets es ON ue.exam_set_id = es.exam_set_id
                WHERE ue.user_id = ? AND ue.status = 'COMPLETED'
                ORDER BY ue.end_time DESC
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """;
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return list;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, offset);
            ps.setInt(3, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserExam ue = mapUserExam(rs);
                ue.setExamName(rs.getString("exam_name"));
                list.add(ue);
            }
        } catch (SQLException ex) {
        }
        return list;
    }

    /**
     * Chuyển đổi dữ liệu từ ResultSet sang đối tượng UserExam.
     * Hàm helper dùng để map các cột trong ResultSet vào các thuộc tính của
     * UserExam.
     * Xử lý các giá trị NULL bằng cách sử dụng getObject() và cast.
     * 
     * @param rs ResultSet chứa dữ liệu từ database
     * @return Đối tượng UserExam đã được điền đầy đủ thông tin
     * @throws SQLException Nếu có lỗi khi đọc dữ liệu từ ResultSet
     */
    private UserExam mapUserExam(ResultSet rs) throws SQLException {
        UserExam ue = new UserExam();
        ue.setUserExamId(rs.getInt("user_exam_id"));
        ue.setUserId(rs.getInt("user_id"));
        ue.setExamSetId(rs.getInt("exam_set_id"));
        ue.setStartTime(rs.getTimestamp("start_time"));
        ue.setEndTime(rs.getTimestamp("end_time"));
        ue.setTotalScore((Integer) rs.getObject("total_score"));
        ue.setCorrectAnswers((Integer) rs.getObject("correct_answers"));
        ue.setWrongAnswers((Integer) rs.getObject("wrong_answers"));
        ue.setIsPassed((Boolean) rs.getObject("is_passed"));
        ue.setStatus(rs.getString("status"));
        return ue;
    }
}
