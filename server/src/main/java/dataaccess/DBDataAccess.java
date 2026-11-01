package dataaccess;

import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DBDataAccess implements DataAccess{
    public DBDataAccess() throws DataAccessException {
        DatabaseManager.createDatabase();
    }

    private static boolean tableExists(Connection c, String table) throws SQLException {
        Statement statement = c.createStatement();
        String query = """
            SELECT EXISTS (
                SELECT 1
                FROM INFORMATION_SCHEMA.TABLES
                WHERE TABLE_SCHEMA = DATABASE()
                AND TABLE_NAME = """ + table + ");";
        var res = statement.executeQuery(query);
        return res.getBoolean(1);
    }

    private static void verifyUserTable(Connection c) throws SQLException {
        if (tableExists(c, "users")) {
            return;
        }
        Statement statement = c.createStatement();
        String query = """
        CREATE TABLE users (
            username VARCHAR(128),
            password VARCHAR(128),
            email VARCHAR(128)
        );
        """;
        statement.execute(query);
    }

    @Override
    public UserData createUser(String username, String password, String email) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            verifyUserTable(connection);
            Statement statement = connection.createStatement();
            String query = """
            INSERT INTO users (
                username,
                password,
                email
            ) VALUES (""" + username + ", " + BCrypt.hashpw(password, BCrypt.gensalt()) + ", " + email + ");";
            statement.execute(query);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return new UserData(username);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            verifyUserTable(connection);
            Statement statement = connection.createStatement();
            String query = "SELECT username FROM users WHERE username = " + username + ");";
            var res = statement.executeQuery(query);
            if (res.first()) {
                return new UserData(res.getString(1));
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public UserLogin getUserLogin(String username) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            verifyUserTable(connection);
            Statement statement = connection.createStatement();
            String query = "SELECT username, password FROM users WHERE username = " + username + ");";
            var res = statement.executeQuery(query);
            if (res.first()) {
                return new UserLogin(res.getString(1), res.getString(2));
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    private static void verifyGameTable(Connection c) throws SQLException {
        if (tableExists(c, "active_games")) {
            return;
        }
        Statement statement = c.createStatement();
        String query = """
        CREATE TABLE active_games (
            gameID INT AUTO_INCREMENT,
            whiteUsername VARCHAR(128),
            blackUsername VARCHAR(128),
            gameName VARCHAR(128),
            PRIMARY KEY (gameID)
        );
        """;
        statement.execute(query);
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
