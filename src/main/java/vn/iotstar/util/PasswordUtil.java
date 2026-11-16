package vn.iotstar.util;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {

    private static final int BCRYPT_COST = 10;

    private PasswordUtil() {
    }

    public static String hashPassword(String rawPassword) {
        if (rawPassword == null || rawPassword.isEmpty()) {
            throw new IllegalArgumentException("Password must not be empty");
        }
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(BCRYPT_COST));
    }

    public static boolean verifyPassword(String rawPassword, String hashedPassword) {
        if (rawPassword == null || rawPassword.isEmpty() || !isHashed(hashedPassword)) {
            return false;
        }
        try {
            return BCrypt.checkpw(rawPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static boolean isHashed(String password) {
        if (password == null) {
            return false;
        }
        return password.startsWith("$2a$")
                || password.startsWith("$2b$")
                || password.startsWith("$2y$");
    }
}
