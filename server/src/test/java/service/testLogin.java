package service;

import dataaccess.DataAccessException;
import dataaccess.FailedDataAccess;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public class testLogin {
    @Test
    @Order(1)
    @DisplayName("Login Success")
    public void loginSuccess() {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        dataAccess.createUser("test", "password", "");

        Assertions.assertDoesNotThrow(() ->
                Assertions.assertEquals(
                        "test", new LoginResponse(dataAccess,
                                new LoginResponse.LoginRequest(
                                        "test", "password")).username()));
    }

    @Test
    @Order(2)
    @DisplayName("Login Exceptions")
    public void loginExceptions() {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        dataAccess.createUser("test", "password", "test@email.com");

        Assertions.assertThrowsExactly(IncorrectLoginException.class, () ->
                new LoginResponse(dataAccess,
                        new LoginResponse.LoginRequest(
                                "test", "not-password")));

        FailedDataAccess failedDataAccess = new FailedDataAccess();
        Assertions.assertThrowsExactly(DataAccessException.class, () ->
                new GameListResponse(failedDataAccess));
    }
}
