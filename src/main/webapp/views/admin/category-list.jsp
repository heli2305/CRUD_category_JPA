<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Danh sách danh mục</title>
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
  table.cate-table { width: 100%; border-collapse: collapse; background: #fff; }
  table.cate-table th, table.cate-table td {
    border: 1px solid #ddd;
    padding: 10px 12px;
    text-align: left;
    font-size: 14px;
  }
  table.cate-table th { background: #f8f9fa; color: #444; }
  .badge-on { color: #2e7d32; font-weight: bold; }
  .badge-off { color: #c62828; font-weight: bold; }
  .action-link { color: #0088cc; text-decoration: none; font-weight: bold; }
  .action-link.delete { color: #e74c3c; }
</style>
</head>
<body>
  <jsp:include page="/views/admin/sidebar.jsp">
    <jsp:param name="active" value="category"/>
  </jsp:include>
  <div class="admin-container">
    <div class="admin-main">
      <h1 class="page-title">Quản lý danh mục</h1>
      <p class="page-subtitle">Danh sách các danh mục trong hệ thống (JPA)</p>

      <a class="btn-add" href="<c:url value='/admin/category/add'/>">+ Add Category</a>

      <table class="cate-table">
        <tr>
          <th>STT</th>
          <th>Images</th>
          <th>Category name</th>
          <th>Status</th>
          <th>Action</th>
        </tr>
        <c:forEach items="${listcate}" var="cate" varStatus="STT">
          <tr>
            <td>${STT.index + 1}</td>
            <td>
              <c:choose>
                <c:when test="${not empty cate.images and (fn:startsWith(cate.images, 'https') or fn:startsWith(cate.images, 'http'))}">
                  <c:url value="${cate.images}" var="imgUrl"/>
                </c:when>
                <c:when test="${not empty cate.images}">
                  <c:url value="/image" var="imgUrl">
                    <c:param name="fname" value="${cate.images}"/>
                  </c:url>
                </c:when>
                <c:otherwise>
                  <c:set var="imgUrl" value=""/>
                </c:otherwise>
              </c:choose>
              <c:if test="${not empty imgUrl}">
                <img height="150" width="200" style="object-fit: cover;" src="${imgUrl}" alt="<c:out value='${cate.categoryname}'/>"/>
              </c:if>
            </td>
            <td><c:out value="${cate.categoryname}"/></td>
            <td>
              <c:if test="${cate.status == 1}">
                <span class="badge-on">Hoạt động</span>
              </c:if>
              <c:if test="${cate.status != 1}">
                <span class="badge-off">Khóa</span>
              </c:if>
            </td>
            <td>
              <a class="action-link" href="<c:url value='/admin/category/edit?id=${cate.categoryid}'/>">Sửa</a>
              |
              <a class="action-link delete"
                 href="<c:url value='/admin/category/delete?id=${cate.categoryid}'/>"
                 onclick="return confirm('Bạn có chắc muốn xóa danh mục này?');">Xóa</a>
            </td>
          </tr>
        </c:forEach>
      </table>
    </div>
  </div>
</body>
</html>
