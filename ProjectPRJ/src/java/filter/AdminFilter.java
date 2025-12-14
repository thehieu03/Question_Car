package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.User;

/**
 * Filter kiểm tra quyền truy cập Admin cho các trang quản trị.
 * Filter này chỉ cho phép người dùng có role = 1 (Admin) truy cập các trang
 * admin.
 * Nếu người dùng chưa đăng nhập hoặc không phải admin, sẽ redirect về trang
 * login.
 */
public class AdminFilter implements Filter {

    /**
     * Khởi tạo filter (không cần cấu hình gì).
     * 
     * @param filterConfig Cấu hình filter
     * @throws ServletException Nếu có lỗi khi khởi tạo
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    /**
     * Thực hiện kiểm tra quyền truy cập Admin.
     * Hàm này kiểm tra:
     * 1. Lấy thông tin user từ session
     * 2. Lấy role từ cookie (nếu session không có)
     * 3. Kiểm tra xem user có phải admin không (role = 1 hoặc user.isAdmin() =
     * true)
     * 4. Nếu không phải admin: redirect về trang login
     * 5. Nếu là admin: cho phép tiếp tục (chain.doFilter)
     * 
     * @param request  ServletRequest (sẽ được cast thành HttpServletRequest)
     * @param response ServletResponse (sẽ được cast thành HttpServletResponse)
     * @param chain    FilterChain để tiếp tục xử lý request nếu hợp lệ
     * @throws IOException      Nếu có lỗi I/O khi redirect hoặc xử lý response
     * @throws ServletException Nếu có lỗi servlet
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);
        User user = null;
        if (session != null) {
            user = (User) session.getAttribute("user");
        }

        String role = null;
        Cookie[] cookies = httpRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("role".equals(cookie.getName())) {
                    role = cookie.getValue();
                    break;
                }
            }
        }

        boolean isAdmin = false;
        if (user != null && user.isAdmin()) {
            isAdmin = true;
        } else if (role != null && "1".equals(role)) {
            isAdmin = true;
        }

        if (!isAdmin) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
            return;
        }

        chain.doFilter(request, response);
    }

    /**
     * Hủy filter khi ứng dụng dừng (không cần cleanup gì).
     */
    @Override
    public void destroy() {
    }
}
