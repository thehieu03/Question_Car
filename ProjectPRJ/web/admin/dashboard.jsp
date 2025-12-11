<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="model.User"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Admin Dashboard - Hệ Thống Ôn Thi Bằng Lái Xe</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" integrity="sha512-iecdLmaskl7CVkqkXNQ/ZH/XLlvWZOJyj7Yy7tcenmpD1ypASozpmT/E0iPtmFIB46ZmdtAc9eNBvH0H/ZpiBw==" crossorigin="anonymous" referrerpolicy="no-referrer" />
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

            .welcome-card {
                background: white;
                border-radius: 10px;
                padding: 30px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                margin-bottom: 30px;
            }

            .welcome-card h2 {
                color: #333;
                margin-bottom: 10px;
            }

            .welcome-card p {
                color: #666;
                font-size: 16px;
            }

            .stats-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                gap: 20px;
                margin-bottom: 30px;
            }

            .stat-card {
                background: white;
                border-radius: 10px;
                padding: 25px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                text-align: center;
            }

            .stat-card h3 {
                color: #666;
                font-size: 14px;
                margin-bottom: 10px;
                text-transform: uppercase;
            }

            .stat-card .number {
                font-size: 36px;
                font-weight: bold;
                color: #667eea;
                margin-bottom: 5px;
            }

            .stat-card .label {
                color: #999;
                font-size: 12px;
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
        %>

        <div class="sidebar">
            <div class="sidebar-header">
                <h2>Admin Panel</h2>
                <p>Quản trị hệ thống</p>
            </div>
            <nav class="sidebar-menu">
                <a href="<%= request.getContextPath() %>/admin" class="menu-item active">
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
                    <h1>Dashboard Thống kê</h1>
                    <div class="user-info">
                        <span>Xin chào, <strong><%= user.getUsername() %></strong></span>
                        <a href="<%= request.getContextPath() %>/logout" class="logout-btn">Đăng xuất</a>
                    </div>
                </div>
            </div>

            <div class="container">
                <div class="welcome-card">
                    <h2>Chào mừng đến với Trang Quản Trị</h2>
                    <p>Quản lý hệ thống ôn thi bằng lái xe</p>
                </div>

                <div class="stats-grid">
                    <div class="stat-card">
                        <h3>Tổng số người dùng</h3>
                        <div class="number"><%= request.getAttribute("totalUsers") != null ? request.getAttribute("totalUsers") : 0 %></div>
                        <div class="label">Users</div>
                    </div>
                    <div class="stat-card">
                        <h3>Tổng số câu hỏi</h3>
                        <div class="number"><%= request.getAttribute("totalQuestions") != null ? request.getAttribute("totalQuestions") : 0 %></div>
                        <div class="label">Questions</div>
                    </div>
                    <div class="stat-card">
                        <h3>Tổng số đề thi</h3>
                        <div class="number"><%= request.getAttribute("totalExamSets") != null ? request.getAttribute("totalExamSets") : 0 %></div>
                        <div class="label">Exam Sets</div>
                    </div>
                    <div class="stat-card">
                        <h3>Bài thi đã làm</h3>
                        <div class="number"><%= request.getAttribute("totalUserExams") != null ? request.getAttribute("totalUserExams") : 0 %></div>
                        <div class="label">Exams Taken</div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>

