package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserLogin;

import java.util.Objects;

public class UserService {
    public record RegisterRequest(String username, String password, String email) {}

    public record RegisterResponse(String username) {}

    public static RegisterResponse register(DataAccess data, RegisterRequest request)
            throws AlreadyExistsException, DataAccessException, BadRequestException {
        if (request.username() == null || request.password() == null || request.email() == null ||
                request.username().isEmpty() || request.password().isEmpty() || request.email().isEmpty()) {
            throw new BadRequestException("all fields are required");
        }
        if (data.getUser(request.username()) != null) {
            throw new AlreadyExistsException("username already exists");
        }
        return new RegisterResponse(data.createUser(request.username(), request.password(), request.email()).username());
    }

    public record LoginRequest(String username, String password) {}

    public record LoginResponse(String username) {}

    public static LoginResponse login(DataAccess data, LoginRequest request)
            throws IncorrectLoginException, DataAccessException, BadRequestException {
        if (request.username() == null || request.username().isEmpty() ||
                request.password() == null || request.password().isEmpty()) {
            throw new BadRequestException("username and password required");
        }
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
