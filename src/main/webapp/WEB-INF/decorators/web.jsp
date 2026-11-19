<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/taglib.jsp"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <title><sitemesh:write property="title" default="JPA"/></title>

  <link href="${pageContext.request.contextPath}/templates/sb-admin-2/vendor/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
  <link href="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i" rel="stylesheet">
  <link href="${pageContext.request.contextPath}/templates/sb-admin-2/css/sb-admin-2.min.css" rel="stylesheet">

  <sitemesh:write property="head"/>
</head>
<body class="d-flex flex-column min-vh-100 bg-light">
  <jsp:include page="/commons/web/header.jsp"/>

  <main class="flex-grow-1">
    <sitemesh:write property="body"/>
  </main>

  <c:import url="/commons/web/footer.jsp"/>

  <script src="${pageContext.request.contextPath}/templates/sb-admin-2/vendor/jquery/jquery.min.js"></script>
  <script src="${pageContext.request.contextPath}/templates/sb-admin-2/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
  <script src="${pageContext.request.contextPath}/templates/sb-admin-2/js/sb-admin-2.min.js"></script>
</body>
</html>
