package serverlet.user;

import dao.ExamSetDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Logger;
import model.ExamSet;
import model.User;

public class ExamStartServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ExamStartServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String examSetIdParam = request.getParameter("examSetId");
        int examSetId;
        try {
            examSetId = Integer.parseInt(examSetIdParam);
        } catch (Exception e) {
            request.setAttribute("error", "ExamSet không hợp lệ.");
            request.getRequestDispatcher("/user/exam-start.jsp").forward(request, response);
            return;
        }

        ExamSetDAO examSetDAO = new ExamSetDAO();
        ExamSet examSet = examSetDAO.getExamSetById(examSetId);
        if (examSet == null) {
            request.setAttribute("error", "Không tìm thấy đề thi.");
        } else {
            request.setAttribute("examSet", examSet);
        }

        request.getRequestDispatcher("/user/exam-start.jsp").forward(request, response);
    }
}


