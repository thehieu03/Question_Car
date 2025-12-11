<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@page import="model.User"%>
<%@page import="model.UserExam"%>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý Bài thi đã làm - Admin</title>
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
            .exam-table { background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
            table { width: 100%; border-collapse: collapse; }
            thead { background: #667eea; color: white; }
            th, td { padding: 12px 14px; text-align: left; }
            tbody tr { border-bottom: 1px solid #e0e0e0; }
            tbody tr:hover { background: #f9f9f9; }
            .badge { padding: 4px 8px; border-radius: 4px; font-size: 12px; font-weight: 600; }
            .badge-pass { background: #4caf50; color: white; }
            .badge-fail { background: #f44336; color: white; }
            .badge-status { background: #999; color: white; }
            .pagination { display: flex; justify-content: center; align-items: center; gap: 5px; margin-top: 20px; flex-wrap: wrap; }
            .pagination a, .pagination span { padding: 8px 12px; border: 1px solid #e0e0e0; border-radius: 5px; text-decoration: none; color: #333; min-width: 40px; text-align: center; transition: all 0.3s; }
            .pagination a:hover:not(.disabled) { background: #667eea; color: white; border-color: #667eea; }
            .pagination .active { background: #667eea; color: white; border-color: #667eea; cursor: default; font-weight: 600; }
            .pagination .disabled { opacity: 0.5; cursor: not-allowed; pointer-events: none; }
            .pagination .ellipsis { border: none; cursor: default; padding: 8px 5px; }
            .pagination-info { text-align: center; margin-top: 15px; color: #666; font-size: 14px; }
            .pagination-info strong { color: #667eea; }
        </style>
    </head>
    <body>
        <%
            User user = (User) session.getAttribute("user");
            if (user == null || !user.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            List<UserExam> results = (List<UserExam>) request.getAttribute("results");
            int currentPage = request.getAttribute("currentPage") != null ? (Integer) request.getAttribute("currentPage") : 1;
            int totalPages = request.getAttribute("totalPages") != null ? (Integer) request.getAttribute("totalPages") : 1;
            int totalResults = request.getAttribute("totalResults") != null ? (Integer) request.getAttribute("totalResults") : 0;
        %>

        <div class="sidebar">
            <div class="sidebar-header">
                <h2>Admin Panel</h2>
                <p>Quản trị hệ thống</p>
            </div>
            <nav class="sidebar-menu">
                <a href="<%= request.getContextPath() %>/admin" class="menu-item">
                    <i class="icon fas fa-chart-line"></i>
                    Dashboard Thống kê
                </a>
                <a href="<%= request.getContextPath() %>/admin/users" class="menu-item">
                    <i class="icon fas fa-users"></i>
                    Quản lý Người dùng
                </a>
                <a href="<%= request.getContextPath() %>/admin/questions" class="menu-item">
                    <i class="icon fas fa-question-circle"></i>
                    Quản lý Câu hỏi
                </a>
                <a href="<%= request.getContextPath() %>/admin/categories" class="menu-item">
                    <i class="icon fas fa-tags"></i>
                    Quản lý Danh mục
                </a>
                <a href="<%= request.getContextPath() %>/admin/exams" class="menu-item">
                    <i class="icon fas fa-file-alt"></i>
                    Quản lý Đề thi
                </a>
                <a href="<%= request.getContextPath() %>/admin/exam-results" class="menu-item active">
                    <i class="icon fas fa-clipboard-list"></i>
                    Quản lý Bài thi đã làm
                </a>
            </nav>
        </div>

        <div class="main-content">
            <div class="header">
                <div class="header-content">
                    <h1>Quản lý Bài thi đã làm</h1>
                    <div class="user-info">
                        <span>Xin chào, <strong><%= user.getUsername() %></strong></span>
                        <a href="<%= request.getContextPath() %>/logout" class="logout-btn">Đăng xuất</a>
                    </div>
                </div>
            </div>

            <div class="container">
                <div class="exam-table">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Người dùng</th>
                                <th>Email</th>
                                <th>Đề thi</th>
                                <th>Điểm</th>
                                <th>Đúng / Sai</th>
                                <th>Trạng thái</th>
                                <th>Thời gian</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (results != null && !results.isEmpty()) { %>
                                <% for (UserExam r : results) { %>
                                <tr>
                                    <td><%= r.getUserExamId() %></td>
                                    <td><%= r.getUsername() %></td>
                                    <td><%= r.getEmail() %></td>
                                    <td><%= r.getExamName() %></td>
                                    <td><%= r.getTotalScore() != null ? r.getTotalScore() : "-" %></td>
                                    <td><%= (r.getCorrectAnswers() != null ? r.getCorrectAnswers() : 0) %> / <%= (r.getWrongAnswers() != null ? r.getWrongAnswers() : 0) %></td>
                                    <td>
                                        <% if (r.getIsPassed() != null) { %>
                                            <span class="badge <%= r.getIsPassed() ? "badge-pass" : "badge-fail" %>">
                                                <%= r.getIsPassed() ? "Đậu" : "Rớt" %>
                                            </span>
                                        <% } else { %>
                                            <span class="badge badge-status">Chưa chấm</span>
                                        <% } %>
                                        <% if (r.getStatus() != null) { %>
                                            <span class="badge badge-status"><%= r.getStatus() %></span>
                                        <% } %>
                                    </td>
                                    <td>
                                        Bắt đầu: <%= r.getStartTime() != null ? r.getStartTime() : "-" %><br>
                                        Kết thúc: <%= r.getEndTime() != null ? r.getEndTime() : "-" %>
                                    </td>
                                </tr>
                                <% } %>
                            <% } else { %>
                                <tr>
                                    <td colspan="8" style="text-align:center; padding: 24px;">Chưa có bài thi nào</td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>

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

                <% if (totalPages > 1) { %>
                <div class="pagination">
                    <% if (currentPage > 1) { %>
                        <a href="<%= request.getContextPath() %>/admin/exam-results?page=<%= currentPage - 1 %>"><i class="fas fa-chevron-left"></i> Trước</a>
                    <% } else { %>
                        <span class="disabled"><i class="fas fa-chevron-left"></i> Trước</span>
                    <% } %>

                    <% if (startPage > 1) { %>
                        <a href="<%= request.getContextPath() %>/admin/exam-results?page=1">1</a>
                        <% if (showStartEllipsis) { %><span class="ellipsis">...</span><% } %>
                    <% } %>

                    <% for (int i = startPage; i <= endPage; i++) { %>
                        <% if (i == currentPage) { %>
                            <span class="active"><%= i %></span>
                        <% } else { %>
                            <a href="<%= request.getContextPath() %>/admin/exam-results?page=<%= i %>"><%= i %></a>
                        <% } %>
                    <% } %>

                    <% if (endPage < totalPages) { %>
                        <% if (showEndEllipsis) { %><span class="ellipsis">...</span><% } %>
                        <a href="<%= request.getContextPath() %>/admin/exam-results?page=<%= totalPages %>"><%= totalPages %></a>
                    <% } %>

                    <% if (currentPage < totalPages) { %>
                        <a href="<%= request.getContextPath() %>/admin/exam-results?page=<%= currentPage + 1 %>">Sau <i class="fas fa-chevron-right"></i></a>
                    <% } else { %>
                        <span class="disabled">Sau <i class="fas fa-chevron-right"></i></span>
                    <% } %>
                </div>
                <% } %>

                <div class="pagination-info">
                    <% if (totalResults > 0) { %>
                        Hiển thị <strong><%= ((currentPage - 1) * 10) + 1 %>-<%= Math.min(currentPage * 10, totalResults) %></strong> trong tổng số <strong><%= totalResults %></strong> bài thi
                        <% if (totalPages > 1) { %> | Trang <strong><%= currentPage %>/<%= totalPages %></strong> <% } %>
                    <% } else { %>
                        Không có bài thi nào
                    <% } %>
                </div>
            </div>
        </div>
    </body>
</html>


