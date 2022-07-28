package ca.utoronto.utm.mcs;

import java.sql.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONException;
import org.json.JSONObject;

public class PostgresDAO {
	
	public Connection conn;
    public Statement st;

	public PostgresDAO() {
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("POSTGRES_ADDR");
        String url = "jdbc:postgresql://" + addr + ":5432/root";
		try {
            Class.forName("org.postgresql.Driver");
			this.conn = DriverManager.getConnection(url, "root", "123456");
            this.st = this.conn.createStatement();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// *** implement database operations here *** //
    //check the user's email and password
    public JSONObject user_login(String email, String password) throws SQLException, JSONException {
        int code;
        String uid = "";
        String query;
        String old_password = "";
        JSONObject jsonObject = new JSONObject();
        int count = 0;
        if (email != null) {
            query = "SELECT * FROM users WHERE email = '%s'";
            query = String.format(query, email);
            ResultSet rs = this.st.executeQuery(query);
            while(rs.next()){
                count++;
                if(count == 1){
                    old_password = rs.getString("password");
                } else if (count > 1) {
                    code = 500;
                    break;
                }
            }
            if(count == 1){
                if(old_password.equals(password)){
                    code = 200;
                    query = "SELECT * FROM users WHERE email = '%s'";
                    query = String.format(query, email);
                    rs = this.st.executeQuery(query);
                    if(rs.next()){
                        uid = rs.getString("uid");
                    }

                }
                else{
                    code = 401;
                }
            }
            else{
                code = 404;
            }
        }
        else{
            code = 400;
        }
        if(code == 200){
            jsonObject.put("code", code);
            jsonObject.put("uid", uid);
        }
        else{
            jsonObject.put("code", code);
        }
        return jsonObject;
    }
    // check whether the user is able to register
    public JSONObject user_register(String name, String email, String password) throws SQLException, JSONException {
        int code = 500;
        String uid = "";
        JSONObject jsonObject = new JSONObject();
        String query;
        if(email!=null){
            query = "SELECT * FROM users WHERE email = '%s'";
            query = String.format(query, email);
            ResultSet rs = this.st.executeQuery(query);
            if(rs.next()){
                code = 409;
            }
            else{
                //insert the new user into table
                query = "INSERT INTO users (prefer_name, email, password, rides, isDriver) VALUES ('%s', '%s', '%s', 0, false)";
                query = String.format(query, name, email, password);
                this.st.execute(query);
                //find the new user's uid
                query = "SELECT * FROM users WHERE email = '%s'";
                query = String.format(query, email);
                rs = this.st.executeQuery(query);
                if(rs.next()){
                    uid = rs.getString("uid");
                    code = 200;
                }
            }
        }
        else {
            code = 400;
        }
        //send the JSON package
        if(code == 200){
            jsonObject.put("code", code);
            jsonObject.put("uid", uid);
        }
        else{
            jsonObject.put("code", code);
        }
        return jsonObject;

    }


    public ResultSet getUsersFromUid(int uid) throws SQLException {
        String query = "SELECT * FROM users WHERE uid = %d";
        query = String.format(query, uid);
        return this.st.executeQuery(query);
    }

    public ResultSet getUserData(int uid) throws SQLException {
        String query = "SELECT prefer_name as name, email, rides, isdriver FROM users WHERE uid = %d";
        query = String.format(query, uid);
        return this.st.executeQuery(query);
    }

    public void updateUserAttributes(int uid, String email, String password, String prefer_name, Integer rides, Boolean isDriver) throws SQLException {

        String query;
        if (email != null) {
            query = "UPDATE users SET email = '%s' WHERE uid = %d";
            query = String.format(query, email, uid);
            this.st.execute(query);
        }
        if (password != null) {
            query = "UPDATE users SET password = '%s' WHERE uid = %d";
            query = String.format(query, password, uid);
            this.st.execute(query);
        }
        if (prefer_name != null) {
            query = "UPDATE users SET prefer_name = '%s' WHERE uid = %d";
            query = String.format(query, prefer_name, uid);
            this.st.execute(query);
        }
        if ((rides != null)) {
            query = "UPDATE users SET rides = %d WHERE uid = %d";
            query = String.format(query, rides, uid);
            this.st.execute(query);
        }
        if (isDriver != null) {
            query = "UPDATE users SET isdriver = %s WHERE uid = %d";
            query = String.format(query, isDriver.toString(), uid);
            this.st.execute(query);
        }
    }

    public void clearDatabase() throws SQLException {
        String query = "DELETE FROM users";
        this.st.execute(query);
    }
}
