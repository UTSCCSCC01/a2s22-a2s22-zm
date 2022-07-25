package ca.utoronto.utm.mcs;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

import com.mongodb.client.MongoDatabase;

import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;

public class MongoDao {
	
	public MongoCollection<Document> collection;

	public MongoDao() {
        // TODO: 
        // Connect to the mongodb database and create the database and collection.
		Dotenv dotenv = Dotenv.load();
		String addr = dotenv.get("MONGODB_ADDR");
		String uriDb = "bolt://" + addr + ":27017";
        // Use Dotenv like in the DAOs of the other microservices.
		Dotenv dotenv = Dotenv.load();
		String addr = dotenv.get("MONGODB_ADDR");
		MongoClient mongoClient = new MongoClient("mongodb://");
		MongoDatabase mongoDatabase = mongoClient.getDatabase("trip");
		this.collection = mongoDatabase.getCollection("trips");
	}

	// *** implement database operations here *** //

	public void trip_confirm(String driverID, String passengerID, int startTime) {
		Document document = new Document();
		document.append("driver", driverID);
		document.append("passenger", passengerID);
		document.append("startTime", startTime);
		collection.insertOne(document);
	}

}
