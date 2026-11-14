package client;

import java.util.*;

public class ClientMain {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("♕ WELCOME to 240 Chess Client!");
        Client client = new Client(new ServerFacade("http://localhost:8080"));
        client.run(scanner);
        scanner.close();
    }
}
