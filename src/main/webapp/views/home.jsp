<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title>Trang chủ</title>
<style>
  body { font-family: Arial, sans-serif; background:#f2f4f7; margin:0; }
  .container { max-width:1200px; margin:28px auto; padding:0 16px; }
  .box { background:#fff; padding:20px 24px; border:1px solid #ddd; border-radius:6px; margin-bottom:24px; }
  h2 { color:#333; margin:0 0 12px 0; }
  p { color:#555; margin:6px 0; }
  .section-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; }
  .section-header h3 { margin:0; color:#222; font-size:20px; }
  .btn-primary {
    display:inline-block; background:#009ee0; color:#fff; padding:8px 14px;
    text-decoration:none; border-radius:4px; font-size:13px; font-weight:bold;
  }
  .btn-primary:hover { background:#007cb3; }
  .video-grid { display:grid; grid-template-columns:repeat(5, 1fr); gap:16px; }
  @media (max-width: 960px) { .video-grid { grid-template-columns:repeat(3, 1fr); } }
  @media (max-width: 600px) { .video-grid { grid-template-columns:repeat(2, 1fr); } }
  .video-card {
    background:#fff; border:1px solid #e0e0e0; border-radius:6px; overflow:hidden;
    display:flex; flex-direction:column;
  }
  .video-thumb { width:100%; height:140px; object-fit:cover; background:#1f2937; display:block; }
  .video-thumb-placeholder {
    width:100%; height:140px; background:#1f2937; color:#9ca3af;
    display:flex; align-items:center; justify-content:center; font-size:13px;
  }
  .video-body { padding:12px; flex:1; display:flex; flex-direction:column; }
  .video-title {
    color:#0088cc; font-size:15px; font-weight:bold; text-decoration:none;
    margin-bottom:6px; display:block;
  }
  .video-title:hover { text-decoration:underline; }
  .video-meta { font-size:12px; color:#666; margin-top:auto; }
</style>
</head>
<body>
  <div class="container">
    <div class="box">
      <h2>Trang chủ</h2>
      <c:choose>
        <c:when test="${sessionScope.account != null}">
          <p>Tài khoản: <strong><c:out value="${sessionScope.account.userName}"/></strong> |
             Họ tên: <strong><c:out value="${sessionScope.account.fullName}"/></strong> |
             Email: <strong><c:out value="${sessionScope.account.email}"/></strong></p>
        </c:when>
        <c:otherwise>
          <p>Bạn chưa đăng nhập. Vui lòng <a href="${pageContext.request.contextPath}/login">đăng nhập</a> hoặc <a href="${pageContext.request.contextPath}/register">đăng ký</a> để quản lý video của bạn.</p>
        </c:otherwise>
      </c:choose>
    </div>

    <div class="box">
      <div class="section-header">
        <h3>10 Video mới nhất</h3>
        <c:if test="${sessionScope.account != null}">
          <a class="btn-primary" href="${pageContext.request.contextPath}/video">Quản lý Video của tôi</a>
        </c:if>
      </div>

      <c:choose>
        <c:when test="${empty latestVideos}">
          <p>Chưa có video nào được đăng tải.</p>
        </c:when>
        <c:otherwise>
          <div class="video-grid">
            <c:forEach items="${latestVideos}" var="v">
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
                  <a class="video-title" href="${pageContext.request.contextPath}/video/detail?id=${v.videoId}&from=home">
                    <c:out value="${v.title}"/>
                  </a>
                  <div class="video-meta">
                    <c:if test="${v.category != null}">
                      <div>Danh mục: <c:out value="${v.category.categoryname}"/></div>
                    </c:if>
                    <div>Lượt xem: ${v.views}</div>
                    <c:if test="${v.user != null}">
                      <div>Người đăng: <c:out value="${v.user.fullName != null ? v.user.fullName : v.user.userName}"/></div>
                    </c:if>
                  </div>
                </div>
              </div>
            </c:forEach>
          </div>
        </c:otherwise>
      </c:choose>
    </div>
  </div>
</body>
</html>
