package serverlet.user;

import dao.ExamSetCommentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.User;

/**
 * Servlet xử lý bình luận của người dùng về đề thi.
 * Servlet này chỉ xử lý POST request để thêm bình luận mới.
 */
public class CommentServlet extends HttpServlet {

    /**
     * Xử lý yêu cầu thêm bình luận về đề thi.
     * Hàm này:
     * 1. Kiểm tra user đã đăng nhập chưa
     * 2. Lấy examSetId, content và userExamId từ request
     * 3. Kiểm tra examSetId hợp lệ
     * 4. Nếu content không rỗng: gọi ExamSetCommentDAO.addComment() để thêm bình
     * luận
     * 5. Redirect về trang kết quả bài thi hoặc trang lịch sử tùy theo userExamId
     * 
     * @param request  HttpServletRequest chứa examSetId, content và userExamId
     * @param response HttpServletResponse
     * @throws ServletException Nếu có lỗi servlet
     * @throws IOException      Nếu có lỗi I/O
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String examSetIdParam = request.getParameter("examSetId");
        String content = request.getParameter("content");
        String userExamIdParam = request.getParameter("userExamId");

        if (examSetIdParam == null || examSetIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/user/exam/result?userExamId=" + userExamIdParam
                    + "&error=Invalid exam set");
            return;
        }

        int examSetId;
        try {
            examSetId = Integer.parseInt(examSetIdParam);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/user/exam/result?userExamId=" + userExamIdParam
                    + "&error=Invalid exam set ID");
            return;
        }

        // Content is optional, but if provided, it should not be empty
        if (content != null && !content.trim().isEmpty()) {
            ExamSetCommentDAO commentDAO = new ExamSetCommentDAO();
            commentDAO.addComment(examSetId, user.getUserId(), content.trim());
        }

        // Redirect back to result page
        if (userExamIdParam != null && !userExamIdParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/user/exam/result?userExamId=" + userExamIdParam);
        } else {
            response.sendRedirect(request.getContextPath() + "/user/history");
        }
    }
}
