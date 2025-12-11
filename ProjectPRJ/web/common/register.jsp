<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Đăng Ký - Hệ Thống Ôn Thi Bằng Lái Xe</title>
        <style>
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                min-height: 100vh;
                display: flex;
                justify-content: center;
                align-items: center;
                padding: 20px;
            }

            .register-container {
                background: white;
                border-radius: 20px;
                box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
                padding: 40px;
                width: 100%;
                max-width: 450px;
            }

            .register-header {
                text-align: center;
                margin-bottom: 30px;
            }

            .register-header h1 {
                color: #333;
                font-size: 28px;
                margin-bottom: 10px;
            }

            .register-header p {
                color: #666;
                font-size: 14px;
            }

            .form-group {
                margin-bottom: 20px;
            }

            .form-group label {
                display: block;
                margin-bottom: 8px;
                color: #333;
                font-weight: 500;
                font-size: 14px;
            }

            .form-group input {
                width: 100%;
                padding: 12px 15px;
                border: 2px solid #e0e0e0;
                border-radius: 8px;
                font-size: 14px;
                transition: border-color 0.3s;
            }

            .form-group input:focus {
                outline: none;
                border-color: #667eea;
            }

            .btn-register {
                width: 100%;
                padding: 12px;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                border: none;
                border-radius: 8px;
                font-size: 16px;
                font-weight: 600;
                cursor: pointer;
                transition: transform 0.2s, box-shadow 0.2s;
                margin-top: 10px;
            }

            .btn-register:hover {
                transform: translateY(-2px);
                box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
            }

            .btn-register:active {
                transform: translateY(0);
            }

            .error-message {
                background: #fee;
                color: #c33;
                padding: 12px;
                border-radius: 8px;
                margin-bottom: 20px;
                border-left: 4px solid #c33;
                font-size: 14px;
            }

            .login-link {
                text-align: center;
                margin-top: 20px;
                padding-top: 20px;
                border-top: 1px solid #e0e0e0;
            }

            .login-link a {
                color: #667eea;
                text-decoration: none;
                font-weight: 500;
            }

            .login-link a:hover {
                text-decoration: underline;
            }
        </style>
    </head>
    <body>
        <div class="register-container">
            <div class="register-header">
                <h1>Đăng Ký</h1>
                <p>Tạo tài khoản mới</p>
            </div>

            <% if (request.getAttribute("error") != null) { %>
            <div class="error-message">
                <%= request.getAttribute("error")%>
            </div>
            <% } %>

            <form action="register" method="POST">
                <div class="form-group">
                    <label for="username">Tên đăng nhập</label>
                    <input type="text" id="username" name="username" required 
                           placeholder="Nhập tên đăng nhập" 
                           value="<%= request.getAttribute("username") != null ? request.getAttribute("username") : (request.getParameter("username") != null ? request.getParameter("username") : "")%>">
                </div>

                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" required 
                           placeholder="Nhập email" 
                           value="<%= request.getAttribute("email") != null ? request.getAttribute("email") : (request.getParameter("email") != null ? request.getParameter("email") : "")%>">
                </div>

                <div class="form-group">
                    <label for="password">Mật khẩu</label>
                    <input type="password" id="password" name="password" required 
                           placeholder="Nhập mật khẩu" minlength="6">
                </div>

                <div class="form-group">
                    <label for="confirmPassword">Xác nhận mật khẩu</label>
                    <input type="password" id="confirmPassword" name="confirmPassword" required 
                           placeholder="Nhập lại mật khẩu" minlength="6">
                </div>

                <button type="submit" class="btn-register">Đăng Ký</button>
            </form>

            <div class="login-link">
                <p>Đã có tài khoản? <a href="login">Đăng nhập</a></p>
            </div>
        </div>
    </body>
</html>

