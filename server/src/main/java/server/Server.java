package server;

import com.google.gson.Gson;
import dataaccess.DBDataAccess;
import dataaccess.DataAccessException;
import dataaccess.DataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import service.*;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final DataAccess dataAccess;
    private final Gson serializer = new Gson();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.delete("/db", this::clearDB);
        javalin.post("/user", this::registerHandler);
        javalin.post("/session", this::loginHandler);
        javalin.delete("/session", this::logoutHandler);
        javalin.get("/game", this::listGamesHandler);
        javalin.post("/game", this::createGameHandler);
        javalin.put("/game", this::joinGameHandler);
        // Register your endpoints and exception handlers here.

        try {
            this.dataAccess = new DBDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearDB(Context context) {
        try {
            DataService.Clear(dataAccess);
        } catch (DataAccessException e) {
            context.status(500);
            context.result(serializer.toJson(Map.of("message", "Error: " + e.getMessage())));
        }

    }

    private void registerHandler(Context context) {
        UserService.RegisterRequest request = serializer.fromJson(context.body(), UserService.RegisterRequest.class);
        try {
            UserService.RegisterResponse response = UserService.register(dataAccess, request);
            context.status(200);
            context.result(serializer.toJson(Map.of("authToken", response.auth(),"username", response.username())));
        } catch (DataAccessException e) {
            context.status(500);
            context.result(serializer.toJson(Map.of("message", "Error: " + e.getMessage())));
        } catch (AlreadyExistsException e) {
            context.status(403);
            context.result(serializer.toJson(Map.of("message", "Error: already taken")));
        } catch (BadRequestException e) {
            context.status(400);
            context.result(serializer.toJson(Map.of("message", "Error: bad request: " + e.getMessage())));
        }
    }

    private void loginHandler(Context context) {
        UserService.LoginRequest request = serializer.fromJson(context.body(), UserService.LoginRequest.class);
        try {
            UserService.LoginResponse response = UserService.login(dataAccess, request);
            context.status(200);
            context.result(serializer.toJson(Map.of("authToken", response.auth(),"username", response.username())));
        } catch (DataAccessException e) {
            context.status(500);
            context.result(serializer.toJson(Map.of("message", "Error: " + e.getMessage())));
        } catch (IncorrectLoginException e) {
            context.status(401);
            context.result(serializer.toJson(Map.of("message", "Error: unauthorized")));
        } catch (BadRequestException e) {
            context.status(400);
            context.result(serializer.toJson(Map.of("message", "Error: bad request: " + e.getMessage())));
        }
    }

    private void logoutHandler(Context context) {
        try {
            if (invalidAuthToken(context)) {
                return;
            }
            UserService.RemoveAuth(dataAccess, context.header("authorization"));
            context.status(200);
        } catch (DataAccessException e) {
            context.status(500);
            context.result(serializer.toJson(Map.of("message", "Error: " + e.getMessage())));
            throw new RuntimeException(e);
        }
    }

    private void listGamesHandler(Context context) {
        try {
            if (invalidAuthToken(context)) {
                return;
            }
            GameService.GameListResponse games = GameService.gameList(dataAccess);
            context.status(200);
            context.result(serializer.toJson(games));
        } catch (DataAccessException e) {
            context.status(500);
            context.result(serializer.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    private void createGameHandler(Context context) {
        GameService.CreateGameRequest request = serializer.fromJson(context.body(), GameService.CreateGameRequest.class);
        try {
            if (invalidAuthToken(context)) {
                return;
            }
            GameService.CreateGameResponse game = GameService.createGame(dataAccess, request);
            context.status(200);
            context.result(serializer.toJson(game));
        } catch (DataAccessException e) {
            context.status(500);
            context.result(serializer.toJson(Map.of("message", "Error: " + e.getMessage())));
        } catch (BadRequestException e) {
            context.status(400);
            context.result(serializer.toJson(Map.of("message", "Error: bad request: " + e.getMessage())));
        }
    }

    private void joinGameHandler(Context context) {
        GameService.JoinGameRequest request = serializer.fromJson(context.body(), GameService.JoinGameRequest.class);
        try {
            if (invalidAuthToken(context)) {
                return;
            }
            GameService.JoinGameRequest userReq = new GameService.JoinGameRequest(request.gameID(), request.playerColor(),
                    UserService.UserFromAuth(dataAccess, context.header("authorization")));
            GameService.joinGame(dataAccess, userReq);
            context.status(200);
        } catch (DataAccessException e) {
            context.status(500);
            context.result(serializer.toJson(Map.of("message", "Error: " + e.getMessage())));
        } catch (AlreadyExistsException e) {
            context.status(403);
            context.result(serializer.toJson(Map.of("message", "Error: already taken")));
        } catch (BadRequestException e) {
            context.status(400);
            context.result(serializer.toJson(Map.of("message", "Error: bad request: " + e.getMessage())));
        }
    }

    private boolean invalidAuthToken(Context ctx) throws DataAccessException {
        String authToken = ctx.header("authorization");
        if (UserService.InvalidAuth(dataAccess, authToken)) {
            ctx.contentType("application/json");
            ctx.status(401);
            ctx.result(serializer.toJson(Map.of("message", "Error: unauthorized")));
            return true;
        }
        return false;
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
