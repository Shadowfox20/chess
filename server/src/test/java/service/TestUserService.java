package service;

import dataaccess.DataAccessException;
import dataaccess.FailedDataAccess;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public class TestUserService {
    @Test
    @Order(3)
    @DisplayName("Register Success")
    public void registerSuccess() {
        MemoryDataAccess dataAccess = new MemoryDataAccess();

        Assertions.assertDoesNotThrow(() ->
                Assertions.assertEquals(
                        "test", UserService.register(dataAccess,
                                new UserService.RegisterRequest(
                                        "test", "hashedPassword", "test@email.com")).username()));

        Assertions.assertDoesNotThrow(() ->
                Assertions.assertEquals(
                        "test2", UserService.register(dataAccess,
                                new UserService.RegisterRequest(
                                        "test2", "hashedPassword", "test2@email.com")).username()));
        Assertions.assertNotNull(dataAccess.getUser("test"));
        Assertions.assertNotNull(dataAccess.getUser("test2"));
    }

    @Test
    @Order(4)
    @DisplayName("Register Exceptions")
    public void registerExceptions() {
        MemoryDataAccess dataAccess = new MemoryDataAccess();

        Assertions.assertDoesNotThrow(() ->
                Assertions.assertEquals(
                        "test", UserService.register(dataAccess,
                                new UserService.RegisterRequest(
                                        "test", "hashedPassword", "test@email.com")).username()));
        Assertions.assertThrowsExactly(AlreadyExistsException.class, () ->
                UserService.register(dataAccess,
                        new UserService.RegisterRequest(
                                "test", "hashedPassword", "test@email.com")));
        Assertions.assertNotNull(dataAccess.getUser("test"));

        FailedDataAccess failedDataAccess = new FailedDataAccess();
        Assertions.assertThrowsExactly(DataAccessException.class, () ->
                UserService.register(failedDataAccess,
                        new UserService.RegisterRequest(
                                "test", "hashedPassword", "test@email.com")));
    }

    @Test
    @Order(1)
    @DisplayName("Login Success")
    public void loginSuccess() {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        dataAccess.createUser("test", "hashedPassword", "");

        Assertions.assertDoesNotThrow(() ->
                Assertions.assertEquals(
                        "test", UserService.login(dataAccess,
                                new UserService.LoginRequest(
                                        "test", "hashedPassword")).username()));
    }

    @Test
    @Order(2)
    @DisplayName("Login Exceptions")
    public void loginExceptions() {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        dataAccess.createUser("test", "hashedPassword", "test@email.com");

        Assertions.assertThrowsExactly(IncorrectLoginException.class, () ->
                UserService.login(dataAccess,
                        new UserService.LoginRequest(
                                "test", "not-hashedPassword")));

        FailedDataAccess failedDataAccess = new FailedDataAccess();
        Assertions.assertThrowsExactly(DataAccessException.class, () ->
                UserService.login(failedDataAccess, new UserService.LoginRequest(
                        "test", "not-hashedPassword")));
    }
}
