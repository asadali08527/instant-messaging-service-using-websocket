package co.vivo.chatservice.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHashingUtil {

    // Hash the password using BCrypt
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Verify the password against the stored hash
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
