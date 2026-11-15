package vn.iotstar.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.util.AuthHelper;
import vn.iotstar.util.Constant;

@WebServlet(urlPatterns = "/admin/home")
public class AdminHomeController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        AuthHelper.forwardIfAuthorized(req, resp, 1, Constant.Path.ADMIN_HOME);
    }
}
