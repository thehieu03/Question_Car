package serverlet.admin;

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

public class ExamResultsServlet extends HttpServlet {
    private static final int RESULTS_PER_PAGE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User admin = (User) session.getAttribute("user");
        if (admin == null || !admin.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String pageParam = request.getParameter("page");
        int page = 1;
        try {
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        int offset = (page - 1) * RESULTS_PER_PAGE;

        UserExamDAO userExamDAO = new UserExamDAO();
        List<UserExam> results = userExamDAO.getUserExamsWithInfo(offset, RESULTS_PER_PAGE);
        int total = userExamDAO.getTotalUserExams();
        int totalPages = (int) Math.ceil((double) total / RESULTS_PER_PAGE);

        request.setAttribute("results", results);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalResults", total);

        request.getRequestDispatcher("/admin/exam-results.jsp").forward(request, response);
    }
}
