package chapters.testserver;

import chapters.testserver.repositories.*;

public record AddNewUserRequest(
        String username
) {

    public User toEntity()  {
        return new User(this.username);
    }
}
