package dataaccess;

import java.util.List;

public interface DataAccess {
    UserData createUser(String username, String password, String email) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    UserLogin getUserLogin(String username) throws DataAccessException;
    List<GameData> activeGames() throws DataAccessException;
}
