package dao;

import dbcontext.DBContext;
import interfa.IQuestionDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Question;

public class QuestionDAO extends DBContext implements IQuestionDAO {

    /**
     * Lấy tổng số lượng câu hỏi trong hệ thống (không có bộ lọc).
     * Hàm này gọi getTotalQuestionsFiltered() với tất cả tham số null.
     * 
     * @return Tổng số lượng câu hỏi, trả về 0 nếu không có dữ liệu hoặc có lỗi
     */
    @Override
    public int getTotalQuestions() {
        return getTotalQuestionsFiltered(null, null, null);
    }

    /**
     * Lấy danh sách tất cả câu hỏi với phân trang (không có bộ lọc).
     * Hàm này gọi getQuestionsFiltered() với tất cả tham số bộ lọc là null.
     * 
     * @param offset Số lượng bản ghi cần bỏ qua (dùng cho phân trang)
     * @param limit  Số lượng bản ghi tối đa cần lấy
     * @return Danh sách các đối tượng Question, danh sách rỗng nếu không có dữ liệu
     */
    @Override
    public List<Question> getAllQuestions(int offset, int limit) {
        return getQuestionsFiltered(null, null, null, offset, limit);
    }

    /**
     * Tìm kiếm câu hỏi theo từ khóa với phân trang.
     * Hàm này tìm các câu hỏi có question_text chứa từ khóa (không phân biệt hoa
     * thường).
     * 
     * @param keyword Từ khóa tìm kiếm (có thể là một phần của nội dung câu hỏi)
     * @param offset  Số lượng bản ghi cần bỏ qua (dùng cho phân trang)
     * @param limit   Số lượng bản ghi tối đa cần lấy
     * @return Danh sách các đối tượng Question khớp với từ khóa, danh sách rỗng nếu
     *         không tìm thấy
     */
    @Override
    public List<Question> searchQuestions(String keyword, int offset, int limit) {
        return getQuestionsFiltered(keyword, null, null, offset, limit);
    }

    /**
     * Lấy tổng số lượng câu hỏi khớp với từ khóa tìm kiếm.
     * Hàm này đếm số câu hỏi có question_text chứa từ khóa, dùng để tính tổng số
     * trang khi phân trang.
     * 
     * @param keyword Từ khóa tìm kiếm
     * @return Tổng số lượng câu hỏi khớp với từ khóa, trả về 0 nếu không tìm thấy
     *         hoặc có lỗi
     */
    @Override
    public int getTotalQuestionsBySearch(String keyword) {
        return getTotalQuestionsFiltered(keyword, null, null);
    }

    /**
     * Lấy thông tin chi tiết của một câu hỏi theo ID.
     * Hàm này trả về đầy đủ thông tin câu hỏi bao gồm: question_id, category_id,
     * question_text,
     * question_image, explanation, is_critical.
     * 
     * @param questionId ID của câu hỏi cần lấy thông tin
     * @return Đối tượng Question nếu tìm thấy, null nếu không tìm thấy hoặc có lỗi
     */
    @Override
    public Question getQuestionById(int questionId) {
        String sql = "SELECT * FROM Questions WHERE question_id = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return null;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, questionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Question question = new Question();
                question.setQuestionId(rs.getInt("question_id"));
                question.setCategoryId(rs.getInt("category_id"));
                question.setQuestionText(rs.getString("question_text"));
                question.setQuestionImage(rs.getString("question_image"));
                question.setExplanation(rs.getString("explanation"));
                question.setCritical(rs.getBoolean("is_critical"));
                return question;
            }
        } catch (SQLException ex) {
        }
        return null;
    }

    /**
     * Thêm một câu hỏi mới vào hệ thống.
     * Hàm này tạo một bản ghi Question mới. Sau khi tạo câu hỏi, cần gọi AnswerDAO
     * để thêm các đáp án.
     * 
     * @param categoryId    ID của danh mục câu hỏi
     * @param questionText  Nội dung câu hỏi
     * @param questionImage Đường dẫn đến hình ảnh của câu hỏi (có thể null nếu
     *                      không có hình)
     * @param explanation   Giải thích đáp án đúng (có thể null)
     * @param isCritical    true nếu là câu hỏi điểm liệt, false nếu là câu hỏi
     *                      thường
     * @return true nếu thêm thành công, false nếu có lỗi
     */
    @Override
    public boolean addQuestion(int categoryId, String questionText, String questionImage, String explanation,
            boolean isCritical) {
        String sql = "INSERT INTO Questions (category_id, question_text, question_image, explanation, is_critical) VALUES (?, ?, ?, ?, ?)";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return false;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryId);
            ps.setString(2, questionText);
            ps.setString(3, questionImage);
            ps.setString(4, explanation);
            ps.setBoolean(5, isCritical);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException ex) {
        }
        return false;
    }

    /**
     * Cập nhật thông tin của một câu hỏi.
     * Hàm này cập nhật tất cả các trường của câu hỏi bao gồm: category_id,
     * question_text,
     * question_image, explanation, is_critical.
     * 
     * @param questionId    ID của câu hỏi cần cập nhật
     * @param categoryId    ID danh mục mới
     * @param questionText  Nội dung câu hỏi mới
     * @param questionImage Đường dẫn hình ảnh mới (có thể null)
     * @param explanation   Giải thích mới (có thể null)
     * @param isCritical    true nếu là câu hỏi điểm liệt, false nếu là câu hỏi
     *                      thường
     * @return true nếu cập nhật thành công, false nếu có lỗi
     */
    @Override
    public boolean updateQuestion(int questionId, int categoryId, String questionText, String questionImage,
            String explanation, boolean isCritical) {
        String sql = "UPDATE Questions SET category_id = ?, question_text = ?, question_image = ?, explanation = ?, is_critical = ? WHERE question_id = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return false;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryId);
            ps.setString(2, questionText);
            ps.setString(3, questionImage);
            ps.setString(4, explanation);
            ps.setBoolean(5, isCritical);
            ps.setInt(6, questionId);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException ex) {
        }
        return false;
    }

    /**
     * Xóa một câu hỏi khỏi hệ thống.
     * Lưu ý: Cần xóa tất cả đáp án của câu hỏi trước khi xóa câu hỏi (sử dụng
     * AnswerDAO.deleteAnswersByQuestionId()).
     * 
     * @param questionId ID của câu hỏi cần xóa
     * @return true nếu xóa thành công, false nếu có lỗi
     */
    @Override
    public boolean deleteQuestion(int questionId) {
        String sql = "DELETE FROM Questions WHERE question_id = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return false;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, questionId);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException ex) {
        }
        return false;
    }

    /**
     * Lấy danh sách câu hỏi với các bộ lọc tùy chọn và phân trang.
     * Hàm này hỗ trợ lọc theo:
     * - keyword: Tìm kiếm trong question_text
     * - type: "critical" (câu điểm liệt) hoặc "normal" (câu thường)
     * - categoryId: Lọc theo danh mục
     * Các tham số có thể null để bỏ qua bộ lọc tương ứng.
     * 
     * @param keyword    Từ khóa tìm kiếm (null nếu không tìm kiếm)
     * @param type       Loại câu hỏi: "critical" hoặc "normal" (null nếu không lọc)
     * @param categoryId ID danh mục (null nếu không lọc theo danh mục)
     * @param offset     Số lượng bản ghi cần bỏ qua (dùng cho phân trang)
     * @param limit      Số lượng bản ghi tối đa cần lấy
     * @return Danh sách các đối tượng Question thỏa mãn điều kiện lọc, danh sách
     *         rỗng nếu không có
     */
    @Override
    public List<Question> getQuestionsFiltered(String keyword, String type, Integer categoryId, int offset, int limit) {
        List<Question> questions = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Questions WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND question_text LIKE ?");
            params.add("%" + keyword.trim() + "%");
        }

        if (type != null && !type.trim().isEmpty()) {
            String normalizedType = type.trim().toLowerCase();
            if ("critical".equals(normalizedType)) {
                sql.append(" AND is_critical = 1");
            } else if ("normal".equals(normalizedType)) {
                sql.append(" AND is_critical = 0");
            }
        }

        if (categoryId != null && categoryId > 0) {
            sql.append(" AND category_id = ?");
            params.add(categoryId);
        }

        sql.append(" ORDER BY question_id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(offset);
        params.add(limit);

        try {
            Connection conn = getConnection();
            if (conn == null) {
                return questions;
            }
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
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
     * Lấy tổng số lượng câu hỏi thỏa mãn các điều kiện lọc.
     * Hàm này đếm số câu hỏi khớp với các bộ lọc, dùng để tính tổng số trang khi
     * phân trang.
     * Các tham số bộ lọc giống với getQuestionsFiltered().
     * 
     * @param keyword    Từ khóa tìm kiếm (null nếu không tìm kiếm)
     * @param type       Loại câu hỏi: "critical" hoặc "normal" (null nếu không lọc)
     * @param categoryId ID danh mục (null nếu không lọc theo danh mục)
     * @return Tổng số lượng câu hỏi thỏa mãn điều kiện, trả về 0 nếu không có hoặc
     *         có lỗi
     */
    @Override
    public int getTotalQuestionsFiltered(String keyword, String type, Integer categoryId) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) AS total FROM Questions WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND question_text LIKE ?");
            params.add("%" + keyword.trim() + "%");
        }

        if (type != null && !type.trim().isEmpty()) {
            String normalizedType = type.trim().toLowerCase();
            if ("critical".equals(normalizedType)) {
                sql.append(" AND is_critical = 1");
            } else if ("normal".equals(normalizedType)) {
                sql.append(" AND is_critical = 0");
            }
        }

        if (categoryId != null && categoryId > 0) {
            sql.append(" AND category_id = ?");
            params.add(categoryId);
        }

        try {
            Connection conn = getConnection();
            if (conn == null) {
                return 0;
            }
            PreparedStatement ps = conn.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException ex) {
        }

        return 0;
    }
}
