<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.User"%>
<%@page import="java.util.List"%>
<%@page import="java.net.URLEncoder"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý Người dùng - Admin</title>
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

            .users-table {
                background: white;
                border-radius: 10px;
                overflow: hidden;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
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

            .badge {
                padding: 5px 10px;
                border-radius: 5px;
                font-size: 12px;
                font-weight: 600;
            }

            .badge-admin {
                background: #667eea;
                color: white;
            }

            .badge-user {
                background: #4caf50;
                color: white;
            }

            .badge-banned {
                background: #f44336;
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
                background: #d32f2f;
                color: white;
            }

            .btn-delete:hover {
                background: #b71c1c;
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
            }

            .modal-content {
                background: white;
                margin: 5% auto;
                padding: 30px;
                border-radius: 10px;
                width: 90%;
                max-width: 500px;
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

            .form-group input, .form-group select {
                width: 100%;
                padding: 10px;
                border: 2px solid #e0e0e0;
                border-radius: 5px;
                font-size: 14px;
            }

            .form-group input:focus, .form-group select:focus {
                outline: none;
                border-color: #667eea;
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
            
            List<User> users = (List<User>) request.getAttribute("users");
            int currentPage = request.getAttribute("currentPage") != null ? (Integer) request.getAttribute("currentPage") : 1;
            int totalPages = request.getAttribute("totalPages") != null ? (Integer) request.getAttribute("totalPages") : 1;
            int totalUsers = request.getAttribute("totalUsers") != null ? (Integer) request.getAttribute("totalUsers") : 0;
            String keyword = (String) request.getAttribute("keyword");
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
                <a href="<%= request.getContextPath() %>/admin/users" class="menu-item active">
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
                <a href="<%= request.getContextPath() %>/admin/exam-results" class="menu-item">
                    <i class="icon fas fa-clipboard-list"></i>
                    Quản lý Bài thi đã làm
                </a>
            </nav>
        </div>

        <div class="main-content">
            <div class="header">
                <div class="header-content">
                    <h1>Quản lý Người dùng</h1>
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
                    <form method="GET" action="<%= request.getContextPath() %>/admin/users" class="search-form" onsubmit="return handleSearch(event)">
                        <input type="text" name="keyword" id="searchKeyword" placeholder="Tìm kiếm theo tên đăng nhập hoặc email..." 
                               value="<%= keyword != null ? keyword : "" %>">
                        <button type="submit"><i class="fas fa-search"></i> Tìm kiếm</button>
                        <% if (keyword != null && !keyword.isEmpty()) { %>
                        <a href="<%= request.getContextPath() %>/admin/users" class="btn btn-secondary">Xóa bộ lọc</a>
                        <% } %>
                    </form>
                </div>

                <div class="users-table">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tên đăng nhập</th>
                                <th>Email</th>
                                <th>Vai trò</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (users != null && !users.isEmpty()) { %>
                                <% for (User u : users) { %>
                                <tr>
                                    <td><%= u.getUserId() %></td>
                                    <td><%= u.getUsername() %></td>
                                    <td><%= u.getEmail() %></td>
                                    <td>
                                        <% if (u.getRole() == 1) { %>
                                            <span class="badge badge-admin">Admin</span>
                                        <% } else if (u.getRole() == -1) { %>
                                            <span class="badge badge-banned">Đã cấm</span>
                                        <% } else { %>
                                            <span class="badge badge-user">User</span>
                                        <% } %>
                                    </td>
                                    <td>
                                        <div class="action-buttons">
                                            <button class="btn btn-edit" 
                                                    data-user-id="<%= u.getUserId() %>"
                                                    data-username="<%= u.getUsername() %>"
                                                    data-email="<%= u.getEmail() %>"
                                                    data-role="<%= u.getRole() %>"
                                                    onclick="openEditModalFromButton(this)">
                                                <i class="fas fa-edit"></i> Sửa
                                            </button>
                                            <% if (u.getRole() != 1) { %>
                                            <form method="POST" action="<%= request.getContextPath() %>/admin/users" style="display: inline;">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="userId" value="<%= u.getUserId() %>">
                                                <button type="submit" class="btn btn-delete" onclick="return confirmDelete('<%= u.getUsername() %>')">
                                                    <i class="fas fa-trash"></i> Xóa
                                                </button>
                                            </form>
                                            <% } %>
                                        </div>
                                    </td>
                                </tr>
                                <% } %>
                            <% } else { %>
                                <tr>
                                    <td colspan="5" style="text-align: center; padding: 30px;">
                                        Không tìm thấy người dùng nào
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
                    
                    int startIndex = (currentPage - 1) * 5 + 1;
                    int endIndex = Math.min(currentPage * 5, totalUsers);
                    
                    String encodedKeyword = "";
                    if (keyword != null && !keyword.isEmpty()) {
                        try {
                            encodedKeyword = "&keyword=" + URLEncoder.encode(keyword, "UTF-8");
                        } catch (Exception e) {
                            encodedKeyword = "&keyword=" + keyword;
                        }
                    }
                %>

                <% if (totalPages > 1) { %>
                <div class="pagination">
                    <% if (currentPage > 1) { %>
                        <a href="<%= request.getContextPath() %>/admin/users?page=<%= currentPage - 1 %><%= encodedKeyword %>">
                            <i class="fas fa-chevron-left"></i> Trước
                        </a>
                    <% } else { %>
                        <span class="disabled">
                            <i class="fas fa-chevron-left"></i> Trước
                        </span>
                    <% } %>
                    
                    <% if (startPage > 1) { %>
                        <a href="<%= request.getContextPath() %>/admin/users?page=1<%= encodedKeyword %>">1</a>
                        <% if (showStartEllipsis) { %>
                            <span class="ellipsis">...</span>
                        <% } %>
                    <% } %>
                    
                    <% for (int i = startPage; i <= endPage; i++) { %>
                        <% if (i == currentPage) { %>
                            <span class="active"><%= i %></span>
                        <% } else { %>
                            <a href="<%= request.getContextPath() %>/admin/users?page=<%= i %><%= encodedKeyword %>"><%= i %></a>
                        <% } %>
                    <% } %>
                    
                    <% if (endPage < totalPages) { %>
                        <% if (showEndEllipsis) { %>
                            <span class="ellipsis">...</span>
                        <% } %>
                        <a href="<%= request.getContextPath() %>/admin/users?page=<%= totalPages %><%= encodedKeyword %>"><%= totalPages %></a>
                    <% } %>
                    
                    <% if (currentPage < totalPages) { %>
                        <a href="<%= request.getContextPath() %>/admin/users?page=<%= currentPage + 1 %><%= encodedKeyword %>">
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
                    <% if (totalUsers > 0) { %>
                        Hiển thị <strong><%= startIndex %>-<%= endIndex %></strong> trong tổng số <strong><%= totalUsers %></strong> người dùng
                        <% if (totalPages > 1) { %>
                            | Trang <strong><%= currentPage %>/<%= totalPages %></strong>
                        <% } %>
                    <% } else { %>
                        Không có người dùng nào
                    <% } %>
                </div>
            </div>
        </div>

        <div id="editModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Sửa thông tin người dùng</h2>
                    <span class="close" onclick="closeEditModal()">&times;</span>
                </div>
                <form method="POST" action="<%= request.getContextPath() %>/admin/users">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="userId" id="editUserId">
                    <div class="form-group">
                        <label>Tên đăng nhập</label>
                        <input type="text" name="username" id="editUsername" required>
                    </div>
                    <div class="form-group">
                        <label>Email</label>
                        <input type="email" name="email" id="editEmail" required>
                    </div>
                    <div class="form-group">
                        <label>Vai trò</label>
                        <select name="role" id="editRole">
                            <option value="0">User</option>
                            <option value="1">Admin</option>
                        </select>
                    </div>
                    <div class="form-actions">
                        <button type="button" class="btn-secondary" onclick="closeEditModal()">Hủy</button>
                        <button type="submit" class="btn-primary">Cập nhật</button>
                    </div>
                </form>
            </div>
        </div>

        <script>
            function openEditModal(userId, username, email, role) {
                document.getElementById('editUserId').value = userId;
                document.getElementById('editUsername').value = username;
                document.getElementById('editEmail').value = email;
                document.getElementById('editRole').value = role;
                document.getElementById('editModal').style.display = 'block';
            }
            
            function openEditModalFromButton(button) {
                var userId = button.getAttribute('data-user-id');
                var username = button.getAttribute('data-username');
                var email = button.getAttribute('data-email');
                var role = button.getAttribute('data-role');
                openEditModal(userId, username, email, role);
            }

            function closeEditModal() {
                document.getElementById('editModal').style.display = 'none';
            }

            window.onclick = function(event) {
                const modal = document.getElementById('editModal');
                if (event.target == modal) {
                    modal.style.display = 'none';
                }
            }
            
            function handleSearch(event) {
                const keyword = document.getElementById('searchKeyword').value.trim();
                if (keyword === '') {
                    window.location.href = '<%= request.getContextPath() %>/admin/users';
                    return false;
                }
                return true;
            }

            function confirmDelete(username) {
                return confirm('Bạn có chắc chắn muốn XÓA vĩnh viễn người dùng "' + username + '"?\n\n' +
                    'Hành động này sẽ xóa:\n' +
                    '- Tất cả bài thi đã làm\n' +
                    '- Tất cả câu trả lời\n' +
                    '- Tất cả bình luận\n' +
                    '- Tài khoản người dùng\n\n' +
                    'Hành động này KHÔNG THỂ hoàn tác!');
            }
        </script>
    </body>
</html>

