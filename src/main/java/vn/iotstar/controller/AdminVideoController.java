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
        "/admin/videos", "/admin/video/add", "/admin/video/insert",
        "/admin/video/edit", "/admin/video/update", "/admin/video/delete"
})
public class AdminVideoController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final IVideoService videoService = new VideoServiceImpl();
    private final ICategoryService categoryService = new CategoryServiceImpl();
    private final UserService userService = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        User admin = requireAdmin(req, resp);
        if (admin == null) {
            return;
        }

        String path = req.getServletPath();
        if ("/admin/videos".equals(path)) {
            List<Video> list = videoService.findAll();
            req.setAttribute("videos", list);
            req.getRequestDispatcher(Constant.Path.ADMIN_VIDEO_LIST).forward(req, resp);
        } else if ("/admin/video/add".equals(path)) {
            req.setAttribute("categories", categoryService.findAll());
            req.setAttribute("mode", "add");
            req.getRequestDispatcher(Constant.Path.ADMIN_VIDEO_FORM).forward(req, resp);
        } else if ("/admin/video/edit".equals(path)) {
            String videoId = req.getParameter("id");
            Video video = videoService.findById(videoId);
            if (video == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/videos");
                return;
            }
            req.setAttribute("video", video);
            req.setAttribute("categories", categoryService.findAll());
            req.setAttribute("mode", "edit");
            req.getRequestDispatcher(Constant.Path.ADMIN_VIDEO_FORM).forward(req, resp);
        } else if ("/admin/video/delete".equals(path)) {
            String videoId = req.getParameter("id");
            Video video = videoService.findById(videoId);
            if (video != null) {
                try {
                    VideoHelper.deleteVideoAndMedia(video, videoService);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            resp.sendRedirect(req.getContextPath() + "/admin/videos");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        User admin = requireAdmin(req, resp);
        if (admin == null) {
            return;
        }

        String path = req.getServletPath();
        if ("/admin/video/insert".equals(path)) {
            Video video = new Video();
            video.setUser(admin);
            String error = VideoHelper.populateVideoFromRequest(req, video, categoryService, videoService, true);
            if (error != null) {
                req.setAttribute("error", error);
                req.setAttribute("video", video);
                req.setAttribute("categories", categoryService.findAll());
                req.setAttribute("mode", "add");
                req.getRequestDispatcher(Constant.Path.ADMIN_VIDEO_FORM).forward(req, resp);
                return;
            }
            videoService.insert(video);
            resp.sendRedirect(req.getContextPath() + "/admin/videos");
        } else if ("/admin/video/update".equals(path)) {
            String videoId = req.getParameter("videoId");
            Video video = videoService.findById(videoId);
            if (video == null) {
                resp.sendRedirect(req.getContextPath() + "/admin/videos");
                return;
            }
            if (video.getUser() == null) {
                video.setUser(admin);
            }
            String error = VideoHelper.populateVideoFromRequest(req, video, categoryService, videoService, false);
            if (error != null) {
                req.setAttribute("error", error);
                req.setAttribute("video", video);
                req.setAttribute("categories", categoryService.findAll());
                req.setAttribute("mode", "edit");
                req.getRequestDispatcher(Constant.Path.ADMIN_VIDEO_FORM).forward(req, resp);
                return;
            }
            videoService.update(video);
            resp.sendRedirect(req.getContextPath() + "/admin/videos");
        }
    }

    private User requireAdmin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        User account = VideoHelper.getAuthenticatedUser(req, userService);
        if (account == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return null;
        }
        if (account.getRoleid() != 1) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return null;
        }
        return account;
    }
}
