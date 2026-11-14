package client;

import org.junit.jupiter.api.*;
import server.Server;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class ServerFacadeTests {

    private static Server server;
    private static ClientMain client;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = null;
        try {
            request = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:" + port + "/clear"))
                    .timeout(Duration.ofMillis(5000))
                    .GET()
                    .build();
            httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    @Order(1)
    @DisplayName("Test Input Happy Path")
    public void TestInput_HappyPath() {
        InputStream originalIn = System.in;

        try {
            String simulatedInput = """
                    help
                    register TestInput_HappyPath p e
                    help
                    list
                    create g
                    list
                    join 1 WHITE
                    exit
                    join 1 BLACK
                    exit
                    logout TestInput_HappyPath p
                    login
                    observe 1
                    quit
                    """;
            ByteArrayInputStream testIn = new ByteArrayInputStream(
                    simulatedInput.getBytes(StandardCharsets.UTF_8)
            );
            System.setIn(testIn);
            System.setIn(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));
            ClientMain.main(null);
        } finally {
            System.setIn(originalIn);
        }
    }

    @Test
    @Order(2)
    @DisplayName("Test help")
    public void TestInput_Help() {
        assertTrue(testIO("""
                    help
                    quit
                    """, "==================="));
        assertTrue(testIO("""
                    register TestInput_Help p e
                    help
                    quit
                    """, "==================="));
    }

    @Test
    @Order(3)
    @DisplayName("Test register/login")
    public void TestInput_Login() {
        assertTrue(testIO("""
                    register TestInput_Login p e
                    quit
                    """, "[TestInput_Login]"));
        assertTrue(testIO("""
                    login TestInput_Login
                    quit
                    """, "[TestInput_Login]"));
    }

    private boolean testIO(String input, String expectedOut) {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        try {
            ByteArrayOutputStream outContent = new ByteArrayOutputStream();
            System.setOut(new PrintStream(outContent, true, StandardCharsets.UTF_8));
            System.setIn(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)));
            ClientMain.main(null);
            String actualOutput = outContent.toString(StandardCharsets.UTF_8);
            return actualOutput.contains(expectedOut);
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
    }
}
