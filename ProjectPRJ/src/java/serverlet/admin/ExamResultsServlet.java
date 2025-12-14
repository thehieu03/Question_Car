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

/**
 * Servlet hiển thị kết quả thi của tất cả người dùng cho admin.
 * Servlet này hiển thị danh sách tất cả bài thi với thông tin người dùng và đề
 * thi, có phân trang.
 */
public class ExamResultsServlet extends HttpServlet {
    /** Số lượng kết quả thi hiển thị trên mỗi trang */
    private static final int RESULTS_PER_PAGE = 10;

    /**
     * Hiển thị trang kết quả thi của tất cả người dùng.
     * Hàm này:
     * 1. Kiểm tra user đã đăng nhập và là admin chưa
     * 2. Lấy số trang từ request (mặc định là trang 1)
     * 3. Lấy danh sách bài thi kèm thông tin user và đề thi với phân trang
     * 4. Tính tổng số trang
     * 5. Forward đến trang exam-results.jsp với danh sách kết quả
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
