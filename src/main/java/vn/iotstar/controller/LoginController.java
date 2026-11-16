package vn.iotstar.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.User;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.AuthHelper;
import vn.iotstar.util.Constant;

@WebServlet(urlPatterns = "/login")
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserService service = new UserServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (AuthHelper.isAuthenticatedOrRestored(req, service)) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }
        HttpSession session = req.getSession(false);
        if (session != null) {
            Object successMsg = session.getAttribute(Constant.SESSION_SUCCESS);
            if (successMsg != null) {
                req.setAttribute("success", successMsg);
                session.removeAttribute(Constant.SESSION_SUCCESS);
            }
        }
        req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        boolean isRememberMe = "on".equals(req.getParameter("remember"));

        if (username == null || password == null
                || username.trim().isEmpty() || password.isEmpty()) {
            req.setAttribute("alert", "Tài khoản hoặc mật khẩu không được rỗng");
            req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
            return;
        }

        User user = service.login(username.trim(), password);
        if (user != null) {
            if (user.getStatus() != 1) {
                HttpSession session = req.getSession(true);
                session.setAttribute(Constant.SESSION_PENDING_EMAIL, user.getEmail());
                req.setAttribute("alert", "Tài khoản chưa được kích hoạt. Vui lòng xác thực mã OTP qua email.");
                req.setAttribute("needVerify", true);
                req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
                return;
            }
            HttpSession session = req.getSession(true);
            session.setAttribute(Constant.SESSION_ACCOUNT, user);

            if (isRememberMe) {
                saveRememberMe(req, resp, user.getUserName());
            }
            resp.sendRedirect(req.getContextPath() + "/waiting");
        } else {
            req.setAttribute("alert", "Tài khoản hoặc mật khẩu không đúng");
            req.getRequestDispatcher(Constant.Path.LOGIN).forward(req, resp);
        }
    }

    private void saveRememberMe(HttpServletRequest req, HttpServletResponse resp, String username) {
        Cookie cookie = new Cookie(Constant.COOKIE_REMEMBER, username);
        cookie.setMaxAge(30 * 60);
        cookie.setPath(AuthHelper.resolveCookiePath(req));
        resp.addCookie(cookie);
    }
}
