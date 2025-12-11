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

public class CategoryManagementServlet extends HttpServlet {

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

