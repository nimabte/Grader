import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final String BEBRAS = "bebras";
    private static final String BEBRAS_DYN = "bebras-dyn";
    //private static ResourceBundle myBundle = ResourceBundle.getBundle("grader");

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
        System.out.println(problems.size()); //ok
        //System.out.println(problems.get(new ObjectId("5248895be4b04211cc6d798b")));
        System.out.println(submissions.size()); //ok

//        for(int i = 0; i<submissions.size();i++){
//            System.out.println(submissions.get(i));
//        }

        checkSubmissions(problems, submissions);
        System.out.println("press enter! \n ");
//        System.in.read();
    }

    private static boolean checkSubmissions(HashMap<ObjectId, BsonDocument> problems,  Queue<Submission> submissions ){
        // we have 2 types: "bebras" & "bebras-dyn"
        //TODO: add more types
        String type;
        BsonDocument p;
        //int right;

        ObjectId p_id;
        ObjectId u_id;
        BsonDocument a;
        int lt;
        for(Submission s : submissions){
            p_id = s.getPid();
            u_id = s.getU();
            lt = s.getLt();
            a = s.getA();
            p = problems.get(p_id);
            if (p == null){
                //TODO: try to catch the problem into the HashMap of problems from the db
                System.err.println("Error: the problem did not found in the HashMap");
            }else{
                type = p.getString("type").getValue();
                switch(type){
                    case BEBRAS:
                        // tru answer of the task
                        int right = p.getInt32("right").getValue();
                        // user's answer
                        if(a.isInt32(a)){
                            //int uAns = a.getInt32("a", new BsonInt32(-1)).getValue();
                            int uAns = a.getInt32("a").getValue();
                            if(uAns != -1) {
                                //TODO to check the answers

                            }
                        }
                        break;
                    case BEBRAS_DYN:

                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + type);
                }
            }

        }
        return true;
    }

    private static boolean checkSubmission(HashMap<ObjectId, BsonDocument> problems,  Submission submission ){

        return true;
    }

}
