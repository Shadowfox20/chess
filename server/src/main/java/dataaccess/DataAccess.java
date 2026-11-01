package dataaccess;

import model.GameData;
import model.UserData;

import java.util.List;

public interface DataAccess {
    UserData createUser(String username, String password, String email) throws DataAccessException;
    boolean userExists(String username) throws DataAccessException;
    UserLogin getUserLogin(String username) throws DataAccessException;
    List<GameData> activeGames() throws DataAccessException;
    GameData addGame(String name) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void joinGame(int gameID, boolean whiteTeam, String username) throws DataAccessException;
    void clear() throws DataAccessException;
    void addAuth(String auth, String username) throws DataAccessException;
    boolean validAuth(String auth) throws DataAccessException;
    String userFromAuth(String auth) throws DataAccessException;
    void removeAuth(String auth) throws DataAccessException;
}
