package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Confirm extends Endpoint {

    /**
     * POST /trip/confirm
     * @body driver, passenger, startTime
     * @return 200, 400
     * Adds trip info into the database after trip has been requested.
     */

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        // TODO
        String body = Utils.convert(r.getRequestBody());
        String path = r.getRequestURI().getPath();
        try{
            JSONObject deserialized = new JSONObject(body);
            switch (path){
                //distinguish the path
                case "/trip/confirm":
                    this.trip_confrim(r, deserialized);
                    break;
            }
        } catch (Exception e){
            e.printStackTrace();
            this.sendStatus(r,500);
        }

    }

    public void trip_confrim(HttpExchange r, JSONObject deserialized) throws IOException, JSONException{
        String driveruid, passengeruid;
        int starttime;
        try{
            if(deserialized.has("driver") && deserialized.has("passenger") && deserialized.has("startTime")){
                driveruid = deserialized.getString("driver");
                passengeruid = deserialized.getString("passenger");
                starttime = deserialized.getInt("starTime");
                try{
                    JSONObject new_data = this.dao.trip_confirm(driveruid, passengeruid, starttime);
                    JSONObject t_id = new JSONObject();
                    t_id.put("_id", new_data);
                    JSONObject res = new JSONObject();
                    res.put("data", t_id);
                    this.sendResponse(r,res,200);
                } catch (Exception e){
                    e.printStackTrace();
                    this.sendStatus(r,500);
                }
            }
        } catch(Exception e){
            e.printStackTrace();
            this.sendStatus(r,500);
        }

    }
}
