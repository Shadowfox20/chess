package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import service.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Server {

    private final Javalin javalin;
    final private HashMap<String, String> validTokens = new HashMap<>();
    private DataAccess dataAccess;
    private final Gson serializer = new Gson();

    public Server(DataAccess dataAccess) {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.delete("/db", this::clearDB);
        javalin.post("/user", this::registerHandler);
        javalin.post("/session", this::loginHandler);
        javalin.delete("/session", this::logoutHandler);
        javalin.get("/list", this::listGamesHandler);
        // Register your endpoints and exception handlers here.

        this.dataAccess = dataAccess;
    }

    private void clearDB(Context context) {
        dataAccess = new MemoryDataAccess();
    }

    private void registerHandler(Context context) {
        UserService.RegisterRequest request = serializer.fromJson(context.body(), UserService.RegisterRequest.class);
        try {
            UserService.RegisterResponse response = UserService.Register(dataAccess, request);
            String auth = UUID.randomUUID().toString();
            validTokens.put(auth, request.username());
            context.status(200);
            context.result(serializer.toJson(Map.of("authToken", auth,"username", response.username())));
        } catch (DataAccessException e) {
            context.status(500);
            context.result(serializer.toJson(Map.of("message", "Error: " + e.getMessage())));
        } catch (AlreadyExistsException e) {
            context.status(403);
            context.result(serializer.toJson(Map.of("message", "Error: already taken")));
        } catch (BadRequestException e) {
            context.status(400);
            context.result(serializer.toJson(Map.of("message", "Error: bad request")));
        }
    }

    private void loginHandler(Context context) {
        UserService.LoginRequest request = serializer.fromJson(context.body(), UserService.LoginRequest.class);
        try {
            UserService.LoginResponse response = UserService.Login(dataAccess, request);
            String auth = UUID.randomUUID().toString();
            validTokens.put(auth, request.username());
            context.status(200);
            context.result(serializer.toJson(Map.of("authToken", auth,"username", response.username())));
        } catch (DataAccessException e) {
            context.status(500);
            context.result(serializer.toJson(Map.of("message", "Error: " + e.getMessage())));
        } catch (IncorrectLoginException e) {
            context.status(401);
            context.result(serializer.toJson(Map.of("message", "Error: unauthorized")));
        } catch (BadRequestException e) {
            context.status(400);
            context.result(serializer.toJson(Map.of("message", "Error: bad request")));
        }
    }

    private void logoutHandler(Context context) {
        if (!authorized(context)) {
            return;
        }
        String authToken = context.header("authorization");
        validTokens.remove(authToken);
        context.status(200);
    }

    private void listGamesHandler(Context context) {
        if (!authorized(context)) {
            return;
        }
        try {
            GameService.GameListResponse games = GameService.GameList(dataAccess);

            context.status(200);
            context.result(serializer.toJson(Map.of("list", games.list())));
        } catch (DataAccessException e) {
            context.status(500);
            context.result(serializer.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    private boolean authorized(Context ctx) {
        String authToken = ctx.header("authorization");
        if (!validTokens.containsKey(authToken)) {
            ctx.contentType("application/json");
            ctx.status(401);
            ctx.result(serializer.toJson(Map.of("message", "Error: unauthorized")));
            return false;
        }
        return true;
    }

    private boolean authorized(Context ctx, String username) {
        String authToken = ctx.header("authorization");
        if (!validTokens.get(authToken).equals(username)) {
            ctx.contentType("application/json");
            ctx.status(401);
            ctx.result(serializer.toJson(Map.of("message", "Error: unauthorized")));
            return false;
        }
        return true;
    }

    private String userFromAuth(Context ctx) {
        return validTokens.get(ctx.header("authorization"));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
