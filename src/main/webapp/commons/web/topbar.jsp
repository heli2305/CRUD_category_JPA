<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/taglib.jsp"%>
<nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom shadow-sm">
  <div class="container">
    <a class="navbar-brand font-weight-bold text-primary" href="${pageContext.request.contextPath}/home">
      <i class="fas fa-film mr-1"></i> JPA
    </a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#webTopbarNav"
            aria-controls="webTopbarNav" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="webTopbarNav">
      <ul class="navbar-nav mr-auto">
        <li class="nav-item">
          <a class="nav-link font-weight-bold" href="${pageContext.request.contextPath}/home">Trang chủ</a>
        </li>
        <c:if test="${sessionScope.account != null}">
          <li class="nav-item">
            <a class="nav-link font-weight-bold" href="${pageContext.request.contextPath}/video">Video của tôi</a>
          </li>
          <c:if test="${sessionScope.account.roleid == 1}">
            <li class="nav-item">
              <a class="nav-link font-weight-bold text-danger" href="${pageContext.request.contextPath}/admin/home">Quản trị</a>
            </li>
          </c:if>
        </c:if>
      </ul>

      <div class="navbar-nav ml-auto align-items-lg-center">
        <c:choose>
          <c:when test="${sessionScope.account == null}">
            <a class="nav-link text-primary font-weight-bold" href="${pageContext.request.contextPath}/login">Đăng nhập</a>
            <a class="btn btn-primary btn-sm ml-lg-2" href="${pageContext.request.contextPath}/register">Đăng ký</a>
          </c:when>
          <c:otherwise>
            <span class="navbar-text mr-3 text-dark small">
              Xin chào, <strong><c:out value="${sessionScope.account.fullName}"/></strong>
            </span>
            <a class="btn btn-outline-danger btn-sm" href="${pageContext.request.contextPath}/logout">Đăng xuất</a>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>
</nav>
