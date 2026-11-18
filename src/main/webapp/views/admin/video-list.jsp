<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Quản lý Video</title>
<style>
  .page-title { color: #e74c3c; margin: 0 0 4px 0; font-size: 24px; font-weight: bold; }
  .page-subtitle { color: #777; margin: 0 0 18px 0; font-size: 13px; }
  .btn-add {
    display: inline-block;
    background: #0088cc;
    color: #fff;
    padding: 8px 14px;
    text-decoration: none;
    border-radius: 3px;
    font-size: 13px;
    font-weight: bold;
    margin-bottom: 14px;
  }
  .btn-add:hover { background: #006699; }
  table.video-table { width: 100%; border-collapse: collapse; background: #fff; }
  table.video-table th, table.video-table td {
    border: 1px solid #ddd;
    padding: 10px 12px;
    text-align: left;
    font-size: 14px;
    vertical-align: middle;
  }
  table.video-table th { background: #f8f9fa; color: #444; }
  .badge-on { color: #2e7d32; font-weight: bold; }
  .badge-off { color: #c62828; font-weight: bold; }
  .action-link { color: #0088cc; text-decoration: none; font-weight: bold; }
  .action-link.delete { color: #e74c3c; }
</style>
</head>
<body>
  <jsp:include page="/views/admin/sidebar.jsp">
    <jsp:param name="active" value="video"/>
  </jsp:include>
  <div class="admin-container">
    <div class="admin-main">
      <h1 class="page-title">Quản lý Video</h1>
      <p class="page-subtitle">Danh sách toàn bộ video trong hệ thống (JPA)</p>

      <a class="btn-add" href="<c:url value='/admin/video/add'/>">+ Thêm Video mới</a>

      <table class="video-table">
        <tr>
          <th>STT</th>
          <th>Poster</th>
          <th>Mã Video</th>
          <th>Tiêu đề</th>
          <th>Danh mục</th>
          <th>Người đăng</th>
          <th>Lượt xem</th>
          <th>Trạng thái</th>
          <th>Thao tác</th>
        </tr>
        <c:forEach items="${videos}" var="v" varStatus="STT">
          <tr>
            <td>${STT.index + 1}</td>
            <td>
              <c:choose>
                <c:when test="${not empty v.poster}">
                  <c:url value="/image" var="posterSrc">
                    <c:param name="fname" value="${v.poster}"/>
                  </c:url>
                  <img height="80" width="120" style="object-fit: cover;" src="${posterSrc}" alt="<c:out value='${v.title}'/>"/>
                </c:when>
                <c:otherwise>—</c:otherwise>
              </c:choose>
            </td>
            <td><c:out value="${v.videoId}"/></td>
            <td>
              <a class="action-link" href="<c:url value='/video/detail?id=${v.videoId}&from=admin'/>">
                <c:out value="${v.title}"/>
              </a>
            </td>
            <td><c:out value="${v.category != null ? v.category.categoryname : '—'}"/></td>
            <td><c:out value="${v.user != null ? v.user.userName : '—'}"/></td>
            <td>${v.views}</td>
            <td>
              <c:choose>
                <c:when test="${v.active == 1}"><span class="badge-on">Hoạt động</span></c:when>
                <c:otherwise><span class="badge-off">Ẩn</span></c:otherwise>
              </c:choose>
            </td>
            <td>
              <a class="action-link" href="<c:url value='/admin/video/edit?id=${v.videoId}'/>">Sửa</a>
              |
              <a class="action-link delete"
                 href="<c:url value='/admin/video/delete?id=${v.videoId}'/>"
                 onclick="return confirm('Bạn có chắc muốn xóa video này?');">Xóa</a>
            </td>
          </tr>
        </c:forEach>
      </table>
    </div>
  </div>
</body>
</html>
