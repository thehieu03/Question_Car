package dao;

import dbcontext.DBContext;
import interfa.IUserExamDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.UserExam;

public class UserExamDAO extends DBContext implements IUserExamDAO {
    
    private static final Logger logger = Logger.getLogger(UserExamDAO.class.getName());

    @Override
    public int getTotalUserExams() {
        String sql = "SELECT COUNT(*) AS total FROM UserExams";
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
            logger.log(Level.SEVERE, "Error getting total user exams", ex);
        }
        return 0;
    }

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
            logger.log(Level.SEVERE, "Error getting user exams", ex);
        }
        return list;
    }

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
            logger.log(Level.SEVERE, "Error getting user exams with info", ex);
        }
        return list;
    }

    @Override
    public int getTotalUserExamsByUser(int userId) {
        String sql = "SELECT COUNT(*) AS total FROM UserExams WHERE user_id = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) return 0;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error counting user exams", ex);
        }
        return 0;
    }

    @Override
    public int getPassedUserExamsByUser(int userId) {
        String sql = "SELECT COUNT(*) AS total FROM UserExams WHERE user_id = ? AND is_passed = 1";
        try {
            Connection conn = getConnection();
            if (conn == null) return 0;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("total");
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error counting passed user exams", ex);
        }
        return 0;
    }

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
            if (conn == null) return null;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (Integer) rs.getObject("total_score");
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error getting last score by user", ex);
        }
        return null;
    }

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

