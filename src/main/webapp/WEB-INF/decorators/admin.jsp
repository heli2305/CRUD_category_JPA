<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/taglib.jsp"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <title><sitemesh:write property="title" default="Quản trị - JPA"/></title>

  <link href="${pageContext.request.contextPath}/templates/sb-admin-2/vendor/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
  <link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/templates/sb-admin-2/css/sb-admin-2.min.css" rel="stylesheet">

  <sitemesh:write property="head"/>
</head>
<body id="page-top">
  <div id="wrapper">
    <jsp:include page="/commons/admin/left.jsp"/>

    <div id="content-wrapper" class="d-flex flex-column">
      <div id="content">
        <jsp:include page="/commons/admin/header.jsp"/>

        <div class="container-fluid">
          <sitemesh:write property="body"/>
        </div>
      </div>

      <c:import url="/commons/admin/footer.jsp"/>
    </div>
  </div>

  <a class="scroll-to-top rounded" href="#page-top">
    <i class="fas fa-angle-up"></i>
  </a>

  <!-- Logout Modal -->
  <div class="modal fade" id="logoutModal" tabindex="-1" role="dialog" aria-labelledby="logoutModalLabel" aria-hidden="true">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title" id="logoutModalLabel">Xác nhận đăng xuất</h5>
          <button class="close" type="button" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">Bạn có chắc chắn muốn đăng xuất khỏi hệ thống không?</div>
        <div class="modal-footer">
          <button class="btn btn-secondary" type="button" data-dismiss="modal">Hủy</button>
          <a class="btn btn-primary" href="<c:url value='/logout'/>">Đăng xuất</a>
        </div>
      </div>
    </div>
  </div>

  <script src="${pageContext.request.contextPath}/templates/sb-admin-2/vendor/jquery/jquery.min.js"></script>
  <script src="${pageContext.request.contextPath}/templates/sb-admin-2/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
  <script src="${pageContext.request.contextPath}/templates/sb-admin-2/vendor/jquery-easing/jquery.easing.min.js"></script>
  <script src="${pageContext.request.contextPath}/templates/sb-admin-2/js/sb-admin-2.min.js"></script>
</body>
</html>
