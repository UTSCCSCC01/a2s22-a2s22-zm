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
    @BeforeAll
    public static void startServer() throws IOException {

        server = HttpServer.create(new InetSocketAddress("0.0.0.0", PORT), 0);
        server.createContext("/location/user", new User());
        server.createContext("/location/", new Location());
        server.createContext("/location/road", new Road());
        server.createContext("/location/hasRoute", new Route());
        server.createContext("/location/route", new Route());
        server.createContext("/location/navigation/", new Navigation());
        server.createContext("/location/nearbyDriver/", new Nearby());
        server.createContext("/location/clear", new Nearby());

        // TODO: Add server contexts here. Do not set executors for the server, you shouldn't need them.

        server.start();
        System.out.printf("Server started on port %d...\n", PORT);

        httpRequest("POST", "/location/clear", "");
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
    void nearbyDriversPass() {
        try {
            double lon1 = 42.02;
            double lon2 = 42.01;
            double lat1 = 127.02;
            double lat2 = 127.01;
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("uid", "1");
            jsonObject1.put("is_driver", false);
            HttpResponse<String> httpResponse1 = httpRequest("PUT", "/location/user", jsonObject1.toString());
            assertEquals(200, httpResponse1.statusCode());

            JSONObject jsonObject2 = new JSONObject();
            jsonObject2.put("uid", "2");
            jsonObject2.put("is_driver", true);
            HttpResponse<String> httpResponse2 = httpRequest("PUT", "/location/user", jsonObject2.toString());
            assertEquals(200, httpResponse2.statusCode());

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("longitude", lon1);
            jsonObject3.put("latitude", lat1);
            jsonObject3.put("street", "Omni");
            HttpResponse<String> httpResponse3 = httpRequest("PATCH", "/location/1", jsonObject3.toString());


            JSONObject jsonObject4 = new JSONObject();
            jsonObject4.put("longitude", lon2);
            jsonObject4.put("latitude", lat2);
            jsonObject4.put("street", "Ellesmere");
            HttpResponse<String> httpResponse4 = httpRequest("PATCH", "/location/2", jsonObject4.toString());


            JSONObject jsonObject5 = new JSONObject();
            jsonObject5.put("roadName", "Omni");
            jsonObject5.put("hasTraffic", false);
            HttpResponse<String> httpResponse5 = httpRequest("PUT", "/location/road", jsonObject5.toString());


            JSONObject jsonObject6 = new JSONObject();
            jsonObject6.put("roadName", "Ellesmere");
            jsonObject6.put("hasTraffic", false);
            HttpResponse<String> httpResponse6 = httpRequest("PUT", "/location/road", jsonObject6.toString());


            JSONObject jsonObject7 = new JSONObject();
            jsonObject7.put("roadName1", "Ellesmere");
            jsonObject7.put("roadName2", "Omni");
            jsonObject7.put("hasTraffic", false);
            jsonObject7.put("time", 5);
            HttpResponse<String> httpResponse7 = httpRequest("POST", "/location/hasRoute", jsonObject7.toString());


            HttpResponse<String> httpResponse8 = httpRequest("GET", "/location/nearbyDriver/1?radius=100", "");
            assertEquals(200, httpResponse8.statusCode());
            JSONObject jsonResponse = new JSONObject(httpResponse8.body());
            assertEquals(42.01, jsonResponse.getJSONObject("data").getJSONObject("2").getDouble("longitude"));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    @Order(2)
    void nearbyDriversFail() {

        HttpResponse<String> httpResponse1 = httpRequest("GET", "/location/nearbyDriver/8?radius=100", "");
        assertEquals(404, httpResponse1.statusCode());

        HttpResponse<String> httpResponse2 = httpRequest("GET", "/location/nearbyDriver/?", "");
        assertEquals(400, httpResponse2.statusCode());

    }

    @Test
    @Order(3)
    void getNavigationPass() {
        try {
            HttpResponse<String> httpResponse1 = httpRequest("GET", "/location/navigation/2?passengerUid=1", "");
            assertEquals(200, httpResponse1.statusCode());
            JSONObject jsonResponse = new JSONObject(httpResponse1.body());
            assertEquals("Omni", jsonResponse.getJSONObject("data").getJSONArray("route").getJSONObject(0).getString("street"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Test
    @Order(4)
    void getNavigationFail() {
        HttpResponse<String> httpResponse1 = httpRequest("GET", "/location/navigation/8?passengerUid=1", "");
        assertEquals(404, httpResponse1.statusCode());

        HttpResponse<String> httpResponse2 = httpRequest("GET", "/location/navigation/?", "");
        assertEquals(400, httpResponse2.statusCode());
    }



    @AfterAll
    public static void shutdownServer() {

        server.stop(0);
    }


}
