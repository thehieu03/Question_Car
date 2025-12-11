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
        String sql = "SELECT * FROM ExamSets ORDER BY exam_set_id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
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
}

