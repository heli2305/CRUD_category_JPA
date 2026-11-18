<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<style>
  .topbar { background:#fff; border-bottom:1px solid #ddd; padding:12px 24px; }
  .topbar-inner { max-width:1080px; margin:0 auto; display:flex; justify-content:space-between; align-items:center; gap:16px; }
  .topbar-left { display:flex; align-items:center; gap:18px; }
  .topbar .brand { font-size:18px; font-weight:bold; color:#009ee0; text-decoration:none; }
  .topbar-nav a { color:#333; text-decoration:none; margin-right:12px; font-size:14px; font-weight:bold; }
  .topbar-nav a:hover { color:#009ee0; }
  .topbar-links a { color:#009ee0; text-decoration:none; margin:0 6px; }
  .topbar-links a:hover { text-decoration:underline; }
</style>
<div class="topbar">
  <div class="topbar-inner">
    <div class="topbar-left">
      <a class="brand" href="${pageContext.request.contextPath}/home">JPA</a>
      <div class="topbar-nav">
        <a href="${pageContext.request.contextPath}/home">Trang chủ</a>
        <c:if test="${sessionScope.account != null}">
          <a href="${pageContext.request.contextPath}/video">Video của tôi</a>
          <c:if test="${sessionScope.account.roleid == 1}">
            <a href="${pageContext.request.contextPath}/admin/home">Quản trị</a>
          </c:if>
        </c:if>
      </div>
    </div>
    <div class="topbar-links">
      <c:choose>
        <c:when test="${sessionScope.account == null}">
          <a href="${pageContext.request.contextPath}/login">Đăng nhập</a>
          <span>|</span>
          <a href="${pageContext.request.contextPath}/register">Đăng ký</a>
        </c:when>
        <c:otherwise>
          <span>Xin chào, <strong><c:out value="${sessionScope.account.fullName}"/></strong></span>
          <span>|</span>
          <a href="${pageContext.request.contextPath}/logout">Đăng Xuất</a>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</div>
