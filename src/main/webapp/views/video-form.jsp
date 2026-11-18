<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>${mode == 'edit' ? 'Cập nhật Video' : 'Đăng tải Video mới'}</title>
<style>
  body { font-family: Arial, sans-serif; background:#f2f4f7; margin:0; }
  .container { max-width:740px; margin:28px auto; padding:0 16px; }
  .form-box { background:#fff; padding:24px; border:1px solid #ddd; border-radius:6px; }
  .form-box h2 { margin:0 0 18px 0; color:#333; }
</style>
</head>
<body>
  <jsp:include page="/views/topbar.jsp"/>
  <div class="container">
    <div class="form-box">
      <h2>${mode == 'edit' ? 'Cập nhật Video' : 'Đăng tải Video mới'}</h2>
      <jsp:include page="/views/fragments/video-form-body.jsp">
        <jsp:param name="formAction" value="${mode == 'edit' ? '/video/update' : '/video/insert'}"/>
        <jsp:param name="cancelUrl" value="/video"/>
      </jsp:include>
    </div>
  </div>
</body>
</html>
