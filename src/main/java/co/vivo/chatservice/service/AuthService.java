package co.vivo.chatservice.service;

import co.vivo.chatservice.enums.UserType;
import co.vivo.chatservice.model.UserEntity;
import co.vivo.chatservice.repository.UserRepository;
import co.vivo.chatservice.util.ChatUtil;
import co.vivo.chatservice.util.CryptoUtils;
import co.vivo.chatservice.util.PasswordHashingUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.websocket.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Service to handle user authentication and token management.
 * Author: Asad Ali
 */
@ApplicationScoped
public class AuthService {

    Logger logger = LoggerFactory.getLogger(AuthService.class);


    @Inject
    UserRepository userRepository;
    /**
     * Authenticates a user by username and password.
     */
    public UserEntity loginUser(String username, String password) {
        Optional<UserEntity> userOptional = userRepository.findUserByUsername(username);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            // Verify the hashed password
            if (PasswordHashingUtil.verifyPassword(password, user.getPassword())) {
                return user;
            }
        }
        return null;
    }
    /**
     * Handles guest login based on device ID.
     */
    public UserEntity guestLogin(String deviceId) {
        return userRepository.findGuestByDeviceId(deviceId)
                .orElseGet(() -> {
                    String uuid = UUID.randomUUID().toString();
                    UserEntity guest = new UserEntity();
                    guest.setDeviceId(deviceId);
                    guest.setMobile(uuid);
                    guest.setEmail(uuid);
                    guest.setUsername(uuid);
                    guest.setUserId(uuid);
                    guest.setCreatedAt(LocalDateTime.now());
                    guest.setUserType(UserType.GUEST);
                    guest = userRepository.saveUser(guest);
                    return guest;
                });
    }

    /**
     * Generates a token using encryption for a user.
     */
    public String generateToken(UserEntity user) {
        try {
            // Create a token with userId and timestamp for uniqueness
            String tokenData = user.getUserId() + ":" + System.currentTimeMillis();
            // Encrypt the token data
            String encryptedToken = CryptoUtils.encrypt(tokenData);
            // Save the token in the user's entity
            user.setToken(encryptedToken);
            // Update the user in the database (if needed)
            logger.info("User {}", user);
            userRepository.updateUser(user);
            return encryptedToken;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Handle error appropriately
        }
    }

    /**
     * Verifies a token by decrypting it and retrieving the user.
     */
    public UserEntity verifyToken(String token) {
        try {
            // Decrypt the token
            String decryptedToken = CryptoUtils.decrypt(token);
            logger.info("decryptedToken :{} ", decryptedToken);
            // Token format: userId:timestamp
            String[] tokenParts = decryptedToken.split(":");
            String userId = tokenParts[0];
            logger.info("userId :{} ", userId);
            // Retrieve the user by userId
            Optional<UserEntity> userEntity = userRepository.findByUserId(userId);
            return userEntity.isPresent()? userEntity.get():null;

        } catch (Exception e) {
            e.printStackTrace();
            return null; // Token verification failed
        }
    }

    /**
     * Authenticates the user based on token or guest login.
     */
    public UserEntity authenticateUser(Session session, String userId) {
        String token = ChatUtil.extractToken(session.getQueryString());
        if (token != null && !token.isEmpty()) {
            return verifyToken(token);
        } else {
            return guestLogin(userId);
        }
    }
}

