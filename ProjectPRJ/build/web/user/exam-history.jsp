<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@page import="model.User"%>
<%@page import="model.UserExam"%>
<%@page import="java.util.List"%>
<%@page import="java.text.SimpleDateFormat"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Lịch sử bài thi</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
        <style>
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(180deg, #eef2ff 0%, #f8f9fc 100%); color: #2f2f2f; }
            .navbar { background: linear-gradient(90deg, #667eea 0%, #764ba2 100%); color: white; padding: 16px 22px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 4px 16px rgba(0,0,0,0.12); }
            .container { max-width: 1100px; margin: 24px auto 32px; padding: 0 16px; }
            .page-header { background: white; border-radius: 12px; padding: 20px; box-shadow: 0 6px 20px rgba(0,0,0,0.08); margin-bottom: 20px; }
            .page-header h1 { color: #4b4f5c; margin-bottom: 8px; }
            .page-header p { color: #6b7280; font-size: 14px; }
            .history-list { background: white; border-radius: 12px; box-shadow: 0 8px 24px rgba(0,0,0,0.08); overflow: hidden; }
            table { width: 100%; border-collapse: collapse; }
            th, td { padding: 14px 16px; border-bottom: 1px solid #f1f2f6; text-align: left; }
            thead { background: #f7f8ff; color: #444; }
            tr:last-child td { border-bottom: none; }
            .badge { padding: 6px 10px; border-radius: 6px; font-weight: 600; font-size: 12px; display: inline-block; }
            .badge.passed { background: #e8f5e9; color: #2e7d32; }
            .badge.failed { background: #ffebee; color: #c62828; }
            .btn { padding: 8px 16px; border: none; border-radius: 8px; cursor: pointer; text-decoration: none; font-weight: 600; font-size: 14px; display: inline-block; transition: all 0.2s; }
            .btn-primary { background: #667eea; color: white; box-shadow: 0 6px 16px rgba(102,126,234,0.35); }
            .btn-primary:hover { background: #5568d3; transform: translateY(-1px); }
            .btn-secondary { background: #9aa0b1; color: white; }
            .btn-secondary:hover { background: #7a7f8f; }
            .pagination { display: flex; justify-content: center; gap: 6px; padding: 16px; flex-wrap: wrap; }
            .pagination a, .pagination span { padding: 8px 12px; border: 1px solid #e0e0e0; border-radius: 8px; text-decoration: none; color: #333; min-width: 36px; text-align: center; background: white; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
            .pagination .active { background: #667eea; color: white; border-color: #667eea; box-shadow: 0 4px 12px rgba(102,126,234,0.35); }
            .pagination .disabled { opacity: 0.6; pointer-events: none; }
            .pagination .ellipsis { border: none; box-shadow: none; }
            .info { text-align: center; padding: 14px; color: #6b7280; }
            .empty-state { text-align: center; padding: 48px 24px; color: #6b7280; }
            .empty-state i { font-size: 64px; color: #9aa0b1; margin-bottom: 16px; }
        </style>
    </head>
    <body>
        <%
            User user = (User) session.getAttribute("user");
            if (user == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            List<UserExam> userExams = (List<UserExam>) request.getAttribute("userExams");
            int currentPage = request.getAttribute("currentPage") != null ? (Integer) request.getAttribute("currentPage") : 1;
            int totalPages = request.getAttribute("totalPages") != null ? (Integer) request.getAttribute("totalPages") : 1;
            int totalExams = request.getAttribute("totalExams") != null ? (Integer) request.getAttribute("totalExams") : 0;
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        %>

        <div class="navbar">
            <div style="font-weight:700;">Lịch sử bài thi</div>
            <div style="display:flex; align-items:center; gap:20px;">
                <span>Xin chào, <%= user.getUsername() %></span>
                <a href="<%= request.getContextPath() %>/user/exams" style="color: white; text-decoration: none;">Chọn đề thi</a>
                <a href="<%= request.getContextPath() %>/logout" style="color: white; text-decoration: none;">Đăng xuất</a>
            </div>
        </div>

        <div class="container">
            <div class="page-header">
                <h1>Lịch sử bài thi của tôi</h1>
                <p>Tổng số bài đã làm: <strong><%= totalExams %></strong></p>
            </div>

            <div class="history-list">
                <% if (userExams != null && !userExams.isEmpty()) { %>
                    <table>
                        <thead>
                            <tr>
                                <th>Đề thi</th>
                                <th>Ngày làm</th>
                                <th>Điểm số</th>
                                <th>Kết quả</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (UserExam exam : userExams) { %>
                            <tr>
                                <td><strong><%= exam.getExamName() != null ? exam.getExamName() : "Đề số " + exam.getExamSetId() %></strong></td>
                                <td><%= exam.getEndTime() != null ? dateFormat.format(exam.getEndTime()) : "-" %></td>
                                <td>
                                    <strong style="color: #667eea; font-size: 18px;">
                                        <%= exam.getTotalScore() != null ? exam.getTotalScore() : 0 %>
                                    </strong>
                                    <% if (exam.getCorrectAnswers() != null && exam.getWrongAnswers() != null) { %>
                                        <span style="color: #6b7280; font-size: 12px;">
                                            (<%= exam.getCorrectAnswers() %> đúng / <%= exam.getWrongAnswers() %> sai)
                                        </span>
                                    <% } %>
                                </td>
                                <td>
                                    <% if (exam.getIsPassed() != null && exam.getIsPassed()) { %>
                                        <span class="badge passed">
                                            <i class="fas fa-check-circle"></i> Đậu
                                        </span>
                                    <% } else { %>
                                        <span class="badge failed">
                                            <i class="fas fa-times-circle"></i> Rớt
                                        </span>
                                    <% } %>
                                </td>
                                <td>
                                    <a href="<%= request.getContextPath() %>/user/exam/result?userExamId=<%= exam.getUserExamId() %>" class="btn btn-primary">
                                        <i class="fas fa-eye"></i> Xem chi tiết
                                    </a>
                                </td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                <% } else { %>
                    <div class="empty-state">
                        <i class="fas fa-clipboard-list"></i>
                        <h3 style="margin-bottom: 8px; color: #4b4f5c;">Chưa có bài thi nào</h3>
                        <p>Bạn chưa hoàn thành bài thi nào. Hãy bắt đầu làm bài thi ngay!</p>
                        <a href="<%= request.getContextPath() %>/user/exams" class="btn btn-primary" style="margin-top: 16px;">
                            <i class="fas fa-play"></i> Làm bài thi ngay
                        </a>
                    </div>
                <% } %>
            </div>

            <% if (totalPages > 1) { %>
                <%
                    int startPage = 1;
                    int endPage = totalPages;
                    boolean showStartEllipsis = false;
                    boolean showEndEllipsis = false;

                    if (totalPages > 9) {
                        if (currentPage <= 4) {
                            endPage = 5;
                            showEndEllipsis = true;
                        } else if (currentPage >= totalPages - 3) {
                            startPage = totalPages - 4;
                            showStartEllipsis = true;
                        } else {
                            startPage = currentPage - 2;
                            endPage = currentPage + 2;
                            showStartEllipsis = true;
                            showEndEllipsis = true;
                        }
                    }
                %>

                <div class="pagination">
                    <% if (currentPage > 1) { %>
                        <a href="<%= request.getContextPath() %>/user/history?page=<%= currentPage - 1 %>">
                            <i class="fas fa-chevron-left"></i>
                        </a>
                    <% } else { %>
                        <span class="disabled"><i class="fas fa-chevron-left"></i></span>
                    <% } %>

                    <% if (startPage > 1) { %>
                        <a href="<%= request.getContextPath() %>/user/history?page=1">1</a>
                        <% if (showStartEllipsis) { %><span class="ellipsis">...</span><% } %>
                    <% } %>

                    <% for (int i = startPage; i <= endPage; i++) { %>
                        <% if (i == currentPage) { %>
                            <span class="active"><%= i %></span>
                        <% } else { %>
                            <a href="<%= request.getContextPath() %>/user/history?page=<%= i %>"><%= i %></a>
                        <% } %>
                    <% } %>

                    <% if (endPage < totalPages) { %>
                        <% if (showEndEllipsis) { %><span class="ellipsis">...</span><% } %>
                        <a href="<%= request.getContextPath() %>/user/history?page=<%= totalPages %>"><%= totalPages %></a>
                    <% } %>

                    <% if (currentPage < totalPages) { %>
                        <a href="<%= request.getContextPath() %>/user/history?page=<%= currentPage + 1 %>">
                            <i class="fas fa-chevron-right"></i>
                        </a>
                    <% } else { %>
                        <span class="disabled"><i class="fas fa-chevron-right"></i></span>
                    <% } %>
                </div>
            <% } %>

            <div class="info">
                <% if (totalExams > 0) { %>
                    Hiển thị <strong><%= ((currentPage - 1) * 10) + 1 %>-<%= Math.min(currentPage * 10, totalExams) %></strong> trên <strong><%= totalExams %></strong> bài thi
                    <% if (totalPages > 1) { %> | Trang <strong><%= currentPage %>/<%= totalPages %></strong> <% } %>
                <% } %>
            </div>
        </div>
    </body>
</html>

