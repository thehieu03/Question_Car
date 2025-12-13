package serverlet.common;

import dao.UserDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

public class RegisterServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/common/register.jsp").forward(request, response);
    }

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
