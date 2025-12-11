<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page isELIgnored="false"%>
<%@page import="model.User"%>
<%@page import="model.Question"%>
<%@page import="model.QuestionCategory"%>
<%@page import="model.Answer"%>
<%@page import="java.util.List"%>
<%@page import="dao.AnswerDAO"%>
<%@page import="java.net.URLEncoder"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý Câu hỏi - Admin</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" />
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background: #f5f5f5;
                display: flex;
                min-height: 100vh;
            }

            .sidebar {
                width: 250px;
                background: linear-gradient(180deg, #667eea 0%, #764ba2 100%);
                color: white;
                position: fixed;
                height: 100vh;
                overflow-y: auto;
                box-shadow: 2px 0 10px rgba(0,0,0,0.1);
            }

            .sidebar-header {
                padding: 20px;
                border-bottom: 1px solid rgba(255,255,255,0.1);
            }

            .sidebar-header h2 {
                font-size: 20px;
                margin-bottom: 5px;
            }

            .sidebar-header p {
                font-size: 12px;
                opacity: 0.8;
            }

            .sidebar-menu {
                padding: 20px 0;
            }

            .menu-item {
                display: block;
                padding: 15px 20px;
                color: white;
                text-decoration: none;
                transition: background 0.3s;
                border-left: 3px solid transparent;
            }

            .menu-item:hover {
                background: rgba(255,255,255,0.1);
                border-left-color: white;
            }

            .menu-item.active {
                background: rgba(255,255,255,0.15);
                border-left-color: white;
            }

            .menu-item .icon {
                margin-right: 10px;
                font-size: 18px;
            }

            .main-content {
                margin-left: 250px;
                flex: 1;
                min-height: 100vh;
            }

            .header {
                background: white;
                color: #333;
                padding: 20px 30px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            }

            .header-content {
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            .header h1 {
                font-size: 24px;
                color: #667eea;
            }

            .user-info {
                display: flex;
                align-items: center;
                gap: 15px;
            }

            .user-info span {
                font-size: 14px;
                color: #666;
            }

            .logout-btn {
                background: #667eea;
                color: white;
                border: none;
                padding: 8px 20px;
                border-radius: 5px;
                cursor: pointer;
                text-decoration: none;
                transition: background 0.3s;
                font-size: 14px;
            }

            .logout-btn:hover {
                background: #5568d3;
            }

            .container {
                padding: 30px;
            }

            .search-bar {
                background: white;
                padding: 20px;
                border-radius: 10px;
                margin-bottom: 20px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
            }

            .search-form {
                display: flex;
                gap: 10px;
            }

            .search-form input {
                flex: 1;
                padding: 10px 15px;
                border: 2px solid #e0e0e0;
                border-radius: 5px;
                font-size: 14px;
            }

            .search-form input:focus {
                outline: none;
                border-color: #667eea;
            }

            .search-form button {
                padding: 10px 20px;
                background: #667eea;
                color: white;
                border: none;
                border-radius: 5px;
                cursor: pointer;
                font-size: 14px;
            }

            .search-form button:hover {
                background: #5568d3;
            }

            .add-btn {
                background: #4caf50;
                color: white;
                padding: 10px 20px;
                border: none;
                border-radius: 5px;
                cursor: pointer;
                text-decoration: none;
                display: inline-block;
                margin-bottom: 20px;
            }

            .add-btn:hover {
                background: #388e3c;
            }

            .questions-table {
                background: white;
                border-radius: 10px;
                overflow: hidden;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                max-height: 70vh;
                overflow-y: auto;
            }

            table {
                width: 100%;
                border-collapse: collapse;
            }

            thead {
                background: #667eea;
                color: white;
            }

            th, td {
                padding: 15px;
                text-align: left;
            }

            th {
                font-weight: 600;
            }

            tbody tr {
                border-bottom: 1px solid #e0e0e0;
            }

            tbody tr:hover {
                background: #f9f9f9;
            }

            .question-text {
                max-width: 300px;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
            }

            .badge {
                padding: 5px 10px;
                border-radius: 5px;
                font-size: 12px;
                font-weight: 600;
            }

            .badge-critical {
                background: #f44336;
                color: white;
            }

            .badge-normal {
                background: #4caf50;
                color: white;
            }

            .action-buttons {
                display: flex;
                gap: 5px;
            }

            .btn {
                padding: 5px 10px;
                border: none;
                border-radius: 5px;
                cursor: pointer;
                font-size: 12px;
                text-decoration: none;
                display: inline-block;
            }

            .btn-edit {
                background: #ff9800;
                color: white;
            }

            .btn-edit:hover {
                background: #f57c00;
            }

            .btn-delete {
                background: #f44336;
                color: white;
            }

            .btn-delete:hover {
                background: #d32f2f;
            }

            .pagination {
                display: flex;
                justify-content: center;
                align-items: center;
                gap: 5px;
                margin-top: 20px;
                flex-wrap: wrap;
            }

            .pagination a, .pagination span {
                padding: 8px 12px;
                border: 1px solid #e0e0e0;
                border-radius: 5px;
                text-decoration: none;
                color: #333;
                min-width: 40px;
                text-align: center;
                transition: all 0.3s;
            }

            .pagination a:hover:not(.disabled) {
                background: #667eea;
                color: white;
                border-color: #667eea;
            }

            .pagination .active {
                background: #667eea;
                color: white;
                border-color: #667eea;
                cursor: default;
                font-weight: 600;
            }

            .pagination .disabled {
                opacity: 0.5;
                cursor: not-allowed;
                pointer-events: none;
            }

            .pagination .ellipsis {
                border: none;
                cursor: default;
                padding: 8px 5px;
            }

            .pagination-info {
                text-align: center;
                margin-top: 15px;
                color: #666;
                font-size: 14px;
            }

            .pagination-info strong {
                color: #667eea;
            }

            .alert {
                padding: 15px;
                border-radius: 5px;
                margin-bottom: 20px;
            }

            .alert-success {
                background: #d4edda;
                color: #155724;
                border: 1px solid #c3e6cb;
            }

            .alert-error {
                background: #f8d7da;
                color: #721c24;
                border: 1px solid #f5c6cb;
            }

            .modal {
                display: none;
                position: fixed;
                z-index: 1000;
                left: 0;
                top: 0;
                width: 100%;
                height: 100%;
                background: rgba(0,0,0,0.5);
                overflow-y: auto;
            }

            .modal-content {
                background: white;
                margin: 2% auto;
                padding: 30px;
                border-radius: 10px;
                width: 90%;
                max-width: 600px;
                max-height: 90vh;
                overflow-y: auto;
            }

            .modal-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 20px;
            }

            .modal-header h2 {
                color: #667eea;
            }

            .close {
                font-size: 28px;
                font-weight: bold;
                cursor: pointer;
                color: #999;
            }

            .close:hover {
                color: #333;
            }

            .form-group {
                margin-bottom: 15px;
            }

            .form-group label {
                display: block;
                margin-bottom: 5px;
                color: #333;
                font-weight: 500;
            }

            .form-group input, .form-group select, .form-group textarea {
                width: 100%;
                padding: 10px;
                border: 2px solid #e0e0e0;
                border-radius: 5px;
                font-size: 14px;
            }

            .form-group textarea {
                min-height: 100px;
                resize: vertical;
            }

            .form-group input:focus, .form-group select:focus, .form-group textarea:focus {
                outline: none;
                border-color: #667eea;
            }

            .form-group.checkbox, .form-group.radio {
                display: flex;
                align-items: center;
                gap: 10px;
            }

            .form-group.checkbox input, .form-group.radio input {
                width: auto;
            }

            .radio-group {
                display: flex;
                align-items: center;
                gap: 5px;
            }

            .radio-group input[type="radio"] {
                width: auto;
                margin-right: 5px;
            }

            .answer-group {
                margin-bottom: 15px;
                padding: 15px;
                background: #f9f9f9;
                border-radius: 5px;
            }

            .answer-group label {
                font-weight: 600;
                color: #667eea;
            }

            .form-actions {
                display: flex;
                gap: 10px;
                justify-content: flex-end;
                margin-top: 20px;
            }

            .btn-primary {
                background: #667eea;
                color: white;
                padding: 10px 20px;
                border: none;
                border-radius: 5px;
                cursor: pointer;
            }

            .btn-primary:hover {
                background: #5568d3;
            }

            .btn-secondary {
                background: #999;
                color: white;
                padding: 10px 20px;
                border: none;
                border-radius: 5px;
                cursor: pointer;
            }

            .btn-secondary:hover {
                background: #777;
            }
        </style>
    </head>
    <body>
        <%
            User user = (User) session.getAttribute("user");
            if (user == null || !user.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            
            List<Question> questions = (List<Question>) request.getAttribute("questions");
            List<QuestionCategory> categories = (List<QuestionCategory>) request.getAttribute("categories");
            int currentPage = request.getAttribute("currentPage") != null ? (Integer) request.getAttribute("currentPage") : 1;
            int totalPages = request.getAttribute("totalPages") != null ? (Integer) request.getAttribute("totalPages") : 1;
            int totalQuestions = request.getAttribute("totalQuestions") != null ? (Integer) request.getAttribute("totalQuestions") : 0;
            String keyword = (String) request.getAttribute("keyword");
            String type = request.getAttribute("type") != null ? (String) request.getAttribute("type") : "all";
            Integer selectedCategoryId = request.getAttribute("categoryId") != null ? (Integer) request.getAttribute("categoryId") : null;
            AnswerDAO answerDAO = new AnswerDAO();
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
                <a href="<%= request.getContextPath() %>/admin/questions" class="menu-item active">
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
                <a href="<%= request.getContextPath() %>/admin/exam-results" class="menu-item">
                    <i class="icon fas fa-clipboard-list"></i>
                    Quản lý Bài thi đã làm
                </a>
            </nav>
        </div>

        <div class="main-content">
            <div class="header">
                <div class="header-content">
                    <h1>Quản lý Câu hỏi</h1>
                    <div class="user-info">
                        <span>Xin chào, <strong><%= user.getUsername() %></strong></span>
                        <a href="<%= request.getContextPath() %>/logout" class="logout-btn">Đăng xuất</a>
                    </div>
                </div>
            </div>

            <div class="container">
                <% if (request.getAttribute("success") != null) { %>
                <div class="alert alert-success">
                    <%= request.getAttribute("success") %>
                </div>
                <% } %>

                <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error">
                    <%= request.getAttribute("error") %>
                </div>
                <% } %>

                <div class="search-bar">
                    <form method="GET" action="<%= request.getContextPath() %>/admin/questions" class="search-form" onsubmit="return handleSearch(event)">
                        <input type="text" name="keyword" id="searchKeyword" placeholder="Tìm kiếm theo nội dung câu hỏi..." 
                               value="<%= keyword != null ? keyword : "" %>">
                        <select name="type" id="typeFilter">
                            <option value="all" <%= "all".equals(type) ? "selected" : "" %>>Tất cả loại</option>
                            <option value="critical" <%= "critical".equals(type) ? "selected" : "" %>>Câu hỏi điểm liệt</option>
                            <option value="normal" <%= "normal".equals(type) ? "selected" : "" %>>Câu hỏi thường</option>
                        </select>
                        <select name="categoryId" id="categoryFilter">
                            <option value="">Tất cả danh mục</option>
                            <% if (categories != null) {
                                   for (QuestionCategory cat : categories) { %>
                                <option value="<%= cat.getCategoryId() %>" <%= (selectedCategoryId != null && selectedCategoryId == cat.getCategoryId()) ? "selected" : "" %>>
                                    <%= cat.getCategoryName() %>
                                </option>
                            <%   }
                               } %>
                        </select>
                        <button type="submit"><i class="fas fa-search"></i> Tìm kiếm</button>
                        <% if ((keyword != null && !keyword.isEmpty()) || (type != null && !"all".equals(type)) || selectedCategoryId != null) { %>
                        <a href="<%= request.getContextPath() %>/admin/questions" class="btn btn-secondary">Xóa bộ lọc</a>
                        <% } %>
                    </form>
                </div>

                <a href="#" class="add-btn" onclick="openAddModal(); return false;">
                    <i class="fas fa-plus"></i> Thêm câu hỏi mới
                </a>

                <div class="questions-table">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Danh mục</th>
                                <th>Nội dung câu hỏi</th>
                                <th>Loại</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (questions != null && !questions.isEmpty()) { %>
                                <% for (Question q : questions) { 
                                    QuestionCategory category = null;
                                    for (QuestionCategory cat : categories) {
                                        if (cat.getCategoryId() == q.getCategoryId()) {
                                            category = cat;
                                            break;
                                        }
                                    }
                                    List<Answer> answers = answerDAO.getAnswersByQuestionId(q.getQuestionId());
                                %>
                                <tr>
                                    <td><%= q.getQuestionId() %></td>
                                    <td><%= category != null ? category.getCategoryName() : "N/A" %></td>
                                    <td class="question-text" title="<%= q.getQuestionText() %>">
                                        <%= q.getQuestionText() %>
                                    </td>
                                    <td>
                                        <% if (q.isCritical()) { %>
                                            <span class="badge badge-critical">Điểm liệt</span>
                                        <% } else { %>
                                            <span class="badge badge-normal">Thường</span>
                                        <% } %>
                                    </td>
                                    <td>
                                        <div class="action-buttons">
                                            <button class="btn btn-edit" 
                                                    data-question-id="<%= q.getQuestionId() %>"
                                                    data-category-id="<%= q.getCategoryId() %>"
                                                    data-question-text="<%= q.getQuestionText().replace("\"", "&quot;") %>"
                                                    data-question-image="<%= q.getQuestionImage() != null ? q.getQuestionImage() : "" %>"
                                                    data-explanation="<%= q.getExplanation() != null ? q.getExplanation().replace("\"", "&quot;") : "" %>"
                                                    data-is-critical="<%= q.isCritical() %>"
                                                    onclick="openEditModalFromButton(this)">
                                                <i class="fas fa-edit"></i> Sửa
                                            </button>
                                            <form method="POST" action="<%= request.getContextPath() %>/admin/questions" style="display: inline;">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="questionId" value="<%= q.getQuestionId() %>">
                                                <button type="submit" class="btn btn-delete" onclick="return confirm('Bạn có chắc muốn xóa câu hỏi này?')">
                                                    <i class="fas fa-trash"></i> Xóa
                                                </button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                                <% } %>
                            <% } else { %>
                                <tr>
                                    <td colspan="5" style="text-align: center; padding: 30px;">
                                        Không tìm thấy câu hỏi nào
                                    </td>
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
                    
                    int startIndex = (currentPage - 1) * 10 + 1;
                    int endIndex = Math.min(currentPage * 10, totalQuestions);
                    
                    String encodedKeyword = "";
                    if (keyword != null && !keyword.isEmpty()) {
                        try {
                            encodedKeyword = "&keyword=" + URLEncoder.encode(keyword, "UTF-8");
                        } catch (Exception e) {
                            encodedKeyword = "&keyword=" + keyword;
                        }
                    }
                    
                    String encodedType = "";
                    if (type != null && !"all".equals(type)) {
                        try {
                            encodedType = "&type=" + URLEncoder.encode(type, "UTF-8");
                        } catch (Exception e) {
                            encodedType = "&type=" + type;
                        }
                    }
                    
                    String encodedCategory = "";
                    if (selectedCategoryId != null) {
                        encodedCategory = "&categoryId=" + selectedCategoryId;
                    }
                %>

                <% if (totalPages > 1) { %>
                <div class="pagination">
                    <% if (currentPage > 1) { %>
                        <a href="<%= request.getContextPath() %>/admin/questions?page=<%= currentPage - 1 %><%= encodedKeyword %><%= encodedType %><%= encodedCategory %>">
                            <i class="fas fa-chevron-left"></i> Trước
                        </a>
                    <% } else { %>
                        <span class="disabled">
                            <i class="fas fa-chevron-left"></i> Trước
                        </span>
                    <% } %>
                    
                    <% if (startPage > 1) { %>
                        <a href="<%= request.getContextPath() %>/admin/questions?page=1<%= encodedKeyword %><%= encodedType %><%= encodedCategory %>">1</a>
                        <% if (showStartEllipsis) { %>
                            <span class="ellipsis">...</span>
                        <% } %>
                    <% } %>
                    
                    <% for (int i = startPage; i <= endPage; i++) { %>
                        <% if (i == currentPage) { %>
                            <span class="active"><%= i %></span>
                        <% } else { %>
                            <a href="<%= request.getContextPath() %>/admin/questions?page=<%= i %><%= encodedKeyword %><%= encodedType %><%= encodedCategory %>"><%= i %></a>
                        <% } %>
                    <% } %>
                    
                    <% if (endPage < totalPages) { %>
                        <% if (showEndEllipsis) { %>
                            <span class="ellipsis">...</span>
                        <% } %>
                        <a href="<%= request.getContextPath() %>/admin/questions?page=<%= totalPages %><%= encodedKeyword %><%= encodedType %><%= encodedCategory %>"><%= totalPages %></a>
                    <% } %>
                    
                    <% if (currentPage < totalPages) { %>
                        <a href="<%= request.getContextPath() %>/admin/questions?page=<%= currentPage + 1 %><%= encodedKeyword %><%= encodedType %><%= encodedCategory %>">
                            Sau <i class="fas fa-chevron-right"></i>
                        </a>
                    <% } else { %>
                        <span class="disabled">
                            Sau <i class="fas fa-chevron-right"></i>
                        </span>
                    <% } %>
                </div>
                <% } %>

                <div class="pagination-info">
                    <% if (totalQuestions > 0) { %>
                        Hiển thị <strong><%= startIndex %>-<%= endIndex %></strong> trong tổng số <strong><%= totalQuestions %></strong> câu hỏi
                        <% if (totalPages > 1) { %>
                            | Trang <strong><%= currentPage %>/<%= totalPages %></strong>
                        <% } %>
                    <% } else { %>
                        Không có câu hỏi nào
                    <% } %>
                </div>
            </div>
        </div>

        <div id="addModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Thêm câu hỏi mới</h2>
                    <span class="close" onclick="closeAddModal()">&times;</span>
                </div>
                <form method="POST" action="<%= request.getContextPath() %>/admin/questions">
                    <input type="hidden" name="action" value="add">
                    <div class="form-group">
                        <label>Danh mục</label>
                        <select name="categoryId" required>
                            <% for (QuestionCategory cat : categories) { %>
                            <option value="<%= cat.getCategoryId() %>"><%= cat.getCategoryName() %></option>
                            <% } %>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Nội dung câu hỏi</label>
                        <textarea name="questionText" required></textarea>
                    </div>
                    <div class="form-group">
                        <label>Hình ảnh (URL)</label>
                        <input type="text" name="questionImage" placeholder="Nhập URL hình ảnh (tùy chọn)">
                    </div>
                    <div class="form-group">
                        <label>Giải thích</label>
                        <textarea name="explanation" placeholder="Giải thích đáp án (tùy chọn)"></textarea>
                    </div>
                    <div class="form-group checkbox">
                        <input type="checkbox" name="isCritical" id="addIsCritical">
                        <label for="addIsCritical">Câu hỏi điểm liệt</label>
                    </div>
                    <hr style="margin: 20px 0;">
                    <h3 style="margin-bottom: 15px; color: #667eea;">Đáp án</h3>
                    <p style="margin-bottom: 15px; color: #666; font-size: 14px;">Chọn 1 đáp án đúng:</p>
                    <% 
                        String[] answerLabels = {"A", "B", "C", "D"};
                        for (int i = 1; i <= 4; i++) { 
                            String label = answerLabels[i - 1];
                    %>
                    <div class="answer-group">
                        <div style="display: flex; align-items: center; gap: 10px; margin-bottom: 10px;">
                            <input type="radio" name="correctAnswer" id="addIsCorrect<%= i %>" value="<%= i %>" required>
                            <label for="addIsCorrect<%= i %>" style="font-weight: 600; color: #667eea; margin: 0;">Đáp án <%= label %> (Đúng)</label>
                        </div>
                        <input type="text" name="answer<%= i %>" placeholder="Nhập đáp án <%= label %>" required>
                    </div>
                    <% } %>
                    <div class="form-actions">
                        <button type="button" class="btn-secondary" onclick="closeAddModal()">Hủy</button>
                        <button type="submit" class="btn-primary">Thêm</button>
                    </div>
                </form>
            </div>
        </div>

        <div id="editModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Sửa câu hỏi</h2>
                    <span class="close" onclick="closeEditModal()">&times;</span>
                </div>
                <form method="POST" action="<%= request.getContextPath() %>/admin/questions">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="questionId" id="editQuestionId">
                    <div class="form-group">
                        <label>Danh mục</label>
                        <select name="categoryId" id="editCategoryId" required>
                            <% for (QuestionCategory cat : categories) { %>
                            <option value="<%= cat.getCategoryId() %>"><%= cat.getCategoryName() %></option>
                            <% } %>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Nội dung câu hỏi</label>
                        <textarea name="questionText" id="editQuestionText" required></textarea>
                    </div>
                    <div class="form-group">
                        <label>Hình ảnh (URL)</label>
                        <input type="text" name="questionImage" id="editQuestionImage" placeholder="Nhập URL hình ảnh (tùy chọn)">
                    </div>
                    <div class="form-group">
                        <label>Giải thích</label>
                        <textarea name="explanation" id="editExplanation" placeholder="Giải thích đáp án (tùy chọn)"></textarea>
                    </div>
                    <div class="form-group checkbox">
                        <input type="checkbox" name="isCritical" id="editIsCritical">
                        <label for="editIsCritical">Câu hỏi điểm liệt</label>
                    </div>
                    <hr style="margin: 20px 0;">
                    <h3 style="margin-bottom: 15px; color: #667eea;">Đáp án</h3>
                    <p style="margin-bottom: 15px; color: #666; font-size: 14px;">Chọn 1 đáp án đúng:</p>
                    <div id="editAnswersContainer"></div>
                    <div class="form-actions">
                        <button type="button" class="btn-secondary" onclick="closeEditModal()">Hủy</button>
                        <button type="submit" class="btn-primary">Cập nhật</button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            function openAddModal() {
                document.getElementById('addModal').style.display = 'block';
            }

            function closeAddModal() {
                document.getElementById('addModal').style.display = 'none';
            }

            function openEditModalFromButton(button) {
                const questionId = button.getAttribute('data-question-id');
                const categoryId = button.getAttribute('data-category-id');
                const questionText = button.getAttribute('data-question-text').replace(/&quot;/g, '"');
                const questionImage = button.getAttribute('data-question-image');
                const explanation = button.getAttribute('data-explanation').replace(/&quot;/g, '"');
                const isCritical = button.getAttribute('data-is-critical') === 'true';

                document.getElementById('editQuestionId').value = questionId;
                document.getElementById('editCategoryId').value = categoryId;
                document.getElementById('editQuestionText').value = questionText;
                document.getElementById('editQuestionImage').value = questionImage || '';
                document.getElementById('editExplanation').value = explanation || '';
                document.getElementById('editIsCritical').checked = isCritical;

                loadAnswersForEdit(questionId);
                document.getElementById('editModal').style.display = 'block';
            }

            function loadAnswersForEdit(questionId) {
                const answerLabels = ['A', 'B', 'C', 'D'];
                fetch('<%= request.getContextPath() %>/admin/questions?action=getAnswers&questionId=' + questionId)
                    .then(response => response.json())
                    .then(answers => {
                        const container = document.getElementById('editAnswersContainer');
                        container.innerHTML = '';
                        let correctAnswerIndex = 0;
                        for (let i = 0; i < answers.length; i++) {
                            if (answers[i].isCorrect) {
                                correctAnswerIndex = answers[i].answerOrder;
                                break;
                            }
                        }
                        for (let i = 1; i <= 4; i++) {
                            const answer = answers.find(a => a.answerOrder === i) || { answerText: '', isCorrect: false };
                            const label = answerLabels[i - 1];
                            const answerText = (answer.answerText || '').replace(/"/g, '&quot;').replace(/'/g, '&#39;');
                            const div = document.createElement('div');
                            div.className = 'answer-group';
                            div.innerHTML = 
                                '<div style="display: flex; align-items: center; gap: 10px; margin-bottom: 10px;">' +
                                    '<input type="radio" name="correctAnswer" id="editIsCorrect' + i + '" value="' + i + '"' + (answer.isCorrect ? ' checked' : '') + ' required>' +
                                    '<label for="editIsCorrect' + i + '" style="font-weight: 600; color: #667eea; margin: 0;">Đáp án ' + label + ' (Đúng)</label>' +
                                '</div>' +
                                '<input type="text" name="answer' + i + '" value="' + answerText + '" placeholder="Nhập đáp án ' + label + '" required>';
                            container.appendChild(div);
                        }
                    })
                    .catch(error => {
                        console.error('Error loading answers:', error);
                        const container = document.getElementById('editAnswersContainer');
                        container.innerHTML = '';
                        for (let i = 1; i <= 4; i++) {
                            const label = answerLabels[i - 1];
                            const div = document.createElement('div');
                            div.className = 'answer-group';
                            div.innerHTML = 
                                '<div style="display: flex; align-items: center; gap: 10px; margin-bottom: 10px;">' +
                                    '<input type="radio" name="correctAnswer" id="editIsCorrect' + i + '" value="' + i + '" required>' +
                                    '<label for="editIsCorrect' + i + '" style="font-weight: 600; color: #667eea; margin: 0;">Đáp án ' + label + ' (Đúng)</label>' +
                                '</div>' +
                                '<input type="text" name="answer' + i + '" placeholder="Nhập đáp án ' + label + '" required>';
                            container.appendChild(div);
                        }
                    });
            }

            function closeEditModal() {
                document.getElementById('editModal').style.display = 'none';
            }

            window.onclick = function(event) {
                const addModal = document.getElementById('addModal');
                const editModal = document.getElementById('editModal');
                if (event.target == addModal) {
                    addModal.style.display = 'none';
                }
                if (event.target == editModal) {
                    editModal.style.display = 'none';
                }
            }
            
            function handleSearch(event) {
                const keyword = document.getElementById('searchKeyword').value.trim();
                const type = document.getElementById('typeFilter').value;
                const category = document.getElementById('categoryFilter').value;
                if (keyword === '' && (type === 'all' || type === '') && category === '') {
                    window.location.href = '<%= request.getContextPath() %>/admin/questions';
                    return false;
                }
                return true;
            }
        </script>
    </body>
</html>

