<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Trang chủ</title>
<style>
  body { font-family: Arial, sans-serif; background:#f2f4f7; margin:0; }
  .box { max-width:720px; margin:40px auto; background:#fff; padding:28px;
         border:1px solid #ddd; border-radius:6px; }
  h2 { color:#333; margin-top:0; }
  p { color:#555; }
</style>
</head>
<body>
  <jsp:include page="/views/topbar.jsp"/>
  <div class="box">
    <h2>Trang chủ</h2>
    <c:choose>
      <c:when test="${sessionScope.account != null}">
        <p>Tài khoản: <strong><c:out value="${sessionScope.account.userName}"/></strong></p>
        <p>Họ tên: <strong><c:out value="${sessionScope.account.fullName}"/></strong></p>
        <p>Email: <strong><c:out value="${sessionScope.account.email}"/></strong></p>
        <p>Vai trò (roleid): <strong>${sessionScope.account.roleid}</strong></p>
      </c:when>
      <c:otherwise>
        <p>Bạn chưa đăng nhập. Vui lòng đăng nhập hoặc đăng ký tài khoản.</p>
      </c:otherwise>
    </c:choose>
  </div>
</body>
</html>
