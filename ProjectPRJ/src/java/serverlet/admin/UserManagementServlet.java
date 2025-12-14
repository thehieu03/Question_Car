package serverlet.admin;

import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import model.User;

/**
 * Servlet quản lý người dùng cho admin.
 * Servlet này xử lý:
 * - GET: Hiển thị danh sách người dùng với tìm kiếm và phân trang
 * - POST: Xử lý các thao tác: cập nhật, xóa người dùng
 */
public class UserManagementServlet extends HttpServlet {

    /** Số lượng người dùng hiển thị trên mỗi trang */
    private static final int USERS_PER_PAGE = 5;

    /**
     * Hiển thị trang quản lý người dùng.
     * Hàm này:
     * 1. Kiểm tra user đã đăng nhập và là admin chưa
     * 2. Lấy từ khóa tìm kiếm và số trang từ request
     * 3. Nếu có từ khóa: tìm kiếm user theo username/email
     * 4. Nếu không có từ khóa: lấy tất cả user
     * 5. Tính tổng số trang
     * 6. Forward đến trang user-management.jsp với danh sách user
     * 
     * @param request  HttpServletRequest chứa keyword và page
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
        } else {
            users = userDAO.getAllUsers(offset, USERS_PER_PAGE);
            totalUsers = userDAO.getTotalUsers();
        }

        int totalPages = (int) Math.ceil((double) totalUsers / USERS_PER_PAGE);

        request.setAttribute("users", users);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalUsers", totalUsers);

        request.getRequestDispatcher("/admin/user-management.jsp").forward(request, response);
    }

    /**
     * Xử lý các thao tác quản lý người dùng.
     * Hàm này xử lý các action:
     * - "ban": Cấm người dùng (set role = -1)
     * - "unban": Gỡ cấm người dùng (set role = 0)
     * - "delete": Xóa người dùng và tất cả dữ liệu liên quan
     * - "update": Cập nhật thông tin người dùng (username, email, role)
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
        } else if ("delete".equals(action)) {
            int userId = Integer.parseInt(request.getParameter("userId"));
            User userToDelete = userDAO.getUserById(userId);
            if (userToDelete != null && userToDelete.isAdmin()) {
                message = "Không thể xóa tài khoản Admin!";
                success = false;
            } else {
                success = userDAO.deleteUser(userId);
                message = success ? "Đã xóa người dùng thành công!" : "Xóa người dùng thất bại!";
            }
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
