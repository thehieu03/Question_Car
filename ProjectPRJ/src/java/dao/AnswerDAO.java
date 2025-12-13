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
