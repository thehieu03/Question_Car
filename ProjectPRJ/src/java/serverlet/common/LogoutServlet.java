package serverlet.common;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet xử lý đăng xuất người dùng.
 * Servlet này xóa session và cookie, sau đó redirect về trang login.
 */
public class LogoutServlet extends HttpServlet {

    /**
     * Xử lý yêu cầu đăng xuất (GET hoặc POST).
     * Hàm này:
     * 1. Invalidate session hiện tại (xóa tất cả dữ liệu trong session)
     * 2. Xóa cookie username và role bằng cách set MaxAge = 0
     * 3. Redirect về trang login
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException Nếu có lỗi servlet
     * @throws IOException      Nếu có lỗi I/O
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        Cookie usernameCookie = new Cookie("username", "");
        usernameCookie.setMaxAge(0);
        usernameCookie.setPath("/");
        response.addCookie(usernameCookie);

        Cookie roleCookie = new Cookie("role", "");
        roleCookie.setMaxAge(0);
        roleCookie.setPath("/");
        response.addCookie(roleCookie);

        response.sendRedirect(request.getContextPath() + "/login");
    }

    /**
     * Xử lý yêu cầu đăng xuất từ POST request.
     * Gọi doGet() để xử lý tương tự.
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException Nếu có lỗi servlet
     * @throws IOException      Nếu có lỗi I/O
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
