package dataaccess;

import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemoryDataAccess implements DataAccess {
    private record user(String username, String password, String email) {}

    private final HashMap<String, user> userTable = new HashMap<>();
    private final List<GameData> activeGameList = new ArrayList<>(0);

    @Override
    public UserData createUser(String username, String password, String email) {
        userTable.put(username, new user(username, password, email));
        return getUser(username);
    }

    @Override
    public UserData getUser(String username) {
        user res = userTable.get(username);
        if (res == null) {
            return null;
        }
        return new UserData(res.username());
    }

    @Override
    public UserLogin getUserLogin(String username) {
        user res = userTable.get(username);
        if (res == null) {
            return null;
        }
        return new UserLogin(res.username(), res.password());
    }

    @Override
    public List<GameData> activeGames() {
        return activeGameList;
    }

    @Override
    public GameData addGame(String name) {
        GameData newGame = new GameData(activeGameList.size() + 1, null, null, name);
        activeGameList.add(newGame);
        return newGame;
    }

    @Override
    public GameData getGame(int gameID) {
        if (gameID > activeGameList.size()) {
            return null;
        }
        return activeGameList.get(gameID - 1);
    }

    @Override
    public void joinGame(int gameID, boolean whiteTeam, String username) throws DataAccessException {
        GameData gameData = getGame(gameID);
        String whiteUser = whiteTeam ? username : gameData.whiteUsername();
        String blackUser = whiteTeam ? gameData.blackUsername() : username;
        activeGameList.set(gameID - 1, new GameData(gameID, whiteUser, blackUser, gameData.gameName()));
    }}
