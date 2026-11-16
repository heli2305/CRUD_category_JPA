package vn.iotstar.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

import vn.iotstar.dao.UserDao;
import vn.iotstar.entity.User;
import vn.iotstar.service.EmailService;
import vn.iotstar.service.OtpStatus;
import vn.iotstar.service.impl.UserServiceImpl;
import vn.iotstar.util.OtpUtil;

class UserOtpTest {

    @Test
    void testGenerateOtpFormat() {
        for (int i = 0; i < 20; i++) {
            String otp = OtpUtil.generateOtp();
            assertNotNull(otp);
            assertTrue(otp.matches("\\d{6}"));
        }
    }

    @Test
    void testRegisterVerifyAndResendOtpFlow() {
        InMemoryUserDao userDao = new InMemoryUserDao();
        RecordingEmailService emailService = new RecordingEmailService();
        UserServiceImpl service = new UserServiceImpl(userDao, emailService);

        boolean registered = service.register("otpuser", "secret", "otpuser@example.com", "Otp User", "0901234567");
        assertTrue(registered);
        assertEquals(1, emailService.sentCount);
        assertEquals("otpuser@example.com", emailService.lastToEmail);
        assertNotNull(emailService.lastOtp);

        User saved = userDao.findByEmail("otpuser@example.com");
        assertNotNull(saved);
        assertEquals(0, saved.getStatus());
        assertEquals(emailService.lastOtp, saved.getOtp());
        assertNotNull(saved.getOtpExpiry());
        assertTrue(saved.getOtpExpiry().isAfter(LocalDateTime.now()));

        assertEquals(OtpStatus.INVALID_OTP, service.verifyOtp("otpuser@example.com", "000000".equals(saved.getOtp()) ? "111111" : "000000"));
        assertEquals(0, userDao.findByEmail("otpuser@example.com").getStatus());

        saved.setOtpExpiry(LocalDateTime.now().minusMinutes(1));
        assertEquals(OtpStatus.EXPIRED_OTP, service.verifyOtp("otpuser@example.com", emailService.lastOtp));

        String oldOtp = saved.getOtp();
        assertEquals(OtpStatus.SUCCESS, service.resendOtp("otpuser@example.com"));
        assertEquals(2, emailService.sentCount);
        assertNotNull(saved.getOtp());
        assertTrue(saved.getOtpExpiry().isAfter(LocalDateTime.now()));
        if (!oldOtp.equals(emailService.lastOtp)) {
            assertNotEquals(oldOtp, saved.getOtp());
        }

        assertEquals(OtpStatus.SUCCESS, service.verifyOtp("otpuser@example.com", emailService.lastOtp));
        assertEquals(1, saved.getStatus());
        assertNull(saved.getOtp());
        assertNull(saved.getOtpExpiry());

        assertEquals(OtpStatus.ALREADY_ACTIVE, service.verifyOtp("otpuser@example.com", "123456"));
        assertEquals(OtpStatus.ALREADY_ACTIVE, service.resendOtp("otpuser@example.com"));
    }

    @Test
    void testRegisterRollsBackWhenEmailFails() {
        InMemoryUserDao userDao = new InMemoryUserDao();
        EmailService failingEmailService = (toEmail, fullName, otp) -> {
            throw new IllegalStateException("SMTP unavailable");
        };
        UserServiceImpl service = new UserServiceImpl(userDao, failingEmailService);

        boolean registered = service.register("failuser", "secret", "fail@example.com", "Fail User", null);
        assertFalse(registered);
        assertNull(userDao.findByEmail("fail@example.com"));
        assertNull(userDao.get("failuser"));
    }

    private static class RecordingEmailService implements EmailService {
        private int sentCount;
        private String lastToEmail;
        private String lastOtp;

        @Override
        public void sendOtpEmail(String toEmail, String fullName, String otp) {
            this.sentCount++;
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
