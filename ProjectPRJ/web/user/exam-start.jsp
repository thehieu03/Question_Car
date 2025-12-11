<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@page import="model.User"%>
<%@page import="model.ExamSet"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Bắt đầu làm bài</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
        <style>
            body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(180deg, #eef2ff 0%, #f8f9fc 100%); }
            .container { max-width: 760px; margin: 48px auto; background: white; padding: 28px; border-radius: 12px; box-shadow: 0 10px 30px rgba(0,0,0,0.12); }
            h1 { color: #4b4f5c; margin-bottom: 10px; }
            .meta { color: #555; margin-bottom: 16px; }
            .meta strong { color: #667eea; }
            .actions { display: flex; gap: 10px; margin-top: 20px; }
            .btn { padding: 10px 16px; border: none; border-radius: 8px; cursor: pointer; text-decoration: none; font-weight: 600; }
            .btn-primary { background: #667eea; color: white; box-shadow: 0 8px 18px rgba(102,126,234,0.35); }
            .btn-primary:disabled { opacity: 0.6; cursor: not-allowed; box-shadow: none; }
            .btn-secondary { background: #9aa0b1; color: white; }
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
            String error = (String) request.getAttribute("error");
        %>
        <div class="container">
            <% if (error != null) { %>
                <h2 style="color:#e53935;"><%= error %></h2>
                <div class="actions">
                    <a href="<%= request.getContextPath() %>/user/exams" class="btn btn-secondary">Quay lại</a>
                </div>
            <% } else if (examSet != null) { %>
                <h1><%= examSet.getExamName() %></h1>
                <div class="meta">
                    Số câu: <strong><%= examSet.getTotalQuestions() %></strong> |
                    Thời gian: <strong><%= examSet.getDurationMinutes() %> phút</strong> |
                    Điểm đạt: <strong><%= examSet.getPassingScore() %></strong>
                </div>
                <p>Nhấn "Bắt đầu" để vào bài thi (chức năng làm bài sẽ được bổ sung).</p>
                <div class="actions">
                    <a href="<%= request.getContextPath() %>/user/exams" class="btn btn-secondary">Quay lại</a>
                    <button class="btn btn-primary" disabled>Bắt đầu (sắp ra mắt)</button>
                </div>
            <% } %>
        </div>
    </body>
</html>


