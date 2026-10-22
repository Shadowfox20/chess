package dataaccess;

import java.util.HashMap;

public class MemoryDataAccess implements DataAccess {
    private record user(String username, String password, String email) {}

    private final HashMap<String, user> userTable = new HashMap<>();

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
}
