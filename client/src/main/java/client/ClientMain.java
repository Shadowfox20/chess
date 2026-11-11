package client;

import chess.*;
import service.BadRequestException;

import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class ClientMain {
    private record request(String command, String[] body) {};

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
            request request = parseRequest(raw);

            switch (request.command().toLowerCase(Locale.ROOT)) {
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
                    return;
                }
            }
        }
    }

    private static request parseRequest(String rawString) throws BadRequestException {
        String[] split = rawString.split(" ");
        if (split.length == 0) {
            throw new BadRequestException("please enter a command. enter \"help\" to see commands.");
        }
        return new request(split[0], Arrays.copyOfRange(split, 1, split.length));
    }
}
