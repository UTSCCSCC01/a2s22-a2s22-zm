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

/*
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
    void confirmTripPass() {
        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("driver", "2");
            jsonObject1.put("passenger", "1");
            jsonObject1.put("startTime", 123456);
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
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("passenger", "1");
            jsonObject2.put("startTime", "123456");
            HttpResponse<String> httpResponse2 = httpRequest("POST", "/trip/confirm", jsonObject2.toString());
            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("passenger", "1");
            jsonObject3.put("driver", "2");
            HttpResponse<String> httpResponse3 = httpRequest("POST", "/trip/confirm", jsonObject3.toString());

            assertEquals(400, httpResponse1.statusCode());
            assertEquals(400, httpResponse2.statusCode());
            assertEquals(400, httpResponse3.statusCode());


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
            jsonObject1.put("totalCost", "120.00");
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
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("distance", 2);
            jsonObject1.put("endTime", 123458);
            jsonObject1.put("timeElapsed", 2);
            HttpResponse<String> httpResponse1 = httpRequest("PATCH", "/trip/12?id=3", jsonObject1.toString());
            assertEquals(400, httpResponse1.statusCode());

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("distance", 2);
            jsonObject2.put("endTime", 123458);
            jsonObject2.put("timeElapsed", 2);
            HttpResponse<String> httpResponse2 = httpRequest("PATCH", "/trip/12", jsonObject2.toString());
            assertEquals(400, httpResponse2.statusCode());

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("distance", 2);
            jsonObject3.put("endTime", 123458);
            jsonObject3.put("timeElapsed", 2);
            jsonObject3.put("totalCost", "120.00");
            HttpResponse<String> httpResponse3 = httpRequest("PATCH", "/trip/100", jsonObject3.toString());
            assertEquals(404, httpResponse2.statusCode());

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
        HttpResponse<String> httpResponse3 = httpRequest("GET", "/trip/passenger/8/3", "");

        assertEquals(400, httpResponse3.statusCode());

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

    @Test
    @Order(9)
    void DriverTimePass(){
        try{
            HttpResponse<String> httpResponse1 = httpRequest("GET", "/trip/driverTime/" + id, "");
            assertEquals(200, httpResponse1.statusCode());
            JSONObject jsonResponse = new JSONObject(httpResponse1.body());
            assertEquals(4, jsonResponse.getJSONObject("data").getInt("arrival_time"));
            assertEquals(1, jsonResponse.getJSONObject("data").length());
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    @Order(10)
    void DriverTimeFail(){
        try{
            HttpResponse<String> httpResponse1 = httpRequest("GET", "/trip/driverTime/8/3", "");
            assertEquals(400, httpResponse1.statusCode());
            HttpResponse<String> httpResponse2 = httpRequest("GET", "/trip/driverTime/", "");
            assertEquals(400, httpResponse2.statusCode());
            HttpResponse<String> httpResponse3 = httpRequest("GET", "/trip/driverTime/2323", "");
            assertEquals(404, httpResponse3.statusCode());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Test
    @Order(11)
    void RequestTripPass(){
        try{
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("uid", "1");
            jsonObject1.put("radius", 150);
            HttpResponse<String> httpResponse1 = httpRequest("PATCH", "/trip/request", jsonObject1.toString());
            assertEquals(200, httpResponse1.statusCode());
            JSONObject jsonResponse = new JSONObject(httpResponse1.body());
            assertEquals("2", jsonResponse.getJSONArray("data").getString(0));
            assertEquals("3", jsonResponse.getJSONArray("data").getString(1));
            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("uid", "2");
            jsonObject2.put("radius", 150);
            HttpResponse<String> httpResponse2 = httpRequest("PATCH", "/trip/request", jsonObject2.toString());
            assertEquals(200, httpResponse2.statusCode());
            JSONObject jsonResponse2 = new JSONObject(httpResponse1.body());
            assertEquals("2", jsonResponse2.getJSONArray("data").getString(0));

            
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Test
    @Order(12)
    void RequestTripFail(){
        try{
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("uid", "100");
            jsonObject1.put("radius", 200);
            HttpResponse<String> httpResponse1 = httpRequest("PATCH", "/trip/request", jsonObject1.toString());
            assertEquals(404, httpResponse1.statusCode());

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("uid", "1");
            jsonObject1.put("radius", -1);
            HttpResponse<String> httpResponse2 = httpRequest("PATCH", "/trip/request", jsonObject2.toString());
            assertEquals(400, httpResponse2.statusCode());

            JSONObject jsonObject3 = new JSONObject();
            jsonObject2.put("uid", "1");
            HttpResponse<String> httpResponse3 = httpRequest("PATCH", "/trip/request", jsonObject3.toString());
            assertEquals(400, httpResponse3.statusCode());

            JSONObject jsonObject4 = new JSONObject();
            jsonObject1.put("radius", 200);
            HttpResponse<String> httpResponse4 = httpRequest("PATCH", "/trip/request", jsonObject4.toString());
            assertEquals(400, httpResponse4.statusCode());

        } catch(Exception e){
            e.printStackTrace();
        }
    }
/*
    @AfterAll
    public static void shutdownServer() {

        server.stop(0);
    }
*/
}
