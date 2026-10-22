package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserLogin;

import java.util.Objects;

public class UserService {
    public record RegisterRequest(String username, String password, String email) {}

    public record RegisterResponse(String username) {}

    public static RegisterResponse Register(DataAccess data, RegisterRequest request) throws AlreadyExistsException, DataAccessException {
        if (data.getUser(request.username()) != null) {
            throw new AlreadyExistsException("username already exists");
        }
        return new RegisterResponse(data.createUser(request.username(), request.password(), request.email()).username());
    }

    public record LoginRequest(String username, String password) {}

    public record LoginResponse(String username) {}

    public static LoginResponse Login(DataAccess data, LoginRequest request) throws IncorrectLoginException, DataAccessException {
        UserLogin userLogin = data.getUserLogin(request.username());
        if (userLogin == null) {
            throw new IncorrectLoginException("username not found");
        }
        if (!Objects.equals(userLogin.password(), request.password())) {
            throw new IncorrectLoginException("incorrect password");
        }
        return new LoginResponse(userLogin.username());
    }

}
