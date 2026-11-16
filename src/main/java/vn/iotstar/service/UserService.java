package vn.iotstar.service;

import vn.iotstar.entity.User;

public interface UserService {
    User login(String username, String password);
    User get(String username);
    User findByEmail(String email);
    void insert(User user);
    boolean register(String username, String password, String email, String fullname, String phone);
    OtpStatus verifyOtp(String email, String otp);
    OtpStatus resendOtp(String email);
    OtpStatus sendForgotPasswordOtp(String email);
    OtpStatus verifyForgotPasswordOtp(String email, String otp);
    OtpStatus resetPassword(String email, String newPassword);
    boolean checkExistEmail(String email);
    boolean checkExistUsername(String username);
    boolean checkExistPhone(String phone);
}
