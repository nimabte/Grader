import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final boolean DEBUG = true;
    private static final String BEBRAS = "bebras";
    private static final String BEBRAS_DYN = "bebras-dyn";
    private static final int CORRECT_ANSWER = 1;
    private static final int WRONG_ANSWER = -1;
    private static final int NO_ANSWER = 0;
    //private static ResourceBundle myBundle = ResourceBundle.getBundle("grader");
    public static HashMap<ObjectId, User> users;
    private static Event event;
    private static Competition competition_1;

    public static void main(String[] args) throws Exception {
        event = new Event(new ObjectId());
        competition_1 = new Competition(new ObjectId());
        event.addCompetition(competition_1);
        System.out.println("____________ EVENT INITIALIZING____________");
        System.out.println(event);
        //................................
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.WARNING);
        MongoHandler mongoHandler = new MongoHandler();
        mongoHandler.mongoClientInstance();
        mongoHandler.getCollections();
        //................................
        Queue<Submission> submissions = mongoHandler.readSubmissions();
        HashMap<ObjectId, BsonDocument> problems = mongoHandler.readProblems();
        //System.out.println(problems.size()); //ok
        //System.out.println(problems.get(new ObjectId("5248895be4b04211cc6d798b")));
        //System.out.println(submissions.size()); //ok
        //for(int i = 0; i<submissions.size();i++){
            //System.out.println(submissions.get(i));
        //}
        users = mongoHandler.getUsers();
        users.forEach((k, v) -> {
            v.addEvent(event);
            v.updateRegion();
            //System.out.println(v.updateRegion());
        });
         checkSubmissions(problems, submissions);
    }

    private static boolean checkSubmissions(HashMap<ObjectId, BsonDocument> problems, Queue<Submission> submissions) {
        // we have 2 types: "bebras" & "bebras-dyn"
        //TODO: add more types
        String type;
        BsonDocument p;
        ObjectId p_id;
        ObjectId u_id;
        BsonDocument a;
        int lt;
        for (Submission s : submissions) {
            p_id = s.getPid();
            u_id = s.getU();
            lt = s.getLt();
            a = s.getA();
            p = problems.get(p_id); // get problem in the format of BsonDocument
            if (p == null) {
                //TODO: try to catch the problem into the HashMap of problems from the db
                System.err.println("Error: the problem did not found in the HashMap");
            } else {
                type = p.getString("type").getValue();
                switch (type) {
                    case BEBRAS:
                        //System.out.println("BEBRAS:\n");
                        // tru answer of the task
                        int right;
                        try {
                            right = p.getInt32("right").getValue();
                        } catch (Exception e) {
                            e.printStackTrace();
                            break;
                        }
                        // user's answer
                        if (a.isInt32("a")) { // check if an integer answer with the key 'a' exist.
                            //int uAns = a.getInt32("a", new BsonInt32(-1)).getValue();
                            int uAns = a.getInt32("a").getValue();
                            if (uAns != -1) {
                                //possible results of checking the task: -1 ,0, 1
                                // the grade coefficient will be applied when storing in the user result.
                                if (uAns == right) {
                                    userUpdate(u_id, p_id, lt, CORRECT_ANSWER);
                                } else
                                    userUpdate(u_id, p_id, lt, WRONG_ANSWER);
                                break;
                            }
                        }
                        userUpdate(u_id, p_id, lt, NO_ANSWER);
                        break;
                    case BEBRAS_DYN:
                        //System.out.println("BEBRAS_DYN:\n");
                        int uAns = -2;
                        try {
                            uAns = a.getInt32("r").getValue();
                        } catch (Exception e) {
                            System.err.println(e.getLocalizedMessage()+"\npid:" + p_id + ", u_id:"+ u_id + ", lt:"+ lt);
                            break;
                        }

                        /*
                        r = null => program error -> do nothing
                        r = 0 => no answer
                        r = -1 => wrong answer
                        r = 1 => correct answer
                        r = 2 => either wrong or correct, user answer should be compared with the correct one */
                        switch(uAns){
                            case 0:
                                userUpdate(u_id, p_id, lt, NO_ANSWER);
                                break;
                            case -1:
                                userUpdate(u_id, p_id, lt, WRONG_ANSWER);
                                break;
                            case 1:
                                userUpdate(u_id, p_id, lt, CORRECT_ANSWER);
                                break;
                            case 2:
                                try {
                                    BsonString correct_answer = p.getString("correct_answer");
                                    BsonString user_answer = a.getString("s");
                                    if (correct_answer.equals(user_answer)){
                                        userUpdate(u_id, p_id, lt, CORRECT_ANSWER);
                                    }else
                                        userUpdate(u_id, p_id, lt, WRONG_ANSWER);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    break;
                                }
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value: " + type);
                        }

                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + type);
                }
            }

        }
        return true;
    }

    private static void userUpdate(ObjectId u_id, ObjectId p_id, int lt, int u_answer) {
        //TODO: Implement userUpdate:
        User u;
        try {
            u = users.get(u_id);
            if(u == null) {
                //String msg = "User no found in Hashmap!";
                throw new Exception("User no found in Hashmap!");
            }
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
            return;
        }
        //u.getCompetition(competition_1.getId()).addTask(p_id, u_answer);


        //.................................
        System.out.println("uID: " + u_id.toString() + "| pid: " + p_id.toString() + "| Answer given:" + u_answer + "\n");
//        try {
//            TimeUnit.MILLISECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }

    

}
