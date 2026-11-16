<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Đăng nhập</title>
<style>
  body { font-family: Arial, sans-serif; background:#f2f4f7; }
  .box { width:360px; margin:80px auto; background:#fff; padding:28px;
         border:1px solid #ddd; border-radius:6px; }
  h2 { text-align:center; color:#555; font-weight:normal; }
  input[type=text], input[type=password] { width:100%; padding:10px; margin:8px 0;
         box-sizing:border-box; border:1px solid #ccc; }
  button { width:100%; padding:10px; background:#009ee0; color:#fff; border:0; cursor:pointer; }
  .alert { color:#a94442; background:#f2dede; padding:8px; margin-bottom:8px; }
  .success { color:#3c763d; background:#dff0d8; padding:8px; margin-bottom:8px; }
  .remember { font-size:14px; color:#666; margin:6px 0 12px; }
</style>
</head>
<body>
  <div class="box">
    <h2>Đăng Nhập Vào Hệ Thống</h2>

    <c:if test="${success != null}">
      <div class="success"><c:out value="${success}"/></div>
    </c:if>
    <c:if test="${alert != null}">
      <div class="alert">
        <c:out value="${alert}"/>
        <c:if test="${needVerify}">
          <div style="margin-top:6px;">
            <a href="${pageContext.request.contextPath}/verify-otp">Xác thực OTP ngay</a>
          </div>
        </c:if>
      </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="post">
      <input type="text" name="username" placeholder="Tài khoản">
      <input type="password" name="password" placeholder="Mật khẩu">
      <div class="remember" style="display:flex; justify-content:space-between; align-items:center;">
        <label><input type="checkbox" name="remember"> Nhớ tôi</label>
        <a href="${pageContext.request.contextPath}/forgot-password">Quên mật khẩu?</a>
      </div>
      <button type="submit">Đăng nhập</button>
    </form>

    <p style="text-align:center; font-size:14px;">
      Chưa có tài khoản?
      <a href="${pageContext.request.contextPath}/register">Đăng ký</a>
    </p>
  </div>
</body>
</html>
