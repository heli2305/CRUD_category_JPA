<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<style>
  .vf-error {
    background:#fdecea; color:#c62828; border:1px solid #f5c2c7;
    padding:10px 14px; border-radius:6px; margin-bottom:16px; font-size:13px;
  }
  .vf-section {
    border:1px solid #e5e7eb; border-radius:6px; padding:16px;
    margin-bottom:16px; background:#fafafa;
  }
  .vf-section-title {
    margin:0 0 12px 0; font-size:14px; font-weight:bold; color:#1f2937;
  }
  .vf-group { margin-bottom:14px; }
  .vf-group:last-child { margin-bottom:0; }
  .vf-label { display:block; font-size:13px; font-weight:bold; margin-bottom:6px; color:#374151; }
  .vf-input, .vf-select, .vf-textarea {
    width:100%; padding:9px 11px; border:1px solid #d1d5db; border-radius:5px;
    box-sizing:border-box; font-family:inherit; font-size:14px; background:#fff;
  }
  .vf-input:focus, .vf-select:focus, .vf-textarea:focus {
    outline:none; border-color:#0088cc;
  }
  .vf-dropzone {
    display:block; border:2px dashed #cbd5e1; border-radius:6px; padding:16px;
    text-align:center; background:#fff; cursor:pointer; transition:border-color 0.2s;
  }
  .vf-dropzone:hover { border-color:#0088cc; }
  .vf-dropzone input[type=file] { display:none; }
  .vf-dropzone-main { font-size:13px; font-weight:bold; color:#0088cc; }
  .vf-dropzone-sub { font-size:12px; color:#6b7280; margin-top:4px; }
  .vf-preview-box { margin-top:10px; }
  .vf-preview-img { max-height:150px; max-width:260px; object-fit:cover; border:1px solid #d1d5db; border-radius:5px; display:block; }
  .vf-preview-video { width:100%; max-height:240px; border-radius:5px; background:#000; display:block; }
  .vf-current-file { font-size:12px; color:#4b5563; margin-top:6px; }
  .vf-radio-group { display:flex; gap:18px; align-items:center; }
  .vf-radio-item { display:inline-flex; align-items:center; gap:6px; font-size:14px; color:#374151; cursor:pointer; }
  .vf-actions { display:flex; align-items:center; gap:12px; margin-top:18px; }
  .vf-btn-submit {
    background:#0088cc; color:#fff; border:0; padding:10px 22px;
    border-radius:5px; cursor:pointer; font-weight:bold; font-size:14px;
  }
  .vf-btn-submit:hover { background:#006ea6; }
  .vf-btn-submit:disabled { background:#93c5fd; cursor:wait; }
  .vf-btn-back { color:#4b5563; text-decoration:none; font-size:14px; }
  .vf-btn-back:hover { text-decoration:underline; }
</style>

<c:if test="${not empty error}">
  <div class="vf-error"><c:out value="${error}"/></div>
</c:if>

<form id="videoUploadForm" action="<c:url value='${param.formAction}'/>" method="post" enctype="multipart/form-data">
  <c:if test="${mode == 'edit'}">
    <input type="hidden" name="videoId" value="<c:out value='${video.videoId}'/>">
  </c:if>

  <div class="vf-section">
    <div class="vf-section-title">1. Tải lên Video &amp; Ảnh bìa (Multipart)</div>

    <div class="vf-group">
      <label class="vf-label">File Video (tối đa 200MB):</label>
      <label class="vf-dropzone" for="videoFile">
        <input type="file" id="videoFile" name="videoFile" accept="video/mp4,video/webm,video/ogg,video/*">
        <div class="vf-dropzone-main" id="videoFileLabel">Chọn file video (.mp4, .webm, .ogg)</div>
        <div class="vf-dropzone-sub">Nhấn để chọn file từ máy tính</div>
      </label>
      <c:if test="${not empty video.videoUrl}">
        <div class="vf-current-file">Đã có file video trên hệ thống (chọn file mới nếu muốn thay thế).</div>
      </c:if>

      <div class="vf-preview-box" id="videoPreviewWrap" style="${not empty video.videoUrl ? '' : 'display:none;'}">
        <video class="vf-preview-video" id="videoPreview" controls>
          <c:if test="${not empty video.videoUrl}">
            <c:url value="/video-file" var="existingVideoSrc">
              <c:param name="fname" value="${video.videoUrl}"/>
            </c:url>
            <source src="${existingVideoSrc}">
          </c:if>
        </video>
      </div>
    </div>

    <div class="vf-group">
      <label class="vf-label">Ảnh bìa (Poster):</label>
      <label class="vf-dropzone" for="posterFile">
        <input type="file" id="posterFile" name="posterFile" accept="image/*">
        <div class="vf-dropzone-main" id="posterFileLabel">Chọn ảnh bìa (.jpg, .png, .webp)</div>
        <div class="vf-dropzone-sub">Nhấn để chọn ảnh từ máy tính</div>
      </label>

      <div class="vf-preview-box" id="posterPreviewWrap" style="${not empty video.poster ? '' : 'display:none;'}">
        <c:choose>
          <c:when test="${not empty video.poster}">
            <c:url value="/image" var="existingPosterSrc">
              <c:param name="fname" value="${video.poster}"/>
            </c:url>
            <img class="vf-preview-img" id="posterPreview" src="${existingPosterSrc}" alt="Poster"/>
          </c:when>
          <c:otherwise>
            <img class="vf-preview-img" id="posterPreview" src="" alt="Poster"/>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </div>

  <div class="vf-section">
    <div class="vf-section-title">2. Thông tin Video</div>

    <div class="vf-group">
      <label class="vf-label" for="title">Tiêu đề (*):</label>
      <input class="vf-input" type="text" id="title" name="title" value="<c:out value='${video.title}'/>" placeholder="Nhập tiêu đề video..." required>
    </div>

    <div class="vf-group">
      <label class="vf-label" for="categoryId">Danh mục:</label>
      <select class="vf-select" id="categoryId" name="categoryId">
        <option value="">-- Chọn danh mục --</option>
        <c:forEach items="${categories}" var="c">
          <option value="${c.categoryid}" ${video.category != null && video.category.categoryid == c.categoryid ? 'selected' : ''}>
            <c:out value="${c.categoryname}"/>
          </option>
        </c:forEach>
      </select>
    </div>

    <div class="vf-group">
      <label class="vf-label" for="description">Mô tả:</label>
      <textarea class="vf-textarea" id="description" name="description" rows="4" placeholder="Giới thiệu ngắn về nội dung video..."><c:out value="${video.description}"/></textarea>
    </div>

    <div class="vf-group">
      <label class="vf-label">Trạng thái hiển thị:</label>
      <div class="vf-radio-group">
        <label class="vf-radio-item">
          <input type="radio" name="active" value="1" ${video == null || video.active == 1 ? 'checked' : ''}>
          <span>Hoạt động (Công khai)</span>
        </label>
        <label class="vf-radio-item">
          <input type="radio" name="active" value="0" ${video != null && video.active == 0 ? 'checked' : ''}>
          <span>Ẩn</span>
        </label>
      </div>
    </div>
  </div>

  <div class="vf-actions">
    <button class="vf-btn-submit" id="submitVideoBtn" type="submit">
      ${mode == 'edit' ? 'Lưu thay đổi' : 'Đăng tải Video'}
    </button>
    <a class="vf-btn-back" href="<c:url value='${param.cancelUrl}'/>">Quay lại</a>
  </div>
</form>

<script>
  (function () {
    var videoFileInput = document.getElementById('videoFile');
    var videoPreviewWrap = document.getElementById('videoPreviewWrap');
    var videoPreview = document.getElementById('videoPreview');
    var videoFileLabel = document.getElementById('videoFileLabel');
    if (videoFileInput) {
      videoFileInput.addEventListener('change', function () {
        var file = videoFileInput.files && videoFileInput.files[0];
        if (!file) return;
        var maxBytes = 200 * 1024 * 1024;
        if (file.size > maxBytes) {
          alert('File video vượt quá giới hạn 200MB. Vui lòng chọn file nhỏ hơn.');
          videoFileInput.value = '';
          videoFileLabel.textContent = 'Chọn file video (.mp4, .webm, .ogg)';
          videoPreviewWrap.style.display = 'none';
          return;
        }
        var sizeMb = (file.size / (1024 * 1024)).toFixed(2);
        videoFileLabel.textContent = file.name + ' (' + sizeMb + ' MB)';
        videoPreview.src = URL.createObjectURL(file);
        videoPreviewWrap.style.display = '';
      });
    }

    var posterFileInput = document.getElementById('posterFile');
    var posterPreviewWrap = document.getElementById('posterPreviewWrap');
    var posterPreview = document.getElementById('posterPreview');
    var posterFileLabel = document.getElementById('posterFileLabel');
    if (posterFileInput) {
      posterFileInput.addEventListener('change', function () {
        var file = posterFileInput.files && posterFileInput.files[0];
        if (!file) return;
        posterFileLabel.textContent = file.name;
        posterPreview.src = URL.createObjectURL(file);
        posterPreviewWrap.style.display = '';
      });
    }

    var form = document.getElementById('videoUploadForm');
    var submitBtn = document.getElementById('submitVideoBtn');
    if (form && submitBtn) {
      form.addEventListener('submit', function () {
        submitBtn.disabled = true;
        submitBtn.textContent = 'Đang tải lên...';
      });
    }
  })();
</script>
