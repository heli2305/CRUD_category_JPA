package vn.iotstar.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import vn.iotstar.service.EmailService;
import vn.iotstar.util.OtpUtil;

public class EmailServiceImpl implements EmailService {

    private final Properties config;

    public EmailServiceImpl() {
        this.config = loadConfig();
    }

    @Override
    public void sendOtpEmail(String toEmail, String fullName, String otp) {
        String host = resolveSetting("mail.smtp.host", "MAIL_HOST", "smtp.gmail.com");
        String port = resolveSetting("mail.smtp.port", "MAIL_PORT", "587");
        String auth = config.getProperty("mail.smtp.auth", "true");
        String starttls = config.getProperty("mail.smtp.starttls.enable", "true");
        String username = resolveSetting("mail.smtp.username", "MAIL_USERNAME", "");
        String password = resolveSetting("mail.smtp.password", "MAIL_PASSWORD", "");
        String from = resolveSetting("mail.from", "MAIL_FROM", username);

        if (username.isEmpty() || password.isEmpty()) {
            throw new IllegalStateException(
                    "Chưa cấu hình SMTP email (mail.smtp.username / mail.smtp.password)");
        }

        Properties mailProps = new Properties();
        mailProps.put("mail.smtp.host", host);
        mailProps.put("mail.smtp.port", port);
        mailProps.put("mail.smtp.auth", auth);
        mailProps.put("mail.smtp.starttls.enable", starttls);

        Session session = Session.getInstance(mailProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Mã OTP kích hoạt tài khoản", "UTF-8");

            String displayName = (fullName == null || fullName.isBlank()) ? "bạn" : fullName.trim();
            String body = "<p>Xin chào <strong>" + escapeHtml(displayName) + "</strong>,</p>"
                    + "<p>Mã OTP kích hoạt tài khoản của bạn là: "
                    + "<strong style=\"font-size:20px; color:#009ee0;\">" + escapeHtml(otp) + "</strong></p>"
                    + "<p>Mã có hiệu lực trong vòng <strong>" + OtpUtil.OTP_EXPIRY_MINUTES + " phút</strong>.</p>"
                    + "<p>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.</p>";
            message.setContent(body, "text/html; charset=UTF-8");

            Transport.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("Gửi email OTP thất bại", e);
        }
    }

    private Properties loadConfig() {
        Properties props = new Properties();
        try (InputStream is = EmailServiceImpl.class.getClassLoader().getResourceAsStream("mail.properties")) {
            if (is != null) {
                props.load(is);
            }
        } catch (IOException ignored) {
        }
        return props;
    }

    private String resolveSetting(String propKey, String envKey, String defaultValue) {
        String envVal = System.getenv(envKey);
        if (envVal != null && !envVal.isBlank()) {
            return envVal.trim();
        }
        String propVal = config.getProperty(propKey);
        if (propVal != null && !propVal.isBlank()) {
            return propVal.trim();
        }
        return defaultValue;
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
