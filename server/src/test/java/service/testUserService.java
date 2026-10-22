package service;

import dataaccess.DataAccessException;
import dataaccess.FailedDataAccess;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public class testUserService {
    @Test
    @Order(3)
    @DisplayName("Register Success")
    public void registerSuccess() {
        MemoryDataAccess dataAccess = new MemoryDataAccess();

        Assertions.assertDoesNotThrow(() ->
                Assertions.assertEquals(
                        "test", UserService.Register(dataAccess,
                                new UserService.RegisterRequest(
                                        "test", "password", "test@email.com")).username()));

        Assertions.assertDoesNotThrow(() ->
                Assertions.assertEquals(
                        "test2", UserService.Register(dataAccess,
                                new UserService.RegisterRequest(
                                        "test2", "password", "test2@email.com")).username()));
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
                        "test", UserService.Register(dataAccess,
                                new UserService.RegisterRequest(
                                        "test", "password", "test@email.com")).username()));
        Assertions.assertThrowsExactly(AlreadyExistsException.class, () ->
                UserService.Register(dataAccess,
                        new UserService.RegisterRequest(
                                "test", "password", "test@email.com")));
        Assertions.assertNotNull(dataAccess.getUser("test"));

        FailedDataAccess failedDataAccess = new FailedDataAccess();
        Assertions.assertThrowsExactly(DataAccessException.class, () ->
                UserService.Register(failedDataAccess,
                        new UserService.RegisterRequest(
                                "test", "password", "test@email.com")));
    }

    @Test
    @Order(1)
    @DisplayName("Login Success")
    public void loginSuccess() {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        dataAccess.createUser("test", "password", "");

        Assertions.assertDoesNotThrow(() ->
                Assertions.assertEquals(
                        "test", UserService.Login(dataAccess,
                                new UserService.LoginRequest(
                                        "test", "password")).username()));
    }

    @Test
    @Order(2)
    @DisplayName("Login Exceptions")
    public void loginExceptions() {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        dataAccess.createUser("test", "password", "test@email.com");

        Assertions.assertThrowsExactly(IncorrectLoginException.class, () ->
                UserService.Login(dataAccess,
                        new UserService.LoginRequest(
                                "test", "not-password")));

        FailedDataAccess failedDataAccess = new FailedDataAccess();
        Assertions.assertThrowsExactly(DataAccessException.class, () ->
                UserService.Login(failedDataAccess, new UserService.LoginRequest(
                        "test", "not-password")));
    }
}
