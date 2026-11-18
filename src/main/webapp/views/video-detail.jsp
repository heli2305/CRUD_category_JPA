<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
<meta charset="UTF-8">
<title><c:out value="${video.title}"/></title>
<style>
  body { font-family: Arial, sans-serif; background:#f2f4f7; margin:0; }
  .container { max-width:960px; margin:28px auto; padding:0 16px; }
  .detail-box { background:#fff; padding:24px; border:1px solid #ddd; border-radius:6px; }
  .back-link { display:inline-block; color:#0088cc; text-decoration:none; font-size:14px; font-weight:bold; margin-bottom:14px; }
  .back-link:hover { text-decoration:underline; }
  .player-wrap { width:100%; background:#000; border-radius:6px; overflow:hidden; margin-bottom:18px; }
  .player-wrap video { width:100%; max-height:520px; display:block; }
  .no-video { padding:64px 20px; text-align:center; color:#ccc; font-size:15px; }
  h1 { margin:0 0 10px 0; font-size:24px; color:#222; }
  .meta-bar { display:flex; flex-wrap:wrap; gap:18px; font-size:13px; color:#666; padding-bottom:14px; border-bottom:1px solid #eee; margin-bottom:16px; }
  .desc { font-size:15px; line-height:1.6; color:#333; white-space:pre-line; }
</style>
</head>
<body>
  <jsp:include page="/views/topbar.jsp"/>
  <div class="container">
    <div class="detail-box">
      <c:choose>
        <c:when test="${fromPage == 'video'}">
          <a class="back-link" href="${pageContext.request.contextPath}/video">&larr; Quay lại Video của tôi</a>
        </c:when>
        <c:when test="${fromPage == 'admin'}">
          <a class="back-link" href="${pageContext.request.contextPath}/admin/videos">&larr; Quay lại Quản lý Video</a>
        </c:when>
        <c:otherwise>
          <a class="back-link" href="${pageContext.request.contextPath}/home">&larr; Quay lại Trang chủ</a>
        </c:otherwise>
      </c:choose>

      <div class="player-wrap">
        <c:choose>
          <c:when test="${not empty video.videoUrl}">
            <c:url value="/video-file" var="playSrc">
              <c:param name="fname" value="${video.videoUrl}"/>
            </c:url>
            <c:choose>
              <c:when test="${not empty video.poster}">
                <c:url value="/image" var="posterAttr">
                  <c:param name="fname" value="${video.poster}"/>
                </c:url>
              </c:when>
              <c:otherwise>
                <c:set var="posterAttr" value=""/>
              </c:otherwise>
            </c:choose>
            <video controls poster="${posterAttr}">
              <source src="${playSrc}">
              Trình duyệt của bạn không hỗ trợ phát video HTML5.
            </video>
          </c:when>
          <c:otherwise>
            <div class="no-video">Video này chưa có file phát.</div>
          </c:otherwise>
        </c:choose>
      </div>

      <h1><c:out value="${video.title}"/></h1>
      <div class="meta-bar">
        <span><strong>Mã video:</strong> <c:out value="${video.videoId}"/></span>
        <span><strong>Lượt xem:</strong> ${video.views}</span>
        <c:if test="${video.category != null}">
          <span><strong>Danh mục:</strong> <c:out value="${video.category.categoryname}"/></span>
        </c:if>
        <c:if test="${video.user != null}">
          <span><strong>Người đăng:</strong> <c:out value="${video.user.fullName != null ? video.user.fullName : video.user.userName}"/></span>
        </c:if>
        <c:if test="${video.createdDate != null}">
          <span><strong>Ngày đăng:</strong> ${video.createdDate}</span>
        </c:if>
      </div>

      <div class="desc">
        <c:choose>
          <c:when test="${not empty video.description}">
            <c:out value="${video.description}"/>
          </c:when>
          <c:otherwise>Không có mô tả.</c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>
</body>
</html>
