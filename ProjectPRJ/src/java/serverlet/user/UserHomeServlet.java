package serverlet.user;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.User;

/**
 * Servlet xử lý trang chủ của người dùng.
 * Servlet này kiểm tra đăng nhập và redirect đến trang chọn đề thi.
 */
public class UserHomeServlet extends HttpServlet {

    /**
     * Xử lý yêu cầu truy cập trang chủ user.
     * Hàm này:
     * 1. Kiểm tra user đã đăng nhập chưa (lấy từ session)
     * 2. Nếu chưa đăng nhập: redirect về trang login
     * 3. Nếu đã đăng nhập: redirect đến trang chọn đề thi (/user/exams)
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
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        response.sendRedirect(request.getContextPath() + "/user/exams");
    }
}
