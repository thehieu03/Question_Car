<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.User"%>
<%@page import="model.QuestionCategory"%>
<%@page import="java.util.List"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Quản lý Danh mục - Admin</title>
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

            .categories-table {
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

            .form-group input {
                width: 100%;
                padding: 10px;
                border: 2px solid #e0e0e0;
                border-radius: 5px;
                font-size: 14px;
            }

            .form-group input:focus {
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

            .total-info {
                margin-top: 20px;
                text-align: center;
                color: #666;
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
            
            List<QuestionCategory> categories = (List<QuestionCategory>) request.getAttribute("categories");
            int totalCategories = request.getAttribute("totalCategories") != null ? (Integer) request.getAttribute("totalCategories") : 0;
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
                <a href="<%= request.getContextPath() %>/admin/categories" class="menu-item active">
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
                    <h1>Quản lý Danh mục</h1>
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

                <a href="#" class="add-btn" onclick="openAddModal(); return false;">
                    <i class="fas fa-plus"></i> Thêm danh mục mới
                </a>

                <div class="categories-table">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tên danh mục</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (categories != null && !categories.isEmpty()) { %>
                                <% for (QuestionCategory cat : categories) { %>
                                <tr>
                                    <td><%= cat.getCategoryId() %></td>
                                    <td><%= cat.getCategoryName() %></td>
                                    <td>
                                        <div class="action-buttons">
                                            <button class="btn btn-edit" 
                                                    data-category-id="<%= cat.getCategoryId() %>"
                                                    data-category-name="<%= cat.getCategoryName() %>"
                                                    onclick="openEditModalFromButton(this)">
                                                <i class="fas fa-edit"></i> Sửa
                                            </button>
                                            <form method="POST" action="<%= request.getContextPath() %>/admin/categories" style="display: inline;">
                                                <input type="hidden" name="action" value="delete">
                                                <input type="hidden" name="categoryId" value="<%= cat.getCategoryId() %>">
                                                <button type="submit" class="btn btn-delete" onclick="return confirm('Bạn có chắc muốn xóa danh mục này?')">
                                                    <i class="fas fa-trash"></i> Xóa
                                                </button>
                                            </form>
                                        </div>
                                    </td>
                                </tr>
                                <% } %>
                            <% } else { %>
                                <tr>
                                    <td colspan="3" style="text-align: center; padding: 30px;">
                                        Không có danh mục nào
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>

                <div class="total-info">
                    Tổng số: <strong><%= totalCategories %></strong> danh mục
                </div>
            </div>
        </div>

        <div id="addModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Thêm danh mục mới</h2>
                    <span class="close" onclick="closeAddModal()">&times;</span>
                </div>
                <form method="POST" action="<%= request.getContextPath() %>/admin/categories">
                    <input type="hidden" name="action" value="add">
                    <div class="form-group">
                        <label>Tên danh mục</label>
                        <input type="text" name="categoryName" required placeholder="Nhập tên danh mục">
                    </div>
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
                    <h2>Sửa danh mục</h2>
                    <span class="close" onclick="closeEditModal()">&times;</span>
                </div>
                <form method="POST" action="<%= request.getContextPath() %>/admin/categories">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="categoryId" id="editCategoryId">
                    <div class="form-group">
                        <label>Tên danh mục</label>
                        <input type="text" name="categoryName" id="editCategoryName" required placeholder="Nhập tên danh mục">
                    </div>
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
                const categoryId = button.getAttribute('data-category-id');
                const categoryName = button.getAttribute('data-category-name');
                
                document.getElementById('editCategoryId').value = categoryId;
                document.getElementById('editCategoryName').value = categoryName;
                document.getElementById('editModal').style.display = 'block';
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
        </script>
    </body>
</html>

