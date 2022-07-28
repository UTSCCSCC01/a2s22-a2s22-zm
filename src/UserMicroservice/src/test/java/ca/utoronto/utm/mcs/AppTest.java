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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppTest {

    private static final int PORT = 8000;
    private static HttpServer server;





    //Start the backend server. Run at the beginning of every single test method.
    /*
    @BeforeAll
    public static void startServer() throws IOException {

        server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
        server.createContext("/user", new User());
        server.createContext("/user/register", new Register());
        server.createContext("/user/login", new Login());

        // TODO: Add server contexts here. Do not set executors for the server, you shouldn't need them.

        server.start();
        System.out.printf("Server started on port %d...\n", PORT);

        httpRequest("POST", "/user/clear", "");
        System.out.printf("Database cleared\n");


    }
    */

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
    void userRegisterPass() {
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("name", "Alexander Hamilton");
            jsonObject1.put("email", "456@gmail.com");
            jsonObject1.put("password", "123456");
            HttpResponse<String> httpResponse1 = httpRequest("POST", "/user/register", jsonObject1.toString());
            assertEquals(200, httpResponse1.statusCode());

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("name", "Lewis Hamilton");
            jsonObject2.put("email", "789@gmail.com");
            jsonObject2.put("password", "123456");
            HttpResponse<String> httpResponse2 = httpRequest("POST", "/user/register", jsonObject2.toString());
            assertEquals(200, httpResponse2.statusCode());


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(2)
    void userRegisterFail() {
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("name", "Alexander Hamilton");
            jsonObject1.put("password", "123456");
            HttpResponse<String> httpResponse1 = httpRequest("POST", "/user/register", jsonObject1.toString());
            assertEquals(400, httpResponse1.statusCode());


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(3)
    void userLoginPass() {
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("email", "456@gmail.com");
            jsonObject1.put("password", "123456");
            HttpResponse<String> httpResponse1 = httpRequest("POST", "/user/login", jsonObject1.toString());
            assertEquals(200, httpResponse1.statusCode());


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(4)
    void userLoginFail() {
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("email", "456@gmail.com");
            HttpResponse<String> httpResponse1 = httpRequest("POST", "/user/login", jsonObject1.toString());
            assertEquals(400, httpResponse1.statusCode());

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("email", "4@gmail.com");
            jsonObject2.put("password", "123456");
            HttpResponse<String> httpResponse2 = httpRequest("POST", "/user/login", jsonObject2.toString());
            assertEquals(404, httpResponse2.statusCode());

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("email", "456@gmail.com");
            jsonObject3.put("password", "456");
            HttpResponse<String> httpResponse3 = httpRequest("POST", "/user/login", jsonObject3.toString());
            assertEquals(401, httpResponse3.statusCode());


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    @AfterAll
    public static void shutdownServer() {
        httpRequest("POST", "/user/clear", "");
        server.stop(0);
    }
    */




}
