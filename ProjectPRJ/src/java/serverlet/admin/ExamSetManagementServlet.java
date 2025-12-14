package serverlet.admin;

import dao.ExamSetDAO;
import dao.AnswerDAO;
import dao.QuestionDAO;
import dao.QuestionCategoryDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.ExamSet;
import model.Question;
import model.Answer;
import model.QuestionCategory;
import model.User;

/**
 * Servlet quản lý đề thi cho admin.
 * Servlet này xử lý:
 * - GET: Hiển thị danh sách đề thi với phân trang, hoặc xem chi tiết đề thi
 * (action = "view")
 * - POST: Xử lý các thao tác: thêm, xóa đề thi
 */
public class ExamSetManagementServlet extends HttpServlet {

    /** Số lượng đề thi hiển thị trên mỗi trang */
    private static final int EXAMS_PER_PAGE = 10;

    /**
     * Hiển thị trang quản lý đề thi hoặc xem chi tiết đề thi.
     * Hàm này:
     * 1. Kiểm tra user đã đăng nhập và là admin chưa
     * 2. Nếu action = "view": gọi handleViewDetail() để hiển thị chi tiết đề thi
     * 3. Nếu không có action đặc biệt:
     * - Lấy số trang từ request (mặc định là trang 1)
     * - Lấy danh sách đề thi với phân trang
     * - Lấy danh sách danh mục để hiển thị dropdown
     * - Tính tổng số trang
     * - Forward đến trang exam-management.jsp
     * 
     * @param request  HttpServletRequest chứa page và action
     * @param response HttpServletResponse
     * @throws ServletException Nếu có lỗi servlet
     * @throws IOException      Nếu có lỗi I/O
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User admin = (User) session.getAttribute("user");

        if (admin == null || !admin.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if ("view".equals(action)) {
            handleViewDetail(request, response);
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
        QuestionCategoryDAO categoryDAO = new QuestionCategoryDAO();
        List<QuestionCategory> categories = categoryDAO.getAllCategories();

        request.setAttribute("examSets", examSets);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalExamSets", totalExamSets);
        request.setAttribute("categories", categories);

        request.getRequestDispatcher("/admin/exam-management.jsp").forward(request, response);
    }

    /**
     * Xử lý các thao tác quản lý đề thi.
     * Hàm này xử lý các action:
     * - "add": Tạo đề thi mới:
     * + Kiểm tra tính hợp lệ của dữ liệu đầu vào
     * + Kiểm tra đủ số lượng câu hỏi (40% câu điểm liệt, 60% câu thường)
     * + Tạo đề thi và tự động chọn câu hỏi từ danh mục
     * - "delete": Xóa đề thi và tất cả bài thi liên quan
     * Sau khi xử lý, gọi doGet() để hiển thị lại danh sách với thông báo kết quả.
     * 
     * @param request  HttpServletRequest chứa action và các thông tin đề thi
     * @param response HttpServletResponse
     * @throws ServletException Nếu có lỗi servlet
     * @throws IOException      Nếu có lỗi I/O
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User admin = (User) session.getAttribute("user");

        if (admin == null || !admin.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        ExamSetDAO examSetDAO = new ExamSetDAO();
        QuestionDAO questionDAO = new QuestionDAO();
        boolean success = false;
        String message;

        if ("add".equals(action)) {
            String examName = request.getParameter("examName");
            String totalQuestionsParam = request.getParameter("totalQuestions");
            String durationParam = request.getParameter("durationMinutes");
            String passingScoreParam = request.getParameter("passingScore");
            String categoryIdParam = request.getParameter("categoryId");

            int totalQuestions = parsePositiveInt(totalQuestionsParam);
            int durationMinutes = parsePositiveInt(durationParam);
            int passingScore = parsePositiveInt(passingScoreParam);
            int categoryId = parsePositiveInt(categoryIdParam);

            if (examName == null || examName.trim().isEmpty()) {
                message = "Tên đề thi không được để trống.";
            } else if (categoryId <= 0) {
                message = "Vui lòng chọn loại đề (danh mục).";
            } else if (totalQuestions <= 0 || durationMinutes <= 0 || passingScore < 0) {
                message = "Vui lòng nhập số hợp lệ cho tổng số câu, thời gian và điểm đạt.";
            } else if (passingScore > totalQuestions) {
                message = "Điểm đạt không được lớn hơn tổng số câu hỏi.";
            } else {
                int neededCritical = (int) Math.ceil(totalQuestions * 0.4);
                int availableCritical = questionDAO.getTotalQuestionsFiltered(null, "critical", categoryId);
                int availableNormal = questionDAO.getTotalQuestionsFiltered(null, "normal", categoryId);
                int availableAll = availableCritical + availableNormal;

                if (availableCritical < neededCritical) {
                    message = "Không đủ câu điểm liệt. Cần " + neededCritical + ", hiện có " + availableCritical + ".";
                } else if (availableAll < totalQuestions) {
                    message = "Số câu hỏi khả dụng (" + availableAll + ") không đủ cho danh mục đã chọn.";
                } else {
                    success = examSetDAO.addExamSet(examName.trim(), totalQuestions, durationMinutes, passingScore);
                    if (success) {
                        int newExamSetId = examSetDAO.getLastInsertedExamSetId();
                        List<Question> selectedQuestions = new ArrayList<>();
                        selectedQuestions.addAll(
                                questionDAO.getQuestionsFiltered(null, "critical", categoryId, 0, neededCritical));
                        selectedQuestions.addAll(
                                questionDAO.getQuestionsFiltered(null, "normal", categoryId, 0,
                                        totalQuestions - neededCritical));
                        boolean linked = examSetDAO.addExamQuestions(newExamSetId, selectedQuestions);
                        if (!linked) {
                            message = "Tạo đề thi thất bại khi gán câu hỏi.";
                            success = false;
                        } else {
                            message = "Tạo đề thi mới thành công!";
                        }
                    } else {
                        message = "Tạo đề thi mới thất bại!";
                    }
                }
            }
        } else if ("delete".equals(action)) {
            String examSetIdParam = request.getParameter("examSetId");
            int examSetId = parsePositiveInt(examSetIdParam);
            if (examSetId <= 0) {
                message = "Exam set id không hợp lệ.";
            } else {
                success = examSetDAO.deleteExamSet(examSetId);
                message = success ? "Xóa đề thi thành công!" : "Xóa đề thi thất bại!";
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

    /**
     * Hiển thị chi tiết một đề thi.
     * Hàm này:
     * 1. Lấy examSetId từ request và kiểm tra tính hợp lệ
     * 2. Lấy thông tin đề thi
     * 3. Lấy danh sách câu hỏi trong đề thi
     * 4. Lấy đáp án cho từng câu hỏi
     * 5. Forward đến trang exam-detail.jsp với tất cả dữ liệu
     * 
     * @param request  HttpServletRequest chứa examSetId
     * @param response HttpServletResponse
     * @throws ServletException Nếu có lỗi servlet
     * @throws IOException      Nếu có lỗi I/O
     */
    private void handleViewDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String examSetIdParam = request.getParameter("examSetId");
        int examSetId;
        try {
            examSetId = Integer.parseInt(examSetIdParam);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Exam set id không hợp lệ.");
            doGet(request, response);
            return;
        }

        ExamSetDAO examSetDAO = new ExamSetDAO();
        ExamSet examSet = examSetDAO.getExamSetById(examSetId);
        if (examSet == null) {
            request.setAttribute("error", "Không tìm thấy đề thi.");
            doGet(request, response);
            return;
        }

        List<Question> questions = examSetDAO.getQuestionsByExamSet(examSetId);
        AnswerDAO answerDAO = new AnswerDAO();
        Map<Integer, List<Answer>> answersMap = new HashMap<>();
        for (Question q : questions) {
            answersMap.put(q.getQuestionId(), answerDAO.getAnswersByQuestionId(q.getQuestionId()));
        }

        request.setAttribute("examSet", examSet);
        request.setAttribute("questions", questions);
        request.setAttribute("answersMap", answersMap);

        request.getRequestDispatcher("/admin/exam-detail.jsp").forward(request, response);
    }

    /**
     * Chuyển đổi chuỗi thành số nguyên dương.
     * Hàm helper này parse string thành int, trả về -1 nếu không hợp lệ.
     * 
     * @param value Chuỗi cần chuyển đổi
     * @return Số nguyên dương nếu hợp lệ, -1 nếu không hợp lệ hoặc rỗng
     */
    private int parsePositiveInt(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return -1;
            }
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
}
