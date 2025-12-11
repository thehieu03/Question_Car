package dao;

import dbcontext.DBContext;
import interfa.IExamSetDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.ExamSet;
import model.Question;

public class ExamSetDAO extends DBContext implements IExamSetDAO {
    
    private static final Logger logger = Logger.getLogger(ExamSetDAO.class.getName());

    public int getTotalExamSets() {
        String sql = "SELECT COUNT(*) AS total FROM ExamSets";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                logger.severe("ERROR: Database connection is null!");
                return 0;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error getting total exam sets", ex);
        }
        return 0;
    }

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
            logger.log(Level.SEVERE, "Error getting exam sets", ex);
        }
        return examSets;
    }

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
            logger.log(Level.SEVERE, "Error adding exam set", ex);
        }
        return false;
    }

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
            logger.log(Level.SEVERE, "Error deleting exam set id=" + examSetId, ex);
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Error rolling back delete exam set", e);
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    logger.log(Level.WARNING, "Failed to reset autocommit", e);
                }
            }
        }
        return false;
    }

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
            logger.log(Level.SEVERE, "Error getting exam set by id", ex);
        }
        return null;
    }

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
            logger.log(Level.SEVERE, "Error getting questions by exam set", ex);
        }
        return questions;
    }

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
            logger.log(Level.SEVERE, "Error getting last inserted exam set id", ex);
        }
        return 0;
    }

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
            logger.log(Level.SEVERE, "Error adding questions to exam set", ex);
        }
        return false;
    }

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
            logger.log(Level.SEVERE, "Error getting exam sets by category", ex);
        }
        return examSets;
    }

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
            logger.log(Level.SEVERE, "Error getting total exam sets by category", ex);
        }
        return 0;
    }
}

