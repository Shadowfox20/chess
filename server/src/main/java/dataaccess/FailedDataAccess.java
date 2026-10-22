package dataaccess;

import java.util.List;

public class FailedDataAccess implements DataAccess{
    @Override
    public UserData createUser(String username, String password, String email) throws DataAccessException {
        throw new DataAccessException("test-createUser");
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
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
}
