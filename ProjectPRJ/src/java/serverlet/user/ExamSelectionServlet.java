package serverlet.user;

import dao.ExamSetDAO;
import dao.QuestionCategoryDAO;
import dao.UserExamDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import model.ExamSet;
import model.QuestionCategory;
import model.User;

public class ExamSelectionServlet extends HttpServlet {
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

        QuestionCategoryDAO categoryDAO = new QuestionCategoryDAO();
        List<QuestionCategory> categories = categoryDAO.getAllCategories();
        if (categories == null || categories.isEmpty()) {
            request.setAttribute("error", "Chưa có danh mục câu hỏi.");
            request.getRequestDispatcher("/user/exam-selection.jsp").forward(request, response);
            return;
        }

        String categoryParam = request.getParameter("categoryId");
        int selectedCategoryId = categories.get(0).getCategoryId();
        if (categoryParam != null && !categoryParam.trim().isEmpty()) {
            try {
                selectedCategoryId = Integer.parseInt(categoryParam.trim());
            } catch (NumberFormatException e) {
                selectedCategoryId = categories.get(0).getCategoryId();
            }
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

        ExamSetDAO examSetDAO = new ExamSetDAO();
        List<ExamSet> examSets = examSetDAO.getExamSetsByCategory(selectedCategoryId, offset, EXAMS_PER_PAGE);
        int totalExamSets = examSetDAO.getTotalExamSetsByCategory(selectedCategoryId);
        int totalPages = (int) Math.ceil((double) totalExamSets / EXAMS_PER_PAGE);

        UserExamDAO userExamDAO = new UserExamDAO();
        int totalTaken = userExamDAO.getTotalUserExamsByUser(user.getUserId());
        int totalPassed = userExamDAO.getPassedUserExamsByUser(user.getUserId());
        Integer lastScore = userExamDAO.getLastScoreByUser(user.getUserId());

        request.setAttribute("categories", categories);
        request.setAttribute("examSets", examSets);
        request.setAttribute("selectedCategoryId", selectedCategoryId);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalExamSets", totalExamSets);
        request.setAttribute("totalTaken", totalTaken);
        request.setAttribute("totalPassed", totalPassed);
        request.setAttribute("lastScore", lastScore);

        request.getRequestDispatcher("/user/exam-selection.jsp").forward(request, response);
    }
}
