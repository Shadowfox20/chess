package dataaccess;

import model.GameData;
import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class DBDataAccess implements DataAccess{
    public DBDataAccess() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

    @Override
    public UserData createUser(String username, String password, String email) throws DataAccessException {
        return null;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public UserLogin getUserLogin(String username) throws DataAccessException {
        return null;
    }

    @Override
    public GameData addGame(String name) throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public List<GameData> activeGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void joinGame(int gameID, boolean whiteTeam, String username) throws DataAccessException {
    }

    public void clear() throws DataAccessException {}
}
