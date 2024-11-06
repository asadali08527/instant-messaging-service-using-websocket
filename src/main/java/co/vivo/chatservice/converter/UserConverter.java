package co.vivo.chatservice.converter;

import co.vivo.chatservice.model.UserEntity;
import co.vivo.chatservice.wrapper.User;

public class UserConverter {
    public static User convert(Object object) {
        if(object instanceof UserEntity){
            UserEntity userEntity = (UserEntity)object;
            return new User(userEntity.getFirstName(),userEntity.getMiddleName(), userEntity.getLastName(),
                    userEntity.getId(),
                    userEntity.getUserId(),
                    userEntity.getUsername(),
                    userEntity.getEmail(),
                    userEntity.getMobile(),
                    userEntity.getDeviceId(),
                    null, // Token will be set during login
                    userEntity.getUserType(),
                    userEntity.getCreatedAt()
            );
        }
            return null;

    }
}
