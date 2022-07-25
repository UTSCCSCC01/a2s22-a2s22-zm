package ca.utoronto.utm.mcs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.junit.jupiter.api.*;


import java.io.IOException;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Please write your tests in this class. 
 */
 
public class AppTest {

    private static final int PORT = 8000;




    //Start the backend server. Run at the beginning of every single test method.
    @BeforeAll
    public static void startServer() throws IOException {

        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
        server.createContext("/user", new User());
        server.createContext("/user/register", new Register());
        server.createContext("/user/login", new Login());

        // TODO: Add server contexts here. Do not set executors for the server, you shouldn't need them.

        server.start();
        System.out.printf("Server started on port %d...\n", PORT);
    }

    //Send a http request and get the http response.
    public static HttpResponse httpRequest(String method, String endpoint, String body) {
        try {
            URI uri = new URI("http://127.0.0.1:" + PORT + endpoint);
            HttpClient httpClient = HttpClient.newBuilder().build();
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(uri).method(method, HttpRequest.BodyPublishers.ofString(body)).build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            return httpResponse;


        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Test
    @Order(1)
    void addActorPass() {
        try {

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("name", "Alexander Hamilton");
            jsonObject1.put("email", "123@gmail.com");
            jsonObject1.put("password", "123456");
            HttpResponse<String> httpResponse1 = httpRequest("POST", "/user/register", jsonObject1.toString());
            assertEquals(200, httpResponse1.statusCode());



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
