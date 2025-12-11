package dao;

import dbcontext.DBContext;
import interfa.IExamSetDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ExamSetDAO extends DBContext implements IExamSetDAO {

    public int getTotalExamSets() {
        String sql = "SELECT COUNT(*) AS total FROM ExamSets";
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
            System.err.println("Error getting total exam sets: " + ex.getMessage());
            ex.printStackTrace();
        }
        return 0;
    }
}

