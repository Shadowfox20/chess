package client;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

import chess.ChessPiece;
import chess.ChessGame;

public class ClientMain {
    private static class InsufficientArgumentsError extends Exception {
        public InsufficientArgumentsError(String message) {
            super(message);
        }
    }

    private record request(String command, String[] body) {}

    private static final HttpClient httpClient = HttpClient.newHttpClient();

    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        Scanner scanner = new Scanner(System.in);
        loggedOut(scanner);
        scanner.close();
    }

    private static void loggedOut(Scanner scanner) {
        while (true) {
            System.out.print("[Logged Out] >> ");
            String raw = scanner.nextLine();

            try {
                request request = parseRequest(raw);
                switch (request.command().toLowerCase(Locale.ROOT)) {
                    case "register" -> {
                        if (request.body().length < 3) {
                            throw new InsufficientArgumentsError("register requires 3 arguments - username, password and email");
                        }
                        String username = request.body()[0];
                        String password = request.body()[1];
                        String email = request.body()[2];
                        loginResponse response = register(username, password, email);
                        if (Objects.equals(response.authToken(), "failed")) {
                            continue;
                        }
                        boolean quit = loggedIn(scanner, response.username(), response.authToken());
                        if (quit) {
                            System.out.println("Until next time!");
                            return;
                        }
                    }
                    case "login" -> {
                        if (request.body().length < 2) {
                            throw new InsufficientArgumentsError("login requires 2 arguments - username, password");
                        }
                        String username = request.body()[0];
                        String password = request.body()[1];
                        loginResponse response = login(username, password);
                        if (Objects.equals(response.authToken(), "failed")) {
                            continue;
                        }
                        boolean quit = loggedIn(scanner, response.username(), response.authToken());
                        if (quit) {
                            System.out.println("Until next time!");
                            return;
                        }
                    }
                    case "help" -> {
                        System.out.println("""
                             //========================================+=============================\\\\
                            || command [arguments]                     | description                  ||
                            ||=========================================|==============================||
                            || register [username] [password] [email]  | create an account            ||
                            |+-----------------------------------------+------------------------------+|
                            || login [username] [password]             | login to an existing account ||
                            |+-----------------------------------------+------------------------------+|
                            || quit                                    | exit program                 ||
                            |+-----------------------------------------+------------------------------+|
                            || help                                    | display commands             ||
                             \\\\========================================+=============================//
                            """);
                    }
                    case "quit" -> {
                        System.out.println("Until next time!");
                        return;
                    }
                    default -> {
                        System.out.println("Command not recognized. Type 'help' to see available commands.");
                    }
                }
            } catch (InsufficientArgumentsError e) {
                System.out.println("Insufficient arguments: " + e);
            } catch (ConnectException e) {
                System.out.println("Unable to connect: " + e);
            }
        }
    }

    private static boolean loggedIn(Scanner scanner, String username, String authToken) {
        while (true) {
            System.out.print("[" + username + "] >> ");
            String raw = scanner.nextLine();

            try {
                request request = parseRequest(raw);
                switch (request.command().toLowerCase(Locale.ROOT)) {
                    case "create" -> {
                        if (request.body().length == 0) {
                            throw new InsufficientArgumentsError("create requires 1 argument - game name");
                        }
                    }
                    case "join" -> {

                    }
                    case "observe" -> {

                    }
                    case "list" -> {

                    }
                    case "logout" -> {
                        logout(authToken);
                        return false;
                    }
                    case "quit" -> {
                        return true;
                    }
                    case "help" -> {
                        System.out.println("""
                                 //========================================+===============================================\\\\
                                || command [arguments]                     | description                                    ||
                                ||=========================================|================================================||
                                || create [name]                           | start a new game with chosen name              ||
                                |+-----------------------------------------+------------------------------------------------+|
                                || join [ID] [WHITE | BLACK]               | join a game using a color; use list to see ids ||
                                |+-----------------------------------------+------------------------------------------------+|
                                || observe [ID]                            | watch an active game                           ||
                                |+-----------------------------------------+------------------------------------------------+|
                                || logout                                  | end session                                    ||
                                |+-----------------------------------------+------------------------------------------------+|
                                || list                                    | list available games and ids                   ||
                                |+-----------------------------------------+------------------------------------------------+|
                                || quit                                    | exit program                                   ||
                                |+-----------------------------------------+------------------------------------------------+|
                                || help                                    | display commands                               ||
                                 \\\\========================================+===============================================//
                                """);
                    }
                    default -> {
                        System.out.println("Command not recognized. Enter \"help\" to see commands.");
                    }
                }
            } catch (InsufficientArgumentsError e) {
                System.out.println("Insufficient arguments: " + e);
            } catch (ConnectException e) {
                System.out.println("Unable to connect: " + e);
            }
        }
    }

    private record loginResponse(String authToken, String username) {}

    private static loginResponse register(String username, String password, String email) throws ConnectException {
        String body = new Gson().toJson(Map.of("username", username, "password", password, "email", email));
        try {
            HttpResponse<String> httpResponse = sendRequest("/user", "POST", body);

            if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
                return new Gson().fromJson(httpResponse.body(), loginResponse.class);
            } else {
                System.out.println(httpResponse.body());
            }
            return new loginResponse("failed", "");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new ConnectException("There was an issue processing your request.");
        }
    }

    private static loginResponse login(String username, String password) throws ConnectException {
        String body = new Gson().toJson(Map.of("username", username, "password", password));
        try {
            HttpResponse<String> httpResponse = sendRequest("/session", "POST", body);

            if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
                return new Gson().fromJson(httpResponse.body(), loginResponse.class);
            } else {
                System.out.println(httpResponse.body());
            }
            return new loginResponse("failed", "");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new ConnectException("There was an issue processing your request.");
        }
    }

    private static void logout(String authToken) throws ConnectException {
        String body = new Gson().toJson(Map.of("authToken", authToken));
        try {
            HttpResponse<String> httpResponse = sendRequest("/session", "DELETE", body);

            if (httpResponse.statusCode() < 200 || httpResponse.statusCode() >= 300) {
                System.out.println(httpResponse.body());
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new ConnectException("There was an issue processing your request.");
        }
    }

    private static HttpResponse<String> sendRequest(String endpoint, String method, String body)
            throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080" + endpoint))
                .timeout(Duration.ofMillis(5000))
                .method(method, HttpRequest.BodyPublishers.ofString(body))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static request parseRequest(String rawString) throws InsufficientArgumentsError {
        String[] split = rawString.split(" ");
        if (split.length == 0) {
            throw new InsufficientArgumentsError("Please enter a command. Enter \"help\" to see commands.");
        }
        return new request(split[0], Arrays.copyOfRange(split, 1, split.length));
    }
}
