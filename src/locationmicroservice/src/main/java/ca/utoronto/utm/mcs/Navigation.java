package ca.utoronto.utm.mcs;

import java.io.IOException;
import org.json.*;
import com.sun.net.httpserver.HttpExchange;
import org.neo4j.driver.Result;
import org.neo4j.driver.Record;

public class Navigation extends Endpoint {

    /**
     * GET /location/navigation/:driverUid?passengerUid=:passengerUid
     * @param driverUid, passengerUid
     * @return 200, 400, 404, 500
     * Get the shortest path from a driver to passenger weighted by the
     * travel_time attribute on the ROUTE_TO relationship.
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
        params = param[0].split(":");
        if(params.length !=2 || params[1].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }
        String driveruid = params[1];
        String[] paramet = param[1].split(":");
        if(paramet.length !=2 || paramet[1].isEmpty()) {
            this.sendStatus(r, 400);
            return;
        }
        String passengeruid = paramet[1];
        try{
            Result result = this.dao.getNavigation(driveruid,passengeruid);
            if(result.hasNext()){
                JSONObject res = new JSONObject();
                Record record;
                int time;
                int total_time=0;
                String street;
                JSONObject route = new JSONObject();
                JSONObject data = new JSONObject();
                boolean has_traffic;
                while(result.hasNext()){
                    record = result.next();
                    time = record.get("travel_time").asInt();
                    total_time = total_time + time;
                    has_traffic = record.get("has_traffic").asBoolean();
                    street = record.get("name").asString();
                    route.put("time", time);
                    route.put("has_traffic", has_traffic);
                    route.put("street", street);
                }
                data.put("route", route);
                data.put("total_time", total_time);
                res.put("data", data);
                this.sendResponse(r, res, 200);

            } else{
                this.sendStatus(r,404);
            }

        } catch (Exception e){
            e.printStackTrace();
            this.sendStatus(r, 500);
        }

    }
}
