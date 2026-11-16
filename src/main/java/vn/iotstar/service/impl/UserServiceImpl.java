package vn.iotstar.service.impl;

import java.time.LocalDateTime;

import vn.iotstar.dao.UserDao;
import vn.iotstar.dao.impl.UserDaoImpl;
import vn.iotstar.entity.User;
import vn.iotstar.service.EmailService;
import vn.iotstar.service.OtpStatus;
import vn.iotstar.service.UserService;
import vn.iotstar.util.OtpUtil;
import vn.iotstar.util.PasswordUtil;

public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final EmailService emailService;

    public UserServiceImpl() {
        this(new UserDaoImpl(), new EmailServiceImpl());
    }

    public UserServiceImpl(UserDao userDao, EmailService emailService) {
        this.userDao = userDao;
        this.emailService = emailService;
    }

    @Override
    public User login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isEmpty()) {
            return null;
        }
        User user = this.get(username.trim());
        if (user == null || user.getPassWord() == null) {
            return null;
        }
        if (PasswordUtil.isHashed(user.getPassWord())) {
            return PasswordUtil.verifyPassword(password, user.getPassWord()) ? user : null;
        }
        if (password.equals(user.getPassWord())) {
            user.setPassWord(PasswordUtil.hashPassword(password));
            userDao.update(user);
            return user;
        }
        return null;
    }

    @Override
    public User get(String username) {
        return userDao.get(username);
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public void insert(User user) {
        if (user != null && user.getPassWord() != null && !user.getPassWord().isEmpty()
                && !PasswordUtil.isHashed(user.getPassWord())) {
            user.setPassWord(PasswordUtil.hashPassword(user.getPassWord()));
        }
        userDao.insert(user);
    }

    @Override
    public boolean register(String username, String password, String email, String fullname, String phone) {
        if (password == null || password.isEmpty()
                || userDao.checkExistUsername(username)
                || userDao.checkExistEmail(email)) {
            return false;
        }
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        String hashedPassword = PasswordUtil.hashPassword(password);
        User user = new User(email, username, fullname, hashedPassword, null, 5, phone, date);
        String otp = OtpUtil.generateOtp();
        user.setStatus(0);
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(OtpUtil.OTP_EXPIRY_MINUTES));

        try {
            userDao.insert(user);
        } catch (RuntimeException e) {
            return false;
        }

        try {
            emailService.sendOtpEmail(email, fullname, otp);
            return true;
        } catch (RuntimeException e) {
            if (user.getId() > 0) {
                try {
                    userDao.delete(user.getId());
                } catch (RuntimeException ignored) {
                }
            }
            return false;
        }
    }

    @Override
    public OtpStatus verifyOtp(String email, String otp) {
        if (email == null || email.isBlank()) {
            return OtpStatus.USER_NOT_FOUND;
        }
        User user = userDao.findByEmail(email.trim());
        if (user == null) {
            return OtpStatus.USER_NOT_FOUND;
        }
        if (user.getStatus() == 1) {
            return OtpStatus.ALREADY_ACTIVE;
        }
        OtpStatus otpValidation = validateOtp(user, otp);
        if (otpValidation != null) {
            return otpValidation;
        }

        user.setStatus(1);
        user.setOtp(null);
        user.setOtpExpiry(null);
        userDao.update(user);
        return OtpStatus.SUCCESS;
    }

    @Override
    public OtpStatus resendOtp(String email) {
        if (email == null || email.isBlank()) {
            return OtpStatus.USER_NOT_FOUND;
        }
        User user = userDao.findByEmail(email.trim());
        if (user == null) {
            return OtpStatus.USER_NOT_FOUND;
        }
        if (user.getStatus() == 1) {
            return OtpStatus.ALREADY_ACTIVE;
        }

        String newOtp = OtpUtil.generateOtp();
        user.setOtp(newOtp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(OtpUtil.OTP_EXPIRY_MINUTES));
        userDao.update(user);

        try {
            emailService.sendOtpEmail(user.getEmail(), user.getFullName(), newOtp);
            return OtpStatus.SUCCESS;
        } catch (RuntimeException e) {
            return OtpStatus.SEND_FAILED;
        }
    }

    @Override
    public OtpStatus sendForgotPasswordOtp(String email) {
        if (email == null || email.isBlank()) {
            return OtpStatus.USER_NOT_FOUND;
        }
        User user = userDao.findByEmail(email.trim());
        if (user == null) {
            return OtpStatus.USER_NOT_FOUND;
        }
        if (user.getStatus() != 1) {
            return OtpStatus.INACTIVE_ACCOUNT;
        }

        String previousOtp = user.getOtp();
        LocalDateTime previousExpiry = user.getOtpExpiry();
        String newOtp = OtpUtil.generateOtp();
        user.setOtp(newOtp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(OtpUtil.OTP_EXPIRY_MINUTES));
        userDao.update(user);

        try {
            emailService.sendResetPasswordOtpEmail(user.getEmail(), user.getFullName(), newOtp);
            return OtpStatus.SUCCESS;
        } catch (RuntimeException e) {
            user.setOtp(previousOtp);
            user.setOtpExpiry(previousExpiry);
            try {
                userDao.update(user);
            } catch (RuntimeException ignored) {
            }
            return OtpStatus.SEND_FAILED;
        }
    }

    @Override
    public OtpStatus verifyForgotPasswordOtp(String email, String otp) {
        if (email == null || email.isBlank()) {
            return OtpStatus.USER_NOT_FOUND;
        }
        User user = userDao.findByEmail(email.trim());
        if (user == null) {
            return OtpStatus.USER_NOT_FOUND;
        }
        if (user.getStatus() != 1) {
            return OtpStatus.INACTIVE_ACCOUNT;
        }
        OtpStatus otpValidation = validateOtp(user, otp);
        if (otpValidation != null) {
            return otpValidation;
        }

        user.setOtp(null);
        user.setOtpExpiry(null);
        userDao.update(user);
        return OtpStatus.SUCCESS;
    }

    @Override
    public OtpStatus resetPassword(String email, String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu mới không được để trống");
        }
        if (email == null || email.isBlank()) {
            return OtpStatus.USER_NOT_FOUND;
        }
        User user = userDao.findByEmail(email.trim());
        if (user == null) {
            return OtpStatus.USER_NOT_FOUND;
        }
        if (user.getStatus() != 1) {
            return OtpStatus.INACTIVE_ACCOUNT;
        }

        user.setPassWord(PasswordUtil.hashPassword(newPassword));
        user.setOtp(null);
        user.setOtpExpiry(null);
        userDao.update(user);
        return OtpStatus.SUCCESS;
    }

    private OtpStatus validateOtp(User user, String otp) {
        if (otp == null || otp.isBlank() || user.getOtp() == null || !user.getOtp().equals(otp.trim())) {
            return OtpStatus.INVALID_OTP;
        }
        if (user.getOtpExpiry() == null || LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            return OtpStatus.EXPIRED_OTP;
        }
        return null;
    }

    @Override
    public boolean checkExistEmail(String email) {
        return userDao.checkExistEmail(email);
    }

    @Override
    public boolean checkExistUsername(String username) {
        return userDao.checkExistUsername(username);
    }

    @Override
    public boolean checkExistPhone(String phone) {
        return userDao.checkExistPhone(phone);
    }
}
