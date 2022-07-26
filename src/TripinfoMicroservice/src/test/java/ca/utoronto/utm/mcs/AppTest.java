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
    private static String id;


    @BeforeAll
    public static void startServer() throws IOException {

        server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
        server.createContext("/trip/request", new Request());
        server.createContext("/trip/confirm", new Confirm());
        server.createContext("/trip/", new Trip());
        server.createContext("/trip/passenger/", new Passenger());
        server.createContext("/trip/driver/", new Driver());
        server.createContext("/trip/driverTime/", new Drivetime());
        server.createContext("/trip/clear", new Request());

        // TODO: Add server contexts here. Do not set executors for the server, you shouldn't need them.

        server.start();
        System.out.printf("Server started on port %d...\n", PORT);

        httpRequest("DELETE", "/trip/clear", "");
        System.out.printf("Database cleared\n");


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
    void confirmTripPass() {
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("driver", "2");
            jsonObject1.put("passenger", "1");
            jsonObject1.put("startTime", "123456");
            HttpResponse<String> httpResponse1 = httpRequest("POST", "/trip/confirm", jsonObject1.toString());
            assertEquals(200, httpResponse1.statusCode());
            JSONObject jsonResponse = new JSONObject(httpResponse1.body());
            id = jsonResponse.getJSONObject("data").getJSONObject("_id").getString("$oid");


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(2)
    void confirmTripFail() {
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("driver", "2");
            jsonObject1.put("startTime", "123456");
            HttpResponse<String> httpResponse1 = httpRequest("POST", "/trip/confirm", jsonObject1.toString());
            assertEquals(400, httpResponse1.statusCode());



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(3)
    void UpdateTripPass() {
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("distance", 2);
            jsonObject1.put("endTime", 123458);
            jsonObject1.put("timeElapsed", 2);
            jsonObject1.put("totalCost", "120");
            HttpResponse<String> httpResponse1 = httpRequest("PATCH", "/trip/" + id, jsonObject1.toString());
            assertEquals(200, httpResponse1.statusCode());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(4)
    void UpdateTripFail() {
        try {
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("distance", 2);
            jsonObject2.put("endTime", 123458);
            jsonObject2.put("timeElapsed", 2);
            HttpResponse<String> httpResponse2 = httpRequest("PATCH", "/trip/12", jsonObject2.toString());
            assertEquals(400, httpResponse2.statusCode());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(5)
    void PassengerTripsPass() {
        try {

            HttpResponse<String> httpResponse1 = httpRequest("GET", "/trip/passenger/1", "");
            assertEquals(200, httpResponse1.statusCode());
            JSONObject jsonResponse = new JSONObject(httpResponse1.body());
            assertEquals(id, jsonResponse.getJSONObject("data").getJSONArray("trips").getJSONObject(0).getString("_id"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(6)
    void PassengerTripsFail() {


            HttpResponse<String> httpResponse1 = httpRequest("GET", "/trip/passenger/8", "");
            assertEquals(404, httpResponse1.statusCode());

            HttpResponse<String> httpResponse2 = httpRequest("GET", "/trip/passenger/", "");
            assertEquals(400, httpResponse2.statusCode());

    }

    @Test
    @Order(7)
    void DriverTripsPass() {
        try {

            HttpResponse<String> httpResponse1 = httpRequest("GET", "/trip/driver/2", "");
            assertEquals(200, httpResponse1.statusCode());
            JSONObject jsonResponse = new JSONObject(httpResponse1.body());
            assertEquals(id, jsonResponse.getJSONObject("data").getJSONArray("trips").getJSONObject(0).getString("_id"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(8)
    void DriverTripsFail() {


        HttpResponse<String> httpResponse1 = httpRequest("GET", "/trip/driver/8", "");
        assertEquals(404, httpResponse1.statusCode());

        HttpResponse<String> httpResponse2 = httpRequest("GET", "/trip/driver/", "");
        assertEquals(400, httpResponse2.statusCode());

    }

    @AfterAll
    public static void shutdownServer() {

        server.stop(0);
    }

}
