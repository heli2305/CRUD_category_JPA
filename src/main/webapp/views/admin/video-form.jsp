<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>${mode == 'edit' ? 'Cập nhật Video' : 'Thêm Video mới'}</title>
<style>
  .page-title { color: #e74c3c; margin: 0 0 4px 0; font-size: 24px; font-weight: bold; }
  .page-subtitle { color: #777; margin: 0 0 18px 0; font-size: 13px; }
  .admin-form-wrap { max-width: 700px; }
</style>
</head>
<body>
  <jsp:include page="/views/admin/sidebar.jsp">
    <jsp:param name="active" value="video"/>
  </jsp:include>
  <div class="admin-container">
    <div class="admin-main">
      <h1 class="page-title">${mode == 'edit' ? 'Cập nhật Video' : 'Thêm Video mới'}</h1>
      <p class="page-subtitle">Đăng tải video và ảnh bìa bằng Multipart</p>

      <div class="admin-form-wrap">
        <jsp:include page="/views/fragments/video-form-body.jsp">
          <jsp:param name="formAction" value="${mode == 'edit' ? '/admin/video/update' : '/admin/video/insert'}"/>
          <jsp:param name="cancelUrl" value="/admin/videos"/>
        </jsp:include>
      </div>
    </div>
  </div>
</body>
</html>
