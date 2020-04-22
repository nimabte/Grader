
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MongoHandlerTest {
    MongoHandler mongoHandler;
    @Before
    public void setup() throws Exception {
        System.out.println("____________ INITIALIZING____________");
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.WARNING);
        mongoHandler = new MongoHandler();
        mongoHandler.mongoClientInstance();
        mongoHandler.getCollections();
        //Queue<Submission> submissions = mongoHandler.readSubmissions();
        //HashMap<ObjectId, BsonDocument> problems = mongoHandler.readProblems();

    }

    @Test
    public void UserTest() {
        HashMap<ObjectId, User> users = mongoHandler.getUsers();
        users.forEach((k, v) -> {
            System.out.println(v);
        });
    }

    @Test
    public void testFunction() {
        int expected = 0;
        int actual = MongoHandler.testFunction();
        Assert.assertEquals(expected,actual);
    }
}