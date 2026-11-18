package vn.iotstar.controller;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.entity.User;
import vn.iotstar.entity.Video;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.IVideoService;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.service.impl.VideoServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.VideoHelper;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024L * 1024 * 200,
        maxRequestSize = 1024L * 1024 * 250
)
@WebServlet(urlPatterns = {
        "/video", "/video/detail", "/video/add", "/video/insert",
        "/video/edit", "/video/update", "/video/delete"
})
public class VideoController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final int PAGE_SIZE = 6;

    private final IVideoService videoService = new VideoServiceImpl();
    private final ICategoryService categoryService = new CategoryServiceImpl();
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String path = req.getServletPath();
        if ("/video/detail".equals(path)) {
            handleDetail(req, resp);
            return;
        }

        User account = VideoHelper.getAuthenticatedUser(req, userService);
        if (account == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if ("/video".equals(path)) {
            handleUserVideoList(req, resp, account);
        } else if ("/video/add".equals(path)) {
            req.setAttribute("categories", categoryService.findAll());
            req.setAttribute("mode", "add");
            req.getRequestDispatcher(Constant.Path.VIDEO_FORM).forward(req, resp);
        } else if ("/video/edit".equals(path)) {
            String videoId = req.getParameter("id");
            Video video = videoService.findById(videoId);
            if (!isOwnerOrAdmin(video, account)) {
                resp.sendRedirect(req.getContextPath() + "/video");
                return;
            }
            req.setAttribute("video", video);
            req.setAttribute("categories", categoryService.findAll());
            req.setAttribute("mode", "edit");
            req.getRequestDispatcher(Constant.Path.VIDEO_FORM).forward(req, resp);
        } else if ("/video/delete".equals(path)) {
            String videoId = req.getParameter("id");
            Video video = videoService.findById(videoId);
            if (isOwnerOrAdmin(video, account)) {
                try {
                    VideoHelper.deleteVideoAndMedia(video, videoService);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            resp.sendRedirect(req.getContextPath() + "/video");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        User account = VideoHelper.getAuthenticatedUser(req, userService);
        if (account == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String path = req.getServletPath();
        if ("/video/insert".equals(path)) {
            Video video = new Video();
            video.setUser(account);
            String error = VideoHelper.populateVideoFromRequest(req, video, categoryService, videoService, true);
            if (error != null) {
                req.setAttribute("error", error);
                req.setAttribute("video", video);
                req.setAttribute("categories", categoryService.findAll());
                req.setAttribute("mode", "add");
                req.getRequestDispatcher(Constant.Path.VIDEO_FORM).forward(req, resp);
                return;
            }
            videoService.insert(video);
            resp.sendRedirect(req.getContextPath() + "/video");
        } else if ("/video/update".equals(path)) {
            String videoId = req.getParameter("videoId");
            Video video = videoService.findById(videoId);
            if (!isOwnerOrAdmin(video, account)) {
                resp.sendRedirect(req.getContextPath() + "/video");
                return;
            }
            String error = VideoHelper.populateVideoFromRequest(req, video, categoryService, videoService, false);
            if (error != null) {
                req.setAttribute("error", error);
                req.setAttribute("video", video);
                req.setAttribute("categories", categoryService.findAll());
                req.setAttribute("mode", "edit");
                req.getRequestDispatcher(Constant.Path.VIDEO_FORM).forward(req, resp);
                return;
            }
            videoService.update(video);
            resp.sendRedirect(req.getContextPath() + "/video");
        }
    }

    private void handleUserVideoList(HttpServletRequest req, HttpServletResponse resp, User account)
            throws ServletException, IOException {
        int totalItems = videoService.countByUserId(account.getId());
        int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / PAGE_SIZE));
        int currentPage = parsePage(req.getParameter("page"), totalPages);

        List<Video> videos = videoService.findByUserId(account.getId(), currentPage - 1, PAGE_SIZE);
        req.setAttribute("videos", videos);
        req.setAttribute("currentPage", currentPage);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalItems", totalItems);
        req.setAttribute("pageSize", PAGE_SIZE);
        req.getRequestDispatcher(Constant.Path.VIDEO_LIST).forward(req, resp);
    }

    private void handleDetail(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String videoId = req.getParameter("id");
        Video video = videoService.findById(videoId);
        if (video == null) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        User account = VideoHelper.getAuthenticatedUser(req, userService);
        if (video.getActive() != 1 && !isOwnerOrAdmin(video, account)) {
            resp.sendRedirect(req.getContextPath() + "/home");
            return;
        }

        videoService.incrementViews(video.getVideoId());
        video.setViews(video.getViews() + 1);

        req.setAttribute("video", video);
        req.setAttribute("fromPage", req.getParameter("from"));
        req.getRequestDispatcher(Constant.Path.VIDEO_DETAIL).forward(req, resp);
    }

    private boolean isOwnerOrAdmin(Video video, User account) {
        if (video == null || account == null) {
            return false;
        }
        if (account.getRoleid() == 1) {
            return true;
        }
        return video.getUser() != null && video.getUser().getId() == account.getId();
    }

    private int parsePage(String pageParam, int totalPages) {
        int page = 1;
        if (pageParam != null && !pageParam.isBlank()) {
            try {
                page = Integer.parseInt(pageParam.trim());
            } catch (NumberFormatException ignored) {
                page = 1;
            }
        }
        if (page < 1) {
            return 1;
        }
        return Math.min(page, totalPages);
    }
}
