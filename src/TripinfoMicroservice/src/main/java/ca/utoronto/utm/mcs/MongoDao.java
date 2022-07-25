package ca.utoronto.utm.mcs;

import com.mongodb.client.MongoCollection;
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
	}

	// *** implement database operations here *** //

}
