<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Đăng ký</title>
<style>
  body { font-family: Arial, sans-serif; background:#f2f4f7; }
  .box { width:380px; margin:60px auto; background:#fff; padding:28px;
         border:1px solid #ddd; border-radius:6px; }
  h2 { text-align:center; color:#555; font-weight:normal; }
  input[type=text], input[type=password] { width:100%; padding:10px; margin:8px 0;
         box-sizing:border-box; border:1px solid #ccc; }
  button { width:100%; padding:10px; background:#009ee0; color:#fff; border:0; cursor:pointer; margin-top:8px; }
  .alert { color:#a94442; background:#f2dede; padding:8px; margin-bottom:8px; }
</style>
</head>
<body>
  <div class="box">
    <h2>Tạo tài khoản mới</h2>

    <c:if test="${alert != null}">
      <div class="alert">${alert}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/register" method="post">
      <input type="text" name="username" placeholder="Tài khoản">
      <input type="text" name="fullname" placeholder="Họ tên">
      <input type="text" name="email" placeholder="Nhập Email">
      <input type="text" name="phone" placeholder="Số điện thoại">
      <input type="password" name="password" placeholder="Mật khẩu">
      <input type="password" name="repassword" placeholder="Nhập lại mật khẩu">
      <button type="submit">Tạo tài khoản</button>
    </form>

    <p style="text-align:center; font-size:14px;">
      Nếu bạn đã có tài khoản?
      <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
    </p>
  </div>
</body>
</html>
