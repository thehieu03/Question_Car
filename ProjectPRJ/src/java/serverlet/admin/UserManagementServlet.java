package serverlet.admin;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import model.User;

public class UserManagementServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(UserManagementServlet.class.getName());

    private static final int USERS_PER_PAGE = 5;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User admin = (User) session.getAttribute("user");
        
        if (admin == null || !admin.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserDAO userDAO = new UserDAO();
        String keyword = request.getParameter("keyword");
        String pageParam = request.getParameter("page");
        
        int page = 1;
        try {
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            page = 1;
        }
        
        int offset = (page - 1) * USERS_PER_PAGE;

        List<User> users;
        int totalUsers;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            String trimmedKeyword = keyword.trim();
            users = userDAO.searchUsers(trimmedKeyword, offset, USERS_PER_PAGE);
            totalUsers = userDAO.getTotalUsersBySearch(trimmedKeyword);
            request.setAttribute("keyword", trimmedKeyword);
            logger.info("Searching for keyword: " + trimmedKeyword);
            logger.info("Found " + users.size() + " users");
        } else {
            users = userDAO.getAllUsers(offset, USERS_PER_PAGE);
            totalUsers = userDAO.getTotalUsers();
            logger.info("Getting all users, total: " + totalUsers);
            logger.info("Returned " + users.size() + " users");
        }

        int totalPages = (int) Math.ceil((double) totalUsers / USERS_PER_PAGE);

        request.setAttribute("users", users);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalUsers", totalUsers);

        request.getRequestDispatcher("/admin/user-management.jsp").forward(request, response);
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
        UserDAO userDAO = new UserDAO();
        String message = "";
        boolean success = false;

        if ("ban".equals(action)) {
            int userId = Integer.parseInt(request.getParameter("userId"));
            success = userDAO.banUser(userId);
            message = success ? "Đã cấm người dùng thành công!" : "Cấm người dùng thất bại!";
        } else if ("unban".equals(action)) {
            int userId = Integer.parseInt(request.getParameter("userId"));
            success = userDAO.unbanUser(userId);
            message = success ? "Đã gỡ cấm người dùng thành công!" : "Gỡ cấm người dùng thất bại!";
        } else if ("update".equals(action)) {
            int userId = Integer.parseInt(request.getParameter("userId"));
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            int role = Integer.parseInt(request.getParameter("role"));
            success = userDAO.updateUser(userId, username, email, role);
            message = success ? "Cập nhật người dùng thành công!" : "Cập nhật người dùng thất bại!";
        }

        if (success) {
            request.setAttribute("success", message);
        } else {
            request.setAttribute("error", message);
        }

        doGet(request, response);
    }
}

