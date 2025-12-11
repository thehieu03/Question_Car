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
        
        System.out.println("=== Register Attempt ===");
        System.out.println("Username: " + username);
        System.out.println("Email: " + email);
        
        boolean success = userDAO.register(username, email, password);
        System.out.println("Register result: " + success);
        
        if (success) {
            System.out.println("Registration successful, redirecting to login page");
            request.setAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/common/login.jsp").forward(request, response);
        } else {
            System.out.println("Registration failed!");
            request.setAttribute("error", "Đăng ký thất bại. Vui lòng kiểm tra lại thông tin hoặc thử lại sau! (Xem log server để biết chi tiết)");
            request.setAttribute("username", username);
            request.setAttribute("email", email);
            request.getRequestDispatcher("/common/register.jsp").forward(request, response);
        }
    }
}

