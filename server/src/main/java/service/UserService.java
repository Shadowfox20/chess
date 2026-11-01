package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserLogin;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

public class UserService {
    public record RegisterRequest(String username, String password, String email) {}

    public record RegisterResponse(String username, String auth) {}

    public static RegisterResponse register(DataAccess data, RegisterRequest request)
            throws AlreadyExistsException, DataAccessException, BadRequestException {
        if (request.username() == null || request.password() == null || request.email() == null ||
                request.username().isEmpty() || request.password().isEmpty() || request.email().isEmpty()) {
            throw new BadRequestException("all fields are required");
        }
        if (data.getUser(request.username()) != null) {
            throw new AlreadyExistsException("username already exists");
        }
        UserData resp = data.createUser(request.username(), request.password(), request.email());
        return new RegisterResponse(resp.username(), resp.auth());
    }

    public record LoginRequest(String username, String password) {}

    public record LoginResponse(String username, String auth) {}

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
        if (!BCrypt.checkpw(request.password(), userLogin.hashedPassword())) {
            throw new IncorrectLoginException("incorrect password");
        }
        return new LoginResponse(userLogin.username(), userLogin.auth());
    }

    public static boolean InvalidAuth(DataAccess dataAccess, String auth) throws DataAccessException {
        return !dataAccess.validAuth(auth);
    }

    public static String UserFromAuth(DataAccess dataAccess, String auth) throws  DataAccessException {
        return dataAccess.userFromAuth(auth);
    }

    public static void RemoveAuth(DataAccess dataAccess, String auth) throws  DataAccessException {
        dataAccess.removeAuth(auth);
    }
}
