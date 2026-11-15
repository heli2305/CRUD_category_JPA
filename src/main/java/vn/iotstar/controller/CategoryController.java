package vn.iotstar.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import vn.iotstar.entity.Category;
import vn.iotstar.service.ICategoryService;
import vn.iotstar.service.impl.CategoryServiceImpl;
import vn.iotstar.util.Constant;

@MultipartConfig()
@WebServlet(urlPatterns = { "/admin/categories", "/admin/category/add", "/admin/category/insert",
        "/admin/category/edit", "/admin/category/update", "/admin/category/delete" })
public class CategoryController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    public ICategoryService cateService = new CategoryServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String url = req.getRequestURI();
        if (url.contains("/admin/categories")) {
            List<Category> list = cateService.findAll();
            req.setAttribute("listcate", list);
            req.getRequestDispatcher(Constant.Path.CATEGORY_LIST).forward(req, resp);
        } else if (url.contains("/admin/category/add")) {
            req.getRequestDispatcher(Constant.Path.CATEGORY_ADD).forward(req, resp);
        } else if (url.contains("/admin/category/edit")) {
            int id = Integer.parseInt(req.getParameter("id"));
            Category category = cateService.findById(id);
            req.setAttribute("cate", category);
            req.getRequestDispatcher(Constant.Path.CATEGORY_EDIT).forward(req, resp);
        } else if (url.contains("/admin/category/delete")) {
            int id = Integer.parseInt(req.getParameter("id"));
            try {
                cateService.delete(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String url = req.getRequestURI();
        if (url.contains("/admin/category/insert")) {
            String categoryname = req.getParameter("categoryname");
            int status = parseStatus(req.getParameter("status"));
            String images = req.getParameter("images");

            Category category = new Category();
            category.setCategoryname(categoryname);
            category.setStatus(status);
            category.setImages(resolveImage(req, images, "avatar.png", null));

            cateService.insert(category);
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
        } else if (url.contains("/admin/category/update")) {
            int categoryid = Integer.parseInt(req.getParameter("categoryid"));
            String categoryname = req.getParameter("categoryname");
            int status = parseStatus(req.getParameter("status"));
            String images = req.getParameter("images");

            Category category = cateService.findById(categoryid);
            if (category != null) {
                String fileold = category.getImages();
                category.setCategoryname(categoryname);
                category.setStatus(status);
                category.setImages(resolveImage(req, images, fileold, fileold));
                cateService.update(category);
            }
            resp.sendRedirect(req.getContextPath() + "/admin/categories");
        }
    }

    private String resolveImage(HttpServletRequest req, String imageUrlParam,
                                String fallbackImage, String oldLocalImage)
            throws IOException, ServletException {
        File uploadDir = new File(Constant.DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        Part part = req.getPart("images1");
        if (part != null && part.getSize() > 0 && part.getSubmittedFileName() != null
                && !part.getSubmittedFileName().isBlank()) {
            if (oldLocalImage != null && !oldLocalImage.isBlank() && !oldLocalImage.startsWith("https")) {
                deleteFile(Constant.DIR + File.separator + oldLocalImage);
            }
            String filename = Paths.get(part.getSubmittedFileName()).getFileName().toString();
            int index = filename.lastIndexOf(".");
            String ext = (index >= 0) ? filename.substring(index + 1) : "png";
            String fname = System.currentTimeMillis() + "." + ext;
            part.write(Constant.DIR + File.separator + fname);
            return fname;
        }
        if (imageUrlParam != null && !imageUrlParam.trim().isEmpty()) {
            return imageUrlParam.trim();
        }
        return fallbackImage;
    }

    private int parseStatus(String rawStatus) {
        if (rawStatus == null || rawStatus.trim().isEmpty()) {
            return 1;
        }
        return Integer.parseInt(rawStatus.trim());
    }

    public static void deleteFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        Files.deleteIfExists(path);
    }
}
