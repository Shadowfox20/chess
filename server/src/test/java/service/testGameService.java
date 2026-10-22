package service;

import dataaccess.DataAccessException;
import dataaccess.FailedDataAccess;
import model.GameData;
import dataaccess.MemoryDataAccess;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

public class testGameService {
    @Test
    @Order(1)
    @DisplayName("Game List Success")
    public void gameListSuccess() {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        dataAccess.addGame("game 1");
        dataAccess.addGame("game 2");
        List<GameData> expected = List.of(new GameData[]{
                new GameData(1, null, null, "game 1"),
                new GameData(2, null, null, "game 2"),
        });
        Assertions.assertDoesNotThrow(() ->
                Assertions.assertEquals(expected,
                        GameService.gameList(dataAccess).games()));
    }

    @Test
    @Order(2)
    @DisplayName("Game List Exceptions")
    public void gameListExceptions() {
        FailedDataAccess dataAccess = new FailedDataAccess();
        Assertions.assertThrowsExactly(DataAccessException.class, () ->
                GameService.gameList(dataAccess));
    }
}
