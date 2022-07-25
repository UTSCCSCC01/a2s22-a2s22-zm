package ca.utoronto.utm.mcs;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

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

}
