package vn.iotstar.service;

public interface EmailService {
    void sendOtpEmail(String toEmail, String fullName, String otp);
}
