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

/*
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
    void nearbyDriversPass() {
        try {
            double lat1 = 42.02;
            double lat2 = 42.01;
            double lat3 = 41.45;
            double lon1 = 127.02;
            double lon2 = 127.01;
            double lon3 = 126.10;
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

            JSONObject jsonObject16 = new JSONObject();
            jsonObject16.put("uid", "3");
            jsonObject16.put("is_driver", true);
            HttpResponse<String> httpResponse16 = httpRequest("PUT", "/location/user", jsonObject16.toString());
            assertEquals(200, httpResponse16.statusCode());

            JSONObject jsonObject3 = new JSONObject();
            jsonObject3.put("longitude", lon1);
            jsonObject3.put("latitude", lat1);
            jsonObject3.put("street", "Omni");
            HttpResponse<String> httpResponse3 = httpRequest("PATCH", "/location/1", jsonObject3.toString());

            JSONObject jsonObject17 = new JSONObject();
            jsonObject17.put("longitude", lon3);
            jsonObject17.put("latitude", lat3);
            jsonObject17.put("street", "JXR");
            HttpResponse<String> httpResponse17 = httpRequest("PATCH", "/location/3", jsonObject17.toString());


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
            jsonObject7.put("time", 6);
            HttpResponse<String> httpResponse7 = httpRequest("POST", "/location/hasRoute", jsonObject7.toString());

            JSONObject jsonObject9 = new JSONObject();
            jsonObject9.put("roadName", "Borough");
            jsonObject9.put("hasTraffic", true);
            HttpResponse<String> httpResponse9 = httpRequest("PUT", "/location/road", jsonObject9.toString());;

            JSONObject jsonObject10 = new JSONObject();
            jsonObject10.put("roadName", "Town Center");
            jsonObject10.put("hasTraffic", true);
            HttpResponse<String> httpResponse10 = httpRequest("PUT", "/location/road", jsonObject10.toString());;

            JSONObject jsonObject11 = new JSONObject();
            jsonObject11.put("roadName1", "Ellesmere");
            jsonObject11.put("roadName2", "Borough");
            jsonObject11.put("hasTraffic", false);
            jsonObject11.put("time", 2);
            HttpResponse<String> httpResponse11 = httpRequest("POST", "/location/hasRoute", jsonObject11.toString());


            JSONObject jsonObject15 = new JSONObject();
            jsonObject15.put("roadName", "Town center");
            jsonObject15.put("hasTraffic", false);
            HttpResponse<String> httpResponse15 = httpRequest("PUT", "/location/road", jsonObject15.toString());

            JSONObject jsonObject12 = new JSONObject();
            jsonObject12.put("roadName1", "Borough");
            jsonObject12.put("roadName2", "Town center");
            jsonObject12.put("hasTraffic", false);
            jsonObject12.put("time", 1);
            HttpResponse<String> httpResponse12 = httpRequest("POST", "/location/hasRoute", jsonObject12.toString());

            JSONObject jsonObject13 = new JSONObject();
            jsonObject13.put("roadName1", "Town center");
            jsonObject13.put("roadName2", "Omni");
            jsonObject13.put("hasTraffic", false);
            jsonObject13.put("time", 1);
            HttpResponse<String> httpResponse13 = httpRequest("POST", "/location/hasRoute", jsonObject13.toString());

            JSONObject jsonObject14 = new JSONObject();
            jsonObject14.put("roadName1", "Borough");
            jsonObject14.put("roadName2", "Omni");
            jsonObject14.put("hasTraffic", false);
            jsonObject14.put("time", 3);
            HttpResponse<String> httpResponse14 = httpRequest("POST", "/location/hasRoute", jsonObject14.toString());


            HttpResponse<String> httpResponse8 = httpRequest("GET", "/location/nearbyDriver/1?radius=100", "");
            assertEquals(200, httpResponse8.statusCode());
            JSONObject jsonResponse = new JSONObject(httpResponse8.body());
            assertEquals(127.01, jsonResponse.getJSONObject("data").getJSONObject("2").getDouble("longitude"));


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
            assertEquals("Ellesmere", jsonResponse.getJSONObject("data").getJSONArray("route").getJSONObject(0).getString("street"));
            assertEquals("Borough", jsonResponse.getJSONObject("data").getJSONArray("route").getJSONObject(1).getString("street"));
            assertEquals("Town center", jsonResponse.getJSONObject("data").getJSONArray("route").getJSONObject(2).getString("street"));
            assertEquals(4, jsonResponse.getJSONObject("data").getInt("total_time"));
            assertEquals("1", jsonResponse.getJSONObject("data").getJSONArray("route").getJSONObject(2).getString("time"));
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


/*
    @AfterAll
    public static void shutdownServer() {

        server.stop(0);
    }
*/

}
