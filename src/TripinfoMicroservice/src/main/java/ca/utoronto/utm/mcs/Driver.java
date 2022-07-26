package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Driver extends Endpoint {

    /**
     * GET /trip/driver/:uid
     * @param uid
     * @return 200, 400, 404
     * Get all trips driver with the given uid has.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
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
            new_data = this.dao.trip_driver(uid);
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
