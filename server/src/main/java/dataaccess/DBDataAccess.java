package dataaccess;

import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
                AND TABLE_NAME = '""" + table + "');";
        var res = statement.executeQuery(query);
        res.next();
        return res.getBoolean(1);
    }

    private static void verifyUserTable(Connection c) throws SQLException {
        if (tableExists(c, "users")) {
            return;
        }
        Statement statement = c.createStatement();
        String query = """
        CREATE TABLE users (
            username VARCHAR(128) NOT NULL,
            password VARCHAR(128) NOT NULL,
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
            ) VALUE ('""" + username + "', '" + BCrypt.hashpw(password, BCrypt.gensalt()) + "', '" + email + "');";
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
            String query = """
            SELECT username
            FROM users
            WHERE username = '""" + username + "';";
            var res = statement.executeQuery(query);
            UserData data = null;
            if (res.next()) {
                data = new UserData(res.getString(1));
            }
            return data;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public UserLogin getUserLogin(String username) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            verifyUserTable(connection);
            Statement statement = connection.createStatement();
            String query = "SELECT username, password FROM users WHERE username = '" + username + "';";
            var res = statement.executeQuery(query);
            UserLogin login = null;
            if (res.next()) {
                login = new UserLogin(res.getString(1), res.getString(2));
            }
            return login;
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
            gameID INT PRIMARY KEY AUTO_INCREMENT,
            whiteUsername VARCHAR(128),
            blackUsername VARCHAR(128),
            gameName VARCHAR(128) NOT NULL
        );
        """;
        statement.execute(query);
    }

    @Override
    public GameData addGame(String name) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            verifyGameTable(connection);
            Statement statement = connection.createStatement();
            String query = """
            INSERT INTO active_games (
                gameName
            ) VALUE ('""" + name + "');";
            statement.execute(query);
            query = """
            SELECT gameID, whiteUsername, blackUsername, gameName
            FROM active_games
            WHERE gameName = '""" + name + "';";
            var res = statement.executeQuery(query);
            if (res.next()) {
                return new GameData(res.getInt(1), res.getString(2), res.getString(3), res.getString(4));
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            verifyGameTable(connection);
            Statement statement = connection.createStatement();
            String query = """
            SELECT gameID, whiteUsername, blackUsername, gameName
            FROM active_games
            WHERE gameID =\s""" + gameID + ";";
            var res = statement.executeQuery(query);
            if (res.next()) {
                return new GameData(res.getInt(1), res.getString(2), res.getString(3), res.getString(4));
            }
            return null;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public List<GameData> activeGames() throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            verifyGameTable(connection);
            Statement statement = connection.createStatement();
            String query = """
            SELECT gameID, whiteUsername, blackUsername, gameName
            FROM active_games
            ORDER BY gameID ASC;
            """;
            var res = statement.executeQuery(query);
            ArrayList<GameData> games = new ArrayList<>();
            while (res.next()) {
                games.add(new GameData(res.getInt(1), res.getString(2), res.getString(3), res.getString(4)));
            }
            return games;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void joinGame(int gameID, boolean whiteTeam, String username) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            String team = "blackUsername";
            if (whiteTeam) {
                team = "whiteUsername";
            }
            verifyGameTable(connection);
            Statement statement = connection.createStatement();
            String query = """
            UPDATE active_games
            SET\s""" + team + " = '" + username + "'" + """
            WHERE gameID =\s""" + gameID + ";";
            statement.execute(query);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            Statement statement = connection.createStatement();
            String query = "DROP DATABASE chess;";
            statement.execute(query);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        DatabaseManager.createDatabase();
    }
}
