<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Dashboard Quản Trị</title>
<style>
  .page-title { color: #e74c3c; margin: 0 0 4px 0; font-size: 24px; font-weight: bold; }
  .page-subtitle { color: #777; margin: 0 0 20px 0; font-size: 13px; }
  .panel { border: 1px solid #ddd; border-radius: 3px; background: #fff; max-width: 720px; }
  .panel-header {
    background: #f8f9fa;
    padding: 12px 16px;
    font-weight: bold;
    font-size: 14px;
    color: #444;
    border-bottom: 1px solid #ddd;
  }
  .panel-body { padding: 20px; font-size: 14px; }
  .panel-body p { margin: 8px 0; color: #555; }
</style>
</head>
<body>
  <jsp:include page="/views/admin/sidebar.jsp">
    <jsp:param name="active" value="dashboard"/>
  </jsp:include>
  <div class="admin-container">
    <div class="admin-main">
      <h1 class="page-title">Dashboard</h1>
      <p class="page-subtitle">Chào mừng quay trở lại hệ thống quản trị</p>

      <div class="panel">
        <div class="panel-header">Trang Quản Trị (Admin)</div>
        <div class="panel-body">
          <p>Tài khoản: <strong><c:out value="${sessionScope.account.userName}"/></strong></p>
          <p>Họ tên: <strong><c:out value="${sessionScope.account.fullName}"/></strong></p>
          <p>Email: <strong><c:out value="${sessionScope.account.email}"/></strong></p>
          <p>Vai trò (roleid): <strong>${sessionScope.account.roleid}</strong></p>
        </div>
      </div>
    </div>
  </div>
</body>
</html>
