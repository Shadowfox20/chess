package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;

import java.util.List;

public class GameService {
    public record GameListResponse(List<GameData> list) {}

    public static GameListResponse GameList(DataAccess dataAccess) throws DataAccessException {
        return new GameListResponse(dataAccess.activeGames());
    }
}
