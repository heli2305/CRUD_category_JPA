package vn.iotstar.service.impl;

import java.time.LocalDateTime;

import vn.iotstar.dao.UserDao;
import vn.iotstar.dao.impl.UserDaoImpl;
import vn.iotstar.entity.User;
import vn.iotstar.service.EmailService;
import vn.iotstar.service.OtpStatus;
import vn.iotstar.service.UserService;
import vn.iotstar.util.OtpUtil;

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
        if (username == null || password == null) {
            return null;
        }
        User user = this.get(username);
        if (user != null && password.equals(user.getPassWord())) {
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
        userDao.insert(user);
    }

    @Override
    public boolean register(String username, String password, String email, String fullname, String phone) {
        if (userDao.checkExistUsername(username) || userDao.checkExistEmail(email)) {
            return false;
        }
        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        User user = new User(email, username, fullname, password, null, 5, phone, date);
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
        if (otp == null || otp.isBlank() || user.getOtp() == null || !user.getOtp().equals(otp.trim())) {
            return OtpStatus.INVALID_OTP;
        }
        if (user.getOtpExpiry() == null || LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            return OtpStatus.EXPIRED_OTP;
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
