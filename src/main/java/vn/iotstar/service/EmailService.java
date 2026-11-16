package vn.iotstar.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String fullName, String otp);

    default void sendResetPasswordOtpEmail(String toEmail, String fullName, String otp) {
        sendOtpEmail(toEmail, fullName, otp);
    }
}
