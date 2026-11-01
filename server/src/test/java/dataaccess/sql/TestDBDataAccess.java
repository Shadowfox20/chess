package dataaccess.sql;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.opentest4j.AssertionFailedError;

public class TestDBDataAccess {
    DBDataAccess dbDataAccess;

    {
        try {
            dbDataAccess = new DBDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(1)
    @DisplayName("user DB expected behavior")
    public void userDBAccess() {
        try {
            dbDataAccess.clear();
            dbDataAccess.createUser("test_user", "passing_word", "email");
            Assertions.assertTrue(dbDataAccess.userExists("test_user"));
            var login = dbDataAccess.getUserLogin("test_user");
            Assertions.assertTrue(BCrypt.checkpw("passing_word", login.hashedPassword()));
        } catch (DataAccessException e) {
            throw new AssertionFailedError("unexpected error: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DisplayName("auth DB expected behavior")
    public void authDBAccess() {
        try {
            dbDataAccess.clear();
            dbDataAccess.addAuth("test_auth", "test_user");
            Assertions.assertTrue(dbDataAccess.validAuth("test_auth"));
            String user = dbDataAccess.userFromAuth("test_auth");
            Assertions.assertEquals("test_user", user);
            dbDataAccess.removeAuth("test_auth");
            Assertions.assertFalse(dbDataAccess.validAuth("test_auth"));
        } catch (DataAccessException e) {
            throw new AssertionFailedError("unexpected error: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    @DisplayName("game DB expected behavior")
    public void gameDBAccess() {
        try {
            dbDataAccess.clear();
            var gd = dbDataAccess.addGame("test_game");
            Assertions.assertNotNull(dbDataAccess.getGame(gd.gameID()));
            dbDataAccess.joinGame(gd.gameID(), true, "test_user");
            Assertions.assertEquals("test_user", dbDataAccess.getGame(gd.gameID()).whiteUsername());
            dbDataAccess.addGame("test_game_2");
            dbDataAccess.addGame("test_game_3");
            Assertions.assertEquals(3, dbDataAccess.activeGames().size());
        } catch (DataAccessException e) {
            throw new AssertionFailedError("unexpected error: " + e.getMessage());
        }
    }
}
