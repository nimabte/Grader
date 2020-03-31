import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static HashMap<ObjectId, User> users;
    public static HashMap<ObjectId, User> c_participants;
    public static ArrayList<ObjectId> grade3_list;
    //..................................
    private static final boolean DEBUG = true;
    private static final String BEBRAS = "bebras";
    private static final String BEBRAS_DYN = "bebras-dyn";
    public static final int CORRECT_ANSWER = 3;
    public static final int WRONG_ANSWER = -1;
    public static final int NO_ANSWER = 0;
    //private static ResourceBundle myBundle = ResourceBundle.getBundle("grader");
    private static ObjectId eventId;
    private static String eventTitle;
    private static ObjectId competitionId;
    private static String competitionTitle;

    public static void main(String[] args){
        eventId = new ObjectId();
        eventTitle = "e_bebras_17";
        competitionId = new ObjectId();
        competitionTitle = "c_bebras17_3-4";
        System.out.println("____________ INITIALIZING ____________");
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
        c_participants = new HashMap<>();
        // Remove SCHOOL_ORG users from map
//        users.entrySet()
//                .removeIf(
//                        entry -> (entry.getValue().getRole().equals("SCHOOL_ORG")));
        checkSubmissions(problems, submissions);
//        users.forEach((k, v) -> {
//            HashMap<ObjectId, int[]> t;
//            t = v.getCompetition(competitionId).getTasks();
//            t.forEach((key, val)->{
//                System.out.print(key + "->" + val[0] + " , " + val[1] + " | ");
//            });
//            System.out.println();
//        });
        System.out.println("*********************** yeaaaaahhhhh ***********************************************************");
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
                                //possible results of checking the task: -1 ,0, 1 -> changed to 3 to ease up calculations
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
                            e.printStackTrace();
                            System.err.println("pid:" + p_id + ", u_id:"+ u_id + ", lt:"+ lt);
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

    private static void userUpdate(ObjectId u_id, ObjectId p_id, int lt, int mark) {
        User u;
        try {
            u = users.get(u_id);
            if(u == null) {
                //String msg = "User no found in Hashmap!";
                throw new Exception("User not found in Hashmap!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        try {
            if(u.getCompetition(competitionId) == null) {
                u.updateRegion();
                Event event = new Event(eventId, eventTitle);
                Competition competition = new Competition(competitionId, competitionTitle);
                competition.addTask(p_id, lt, mark);
                event.addCompetition(competition);
                u.addEvent(event);
                c_participants.put(u.getId(), u);
            }else
                u.getCompetition(competitionId).addTask(p_id, lt, mark);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("uID: " + u_id.toString() + "| pid: " + p_id.toString() + "| Answer given:" + mark + "\n");
//        try {
//            TimeUnit.MILLISECONDS.sleep(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }



}
