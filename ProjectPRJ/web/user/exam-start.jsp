<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@page import="model.User"%>
<%@page import="model.ExamSet"%>
<%@page import="model.Question"%>
<%@page import="model.Answer"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Làm bài thi</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
        <style>
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(180deg, #eef2ff 0%, #f8f9fc 100%); color: #2f2f2f; }
            .navbar { background: linear-gradient(90deg, #667eea 0%, #764ba2 100%); color: white; padding: 16px 22px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 4px 16px rgba(0,0,0,0.12); position: sticky; top: 0; z-index: 100; }
            .navbar .timer { background: rgba(255,255,255,0.2); padding: 8px 16px; border-radius: 8px; font-weight: 700; font-size: 18px; min-width: 120px; text-align: center; }
            .navbar .timer.warning { background: rgba(255,87,34,0.9); animation: pulse 1s infinite; }
            @keyframes pulse { 0%, 100% { opacity: 1; } 50% { opacity: 0.7; } }
            .container { max-width: 900px; margin: 24px auto 32px; padding: 0 16px; }
            .exam-header { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 6px 20px rgba(0,0,0,0.08); margin-bottom: 20px; }
            .exam-header h1 { color: #4b4f5c; margin-bottom: 10px; }
            .exam-header .meta { color: #6b7280; font-size: 14px; }
            .exam-header .meta strong { color: #667eea; }
            .question-container { background: white; border-radius: 12px; padding: 24px; box-shadow: 0 6px 20px rgba(0,0,0,0.08); margin-bottom: 20px; }
            .question-number { color: #667eea; font-weight: 700; font-size: 14px; margin-bottom: 12px; }
            .question-text { font-size: 16px; line-height: 1.6; margin-bottom: 16px; color: #2f2f2f; }
            .question-image { max-width: 100%; border-radius: 8px; margin: 16px 0; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }
            .answer-option { display: flex; align-items: flex-start; padding: 14px; margin-bottom: 12px; border: 2px solid #e5e7eb; border-radius: 8px; cursor: pointer; transition: all 0.2s; background: white; }
            .answer-option:hover { border-color: #667eea; background: #f7f8ff; }
            .answer-option input[type="radio"] { margin-right: 12px; margin-top: 2px; cursor: pointer; width: 18px; height: 18px; }
            .answer-option.selected { border-color: #667eea; background: #f7f8ff; }
            .answer-label { font-weight: 600; color: #667eea; margin-right: 10px; min-width: 24px; }
            .answer-text { flex: 1; line-height: 1.5; }
            .actions { display: flex; justify-content: space-between; align-items: center; margin-top: 24px; padding-top: 20px; border-top: 1px solid #e5e7eb; }
            .btn { padding: 12px 24px; border: none; border-radius: 8px; cursor: pointer; text-decoration: none; font-weight: 600; font-size: 14px; transition: all 0.2s; }
            .btn-primary { background: #667eea; color: white; box-shadow: 0 6px 16px rgba(102,126,234,0.35); }
            .btn-primary:hover { background: #5568d3; transform: translateY(-1px); }
            .btn-secondary { background: #9aa0b1; color: white; }
            .btn-secondary:hover { background: #7a7f8f; }
            .btn-danger { background: #e53935; color: white; }
            .btn-danger:hover { background: #c62828; }
            .progress-info { color: #6b7280; font-size: 14px; }
            .error-message { background: #fee; color: #c62828; padding: 16px; border-radius: 8px; margin-bottom: 20px; border: 1px solid #ffcdd2; }
            .question-wrapper { 
                display: none; 
            }
            .question-wrapper.active { 
                display: block !important; 
            }
            .navigation-buttons { display: flex; justify-content: space-between; align-items: center; margin-top: 24px; padding-top: 20px; border-top: 1px solid #e5e7eb; }
            .nav-btn-group { display: flex; gap: 10px; }
            .question-nav-panel { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 6px 20px rgba(0,0,0,0.08); margin-bottom: 20px; }
            .question-nav-title { font-weight: 700; color: #4b4f5c; margin-bottom: 16px; }
            .question-nav-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(45px, 1fr)); gap: 8px; }
            .question-nav-item { 
                width: 45px; 
                height: 45px; 
                border: 2px solid #e5e7eb; 
                border-radius: 8px; 
                display: flex; 
                align-items: center; 
                justify-content: center; 
                cursor: pointer; 
                font-weight: 600; 
                transition: all 0.2s; 
                background: white; 
                color: #2f2f2f;
            }
            .question-nav-item:hover { 
                border-color: #667eea; 
                transform: translateY(-2px); 
                box-shadow: 0 2px 8px rgba(102,126,234,0.2);
            }
            .question-nav-item.current { 
                border-color: #667eea; 
                background: #667eea; 
                color: white; 
                box-shadow: 0 4px 12px rgba(102,126,234,0.4);
            }
            .question-nav-item.answered { 
                background: #e8f5e9; 
                border-color: #4caf50; 
                color: #2e7d32;
                font-weight: 700;
            }
            .question-nav-item.answered:hover {
                border-color: #4caf50;
                background: #c8e6c9;
                box-shadow: 0 2px 8px rgba(76,175,80,0.3);
            }
            .question-nav-item.answered.current { 
                background: #667eea; 
                color: white; 
                border-color: #667eea;
                box-shadow: 0 4px 12px rgba(102,126,234,0.4);
            }
            .question-nav-item { 
                position: relative;
            }
            .question-nav-item.answered.current::after {
                content: '✓';
                position: absolute;
                top: -5px;
                right: -5px;
                background: #4caf50;
                color: white;
                width: 18px;
                height: 18px;
                border-radius: 50%;
                font-size: 11px;
                display: flex;
                align-items: center;
                justify-content: center;
                border: 2px solid white;
                font-weight: bold;
            }
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
            List<Question> questions = (List<Question>) request.getAttribute("questions");
            Map<Integer, List<Answer>> questionAnswersMap = (Map<Integer, List<Answer>>) request.getAttribute("questionAnswersMap");
            Integer userExamId = (Integer) request.getAttribute("userExamId");
            Integer durationMinutes = (Integer) request.getAttribute("durationMinutes");
            String error = (String) request.getAttribute("error");
        %>

        <div class="navbar">
            <div style="font-weight:700;"><%= examSet != null ? examSet.getExamName() : "Làm bài thi" %></div>
            <div style="display:flex; align-items:center; gap:20px;">
                <div class="timer" id="timer">00:00</div>
                <span>Xin chào, <%= user.getUsername() %></span>
            </div>
        </div>

        <div class="container">
            <% if (error != null) { %>
                <div class="error-message">
                    <strong>Lỗi:</strong> <%= error %>
                </div>
                <div style="text-align: center;">
                    <a href="<%= request.getContextPath() %>/user/exams" class="btn btn-secondary">Quay lại</a>
                </div>
            <% } else if (examSet != null && questions != null && !questions.isEmpty()) { %>
                <div class="exam-header">
                    <h1><%= examSet.getExamName() %></h1>
                    <div class="meta">
                        Số câu: <strong><%= examSet.getTotalQuestions() %></strong> |
                        Thời gian: <strong><%= examSet.getDurationMinutes() %> phút</strong> |
                        Điểm đạt: <strong><%= examSet.getPassingScore() %>/<%= examSet.getTotalQuestions() %></strong>
                    </div>
                </div>

                <!-- Navigation Panel -->
                <div class="question-nav-panel">
                    <div class="question-nav-title">Danh sách câu hỏi</div>
                    <div class="question-nav-grid" id="questionNavGrid">
                        <% for (int i = 0; i < questions.size(); i++) { %>
                            <div class="question-nav-item" data-question-index="<%= i %>" onclick="goToQuestion(<%= i %>); return false;">
                                <%= i + 1 %>
                            </div>
                        <% } %>
                    </div>
                </div>

                <form id="examForm" method="POST" action="<%= request.getContextPath() %>/user/exam/start">
                    <input type="hidden" name="userExamId" value="<%= userExamId %>">
                    
                    <% for (int i = 0; i < questions.size(); i++) { 
                        Question question = questions.get(i);
                        List<Answer> answers = questionAnswersMap.get(question.getQuestionId());
                    %>
                        <div class="question-wrapper <%= i == 0 ? "active" : "" %>" data-question-index="<%= i %>" id="question-<%= i %>">
                            <div class="question-container">
                                <div class="question-number">Câu <%= i + 1 %>/<%= questions.size() %><% if (question.isCritical()) { %> <span style="color:#e53935;">(Điểm liệt)</span><% } %></div>
                                <div class="question-text"><%= question.getQuestionText() %></div>
                                <% if (question.getQuestionImage() != null && !question.getQuestionImage().trim().isEmpty()) { %>
                                    <img src="<%= question.getQuestionImage() %>" alt="Hình minh họa" class="question-image" onerror="this.style.display='none';">
                                <% } %>
                                
                                <% if (answers != null && !answers.isEmpty()) { %>
                                    <% for (Answer answer : answers) { %>
                                        <label class="answer-option" onclick="selectAnswer(this)">
                                            <input type="radio" name="answer_<%= question.getQuestionId() %>" value="<%= answer.getAnswerId() %>" onchange="updateSelected(this)">
                                            <span class="answer-label"><%= answer.getAnswerLabel() %>.</span>
                                            <span class="answer-text"><%= answer.getAnswerText() %></span>
                                        </label>
                                    <% } %>
                                <% } else { %>
                                    <p style="color:#9aa0b1;">Chưa có đáp án cho câu hỏi này.</p>
                                <% } %>
                            </div>
                        </div>
                    <% } %>

                    <div class="navigation-buttons">
                        <div class="progress-info">
                            Đã trả lời: <strong id="answeredCount">0</strong>/<%= questions.size() %> | 
                            Câu hiện tại: <strong id="currentQuestionNumber">1</strong>/<%= questions.size() %>
                        </div>
                        <div class="nav-btn-group">
                            <button type="button" class="btn btn-secondary" id="prevBtn" onclick="previousQuestion()" disabled>
                                <i class="fas fa-chevron-left"></i> Câu trước
                            </button>
                            <button type="button" class="btn btn-primary" id="nextBtn" onclick="nextQuestion()" <%= questions.size() == 1 ? "style='display:none;'" : "" %>>
                                Câu sau <i class="fas fa-chevron-right"></i>
                            </button>
                            <button type="submit" class="btn btn-danger" id="submitBtn" onclick="return confirm('Bạn có chắc muốn nộp bài?')" style="display:none;">
                                <i class="fas fa-check"></i> Nộp bài
                            </button>
                        </div>
                    </div>

                    <div class="actions" style="justify-content: center; margin-top: 16px;">
                        <a href="<%= request.getContextPath() %>/user/exams" class="btn btn-secondary" onclick="return confirm('Bạn có chắc muốn rời khỏi bài thi? Tiến trình sẽ được lưu.')">
                            <i class="fas fa-sign-out-alt"></i> Thoát
                        </a>
                    </div>
                </form>
            <% } else { %>
                <div class="error-message">
                    Không có dữ liệu bài thi.
                </div>
                <div style="text-align: center;">
                    <a href="<%= request.getContextPath() %>/user/exams" class="btn btn-secondary">Quay lại</a>
                </div>
            <% } %>
        </div>

        <script>
            // Global variables
            const totalQuestions = <%= questions != null ? questions.size() : 0 %>;
            let currentQuestionIndex = 0;
            let totalSeconds = <%= durationMinutes != null ? durationMinutes * 60 : 0 %>;
            let timerInterval = null;
            let formSubmitted = false;

            // Timer
            const timerElement = document.getElementById('timer');
            
            function updateTimer() {
                if (totalSeconds <= 0) {
                    timerElement.textContent = '00:00';
                    timerElement.classList.add('warning');
                    if (timerInterval) {
                        clearInterval(timerInterval);
                    }
                    // Tự động nộp bài khi hết giờ
                    alert('Hết thời gian! Hệ thống sẽ tự động nộp bài của bạn.');
                    formSubmitted = true;
                    document.getElementById('examForm').submit();
                    return;
                }
                
                const hours = Math.floor(totalSeconds / 3600);
                const minutes = Math.floor((totalSeconds % 3600) / 60);
                const seconds = totalSeconds % 60;
                
                if (hours > 0) {
                    timerElement.textContent = String(hours).padStart(2, '0') + ':' + 
                                               String(minutes).padStart(2, '0') + ':' + 
                                               String(seconds).padStart(2, '0');
                } else {
                    timerElement.textContent = String(minutes).padStart(2, '0') + ':' + String(seconds).padStart(2, '0');
                }
                
                if (totalSeconds <= 300) { // 5 phút cuối
                    timerElement.classList.add('warning');
                }
                
                totalSeconds--;
            }
            
            // Start timer
            if (totalSeconds > 0) {
                updateTimer();
                timerInterval = setInterval(updateTimer, 1000);
            }

            // Navigation functions
            function goToQuestion(index) {
                try {
                    console.log('goToQuestion called with index:', index, 'totalQuestions:', totalQuestions);
                    
                    if (index < 0 || index >= totalQuestions) {
                        console.log('Invalid index');
                        return false;
                    }
                    
                    // Hide all questions
                    const allWrappers = document.querySelectorAll('.question-wrapper');
                    console.log('Total wrappers found:', allWrappers.length);
                    allWrappers.forEach(wrapper => {
                        wrapper.classList.remove('active');
                    });
                    
                    // Show selected question - try multiple methods
                    let questionWrapper = null;
                    
                    // Method 1: Using data attribute
                    const selector = `.question-wrapper[data-question-index="${index}"]`;
                    questionWrapper = document.querySelector(selector);
                    
                    // Method 2: Using ID
                    if (!questionWrapper) {
                        questionWrapper = document.getElementById(`question-${index}`);
                    }
                    
                    // Method 3: Using array index
                    if (!questionWrapper) {
                        const allWrappers = document.querySelectorAll('.question-wrapper');
                        if (index < allWrappers.length) {
                            questionWrapper = allWrappers[index];
                        }
                    }
                    
                    if (questionWrapper) {
                        questionWrapper.classList.add('active');
                        currentQuestionIndex = index;
                        updateNavigation();
                        updateQuestionNavPanel();
                        scrollToTop();
                        console.log('Successfully switched to question', index);
                        return true;
                    } else {
                        console.error('Question wrapper not found for index:', index);
                        alert('Không tìm thấy câu hỏi số ' + (index + 1));
                    }
                } catch (error) {
                    console.error('Error in goToQuestion:', error);
                }
                return false;
            }

            function nextQuestion() {
                if (currentQuestionIndex < totalQuestions - 1) {
                    goToQuestion(currentQuestionIndex + 1);
                }
            }

            function previousQuestion() {
                if (currentQuestionIndex > 0) {
                    goToQuestion(currentQuestionIndex - 1);
                }
            }

            function updateNavigation() {
                const prevBtn = document.getElementById('prevBtn');
                const nextBtn = document.getElementById('nextBtn');
                const submitBtn = document.getElementById('submitBtn');
                const currentQuestionNumber = document.getElementById('currentQuestionNumber');
                
                currentQuestionNumber.textContent = currentQuestionIndex + 1;
                
                // Update buttons
                prevBtn.disabled = currentQuestionIndex === 0;
                
                if (currentQuestionIndex === totalQuestions - 1) {
                    nextBtn.style.display = 'none';
                    submitBtn.style.display = 'inline-block';
                } else {
                    nextBtn.style.display = 'inline-block';
                    submitBtn.style.display = 'none';
                }
            }

            function updateQuestionNavPanel() {
                document.querySelectorAll('.question-nav-item').forEach((item, index) => {
                    // Remove all state classes first
                    item.classList.remove('current');
                    item.classList.remove('answered');
                    
                    // Check if this is the current question
                    if (index === currentQuestionIndex) {
                        item.classList.add('current');
                    }
                    
                    // Check if question is answered - try multiple methods
                    let questionWrapper = null;
                    
                    // Method 1: Using data attribute
                    questionWrapper = document.querySelector(`.question-wrapper[data-question-index="${index}"]`);
                    
                    // Method 2: Using ID
                    if (!questionWrapper) {
                        questionWrapper = document.getElementById(`question-${index}`);
                    }
                    
                    // Method 3: Using array index
                    if (!questionWrapper) {
                        const allWrappers = document.querySelectorAll('.question-wrapper');
                        if (index < allWrappers.length) {
                            questionWrapper = allWrappers[index];
                        }
                    }
                    
                    if (questionWrapper) {
                        const radio = questionWrapper.querySelector('input[type="radio"]:checked');
                        if (radio) {
                            item.classList.add('answered');
                        }
                    }
                });
            }

            function scrollToTop() {
                window.scrollTo({ top: 0, behavior: 'smooth' });
            }

            // Đếm số câu đã trả lời
            function updateAnsweredCount() {
                const answered = document.querySelectorAll('input[type="radio"]:checked').length;
                document.getElementById('answeredCount').textContent = answered;
                updateQuestionNavPanel();
            }

            // Chọn đáp án
            function selectAnswer(label) {
                const radio = label.querySelector('input[type="radio"]');
                radio.checked = true;
                updateSelected(radio);
            }

            function updateSelected(radio) {
                // Remove selected class from all options in the same question
                const questionContainer = radio.closest('.question-container');
                questionContainer.querySelectorAll('.answer-option').forEach(opt => {
                    opt.classList.remove('selected');
                });
                
                // Add selected class to the selected option
                if (radio.checked) {
                    radio.closest('.answer-option').classList.add('selected');
                }
                
                updateAnsweredCount();
                // Update navigation panel to show answered status
                updateQuestionNavPanel();
            }

            // Keyboard navigation
            document.addEventListener('keydown', function(e) {
                if (e.key === 'ArrowLeft' && currentQuestionIndex > 0) {
                    previousQuestion();
                } else if (e.key === 'ArrowRight' && currentQuestionIndex < totalQuestions - 1) {
                    nextQuestion();
                }
            });

            // Initialize
            document.addEventListener('DOMContentLoaded', function() {
                console.log('DOM loaded, initializing...');
                console.log('Total questions:', totalQuestions);
                console.log('Question wrappers found:', document.querySelectorAll('.question-wrapper').length);
                
                document.querySelectorAll('input[type="radio"]').forEach(radio => {
                    radio.addEventListener('change', function() {
                        updateSelected(this);
                    });
                });
                
                updateNavigation();
                updateAnsweredCount();
                updateQuestionNavPanel();
                
                // Ensure first question is visible
                const firstQuestion = document.querySelector('.question-wrapper[data-question-index="0"]');
                if (firstQuestion) {
                    firstQuestion.classList.add('active');
                }
            });
            
            // Also initialize immediately if DOM is already loaded
            if (document.readyState === 'loading') {
                // DOM is still loading, wait for DOMContentLoaded
            } else {
                // DOM is already loaded
                console.log('DOM already loaded, initializing immediately...');
                document.querySelectorAll('input[type="radio"]').forEach(radio => {
                    radio.addEventListener('change', function() {
                        updateSelected(this);
                    });
                });
                
                updateNavigation();
                updateAnsweredCount();
                updateQuestionNavPanel();
                
                // Ensure first question is visible
                const firstQuestion = document.querySelector('.question-wrapper[data-question-index="0"]');
                if (firstQuestion) {
                    firstQuestion.classList.add('active');
                }
            }

            // Prevent accidental page leave
            document.getElementById('examForm').addEventListener('submit', function() {
                formSubmitted = true;
            });

            window.addEventListener('beforeunload', function(e) {
                if (!formSubmitted) {
                    e.preventDefault();
                    e.returnValue = 'Bạn có chắc muốn rời khỏi trang? Tiến trình làm bài sẽ được lưu.';
                }
            });
        </script>
    </body>
</html>


