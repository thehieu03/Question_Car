package serverlet.admin;

import dao.ExamSetDAO;
import dao.QuestionDAO;
import dao.UserDAO;
import dao.UserExamDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.User;

public class AdminServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null || !user.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        UserDAO userDAO = new UserDAO();
        QuestionDAO questionDAO = new QuestionDAO();
        ExamSetDAO examSetDAO = new ExamSetDAO();
        UserExamDAO userExamDAO = new UserExamDAO();
        
        int totalUsers = userDAO.getTotalUsers();
        int totalQuestions = questionDAO.getTotalQuestions();
        int totalExamSets = examSetDAO.getTotalExamSets();
        int totalUserExams = userExamDAO.getTotalUserExams();
        
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("totalQuestions", totalQuestions);
        request.setAttribute("totalExamSets", totalExamSets);
        request.setAttribute("totalUserExams", totalUserExams);
        
        request.getRequestDispatcher("/admin/dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

