package ca.utoronto.utm.mcs;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import com.mongodb.client.model.Projections;
import com.mongodb.util.JSON;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.print.Doc;

public class MongoDao {
	
	public MongoCollection<Document> collection;

	public MongoDao() {
        // TODO: 
        // Connect to the mongodb database and create the database and collection.
        // Use Dotenv like in the DAOs of the other microservices.
		Dotenv dotenv = Dotenv.load();
		String addr = dotenv.get("MONGODB_ADDR");
		MongoClient mongoClient = new MongoClient("mongodb://root:123456@" + addr + ":27017");
		MongoDatabase mongoDatabase = mongoClient.getDatabase("trip");
		this.collection = mongoDatabase.getCollection("trips");
	}

	// *** implement database operations here *** //

	public JSONObject trip_confirm(String driverID, String passengerID, int startTime) {
		try {
			Document document = new Document();
			ObjectId objectId = new ObjectId();
			document.append("_id", objectId);
			document.append("driver", driverID);
			document.append("passenger", passengerID);
			document.append("startTime", startTime);
			collection.insertOne(document);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("$oid", objectId.toString());
			return jsonObject;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int trip_update(String oid, int distance, int endTime, int timeElapsed, String totalCost) {
		Document query = new Document("_id", oid);
		MongoCursor<Document> mongoCursor = collection.find(query).iterator();
		if(mongoCursor.hasNext()){
			mongoCursor.next().append("distance", distance).append("totalCost", totalCost).append("endTime", endTime).append("timeElapsed", timeElapsed);
			return 200;
		}
		return 404;
	}

	public JSONObject trip_passenger(String uid) {
		try {
			JSONArray jsonArray = new JSONArray();
			Document query = new Document("passenger", uid);
			Bson projection = Projections.fields(Projections.exclude("passenger"));
			MongoCursor<Document> mongoCursor = collection.find(query).projection(projection).iterator();
			if(!mongoCursor.hasNext()) {
				return new JSONObject();
			}
			while(mongoCursor.hasNext()){
				jsonArray.put(new JSONObject(mongoCursor.next().toJson()));
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("trips", jsonArray);
			return jsonObject;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}

	public JSONObject trip_driver(String uid) {
		try {
			JSONArray jsonArray = new JSONArray();
			Document query = new Document("driver", uid);
			Bson projection = Projections.fields(Projections.exclude("driver"));
			MongoCursor<Document> mongoCursor = collection.find(query).projection(projection).iterator();
			if(!mongoCursor.hasNext()) {
				return new JSONObject();
			}
			while(mongoCursor.hasNext()){
				jsonArray.put(new JSONObject(mongoCursor.next().toJson()));
			}
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("trips", jsonArray);
			return jsonObject;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;

	}

	public JSONObject trip_drivertime(String oid) {
		try {
			Document query = new Document("_id", oid);
			Bson projection = Projections.fields(Projections.include("driver", "passenger"));
			MongoCursor<Document> mongoCursor = collection.find(query).projection(projection).iterator();
			if(!mongoCursor.hasNext()) {
				return new JSONObject();
			}
			return new JSONObject(mongoCursor.next().toJson());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}



	

}
