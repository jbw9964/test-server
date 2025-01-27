package chapters.testserver;

import chapters.testserver.repositories.*;

public record NewUserCreatedResponse(
        Long id, String username
) {
    public static NewUserCreatedResponse of(User entity)   {
        return new NewUserCreatedResponse(
                entity.getId(), entity.getUsername()
        );
    }
}
