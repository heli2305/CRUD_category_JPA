<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/taglib.jsp"%>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="utf-8">
  <meta http-equiv="X-UA-Compatible" content="IE=edge">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <title><sitemesh:write property="title" default="Đăng nhập - JPA"/></title>

  <link href="${pageContext.request.contextPath}/templates/sb-admin-2/vendor/fontawesome-free/css/all.min.css" rel="stylesheet" type="text/css">
  <link href="${pageContext.request.contextPath}/templates/sb-admin-2/css/sb-admin-2.min.css" rel="stylesheet">

  <sitemesh:write property="head"/>
  <style>
    .box h2 { font-size: 22px; margin-bottom: 16px; }
    .box p { margin-top: 14px; margin-bottom: 0; }
  </style>
</head>
<body class="bg-light">
  <sitemesh:write property="body"/>

  <script src="${pageContext.request.contextPath}/templates/sb-admin-2/vendor/jquery/jquery.min.js"></script>
  <script src="${pageContext.request.contextPath}/templates/sb-admin-2/vendor/bootstrap/js/bootstrap.bundle.min.js"></script>
</body>
</html>
