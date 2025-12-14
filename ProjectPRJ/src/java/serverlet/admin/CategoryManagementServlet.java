package serverlet.admin;

import dao.QuestionCategoryDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import model.QuestionCategory;
import model.User;

/**
 * Servlet quản lý danh mục câu hỏi cho admin.
 * Servlet này xử lý:
 * - GET: Hiển thị danh sách tất cả danh mục
 * - POST: Xử lý các thao tác: thêm, sửa, xóa danh mục
 */
public class CategoryManagementServlet extends HttpServlet {

    /**
     * Hiển thị trang quản lý danh mục câu hỏi.
     * Hàm này:
     * 1. Kiểm tra user đã đăng nhập và là admin chưa
     * 2. Lấy danh sách tất cả danh mục
     * 3. Lấy tổng số danh mục
     * 4. Forward đến trang category-management.jsp với danh sách danh mục
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException Nếu có lỗi servlet
     * @throws IOException      Nếu có lỗi I/O
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User admin = (User) session.getAttribute("user");

        if (admin == null || !admin.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        QuestionCategoryDAO categoryDAO = new QuestionCategoryDAO();
        List<QuestionCategory> categories = categoryDAO.getAllCategories();
        int totalCategories = categoryDAO.getTotalCategories();

        request.setAttribute("categories", categories);
        request.setAttribute("totalCategories", totalCategories);

        request.getRequestDispatcher("/admin/category-management.jsp").forward(request, response);
    }

    /**
     * Xử lý các thao tác quản lý danh mục.
     * Hàm này xử lý các action:
     * - "add": Thêm danh mục mới
     * - "update": Cập nhật tên danh mục
     * - "delete": Xóa danh mục
     * Sau khi xử lý, gọi doGet() để hiển thị lại danh sách với thông báo kết quả.
     * 
     * @param request  HttpServletRequest chứa action và các thông tin liên quan
     * @param response HttpServletResponse
     * @throws ServletException Nếu có lỗi servlet
     * @throws IOException      Nếu có lỗi I/O
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User admin = (User) session.getAttribute("user");

        if (admin == null || !admin.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        QuestionCategoryDAO categoryDAO = new QuestionCategoryDAO();
        String message = "";
        boolean success = false;

        if ("add".equals(action)) {
            String categoryName = request.getParameter("categoryName");
            if (categoryName != null && !categoryName.trim().isEmpty()) {
                success = categoryDAO.addCategory(categoryName.trim());
                message = success ? "Thêm danh mục thành công!" : "Thêm danh mục thất bại!";
            } else {
                message = "Tên danh mục không được để trống!";
            }
        } else if ("update".equals(action)) {
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            String categoryName = request.getParameter("categoryName");
            if (categoryName != null && !categoryName.trim().isEmpty()) {
                success = categoryDAO.updateCategory(categoryId, categoryName.trim());
                message = success ? "Cập nhật danh mục thành công!" : "Cập nhật danh mục thất bại!";
            } else {
                message = "Tên danh mục không được để trống!";
            }
        } else if ("delete".equals(action)) {
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            success = categoryDAO.deleteCategory(categoryId);
            message = success ? "Xóa danh mục thành công!" : "Xóa danh mục thất bại!";
        }

        if (success) {
            request.setAttribute("success", message);
        } else {
            request.setAttribute("error", message);
        }

        doGet(request, response);
    }
}
