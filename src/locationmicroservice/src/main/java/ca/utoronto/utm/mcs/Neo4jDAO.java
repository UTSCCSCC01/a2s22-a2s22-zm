package ca.utoronto.utm.mcs;

import org.neo4j.driver.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.neo4j.driver.Record;

public class Neo4jDAO {

    private final Session session;
    private final Driver driver;
    private final String username = "neo4j";
    private final String password = "123456";

    public Neo4jDAO() {
        Dotenv dotenv = Dotenv.load();
        String addr = dotenv.get("NEO4J_ADDR");
        String uriDb = "bolt://" + addr + ":7687";

        this.driver = GraphDatabase.driver(uriDb, AuthTokens.basic(this.username, this.password));
        this.session = this.driver.session();
    }

    // *** implement database operations here *** //
    public Result getNavigation(String driveruid, String passengeruid){
        Result driver_loc = getUserLocationByUid(driveruid);
        if(!driver_loc.hasNext()){
            return driver_loc;
        }
        String road1 = driver_loc.next().get(2).asString();
        Result passenger_loc = getUserLocationByUid(passengeruid);
        if(!passenger_loc.hasNext()){
            return passenger_loc;
        }
        String road2 = passenger_loc.next().get(2).asString();
        System.out.println(road1);
        System.out.println(road2);
        String query = ("MATCH p=(a:road {name:'%s'})-[*]->(b:road {name:'%s'}), (WITH p, reduce(s = 0, r IN relationships(p) | s + r.travel_time) AS dist RETURN p, dist ORDER BY dist asc limit 1)");
        query = String.format(query, road1, road2);
        return this.session.run(query);
    }
    public Result addUser(String uid, boolean is_driver) {
        String query = "CREATE (n: user {uid: '%s', is_driver: %b, longitude: 0, latitude: 0, street: ''}) RETURN n";
        query = String.format(query, uid, is_driver);
        return this.session.run(query);
    }

    public Result deleteUser(String uid) {
        String query = "MATCH (n: user {uid: '%s' }) DETACH DELETE n RETURN n";
        query = String.format(query, uid);
        return this.session.run(query);
    }

    public Result getUserLocationByUid(String uid) {
        String query = "MATCH (n: user {uid: '%s' }) RETURN n.longitude, n.latitude, n.street";
        query = String.format(query, uid);
        return this.session.run(query);
    }

    public Result getUserByUid(String uid) {
        String query = "MATCH (n: user {uid: '%s' }) RETURN n";
        query = String.format(query, uid);
        return this.session.run(query);
    }

    public Result updateUserIsDriver(String uid, boolean isDriver) {
        String query = "MATCH (n:user {uid: '%s'}) SET n.is_driver = %b RETURN n";
        query = String.format(query, uid, isDriver);
        return this.session.run(query);
    }

    public Result updateUserLocation(String uid, double longitude, double latitude, String street) {
        String query = "MATCH(n: user {uid: '%s'}) SET n.longitude = %f, n.latitude = %f, n.street = \"%s\" RETURN n";
        query = String.format(query, uid, longitude, latitude, street);
        return this.session.run(query);
    }

    public Result getRoad(String roadName) {
        String query = "MATCH (n :road) where n.name='%s' RETURN n";
        query = String.format(query, roadName);
        return this.session.run(query);
    }

    public Result createRoad(String roadName, boolean has_traffic) {
        String query = "CREATE (n: road {name: '%s', has_traffic: %b}) RETURN n";
        query = String.format(query, roadName, has_traffic);
        return this.session.run(query);
    }

    public Result updateRoad(String roadName, boolean has_traffic) {
        String query = "MATCH (n:road {name: '%s'}) SET n.has_traffic = %b RETURN n";
        query = String.format(query, roadName, has_traffic);
        return this.session.run(query);
    }

    public Result createRoute(String roadname1, String roadname2, int travel_time, boolean has_traffic) {
        String query = "MATCH (r1:road {name: '%s'}), (r2:road {name: '%s'}) CREATE (r1) -[r:ROUTE_TO {travel_time: %d, has_traffic: %b}]->(r2) RETURN type(r)";
        query = String.format(query, roadname1, roadname2, travel_time, has_traffic);
        return this.session.run(query);
    }

    public Result deleteRoute(String roadname1, String roadname2) {
        String query = "MATCH (r1:road {name: '%s'})-[r:ROUTE_TO]->(r2:road {name: '%s'}) DELETE r RETURN COUNT(r) AS numDeletedRoutes";
        query = String.format(query, roadname1, roadname2);
        return this.session.run(query);
    }

    public Result findRoutetime(String roadname1, String roadname2){
        String query = "MATCH (r1:road {name: '%s'}), (r2:road {name: '%s'}) MATCH (r1) -[r:ROUTE_TO]->(r2) RETURN r.travel_time";
        query = String.format(query, roadname1, roadname2);
        return this.session.run(query);
    }

    public Result getNearby(String uid, int radius){
        Result loc = getUserLocationByUid(uid);
        if(loc.hasNext()){
            Record record = loc.next();
            Double latitude = record.get(1).asDouble();
            Double longitude = record.get(0).asDouble();
            String query = "WITH %f AS lat, %f AS lon MATCH(b: user) WHERE 2*6371.393*asin(sqrt(haversin(radians(lat-b.latitude))+ cos(radians(lat))*cos(radians(b.latitude))*haversin(radians(lon-b.longitude)))) < %d RETURN b";
            query = String.format(query, latitude, longitude, radius);
            return this.session.run(query);
        } else{
            return loc;
        }
    }

    public int clearDatabase() {

        this.session.run("MATCH (n) DETACH DELETE n");
        return 200;
    }
} 