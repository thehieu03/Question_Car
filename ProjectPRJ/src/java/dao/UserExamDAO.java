package dao;

import dbcontext.DBContext;
import interfa.IUserExamDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserExamDAO extends DBContext implements IUserExamDAO {

    @Override
    public int getTotalUserExams() {
        String sql = "SELECT COUNT(*) AS total FROM UserExams";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                System.err.println("ERROR: Database connection is null!");
                return 0;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException ex) {
            System.err.println("Error getting total user exams: " + ex.getMessage());
            ex.printStackTrace();
        }
        return 0;
    }
}

