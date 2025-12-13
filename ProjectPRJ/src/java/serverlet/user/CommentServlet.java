package serverlet.user;

import dao.ExamSetCommentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.User;

public class CommentServlet extends HttpServlet {

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
