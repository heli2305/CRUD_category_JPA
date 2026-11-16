<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Quên mật khẩu</title>
<style>
  body { font-family: Arial, sans-serif; background:#f2f4f7; }
  .box { width:380px; margin:70px auto; background:#fff; padding:28px;
         border:1px solid #ddd; border-radius:6px; }
  h2 { text-align:center; color:#555; font-weight:normal; margin-bottom:10px; }
  .hint { font-size:13px; color:#666; text-align:center; margin-bottom:14px; }
  input[type=email] { width:100%; padding:10px; margin:8px 0;
         box-sizing:border-box; border:1px solid #ccc; }
  button { width:100%; padding:10px; background:#009ee0; color:#fff; border:0; cursor:pointer; margin-top:8px; }
  .alert { color:#a94442; background:#f2dede; padding:8px; margin-bottom:8px; font-size:14px; }
  .success { color:#3c763d; background:#dff0d8; padding:8px; margin-bottom:8px; font-size:14px; }
</style>
</head>
<body>
  <div class="box">
    <h2>Quên Mật Khẩu</h2>
    <p class="hint">Nhập email đã đăng ký để nhận mã OTP đặt lại mật khẩu.</p>

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
    <c:if test="${success != null}">
      <div class="success"><c:out value="${success}"/></div>
    </c:if>

    <form action="${pageContext.request.contextPath}/forgot-password" method="post">
      <input type="email" name="email" placeholder="Email đã đăng ký" value="<c:out value='${email}'/>" required>
      <button type="submit">Gửi mã OTP</button>
    </form>

    <p style="text-align:center; font-size:14px; margin-top:16px;">
      <a href="${pageContext.request.contextPath}/login">Quay lại đăng nhập</a>
    </p>
  </div>
</body>
</html>
