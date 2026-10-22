package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.GameData;

import java.util.List;

public class GameListResponse {
    List<GameData> games;
    public GameListResponse(DataAccess dataAccess) throws DataAccessException {
        games = dataAccess.activeGames();
    }

    public List<GameData> list() {
        return games;
    }
}
