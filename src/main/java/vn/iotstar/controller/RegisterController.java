package vn.iotstar.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.AuthHelper;
import vn.iotstar.util.Constant;

@WebServlet(urlPatterns = "/register")
public class RegisterController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserService service = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (AuthHelper.isAuthenticatedOrRestored(req, service)) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }
        req.getRequestDispatcher(Constant.Path.REGISTER).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        req.setCharacterEncoding("UTF-8");

        String username = trimParam(req.getParameter("username"));
        String password = req.getParameter("password");
        String repassword = req.getParameter("repassword");
        String email = trimParam(req.getParameter("email"));
        String fullname = trimParam(req.getParameter("fullname"));
        String phone = trimParam(req.getParameter("phone"));

        if (username.isEmpty() || password == null || password.isEmpty()
                || repassword == null || repassword.isEmpty()
                || email.isEmpty() || fullname.isEmpty()) {
            forwardWithAlert(req, resp, "Vui lòng nhập đầy đủ thông tin bắt buộc!");
            return;
        }

        if (!password.equals(repassword)) {
            forwardWithAlert(req, resp, "Mật khẩu nhập lại không khớp!");
            return;
        }

        if (service.checkExistEmail(email)) {
            forwardWithAlert(req, resp, "Email đã tồn tại!");
            return;
        }

        if (service.checkExistUsername(username)) {
            forwardWithAlert(req, resp, "Tài khoản đã tồn tại!");
            return;
        }

        if (!phone.isEmpty() && service.checkExistPhone(phone)) {
            forwardWithAlert(req, resp, "Số điện thoại đã tồn tại!");
            return;
        }

        boolean isSuccess = service.register(username, password, email, fullname, phone.isEmpty() ? null : phone);
        if (isSuccess) {
            resp.sendRedirect(req.getContextPath() + "/login");
        } else {
            forwardWithAlert(req, resp, "System error!");
        }
    }

    private String trimParam(String value) {
        return value == null ? "" : value.trim();
    }

    private void forwardWithAlert(HttpServletRequest req, HttpServletResponse resp, String alertMsg)
            throws ServletException, IOException {
        req.setAttribute("alert", alertMsg);
        req.getRequestDispatcher(Constant.Path.REGISTER).forward(req, resp);
    }
}
