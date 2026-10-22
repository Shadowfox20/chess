package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;

import java.util.List;

public class GameService {
    public record GameListResponse(List<GameData> games) {}

    public static GameListResponse gameList(DataAccess dataAccess) throws DataAccessException {
        return new GameListResponse(dataAccess.activeGames());
    }

    public record CreateGameResponse(int gameID) {}

    public record CreateGameRequest(String gameName) {}

    public static CreateGameResponse createGame(DataAccess dataAccess, CreateGameRequest request) throws DataAccessException, BadRequestException{
        if (request.gameName() == null || request.gameName().isEmpty()) {
            throw new BadRequestException("name is required to create a game");
        }
        GameData gameData = dataAccess.addGame(request.gameName());
        return new CreateGameResponse(gameData.gameID());
    }

    public record JoinGameRequest(int gameID, String playerColor, String username) {}

    public static void joinGame(DataAccess dataAccess, JoinGameRequest request) throws DataAccessException, BadRequestException, AlreadyExistsException{
        if (request.gameID() == 0 || request.username() == null || request.username().isEmpty() || request.playerColor() == null ||
                (!request.playerColor().equals("WHITE") && !request.playerColor().equals("BLACK"))) {
            throw new BadRequestException("select a game id and a team color (BLACK or WHITE) to join a game");
        }
        GameData gameData = dataAccess.getGame(request.gameID());
        if (gameData == null) {
            throw new BadRequestException("game with requested id not found");
        }
        if (request.playerColor().equals("WHITE") && gameData.whiteUsername() != null ||
                request.playerColor().equals("BLACK") && gameData.blackUsername() != null) {
            throw new AlreadyExistsException("the game you requested to join already has a player for the team you selected");
        }
        dataAccess.joinGame(request.gameID(), request.playerColor().equals("WHITE"), request.username());
    }
}
