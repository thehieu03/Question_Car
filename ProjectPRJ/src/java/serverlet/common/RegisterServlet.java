package serverlet.common;

import dao.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

/**
 * Servlet xử lý đăng ký tài khoản người dùng mới.
 * Servlet này xử lý cả GET (hiển thị form đăng ký) và POST (xử lý đăng ký).
 */
public class RegisterServlet extends HttpServlet {

    /**
     * Hiển thị trang đăng ký.
     * Forward request đến trang register.jsp để hiển thị form đăng ký.
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException Nếu có lỗi servlet
     * @throws IOException      Nếu có lỗi I/O
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/common/register.jsp").forward(request, response);
    }

    /**
     * Xử lý yêu cầu đăng ký từ form.
     * Hàm này:
     * 1. Lấy thông tin đăng ký: username, email, password, confirmPassword
     * 2. Kiểm tra password và confirmPassword có khớp không
     * 3. Kiểm tra username đã tồn tại chưa
     * 4. Kiểm tra email đã được sử dụng chưa
     * 5. Nếu tất cả hợp lệ: gọi UserDAO.register() để tạo tài khoản mới
     * 6. Nếu thành công: forward đến trang login với thông báo thành công
     * 7. Nếu thất bại: hiển thị thông báo lỗi và forward lại trang register
     * 
     * @param request  HttpServletRequest chứa thông tin đăng ký
     * @param response HttpServletResponse
     * @throws ServletException Nếu có lỗi servlet
     * @throws IOException      Nếu có lỗi I/O
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        UserDAO userDAO = new UserDAO();

        if (password == null || !password.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp!");
            request.getRequestDispatcher("/common/register.jsp").forward(request, response);
            return;
        }

        if (userDAO.getUserByUsername(username) != null) {
            request.setAttribute("error", "Tên đăng nhập đã tồn tại!");
            request.getRequestDispatcher("/common/register.jsp").forward(request, response);
            return;
        }

        if (userDAO.getUserByEmail(email) != null) {
            request.setAttribute("error", "Email đã được sử dụng!");
            request.getRequestDispatcher("/common/register.jsp").forward(request, response);
            return;
        }

        boolean success = userDAO.register(username, email, password);

        if (success) {
            request.setAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/common/login.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Đăng ký thất bại. Vui lòng kiểm tra lại thông tin hoặc thử lại sau!");
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/common/register.jsp").forward(request, response);
        }
    }
}
