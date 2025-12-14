package serverlet.common;

import dao.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

/**
 * Servlet xử lý đăng nhập người dùng.
 * Servlet này xử lý cả GET (hiển thị form đăng nhập) và POST (xác thực đăng
 * nhập).
 */
public class LoginServlet extends HttpServlet {

    /**
     * Hiển thị trang đăng nhập.
     * Forward request đến trang login.jsp để hiển thị form đăng nhập.
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException Nếu có lỗi servlet
     * @throws IOException      Nếu có lỗi I/O
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/common/login.jsp").forward(request, response);
    }

    /**
     * Xử lý yêu cầu đăng nhập từ form.
     * Hàm này:
     * 1. Lấy username và password từ request
     * 2. Gọi UserDAO.login() để xác thực
     * 3. Nếu đăng nhập thành công:
     * - Lưu user vào session
     * - Tạo cookie lưu username và role (thời hạn 24 giờ)
     * - Redirect đến trang admin hoặc user tùy theo role
     * 4. Nếu đăng nhập thất bại: hiển thị thông báo lỗi và forward lại trang login
     * 
     * @param request  HttpServletRequest chứa username và password
     * @param response HttpServletResponse
     * @throws ServletException Nếu có lỗi servlet
     * @throws IOException      Nếu có lỗi I/O
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        UserDAO userDAO = new UserDAO();
        User user = userDAO.login(username, password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);

            Cookie usernameCookie = new Cookie("username", user.getUsername());
            usernameCookie.setMaxAge(24 * 60 * 60);
            usernameCookie.setPath("/");
            response.addCookie(usernameCookie);

            Cookie roleCookie = new Cookie("role", String.valueOf(user.getRole()));
            roleCookie.setMaxAge(24 * 60 * 60);
            roleCookie.setPath("/");
            response.addCookie(roleCookie);

            if (user.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/admin");
            } else {
                response.sendRedirect(request.getContextPath() + "/user");
            }
        } else {
            request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
            request.getRequestDispatcher("/common/login.jsp").forward(request, response);
        }
    }
}
