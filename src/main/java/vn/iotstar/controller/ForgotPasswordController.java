package vn.iotstar.controller;

import java.io.IOException;
import java.util.regex.Pattern;

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

@WebServlet(urlPatterns = {"/forgot-password", "/verify-forgot-otp", "/resend-forgot-otp", "/reset-password"})
public class ForgotPasswordController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UserService service;

    public ForgotPasswordController() {
        this(new UserServiceImpl());
    }

    public ForgotPasswordController(UserService service) {
        this.service = service;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if (AuthHelper.isAuthenticatedOrRestored(req, service)) {
            resp.sendRedirect(req.getContextPath() + "/waiting");
            return;
        }

        HttpSession session = req.getSession(false);
        moveSessionSuccessToRequest(req, session);

        String path = req.getServletPath();
        if ("/verify-forgot-otp".equals(path)) {
            String email = resolveEmail(req, session);
            if (email.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/forgot-password");
                return;
            }
            req.setAttribute("email", email);
            req.getRequestDispatcher(Constant.Path.VERIFY_FORGOT_OTP).forward(req, resp);
            return;
        }

        if ("/reset-password".equals(path)) {
            String verifiedEmail = getVerifiedResetEmail(session);
            if (verifiedEmail.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/forgot-password");
                return;
            }
            req.setAttribute("email", verifiedEmail);
            req.getRequestDispatcher(Constant.Path.RESET_PASSWORD).forward(req, resp);
            return;
        }

        req.getRequestDispatcher(Constant.Path.FORGOT_PASSWORD).forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String path = req.getServletPath();
        if ("/verify-forgot-otp".equals(path)) {
            handleVerifyForgotOtp(req, resp);
            return;
        }
        if ("/resend-forgot-otp".equals(path)) {
            handleResendForgotOtp(req, resp);
            return;
        }
        if ("/reset-password".equals(path)) {
            handleResetPassword(req, resp);
            return;
        }
        handleForgotPassword(req, resp);
    }

    private void handleForgotPassword(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String email = trimParam(req.getParameter("email"));
        if (email.isEmpty()) {
            forwardWithAlert(req, resp, Constant.Path.FORGOT_PASSWORD, email, "Vui lòng nhập email của bạn!");
            return;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            forwardWithAlert(req, resp, Constant.Path.FORGOT_PASSWORD, email, "Định dạng email không hợp lệ!");
            return;
        }

        OtpStatus status = service.sendForgotPasswordOtp(email);
        if (status == OtpStatus.SUCCESS) {
            HttpSession session = req.getSession(true);
            session.removeAttribute(Constant.SESSION_RESET_VERIFIED_EMAIL);
            session.setAttribute(Constant.SESSION_RESET_EMAIL, email);
            session.setAttribute(Constant.SESSION_SUCCESS,
                    "Mã OTP đặt lại mật khẩu đã được gửi tới email của bạn.");
            resp.sendRedirect(req.getContextPath() + "/verify-forgot-otp");
            return;
        }
        forwardOtpFailure(req, resp, Constant.Path.FORGOT_PASSWORD, email, status);
    }

    private void handleVerifyForgotOtp(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String email = resolveEmail(req, session);
        String otp = trimParam(req.getParameter("otp"));

        if (email.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password");
            return;
        }
        if (otp.isEmpty()) {
            forwardWithAlert(req, resp, Constant.Path.VERIFY_FORGOT_OTP, email, "Vui lòng nhập mã OTP!");
            return;
        }

        OtpStatus status = service.verifyForgotPasswordOtp(email, otp);
        if (status == OtpStatus.SUCCESS) {
            HttpSession activeSession = req.getSession(true);
            activeSession.setAttribute(Constant.SESSION_RESET_EMAIL, email);
            activeSession.setAttribute(Constant.SESSION_RESET_VERIFIED_EMAIL, email);
            activeSession.setAttribute(Constant.SESSION_SUCCESS,
                    "Xác thực OTP thành công! Vui lòng đặt mật khẩu mới.");
            resp.sendRedirect(req.getContextPath() + "/reset-password");
            return;
        }
        forwardOtpFailure(req, resp, Constant.Path.VERIFY_FORGOT_OTP, email, status);
    }

    private void handleResendForgotOtp(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String email = resolveEmail(req, session);
        if (email.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password");
            return;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            forwardWithAlert(req, resp, Constant.Path.VERIFY_FORGOT_OTP, email, "Định dạng email không hợp lệ!");
            return;
        }

        OtpStatus status = service.sendForgotPasswordOtp(email);
        if (status == OtpStatus.SUCCESS) {
            req.getSession(true).setAttribute(Constant.SESSION_RESET_EMAIL, email);
            req.setAttribute("email", email);
            req.setAttribute("success", "Mã OTP mới đã được gửi tới email của bạn.");
            req.getRequestDispatcher(Constant.Path.VERIFY_FORGOT_OTP).forward(req, resp);
            return;
        }
        forwardOtpFailure(req, resp, Constant.Path.VERIFY_FORGOT_OTP, email, status);
    }

    private void handleResetPassword(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        String verifiedEmail = getVerifiedResetEmail(session);
        if (verifiedEmail.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password");
            return;
        }

        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");
        if (newPassword == null || newPassword.isEmpty() || confirmPassword == null || confirmPassword.isEmpty()) {
            forwardWithAlert(req, resp, Constant.Path.RESET_PASSWORD, verifiedEmail,
                    "Vui lòng nhập đầy đủ mật khẩu mới và xác nhận mật khẩu!");
            return;
        }
        if (newPassword.length() < 6) {
            forwardWithAlert(req, resp, Constant.Path.RESET_PASSWORD, verifiedEmail,
                    "Mật khẩu mới phải có ít nhất 6 ký tự!");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            forwardWithAlert(req, resp, Constant.Path.RESET_PASSWORD, verifiedEmail,
                    "Mật khẩu xác nhận không khớp!");
            return;
        }

        OtpStatus status = service.resetPassword(verifiedEmail, newPassword);
        if (status == OtpStatus.SUCCESS) {
            if (session != null) {
                session.removeAttribute(Constant.SESSION_RESET_EMAIL);
                session.removeAttribute(Constant.SESSION_RESET_VERIFIED_EMAIL);
            }
            req.getSession(true).setAttribute(Constant.SESSION_SUCCESS,
                    "Đặt lại mật khẩu thành công! Vui lòng đăng nhập bằng mật khẩu mới.");
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        forwardOtpFailure(req, resp, Constant.Path.RESET_PASSWORD, verifiedEmail, status);
    }

    private void forwardOtpFailure(HttpServletRequest req, HttpServletResponse resp,
                                   String viewPath, String email, OtpStatus status)
            throws ServletException, IOException {
        switch (status) {
            case USER_NOT_FOUND:
                forwardWithAlert(req, resp, viewPath, email, "Không tìm thấy tài khoản nào với email này!");
                break;
            case INACTIVE_ACCOUNT:
                req.getSession(true).setAttribute(Constant.SESSION_PENDING_EMAIL, email);
                req.setAttribute("needVerify", true);
                forwardWithAlert(req, resp, viewPath, email,
                        "Tài khoản chưa được kích hoạt. Vui lòng xác thực tài khoản trước!");
                break;
            case EXPIRED_OTP:
                forwardWithAlert(req, resp, viewPath, email, "Mã OTP đã hết hạn! Vui lòng gửi lại mã mới.");
                break;
            case SEND_FAILED:
                forwardWithAlert(req, resp, viewPath, email,
                        "Không thể gửi email OTP. Vui lòng kiểm tra cấu hình SMTP hoặc thử lại sau!");
                break;
            case INVALID_OTP:
            default:
                forwardWithAlert(req, resp, viewPath, email, "Mã OTP không chính xác!");
                break;
        }
    }

    private void moveSessionSuccessToRequest(HttpServletRequest req, HttpSession session) {
        if (session != null) {
            Object successMsg = session.getAttribute(Constant.SESSION_SUCCESS);
            if (successMsg != null) {
                req.setAttribute("success", successMsg);
                session.removeAttribute(Constant.SESSION_SUCCESS);
            }
        }
    }

    private String getVerifiedResetEmail(HttpSession session) {
        if (session == null) {
            return "";
        }
        Object verified = session.getAttribute(Constant.SESSION_RESET_VERIFIED_EMAIL);
        return verified == null ? "" : verified.toString().trim();
    }

    private String resolveEmail(HttpServletRequest req, HttpSession session) {
        String emailParam = trimParam(req.getParameter("email"));
        if (!emailParam.isEmpty()) {
            return emailParam;
        }
        if (session != null) {
            Object resetEmail = session.getAttribute(Constant.SESSION_RESET_EMAIL);
            if (resetEmail != null) {
                return resetEmail.toString().trim();
            }
        }
        return "";
    }

    private String trimParam(String value) {
        return value == null ? "" : value.trim();
    }

    private void forwardWithAlert(HttpServletRequest req, HttpServletResponse resp,
                                  String viewPath, String email, String alertMsg)
            throws ServletException, IOException {
        req.setAttribute("email", email);
        req.setAttribute("alert", alertMsg);
        req.getRequestDispatcher(viewPath).forward(req, resp);
    }
}
