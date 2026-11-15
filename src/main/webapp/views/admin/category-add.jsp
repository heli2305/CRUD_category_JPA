<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Thêm danh mục</title>
<style>
  .page-title { color: #e74c3c; margin: 0 0 4px 0; font-size: 24px; font-weight: bold; }
  .page-subtitle { color: #777; margin: 0 0 18px 0; font-size: 13px; }
  .form-box { max-width: 520px; border: 1px solid #ddd; border-radius: 4px; padding: 20px; background: #fff; }
  .form-group { margin-bottom: 14px; }
  .form-group label { display: block; font-size: 13px; font-weight: bold; margin-bottom: 6px; color: #444; }
  .form-group input[type=text] { width: 100%; padding: 8px 10px; border: 1px solid #ccc; border-radius: 3px; }
  .radio-group label { display: inline-block; font-weight: normal; margin-right: 16px; }
  .btn-submit { background: #0088cc; color: #fff; border: 0; padding: 9px 18px; border-radius: 3px; cursor: pointer; font-weight: bold; }
  .btn-back { margin-left: 10px; color: #555; text-decoration: none; font-size: 13px; }
</style>
</head>
<body>
  <jsp:include page="/views/admin/sidebar.jsp">
    <jsp:param name="active" value="category"/>
  </jsp:include>
  <div class="admin-container">
    <div class="admin-main">
      <h1 class="page-title">Thêm danh mục mới</h1>
      <p class="page-subtitle">Nhập thông tin danh mục mới</p>

      <div class="form-box">
        <form action="<c:url value='/admin/category/insert'/>" method="post" enctype="multipart/form-data">
          <div class="form-group">
            <label for="categoryname">Category name:</label>
            <input type="text" id="categoryname" name="categoryname" required>
          </div>
          <div class="form-group">
            <label for="images">Link images:</label>
            <input type="text" id="images" name="images" placeholder="https://... hoặc để trống nếu upload file">
          </div>
          <div class="form-group">
            <label for="images1">Upload images:</label>
            <input type="file" id="images1" name="images1">
          </div>
          <div class="form-group radio-group">
            <label>Status</label>
            <input type="radio" id="ston" name="status" value="1" checked>
            <label for="ston">Hoạt động</label>
            <input type="radio" id="stoff" name="status" value="0">
            <label for="stoff">Khóa</label>
          </div>
          <input class="btn-submit" type="submit" value="Insert">
          <a class="btn-back" href="<c:url value='/admin/categories'/>">Quay lại</a>
        </form>
      </div>
    </div>
  </div>
</body>
</html>
