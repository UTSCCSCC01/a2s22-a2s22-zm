package ca.utoronto.utm.mcs;

/** 
 * Everything you need in order to send and recieve httprequests to 
 * other microservices is given here. Do not use anything else to send 
 * and/or recieve http requests from other microservices. Any other 
 * imports are fine.
 */
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Request extends Endpoint {
    static int PORT = 8000;
    static final String LOCATION_SERVICE = "locationmicroservice";

    /**
     * POST /trip/request
     * @body uid, radius
     * @return 200, 400, 404, 500
     * Returns a list of drivers within the specified radius 
     * using location microservice. List should be obtained
     * from navigation endpoint in location microservice
     */

    public static HttpResponse httpRequest(String method, String hostname, String endpoint, String body) {
        try {
            URI uri = new URI("http://" + hostname + ":" + PORT + endpoint);
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

    @Override
    public void handlePost(HttpExchange r) throws IOException,JSONException{
        JSONObject jsonObject = new JSONObject();
        if(!r.getRequestMethod().equals("POST")){
            jsonObject.put("status", "BAD REQUEST");
            r.sendResponseHeaders(400, jsonObject.toString().length());
            writeOutputStream(r, jsonObject.toString());
            return;
        }

        jsonObject = new JSONObject(Utils.convert(r.getRequestBody()));
        String fields[] = {"uid", "radius"};
        Class<?> fieldClasses[] = {String.class, Integer.class};
        if (!validateFields(jsonObject, fields, fieldClasses)) {
            this.sendStatus(r, 400);
            return;
        }
        if(jsonObject.getInt("radius")< 0){
            this.sendStatus(r, 400);
            return;
        }

        HttpResponse<String> httpResponse = httpRequest("GET", LOCATION_SERVICE, "/location/nearbyDriver/" + jsonObject.get("uid") + "?radius=" + jsonObject.getString("radius"), "");
        r.sendResponseHeaders(httpResponse.statusCode(), httpResponse.body().length());
        OutputStream os= r.getResponseBody();
        os.write(httpResponse.body().getBytes());
        os.close();
    }

    @Override
    public void handleDelete(HttpExchange r) throws IOException {
        this.dao.clearDatabase();
        r.sendResponseHeaders(200, -1);
    }
}
