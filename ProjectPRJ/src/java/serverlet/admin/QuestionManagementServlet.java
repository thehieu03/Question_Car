package serverlet.admin;

import dao.AnswerDAO;
import dao.QuestionCategoryDAO;
import dao.QuestionDAO;
import dbcontext.DBContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Question;
import model.QuestionCategory;
import model.User;

public class QuestionManagementServlet extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(QuestionManagementServlet.class.getName());

    private static final int QUESTIONS_PER_PAGE = 10;

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
        if ("getAnswers".equals(action)) {
            AnswerDAO answerDAO = new AnswerDAO();
            int questionId = Integer.parseInt(request.getParameter("questionId"));
            List<model.Answer> answers = answerDAO.getAnswersByQuestionId(questionId);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            java.io.PrintWriter out = response.getWriter();
            out.print("[");
            for (int i = 0; i < answers.size(); i++) {
                model.Answer ans = answers.get(i);
                out.print("{");
                out.print("\"answerId\":" + ans.getAnswerId() + ",");
                out.print("\"answerText\":\"" + ans.getAnswerText().replace("\"", "\\\"").replace("\n", "\\n") + "\",");
                out.print("\"isCorrect\":" + ans.isCorrect() + ",");
                out.print("\"answerOrder\":" + ans.getAnswerOrder());
                out.print("}");
                if (i < answers.size() - 1) {
                    out.print(",");
                }
            }
            out.print("]");
            out.flush();
            return;
        }

        QuestionDAO questionDAO = new QuestionDAO();
        QuestionCategoryDAO categoryDAO = new QuestionCategoryDAO();
        
        String keyword = request.getParameter("keyword");
        String type = request.getParameter("type");
        String categoryIdParam = request.getParameter("categoryId");
        String pageParam = request.getParameter("page");
        
        if (type == null || type.trim().isEmpty()) {
            type = "all";
        } else {
            type = type.trim().toLowerCase();
            if (!type.equals("critical") && !type.equals("normal")) {
                type = "all";
            }
        }

        Integer categoryId = null;
        if (categoryIdParam != null && !categoryIdParam.trim().isEmpty()) {
            try {
                int parsed = Integer.parseInt(categoryIdParam.trim());
                if (parsed > 0) {
                    categoryId = parsed;
                }
            } catch (NumberFormatException e) {
                categoryId = null;
            }
        }
        
        int page = 1;
        try {
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            page = 1;
        }
        
        int offset = (page - 1) * QUESTIONS_PER_PAGE;

        List<Question> questions;
        int totalQuestions;
        
        String trimmedKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        questions = questionDAO.getQuestionsFiltered(trimmedKeyword, type, categoryId, offset, QUESTIONS_PER_PAGE);
        totalQuestions = questionDAO.getTotalQuestionsFiltered(trimmedKeyword, type, categoryId);

        if (trimmedKeyword != null) {
            request.setAttribute("keyword", trimmedKeyword);
        }
        request.setAttribute("type", type);
        if (categoryId != null) {
            request.setAttribute("categoryId", categoryId);
        }

        int totalPages = (int) Math.ceil((double) totalQuestions / QUESTIONS_PER_PAGE);
        List<QuestionCategory> categories = categoryDAO.getAllCategories();

        request.setAttribute("questions", questions);
        request.setAttribute("categories", categories);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalQuestions", totalQuestions);

        request.getRequestDispatcher("/admin/question-management.jsp").forward(request, response);
    }

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
        QuestionDAO questionDAO = new QuestionDAO();
        AnswerDAO answerDAO = new AnswerDAO();
        String message = "";
        boolean success = false;

        if ("delete".equals(action)) {
            int questionId = Integer.parseInt(request.getParameter("questionId"));
            success = questionDAO.deleteQuestion(questionId);
            message = success ? "Xóa câu hỏi thành công!" : "Xóa câu hỏi thất bại!";
        } else if ("add".equals(action)) {
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            String questionText = request.getParameter("questionText");
            String questionImage = request.getParameter("questionImage");
            String explanation = request.getParameter("explanation");
            boolean isCritical = "on".equals(request.getParameter("isCritical"));
            
            success = questionDAO.addQuestion(categoryId, questionText, questionImage, explanation, isCritical);
            
                if (success) {
                int newQuestionId = getLastInsertedQuestionId(questionDAO);
                if (newQuestionId > 0) {
                    String correctAnswer = request.getParameter("correctAnswer");
                    int correctAnswerIndex = 0;
                    if (correctAnswer != null && !correctAnswer.isEmpty()) {
                        try {
                            correctAnswerIndex = Integer.parseInt(correctAnswer);
                        } catch (NumberFormatException e) {
                            correctAnswerIndex = 0;
                        }
                    }
                    for (int i = 1; i <= 4; i++) {
                        String answerText = request.getParameter("answer" + i);
                        boolean isCorrect = (i == correctAnswerIndex);
                        if (answerText != null && !answerText.trim().isEmpty()) {
                            answerDAO.addAnswer(newQuestionId, answerText.trim(), isCorrect, i);
                        }
                    }
                }
                message = "Thêm câu hỏi thành công!";
            } else {
                message = "Thêm câu hỏi thất bại!";
            }
        } else if ("update".equals(action)) {
            int questionId = Integer.parseInt(request.getParameter("questionId"));
            int categoryId = Integer.parseInt(request.getParameter("categoryId"));
            String questionText = request.getParameter("questionText");
            String questionImage = request.getParameter("questionImage");
            String explanation = request.getParameter("explanation");
            boolean isCritical = "on".equals(request.getParameter("isCritical"));
            
            success = questionDAO.updateQuestion(questionId, categoryId, questionText, questionImage, explanation, isCritical);
            
            if (success) {
                answerDAO.deleteAnswersByQuestionId(questionId);
                String correctAnswer = request.getParameter("correctAnswer");
                int correctAnswerIndex = 0;
                if (correctAnswer != null && !correctAnswer.isEmpty()) {
                    try {
                        correctAnswerIndex = Integer.parseInt(correctAnswer);
                    } catch (NumberFormatException e) {
                        correctAnswerIndex = 0;
                    }
                }
                for (int i = 1; i <= 4; i++) {
                    String answerText = request.getParameter("answer" + i);
                    boolean isCorrect = (i == correctAnswerIndex);
                    if (answerText != null && !answerText.trim().isEmpty()) {
                        answerDAO.addAnswer(questionId, answerText.trim(), isCorrect, i);
                    }
                }
                message = "Cập nhật câu hỏi thành công!";
            } else {
                message = "Cập nhật câu hỏi thất bại!";
            }
        }

        if (success) {
            request.setAttribute("success", message);
        } else {
            request.setAttribute("error", message);
        }

        doGet(request, response);
    }
    
    private int getLastInsertedQuestionId(QuestionDAO questionDAO) {
        String sql = "SELECT MAX(question_id) AS last_id FROM Questions";
        try {
            DBContext dbContext = new DBContext();
            Connection conn = dbContext.getConnection();
            if (conn == null) {
                return 0;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("last_id");
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error getting last question id", ex);
        }
        return 0;
    }
}

