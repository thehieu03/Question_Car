package dao;

import dbcontext.DBContext;
import interfa.IUserExamDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

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
}

