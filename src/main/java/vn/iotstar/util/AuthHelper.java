package vn.iotstar.util;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.User;
import vn.iotstar.service.UserService;

public final class AuthHelper {

    private AuthHelper() {
    }

    public static boolean isAuthenticatedOrRestored(HttpServletRequest req, UserService service) {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute(Constant.SESSION_ACCOUNT) != null) {
            return true;
        }
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (Constant.COOKIE_REMEMBER.equals(cookie.getName())) {
                    User user = service.get(cookie.getValue());
                    if (user != null && user.getStatus() == 1) {
                        req.getSession(true).setAttribute(Constant.SESSION_ACCOUNT, user);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static String resolveCookiePath(HttpServletRequest req) {
        String path = req.getContextPath();
        return (path == null || path.isEmpty()) ? "/" : path;
    }

    public static void forwardIfAuthorized(HttpServletRequest req, HttpServletResponse resp,
                                           int requiredRoleId, String viewPath)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute(Constant.SESSION_ACCOUNT) == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        User account = (User) session.getAttribute(Constant.SESSION_ACCOUNT);
        if (account.getRoleid() != requiredRoleId) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }
        req.getRequestDispatcher(viewPath).forward(req, resp);
    }
}
