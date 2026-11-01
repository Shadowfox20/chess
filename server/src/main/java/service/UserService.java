package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserLogin;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class UserService {
    public record RegisterRequest(String username, String password, String email) {}

    public record RegisterResponse(String username, String auth) {}

    public static RegisterResponse register(DataAccess data, RegisterRequest request)
            throws AlreadyExistsException, DataAccessException, BadRequestException {
        if (request.username() == null || request.password() == null || request.email() == null ||
                request.username().isEmpty() || request.password().isEmpty() || request.email().isEmpty()) {
            throw new BadRequestException("all fields are required");
        }
        if (data.userExists(request.username())) {
            throw new AlreadyExistsException("username already exists");
        }
        UserData resp = data.createUser(request.username(), request.password(), request.email());
        String auth = UUID.randomUUID().toString();
        data.addAuth(auth, request.username());
        return new RegisterResponse(resp.username(), auth);
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
        String auth = UUID.randomUUID().toString();
        data.addAuth(auth, request.username());
        return new LoginResponse(userLogin.username(), auth);
    }

    public static boolean invalidAuth(DataAccess dataAccess, String auth) throws DataAccessException {
        return !dataAccess.validAuth(auth);
    }

    public static String userFromAuth(DataAccess dataAccess, String auth) throws  DataAccessException {
        return dataAccess.userFromAuth(auth);
    }

    public static void removeAuth(DataAccess dataAccess, String auth) throws  DataAccessException {
        dataAccess.removeAuth(auth);
    }
}
