import event_description.Competition_desc;
import event_description.Event_desc;
import event_description.Task_desc;
import org.apache.commons.lang3.SerializationUtils;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.types.ObjectId;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static HashMap<ObjectId, User> users;
    public static HashMap<ObjectId, User> c_participants;
    public static HashMap<ObjectId, User> mapGrade_a;
    public static HashMap<ObjectId, User> mapGrade_b;
    public static ArrayList<HashMap<ObjectId, User>> mapGrades;

    public static HashMap<String, Integer> mapRegion_b;
    public static HashMap<String, Integer> mapRegion_a;
    public static ArrayList<HashMap<String, Integer>> mapRegions;

    public static ArrayList<ObjectId> listGrade_a;
    public static ArrayList<ObjectId> listGrade_b;
    public static ArrayList<ArrayList<ObjectId>> listGrades;
    //..................................
    private static final String BEBRAS = "bebras";
    private static final String BEBRAS_DYN = "bebras-dyn";
    public static final int CORRECT_ANSWER = 3;
    public static final int WRONG_ANSWER = -1;
    public static final int NO_ANSWER = 0;
    //public static final int grade_a = 3;
    //public static final int grade_b = 4;
    private static ObjectId eventId;
    private static String eventTitle;
    //private static ObjectId competitionId;
    private static String competitionTitle;
    private static ArrayList<Integer> competitionValidGrade;
    private static ArrayList<String> competitionTitles;
    //private static HashMap<ObjectId, Integer> competitionIds;
    private static HashMap<Integer, ObjectId> competitionIds_2;
    private static HashMap<Integer, Integer> competitionGrades;
    private static ArrayList<HashMap<ObjectId, int[]>> competitionTasks;


    public static void main(String[] args) {
        //counter = 0;
        Yaml yaml = new Yaml(new Constructor(Event_desc.class));
        InputStream inputStream = Main.class
                .getClassLoader()
                .getResourceAsStream("bebras_evaluator.yml");
        Event_desc event_desc = yaml.load(inputStream);
        System.out.println(event_desc);
        eventId =event_desc.get_id();
        eventTitle = event_desc.getTitle();
      //  competitionId = new ObjectId();
        competitionTitle = "c_bebras17_3-4";
        System.out.println("____________ INITIALIZING ____________");
        //................................
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.WARNING);
        MongoHandler mongoHandler = new MongoHandler();
        mongoHandler.mongoClientInstance();
        mongoHandler.getCollections();
        System.out.println(ANSI_RED + "______________________________________");
        System.out.println(ANSI_YELLOW + "______________________________________");
        System.out.println(ANSI_GREEN + "______________________________________" + ANSI_RESET);

        HashMap<ObjectId, BsonDocument> problems = mongoHandler.readProblems();
        users = mongoHandler.getUsers();
        c_participants = new HashMap<>();
        //mapGrade_a = new HashMap<>();
        //mapGrade_b = new HashMap<>();
        mapRegion_a = new HashMap<>();
        mapRegion_b = new HashMap<>();
        listGrade_a = new ArrayList<>();
        listGrade_b = new ArrayList<>();
        mapGrades = new ArrayList<>();
        mapRegions = new ArrayList<>();
        listGrades = new ArrayList<>();
        //listGrade_Debug = new ArrayList<>();
        competitionValidGrade = new ArrayList<>();
        competitionTitles = new ArrayList<>();
        //competitionIds = new HashMap<>();
        competitionIds_2 = new HashMap<>();
        competitionGrades = new HashMap<>();
        competitionTasks = new ArrayList<>();
        for (Competition_desc competition_desc : event_desc.getCompetition_list()) {
            competitionValidGrade.add(competition_desc.getValid_user().getGrade());
            //competitionIds.put(competition_desc.get_id(), competitionTitles.size()); //put the index in corresponding lists
            competitionIds_2.put(competitionTitles.size(), competition_desc.get_id()); //put the index in corresponding lists
            competitionGrades.put(competition_desc.getValid_user().getGrade(), competitionTitles.size()); //put the index in corresponding lists
            competitionTitles.add(competition_desc.getTitle());
            mapGrades.add(new HashMap<>());
            mapRegions.add(new HashMap<>());
            listGrades.add(new ArrayList<>());
            HashMap<ObjectId, int[]> hm = new HashMap<>();
            for (Task_desc task_desc : competition_desc.getTask_list()){
            //Iterator<Task_desc> iterator = competition_desc.getTask_list().iterator();
            //while (iterator.hasNext()) {
                int[] tmp = new int[3];
                tmp[2] = task_desc.getScore();
                hm.put(task_desc.get_id(), tmp);
            }
            competitionTasks.add(hm);
        }
        mongoHandler.getCompetitions();
        Queue<Submission> submissions = mongoHandler.readSubmissions();
        checkSubmissions(problems, submissions);
        System.out.println(ANSI_RED + "______________________________________");
        System.out.println(ANSI_YELLOW + "______________________________________");
        System.out.println(ANSI_GREEN + "______________________________________\n" + ANSI_RESET);
        outputTest(new ObjectId("5e9efd8c1813b22c476c788e"),0);
        outputTest(new ObjectId("5e9efd8c1813b22c476c788d"),2);
        //program_ids(new ObjectId("5e9efd8c1813b22c476c788e"));
    }

    private static boolean checkSubmissions(HashMap<ObjectId, BsonDocument> problems, Queue<Submission> submissions) {
        // we have 2 types: "bebras" & "bebras-dyn"
        //TODO: add more types
        String type;
        BsonDocument p;
        ObjectId p_id;
        ObjectId u_id;
        ObjectId c_id;
        BsonDocument a;
        int lt;
        for (Submission s : submissions) {
            p_id = s.getPid();
            u_id = s.getU();
            c_id = s.getC_id(); //get the competition id
            lt = s.getLt();
            a = s.getA();
            //TODO: define sets of problems for competitions load the HashMap according to c_id
            p = problems.get(p_id); // get problem in the format of BsonDocument
            if (p == null) {
                System.err.println("Main line 90 -- Error: the problem did not found in the HashMap");
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
                                    userUpdate(c_id, u_id, p_id, lt, CORRECT_ANSWER);
                                } else
                                    userUpdate(c_id, u_id, p_id, lt, WRONG_ANSWER);
                                break;
                            }
                        }
                        userUpdate(c_id, u_id, p_id, lt, NO_ANSWER);
                        break;
                    case BEBRAS_DYN:
                        //System.out.println("BEBRAS_DYN:\n");
                        int uAns;
                        try {
                            uAns = a.getInt32("r").getValue();
                        } catch (Exception e) {
                            System.err.println(ANSI_YELLOW + e.getLocalizedMessage() + ANSI_RED);
                            //System.err.println("pid:" + p_id + ", u_id:"+ u_id + ", lt:"+ lt + ANSI_RED);
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
                                userUpdate(c_id, u_id, p_id, lt, NO_ANSWER);
                                break;
                            case -1:
                                userUpdate(c_id, u_id, p_id, lt, WRONG_ANSWER);
                                break;
                            case 1:
                                userUpdate(c_id, u_id, p_id, lt, CORRECT_ANSWER);
                                break;
                            case 2:
                                try {
                                    BsonString correct_answer = p.getString("correct_answer");
                                    BsonString user_answer = a.getString("s");
                                    if (correct_answer.equals(user_answer)){
                                        userUpdate(c_id, u_id, p_id, lt, CORRECT_ANSWER);
                                    }else
                                        userUpdate(c_id, u_id, p_id, lt, WRONG_ANSWER);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    break;
                                }
                                break;
                            default:
                                throw new IllegalStateException("Main: line 154 _ Unexpected value for uAns: " + uAns);
                        }

                        break;
                    default:
                        throw new IllegalStateException("Main: line 159 _ Unexpected value fpr problem type: " + type);
                }
            }

        }
        return true;
    }

    private static void userUpdate(ObjectId c_id, ObjectId u_id, ObjectId p_id, int lt, int mark) {
        User u;
        try {
            u = users.get(u_id);
            if(u == null) {
                //String msg = "User no found in Hashmap!";
                throw new Exception(" Main: Line 180 _ User not found in Hashmap!");
            }
        } catch (Exception e) {
            System.err.println(ANSI_YELLOW + e.getLocalizedMessage() + ANSI_RED);
            return;
        }
        try {
            if(u.getCompetition(c_id) == null) {
                userFirstTimePreparation(c_id, p_id, lt, mark, u);
            }else {
                //DEBUG:........................................
//                if(u.getRegion().equals("SPB")) {
//                    //System.out.println("-------------------- ");
//                    if (counter == -7) {
//                        User uu;
//                        int score;
//                        int gR, gP;
//                        int rR, rP;
//                        String region_1;
//                        int c = 0;
//                        for (int i = 0; i < listGrade_b.size(); i++) {
//                            uu = mapGrade_b.get(listGrade_b.get(i));
//                            score = uu.getCompetition(c_id).getScore();
//                            gR = uu.getCompetition(c_id).getRank_in_grade();
//                            gP = uu.getGradePosition();
//                            rR = uu.getCompetition(c_id).getRank_in_reg();
//                            rP = uu.getRegionPosition();
//                            region_1 = uu.getRegion();
//                            if (region_1.equals("SPB")) {
//                                c++;
//                                System.out.println("Student_ID: " + uu.getId() + " | Score:" + score + " | Grade_Rank:" + gR + " | Grade_Position:" + gP + " | Region_Rank:" + rR + " | Region_Position: " + rP + " | Region:" + region_1);
//                            }
//                        }
//                        System.out.println("Count:" + c);
//                    }
//                }
                //....................................
                int oldScore = u.getCompetition(c_id).getScore();
                u.getCompetition(c_id).updateTask(p_id, lt, mark); //for those who did not participate in their grade here will throw exception
                int updateDirection = u.getCompetition(c_id).getScore() - oldScore;
                if (!userRankUpdate(c_id, u, oldScore, updateDirection))
                    throw new Exception("Main: line 275 _ User's grade is not valid!");
            }
        } catch (Exception e) {
            System.err.println("Main, line 278: " + e.getMessage());
        }
    }

    private static void userFirstTimePreparation(ObjectId c_id, ObjectId p_id, int lt, int mark, User u) throws Exception {
        //int index = competitionIds.get(c_id);
        // i = current pointer in the list(user's at first)
        // the expectation is to get users competition from the submission directly
        // determining the competition using the grade can be fault prone
        // as some user might participate in a different competition.
        // in cas the we get the competition
        int index = 0;
        try {
            index = competitionGrades.get(u.getGrade());
        } catch (Exception ignore) {
            System.err.println("Main, line 287: not valid user for this competition");
        }
        c_id = competitionIds_2.get(index);
        u.updateRegion();
        Event event = null;
        event = new Event(eventId, eventTitle);
        Competition competition = new Competition(c_id, competitionTitles.get(index));
        //HashMap<ObjectId, int[]> kir;
        //kir = SerializationUtils.clone(competitionTasks.get(index));
        competition.setTasks(SerializationUtils.clone(competitionTasks.get(index)));
        competition.setValid_grade(competitionValidGrade.get(index));
        competition.updateTask(p_id, lt, mark);
        event.addCompetition(competition);
        u.addEvent(event);
        String region;
        int regionLastRank;
        if(u.getGrade() == u.getCompetition(c_id).getValid_grade()) {
            //switch(u.getGrade()) {

            //case grade_b:
            //preparation for regional rank
            region = u.getRegion();
            //regionLastRank = mapRegion_b.getOrDefault(region, -1);
            regionLastRank = mapRegions.get(index).getOrDefault(region, -1);
            //if the region is not in our map (it is the first time)
            if (regionLastRank == -1) {
                // the last position in this region is 1 the current user
                //mapRegion_b.put(region, 1);
                mapRegions.get(index).put(region, 1);
                u.getCompetition(c_id).setRank_in_reg(1);
                u.setRegionPosition(1);
            } else {
                regionLastRank++;
                //mapRegion_b.put(region, regionLastRank);
                mapRegions.get(index).put(region, regionLastRank);
                u.getCompetition(c_id).setRank_in_reg(regionLastRank);
                u.setRegionPosition(regionLastRank);
            }
            //Update ranks
            //mapGrade_b.put(u.getId(), u);
            mapGrades.get(index).put(u.getId(), u);
            //listGrade_b.add(u.getId());
            listGrades.get(index).add(u.getId());
            //u.getCompetition(c_id).setRank_in_grade(listGrade_b.size());
            u.getCompetition(c_id).setRank_in_grade(listGrades.get(index).size());
            u.setGradePosition(listGrades.get(index).size());
            //DEBUG:................................................
//                int[] tmp = new int[5];
//                tmp[0]=u.getCompetition(c_id).getScore();
//                tmp[1]=u.getGradePosition();
//                tmp[2]=u.getCompetition(c_id).getRank_in_grade();
//                tmp[3] = u.getRegionPosition();
//                tmp[4] = u.getCompetition(c_id).getRank_in_reg();
//                listGrade_Debug.add(tmp);
            //DEBUG:................................................
            //firstTimeRankUpdate(c_id, u, listGrade_b, mapGrade_b, region);
            firstTimeRankUpdate(c_id, u, listGrades.get(index), mapGrades.get(index), region);
            return;
//        }
//        if(u.getGrade() == competitionValidGrade.get(index)[1]) {
//            //case grade_a:
//            //preparation for regional rank
//            region = u.getRegion();
//            regionLastRank = mapRegion_a.getOrDefault(region, -1);
//            //if the region is not in our map (it is the first time)
//            if (regionLastRank == -1) {
//                // the last position in this region is 1 the current user
//                mapRegion_a.put(region, 1);
//                u.getCompetition(c_id).setRank_in_reg(1);
//                u.setRegionPosition(1);
//            } else {
//                regionLastRank++;
//                mapRegion_a.put(region, regionLastRank);
//                u.getCompetition(c_id).setRank_in_reg(regionLastRank);
//                u.setRegionPosition(regionLastRank);
//            }
//            //Update ranks
//            mapGrade_a.put(u.getId(), u);
//            listGrade_a.add(u.getId());
//            u.getCompetition(c_id).setRank_in_grade(listGrade_a.size());
//            u.setGradePosition(listGrade_a.size());
//            //DEBUG:................................................
////                int[] tmp1 = new int[5];
////                tmp1[0]=u.getCompetition(c_id).getScore();
////                tmp1[1]=u.getGradePosition();
////                tmp1[2]=u.getCompetition(c_id).getRank_in_grade();
////                tmp1[3] = u.getRegionPosition();
////                tmp1[4] = u.getCompetition(c_id).getRank_in_reg();
////                listGrade_Debug.add(tmp1);
//            //.......................................................
//            firstTimeRankUpdate(c_id, u, listGrade_a, mapGrade_a, region);
//            return;
        }
        //default:
        throw new IllegalStateException("Main: line 327 _ Unexpected value for participant's grade: " + u.getGrade());
    }

    private static boolean userRankUpdate(ObjectId c_id, User u, int oldScore, int updateDirection) {
        //int index = competitionIds.get(c_id);
        int index = competitionGrades.get(u.getGrade());
        if(u.getGrade() == u.getCompetition(c_id).getValid_grade()) {
            if(updateDirection > 0) {
                //upwardRankUpdate(c_id, u, listGrade_b, mapGrade_b, u.getRegion(), oldScore);
                upwardRankUpdate(c_id, u, listGrades.get(index), mapGrades.get(index), u.getRegion(), oldScore);
                return true;
            }
            if(updateDirection < 0){
                //downwardRankUpdate(c_id, u, listGrade_b, mapGrade_b, u.getRegion(), oldScore);
                downwardRankUpdate(c_id, u, listGrades.get(index), mapGrades.get(index), u.getRegion(), oldScore);
            }
            return true;
        }
//        if(u.getGrade() == competitionValidGrades.get(index)[1]) {
//            if(updateDirection > 0) {
//                upwardRankUpdate(c_id, u, listGrade_a, mapGrade_a, u.getRegion(), oldScore);
//                return true;
//            }
//            if(updateDirection < 0){
//                downwardRankUpdate(c_id, u, listGrade_a, mapGrade_a, u.getRegion(), oldScore);
//            }
//            return true;
//        }
        return false;
    }

    private static void firstTimeRankUpdate(ObjectId c_id, User u, ArrayList<ObjectId> listGrade, HashMap<ObjectId,User> mapGrade, String region) {
        int score = u.getCompetition(c_id).getScore();
        // i = current pointer in the list(user's at first)
        int i = u.getGradePosition() - 1;
        User u_2;
        //............................................
        //new user, his position and rank is the last right now
        //....................................
        // as we are going upward we should check not to be the first!
        if(i > 0) { //if the pointer is not on the first user
            u_2 = mapGrade.get(listGrade.get(i - 1));
            /* change the position and ranks as much as needed*/
            while(score > u_2.getCompetition(c_id).getScore()){
                listGrade.set(i, u_2.getId()); // put u-2 at the current pointer
                u_2.getCompetition(c_id).updateRank_in_grade(1); // his rank now is one more! of course!
                u_2.updateGradePosition(1); // the same store for the position
                if (u_2.getRegion().equals(region)) {
                    u_2.getCompetition(c_id).updateRank_in_reg(1);
                    u_2.updateRegionPosition(1);
                    u.getCompetition(c_id).updateRank_in_reg(-1);
                    u.updateRegionPosition(-1);
                }
                //DEBUG:........................................................
//                int[] tmp = new int[5];
//                tmp[0] = u_2.getCompetition(c_id).getScore();
//                tmp[1] = u_2.getGradePosition();
//                tmp[2] = u_2.getCompetition(c_id).getRank_in_grade();
//                tmp[3] = u_2.getRegionPosition();
//                tmp[4] = u_2.getCompetition(c_id).getRank_in_reg();
//                listGrade_Debug.set(i, tmp);
                //..............................................................
                // decreasing the pointer (grade position)
                i--;
                // now the rank in grade also should be declined
                u.getCompetition(c_id).updateRank_in_grade(-1);
                //again, at current position rank and position should be the same:
                //Assert.assertEquals(i + 1, u.getCompetition(c_id).getRank_in_grade());
                if (i == 0) {
                    //this user is first! congratulations!
                    listGrade.set(i, u.getId());
                    u.setGradePosition(i + 1);
                    //no need to set rank in grade!
                    //u.getCompetition(c_id).setRank_in_reg(regionRank);
                    //u.setRegionPosition(regionRank);
                    //DEBUG:.................................................
//                    int[] tmp1 = new int[5];
//                    tmp1[0]=u.getCompetition(c_id).getScore();
//                    tmp1[1]=u.getGradePosition();
//                    tmp1[2]=u.getCompetition(c_id).getRank_in_grade();
//                    tmp1[3] = u.getRegionPosition();
//                    tmp1[4] = u.getCompetition(c_id).getRank_in_reg();
//                    listGrade_Debug.set(i, tmp1);
                    //.......................................................
                    return;
                }
                u_2 = mapGrade.get(listGrade.get(i - 1));
            }
            //ops, someone has a score equal or more than our user's
            /* again if the new score is the same with the one's score who
               stands just before, we need to take the current position as
               users position and the rank of u-2 as his new rank!*/
            if(score == u_2.getCompetition(c_id).getScore()){
                listGrade.set(i, u.getId());
                u.setGradePosition(i + 1);
                u.getCompetition(c_id)
                        .setRank_in_grade(u_2.getCompetition(c_id).getRank_in_grade());
                //for regional ranking we need to check the region of people with the same score
                //the new regional rank shod be the same. or the same as position if we did not find any.
                if(u_2.getRegion().equals(region)) {
                    u.getCompetition(c_id)
                            .setRank_in_reg(u_2.getCompetition(c_id).getRank_in_reg());
                }else{
                    for(int j=i-2; j>=0; j--){
                        u_2 = mapGrade.get(listGrade.get(j));
                        if(score != u_2.getCompetition(c_id).getScore()) {
                            //u.getCompetition(c_id).setRank_in_reg(u.getRegionPosition());
                            break;
                        }
                        if(u_2.getRegion().equals(region)){
                            u.getCompetition(c_id)
                                    .setRank_in_reg(u_2.getCompetition(c_id).getRank_in_reg());
                            break;
                        }
                    }
                }
                //DEBUG:.................................................
//                int[] tmp = new int[5];
//                tmp[0]=u.getCompetition(c_id).getScore();
//                tmp[1]=u.getGradePosition();
//                tmp[2]=u.getCompetition(c_id).getRank_in_grade();
//                tmp[3] = u.getRegionPosition();
//                tmp[4] = u.getCompetition(c_id).getRank_in_reg();
//                listGrade_Debug.set(i, tmp);
                //.......................................................
            }
            /* just as before if the new score is less than what is just before,
               we need to take the current position as user's position
               rank should be automatically the same as the position*/
            else { //if(score < u_2.getCompetition(c_id).getScore()){
                listGrade.set(i, u.getId());
                u.setGradePosition(i + 1);
                //no need to set rank in grade!
                //u.getCompetition(c_id).setRank_in_reg(regionRank);
                //u.setRegionPosition(regionRank);
                //DEBUG:.................................................
//                int[] tmp = new int[5];
//                tmp[0]=u.getCompetition(c_id).getScore();
//                tmp[1]=u.getGradePosition();
//                tmp[2]=u.getCompetition(c_id).getRank_in_grade();
//                tmp[3] = u.getRegionPosition();
//                tmp[4] = u.getCompetition(c_id).getRank_in_reg();
//                listGrade_Debug.set(i, tmp);
                //.......................................................
            }
        }
        // here our use is already the first place, all his rank are already 1.
        // we have already increased the rank of other with the same previous score!
        // nothing to do then!
    }

    private static void upwardRankUpdate(ObjectId c_id, User u, ArrayList<ObjectId> listGrade, HashMap<ObjectId, User> mapGrade, String region, int oldScore) {
        int score = u.getCompetition(c_id).getScore();
        // i = current pointer in the list(user's at first)
        int i = u.getGradePosition() - 1;
        User u_2;
        //............................................
        //to start with I will bring the position to the first place
        //among all who have the same score.
        //....................................
        //update the rank and position of users with the same score (as the old score)
        // for those at the bottom, this is being done by adding 1 to their ranks, leaving their position unchanged.
        if(i < listGrade.size() - 1) { //if the pointer is not on the last user
            for (int j = i; j < listGrade.size() - 1; j++) {
                u_2 = mapGrade.get(listGrade.get(j + 1));
                if(u_2.getCompetition(c_id).getScore() != oldScore)
                    break;
                //n change in position
                u_2.getCompetition(c_id).updateRank_in_grade(1);
                if(u_2.getRegion().equals(region)){
                    u_2.getCompetition(c_id).updateRank_in_reg(1);
                    //u_2.updateRegionPosition(1);
                }
                //DEBUG:........................................................
//                int[] tmp = new int[5];
//                tmp[0]=u_2.getCompetition(c_id).getScore();
//                tmp[1]=u_2.getGradePosition();
//                tmp[2]=u_2.getCompetition(c_id).getRank_in_grade();
//                tmp[3] = u_2.getRegionPosition();
//                tmp[4] = u_2.getCompetition(c_id).getRank_in_reg();
//                listGrade_Debug.set(j + 1, tmp);
                //..............................................................
            }
        }
        // as we are going upward we should check not to be the first!
        if(i > 0) { //if the pointer is not on the first user
            u_2 = mapGrade.get(listGrade.get(i - 1));
            // now move the user to the top among those who had the same score
            while (oldScore == u_2.getCompetition(c_id).getScore()) {
                listGrade.set(i, u_2.getId());
                u_2.getCompetition(c_id).updateRank_in_grade(1);
                u_2.updateGradePosition(1);
                if(u_2.getRegion().equals(region)){
                    u_2.getCompetition(c_id).updateRank_in_reg(1);
                    u_2.updateRegionPosition(1);
                    u.updateRegionPosition(-1);
                    //regionRank --; the same as old score -> all rank are the same now
                }
                //DEBUG:........................................................
//                int[] tmp = new int[5];
//                tmp[0]=u_2.getCompetition(c_id).getScore();
//                tmp[1]=u_2.getGradePosition();
//                tmp[2]=u_2.getCompetition(c_id).getRank_in_grade();
//                tmp[3] = u_2.getRegionPosition();
//                tmp[4] = u_2.getCompetition(c_id).getRank_in_reg();
//                listGrade_Debug.set(i, tmp);
                //..............................................................
                // decreasing the pointer (grade position)
                // now gradeRank grade rank should not be changed as we are moving through the same rank
                i--;
                if (i == 0) {
                    //first place, waw!
                    listGrade.set(i, u.getId());
                    u.setGradePosition(i + 1);
                    //no need to set rank in grade!
                    //Assert.assertEquals( 1, u.getCompetition(c_id).getRank_in_grade());
                    //Assert.assertEquals( 1, u.getCompetition(c_id).getRank_in_reg());
                    //DEBUG:.................................................
//                    int[] tmp1 = new int[5];
//                    tmp1[0]=u.getCompetition(c_id).getScore();
//                    tmp1[1]=u.getGradePosition();
//                    tmp1[2]=u.getCompetition(c_id).getRank_in_grade();
//                    tmp1[3] = u_2.getRegionPosition();
//                    tmp1[4] = u_2.getCompetition(c_id).getRank_in_reg();
//                    listGrade_Debug.set(i, tmp1);
                    //.......................................................
                    return;
                }
                u_2 = mapGrade.get(listGrade.get(i - 1));
            }
            //Now the user position should be the first among those who have the same score as he did
            //then at current position rank and position should be the same:
            /* change the position and ranks as much as needed*/
            while(score > u_2.getCompetition(c_id).getScore()){
                listGrade.set(i, u_2.getId()); // put u-2 at the current pointer
                u_2.getCompetition(c_id).updateRank_in_grade(1); // his rank now is one more! of course!
                u_2.updateGradePosition(1); // the same store for the position
                if (u_2.getRegion().equals(region)) {
                    u_2.getCompetition(c_id).updateRank_in_reg(1);
                    u_2.updateRegionPosition(1);
                    u.getCompetition(c_id).updateRank_in_reg(-1);
                    u.updateRegionPosition(-1);
                }
                //DEBUG:........................................................
//                int[] tmp = new int[5];
//                tmp[0] = u_2.getCompetition(c_id).getScore();
//                tmp[1] = u_2.getGradePosition();
//                tmp[2] = u_2.getCompetition(c_id).getRank_in_grade();
//                tmp[3] = u_2.getRegionPosition();
//                tmp[4] = u_2.getCompetition(c_id).getRank_in_reg();
//                listGrade_Debug.set(i, tmp);
                //..............................................................
                // decreasing the pointer (grade position)
                i--;
                // now the rank in grade also should be declined
                u.getCompetition(c_id).updateRank_in_grade(-1);
                //again, at current position rank and position should be the same:
                if (i == 0) {
                    //this user is first! congratulations!
                    listGrade.set(i, u.getId());
                    u.setGradePosition(i + 1);
                    //no need to set rank in grade!
                    //u.getCompetition(c_id).setRank_in_reg();
                    //u.setRegionPosition(regionRank);
                    //DEBUG:.................................................
//                    int[] tmp1 = new int[5];
//                    tmp1[0]=u.getCompetition(c_id).getScore();
//                    tmp1[1]=u.getGradePosition();
//                    tmp1[2]=u.getCompetition(c_id).getRank_in_grade();
//                    tmp1[3] = u.getRegionPosition();
//                    tmp1[4] = u.getCompetition(c_id).getRank_in_reg();
//                    listGrade_Debug.set(i, tmp1);
                    //.......................................................
                    return;
                }
                u_2 = mapGrade.get(listGrade.get(i - 1));
            }
            //ops, someone has a score equal or more than our user's
            /* again if the new score is the same with the one's score who
               stands just before, we need to take the current position as
               users position and the rank of u-2 as his new rank!*/
            if(score == u_2.getCompetition(c_id).getScore()){
                listGrade.set(i, u.getId());
                u.setGradePosition(i + 1);
                u.getCompetition(c_id)
                        .setRank_in_grade(u_2.getCompetition(c_id).getRank_in_grade());
                if(u_2.getRegion().equals(region)) {
                    u.getCompetition(c_id)
                            .setRank_in_reg(u_2.getCompetition(c_id).getRank_in_reg());
                }else{
                    for(int j=i-2; j>=0; j--){
                        u_2 = mapGrade.get(listGrade.get(j));
                        if(score != u_2.getCompetition(c_id).getScore()) {
                            //u.getCompetition(c_id).setRank_in_reg(regionRank);
                            u.getCompetition(c_id).setRank_in_reg(u.getRegionPosition()); //i+1
                            break;
                        }
                        if(u_2.getRegion().equals(region)){
                            u.getCompetition(c_id)
                                    .setRank_in_reg(u_2.getCompetition(c_id).getRank_in_reg());
                            break;
                        }
                    }
                }
                //DEBUG:.................................................
//                int[] tmp = new int[5];
//                tmp[0]=u.getCompetition(c_id).getScore();
//                tmp[1]=u.getGradePosition();
//                tmp[2]=u.getCompetition(c_id).getRank_in_grade();
//                tmp[3] = u.getRegionPosition();
//                tmp[4] = u.getCompetition(c_id).getRank_in_reg();
//                listGrade_Debug.set(i, tmp);
                //.......................................................
            }
            /* just as before if the new score is less than what is just before,
               we need to take the current position as user's position
               rank should be automatically the same as the position*/
            else { //if(score < u_2.getCompetition(c_id).getScore()){
                listGrade.set(i, u.getId());
                u.setGradePosition(i + 1);
                //no need to set rank in grade!
                //u.getCompetition(c_id).setRank_in_reg(u.getRegionPosition());
                //u.setRegionPosition(regionRank);
                //DEBUG:.................................................
//                int[] tmp = new int[5];
//                tmp[0]=u.getCompetition(c_id).getScore();
//                tmp[1]=u.getGradePosition();
//                tmp[2]=u.getCompetition(c_id).getRank_in_grade();
//                tmp[3] = u.getRegionPosition();
//                tmp[4] = u.getCompetition(c_id).getRank_in_reg();
//                listGrade_Debug.set(i, tmp);
                //.......................................................
            }
        }
        // here our use is already the first place, all his rank are already 1.
        // we have already increased the rank of other with the same previous score!
        // nothing to do then!
        //DEBUG:........................................................
//        int[] tmp = new int[5];
////        tmp[0] = u.getCompetition(c_id).getScore();
////        tmp[1] = u.getGradePosition();
////        tmp[2] = u.getCompetition(c_id).getRank_in_grade();
////        tmp[3] = u.getRegionPosition();
////        tmp[4] = u.getCompetition(c_id).getRank_in_reg();
////        listGrade_Debug.set(i, tmp);
        //..............................................................
    }

    private static void downwardRankUpdate(ObjectId c_id, User u, ArrayList<ObjectId> listGrade, HashMap<ObjectId, User> mapGrade, String region, int oldScore) {
        int score = u.getCompetition(c_id).getScore();
        int i = u.getGradePosition() - 1;
        User u_2;
        //............................................
        //to start with I will bring the position to the last place
        //among all who have the same score. nothing to do with users who stand above.
        //....................................
        //the rank of users with the same score should be left unchanged, but one decline is needed for their position.
        // this is being done by declining 1 from their position, leaving their rank unchanged.
        if(i < listGrade.size() - 1) { //if the pointer is not on the last user
            u_2 = mapGrade.get(listGrade.get(i + 1));
            // now move the user to the bottom among those who had the same score
            while (oldScore == u_2.getCompetition(c_id).getScore()) {
                listGrade.set(i, u_2.getId());
                //no update fo rank of u_2 is needed as we are moving along the same ranks
                u_2.updateGradePosition(-1);
                if (u_2.getRegion().equals(region)) {
                    //u_2.getCompetition(c_id).updateRank_in_reg(-1);
                    u_2.updateRegionPosition(-1);
                    u.updateRegionPosition(1);
                }
                //DEBUG:........................................................
//                int[] tmp = new int[5];
//                tmp[0] = u_2.getCompetition(c_id).getScore();
//                tmp[1] = u_2.getGradePosition();
//                tmp[2] = u_2.getCompetition(c_id).getRank_in_grade();
//                tmp[3] = u_2.getRegionPosition();
//                tmp[4] = u_2.getCompetition(c_id).getRank_in_reg();
//                listGrade_Debug.set(i, tmp);
                //..............................................................
                // increasing the pointer (grade position)
                // now gradeRank grade rank should not be changed as we are moving through the same rank
                i++;
                if (i == listGrade.size() - 1) {
                    //last place, :(
                    listGrade.set(i, u.getId());
                    u.setGradePosition(i + 1);
                    // his last score is the same as one above! now his score decreased!
                    // he is the absolute last person of this competition! booooooo
                    u.getCompetition(c_id).setRank_in_grade(i + 1);
                    u.getCompetition(c_id).setRank_in_reg(u.getRegionPosition());//absolute last person: postion and rank are the same
                    //DEBUG:.................................................
//                    int[] tmp1 = new int[5];
//                    tmp1[0] = u.getCompetition(c_id).getScore();
//                    tmp1[1] = u.getGradePosition();
//                    tmp1[2] = u.getCompetition(c_id).getRank_in_grade();
//                    tmp1[3] = u.getRegionPosition();
//                    tmp1[4] = u.getCompetition(c_id).getRank_in_reg();
//                    listGrade_Debug.set(i, tmp1);
                    //.......................................................
                    return;
                }
                u_2 = mapGrade.get(listGrade.get(i + 1));
            }
            //........................................................................................
            //........................................................................................
            //Now the user position should be the last among those who have the same score as he did
            //then for u_2 we should have:
            //Assert.assertEquals(i + 2, u_2.getCompetition(c_id).getRank_in_grade());
            /* new score is less! change the position and ranks as much as needed*/
            while (score < u_2.getCompetition(c_id).getScore()) {
                listGrade.set(i, u_2.getId()); // put u-2 at the current pointer
                u_2.getCompetition(c_id).updateRank_in_grade(-1); // his rank now is one less! of course!
                u_2.updateGradePosition(-1); // the same story for the position
                if (u_2.getRegion().equals(region)) {
                    u_2.getCompetition(c_id).updateRank_in_reg(-1);
                    u_2.updateRegionPosition(-1);
                    u.updateRegionPosition(1);
                    u.getCompetition(c_id).setRank_in_reg(u.getRegionPosition());
                }
                //DEBUG:........................................................
//                int[] tmp = new int[5];
//                tmp[0] = u_2.getCompetition(c_id).getScore();
//                tmp[1] = u_2.getGradePosition();
//                tmp[2] = u_2.getCompetition(c_id).getRank_in_grade();
//                tmp[3] = u_2.getRegionPosition();
//                tmp[4] = u_2.getCompetition(c_id).getRank_in_reg();
//                listGrade_Debug.set(i, tmp);
                //..............................................................
                // increasing the pointer (grade position)
                i++;
                // now the rank in grade is the same as position!
                u.getCompetition(c_id).setRank_in_grade(i + 1);
                if (i == listGrade.size() - 1) {
                    //last place, :(
                    listGrade.set(i, u.getId());
                    u.setGradePosition(i + 1);
                    // his last score was the same as one above! now his score decreased!
                    // he is the absolute last person of this competition! booooooo
                    //u.getCompetition(c_id).setRank_in_grade(i + 1);// we just did
                    //u.getCompetition(c_id).setRank_in_reg(u.getRegionPosition());
                    //DEBUG:.................................................
//                    int[] tmp1 = new int[5];
//                    tmp1[0] = u.getCompetition(c_id).getScore();
//                    tmp1[1] = u.getGradePosition();
//                    tmp1[2] = u.getCompetition(c_id).getRank_in_grade();
//                    tmp1[3] = u.getRegionPosition();
//                    tmp1[4] = u.getCompetition(c_id).getRank_in_reg();
//                    listGrade_Debug.set(i, tmp1);
                    //.......................................................
                    return;
                }
                u_2 = mapGrade.get(listGrade.get(i + 1));
            }
            /* the new score is the same with the one who stands just after!
               we need to take the current position as users position
               and rank! we should update all others with the same score
               by this new rank*/
            if (score == u_2.getCompetition(c_id).getScore()) {
                listGrade.set(i, u.getId());
                u.setGradePosition(i + 1);
                //Assert.assertEquals(u.getGradePosition(), u_2.getCompetition(c_id).getRank_in_grade()-1);
                u.getCompetition(c_id).setRank_in_grade(i + 1);// we just did - or we did not!! :D
                u.getCompetition(c_id).setRank_in_reg(u.getRegionPosition());
                //DEBUG:.................................................
//                int[] tmp = new int[5];
//                tmp[0] = u.getCompetition(c_id).getScore();
//                tmp[1] = u.getGradePosition();
//                tmp[2] = u.getCompetition(c_id).getRank_in_grade();
//                tmp[3] = u.getRegionPosition();
//                tmp[4] = u.getCompetition(c_id).getRank_in_reg();
//                listGrade_Debug.set(i, tmp);
                //.......................................................
                // update the others with the same score: now their rank is improved by one
                //if(i < listGrade.size() - 1) { //if the pointer is not on the last user- no need we did before
                u_2.getCompetition(c_id).updateRank_in_grade(-1);
                if(u_2.getRegion().equals(region)) {
                    u_2.getCompetition(c_id).updateRank_in_reg(-1);
                }
                //DEBUG:.................................................
//                int[] tmp1 = new int[5];
//                tmp1[0] = u_2.getCompetition(c_id).getScore();
//                tmp1[1] = u_2.getGradePosition();
//                tmp1[2] = u_2.getCompetition(c_id).getRank_in_grade();
//                tmp1[3] = u_2.getRegionPosition();
//                tmp1[4] = u_2.getCompetition(c_id).getRank_in_reg();
//                listGrade_Debug.set(i + 1, tmp1);
                //.......................................................
                for (int j = i + 1 ; j < listGrade.size() - 1; j++) {
                    u_2 = mapGrade.get(listGrade.get(j + 1));
                    if(u_2.getCompetition(c_id).getScore() != score)
                        return;
                    //no change in position
                    u_2.getCompetition(c_id).updateRank_in_grade(-1);
                    if(u_2.getRegion().equals(region)) {
                        u_2.getCompetition(c_id).updateRank_in_reg(-1);
                    }
                    //DEBUG:.................................................
//                    int[] tmp2 = new int[5];
//                    tmp2[0] = u_2.getCompetition(c_id).getScore();
//                    tmp2[1] = u_2.getGradePosition();
//                    tmp2[2] = u_2.getCompetition(c_id).getRank_in_grade();
//                    tmp2[3] = u_2.getRegionPosition();
//                    tmp2[4] = u_2.getCompetition(c_id).getRank_in_reg();
//                    listGrade_Debug.set(j + 1, tmp2);
                    //........................................................
                }
                return;
            }
            /* if the new score is more than who is just after, we need
               just to take the current position and rank as user's position
               rank is one less than the rank of the next user*/
            if (score > u_2.getCompetition(c_id).getScore()) {
                listGrade.set(i, u.getId());
                u.setGradePosition(i + 1);
                u.getCompetition(c_id).setRank_in_grade(i + 1);
                u.getCompetition(c_id).setRank_in_reg(u.getRegionPosition());
                // we already have set the grade rank to be i+1, these 2 value should be the same.
                //u.getCompetition(c_id).setRank_in_grade(u_2.getCompetition(c_id).getRank_in_grade()-1);
                //Assert.assertEquals(u.getCompetition(c_id).getRank_in_grade(), u_2.getCompetition(c_id).getRank_in_grade()-1);
                //DEBUG:.................................................
//                int[] tmp = new int[5];
//                tmp[0] = u.getCompetition(c_id).getScore();
//                tmp[1] = u.getGradePosition();
//                tmp[2] = u.getCompetition(c_id).getRank_in_grade();
//                tmp[3] = u.getRegionPosition();
//                tmp[4] = u.getCompetition(c_id).getRank_in_reg();
//                listGrade_Debug.set(i, tmp);
                //.......................................................
            }
        }
        // OK, he was already the last one more negative point!
        // he is the alone person at the bottom
        else{
            listGrade.set(i, u.getId());
            u.setGradePosition(i + 1);
            // his last score is the same as one above! now his score decreased!
            // he is the absolute last person of this competition! booooooo
            u.getCompetition(c_id).setRank_in_grade(i + 1);
            u.getCompetition(c_id).setRank_in_reg(u.getRegionPosition());
            //DEBUG:.................................................
//            int[] tmp = new int[5];
//            tmp[0] = u.getCompetition(c_id).getScore();
//            tmp[1] = u.getGradePosition();
//            tmp[2] = u.getCompetition(c_id).getRank_in_grade();
//            tmp[3] = u.getRegionPosition();
//            tmp[4] = u.getCompetition(c_id).getRank_in_reg();
//            listGrade_Debug.set(i, tmp);
            //.......................................................
        }
    }

    private static void outputTest(ObjectId c_id, int index) {
        User u;
        int score;
        int gR,gP;
        int rR,rP;
        String region;
        int c =0;
        System.out.println("Grade:" + competitionValidGrade.get(index));
        for(int i =0; i<30; i++){
            //u = mapGrade_b.get(listGrade_b.get(i));
            u = mapGrades.get(index).get(listGrades.get(index).get(i));
            score = u.getCompetition(c_id).getScore();
            gR = u.getCompetition(c_id).getRank_in_grade();
            gP = u.getGradePosition();
            rR = u.getCompetition(c_id).getRank_in_reg();
            rP = u.getRegionPosition();
            region = u.getRegion();
            if(region.equals("NVS")) {
                c++;
            }
                System.out.println("Student_ID: " + u.getId() + " | Score:" + score + " | Grade_Rank:" + gR + " | Grade_Position:" + gP + " | Region_Rank:" + rR + " | Region_Position: " + rP + " | Region:" + region);

        }
        System.out.println("\"NVS\" Count = " + c);
        System.out.println(ANSI_RED + "______________________________________");
        System.out.println(ANSI_YELLOW + "______________________________________");
        System.out.println(ANSI_GREEN + "______________________________________\n" + ANSI_RESET);
        c = 0;
        System.out.println("Grade:" + competitionValidGrade.get(index + 1));
        for(int i =0; i<30; i++){
            //u = mapGrade_b.get(listGrade_b.get(i));
            u = mapGrades.get(index+1).get(listGrades.get(index+1).get(i));
            score = u.getCompetition(c_id).getScore();
            gR = u.getCompetition(c_id).getRank_in_grade();
            gP = u.getGradePosition();
            rR = u.getCompetition(c_id).getRank_in_reg();
            rP = u.getRegionPosition();
            region = u.getRegion();
            if(region.equals("NVS")) {
                c++;
            }
                System.out.println("Student_ID: " + u.getId() + " | Score:" + score + " | Grade_Rank:" + gR + " | Grade_Position:" + gP + " | Region_Rank:" + rR + " | Region_Position: " + rP + " | Region:" + region);
        }
        System.out.println("\"NVS\" Count = " + c);
        System.out.println(ANSI_RED + "______________________________________");
        System.out.println(ANSI_YELLOW + "______________________________________");
        System.out.println(ANSI_GREEN + "______________________________________\n" + ANSI_RESET);
    }

    private static void program_ids(ObjectId c_id) {
        User u;
        int score;
        int gR,gP;
        int rR,rP;
        String region;
        int c =0;
        for(int i =0; i<10; i++){
            //u = mapGrade_b.get(listGrade_b.get(i));
            u = mapGrades.get(0).get(listGrades.get(0).get(i));
            score = u.getCompetition(c_id).getScore();
            gR = u.getCompetition(c_id).getRank_in_grade();
            gP = u.getGradePosition();
            rR = u.getCompetition(c_id).getRank_in_reg();
            rP = u.getRegionPosition();
            region = u.getRegion();
            if(region.equals("NVS"))
                c++;
            System.out.println("Student_ID: " + u.getId() + " | Score:" + score + " | Grade_Rank:" + gR + " | Grade_Position:" + gP + " | Region_Rank:" + rR + " | Region_Position: " + rP + " | Region:" + region);
            u.getCompetition(c_id).getTasks().forEach((key, val)->
                    System.out.print( "pid: " + key + " ,ans: " + val[1] + " | "));
            System.out.println();
        }
        System.out.println("Count:" + c);
        System.out.println("*********************** yeaaaaahhhhh ***********************************************************");
    }
}
