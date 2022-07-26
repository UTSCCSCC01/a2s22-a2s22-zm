package ca.utoronto.utm.mcs;

/** 
 * Everything you need in order to send and recieve httprequests to 
 * other microservices is given here. Do not use anything else to send 
 * and/or recieve http requests from other microservices. Any other 
 * imports are fine.
 */
import java.io.OutputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static ca.utoronto.utm.mcs.Request.httpRequest;

public class Drivetime extends Endpoint {

    static final String LOCATION_SERVICE = "locationmicroservice";

    /**
     * GET /trip/driverTime/:_id
     * @param _id
     * @return 200, 400, 404, 500
     * Get time taken to get from driver to passenger on the trip with
     * the given _id. Time should be obtained from navigation endpoint
     * in location microservice.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        String params[] = r.getRequestURI().toString().split("/");
        if (params.length != 4 || params[3].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }
        String tid = params[3];
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if(body.length()!=0){
            this.sendStatus(r, 400);
            return;
        }
        try{
            JSONObject new_data = this.dao.trip_drivertime(tid);
            if(!new_data.has("driver")){
                this.sendStatus(r,404);
                return;
            }
            String driveruid = new_data.getString("driver");
            String passengeruid = new_data.getString("passenger");
            HttpResponse<String> httpResponse = httpRequest("GET", LOCATION_SERVICE, "/location/navigation/" + driveruid + "?passengerUid=" + passengeruid, "");
            JSONObject content = new JSONObject(httpResponse.body());
            int time = content.getJSONObject("data").getInt("total_time");
            JSONObject data = new JSONObject();
            data.put("arrival_time", time);
            JSONObject res = new JSONObject();
            res.put("data", data);
            sendResponse(r,res,200);
        } catch (Exception e){
            e.printStackTrace();
            this.sendStatus(r,500);
        }
    }
}
