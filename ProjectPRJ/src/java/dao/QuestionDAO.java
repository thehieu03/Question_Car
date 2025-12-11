package dao;

import dbcontext.DBContext;
import interfa.IQuestionDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QuestionDAO extends DBContext implements IQuestionDAO {

    public int getTotalQuestions() {
        String sql = "SELECT COUNT(*) AS total FROM Questions";
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
            System.err.println("Error getting total questions: " + ex.getMessage());
            ex.printStackTrace();
        }
        return 0;
    }
}

