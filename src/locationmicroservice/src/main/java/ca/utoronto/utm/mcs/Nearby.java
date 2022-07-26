package ca.utoronto.utm.mcs;

import java.io.IOException;
import org.json.*;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;

public class Nearby extends Endpoint {
    
    /**
     * GET /location/nearbyDriver/:uid?radius=:radius
     * @param uid, radius
     * @return 200, 400, 404, 500
     * Get drivers that are within a certain radius around a user.
     */

    @Override
    public void handleGet(HttpExchange r) throws IOException, JSONException {
        // TODO
        String[] params = r.getRequestURI().toString().split("/");
        if (params.length != 4 || params[3].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }
        String[] param = params[3].split("\\?");
        if(param.length != 2 || param[1].isEmpty()){
            this.sendStatus(r, 400);
            return;
        }
        String uid = param[0];
        String[] paramet = param[1].split("=");
        if(paramet.length !=2 || paramet[1].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }
        String rad = paramet[1];
        int radius;
        try{
            radius = Integer.parseInt(rad);
        } catch (NumberFormatException e){
            this.sendStatus(r, 400);
            return;
        }
        try{
            Result result = this.dao.getNearby(uid,radius);
            if(result.hasNext()){
                JSONObject res = new JSONObject();
                Record record;
                Double longitude;
                Double latitude;
                String street;
                String driverUID;
                JSONObject data = new JSONObject();
                JSONObject driverID = new JSONObject();
                while(result.hasNext()){
                    record = result.next();
                    longitude = record.get(0).get("longitude").asDouble();
                    latitude = record.get(0).get("latitude").asDouble();
                    street = record.get(0).get("street").asString();
                    driverUID = record.get(0).get("uid").asString();
                    if(record.get(0).get("is_driver").asBoolean()){

                    }
                    driverID.put("longitude",longitude);
                    driverID.put("latitude", latitude);
                    driverID.put("street", street);
                    data.put(driverUID, driverID);
                }
                res.put("data", data);
                this.sendResponse(r,res,200);
            } else{
                this.sendStatus(r,404);
            }
        } catch (Exception e){
            e.printStackTrace();
            this.sendStatus(r, 500);
        }
    }

    @Override
    public void handlePost(HttpExchange r) {
        try {
            this.dao.clearDatabase();
            r.sendResponseHeaders(200, -1);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
