package dao;

import dbcontext.DBContext;
import interfa.IQuestionCategoryDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.QuestionCategory;

public class QuestionCategoryDAO extends DBContext implements IQuestionCategoryDAO {

    /**
     * Lấy danh sách tất cả danh mục câu hỏi, sắp xếp theo category_id.
     * Hàm này trả về tất cả danh mục trong hệ thống (ví dụ: Ô tô, Xe máy).
     * 
     * @return Danh sách các đối tượng QuestionCategory, danh sách rỗng nếu không có
     *         dữ liệu
     */
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
        }
        return categories;
    }

    /**
     * Lấy thông tin chi tiết của một danh mục câu hỏi theo ID.
     * 
     * @param categoryId ID của danh mục cần lấy thông tin
     * @return Đối tượng QuestionCategory nếu tìm thấy, null nếu không tìm thấy hoặc
     *         có lỗi
     */
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
        }
        return null;
    }

    /**
     * Thêm một danh mục câu hỏi mới vào hệ thống.
     * 
     * @param categoryName Tên danh mục mới (ví dụ: "Ô tô", "Xe máy")
     * @return true nếu thêm thành công, false nếu có lỗi
     */
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
        }
        return false;
    }

    /**
     * Cập nhật tên của một danh mục câu hỏi.
     * 
     * @param categoryId   ID của danh mục cần cập nhật
     * @param categoryName Tên danh mục mới
     * @return true nếu cập nhật thành công, false nếu có lỗi
     */
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
        }
        return false;
    }

    /**
     * Xóa một danh mục câu hỏi khỏi hệ thống.
     * Lưu ý: Cần đảm bảo không còn câu hỏi nào thuộc danh mục này trước khi xóa.
     * 
     * @param categoryId ID của danh mục cần xóa
     * @return true nếu xóa thành công, false nếu có lỗi
     */
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
        }
        return false;
    }

    /**
     * Lấy tổng số lượng danh mục câu hỏi trong hệ thống.
     * 
     * @return Tổng số lượng danh mục, trả về 0 nếu không có dữ liệu hoặc có lỗi
     */
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
        }
        return 0;
    }
}
