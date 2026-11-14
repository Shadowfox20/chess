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
import java.util.ArrayList;
import java.util.Map;

public class ServerFacade {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final String url;
    public ServerFacade(String url) {
        this.url = url;
    }

    public record errorResponse(String message) {}
    public record loginResponse(String authToken, String username) {}

    public loginResponse register(String username, String password, String email) throws ConnectException {
        String body = new Gson().toJson(Map.of("username", username, "password", password, "email", email));
        try {
            HttpResponse<String> httpResponse = sendRequest("/user", "POST", body, null);

            if (httpResponse.statusCode() == 500) {
                System.out.println("Sorry, we hit an internal server error...");
            } else if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
                return new Gson().fromJson(httpResponse.body(), loginResponse.class);
            } else {
                errorResponse err = new Gson().fromJson(httpResponse.body(), errorResponse.class);
                System.out.println(err.message());
            }
            return new loginResponse("failed", "");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new ConnectException("There was an issue processing your request.");
        }
    }

    public loginResponse login(String username, String password) throws ConnectException {
        String body = new Gson().toJson(Map.of("username", username, "password", password));
        try {
            HttpResponse<String> httpResponse = sendRequest("/session", "POST", body, null);

            if (httpResponse.statusCode() == 500) {
                System.out.println("Sorry, we hit an internal server error...");
            } else if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
                return new Gson().fromJson(httpResponse.body(), loginResponse.class);
            } else {
                errorResponse err = new Gson().fromJson(httpResponse.body(), errorResponse.class);
                System.out.println(err.message());
            }
            return new loginResponse("failed", "");
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new ConnectException("There was an issue processing your request.");
        }
    }

    public void logout(String authToken) throws ConnectException {
        try {
            HttpResponse<String> httpResponse = sendRequest("/session", "DELETE", null, authToken);

            if (httpResponse.statusCode() == 500) {
                System.out.println("Sorry, we hit an internal server error...");
            } else if (httpResponse.statusCode() < 200 || httpResponse.statusCode() >= 300) {
                errorResponse err = new Gson().fromJson(httpResponse.body(), errorResponse.class);
                System.out.println(err.message());
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new ConnectException("There was an issue processing your request.");
        }
    }

    public record gameList(ArrayList<gameInfo> games) {}
    public record gameInfo(String gameID, String whiteUsername, String blackUsername, String gameName) {}

    public gameList list(String authToken) throws ConnectException {
        try {
            HttpResponse<String> httpResponse = sendRequest("/game", "GET", null, authToken);

            if (httpResponse.statusCode() == 500) {
                System.out.println("Sorry, we hit an internal server error...");
            } else if (httpResponse.statusCode() >= 200 && httpResponse.statusCode() < 300) {
                return new Gson().fromJson(httpResponse.body(), gameList.class);
            } else {
                errorResponse err = new Gson().fromJson(httpResponse.body(), errorResponse.class);
                System.out.println(err.message());
            }
            return null;
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new ConnectException("There was an issue processing your request.");
        }
    }

    public record createResponse(int gameID) {}

    public createResponse create(String authToken, String name) throws ConnectException {
        String body = new Gson().toJson(Map.of("gameName", name));
        try {
            HttpResponse<String> httpResponse = sendRequest("/game", "POST", body, authToken);

            if (httpResponse.statusCode() == 500) {
                System.out.println("Sorry, we hit an internal server error...");
            } else if (httpResponse.statusCode() >= 200 || httpResponse.statusCode() < 300) {
                return new Gson().fromJson(httpResponse.body(), createResponse.class);
            } else {
                errorResponse err = new Gson().fromJson(httpResponse.body(), errorResponse.class);
                System.out.println(err.message());
            }
            return null;
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new ConnectException("There was an issue processing your request.");
        }
    }

    public boolean join(String authToken, String gameID, String color) throws ConnectException {
        String body = new Gson().toJson(Map.of("gameID", gameID, "playerColor", color));
        try {
            HttpResponse<String> httpResponse = sendRequest("/game", "PUT", body, authToken);

            if (httpResponse.statusCode() == 500) {
                System.out.println("Sorry, we hit an internal server error...");
            } else if (httpResponse.statusCode() < 200 || httpResponse.statusCode() >= 300) {
                errorResponse err = new Gson().fromJson(httpResponse.body(), errorResponse.class);
                System.out.println(err.message());
                return false;
            }
            return true;
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new ConnectException("There was an issue processing your request.");
        }
    }

    public HttpResponse<String> sendRequest(String endpoint, String method, String body, String authToken)
            throws IOException, InterruptedException, URISyntaxException {
        HttpRequest request = addAuth(HttpRequest.newBuilder()
                .uri(new URI(url + endpoint))
                .timeout(Duration.ofMillis(5000))
                .method(method, requestBodyPublisher(body)), authToken)
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpRequest.BodyPublisher requestBodyPublisher(String body) {
        if (body != null) {
            return HttpRequest.BodyPublishers.ofString(body);
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    public HttpRequest.Builder addAuth(HttpRequest.Builder builder, String authToken) {
        if (authToken == null) {
            return builder;
        }
        return builder.header("authorization", authToken);
    }
}
