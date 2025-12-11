<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Đăng Nhập - Hệ Thống Ôn Thi Bằng Lái Xe</title>
    <style>
      * {
        margin: 0;
        padding: 0;
        box-sizing: border-box;
      }

      body {
        font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        min-height: 100vh;
        display: flex;
        justify-content: center;
        align-items: center;
        padding: 20px;
      }

      .login-container {
        background: white;
        border-radius: 20px;
        box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
        padding: 40px;
        width: 100%;
        max-width: 400px;
      }

      .login-header {
        text-align: center;
        margin-bottom: 30px;
      }

      .login-header h1 {
        color: #333;
        font-size: 28px;
        margin-bottom: 10px;
      }

      .login-header p {
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

      .btn-login {
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

      .btn-login:hover {
        transform: translateY(-2px);
        box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
      }

      .btn-login:active {
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

      .success-message {
        background: #efe;
        color: #3c3;
        padding: 12px;
        border-radius: 8px;
        margin-bottom: 20px;
        border-left: 4px solid #3c3;
        font-size: 14px;
      }

      .register-link {
        text-align: center;
        margin-top: 20px;
        padding-top: 20px;
        border-top: 1px solid #e0e0e0;
      }

      .register-link a {
        color: #667eea;
        text-decoration: none;
        font-weight: 500;
      }

      .register-link a:hover {
        text-decoration: underline;
      }
    </style>
  </head>
  <body>
    <div class="login-container">
      <div class="login-header">
        <h1>Đăng Nhập</h1>
        <p>Hệ Thống Ôn Thi Bằng Lái Xe</p>
      </div>

      <% if (request.getAttribute("error") != null) { %>
      <div class="error-message"><%= request.getAttribute("error")%></div>
      <% } %>
      
      <% if (request.getAttribute("success") != null) { %>
      <div class="success-message"><%= request.getAttribute("success")%></div>
      <% } %>

      <form action="login" method="POST">
        <div class="form-group">
          <label for="username">Tên đăng nhập</label>
          <input type="text" id="username" name="username" required
          placeholder="Nhập tên đăng nhập" value="<%= 
          request.getAttribute("username") != null ? request.getAttribute("username") : 
          (request.getParameter("username") != null ? request.getParameter("username") : "")%>">
        </div>

        <div class="form-group">
          <label for="password">Mật khẩu</label>
          <input
            type="password"
            id="password"
            name="password"
            required
            placeholder="Nhập mật khẩu"
          />
        </div>

        <button type="submit" class="btn-login">Đăng Nhập</button>
      </form>

      <div class="register-link">
        <p>Chưa có tài khoản? <a href="register">Đăng ký ngay</a></p>
      </div>
    </div>
  </body>
</html>
