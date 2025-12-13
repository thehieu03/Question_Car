<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@page import="model.User"%>
<%@page import="model.ExamSet"%>
<%@page import="model.UserExam"%>
<%@page import="model.Question"%>
<%@page import="model.Answer"%>
<%@page import="model.UserAnswer"%>
<%@page import="model.ExamSetComment"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Kết quả bài thi</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
        <style>
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(180deg, #eef2ff 0%, #f8f9fc 100%); color: #2f2f2f; }
            .navbar { background: linear-gradient(90deg, #667eea 0%, #764ba2 100%); color: white; padding: 16px 22px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 4px 16px rgba(0,0,0,0.12); }
            .container { max-width: 900px; margin: 24px auto 32px; padding: 0 16px; }
            .result-card { background: white; border-radius: 12px; padding: 32px; box-shadow: 0 6px 20px rgba(0,0,0,0.08); margin-bottom: 20px; text-align: center; }
            .result-card.passed { border-top: 4px solid #4caf50; }
            .result-card.failed { border-top: 4px solid #e53935; }
            .result-icon { font-size: 64px; margin-bottom: 16px; }
            .result-card.passed .result-icon { color: #4caf50; }
            .result-card.failed .result-icon { color: #e53935; }
            .result-title { font-size: 24px; font-weight: 700; margin-bottom: 12px; }
            .result-score { font-size: 48px; font-weight: 700; color: #667eea; margin: 16px 0; }
            .result-details { display: flex; justify-content: center; gap: 32px; margin-top: 24px; flex-wrap: wrap; }
            .result-detail-item { text-align: center; }
            .result-detail-value { font-size: 28px; font-weight: 700; color: #667eea; }
            .result-detail-label { font-size: 14px; color: #6b7280; margin-top: 4px; }
            .question-container { background: white; border-radius: 12px; padding: 24px; box-shadow: 0 6px 20px rgba(0,0,0,0.08); margin-bottom: 20px; }
            .question-number { color: #667eea; font-weight: 700; font-size: 14px; margin-bottom: 12px; }
            .question-text { font-size: 16px; line-height: 1.6; margin-bottom: 16px; color: #2f2f2f; }
            .question-image { max-width: 100%; border-radius: 8px; margin: 16px 0; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
            .answer-option { display: flex; align-items: flex-start; padding: 14px; margin-bottom: 12px; border: 2px solid #e5e7eb; border-radius: 8px; background: white; }
            .answer-option.correct { border-color: #4caf50; background: #e8f5e9; }
            .answer-option.incorrect { border-color: #e53935; background: #ffebee; }
            .answer-option.user-selected { border-width: 3px; }
            .answer-label { font-weight: 600; color: #667eea; margin-right: 10px; min-width: 24px; }
            .answer-text { flex: 1; line-height: 1.5; }
            .answer-status { margin-left: 8px; font-size: 18px; }
            .answer-status.correct { color: #4caf50; }
            .answer-status.incorrect { color: #e53935; }
            .explanation { background: #f7f8ff; border-left: 4px solid #667eea; padding: 12px 16px; border-radius: 4px; margin-top: 12px; font-size: 14px; color: #4b4f5c; }
            .actions { text-align: center; margin-top: 32px; }
            .btn { padding: 12px 24px; border: none; border-radius: 8px; cursor: pointer; text-decoration: none; font-weight: 600; font-size: 14px; display: inline-block; margin: 0 8px; transition: all 0.2s; }
            .btn-primary { background: #667eea; color: white; box-shadow: 0 6px 16px rgba(102,126,234,0.35); }
            .btn-primary:hover { background: #5568d3; transform: translateY(-1px); }
            .btn-secondary { background: #9aa0b1; color: white; }
            .btn-secondary:hover { background: #7a7f8f; }
            .error-message { background: #fee; color: #c62828; padding: 16px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #ffcdd2; }
            .comment-section { background: white; border-radius: 12px; padding: 24px; box-shadow: 0 6px 20px rgba(0,0,0,0.08); margin-top: 20px; }
            .comment-title { font-size: 18px; font-weight: 700; color: #4b4f5c; margin-bottom: 16px; }
            .comment-form textarea { width: 100%; min-height: 100px; padding: 12px; border: 2px solid #e5e7eb; border-radius: 8px; font-family: inherit; font-size: 14px; resize: vertical; }
            .comment-form textarea:focus { outline: none; border-color: #667eea; }
            .comment-form .btn-submit { margin-top: 12px; }
            .comment-display { background: #f7f8ff; border-left: 4px solid #667eea; padding: 16px; border-radius: 8px; margin-top: 16px; }
            .comment-display .comment-text { color: #4b4f5c; line-height: 1.6; margin-bottom: 8px; }
            .comment-display .comment-date { color: #6b7280; font-size: 12px; }
        </style>
    </head>
    <body>
        <%
            User user = (User) session.getAttribute("user");
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            ExamSet examSet = (ExamSet) request.getAttribute("examSet");
            UserExam userExam = (UserExam) request.getAttribute("userExam");
            List<Question> questions = (List<Question>) request.getAttribute("questions");
            Map<Integer, List<Answer>> questionAnswersMap = (Map<Integer, List<Answer>>) request.getAttribute("questionAnswersMap");
            Map<Integer, UserAnswer> userAnswersMap = (Map<Integer, UserAnswer>) request.getAttribute("userAnswersMap");
            Map<Integer, Answer> correctAnswersMap = (Map<Integer, Answer>) request.getAttribute("correctAnswersMap");
            ExamSetComment userComment = (ExamSetComment) request.getAttribute("userComment");
            String error = (String) request.getAttribute("error");
        %>

        <div class="navbar">
            <div style="font-weight:700;">Kết quả bài thi</div>
            <div>Xin chào, <%= user.getUsername() %></div>
        </div>

        <div class="container">
            <% if (error != null) { %>
                <div class="error-message">
                    <strong>Lỗi:</strong> <%= error %>
                </div>
                <div class="actions">
                    <a href="<%= request.getContextPath() %>/user/exams" class="btn btn-secondary">Quay lại</a>
                </div>
            <% } else if (userExam != null && examSet != null && questions != null) { %>
                <div class="result-card <%= userExam.getIsPassed() != null && userExam.getIsPassed() ? "passed" : "failed" %>">
                    <div class="result-icon">
                        <i class="fas <%= userExam.getIsPassed() != null && userExam.getIsPassed() ? "fa-check-circle" : "fa-times-circle" %>"></i>
                    </div>
                    <div class="result-title">
                        <%= userExam.getIsPassed() != null && userExam.getIsPassed() ? "Chúc mừng! Bạn đã đậu!" : "Rất tiếc! Bạn chưa đạt yêu cầu" %>
                    </div>
                    <div class="result-score">
                        <%= userExam.getTotalScore() != null ? userExam.getTotalScore() : 0 %>/<%= examSet.getTotalQuestions() %>
                    </div>
                    <div class="result-details">
                        <div class="result-detail-item">
                            <div class="result-detail-value" style="color:#4caf50;"><%= userExam.getCorrectAnswers() != null ? userExam.getCorrectAnswers() : 0 %></div>
                            <div class="result-detail-label">Câu đúng</div>
                        </div>
                        <div class="result-detail-item">
                            <div class="result-detail-value" style="color:#e53935;"><%= userExam.getWrongAnswers() != null ? userExam.getWrongAnswers() : 0 %></div>
                            <div class="result-detail-label">Câu sai</div>
                        </div>
                        <div class="result-detail-item">
                            <div class="result-detail-value" style="color:#9aa0b1;"><%= examSet.getTotalQuestions() - (userExam.getCorrectAnswers() != null ? userExam.getCorrectAnswers() : 0) - (userExam.getWrongAnswers() != null ? userExam.getWrongAnswers() : 0) %></div>
                            <div class="result-detail-label">Câu bỏ qua</div>
                        </div>
                    </div>
                </div>

                <h2 style="color:#4b4f5c; margin-bottom: 16px;">Chi tiết bài làm</h2>

                <% for (int i = 0; i < questions.size(); i++) { 
                    Question question = questions.get(i);
                    List<Answer> answers = questionAnswersMap.get(question.getQuestionId());
                    UserAnswer userAnswer = userAnswersMap != null ? userAnswersMap.get(question.getQuestionId()) : null;
                    Answer correctAnswer = correctAnswersMap.get(question.getQuestionId());
                    boolean isCorrect = userAnswer != null && userAnswer.getIsCorrect() != null && userAnswer.getIsCorrect();
                    boolean isWrong = userAnswer != null && userAnswer.getIsCorrect() != null && !userAnswer.getIsCorrect();
                %>
                    <div class="question-container">
                        <div class="question-number">
                            Câu <%= i + 1 %>/<%= questions.size() %>
                            <% if (isCorrect) { %>
                                <span style="color:#4caf50; margin-left: 8px;"><i class="fas fa-check"></i> Đúng</span>
                            <% } else if (isWrong) { %>
                                <span style="color:#e53935; margin-left: 8px;"><i class="fas fa-times"></i> Sai</span>
                            <% } else { %>
                                <span style="color:#9aa0b1; margin-left: 8px;"><i class="fas fa-minus"></i> Bỏ qua</span>
                            <% } %>
                            <% if (question.isCritical()) { %>
                                <span style="color:#e53935; margin-left: 8px;">(Điểm liệt)</span>
                            <% } %>
                        </div>
                        <div class="question-text"><%= question.getQuestionText() %></div>
                        <% if (question.getQuestionImage() != null && !question.getQuestionImage().trim().isEmpty()) { %>
                            <img src="<%= question.getQuestionImage() %>" alt="Hình minh họa" class="question-image" onerror="this.style.display='none';">
                        <% } %>
                        
                        <% if (answers != null && !answers.isEmpty()) { %>
                            <% for (Answer answer : answers) { 
                                boolean isUserSelected = userAnswer != null && userAnswer.getAnswerId() != null && userAnswer.getAnswerId() == answer.getAnswerId();
                                boolean isCorrectAnswer = answer.isCorrect();
                                String optionClass = "";
                                if (isCorrectAnswer) {
                                    optionClass = "correct";
                                } else if (isUserSelected && !isCorrectAnswer) {
                                    optionClass = "incorrect";
                                }
                                if (isUserSelected) {
                                    optionClass += " user-selected";
                                }
                            %>
                                <div class="answer-option <%= optionClass %>">
                                    <span class="answer-label"><%= answer.getAnswerLabel() %>.</span>
                                    <span class="answer-text"><%= answer.getAnswerText() %></span>
                                    <% if (isCorrectAnswer) { %>
                                        <span class="answer-status correct"><i class="fas fa-check-circle"></i></span>
                                    <% } else if (isUserSelected) { %>
                                        <span class="answer-status incorrect"><i class="fas fa-times-circle"></i></span>
                                    <% } %>
                                </div>
                            <% } %>
                        <% } %>

                        <% if (question.getExplanation() != null && !question.getExplanation().trim().isEmpty()) { %>
                            <div class="explanation">
                                <strong>Giải thích:</strong> <%= question.getExplanation() %>
                            </div>
                        <% } %>
                    </div>
                <% } %>

                <!-- Comment Section -->
                <div class="comment-section">
                    <div class="comment-title">
                        <i class="fas fa-comment"></i> Bình luận về đề thi này
                    </div>
                    <% if (userComment != null && userComment.getContent() != null) { %>
                        <div class="comment-display">
                            <div class="comment-text"><%= userComment.getContent() %></div>
                            <div class="comment-date">
                                Đã bình luận: <%= new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm").format(userComment.getCreatedAt()) %>
                            </div>
                        </div>
                    <% } else { %>
                        <form class="comment-form" method="POST" action="<%= request.getContextPath() %>/user/comment">
                            <input type="hidden" name="examSetId" value="<%= examSet.getExamSetId() %>">
                            <input type="hidden" name="userExamId" value="<%= userExam.getUserExamId() %>">
                            <textarea name="content" placeholder="Chia sẻ cảm nhận của bạn về đề thi này (tùy chọn)..." maxlength="1000"></textarea>
                            <button type="submit" class="btn btn-primary btn-submit">
                                <i class="fas fa-paper-plane"></i> Gửi bình luận
                            </button>
                        </form>
                    <% } %>
                </div>

                <div class="actions">
                    <a href="<%= request.getContextPath() %>/user/exams" class="btn btn-primary">Làm đề khác</a>
                    <a href="<%= request.getContextPath() %>/user/history" class="btn btn-secondary">Xem lịch sử</a>
                </div>
            <% } else { %>
                <div class="error-message">
                    Không có dữ liệu kết quả.
                </div>
                <div class="actions">
                    <a href="<%= request.getContextPath() %>/user/exams" class="btn btn-secondary">Quay lại</a>
                </div>
            <% } %>
        </div>
    </body>
</html>

