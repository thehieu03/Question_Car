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
        <title>Chi tiết đề thi - Admin</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
        <style>
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: #f5f5f5; display: flex; min-height: 100vh; }
            .sidebar { width: 250px; background: linear-gradient(180deg, #667eea 0%, #764ba2 100%); color: white; position: fixed; height: 100vh; overflow-y: auto; box-shadow: 2px 0 10px rgba(0,0,0,0.1); }
            .sidebar-header { padding: 20px; border-bottom: 1px solid rgba(255,255,255,0.1); }
            .sidebar-header h2 { font-size: 20px; margin-bottom: 5px; }
            .sidebar-header p { font-size: 12px; opacity: 0.8; }
            .sidebar-menu { padding: 20px 0; }
            .menu-item { display: block; padding: 15px 20px; color: white; text-decoration: none; transition: background 0.3s; border-left: 3px solid transparent; }
            .menu-item:hover { background: rgba(255,255,255,0.1); border-left-color: white; }
            .menu-item.active { background: rgba(255,255,255,0.15); border-left-color: white; }
            .menu-item .icon { margin-right: 10px; font-size: 18px; }
            .main-content { margin-left: 250px; flex: 1; min-height: 100vh; }
            .header { background: white; color: #333; padding: 20px 30px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
            .header-content { display: flex; justify-content: space-between; align-items: center; }
            .header h1 { font-size: 24px; color: #667eea; }
            .user-info { display: flex; align-items: center; gap: 15px; }
            .user-info span { font-size: 14px; color: #666; }
            .logout-btn { background: #667eea; color: white; border: none; padding: 8px 20px; border-radius: 5px; cursor: pointer; text-decoration: none; transition: background 0.3s; font-size: 14px; }
            .logout-btn:hover { background: #5568d3; }
            .container { padding: 30px; }
            .card { background: white; border-radius: 10px; padding: 20px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); margin-bottom: 20px; }
            .card h2 { color: #667eea; margin-bottom: 10px; }
            .meta { color: #666; margin-bottom: 10px; }
            .badge { display: inline-block; padding: 6px 10px; border-radius: 5px; font-size: 12px; font-weight: 600; }
            .badge-critical { background: #f44336; color: white; }
            .badge-normal { background: #4caf50; color: white; }
            .question { background: #fafafa; border: 1px solid #e0e0e0; border-radius: 8px; padding: 15px; margin-bottom: 15px; }
            .question-title { font-weight: 600; margin-bottom: 8px; color: #333; }
            .question-meta { color: #777; font-size: 13px; margin-bottom: 8px; }
            .answers { margin-left: 15px; }
            .answer { padding: 6px 0; color: #444; }
            .answer.correct { font-weight: 600; color: #2e7d32; }
            .back-link { display: inline-block; margin-bottom: 15px; color: #667eea; text-decoration: none; }
            .back-link:hover { text-decoration: underline; }
        </style>
    </head>
    <body>
        <%
            User user = (User) session.getAttribute("user");
            if (user == null || !user.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            ExamSet examSet = (ExamSet) request.getAttribute("examSet");
            List<Question> questions = (List<Question>) request.getAttribute("questions");
            Map<Integer, List<Answer>> answersMap = (Map<Integer, List<Answer>>) request.getAttribute("answersMap");
        %>

        <div class="sidebar">
            <div class="sidebar-header">
                <h2>Admin Panel</h2>
                <p>Quản trị hệ thống</p>
            </div>
            <nav class="sidebar-menu">
                <a href="<%= request.getContextPath() %>/admin" class="menu-item">
                    <i class="icon fas fa-chart-line"></i> Dashboard Thống kê
                </a>
                <a href="<%= request.getContextPath() %>/admin/users" class="menu-item">
                    <i class="icon fas fa-users"></i> Quản lý Người dùng
                </a>
                <a href="<%= request.getContextPath() %>/admin/questions" class="menu-item">
                    <i class="icon fas fa-question-circle"></i> Quản lý Câu hỏi
                </a>
                <a href="<%= request.getContextPath() %>/admin/categories" class="menu-item">
                    <i class="icon fas fa-tags"></i> Quản lý Danh mục
                </a>
                <a href="<%= request.getContextPath() %>/admin/exams" class="menu-item active">
                    <i class="icon fas fa-file-alt"></i> Quản lý Đề thi
                </a>
                <a href="<%= request.getContextPath() %>/admin/exam-results" class="menu-item">
                    <i class="icon fas fa-clipboard-list"></i> Quản lý Bài thi đã làm
                </a>
            </nav>
        </div>

        <div class="main-content">
            <div class="header">
                <div class="header-content">
                    <h1>Chi tiết đề thi</h1>
                    <div class="user-info">
                        <span>Xin chào, <strong><%= user.getUsername() %></strong></span>
                        <a href="<%= request.getContextPath() %>/logout" class="logout-btn">Đăng xuất</a>
                    </div>
                </div>
            </div>

            <div class="container">
                <a href="<%= request.getContextPath() %>/admin/exams" class="back-link"><i class="fas fa-arrow-left"></i> Quay lại danh sách đề</a>

                <div class="card">
                    <h2><%= examSet.getExamName() %></h2>
                    <div class="meta">
                        <strong>Tổng số câu:</strong> <%= examSet.getTotalQuestions() %> |
                        <strong>Thời gian:</strong> <%= examSet.getDurationMinutes() %> phút |
                        <strong>Điểm đạt:</strong> <%= examSet.getPassingScore() %> câu đúng
                    </div>
                    <div class="meta">
                        <strong>Loại đề:</strong> <%= examSet.getCategoryName() != null ? examSet.getCategoryName() : "Chưa gán" %>
                    </div>
                </div>

                <% if (questions != null && !questions.isEmpty()) { %>
                    <% int idx = 1;
                       for (Question q : questions) { 
                           List<Answer> ansList = answersMap != null ? answersMap.get(q.getQuestionId()) : null;
                    %>
                    <div class="question">
                        <div class="question-title">Câu <%= idx++ %>: <%= q.getQuestionText() %></div>
                        <div class="question-meta">
                            Danh mục: <%= q.getCategoryId() %> 
                            <% if (q.isCritical()) { %>
                                | <span class="badge badge-critical">Điểm liệt</span>
                            <% } else { %>
                                | <span class="badge badge-normal">Thường</span>
                            <% } %>
                        </div>
                        <% if (q.getQuestionImage() != null && !q.getQuestionImage().isEmpty()) { %>
                            <div class="question-image" style="margin:10px 0;">
                                <img src="<%= q.getQuestionImage() %>" alt="Hình minh họa" style="max-width: 100%; border-radius: 6px;">
                            </div>
                        <% } %>
                        <div class="answers">
                            <% if (ansList != null && !ansList.isEmpty()) { 
                                   for (Answer a : ansList) { %>
                                <div class="answer <%= a.isCorrect() ? "correct" : "" %>">
                                    <strong><%= (char)('A' + Math.max(0, a.getAnswerOrder() - 1)) %>. </strong>
                                    <%= a.getAnswerText() %>
                                    <% if (a.isCorrect()) { %> <i class="fas fa-check-circle"></i> <% } %>
                                </div>
                            <%   }
                               } else { %>
                                <div class="answer">Chưa có đáp án.</div>
                            <% } %>
                        </div>
                    </div>
                    <% } %>
                <% } else { %>
                    <div class="card">Chưa có câu hỏi trong đề này.</div>
                <% } %>
            </div>
        </div>
    </body>
</html>


