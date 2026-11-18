package vn.iotstar.util;

import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.User;
import vn.iotstar.entity.Video;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IVideoService;
import vn.iotstar.service.UserService;

public final class VideoHelper {

    private VideoHelper() {
    }

    public static User getAuthenticatedUser(HttpServletRequest req, UserService userService) {
        if (!AuthHelper.isAuthenticatedOrRestored(req, userService)) {
            return null;
        }
        HttpSession session = req.getSession(false);
        return session != null ? (User) session.getAttribute(Constant.SESSION_ACCOUNT) : null;
    }

    public static String populateVideoFromRequest(HttpServletRequest req, Video video,
                                                  ICategoryService categoryService,
                                                  IVideoService videoService,
                                                  boolean isCreate)
            throws IOException, ServletException {
        String title = trimToNull(req.getParameter("title"));
        if (title == null) {
            return "Tiêu đề video không được để trống.";
        }

        if (isCreate) {
            String videoId = trimToNull(req.getParameter("videoId"));
            if (videoId == null) {
                videoId = "VID_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
            } else if (videoService.findById(videoId) != null) {
                return "Mã video '" + videoId + "' đã tồn tại.";
            }
            video.setVideoId(videoId);
            video.setViews(0);
        }

        String oldPoster = isCreate ? null : video.getPoster();
        String oldVideoUrl = isCreate ? null : video.getVideoUrl();

        Part posterPart = safeGetPart(req, "posterFile");
        Part videoPart = safeGetPart(req, "videoFile");

        String poster = FileUploadUtil.saveUploadedFile(posterPart, oldPoster, oldPoster, "png");
        String videoSource = FileUploadUtil.saveUploadedFile(videoPart, oldVideoUrl, oldVideoUrl, "mp4");

        video.setTitle(title);
        video.setDescription(trimToNull(req.getParameter("description")));
        video.setActive(parseActive(req.getParameter("active")));
        video.setPoster(poster);
        video.setVideoUrl(videoSource);

        String cateIdParam = trimToNull(req.getParameter("categoryId"));
        if (cateIdParam != null) {
            try {
                int categoryId = Integer.parseInt(cateIdParam);
                Category category = categoryService.findById(categoryId);
                video.setCategory(category);
            } catch (NumberFormatException ignored) {
                video.setCategory(null);
            }
        } else {
            video.setCategory(null);
        }
        return null;
    }

    public static void deleteVideoAndMedia(Video video, IVideoService videoService) throws Exception {
        if (video == null) {
            return;
        }
        String poster = video.getPoster();
        String videoUrl = video.getVideoUrl();
        videoService.delete(video.getVideoId());
        FileUploadUtil.deleteLocalFile(poster);
        FileUploadUtil.deleteLocalFile(videoUrl);
    }

    private static Part safeGetPart(HttpServletRequest req, String name)
            throws IOException, ServletException {
        try {
            return req.getPart(name);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static int parseActive(String activeParam) {
        if ("0".equals(activeParam)) {
            return 0;
        }
        return 1;
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
