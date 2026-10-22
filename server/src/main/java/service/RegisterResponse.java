package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserData;

public class RegisterResponse {
    private final UserData created;
    public record RegisterRequest(String username, String password, String email) {}

    public RegisterResponse(DataAccess data,  RegisterRequest request) throws AlreadyExistsException, DataAccessException {
        if (data.getUser(request.username()) != null) {
            throw new AlreadyExistsException("username already exists");
        }
        created = data.createUser(request.username(), request.password(), request.email());
    }

    public String username() {
        return created.username();
    }
}
