package ca.utoronto.utm.mcs;

import java.io.IOException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.json.JSONException;
import org.json.JSONObject;


/** 
 * Everything you need in order to send and recieve httprequests to 
 * the microservices is given here. Do not use anything else to send 
 * and/or recieve http requests from other microservices. Any other 
 * imports are fine.
 */
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.io.OutputStream;    // Also given to you to send back your response

public class RequestRouter implements HttpHandler {
	static final int PORT = 8000;

	static final String LOCATION_SERVICE = "locationmicroservice";
	static final String TRIP_SERVICE = "tripinfomicroservice";
	static final String USER_SERVICE = "usermicroservice";

	HttpResponse<String> httpResponse;

    /**
     * You may add and/or initialize attributes here if you 
     * need.
     */
	public RequestRouter() {

	}

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
	public void handle(HttpExchange r) throws IOException {
		System.out.println("Handle invoked");
        // TODO
		JSONObject JSON_Failed = new JSONObject();
		try {
			JSON_Failed.put("status", "INTERNAL SERVER ERROR");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		String path = r.getRequestURI().getPath();
		String tar = path.split("/")[1];
		String method = r.getRequestMethod();
		switch (tar) {
			case "location":
				httpResponse = httpRequest(method, LOCATION_SERVICE, path, Utils.convert(r.getRequestBody()));
				break;
			case "trip":
				httpResponse = httpRequest(method, TRIP_SERVICE, path, Utils.convert(r.getRequestBody()));
				break;
			case "user":
				System.out.println("Case correct");
				System.out.println(method);
				httpResponse = httpRequest(method, USER_SERVICE, path, Utils.convert(r.getRequestBody()));
				break;
		}
		if (httpResponse.statusCode() == 500) {
			r.sendResponseHeaders(500, JSON_Failed.toString().length());
			OutputStream os= r.getResponseBody();
			os.write(JSON_Failed.toString().getBytes());
			os.close();
			return;
		}
		r.sendResponseHeaders(httpResponse.statusCode(), httpResponse.body().length());
		OutputStream os= r.getResponseBody();
		os.write(httpResponse.body().getBytes());
		os.close();
	}
}
