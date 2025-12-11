package serverlet.admin;

import dao.ExamSetDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import model.ExamSet;
import model.User;

public class ExamSetManagementServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ExamSetManagementServlet.class.getName());
    private static final int EXAMS_PER_PAGE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

        int offset = (page - 1) * EXAMS_PER_PAGE;

        ExamSetDAO examSetDAO = new ExamSetDAO();
        List<ExamSet> examSets = examSetDAO.getExamSets(offset, EXAMS_PER_PAGE);
        int totalExamSets = examSetDAO.getTotalExamSets();
        int totalPages = (int) Math.ceil((double) totalExamSets / EXAMS_PER_PAGE);

        request.setAttribute("examSets", examSets);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalExamSets", totalExamSets);

        request.getRequestDispatcher("/admin/exam-management.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User admin = (User) session.getAttribute("user");

        if (admin == null || !admin.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        ExamSetDAO examSetDAO = new ExamSetDAO();
        boolean success = false;
        String message;

        if ("add".equals(action)) {
            String examName = request.getParameter("examName");
            String totalQuestionsParam = request.getParameter("totalQuestions");
            String durationParam = request.getParameter("durationMinutes");
            String passingScoreParam = request.getParameter("passingScore");

            int totalQuestions = parsePositiveInt(totalQuestionsParam);
            int durationMinutes = parsePositiveInt(durationParam);
            int passingScore = parsePositiveInt(passingScoreParam);

            if (examName == null || examName.trim().isEmpty()) {
                message = "Tên đề thi không được để trống.";
            } else if (totalQuestions <= 0 || durationMinutes <= 0 || passingScore < 0) {
                message = "Vui lòng nhập số hợp lệ cho tổng số câu, thời gian và điểm đạt.";
            } else if (passingScore > totalQuestions) {
                message = "Điểm đạt không được lớn hơn tổng số câu hỏi.";
            } else {
                success = examSetDAO.addExamSet(examName.trim(), totalQuestions, durationMinutes, passingScore);
                message = success ? "Tạo đề thi mới thành công!" : "Tạo đề thi mới thất bại!";
            }
        } else {
            message = "Hành động không hợp lệ.";
        }

        if (success) {
            request.setAttribute("success", message);
        } else {
            request.setAttribute("error", message);
        }

        doGet(request, response);
    }

    private int parsePositiveInt(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return -1;
            }
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            logger.fine("Invalid number format: " + value);
            return -1;
        }
    }
}


