
import org.bson.BsonDocument;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MongoHandlerTest {

    @Before
    public void setup() throws Exception {
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.WARNING);
        MongoHandler mongoHandler = new MongoHandler();
        mongoHandler.mongoClientInstance();
        mongoHandler.getCollections();
        Queue<Submission> submissions = mongoHandler.readSubmissions();
        HashMap<ObjectId, BsonDocument> problems = mongoHandler.readProblems();
        HashMap<ObjectId, User> users = mongoHandler.getUsers();
    }
    @Test
    public void testFunction() {
        int expected = 0;
        int actual = MongoHandler.testFunction();
        Assert.assertEquals(expected,actual);
    }
}