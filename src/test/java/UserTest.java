import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ObjectInputStream;
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
    private static ObjectId eventId;
    private static ObjectId competitionId;
    @Before
    public void setup() throws Exception {
        eventId = new ObjectId();
        competitionId = new ObjectId();
        event = new Event(eventId);
        competition_1 = new Competition(competitionId);
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
            //to check to empty error and initialization of even list in the empty constructor
            v.addCompetition(competition_1);
            //v.addEvent(event);
            //v.updateRegion();
            //System.out.println(v.updateRegion());
        });
    }

    @Test
    public void dataUpdateTest() {
        User u = users.get(new ObjectId("59b55f5d6276ee9fadbd5c7e"));
        System.out.println("User grade: " + u.getGrade());
        u.setGrade(123);
        System.out.println("after Updating Grade:" + u.getGrade());
        Assert.assertNotEquals(u.toString(), users.get(new ObjectId("59b55f5d6276ee9fadbd5c7e").toString()));
        System.out.println("in the database : " + users.get(new ObjectId("59b55f5d6276ee9fadbd5c7e")).getGrade());
    }

    @Test
    public void assTaskTest() {
        HashMap<String, int[]> tasks = new HashMap<>();
        taskAdder(100, 1, tasks, "1");
        taskAdder(150, 2, tasks, "1");
        taskAdder(100, 3, tasks, "1");
        taskAdder(500, 4, tasks, "2");
    }
    private void taskAdder(int lt, int u_answer, HashMap<String, int[]> tasks, String key) {
        int[] ans = tasks.get(key);
        if(ans != null){
            //if the saved answer is newer do nothing!
            if(ans[0]>=lt)
                return;
            ans[0]=lt;
            ans[1]=u_answer;
            return;
        }
        ans = new int[2];
        ans[0]=lt;
        ans[1]=u_answer;
        tasks.put(key, ans);
    }
}