package vn.iotstar.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.controller.ForgotPasswordController;
import vn.iotstar.dao.UserDao;
import vn.iotstar.entity.User;
import vn.iotstar.service.EmailService;
import vn.iotstar.service.OtpStatus;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.Constant;
import vn.iotstar.util.PasswordUtil;

class ForgotPasswordTest {

    @Test
    void testThreeStepForgotVerifyAndResetPasswordSuccess() {
        InMemoryUserDao userDao = new InMemoryUserDao();
        RecordingEmailService emailService = new RecordingEmailService();
        UserServiceImpl service = new UserServiceImpl(userDao, emailService);

        User activeUser = createActiveUser("resetuser", "reset@example.com", "OldPass@123");
        userDao.insert(activeUser);

        OtpStatus sendStatus = service.sendForgotPasswordOtp("reset@example.com");
        assertEquals(OtpStatus.SUCCESS, sendStatus);
        assertEquals(1, emailService.resetSentCount);
        assertEquals(0, emailService.registerSentCount);
        assertEquals("reset@example.com", emailService.lastToEmail);
        assertNotNull(emailService.lastOtp);
        assertTrue(emailService.lastOtp.matches("\\d{6}"));

        User stored = userDao.findByEmail("reset@example.com");
        assertEquals(emailService.lastOtp, stored.getOtp());
        assertNotNull(stored.getOtpExpiry());
        assertTrue(stored.getOtpExpiry().isAfter(LocalDateTime.now()));

        OtpStatus verifyStatus = service.verifyForgotPasswordOtp("reset@example.com", emailService.lastOtp);
        assertEquals(OtpStatus.SUCCESS, verifyStatus);
        assertNull(stored.getOtp());
        assertNull(stored.getOtpExpiry());

        OtpStatus resetStatus = service.resetPassword("reset@example.com", "NewPass@456");
        assertEquals(OtpStatus.SUCCESS, resetStatus);
        assertTrue(PasswordUtil.isHashed(stored.getPassWord()));
        assertNotEquals("NewPass@456", stored.getPassWord());

        assertNull(service.login("resetuser", "OldPass@123"));
        assertNotNull(service.login("resetuser", "NewPass@456"));
    }

    @Test
    void testForgotPasswordRejectsUnknownInactiveAndRestoresOnMailError() {
        InMemoryUserDao userDao = new InMemoryUserDao();
        RecordingEmailService emailService = new RecordingEmailService();
        UserServiceImpl service = new UserServiceImpl(userDao, emailService);

        assertEquals(OtpStatus.USER_NOT_FOUND, service.sendForgotPasswordOtp("missing@example.com"));
        assertEquals(OtpStatus.USER_NOT_FOUND, service.sendForgotPasswordOtp("  "));

        User inactive = createActiveUser("inactive", "inactive@example.com", "Pass@123");
        inactive.setStatus(0);
        userDao.insert(inactive);
        assertEquals(OtpStatus.INACTIVE_ACCOUNT, service.sendForgotPasswordOtp("inactive@example.com"));
        assertEquals(0, emailService.resetSentCount);

        User active = createActiveUser("mailfail", "mailfail@example.com", "Pass@123");
        LocalDateTime oldExpiry = LocalDateTime.now().plusMinutes(3);
        active.setOtp("111222");
        active.setOtpExpiry(oldExpiry);
        userDao.insert(active);

        EmailService failingEmailService = new EmailService() {
            @Override
            public void sendOtpEmail(String toEmail, String fullName, String otp) {
            }

            @Override
            public void sendResetPasswordOtpEmail(String toEmail, String fullName, String otp) {
                throw new IllegalStateException("SMTP error");
            }
        };
        UserServiceImpl failingService = new UserServiceImpl(userDao, failingEmailService);

        assertEquals(OtpStatus.SEND_FAILED, failingService.sendForgotPasswordOtp("mailfail@example.com"));
        assertEquals("111222", active.getOtp());
        assertEquals(oldExpiry, active.getOtpExpiry());
    }

    @Test
    void testVerifyOtpAndResetPasswordRejectsInvalidExpiredAndBlankPassword() {
        InMemoryUserDao userDao = new InMemoryUserDao();
        RecordingEmailService emailService = new RecordingEmailService();
        UserServiceImpl service = new UserServiceImpl(userDao, emailService);

        User active = createActiveUser("otpcheck", "otpcheck@example.com", "OldPass@123");
        userDao.insert(active);

        assertEquals(OtpStatus.SUCCESS, service.sendForgotPasswordOtp("otpcheck@example.com"));
        String validOtp = emailService.lastOtp;
        String wrongOtp = "000000".equals(validOtp) ? "111111" : "000000";

        assertEquals(OtpStatus.INVALID_OTP, service.verifyForgotPasswordOtp("otpcheck@example.com", wrongOtp));
        assertNotNull(active.getOtp());

        active.setOtpExpiry(LocalDateTime.now().minusSeconds(5));
        assertEquals(OtpStatus.EXPIRED_OTP, service.verifyForgotPasswordOtp("otpcheck@example.com", validOtp));

        assertThrows(IllegalArgumentException.class, () -> service.resetPassword("otpcheck@example.com", ""));
        assertThrows(IllegalArgumentException.class, () -> service.resetPassword("otpcheck@example.com", null));
    }

    @Test
    void testThreeStepControllerFlowAndGuards() throws Exception {
        InMemoryUserDao userDao = new InMemoryUserDao();
        RecordingEmailService emailService = new RecordingEmailService();
        UserServiceImpl service = new UserServiceImpl(userDao, emailService);
        userDao.insert(createActiveUser("ctrluser", "ctrl@example.com", "Init@123"));

        TestableForgotPasswordController controller = new TestableForgotPasswordController(service);
        Map<String, Object> sessionAttrs = new HashMap<>();
        HttpSession session = createSessionProxy(sessionAttrs);

        MockResponseCapture unverifiedGetResp = new MockResponseCapture();
        HttpServletRequest unverifiedGetReq = createRequestProxy("/reset-password", Map.of(), session, unverifiedGetResp);
        controller.invokeGet(unverifiedGetReq, unverifiedGetResp.response);
        assertEquals("/JPA/forgot-password", unverifiedGetResp.redirectedUrl);

        MockResponseCapture forgotResp = new MockResponseCapture();
        HttpServletRequest forgotReq = createRequestProxy("/forgot-password",
                Map.of("email", "ctrl@example.com"), session, forgotResp);
        controller.invokePost(forgotReq, forgotResp.response);

        assertEquals("/JPA/verify-forgot-otp", forgotResp.redirectedUrl);
        assertEquals("ctrl@example.com", sessionAttrs.get(Constant.SESSION_RESET_EMAIL));
        assertNull(sessionAttrs.get(Constant.SESSION_RESET_VERIFIED_EMAIL));
        assertNotNull(emailService.lastOtp);

        String wrongOtp = "000000".equals(emailService.lastOtp) ? "111111" : "000000";
        MockResponseCapture wrongOtpResp = new MockResponseCapture();
        HttpServletRequest wrongOtpReq = createRequestProxy("/verify-forgot-otp",
                Map.of("email", "ctrl@example.com", "otp", wrongOtp), session, wrongOtpResp);
        controller.invokePost(wrongOtpReq, wrongOtpResp.response);

        assertEquals(Constant.Path.VERIFY_FORGOT_OTP, wrongOtpResp.forwardedPath);
        assertNotNull(wrongOtpResp.requestAttributes.get("alert"));
        assertNull(sessionAttrs.get(Constant.SESSION_RESET_VERIFIED_EMAIL));

        MockResponseCapture verifyResp = new MockResponseCapture();
        HttpServletRequest verifyReq = createRequestProxy("/verify-forgot-otp",
                Map.of("email", "ctrl@example.com", "otp", emailService.lastOtp), session, verifyResp);
        controller.invokePost(verifyReq, verifyResp.response);

        assertEquals("/JPA/reset-password", verifyResp.redirectedUrl);
        assertEquals("ctrl@example.com", sessionAttrs.get(Constant.SESSION_RESET_VERIFIED_EMAIL));

        MockResponseCapture mismatchResp = new MockResponseCapture();
        HttpServletRequest mismatchReq = createRequestProxy("/reset-password",
                Map.of("newPassword", "New@123", "confirmPassword", "Diff@123"), session, mismatchResp);
        controller.invokePost(mismatchReq, mismatchResp.response);

        assertEquals(Constant.Path.RESET_PASSWORD, mismatchResp.forwardedPath);
        assertNotNull(mismatchResp.requestAttributes.get("alert"));

        MockResponseCapture resetResp = new MockResponseCapture();
        HttpServletRequest resetReq = createRequestProxy("/reset-password",
                Map.of("newPassword", "New@123", "confirmPassword", "New@123"), session, resetResp);
        controller.invokePost(resetReq, resetResp.response);

        assertEquals("/JPA/login", resetResp.redirectedUrl);
        assertNull(sessionAttrs.get(Constant.SESSION_RESET_EMAIL));
        assertNull(sessionAttrs.get(Constant.SESSION_RESET_VERIFIED_EMAIL));
        assertNotNull(sessionAttrs.get(Constant.SESSION_SUCCESS));
        assertNotNull(service.login("ctrluser", "New@123"));
    }

    private User createActiveUser(String username, String email, String rawPassword) {
        User user = new User(email, username, "Test User", PasswordUtil.hashPassword(rawPassword),
                null, 5, "0900000000", new Date(System.currentTimeMillis()));
        user.setStatus(1);
        return user;
    }

    private HttpSession createSessionProxy(Map<String, Object> attrs) {
        return (HttpSession) Proxy.newProxyInstance(
                HttpSession.class.getClassLoader(),
                new Class<?>[] { HttpSession.class },
                (proxy, method, args) -> switch (method.getName()) {
                    case "getAttribute" -> attrs.get(args[0]);
                    case "setAttribute" -> {
                        attrs.put((String) args[0], args[1]);
                        yield null;
                    }
                    case "removeAttribute" -> {
                        attrs.remove(args[0]);
                        yield null;
                    }
                    default -> null;
                });
    }

    private HttpServletRequest createRequestProxy(String servletPath, Map<String, String> params,
                                                    HttpSession session, MockResponseCapture capture) {
        return (HttpServletRequest) Proxy.newProxyInstance(
                HttpServletRequest.class.getClassLoader(),
                new Class<?>[] { HttpServletRequest.class },
                (proxy, method, args) -> switch (method.getName()) {
                    case "getServletPath" -> servletPath;
                    case "getParameter" -> params.get(args[0]);
                    case "getSession" -> session;
                    case "getContextPath" -> "/JPA";
                    case "setAttribute" -> {
                        capture.requestAttributes.put((String) args[0], args[1]);
                        yield null;
                    }
                    case "getAttribute" -> capture.requestAttributes.get(args[0]);
                    case "getRequestDispatcher" -> {
                        capture.forwardedPath = (String) args[0];
                        yield (RequestDispatcher) Proxy.newProxyInstance(
                                RequestDispatcher.class.getClassLoader(),
                                new Class<?>[] { RequestDispatcher.class },
                                (dProxy, dMethod, dArgs) -> null);
                    }
                    default -> null;
                });
    }

    private static class MockResponseCapture {
        private final Map<String, Object> requestAttributes = new HashMap<>();
        private String forwardedPath;
        private String redirectedUrl;
        private final HttpServletResponse response = (HttpServletResponse) Proxy.newProxyInstance(
                HttpServletResponse.class.getClassLoader(),
                new Class<?>[] { HttpServletResponse.class },
                (proxy, method, args) -> {
                    if ("sendRedirect".equals(method.getName())) {
                        redirectedUrl = (String) args[0];
                    }
                    return null;
                });
    }

    private static class TestableForgotPasswordController extends ForgotPasswordController {
        private static final long serialVersionUID = 1L;

        TestableForgotPasswordController(UserServiceImpl service) {
            super(service);
        }

        void invokeGet(HttpServletRequest req, HttpServletResponse resp) throws Exception {
            doGet(req, resp);
        }

        void invokePost(HttpServletRequest req, HttpServletResponse resp) throws Exception {
            doPost(req, resp);
        }
    }

    private static class RecordingEmailService implements EmailService {
        private int registerSentCount;
        private int resetSentCount;
        private String lastToEmail;
        private String lastOtp;

        @Override
        public void sendOtpEmail(String toEmail, String fullName, String otp) {
            this.registerSentCount++;
            this.lastToEmail = toEmail;
            this.lastOtp = otp;
        }

        @Override
        public void sendResetPasswordOtpEmail(String toEmail, String fullName, String otp) {
            this.resetSentCount++;
            this.lastToEmail = toEmail;
            this.lastOtp = otp;
        }
    }

    private static class InMemoryUserDao implements UserDao {
        private final Map<Integer, User> storage = new HashMap<>();
        private final AtomicInteger sequence = new AtomicInteger(1);

        @Override
        public User get(String username) {
            return storage.values().stream()
                    .filter(u -> username.equals(u.getUserName()))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public User findByEmail(String email) {
            return storage.values().stream()
                    .filter(u -> email.equals(u.getEmail()))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public void insert(User user) {
            int id = sequence.getAndIncrement();
            user.setId(id);
            storage.put(id, user);
        }

        @Override
        public void update(User user) {
            storage.put(user.getId(), user);
        }

        @Override
        public void delete(int id) {
            storage.remove(id);
        }

        @Override
        public boolean checkExistEmail(String email) {
            return findByEmail(email) != null;
        }

        @Override
        public boolean checkExistUsername(String username) {
            return get(username) != null;
        }

        @Override
        public boolean checkExistPhone(String phone) {
            return storage.values().stream().anyMatch(u -> phone.equals(u.getPhone()));
        }
    }
}
