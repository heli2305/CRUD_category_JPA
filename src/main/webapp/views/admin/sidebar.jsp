<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<style>
  * { box-sizing: border-box; }
  body {
    margin: 0;
    font-family: Arial, sans-serif;
    background: #eef2f7;
    color: #333;
  }
  .admin-header {
    background: #0088cc;
    color: #fff;
    padding: 0 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 52px;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 1000;
  }
  .admin-header .brand {
    color: #fff;
    text-decoration: none;
    font-size: 20px;
    font-weight: bold;
  }
  .admin-header .user-meta {
    font-size: 13px;
    display: flex;
    align-items: center;
  }
  .admin-header .btn-logout {
    background: #e74c3c;
    color: #fff;
    padding: 5px 12px;
    text-decoration: none;
    border-radius: 3px;
    margin-left: 14px;
    font-size: 12px;
    font-weight: bold;
  }
  .admin-header .btn-logout:hover {
    background: #c0392b;
  }
  .admin-container {
    padding-top: 52px;
  }
  .admin-sidebar {
    width: 220px;
    background: #0088cc;
    color: #fff;
    position: fixed;
    top: 52px;
    left: 0;
    bottom: 0;
    z-index: 999;
    overflow-y: auto;
  }
  .profile-box {
    text-align: center;
    padding: 24px 16px 16px;
    background: #0077b3;
    border-bottom: 1px solid rgba(255, 255, 255, 0.15);
  }
  .profile-avatar-icon {
    width: 72px;
    height: 72px;
    border-radius: 50%;
    border: 3px solid #fff;
    margin: 0 auto 10px;
    background: #e6ebf2;
    color: #0077b3;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  .profile-role {
    font-size: 13px;
    color: #e0f2fe;
  }
  .sidebar-menu {
    list-style: none;
    padding: 0;
    margin: 0;
  }
  .sidebar-menu > li > a {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 13px 18px;
    color: #fff;
    text-decoration: none;
    font-size: 13px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  }
  .sidebar-menu > li > a:hover {
    background: #006699;
  }
  .sidebar-menu > li.active-dashboard > a {
    background: #e74c3c;
  }
  .sidebar-menu > li.active-category > a {
    background: #1a1a1a;
  }
  .sidebar-menu .submenu {
    list-style: none;
    padding: 0;
    margin: 0;
    background: #0077b3;
  }
  .sidebar-menu .submenu a {
    display: block;
    padding: 9px 18px 9px 34px;
    font-size: 12px;
    color: #dbeafe;
    text-decoration: none;
    border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  }
  .sidebar-menu .submenu a:hover {
    background: #006699;
    color: #fff;
  }
  .admin-main {
    margin-left: 220px;
    padding: 24px 32px;
    min-height: calc(100vh - 52px);
    background: #fff;
  }
  .icon-svg {
    width: 16px;
    height: 16px;
    fill: currentColor;
    display: inline-block;
    flex-shrink: 0;
  }
</style>

<div class="admin-header">
  <a class="brand" href="<c:url value='/admin/home'/>">Dashboard <span style="font-size:12px; font-weight:normal; opacity:0.8;">(JPA)</span></a>
  <div class="user-meta">
    <span>Xin chào <strong><c:out value="${not empty sessionScope.account.fullName ? sessionScope.account.fullName : 'Admin'}"/></strong></span>
    <a class="btn-logout" href="<c:url value='/logout'/>">Đăng xuất</a>
  </div>
</div>

<div class="admin-sidebar">
  <div class="profile-box">
    <div class="profile-avatar-icon" aria-label="Admin Avatar">
      <svg width="36" height="36" viewBox="0 0 24 24" fill="currentColor">
        <path d="M12 12c2.7 0 4.8-2.1 4.8-4.8S14.7 2.4 12 2.4 7.2 4.5 7.2 7.2 9.3 12 12 12zm0 2.4c-3.2 0-9.6 1.6-9.6 4.8v2.4h19.2v-2.4c0-3.2-6.4-4.8-9.6-4.8z"/>
      </svg>
    </div>
    <div class="profile-role">Bạn là Admin</div>
  </div>
  <ul class="sidebar-menu">
    <li class="${param.active == 'dashboard' ? 'active-dashboard' : ''}">
      <a href="<c:url value='/admin/home'/>">
        <svg class="icon-svg" viewBox="0 0 24 24"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-6h2v6zm0-8h-2V7h2v2z"/></svg>
        <span>Dashboard</span>
      </a>
    </li>
    <li class="${param.active == 'category' ? 'active-category' : ''}">
      <a href="<c:url value='/admin/categories'/>">
        <svg class="icon-svg" viewBox="0 0 24 24"><path d="M10 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2h-8l-2-2z"/></svg>
        <span>Quản lý Danh mục</span>
      </a>
      <ul class="submenu">
        <li><a href="<c:url value='/admin/category/add'/>">— Thêm danh mục mới</a></li>
        <li><a href="<c:url value='/admin/categories'/>">— Danh sách danh mục</a></li>
      </ul>
    </li>
    <li>
      <a href="#">
        <svg class="icon-svg" viewBox="0 0 24 24"><path d="M21 2H3c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h7l-2 3v1h8v-1l-2-3h7c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm0 12H3V4h18v10z"/></svg>
        <span>Quản lý sản phẩm</span>
      </a>
    </li>
    <li>
      <a href="#">
        <svg class="icon-svg" viewBox="0 0 24 24"><path d="M3 3v8h8V3H3zm6 6H5V5h4v4zm-6 4v8h8v-8H3zm6 6H5v-4h4v4zm4-16v8h8V3h-8zm6 6h-4V5h4v4zm-6 4v8h8v-8h-8zm6 6h-4v-4h4v4z"/></svg>
        <span>Quản lý tài khoản</span>
      </a>
    </li>
  </ul>
</div>
