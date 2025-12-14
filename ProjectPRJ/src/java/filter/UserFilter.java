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
 * Filter kiểm tra quyền truy cập User cho các trang người dùng.
 * Filter này chỉ cho phép người dùng có role = 0 (User, không phải Admin) truy
 * cập các trang user.
 * Nếu người dùng chưa đăng nhập hoặc là admin, sẽ redirect về trang login.
 */
public class UserFilter implements Filter {

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
     * Thực hiện kiểm tra quyền truy cập User.
     * Hàm này kiểm tra:
     * 1. Lấy thông tin user từ session
     * 2. Lấy role từ cookie (nếu session không có)
     * 3. Kiểm tra xem user có phải user thường không (role = 0 và không phải admin)
     * 4. Nếu không phải user thường: redirect về trang login
     * 5. Nếu là user thường: cho phép tiếp tục (chain.doFilter)
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

        boolean isUser = false;
        if (user != null && !user.isAdmin()) {
            isUser = true;
        } else if (role != null && "0".equals(role)) {
            isUser = true;
        }

        if (!isUser) {
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
