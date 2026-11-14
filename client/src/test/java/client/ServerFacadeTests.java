package client;

import dataaccess.DataAccessException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Assertions;
import server.Server;

import java.net.ConnectException;

public class ServerFacadeTests {
    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        try {
            server.clearDB();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        var port = server.run(0);
        facade = new ServerFacade("http://localhost:" + port);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }
    
    @Test
    @Order(1)
    @DisplayName("login and register")
    public void login() {
        try {
            var resp = facade.register("Test_Login", "p", "e");
            Assertions.assertEquals("Test_Login", resp.username());
            Assertions.assertNotEquals("failed", resp.authToken());
            resp = facade.login("Test_Login", "p");
            Assertions.assertEquals("Test_Login", resp.username());
            Assertions.assertNotEquals("failed", resp.authToken());
            // validate auth
            facade.list(resp.authToken());

            resp = facade.register("Test_Login", "np", "e");
            Assertions.assertEquals("failed", resp.authToken());
            resp = facade.login("Test_Login", "np");
            Assertions.assertEquals("failed", resp.authToken());
        } catch (ConnectException e) {
            Assertions.fail("hit an unexpected error: " + e);
        }
    }

    @Test
    @Order(2)
    @DisplayName("create and list game")
    public void list() {
        try {
            var reg = facade.register("Test_List", "p", "e");
            facade.create(reg.authToken(), "List Games Test");
            var list = facade.list(reg.authToken());
            boolean found = false;
            for (var game : list.games()) {
                if (game.gameName().equals("List Games Test")) {
                    found = true;
                    break;
                }
            }
            Assertions.assertTrue(found);
        } catch (ConnectException e) {
            Assertions.fail("hit an unexpected error: " + e);
        }
    }

    @Test
    @Order(3)
    @DisplayName("create and join game")
    public void join() {
        try {
            var reg = facade.register("Test_Join", "p", "e");
            var game = facade.create(reg.authToken(), "List Games Test");
            Assertions.assertTrue(facade.join(reg.authToken(), String.valueOf(game.gameID()), "WHITE"));
            Assertions.assertFalse(facade.join(reg.authToken(), String.valueOf(game.gameID()), "WHITE"));
        } catch (ConnectException e) {
            Assertions.fail("hit an unexpected error: " + e);
        }
    }
}
