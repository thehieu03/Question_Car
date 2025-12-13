package serverlet.user;

import dao.AnswerDAO;
import dao.ExamSetDAO;
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
import model.Question;
import model.User;
import model.UserExam;

public class ExamStartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String examSetIdParam = request.getParameter("examSetId");
        int examSetId;
        try {
            examSetId = Integer.parseInt(examSetIdParam);
        } catch (Exception e) {
            request.setAttribute("error", "Đề thi không hợp lệ.");
            request.getRequestDispatcher("/user/exam-start.jsp").forward(request, response);
            return;
        }

        ExamSetDAO examSetDAO = new ExamSetDAO();
        ExamSet examSet = examSetDAO.getExamSetById(examSetId);
        if (examSet == null) {
            request.setAttribute("error", "Không tìm thấy đề thi.");
            request.getRequestDispatcher("/user/exam-start.jsp").forward(request, response);
            return;
        }

        // Kiểm tra xem có bài thi đang làm dở không
        UserExamDAO userExamDAO = new UserExamDAO();
        UserExam inProgressExam = userExamDAO.getInProgressExam(user.getUserId(), examSetId);

        int userExamId;
        if (inProgressExam != null) {
            // Tiếp tục bài thi đang làm dở
            userExamId = inProgressExam.getUserExamId();
        } else {
            // Tạo bài thi mới
            userExamId = userExamDAO.startExam(user.getUserId(), examSetId);
            if (userExamId == 0) {
                request.setAttribute("error", "Không thể bắt đầu bài thi. Vui lòng thử lại.");
                request.getRequestDispatcher("/user/exam-start.jsp").forward(request, response);
                return;
            }
        }

        // Lấy danh sách câu hỏi của đề thi
        List<Question> questions = examSetDAO.getQuestionsByExamSet(examSetId);
        if (questions == null || questions.isEmpty()) {
            request.setAttribute("error", "Đề thi này chưa có câu hỏi.");
            request.getRequestDispatcher("/user/exam-start.jsp").forward(request, response);
            return;
        }

        // Lấy đáp án cho từng câu hỏi
        AnswerDAO answerDAO = new AnswerDAO();
        Map<Integer, List<Answer>> questionAnswersMap = new HashMap<>();
        for (Question question : questions) {
            List<Answer> answers = answerDAO.getAnswersByQuestionId(question.getQuestionId());
            questionAnswersMap.put(question.getQuestionId(), answers);
        }

        request.setAttribute("examSet", examSet);
        request.setAttribute("questions", questions);
        request.setAttribute("questionAnswersMap", questionAnswersMap);
        request.setAttribute("userExamId", userExamId);
        request.setAttribute("durationMinutes", examSet.getDurationMinutes());

        request.getRequestDispatcher("/user/exam-start.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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
            request.getRequestDispatcher("/user/exam-start.jsp").forward(request, response);
            return;
        }

        UserExamDAO userExamDAO = new UserExamDAO();
        UserExam userExam = userExamDAO.getUserExamById(userExamId);
        if (userExam == null || userExam.getUserId() != user.getUserId()) {
            request.setAttribute("error", "Không tìm thấy bài thi.");
            request.getRequestDispatcher("/user/exam-start.jsp").forward(request, response);
            return;
        }

        if (!"IN_PROGRESS".equals(userExam.getStatus())) {
            request.setAttribute("error", "Bài thi này đã được nộp.");
            request.getRequestDispatcher("/user/exam-start.jsp").forward(request, response);
            return;
        }

        ExamSetDAO examSetDAO = new ExamSetDAO();
        ExamSet examSet = examSetDAO.getExamSetById(userExam.getExamSetId());
        if (examSet == null) {
            request.setAttribute("error", "Không tìm thấy đề thi.");
            request.getRequestDispatcher("/user/exam-start.jsp").forward(request, response);
            return;
        }

        // Lấy danh sách câu hỏi
        List<Question> questions = examSetDAO.getQuestionsByExamSet(examSet.getExamSetId());
        AnswerDAO answerDAO = new AnswerDAO();

        int correctAnswers = 0;
        int wrongAnswers = 0;
        int totalQuestions = questions.size();

        // Xử lý từng câu trả lời
        for (Question question : questions) {
            String answerIdParam = request.getParameter("answer_" + question.getQuestionId());
            Integer answerId = null;
            if (answerIdParam != null && !answerIdParam.trim().isEmpty()) {
                try {
                    answerId = Integer.parseInt(answerIdParam);
                } catch (NumberFormatException e) {
                    // Bỏ qua câu không trả lời
                }
            }

            // Kiểm tra đáp án đúng
            Boolean isCorrect = null;
            if (answerId != null) {
                List<Answer> answers = answerDAO.getAnswersByQuestionId(question.getQuestionId());
                for (Answer answer : answers) {
                    if (answer.getAnswerId() == answerId) {
                        isCorrect = answer.isCorrect();
                        break;
                    }
                }
            }

            // Lưu câu trả lời
            userExamDAO.saveUserAnswer(userExamId, question.getQuestionId(), answerId, isCorrect);

            // Đếm số câu đúng/sai
            if (isCorrect != null) {
                if (isCorrect) {
                    correctAnswers++;
                } else {
                    wrongAnswers++;
                }
            }
        }

        // Tính điểm (số câu đúng)
        int totalScore = correctAnswers;
        boolean isPassed = totalScore >= examSet.getPassingScore();

        // Submit bài thi
        boolean submitted = userExamDAO.submitExam(userExamId, totalScore, correctAnswers, wrongAnswers, isPassed);

        if (submitted) {
            // Redirect đến trang kết quả
            response.sendRedirect(request.getContextPath() + "/user/exam/result?userExamId=" + userExamId);
        } else {
            request.setAttribute("error", "Không thể nộp bài thi. Vui lòng thử lại.");
            request.getRequestDispatcher("/user/exam-start.jsp").forward(request, response);
        }
    }
}
