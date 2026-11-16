package vn.iotstar.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.service.OtpStatus;
import vn.iotstar.service.UserService;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.AuthHelper;
import vn.iotstar.util.Constant;

@WebServlet(urlPatterns = {"/verify-otp", "/resend-otp"})
public class VerifyOtpController extends HttpServlet {
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
        String email = resolveEmail(req, session);
        if (session != null) {
            Object successMsg = session.getAttribute(Constant.SESSION_SUCCESS);
            if (successMsg != null) {
                req.setAttribute("success", successMsg);
                session.removeAttribute(Constant.SESSION_SUCCESS);
            }
        }
        req.setAttribute("email", email);
        req.getRequestDispatcher(Constant.Path.VERIFY_OTP).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        String email = resolveEmail(req, session);
        String path = req.getServletPath();

        if ("/resend-otp".equals(path)) {
            handleResendOtp(req, resp, session, email);
            return;
        }

        handleVerifyOtp(req, resp, session, email);
    }

    private void handleVerifyOtp(HttpServletRequest req, HttpServletResponse resp,
                                 HttpSession session, String email)
            throws ServletException, IOException {
        String otp = req.getParameter("otp");
        if (email.isEmpty()) {
            forwardWithAlert(req, resp, email, "Vui lòng nhập email đã đăng ký!");
            return;
        }
        if (otp == null || otp.trim().isEmpty()) {
            forwardWithAlert(req, resp, email, "Vui lòng nhập mã OTP!");
            return;
        }

        OtpStatus status = service.verifyOtp(email, otp);
        switch (status) {
            case SUCCESS:
                if (session != null) {
                    session.removeAttribute(Constant.SESSION_PENDING_EMAIL);
                }
                req.getSession(true).setAttribute(Constant.SESSION_SUCCESS,
                        "Kích hoạt tài khoản thành công! Vui lòng đăng nhập.");
                resp.sendRedirect(req.getContextPath() + "/login");
                break;
            case USER_NOT_FOUND:
                forwardWithAlert(req, resp, email, "Không tìm thấy tài khoản với email này!");
                break;
            case ALREADY_ACTIVE:
                if (session != null) {
                    session.removeAttribute(Constant.SESSION_PENDING_EMAIL);
                }
                req.getSession(true).setAttribute(Constant.SESSION_SUCCESS,
                        "Tài khoản đã được kích hoạt trước đó. Vui lòng đăng nhập.");
                resp.sendRedirect(req.getContextPath() + "/login");
                break;
            case EXPIRED_OTP:
                forwardWithAlert(req, resp, email, "Mã OTP đã hết hạn! Vui lòng gửi lại mã mới.");
                break;
            case INVALID_OTP:
            default:
                forwardWithAlert(req, resp, email, "Mã OTP không chính xác!");
                break;
        }
    }

    private void handleResendOtp(HttpServletRequest req, HttpServletResponse resp,
                                 HttpSession session, String email)
            throws ServletException, IOException {
        if (email.isEmpty()) {
            forwardWithAlert(req, resp, email, "Vui lòng nhập email để gửi lại mã OTP!");
            return;
        }
        req.getSession(true).setAttribute(Constant.SESSION_PENDING_EMAIL, email);

        OtpStatus status = service.resendOtp(email);
        switch (status) {
            case SUCCESS:
                req.setAttribute("email", email);
                req.setAttribute("success", "Mã OTP mới đã được gửi tới email của bạn.");
                req.getRequestDispatcher(Constant.Path.VERIFY_OTP).forward(req, resp);
                break;
            case USER_NOT_FOUND:
                forwardWithAlert(req, resp, email, "Không tìm thấy tài khoản với email này!");
                break;
            case ALREADY_ACTIVE:
                if (session != null) {
                    session.removeAttribute(Constant.SESSION_PENDING_EMAIL);
                }
                req.getSession(true).setAttribute(Constant.SESSION_SUCCESS,
                        "Tài khoản đã được kích hoạt trước đó. Vui lòng đăng nhập.");
                resp.sendRedirect(req.getContextPath() + "/login");
                break;
            case SEND_FAILED:
            default:
                forwardWithAlert(req, resp, email,
                        "Không thể gửi lại email OTP. Vui lòng kiểm tra cấu hình SMTP hoặc thử lại sau!");
                break;
        }
    }

    private String resolveEmail(HttpServletRequest req, HttpSession session) {
        String emailParam = req.getParameter("email");
        if (emailParam != null && !emailParam.trim().isEmpty()) {
            return emailParam.trim();
        }
        if (session != null) {
            Object pendingEmail = session.getAttribute(Constant.SESSION_PENDING_EMAIL);
            if (pendingEmail != null) {
                return pendingEmail.toString().trim();
            }
        }
        return "";
    }

    private void forwardWithAlert(HttpServletRequest req, HttpServletResponse resp,
                                  String email, String alertMsg)
            throws ServletException, IOException {
        req.setAttribute("email", email);
        req.setAttribute("alert", alertMsg);
        req.getRequestDispatcher(Constant.Path.VERIFY_OTP).forward(req, resp);
    }
}
