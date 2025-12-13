package serverlet.user;

import dao.ExamSetDAO;
import dao.UserExamDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import model.User;
import model.UserExam;

public class ExamHistoryServlet extends HttpServlet {
    private static final int EXAMS_PER_PAGE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String pageParam = request.getParameter("page");
        int page = 1;
        try {
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                page = Integer.parseInt(pageParam.trim());
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        int offset = (page - 1) * EXAMS_PER_PAGE;

        UserExamDAO userExamDAO = new UserExamDAO();
        List<UserExam> userExams = userExamDAO.getUserExamsByUser(user.getUserId(), offset, EXAMS_PER_PAGE);
        int totalExams = userExamDAO.getTotalUserExamsByUser(user.getUserId());
        int totalPages = (int) Math.ceil((double) totalExams / EXAMS_PER_PAGE);

        request.setAttribute("userExams", userExams);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalExams", totalExams);

        request.getRequestDispatcher("/user/exam-history.jsp").forward(request, response);
    }
}
