<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Đặt lại mật khẩu</title>
<style>
  body { font-family: Arial, sans-serif; background:#f2f4f7; }
  .box { width:380px; margin:70px auto; background:#fff; padding:28px;
         border:1px solid #ddd; border-radius:6px; }
  h2 { text-align:center; color:#555; font-weight:normal; margin-bottom:10px; }
  .hint { font-size:13px; color:#666; text-align:center; margin-bottom:14px; }
  input[type=password] { width:100%; padding:10px; margin:8px 0;
         box-sizing:border-box; border:1px solid #ccc; }
  button { width:100%; padding:10px; background:#009ee0; color:#fff; border:0; cursor:pointer; margin-top:8px; }
  .alert { color:#a94442; background:#f2dede; padding:8px; margin-bottom:8px; font-size:14px; }
  .success { color:#3c763d; background:#dff0d8; padding:8px; margin-bottom:8px; font-size:14px; }
</style>
</head>
<body>
  <div class="box">
    <h2>Đặt Lại Mật Khẩu</h2>
    <p class="hint">Nhập mật khẩu mới cho tài khoản <strong><c:out value="${email}"/></strong>.</p>

    <c:if test="${alert != null}">
      <div class="alert"><c:out value="${alert}"/></div>
    </c:if>
    <c:if test="${success != null}">
      <div class="success"><c:out value="${success}"/></div>
    </c:if>

    <form action="${pageContext.request.contextPath}/reset-password" method="post">
      <input type="password" name="newPassword" placeholder="Mật khẩu mới (tối thiểu 6 ký tự)" minlength="6" required>
      <input type="password" name="confirmPassword" placeholder="Xác nhận mật khẩu mới" minlength="6" required>
      <button type="submit">Đặt lại mật khẩu</button>
    </form>

    <p style="text-align:center; font-size:14px; margin-top:16px;">
      <a href="${pageContext.request.contextPath}/login">Quay lại đăng nhập</a>
    </p>
  </div>
</body>
</html>
