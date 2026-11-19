<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/commons/taglib.jsp"%>
<c:set var="currentUri" value="${not empty requestScope['jakarta.servlet.forward.request_uri'] ? requestScope['jakarta.servlet.forward.request_uri'] : pageContext.request.requestURI}"/>
<c:set var="isDashboard" value="${fn:contains(currentUri, '/admin/home')}"/>
<c:set var="isCategory" value="${fn:contains(currentUri, '/admin/categor')}"/>
<c:set var="isVideo" value="${fn:contains(currentUri, '/admin/video')}"/>

<ul class="navbar-nav bg-gradient-primary sidebar sidebar-dark accordion" id="accordionSidebar">
  <a class="sidebar-brand d-flex align-items-center justify-content-center" href="<c:url value='/admin/home'/>">
    <div class="sidebar-brand-icon rotate-n-15">
      <i class="fas fa-film"></i>
    </div>
    <div class="sidebar-brand-text mx-3">JPA <sup>Admin</sup></div>
  </a>

  <hr class="sidebar-divider my-0">

  <li class="nav-item ${isDashboard ? 'active' : ''}">
    <a class="nav-link" href="<c:url value='/admin/home'/>">
      <i class="fas fa-fw fa-tachometer-alt"></i>
      <span>Dashboard</span>
    </a>
  </li>

  <hr class="sidebar-divider">

  <div class="sidebar-heading">Quản lý nội dung</div>

  <li class="nav-item ${isCategory ? 'active' : ''}">
    <a class="nav-link ${isCategory ? '' : 'collapsed'}" href="#" data-toggle="collapse" data-target="#collapseCategory"
       aria-expanded="${isCategory ? 'true' : 'false'}" aria-controls="collapseCategory">
      <i class="fas fa-fw fa-folder"></i>
      <span>Quản lý Danh mục</span>
    </a>
    <div id="collapseCategory" class="collapse ${isCategory ? 'show' : ''}" data-parent="#accordionSidebar">
      <div class="bg-white py-2 collapse-inner rounded">
        <a class="collapse-item" href="<c:url value='/admin/category/add'/>">Thêm danh mục mới</a>
        <a class="collapse-item" href="<c:url value='/admin/categories'/>">Danh sách danh mục</a>
      </div>
    </div>
  </li>

  <li class="nav-item ${isVideo ? 'active' : ''}">
    <a class="nav-link ${isVideo ? '' : 'collapsed'}" href="#" data-toggle="collapse" data-target="#collapseVideo"
       aria-expanded="${isVideo ? 'true' : 'false'}" aria-controls="collapseVideo">
      <i class="fas fa-fw fa-video"></i>
      <span>Quản lý Video</span>
    </a>
    <div id="collapseVideo" class="collapse ${isVideo ? 'show' : ''}" data-parent="#accordionSidebar">
      <div class="bg-white py-2 collapse-inner rounded">
        <a class="collapse-item" href="<c:url value='/admin/video/add'/>">Thêm video mới</a>
        <a class="collapse-item" href="<c:url value='/admin/videos'/>">Danh sách video</a>
      </div>
    </div>
  </li>

  <hr class="sidebar-divider">

  <li class="nav-item">
    <a class="nav-link" href="<c:url value='/home'/>">
      <i class="fas fa-fw fa-home"></i>
      <span>Trang chủ Video</span>
    </a>
  </li>

  <hr class="sidebar-divider d-none d-md-block">

  <div class="text-center d-none d-md-inline">
    <button class="rounded-circle border-0" id="sidebarToggle"></button>
  </div>
</ul>
