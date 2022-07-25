package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import java.io.IOException;

public class Register extends Endpoint {

    /**
     * POST /user/register
     * @body name, email, password
     * @return 200, 400, 500
     * Register a user into the system using the given information.
     */

    //register a user
    public void user_register(HttpExchange r, JSONObject deserialized){
        String name, email, password;
        int user_register_res;
        String uid;
        try{
            if(deserialized.has("name") && && deserialized.has("email") && deserialized.has("password")){
                //check whether it is a bad request
                name = deserialized.getString("name");
                email = deserialized.getString("email");
                password = deserialized.getString("password");
            } else{
                this.sendStatus(r,400);
                return;
            }
        } catch (Exception e){
            e.printStackTrace();
            this.sendStatus(r,500);
            return;
        }
        try{
            //get the JSONObject from PostgresDAO.java
            JSONObject new_data = this.dao.user_register(name, email, password);
            try{
                if(new_data.has("code")){
                    user_register_res = new_data.getInt("code");
                }
                if(new_data.has("uid")){
                    uid = new_data.getString("uid");
                }
                JSONobject jsonObject = new JSONObject();
                if(user_register_res == 200){
                    jsonObject.put("uid", uid);
                    this.sendResponse(r, jsonObject, 200);
                    return;
                }
                else{
                    this.sendStatus(r, user_register_res);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
            this.sendStatus(r,500);
            return;
        }
    }

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        // TODO
        String body = Utils.conver(r.getRequestBody());
        String path = r.getRequestURI().getPath();
        try{
            JSONObject deserailized = new JSONObject(body);
            switch (path){
                //distinguish the path
                case "/api/user/register":
                    this.user_register(r,deserailized);
                    break;
            }
        } catch (Exception e){
            e.printStackTrace();
            sendStatus(r, 500);
        }
    }
}
