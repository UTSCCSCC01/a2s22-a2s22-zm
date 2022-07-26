package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Passenger extends Endpoint {

    /**
     * GET /trip/passenger/:uid
     * @param uid
     * @return 200, 400, 404
     * Get all trips the passenger with the given uid has.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException,JSONException{
        String params[] = r.getRequestURI().toString().split("/");
        if (params.length != 4 || params[3].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }
        String uid = params[3];
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        if(body.length()!=0){
            this.sendStatus(r, 400);
            return;
        }
        try {
            JSONObject new_data;
            JSONObject res = new JSONObject();
            new_data = this.dao.trip_passenger(uid);
            if(new_data.has("trips")){
                res.put("data", new_data);
                this.sendResponse(r,res,200);
            }else{
                this.sendStatus(r,404);
            }
        } catch (Exception e){
            e.printStackTrace();
            this.sendStatus(r,500);
        }
    }
}
