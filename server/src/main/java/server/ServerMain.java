package server;

import chess.*;
import dataaccess.MemoryDataAccess;

public class ServerMain {
    public static void main(String[] args) {
        var port = 8080;
        Server server = new Server(new MemoryDataAccess());
        server.run(port);

        System.out.println("♕ 240 Chess Server running on port " + port);
    }
}
