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

/**
 * Servlet xử lý trang dashboard của admin.
 * Servlet này hiển thị thống kê tổng quan: tổng số user, câu hỏi, đề thi, bài
 * thi.
 */
public class AdminServlet extends HttpServlet {

    /**
     * Hiển thị trang dashboard admin với thống kê tổng quan.
     * Hàm này:
     * 1. Kiểm tra user đã đăng nhập và là admin chưa
     * 2. Lấy thống kê từ các DAO:
     * - Tổng số người dùng (không bao gồm admin)
     * - Tổng số câu hỏi
     * - Tổng số đề thi
     * - Tổng số bài thi
     * 3. Forward đến trang dashboard.jsp với tất cả thống kê
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException Nếu có lỗi servlet
     * @throws IOException      Nếu có lỗi I/O
     */
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

    /**
     * Xử lý POST request (gọi doGet() để xử lý tương tự).
     * 
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException Nếu có lỗi servlet
     * @throws IOException      Nếu có lỗi I/O
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
