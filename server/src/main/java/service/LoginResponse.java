package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserLogin;

import java.util.Objects;

public class LoginResponse {
    private final UserLogin userLogin;
    public record LoginRequest(String username, String password) {}

    public LoginResponse(DataAccess data, LoginRequest request) throws IncorrectLoginException, DataAccessException {
        userLogin = data.getUserLogin(request.username());
        if (userLogin == null) {
            throw new IncorrectLoginException("username not found");
        }
        if (!Objects.equals(userLogin.password(), request.password())) {
            throw new IncorrectLoginException("incorrect password");
        }
    }

    public String username() {
        return userLogin.username();
    }
}
