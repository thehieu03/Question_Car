package serverlet.user;

import dao.AnswerDAO;
import dao.ExamSetDAO;
import dao.ExamSetCommentDAO;
import dao.QuestionDAO;
import dao.UserExamDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Answer;
import model.ExamSet;
import model.ExamSetComment;
import model.Question;
import model.User;
import model.UserAnswer;
import model.UserExam;

public class ExamResultServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String userExamIdParam = request.getParameter("userExamId");
        int userExamId;
        try {
            userExamId = Integer.parseInt(userExamIdParam);
        } catch (Exception e) {
            request.setAttribute("error", "Bài thi không hợp lệ.");
            request.getRequestDispatcher("/user/exam-result.jsp").forward(request, response);
            return;
        }

        UserExamDAO userExamDAO = new UserExamDAO();
        UserExam userExam = userExamDAO.getUserExamById(userExamId);
        if (userExam == null || userExam.getUserId() != user.getUserId()) {
            request.setAttribute("error", "Không tìm thấy bài thi.");
            request.getRequestDispatcher("/user/exam-result.jsp").forward(request, response);
            return;
        }

        ExamSetDAO examSetDAO = new ExamSetDAO();
        ExamSet examSet = examSetDAO.getExamSetById(userExam.getExamSetId());
        if (examSet == null) {
            request.setAttribute("error", "Không tìm thấy đề thi.");
            request.getRequestDispatcher("/user/exam-result.jsp").forward(request, response);
            return;
        }

        // Lấy danh sách câu hỏi
        List<Question> questions = examSetDAO.getQuestionsByExamSet(examSet.getExamSetId());
        AnswerDAO answerDAO = new AnswerDAO();
        QuestionDAO questionDAO = new QuestionDAO();

        // Lấy câu trả lời của user
        Map<Integer, UserAnswer> userAnswersMap = getUserAnswers(userExamId, questions);

        // Lấy đáp án cho từng câu hỏi
        Map<Integer, List<Answer>> questionAnswersMap = new HashMap<>();
        Map<Integer, Answer> correctAnswersMap = new HashMap<>();
        for (Question question : questions) {
            List<Answer> answers = answerDAO.getAnswersByQuestionId(question.getQuestionId());
            questionAnswersMap.put(question.getQuestionId(), answers);

            // Tìm đáp án đúng
            for (Answer answer : answers) {
                if (answer.isCorrect()) {
                    correctAnswersMap.put(question.getQuestionId(), answer);
                    break;
                }
            }
        }

        request.setAttribute("examSet", examSet);
        request.setAttribute("userExam", userExam);
        request.setAttribute("questions", questions);
        request.setAttribute("questionAnswersMap", questionAnswersMap);
        request.setAttribute("userAnswersMap", userAnswersMap);
        request.setAttribute("correctAnswersMap", correctAnswersMap);

        // Get user's comment for this exam set (if any)
        ExamSetCommentDAO commentDAO = new ExamSetCommentDAO();
        ExamSetComment userComment = commentDAO.getCommentByUserAndExamSet(user.getUserId(), examSet.getExamSetId());
        request.setAttribute("userComment", userComment);

        request.getRequestDispatcher("/user/exam-result.jsp").forward(request, response);
    }

    private Map<Integer, UserAnswer> getUserAnswers(int userExamId, List<Question> questions) {
        Map<Integer, UserAnswer> userAnswersMap = new HashMap<>();
        String sql = "SELECT * FROM UserAnswers WHERE user_exam_id = ?";
        UserExamDAO userExamDAO = new UserExamDAO();
        try {
            java.sql.Connection conn = userExamDAO.getConnection();
            if (conn != null) {
                java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, userExamId);
                java.sql.ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    UserAnswer userAnswer = new UserAnswer();
                    userAnswer.setUserAnswerId(rs.getInt("user_answer_id"));
                    userAnswer.setUserExamId(rs.getInt("user_exam_id"));
                    userAnswer.setQuestionId(rs.getInt("question_id"));
                    userAnswer.setAnswerId((Integer) rs.getObject("answer_id"));
                    userAnswer.setIsCorrect((Boolean) rs.getObject("is_correct"));
                    userAnswersMap.put(userAnswer.getQuestionId(), userAnswer);
                }
                rs.close();
                ps.close();
            }
        } catch (java.sql.SQLException ex) {
        }
        return userAnswersMap;
    }
}
