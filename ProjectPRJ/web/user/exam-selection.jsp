<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@page import="model.User"%>
<%@page import="model.ExamSet"%>
<%@page import="model.QuestionCategory"%>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Chọn bài thi</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
        <style>
            * { margin: 0; padding: 0; box-sizing: border-box; }
            body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(180deg, #eef2ff 0%, #f8f9fc 100%); color: #2f2f2f; }
            .navbar { background: linear-gradient(90deg, #667eea 0%, #764ba2 100%); color: white; padding: 16px 22px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 4px 16px rgba(0,0,0,0.12); }
            .navbar a { color: white; text-decoration: none; margin-left: 15px; font-weight: 600; }
            .container { max-width: 1100px; margin: 24px auto 32px; padding: 0 16px; }
            .hero { background: white; border-radius: 12px; padding: 18px 20px; box-shadow: 0 6px 20px rgba(0,0,0,0.08); margin-bottom: 16px; display: flex; justify-content: space-between; align-items: center; }
            .hero h2 { color: #4b4f5c; margin-bottom: 6px; }
            .hero p { color: #6b7280; font-size: 14px; }
            .categories { display: flex; gap: 10px; flex-wrap: wrap; margin-bottom: 16px; }
            .cat-btn { padding: 10px 14px; border-radius: 999px; border: 1px solid #e0e0e0; background: white; cursor: pointer; text-decoration: none; color: #333; transition: all 0.2s; box-shadow: 0 2px 6px rgba(0,0,0,0.06); }
            .cat-btn:hover { transform: translateY(-1px); }
            .cat-btn.active { background: #667eea; color: white; border-color: #667eea; box-shadow: 0 4px 12px rgba(102,126,234,0.35); }
            .list { background: white; border-radius: 12px; box-shadow: 0 8px 24px rgba(0,0,0,0.08); overflow: hidden; }
            table { width: 100%; border-collapse: collapse; }
            th, td { padding: 14px 16px; border-bottom: 1px solid #f1f2f6; text-align: left; }
            thead { background: #f7f8ff; color: #444; }
            tr:last-child td { border-bottom: none; }
            .badge { padding: 6px 10px; border-radius: 6px; background: #eef2ff; color: #3f51b5; font-weight: 600; font-size: 12px; }
            .btn-primary { background: #667eea; color: white; padding: 9px 14px; border: none; border-radius: 8px; cursor: pointer; text-decoration: none; font-size: 14px; box-shadow: 0 6px 16px rgba(102,126,234,0.35); transition: all 0.2s; }
            .btn-primary:hover { background: #5568d3; transform: translateY(-1px); }
            .pagination { display: flex; justify-content: center; gap: 6px; padding: 16px; flex-wrap: wrap; }
            .pagination a, .pagination span { padding: 8px 12px; border: 1px solid #e0e0e0; border-radius: 8px; text-decoration: none; color: #333; min-width: 36px; text-align: center; background: white; box-shadow: 0 2px 8px rgba(0,0,0,0.04); }
            .pagination .active { background: #667eea; color: white; border-color: #667eea; box-shadow: 0 4px 12px rgba(102,126,234,0.35); }
            .pagination .disabled { opacity: 0.6; pointer-events: none; }
            .pagination .ellipsis { border: none; box-shadow: none; }
            .info { text-align: center; padding: 14px; color: #6b7280; }
        </style>
    </head>
    <body>
        <%
            User u = (User) session.getAttribute("user");
            if (u == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            List<QuestionCategory> categories = (List<QuestionCategory>) request.getAttribute("categories");
            List<ExamSet> examSets = (List<ExamSet>) request.getAttribute("examSets");
            Integer selectedCategoryId = request.getAttribute("selectedCategoryId") != null ? (Integer) request.getAttribute("selectedCategoryId") : null;
            int currentPage = request.getAttribute("currentPage") != null ? (Integer) request.getAttribute("currentPage") : 1;
            int totalPages = request.getAttribute("totalPages") != null ? (Integer) request.getAttribute("totalPages") : 1;
            int totalExamSets = request.getAttribute("totalExamSets") != null ? (Integer) request.getAttribute("totalExamSets") : 0;
        %>

        <div class="navbar">
            <div style="font-weight:700;">Chọn bài thi</div>
            <div style="display:flex; align-items:center; gap:20px;">
                <div style="display:flex; gap:12px; align-items:center; background: rgba(255,255,255,0.15); padding: 8px 12px; border-radius: 10px; font-size: 13px;">
                    <span>Bài đã làm: <strong><%= request.getAttribute("totalTaken") != null ? request.getAttribute("totalTaken") : 0 %></strong></span>
                    <span>| Đậu: <strong><%= request.getAttribute("totalPassed") != null ? request.getAttribute("totalPassed") : 0 %></strong></span>
                    <span>| Điểm gần nhất: <strong><%= request.getAttribute("lastScore") != null ? request.getAttribute("lastScore") : "-" %></strong></span>
                </div>
                <span>Xin chào, <%= u.getUsername() %></span>
                <a href="<%= request.getContextPath() %>/logout">Đăng xuất</a>
            </div>
        </div>

        <div class="container">
            <div class="hero">
                <div>
                    <h2>Chọn bài thi theo chủ đề</h2>
                    <p>Ô tô · Xe máy · Chọn đề phù hợp và bắt đầu luyện tập</p>
                </div>
                <div style="color:#667eea; font-weight:700;">Bài thi: <%= totalExamSets %></div>
            </div>
            <div class="categories">
                <% if (categories != null) {
                       for (QuestionCategory c : categories) {
                           boolean active = selectedCategoryId != null && selectedCategoryId == c.getCategoryId();
                %>
                <a class="cat-btn <%= active ? "active" : "" %>" href="<%= request.getContextPath() %>/user/exams?categoryId=<%= c.getCategoryId() %>"><%= c.getCategoryName() %></a>
                <%   }
                   } %>
            </div>

            <div class="list">
                <table>
                    <thead>
                        <tr>
                            <th>Tên đề</th>
                            <th>Số câu</th>
                            <th>Thời gian (phút)</th>
                            <th>Điểm đạt</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <% if (examSets != null && !examSets.isEmpty()) { %>
                            <% for (ExamSet es : examSets) { %>
                            <tr>
                                <td><%= es.getExamName() %></td>
                                <td><span class="badge"><%= es.getTotalQuestions() %> câu</span></td>
                                <td><%= es.getDurationMinutes() %></td>
                                <td><%= es.getPassingScore() %></td>
                                <td>
                                    <a class="btn-primary" href="<%= request.getContextPath() %>/user/exam/start?examSetId=<%= es.getExamSetId() %>">
                                        Bắt đầu
                                    </a>
                                </td>
                            </tr>
                            <% } %>
                        <% } else { %>
                            <tr>
                                <td colspan="5" style="text-align:center; padding: 16px;">Chưa có đề thi cho danh mục này.</td>
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
                    <a href="<%= request.getContextPath() %>/user/exams?page=<%= currentPage - 1 %>&categoryId=<%= selectedCategoryId %>"><i class="fas fa-chevron-left"></i></a>
                <% } else { %>
                    <span class="disabled"><i class="fas fa-chevron-left"></i></span>
                <% } %>

                <% if (startPage > 1) { %>
                    <a href="<%= request.getContextPath() %>/user/exams?page=1&categoryId=<%= selectedCategoryId %>">1</a>
                    <% if (showStartEllipsis) { %><span class="ellipsis">...</span><% } %>
                <% } %>

                <% for (int i = startPage; i <= endPage; i++) { %>
                    <% if (i == currentPage) { %>
                        <span class="active"><%= i %></span>
                    <% } else { %>
                        <a href="<%= request.getContextPath() %>/user/exams?page=<%= i %>&categoryId=<%= selectedCategoryId %>"><%= i %></a>
                    <% } %>
                <% } %>

                <% if (endPage < totalPages) { %>
                    <% if (showEndEllipsis) { %><span class="ellipsis">...</span><% } %>
                    <a href="<%= request.getContextPath() %>/user/exams?page=<%= totalPages %>&categoryId=<%= selectedCategoryId %>"><%= totalPages %></a>
                <% } %>

                <% if (currentPage < totalPages) { %>
                    <a href="<%= request.getContextPath() %>/user/exams?page=<%= currentPage + 1 %>&categoryId=<%= selectedCategoryId %>"><i class="fas fa-chevron-right"></i></a>
                <% } else { %>
                    <span class="disabled"><i class="fas fa-chevron-right"></i></span>
                <% } %>
            </div>
            <% } %>

            <div class="info">
                <% if (totalExamSets > 0) { %>
                    Hiển thị <strong><%= ((currentPage - 1) * 10) + 1 %>-<%= Math.min(currentPage * 10, totalExamSets) %></strong> trên <strong><%= totalExamSets %></strong> đề
                    <% if (totalPages > 1) { %> | Trang <strong><%= currentPage %>/<%= totalPages %></strong> <% } %>
                <% } else { %>
                    Không có đề thi.
                <% } %>
            </div>
        </div>
    </body>
</html>


