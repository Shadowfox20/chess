package service;

import dataaccess.DataAccessException;
import dataaccess.FailedDataAccess;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

public class testRegister {
    @Test
    @Order(1)
    @DisplayName("Register Success")
    public void registerSuccess() {
        MemoryDataAccess dataAccess = new MemoryDataAccess();

        Assertions.assertDoesNotThrow(() ->
                Assertions.assertEquals(
                        "test", new RegisterResponse(dataAccess,
                                new RegisterResponse.RegisterRequest(
                                        "test", "password", "test@email.com")).username()));

        Assertions.assertDoesNotThrow(() ->
                Assertions.assertEquals(
                        "test2", new RegisterResponse(dataAccess,
                                new RegisterResponse.RegisterRequest(
                                        "test2", "password", "test2@email.com")).username()));
        Assertions.assertNotNull(dataAccess.getUser("test"));
        Assertions.assertNotNull(dataAccess.getUser("test2"));
    }

    @Test
    @Order(2)
    @DisplayName("Register Exceptions")
    public void registerExceptions() {
        MemoryDataAccess dataAccess = new MemoryDataAccess();

        Assertions.assertDoesNotThrow(() ->
                Assertions.assertEquals(
                        "test", new RegisterResponse(dataAccess,
                                new RegisterResponse.RegisterRequest(
                                        "test", "password", "test@email.com")).username()));
        Assertions.assertThrowsExactly(AlreadyExistsException.class, () ->
                new RegisterResponse(dataAccess,
                        new RegisterResponse.RegisterRequest(
                                "test", "password", "test@email.com")));
        Assertions.assertNotNull(dataAccess.getUser("test"));

        FailedDataAccess failedDataAccess = new FailedDataAccess();
        Assertions.assertThrowsExactly(DataAccessException.class, () ->
                new GameListResponse(failedDataAccess));
    }
}
