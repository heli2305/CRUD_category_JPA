<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Video của tôi</title>
<style>
  body { font-family: Arial, sans-serif; background:#f2f4f7; margin:0; }
  .container { max-width:1080px; margin:28px auto; padding:0 16px; }
  .box { background:#fff; padding:22px 24px; border:1px solid #ddd; border-radius:6px; }
  .header-row { display:flex; justify-content:space-between; align-items:center; margin-bottom:18px; flex-wrap:wrap; gap:12px; }
  .header-row h2 { margin:0; color:#333; }
  .sub-info { font-size:13px; color:#666; margin-top:4px; }
  .btn-add {
    display:inline-block; background:#009ee0; color:#fff; padding:9px 16px;
    text-decoration:none; border-radius:4px; font-size:13px; font-weight:bold;
  }
  .btn-add:hover { background:#007cb3; }
  .video-grid { display:grid; grid-template-columns:repeat(3, 1fr); gap:18px; }
  @media (max-width: 820px) { .video-grid { grid-template-columns:repeat(2, 1fr); } }
  @media (max-width: 540px) { .video-grid { grid-template-columns:1fr; } }
  .video-card {
    border:1px solid #e0e0e0; border-radius:6px; overflow:hidden; background:#fff;
    display:flex; flex-direction:column;
  }
  .video-thumb { width:100%; height:170px; object-fit:cover; background:#1f2937; display:block; }
  .video-thumb-placeholder {
    width:100%; height:170px; background:#1f2937; color:#9ca3af;
    display:flex; align-items:center; justify-content:center; font-size:13px;
  }
  .video-body { padding:14px; flex:1; display:flex; flex-direction:column; }
  .video-title {
    color:#0088cc; font-size:16px; font-weight:bold; text-decoration:none;
    margin-bottom:8px; display:block;
  }
  .video-title:hover { text-decoration:underline; }
  .video-meta { font-size:12px; color:#666; line-height:1.5; margin-bottom:12px; }
  .badge-on { color:#2e7d32; font-weight:bold; }
  .badge-off { color:#c62828; font-weight:bold; }
  .card-actions { margin-top:auto; padding-top:10px; border-top:1px solid #eee; display:flex; gap:12px; font-size:13px; }
  .card-actions a { color:#0088cc; text-decoration:none; font-weight:bold; }
  .card-actions a.delete { color:#e74c3c; }
  .pagination { display:flex; justify-content:center; align-items:center; gap:6px; margin-top:24px; flex-wrap:wrap; }
  .page-link {
    display:inline-block; padding:7px 12px; border:1px solid #ccc; border-radius:4px;
    text-decoration:none; color:#333; font-size:13px; background:#fff;
  }
  .page-link:hover { background:#eef6fb; border-color:#009ee0; }
  .page-link.active { background:#009ee0; color:#fff; border-color:#009ee0; font-weight:bold; }
  .page-link.disabled { color:#aaa; pointer-events:none; background:#f8f9fa; }
</style>
</head>
<body>
  <div class="container">
    <div class="box">
      <div class="header-row">
        <div>
          <h2>Video của tôi</h2>
          <div class="sub-info">Tổng cộng ${totalItems} video (${pageSize} video / trang)</div>
        </div>
        <a class="btn-add" href="${pageContext.request.contextPath}/video/add">+ Thêm Video mới</a>
      </div>

      <c:choose>
        <c:when test="${empty videos}">
          <p>Bạn chưa có video nào. Nhấn <strong>+ Thêm Video mới</strong> để đăng tải video đầu tiên.</p>
        </c:when>
        <c:otherwise>
          <div class="video-grid">
            <c:forEach items="${videos}" var="v">
              <div class="video-card">
                <c:choose>
                  <c:when test="${not empty v.poster}">
                    <c:url value="/image" var="posterSrc">
                      <c:param name="fname" value="${v.poster}"/>
                    </c:url>
                    <img class="video-thumb" src="${posterSrc}" alt="<c:out value='${v.title}'/>"/>
                  </c:when>
                  <c:otherwise>
                    <div class="video-thumb-placeholder">No Poster</div>
                  </c:otherwise>
                </c:choose>
                <div class="video-body">
                  <a class="video-title" href="${pageContext.request.contextPath}/video/detail?id=${v.videoId}&from=video">
                    <c:out value="${v.title}"/>
                  </a>
                  <div class="video-meta">
                    <c:if test="${v.category != null}">
                      <div>Danh mục: <c:out value="${v.category.categoryname}"/></div>
                    </c:if>
                    <div>Lượt xem: ${v.views}</div>
                    <div>Trạng thái:
                      <c:choose>
                        <c:when test="${v.active == 1}"><span class="badge-on">Hoạt động</span></c:when>
                        <c:otherwise><span class="badge-off">Ẩn</span></c:otherwise>
                      </c:choose>
                    </div>
                  </div>
                  <div class="card-actions">
                    <a href="${pageContext.request.contextPath}/video/detail?id=${v.videoId}&from=video">Xem</a>
                    <a href="${pageContext.request.contextPath}/video/edit?id=${v.videoId}">Sửa</a>
                    <a class="delete" href="${pageContext.request.contextPath}/video/delete?id=${v.videoId}"
                       onclick="return confirm('Bạn có chắc muốn xóa video này?');">Xóa</a>
                  </div>
                </div>
              </div>
            </c:forEach>
          </div>

          <c:if test="${totalPages > 1}">
            <div class="pagination">
              <a class="page-link ${currentPage <= 1 ? 'disabled' : ''}"
                 href="${pageContext.request.contextPath}/video?page=${currentPage - 1}">&laquo; Trước</a>
              <c:forEach begin="1" end="${totalPages}" var="p">
                <a class="page-link ${p == currentPage ? 'active' : ''}"
                   href="${pageContext.request.contextPath}/video?page=${p}">${p}</a>
              </c:forEach>
              <a class="page-link ${currentPage >= totalPages ? 'disabled' : ''}"
                 href="${pageContext.request.contextPath}/video?page=${currentPage + 1}">Sau &raquo;</a>
            </div>
          </c:if>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</body>
</html>
