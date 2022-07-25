package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Trip extends Endpoint {

    /**
     * PATCH /trip/:_id
     * @param _id
     * @body distance, endTime, timeElapsed, totalCost
     * @return 200, 400, 404
     * Adds extra information to the trip with the given id when the 
     * trip is done. 
     */

    @Override
    public void handlePatch(HttpExchange r) throws IOException, JSONException {
        // TODO
        String params[] = r.getRequestURI().toString().split("/");
        if (params.length != 3 || params[2].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }
        JSONObject body = new JSONObject(Utils.convert(r.getRequestBody()));
        String fields[] = {"distance", "endTime", "timeElapsed", "totalCost"};
        Class<?> fieldClasses[] = {Integer.class, Integer.class, Integer.class, String.class};
        if (!validateFields(body, fields, fieldClasses)) {
            this.sendStatus(r, 400);
            return;
        }
        if(body.length()!=4){
            this.sendStatus(r, 400);
            return;
        }

        String oid = params[2];
        int distance = body.getInt("distance");
        int endTime = body.getInt("endTime");
        int timeElapsed = body.getInt("timeElapsed");
        String totalCost = body.getString("totalCost");
        int res;
        try{
            res = this.dao.trip_update(oid, distance, endTime, timeElapsed, totalCost);
            this.sendStatus(r, res);

        } catch (Exception e){
            e.printStackTrace();
            this.sendStatus(r,500);
        }

    }
}
