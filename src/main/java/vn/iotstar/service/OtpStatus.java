package vn.iotstar.service;

public enum OtpStatus {
    SUCCESS,
    USER_NOT_FOUND,
    ALREADY_ACTIVE,
    INACTIVE_ACCOUNT,
    INVALID_OTP,
    EXPIRED_OTP,
    SEND_FAILED
}
