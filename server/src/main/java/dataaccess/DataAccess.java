package dataaccess;

import model.GameData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.List;

public interface DataAccess {
    UserData createUser(String username, String password, String email) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    UserLogin getUserLogin(String username) throws DataAccessException;
    List<GameData> activeGames() throws DataAccessException;
    GameData addGame(String name) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void joinGame(int gameID, boolean whiteTeam, String username) throws DataAccessException;
}
