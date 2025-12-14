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

/**
 * Servlet hiển thị lịch sử bài thi của người dùng.
 * Servlet này hiển thị danh sách tất cả bài thi đã hoàn thành của user với phân
 * trang.
 */
public class ExamHistoryServlet extends HttpServlet {
    /** Số lượng bài thi hiển thị trên mỗi trang */
    private static final int EXAMS_PER_PAGE = 10;

    /**
     * Hiển thị trang lịch sử bài thi.
     * Hàm này:
     * 1. Kiểm tra user đã đăng nhập chưa
     * 2. Lấy số trang từ request (mặc định là trang 1)
     * 3. Lấy danh sách bài thi đã hoàn thành của user với phân trang
     * 4. Tính tổng số trang
     * 5. Forward đến trang exam-history.jsp với tất cả dữ liệu
     * 
     * @param request  HttpServletRequest chứa page
     * @param response HttpServletResponse
     * @throws ServletException Nếu có lỗi servlet
     * @throws IOException      Nếu có lỗi I/O
     */
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
