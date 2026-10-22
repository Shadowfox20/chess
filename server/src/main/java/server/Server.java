package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DataAccess;
import io.javalin.*;
import io.javalin.http.Context;
import service.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Server {

    private final Javalin javalin;
    final private HashMap<String, String> validTokens = new HashMap<>();
    private final DataAccess dataAccess;

    public Server(DataAccess dataAccess) {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        javalin.post("/user", this::registerHandler);
        javalin.post("/session", this::loginHandler);
        javalin.delete("/session", this::logoutHandler);
        javalin.get("/list", this::listGamesHandler);
        // Register your endpoints and exception handlers here.

        this.dataAccess = dataAccess;
    }

    private void registerHandler(Context context) {
        RegisterResponse.RegisterRequest request = context.bodyAsClass(RegisterResponse.RegisterRequest.class);
        try {
            RegisterResponse response = new RegisterResponse(dataAccess, request);
            String auth = UUID.randomUUID().toString();
            validTokens.put(auth, request.username());
            context.status(200);
            context.result(new Gson().toJson(Map.of("authData", auth,"username", response.username())));
        } catch (DataAccessException e) {
            context.status(500);
            context.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        } catch (AlreadyExistsException e) {
            context.status(403);
            context.result(new Gson().toJson(Map.of("message", "Error: already taken")));
        }
    }

    private void loginHandler(Context context) {
        LoginResponse.LoginRequest request = context.bodyAsClass(LoginResponse.LoginRequest.class);
        try {
            LoginResponse response = new LoginResponse(dataAccess, request);
            String auth = UUID.randomUUID().toString();
            validTokens.put(auth, request.username());
            context.status(200);
            context.result(new Gson().toJson(Map.of("authData", auth,"username", response.username())));
        } catch (DataAccessException e) {
            context.status(500);
            context.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        } catch (IncorrectLoginException e) {
            context.status(401);
            context.result(new Gson().toJson(Map.of("message", "Error: unauthorized")));
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
            GameListResponse games = new GameListResponse(dataAccess);

            context.status(200);
            context.result(new Gson().toJson(Map.of("list", games.list())));
        } catch (DataAccessException e) {
            context.status(500);
            context.result(new Gson().toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    private boolean authorized(Context ctx) {
        String authToken = ctx.header("authorization");
        if (!validTokens.containsKey(authToken)) {
            ctx.contentType("application/json");
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message", "Error: unauthorized")));
            return false;
        }
        return true;
    }

    private boolean authorized(Context ctx, String username) {
        String authToken = ctx.header("authorization");
        if (!validTokens.get(authToken).equals(username)) {
            ctx.contentType("application/json");
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("message", "Error: unauthorized")));
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
