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

    @Override
    public int getTotalQuestions() {
        return getTotalQuestionsFiltered(null, null, null);
    }

    @Override
    public List<Question> getAllQuestions(int offset, int limit) {
        return getQuestionsFiltered(null, null, null, offset, limit);
    }

    @Override
    public List<Question> searchQuestions(String keyword, int offset, int limit) {
        return getQuestionsFiltered(keyword, null, null, offset, limit);
    }

    @Override
    public int getTotalQuestionsBySearch(String keyword) {
        return getTotalQuestionsFiltered(keyword, null, null);
    }

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
