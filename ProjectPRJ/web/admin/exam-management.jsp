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
        <title>Quản lý Đề thi - Admin</title>
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
            .alert { padding: 15px; border-radius: 5px; margin-bottom: 20px; }
            .alert-success { background: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
            .alert-error { background: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
            .actions { margin-bottom: 20px; display: flex; justify-content: flex-end; }
            .add-btn { background: #4caf50; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; text-decoration: none; display: inline-block; }
            .add-btn:hover { background: #388e3c; }
            .exam-table { background: white; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
            table { width: 100%; border-collapse: collapse; }
            thead { background: #667eea; color: white; }
            th, td { padding: 15px; text-align: left; }
            tbody tr { border-bottom: 1px solid #e0e0e0; }
            tbody tr:hover { background: #f9f9f9; }
            .pagination { display: flex; justify-content: center; align-items: center; gap: 5px; margin-top: 20px; flex-wrap: wrap; }
            .pagination a, .pagination span { padding: 8px 12px; border: 1px solid #e0e0e0; border-radius: 5px; text-decoration: none; color: #333; min-width: 40px; text-align: center; transition: all 0.3s; }
            .pagination a:hover:not(.disabled) { background: #667eea; color: white; border-color: #667eea; }
            .pagination .active { background: #667eea; color: white; border-color: #667eea; cursor: default; font-weight: 600; }
            .pagination .disabled { opacity: 0.5; cursor: not-allowed; pointer-events: none; }
            .pagination .ellipsis { border: none; cursor: default; padding: 8px 5px; }
            .pagination-info { text-align: center; margin-top: 15px; color: #666; font-size: 14px; }
            .pagination-info strong { color: #667eea; }
            .modal { display: none; position: fixed; z-index: 1000; left: 0; top: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); overflow-y: auto; }
            .modal-content { background: white; margin: 4% auto; padding: 30px; border-radius: 10px; width: 90%; max-width: 520px; }
            .modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
            .modal-header h2 { color: #667eea; }
            .close { font-size: 28px; font-weight: bold; cursor: pointer; color: #999; }
            .close:hover { color: #333; }
            .form-group { margin-bottom: 15px; }
            .form-group label { display: block; margin-bottom: 5px; color: #333; font-weight: 500; }
            .form-group input { width: 100%; padding: 10px; border: 2px solid #e0e0e0; border-radius: 5px; font-size: 14px; }
            .form-group input:focus { outline: none; border-color: #667eea; }
            .form-actions { display: flex; gap: 10px; justify-content: flex-end; margin-top: 20px; }
            .btn-primary { background: #667eea; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; }
            .btn-primary:hover { background: #5568d3; }
            .btn-secondary { background: #999; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; }
            .btn-secondary:hover { background: #777; }
        </style>
    </head>
    <body>
        <%
            User user = (User) session.getAttribute("user");
            if (user == null || !user.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            List<ExamSet> examSets = (List<ExamSet>) request.getAttribute("examSets");
            List<QuestionCategory> categories = (List<QuestionCategory>) request.getAttribute("categories");
            int currentPage = request.getAttribute("currentPage") != null ? (Integer) request.getAttribute("currentPage") : 1;
            int totalPages = request.getAttribute("totalPages") != null ? (Integer) request.getAttribute("totalPages") : 1;
            int totalExamSets = request.getAttribute("totalExamSets") != null ? (Integer) request.getAttribute("totalExamSets") : 0;
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
                <a href="<%= request.getContextPath() %>/admin/exams" class="menu-item active">
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
                    <h1>Quản lý Đề thi</h1>
                    <div class="user-info">
                        <span>Xin chào, <strong><%= user.getUsername() %></strong></span>
                        <a href="<%= request.getContextPath() %>/logout" class="logout-btn">Đăng xuất</a>
                    </div>
                </div>
            </div>

            <div class="container">
                <% if (request.getAttribute("success") != null) { %>
                <div class="alert alert-success"><%= request.getAttribute("success") %></div>
                <% } %>
                <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-error"><%= request.getAttribute("error") %></div>
                <% } %>

                <div class="actions">
                    <a href="#" class="add-btn" onclick="openAddModal(); return false;">
                        <i class="fas fa-plus"></i> Tạo đề thi mới
                    </a>
                </div>

                <div class="exam-table">
                    <table>
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Tên đề thi</th>
                                <th>Số câu</th>
                                <th>Thời gian (phút)</th>
                                <th>Điểm đạt (số câu đúng)</th>
                                <th>Loại đề (Danh mục)</th>
                                <th>Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% if (examSets != null && !examSets.isEmpty()) { %>
                                <% for (ExamSet exam : examSets) { %>
                                <tr>
                                    <td><%= exam.getExamSetId() %></td>
                                    <td><%= exam.getExamName() %></td>
                                    <td><%= exam.getTotalQuestions() %></td>
                                    <td><%= exam.getDurationMinutes() %></td>
                                    <td><%= exam.getPassingScore() %></td>
                                    <td><%= exam.getCategoryName() != null ? exam.getCategoryName() : "Chưa gán" %></td>
                                    <td>
                                        <a href="<%= request.getContextPath() %>/admin/exams?action=view&examSetId=<%= exam.getExamSetId() %>" class="btn btn-secondary" style="padding: 6px 10px;">Xem đề</a>
                                        <form method="POST" action="<%= request.getContextPath() %>/admin/exams" style="display:inline;">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="examSetId" value="<%= exam.getExamSetId() %>">
                                            <button type="submit" class="btn btn-delete" style="padding: 6px 10px; background:#f44336; color:white; border:none; border-radius:4px; cursor:pointer;" onclick="return confirm('Bạn có chắc muốn xóa đề thi này?');">
                                                Xóa
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                                <% } %>
                            <% } else { %>
                                <tr>
                                    <td colspan="7" style="text-align: center; padding: 30px;">Chưa có đề thi nào</td>
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
                        <a href="<%= request.getContextPath() %>/admin/exams?page=<%= currentPage - 1 %>">
                            <i class="fas fa-chevron-left"></i> Trước
                        </a>
                    <% } else { %>
                        <span class="disabled"><i class="fas fa-chevron-left"></i> Trước</span>
                    <% } %>

                    <% if (startPage > 1) { %>
                        <a href="<%= request.getContextPath() %>/admin/exams?page=1">1</a>
                        <% if (showStartEllipsis) { %><span class="ellipsis">...</span><% } %>
                    <% } %>

                    <% for (int i = startPage; i <= endPage; i++) { %>
                        <% if (i == currentPage) { %>
                            <span class="active"><%= i %></span>
                        <% } else { %>
                            <a href="<%= request.getContextPath() %>/admin/exams?page=<%= i %>"><%= i %></a>
                        <% } %>
                    <% } %>

                    <% if (endPage < totalPages) { %>
                        <% if (showEndEllipsis) { %><span class="ellipsis">...</span><% } %>
                        <a href="<%= request.getContextPath() %>/admin/exams?page=<%= totalPages %>"><%= totalPages %></a>
                    <% } %>

                    <% if (currentPage < totalPages) { %>
                        <a href="<%= request.getContextPath() %>/admin/exams?page=<%= currentPage + 1 %>">
                            Sau <i class="fas fa-chevron-right"></i>
                        </a>
                    <% } else { %>
                        <span class="disabled">Sau <i class="fas fa-chevron-right"></i></span>
                    <% } %>
                </div>
                <% } %>

                <div class="pagination-info">
                    <% if (totalExamSets > 0) { %>
                        Hiển thị <strong><%= ((currentPage - 1) * 10) + 1 %>-<%= Math.min(currentPage * 10, totalExamSets) %></strong> trong tổng số <strong><%= totalExamSets %></strong> đề thi
                        <% if (totalPages > 1) { %> | Trang <strong><%= currentPage %>/<%= totalPages %></strong> <% } %>
                    <% } else { %>
                        Không có đề thi nào
                    <% } %>
                </div>
            </div>
        </div>

        <div id="addModal" class="modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h2>Tạo đề thi mới</h2>
                    <span class="close" onclick="closeAddModal()">&times;</span>
                </div>
                <form method="POST" action="<%= request.getContextPath() %>/admin/exams" onsubmit="return validateForm();">
                    <input type="hidden" name="action" value="add">
                    <div class="form-group">
                        <label>Loại đề (Danh mục)</label>
                        <select name="categoryId" id="categoryId" required>
                            <option value="">-- Chọn danh mục --</option>
                            <% if (categories != null) { 
                                   for (QuestionCategory cat : categories) { %>
                                <option value="<%= cat.getCategoryId() %>"><%= cat.getCategoryName() %></option>
                            <%   } 
                               } %>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Tên đề thi</label>
                        <input type="text" name="examName" id="examName" required>
                    </div>
                    <div class="form-group">
                        <label>Tổng số câu hỏi</label>
                        <input type="number" name="totalQuestions" id="totalQuestions" min="1" required>
                    </div>
                    <div class="form-group">
                        <label>Thời gian làm bài (phút)</label>
                        <input type="number" name="durationMinutes" id="durationMinutes" min="1" required>
                    </div>
                    <div class="form-group">
                        <label>Điểm đạt (số câu đúng tối thiểu)</label>
                        <input type="number" name="passingScore" id="passingScore" min="0" required>
                    </div>
                    <div class="form-actions">
                        <button type="button" class="btn-secondary" onclick="closeAddModal()">Hủy</button>
                        <button type="submit" class="btn-primary">Tạo</button>
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
            window.onclick = function(event) {
                const addModal = document.getElementById('addModal');
                if (event.target == addModal) {
                    addModal.style.display = 'none';
                }
            }
            function validateForm() {
                const name = document.getElementById('examName').value.trim();
                const total = parseInt(document.getElementById('totalQuestions').value, 10);
                const duration = parseInt(document.getElementById('durationMinutes').value, 10);
                const pass = parseInt(document.getElementById('passingScore').value, 10);
                const category = document.getElementById('categoryId').value;
                if (!category) {
                    alert('Vui lòng chọn loại đề (danh mục).');
                    return false;
                }
                if (!name) {
                    alert('Tên đề thi không được để trống.');
                    return false;
                }
                if (isNaN(total) || total <= 0 || isNaN(duration) || duration <= 0 || isNaN(pass) || pass < 0) {
                    alert('Vui lòng nhập số hợp lệ cho tổng số câu, thời gian và điểm đạt.');
                    return false;
                }
                if (pass > total) {
                    alert('Điểm đạt không được lớn hơn tổng số câu hỏi.');
                    return false;
                }
                return true;
            }
        </script>
    </body>
</html>


