package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import java.io.IOException;

public class Login extends Endpoint {

    /**
     * POST /user/login
     * @body email, password
     * @return 200, 400, 401, 404, 500
     * Login a user into the system if the given information matches the 
     * information of the user in the database.
     */
    
    public int user_login(JSONObject deserialized){
        String email, password;
        int user_login_res;
        try{
            if(deserialized.has("email") && deserialized.has("password")){
                //check whether the user exists
                email = deserialized.getString("email");
                password = deserialized.getString("password");
            } else{
                return 400;
            }
        } catch(Exception e){
            //can't find the string from deserailized
            e.printStackTrace();
            return 500;
        }
        try{
            user_login_res = this.dao.user_login(email, password);
        } catch (Exception e){
            e.printStackTrace();
            return 500;
        }
        return user_login_res
    }

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        // TODO
        String body = Utils.convert(r.getRequestBody());
        String path = r.getRequestURI().getPath();
        int api_response;
        try{
            JSONObject deserailized = new JSONObject(body);
            switch(path){
                //distinguish the path
                case "/api/user/login":
                    api_response = this.user_login(deserailized);
                    sendStatus(r,api_response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }



}
