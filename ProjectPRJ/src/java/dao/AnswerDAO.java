package dao;

import dbcontext.DBContext;
import interfa.IAnswerDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Answer;

public class AnswerDAO extends DBContext implements IAnswerDAO {

    /**
     * Lấy danh sách tất cả đáp án của một câu hỏi, sắp xếp theo thứ tự.
     * Hàm này trả về tất cả đáp án của câu hỏi, sắp xếp theo answer_order để hiển
     * thị đúng thứ tự.
     * 
     * @param questionId ID của câu hỏi cần lấy đáp án
     * @return Danh sách các đối tượng Answer của câu hỏi, danh sách rỗng nếu không
     *         có đáp án
     */
    @Override
    public List<Answer> getAnswersByQuestionId(int questionId) {
        List<Answer> answers = new ArrayList<>();
        String sql = "SELECT * FROM Answers WHERE question_id = ? ORDER BY answer_order";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return answers;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, questionId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Answer answer = new Answer();
                answer.setAnswerId(rs.getInt("answer_id"));
                answer.setQuestionId(rs.getInt("question_id"));
                answer.setAnswerText(rs.getString("answer_text"));
                answer.setCorrect(rs.getBoolean("is_correct"));
                answer.setAnswerOrder(rs.getInt("answer_order"));
                answers.add(answer);
            }
        } catch (SQLException ex) {
        }
        return answers;
    }

    /**
     * Thêm một đáp án mới cho câu hỏi.
     * Hàm này tạo một bản ghi Answer mới. Mỗi câu hỏi thường có 4 đáp án
     * (answer_order từ 1 đến 4).
     * Chỉ có một đáp án có is_correct = true (đáp án đúng).
     * 
     * @param questionId  ID của câu hỏi cần thêm đáp án
     * @param answerText  Nội dung đáp án
     * @param isCorrect   true nếu là đáp án đúng, false nếu là đáp án sai
     * @param answerOrder Thứ tự hiển thị của đáp án (1, 2, 3, 4)
     * @return true nếu thêm thành công, false nếu có lỗi
     */
    @Override
    public boolean addAnswer(int questionId, String answerText, boolean isCorrect, int answerOrder) {
        String sql = "INSERT INTO Answers (question_id, answer_text, is_correct, answer_order) VALUES (?, ?, ?, ?)";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return false;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, questionId);
            ps.setString(2, answerText);
            ps.setBoolean(3, isCorrect);
            ps.setInt(4, answerOrder);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException ex) {
        }
        return false;
    }

    /**
     * Cập nhật thông tin của một đáp án.
     * Hàm này cập nhật tất cả các trường của đáp án bao gồm: answer_text,
     * is_correct, answer_order.
     * 
     * @param answerId    ID của đáp án cần cập nhật
     * @param answerText  Nội dung đáp án mới
     * @param isCorrect   true nếu là đáp án đúng, false nếu là đáp án sai
     * @param answerOrder Thứ tự hiển thị mới
     * @return true nếu cập nhật thành công, false nếu có lỗi
     */
    @Override
    public boolean updateAnswer(int answerId, String answerText, boolean isCorrect, int answerOrder) {
        String sql = "UPDATE Answers SET answer_text = ?, is_correct = ?, answer_order = ? WHERE answer_id = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return false;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, answerText);
            ps.setBoolean(2, isCorrect);
            ps.setInt(3, answerOrder);
            ps.setInt(4, answerId);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException ex) {
        }
        return false;
    }

    /**
     * Xóa một đáp án khỏi hệ thống.
     * Hàm này xóa một đáp án cụ thể theo ID.
     * 
     * @param answerId ID của đáp án cần xóa
     * @return true nếu xóa thành công, false nếu có lỗi
     */
    @Override
    public boolean deleteAnswer(int answerId) {
        String sql = "DELETE FROM Answers WHERE answer_id = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return false;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, answerId);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException ex) {
        }
        return false;
    }

    /**
     * Xóa tất cả đáp án của một câu hỏi.
     * Hàm này xóa tất cả đáp án thuộc câu hỏi được chỉ định.
     * Thường được gọi trước khi xóa câu hỏi để đảm bảo tính toàn vẹn dữ liệu.
     * 
     * @param questionId ID của câu hỏi cần xóa tất cả đáp án
     * @return true nếu xóa thành công, false nếu có lỗi
     */
    @Override
    public boolean deleteAnswersByQuestionId(int questionId) {
        String sql = "DELETE FROM Answers WHERE question_id = ?";
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
}
