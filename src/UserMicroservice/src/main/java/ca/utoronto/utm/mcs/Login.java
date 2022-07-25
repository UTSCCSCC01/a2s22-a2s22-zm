package ca.utoronto.utm.mcs;

import com.sun.net.httpserver.HttpExchange;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Login extends Endpoint {

    /**
     * POST /user/login
     * @body email, password
     * @return 200, 400, 401, 404, 500
     * Login a user into the system if the given information matches the 
     * information of the user in the database.
     */
    
    public void user_login(HttpExchange r, JSONObject deserialized) throws JSONException, IOException {
        String email, password;
        int user_login_res = 0;
        String uid = "";
        try{
            if(deserialized.has("email") && deserialized.has("password")){
                //check whether the user exists
                email = deserialized.getString("email");
                password = deserialized.getString("password");
            } else{
                this.sendStatus(r, 400);
                return;
            }
        } catch(Exception e){
            //can't find the string from deserailized
            e.printStackTrace();
            this.sendStatus(r, 500);
            return;
        }
        try{
            JSONObject new_data = this.dao.user_login(email, password);
            if(new_data.has("code")){
                user_login_res = new_data.getInt("code");
            }
            if(new_data.has("uid")){
                uid = new_data.getString(uid);
            }
            JSONObject jsonObject = new JSONObject();
            if(user_login_res == 200){
                jsonObject.put("uid", uid);
                this.sendResponse(r,jsonObject, 200);
            }
            else{
                this.sendStatus(r,user_login_res);
            }
        } catch (Exception e){
            e.printStackTrace();
            this.sendStatus(r, 500);
            return;
        }
        return;
    }

    @Override
    public void handlePost(HttpExchange r) throws IOException, JSONException {
        // TODO
        String body = Utils.convert(r.getRequestBody());
        String path = r.getRequestURI().getPath();
        try{
            JSONObject deserialized = new JSONObject(body);
            switch(path){
                //distinguish the path
                case "/api/user/login":
                    this.user_login(r, deserialized);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.sendStatus(r, 500);
        }   
    }



}
