package serverlet.common;

import dao.UserDAO;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;

public class LoginServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(LoginServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/common/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        logger.info("=== LoginServlet.doPost() called ===");
        logger.info("Username: " + (username != null ? username : "null"));
        logger.info("Password: " + (password != null ? "***" : "null"));
        
        UserDAO userDAO = new UserDAO();
        User user = userDAO.login(username, password);
        
        if (user != null) {
            logger.info("Login successful for user: " + user.getUsername() + " (Role: " + user.getRole() + ")");
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
                logger.info("Redirecting admin to: " + request.getContextPath() + "/admin");
                response.sendRedirect(request.getContextPath() + "/admin");
            } else {
                logger.info("Redirecting user to: " + request.getContextPath() + "/user");
                response.sendRedirect(request.getContextPath() + "/user");
            }
        } else {
            logger.warning("Login failed for username: " + username);
            request.setAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
            request.getRequestDispatcher("/common/login.jsp").forward(request, response);
        }
    }
}

