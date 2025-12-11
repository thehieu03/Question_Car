package dao;

import dbcontext.DBContext;
import interfa.IQuestionCategoryDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.QuestionCategory;

public class QuestionCategoryDAO extends DBContext implements IQuestionCategoryDAO {
    
    private static final Logger logger = Logger.getLogger(QuestionCategoryDAO.class.getName());

    @Override
    public List<QuestionCategory> getAllCategories() {
        List<QuestionCategory> categories = new ArrayList<>();
        String sql = "SELECT * FROM QuestionCategories ORDER BY category_id";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return categories;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                QuestionCategory category = new QuestionCategory();
                category.setCategoryId(rs.getInt("category_id"));
                category.setCategoryName(rs.getString("category_name"));
                categories.add(category);
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error getting all categories", ex);
        }
        return categories;
    }

    @Override
    public QuestionCategory getCategoryById(int categoryId) {
        String sql = "SELECT * FROM QuestionCategories WHERE category_id = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return null;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                QuestionCategory category = new QuestionCategory();
                category.setCategoryId(rs.getInt("category_id"));
                category.setCategoryName(rs.getString("category_name"));
                return category;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error getting category by id", ex);
        }
        return null;
    }

    @Override
    public boolean addCategory(String categoryName) {
        String sql = "INSERT INTO QuestionCategories (category_name) VALUES (?)";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return false;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, categoryName);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error adding category", ex);
        }
        return false;
    }

    @Override
    public boolean updateCategory(int categoryId, String categoryName) {
        String sql = "UPDATE QuestionCategories SET category_name = ? WHERE category_id = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return false;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, categoryName);
            ps.setInt(2, categoryId);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error updating category", ex);
        }
        return false;
    }

    @Override
    public boolean deleteCategory(int categoryId) {
        String sql = "DELETE FROM QuestionCategories WHERE category_id = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return false;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, categoryId);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error deleting category", ex);
        }
        return false;
    }

    @Override
    public int getTotalCategories() {
        String sql = "SELECT COUNT(*) AS total FROM QuestionCategories";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return 0;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error getting total categories", ex);
        }
        return 0;
    }
}

