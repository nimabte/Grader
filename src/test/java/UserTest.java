import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.Assert.*;

public class UserTest {
    MongoHandler mongoHandler;
    public static HashMap<ObjectId, User> users;
    private static Event event;
    private static Competition competition_1;
    @Before
    public void setup() throws Exception {
        event = new Event(new ObjectId());
        competition_1 = new Competition(new ObjectId());
        event.addCompetition(competition_1);
        System.out.println("____________ EVENT INITIALIZING____________");
        System.out.println(event);
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.WARNING);
        mongoHandler = new MongoHandler();
        mongoHandler.mongoClientInstance();
        mongoHandler.getCollections();
        //Queue<Submission> submissions = mongoHandler.readSubmissions();
        //HashMap<ObjectId, BsonDocument> problems = mongoHandler.readProblems();
        event = new Event(new ObjectId());
        competition_1 = new Competition(new ObjectId());
        users = mongoHandler.getUsers();
    }
    @Test
    public void addCompetition() {
        users.forEach((k, v) -> {
            v.addCompetition(competition_1);
            //v.addEvent(event);
            //v.updateRegion();
            //System.out.println(v.updateRegion());
        });
    }
}