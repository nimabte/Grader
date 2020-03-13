import org.bson.BsonDocument;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) throws Exception{
        System.out.println("Hello\nthis is nima");
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.WARNING);
        MongoHandler mongoHandler = new MongoHandler();
        mongoHandler.mongoClientInstance();
        //mongoHandler.documentInstance();
        mongoHandler.getCollections();
        Queue<Submission> submissions = mongoHandler.readSubmissions();
        HashMap<ObjectId, BsonDocument> problems = mongoHandler.readProblems();
        System.out.println(problems.size());
        System.out.println(problems.get(new ObjectId("5248895be4b04211cc6d798b")));
        System.out.println(submissions.size());

//        for(int i = 0; i<submissions.size();i++){
//            System.out.println(submissions.get(i));
//        }


        System.out.println("press enter! \n ");
//        System.in.read();
    }

}
