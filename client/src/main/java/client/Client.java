package client;

import chess.ChessGame;
import ui.ChessDisplay;

import java.net.ConnectException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

public class Client {
    private final ServerFacade server;
    
    public Client(ServerFacade server) {
        this.server = server;
    }
    
    private static class InsufficientArgumentsError extends Exception {
        public InsufficientArgumentsError(String message) {
            super(message);
        }
    }
    public void run(Scanner scanner) {
        loggedOut(scanner);
    }

    private record request(String command, String[] body) {}

    private void loggedOut(Scanner scanner) {
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
                        ServerFacade.loginResponse response = server.register(username, password, email);
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
                        ServerFacade.loginResponse response = server.login(username, password);
                        if (Objects.equals(response.authToken(), "failed")) {
                            continue;
                        }
                        System.out.println("Welcome!");
                        if (loggedIn(scanner, response.username(), response.authToken())) {
                            System.out.println("Until next time!");
                            return;
                        }
                    }
                    case "help" -> {
                        System.out.println("""
                             \u001B[38;5;46m//========================================+=============================\\\\
                            || command [arguments]                     | description                  ||
                            ||=========================================|==============================||
                            || register [username] [password] [email]  | create an account            ||
                            |+-----------------------------------------+------------------------------+|
                            || login [username] [password]             | login to an existing account ||
                            |+-----------------------------------------+------------------------------+|
                            || quit                                    | exit program                 ||
                            |+-----------------------------------------+------------------------------+|
                            || help                                    | display commands             ||
                             \\\\========================================+=============================//\u001b[0m
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
                System.out.println("Insufficient arguments: " + e.getMessage());
            } catch (ConnectException e) {
                System.out.println("Unable to connect: " + e.getMessage());
            }
        }
    }

    private boolean loggedIn(Scanner scanner, String username, String authToken) {
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
                        String name = request.body()[0];
                        ServerFacade.createResponse response = server.create(authToken, name);
                        if (response != null) {
                            System.out.println("Game created with id " + response.gameID());
                        }
                    }
                    case "join" -> {
                        if (request.body().length < 2) {
                            throw new InsufficientArgumentsError("create requires 2 argument - game ID and color (WHITE or BLACK)");
                        }
                        String gameID = request.body()[0];
                        String color = request.body()[1];
                        if (server.join(authToken, gameID, color)) {
                            System.out.println("Successfully joined! Opening game window...");
                            new ChessDisplay(new ChessGame()).display(color);
                            inGame(scanner, username);
                        }
                    }
                    case "observe" -> {
                        if (request.body().length == 1) {
                            throw new InsufficientArgumentsError("create requires 1 argument - game ID");
                        }
                        System.out.println("Successfully joined! Opening game window...");
                        new ChessDisplay(new ChessGame()).display("WHITE");
                        inGame(scanner, username);
                    }
                    case "list" -> {
                        ServerFacade.gameList list = server.list(authToken);
                        if (list == null) {
                            continue;
                        }
                        for (ServerFacade.gameInfo game : list.games()) {
                            System.out.println(game.gameID() + " " + game.gameName() + ": WHITE [" +
                                    game.whiteUsername() + "], BLACK [" + game.blackUsername() + "]");
                        }
                    }
                    case "logout" -> {
                        server.logout(authToken);
                        return false;
                    }
                    case "quit" -> {
                        return true;
                    }
                    case "help" -> System.out.println("""
                                 \u001B[38;5;46m//========================================+===============================================\\\\
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
                                 \\\\========================================+===============================================//\u001b[0m
                                """);
                    default -> System.out.println("Command not recognized. Enter \"help\" to see commands.");
                }
            } catch (InsufficientArgumentsError e) {
                System.out.println("Insufficient arguments: " + e.getMessage());
            } catch (ConnectException e) {
                System.out.println("Unable to connect: " + e.getMessage());
            }
        }
    }

    private void inGame(Scanner scanner, String username) {
        while (true) {
            System.out.print("[" + username + "] >> ");
            String raw = scanner.nextLine();

            try {
                request request = parseRequest(raw);
                switch (request.command().toLowerCase(Locale.ROOT)) {
                    case "exit" -> {
                        return;
                    }
                    case "help" -> System.out.println("""
                                 \u001B[38;5;46m//========================================+===============================================\\\\
                                || command [arguments]                     | description                                    ||
                                ||=========================================|================================================||
                                || exit                                    | exit game                                      ||
                                |+-----------------------------------------+------------------------------------------------+|
                                || help                                    | display commands                               ||
                                 \\\\========================================+===============================================//\u001b[0m
                                """);
                    default -> System.out.println("Command not recognized. Enter \"help\" to see commands.");
                }
            } catch (InsufficientArgumentsError e) {
                System.out.println("Insufficient arguments: " + e.getMessage());
            }
        }
    }

    private request parseRequest(String rawString) throws InsufficientArgumentsError {
        String[] split = rawString.split(" ");
        if (split.length == 0) {
            throw new InsufficientArgumentsError("Please enter a command. Enter \"help\" to see commands.");
        }
        return new request(split[0], Arrays.copyOfRange(split, 1, split.length));
    }
}
