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

    @Test
    @Order(3)
    @DisplayName("Create Game Success")
    public void createGameSuccess() {
        MemoryDataAccess dataAccess = new MemoryDataAccess();

        Assertions.assertDoesNotThrow(() -> GameService.createGame(dataAccess,
                new GameService.CreateGameRequest("game 1")));
        Assertions.assertDoesNotThrow(() -> GameService.createGame(dataAccess,
                new GameService.CreateGameRequest("game 2")));
        List<GameData> expected = List.of(new GameData[]{
                new GameData(1, null, null, "game 1"),
                new GameData(2, null, null, "game 2"),
        });
        Assertions.assertEquals(expected, dataAccess.activeGames());
    }

    @Test
    @Order(4)
    @DisplayName("Create Game Exceptions")
    public void createGameExceptions() {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        Assertions.assertThrowsExactly(BadRequestException.class, () ->
                GameService.createGame(dataAccess, new GameService.CreateGameRequest(null)));

        FailedDataAccess failedDataAccess = new FailedDataAccess();
        Assertions.assertThrowsExactly(DataAccessException.class, () ->
                GameService.createGame(failedDataAccess, new GameService.CreateGameRequest("game 1")));
    }

    @Test
    @Order(5)
    @DisplayName("Join Game Success")
    public void joinGameSuccess() {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        dataAccess.addGame("game 1");
        dataAccess.addGame("game 2");
        List<GameData> expected = List.of(new GameData[]{
                new GameData(1, "test-user", null, "game 1"),
                new GameData(2, null, null, "game 2"),
        });
        Assertions.assertDoesNotThrow(() ->
                GameService.joinGame(dataAccess, new GameService.JoinGameRequest(1, "WHITE", "test-user")));
        Assertions.assertEquals(expected, dataAccess.activeGames());
    }

    @Test
    @Order(4)
    @DisplayName("Join Game Exceptions")
    public void joinGameExceptions() {
        MemoryDataAccess dataAccess = new MemoryDataAccess();
        Assertions.assertThrowsExactly(BadRequestException.class, () ->
                GameService.joinGame(dataAccess, new GameService.JoinGameRequest(1, "WHITE", "test-user")));
        dataAccess.addGame("game 1");
        Assertions.assertDoesNotThrow(() ->
                GameService.joinGame(dataAccess, new GameService.JoinGameRequest(1, "WHITE", "test-user")));
        Assertions.assertThrowsExactly(AlreadyExistsException.class, () ->
                GameService.joinGame(dataAccess, new GameService.JoinGameRequest(1, "WHITE", "test-user")));

        FailedDataAccess failedDataAccess = new FailedDataAccess();
        Assertions.assertThrowsExactly(DataAccessException.class, () ->
                GameService.joinGame(failedDataAccess, new GameService.JoinGameRequest(1, "WHITE", "test-user")));
    }
}
