package co.vivo.chatservice.service;

import co.vivo.chatservice.enums.UserType;
import co.vivo.chatservice.dto.UserDto;
import co.vivo.chatservice.model.UserEntity;
import co.vivo.chatservice.repository.UserRepository;
import co.vivo.chatservice.util.PasswordHashingUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserService {

    @Inject
    UserRepository userRepository;


    public UserEntity registerUser(UserDto userRequestDto) {
        UserEntity userEntity = new UserEntity();

        // Generate userId using UUID
        userEntity.setUserId(UUID.randomUUID().toString());
        userEntity.setFirstName(userRequestDto.getFirstName());
        userEntity.setMiddleName(userRequestDto.getMiddleName());
        userEntity.setLastName(userRequestDto.getLastName());
        String username = userRequestDto.getUsername();

        // Set email if provided and use it as the username if none is provided
        if (isNotEmpty(userRequestDto.getEmail())) {
            userEntity.setEmail(userRequestDto.getEmail());
            if (!isNotEmpty(username)) {
                username = userRequestDto.getEmail();
            }
        }

        // Set mobile if provided and use it as the username if none is provided
        if (isNotEmpty(userRequestDto.getMobile())) {
            userEntity.setMobile(userRequestDto.getMobile());
            if (!isNotEmpty(username)) {
                username = userRequestDto.getMobile();
            }
        }

        // Set the username if it has been determined
        if (isNotEmpty(username)) {
            userEntity.setUsername(username);
        }

        // Hash the password before saving
        userEntity.setPassword(PasswordHashingUtil.hashPassword(userRequestDto.getPassword()));

        // Set user type as REGISTERED
        userEntity.setUserType(UserType.REGISTERED);

        // Set device ID and creation date
        userEntity.setDeviceId(userRequestDto.getDeviceId());
        userEntity.setCreatedAt(LocalDateTime.now());

        // Save the user entity to the database
        userRepository.saveUser(userEntity);

        return userEntity;
    }

    private boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    public UserEntity getUserByUserId(String userId) {
        Optional<UserEntity> userEntity = userRepository.findByUserId(userId);
        return userEntity.isPresent()? userEntity.get():null;
    }
}