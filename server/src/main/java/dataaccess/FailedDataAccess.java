package dataaccess;

import model.GameData;
import model.UserData;

import java.util.List;

public class FailedDataAccess implements DataAccess{
    @Override
    public UserData createUser(String username, String password, String email) throws DataAccessException {
        throw new DataAccessException("test-createUser");
    }

    @Override
    public boolean userExists(String username) throws DataAccessException {
        throw new DataAccessException("test-getUser");
    }

    @Override
    public UserLogin getUserLogin(String username) throws DataAccessException {
        throw new DataAccessException("test-getUserLogin");
    }

    @Override
    public List<GameData> activeGames() throws DataAccessException {
        throw new DataAccessException("test-activeGames");
    }

    @Override
    public GameData addGame(String name) throws DataAccessException {
        throw new DataAccessException("test-addGame");
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        throw new DataAccessException("test-getGame");
    }

    @Override
    public void joinGame(int gameID, boolean whiteTeam, String username) throws DataAccessException {
        throw new DataAccessException("test-joinGame");
    }

    @Override
    public void clear() throws DataAccessException {
        throw new DataAccessException("test-clear");
    }

    @Override
    public boolean validAuth(String auth) throws DataAccessException {
        throw new DataAccessException("test-validAuth");
    }

    @Override
    public String userFromAuth(String auth) throws DataAccessException {
        throw new DataAccessException("test-userFromAuth");
    }

    @Override
    public void removeAuth(String auth) throws DataAccessException {
        throw new DataAccessException("test-removeAuth");
    }

    @Override
    public void addAuth(String auth, String username) throws DataAccessException {
        throw new DataAccessException("test-addAuth");
    }
}
