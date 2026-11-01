package dataaccess;

import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.*;

public class MemoryDataAccess implements DataAccess {
    private record User(String username, String password, String email) {}
    private HashMap<String, User> userTable = new HashMap<>();
    private List<GameData> activeGameList = new ArrayList<>(0);
    private HashMap<String, String> validTokens = new HashMap<>();

    @Override
    public UserData createUser(String username, String password, String email) {
        userTable.put(username, new User(username, BCrypt.hashpw(password, BCrypt.gensalt()), email));
        String auth = UUID.randomUUID().toString();
        validTokens.put(auth, username);
        return getUser(username);
    }

    @Override
    public UserData getUser(String username) {
        User res = userTable.get(username);
        if (res == null) {
            return null;
        }
        return new UserData(res.username(), validTokens.get(username));
    }

    @Override
    public UserLogin getUserLogin(String username) {
        User res = userTable.get(username);
        if (res == null) {
            return null;
        }
        return new UserLogin(res.username(), res.password(), validTokens.get(username));
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
    public void joinGame(int gameID, boolean whiteTeam, String username) {
        GameData gameData = getGame(gameID);
        String whiteUser = whiteTeam ? username : gameData.whiteUsername();
        String blackUser = whiteTeam ? gameData.blackUsername() : username;
        activeGameList.set(gameID - 1, new GameData(gameID, whiteUser, blackUser, gameData.gameName()));
    }

    @Override
    public void clear() {
        userTable = new HashMap<>();
        activeGameList = new ArrayList<>(0);
        validTokens = new HashMap<>();
    }

    @Override
    public boolean validAuth(String auth) {
        return validTokens.containsKey(auth);
    }

    @Override
    public String userFromAuth(String auth) {
        return validTokens.get(auth);
    }

    @Override
    public void removeAuth(String auth) {
        validTokens.remove(auth);
    }
}
