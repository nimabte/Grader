import event_description.Event_desc;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.types.ObjectId;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainTest3 {
    public static ArrayList<int[]> listGrade_Debug;
    //..................................
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    private static final boolean DEBUG = true;
    private static final String BEBRAS = "bebras";
    private static final String BEBRAS_DYN = "bebras-dyn";
    public static final int CORRECT_ANSWER = 3;
    public static final int WRONG_ANSWER = -1;
    public static final int NO_ANSWER = 0;
    //public static final int grade_a = 3;
    //public static final int grade_b = 4;
    public static final int grade_a = 5;
    public static final int grade_b = 6;
    private static ObjectId eventId;
    private static String eventTitle;
    private static ObjectId competitionId;
    private static String competitionTitle;
    private static int counter;
    @Test
    public void main() {
        Event_desc eventDesc = new Event_desc();
        Yaml yaml = new Yaml(new Constructor(Event_desc.class));;
        try(InputStream in = ClassLoader.getSystemResourceAsStream("bebras_evaluator.yml")) {
            eventDesc = yaml.loadAs(in, Event_desc.class);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        System.out.println(eventDesc.toString());
        counter = 0;
        eventId = new ObjectId();
        eventTitle = "e_bebras_17";
        competitionId = new ObjectId();
        //competitionTitle = "c_bebras17_3-4";
        competitionTitle = "bebras17-5-6";
        System.out.println("____________ INITIALIZING ____________");
        //................................
        Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
        mongoLogger.setLevel(Level.WARNING);
        MongoHandler mongoHandler = new MongoHandler();
        mongoHandler.mongoClientInstance();
        mongoHandler.getCollections(competitionTitle);
        System.out.println(ANSI_RED + "______________________________________");
        //................................
        Queue<Submission> submissions = mongoHandler.readSubmissions();
        System.out.println(ANSI_YELLOW + "______________________________________");
        HashMap<ObjectId, BsonDocument> problems = mongoHandler.readProblems();
        Main.users = mongoHandler.getUsers();
        Main.c_participants = new HashMap<>();
        Main.mapGrade_a = new HashMap<>();
        Main.mapGrade_b = new HashMap<>();
        Main.mapRegion_a = new HashMap<>();
        Main.mapRegion_b = new HashMap<>();
        Main.listGrade_a = new ArrayList<>();
        Main.listGrade_b = new ArrayList<>();
        listGrade_Debug = new ArrayList<>();
        ObjectId a = new ObjectId();
        ObjectId b = new ObjectId();
        System.out.println(a + "\n" + b);
        System.out.println(ANSI_GREEN + "______________________________________" + ANSI_RESET);
        checkSubmissions(problems, submissions);
        outputTest();
        program_ids();
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

    private static void userUpdate(ObjectId u_id, ObjectId p_id, int lt, int mark) {
        User u;
        try {
            u = Main.users.get(u_id);
            if(u == null) {
                //String msg = "User no found in Hashmap!";
                throw new Exception(" Main: Line 180 _ User not found in Hashmap!");
            }
        } catch (Exception e) {
            System.err.println(ANSI_YELLOW + e.getLocalizedMessage() + ANSI_RED);
            return;
        }
        try {
            if(u.getCompetition(competitionId) == null) {
                userFirstTimePreparation(p_id, lt, mark, u);
            }else {
                //DEBUG:........................................
                if(u.getRegion().equals("SPB")) {
                    //System.out.println("-------------------- ");
                    if (counter == -7) {
                        User uu;
                        int score;
                        int gR, gP;
                        int rR, rP;
                        String region_1;
                        int c = 0;
                        for (int i = 0; i < Main.listGrade_b.size(); i++) {
                            uu = Main.mapGrade_b.get(Main.listGrade_b.get(i));
                            score = uu.getCompetition(competitionId).getScore();
                            gR = uu.getCompetition(competitionId).getRank_in_grade();
                            gP = uu.getGradePosition();
                            rR = uu.getCompetition(competitionId).getRank_in_reg();
                            rP = uu.getRegionPosition();
                            region_1 = uu.getRegion();
                            if (region_1.equals("SPB")) {
                                c++;
                                System.out.println("Student_ID: " + uu.getId() + " | Score:" + score + " | Grade_Rank:" + gR + " | Grade_Position:" + gP + " | Region_Rank:" + rR + " | Region_Position: " + rP + " | Region:" + region_1);
                            }
                        }
                        System.out.println("Count:" + c);
                    }
                }
                //....................................
                int oldScore = u.getCompetition(competitionId).getScore();
                u.getCompetition(competitionId).addTask(p_id, lt, mark);
                int updateDirection = u.getCompetition(competitionId).getScore() - oldScore;
                if (!userRankUpdate(u, oldScore, updateDirection))
                    throw new Exception("Main: line 221 _ User's grade is not valid!");
            }
        } catch (Exception e) {
            System.err.println(e.getLocalizedMessage());
        }
    }

    private static void userFirstTimePreparation(ObjectId p_id, int lt, int mark, User u) throws Exception {
        u.updateRegion();
        Event event = new Event(eventId, eventTitle);
        Competition competition = new Competition(competitionId, competitionTitle);
        competition.addTask(p_id, lt, mark);
        event.addCompetition(competition);
        u.addEvent(event);
        String region;
        int regionLastRank;
        switch(u.getGrade()) {
            case grade_b:
                //preparation for regional rank
                region = u.getRegion();
                regionLastRank = Main.mapRegion_b.getOrDefault(region, -1);
                //if the region is not in our map (it is the first time)
                if(regionLastRank == -1){
                    // the last position in this region is 1 the current user
                    Main.mapRegion_b.put(region, 1);
                    u.getCompetition(competitionId).setRank_in_reg(1);
                    u.setRegionPosition(1);
                }else{
                    regionLastRank ++;
                    Main.mapRegion_b.put(region, regionLastRank);
                    u.getCompetition(competitionId).setRank_in_reg(regionLastRank);
                    u.setRegionPosition(regionLastRank);
                }
                //Update ranks
                Main.mapGrade_b.put(u.getId(), u);
                Main.listGrade_b.add(u.getId());
                u.getCompetition(competitionId).setRank_in_grade(Main.listGrade_b.size());
                u.setGradePosition(Main.listGrade_b.size());
                //DEBUG:................................................
                int[] tmp = new int[5];
                tmp[0]=u.getCompetition(competitionId).getScore();
                tmp[1]=u.getGradePosition();
                tmp[2]=u.getCompetition(competitionId).getRank_in_grade();
                tmp[3] = u.getRegionPosition();
                tmp[4] = u.getCompetition(competitionId).getRank_in_reg();
                //DEBUG:................................................
                listGrade_Debug.add(tmp);
                firstTimeRankUpdate(u, Main.listGrade_b, Main.mapGrade_b, region, Main.mapRegion_b);
                break;
            case grade_a:
                //preparation for regional rank
                region = u.getRegion();
                regionLastRank = Main.mapRegion_a.getOrDefault(region, -1);
                //if the region is not in our map (it is the first time)
                if(regionLastRank == -1){
                    // the last position in this region is 1 the current user
                    Main.mapRegion_a.put(region, 1);
                    u.getCompetition(competitionId).setRank_in_reg(1);
                    u.setRegionPosition(1);
                }else{
                    regionLastRank ++;
                    Main.mapRegion_a.put(region, regionLastRank);
                    u.getCompetition(competitionId).setRank_in_reg(regionLastRank);
                    u.setRegionPosition(regionLastRank);
                }
                //Update ranks
                Main.mapGrade_a.put(u.getId(), u);
                Main.listGrade_a.add(u.getId());
                u.getCompetition(competitionId).setRank_in_grade(Main.listGrade_a.size());
                u.setGradePosition(Main.listGrade_a.size());
                //DEBUG:................................................
                int[] tmp1 = new int[5];
                tmp1[0]=u.getCompetition(competitionId).getScore();
                tmp1[1]=u.getGradePosition();
                tmp1[2]=u.getCompetition(competitionId).getRank_in_grade();
                tmp1[3] = u.getRegionPosition();
                tmp1[4] = u.getCompetition(competitionId).getRank_in_reg();
                listGrade_Debug.add(tmp1);
                //.......................................................
                firstTimeRankUpdate(u, Main.listGrade_a, Main.mapGrade_a, region, Main.mapRegion_a);
                break;
            default:
                throw new IllegalStateException("Main: line 327 _ Unexpected value for participant's grade: " + u.getGrade());
        }
    }

    private static boolean userRankUpdate(User u, int oldScore, int updateDirection) {
        if(u.getGrade() == grade_b) {
            if(updateDirection > 0) {
                upwardRankUpdate(u, Main.listGrade_b, Main.mapGrade_b, u.getRegion(), Main.mapRegion_b, oldScore);
                return true;
            }
            if(updateDirection < 0){
                downwardRankUpdate(u, Main.listGrade_b, Main.mapGrade_b, u.getRegion(), Main.mapRegion_b, oldScore);
            }
            return true;
        }
        if(u.getGrade() == grade_a) {
            if(updateDirection > 0) {
                upwardRankUpdate(u, Main.listGrade_a, Main.mapGrade_a, u.getRegion(), Main.mapRegion_a, oldScore);
                return true;
            }
            if(updateDirection < 0){
                downwardRankUpdate(u, Main.listGrade_a, Main.mapGrade_a, u.getRegion(), Main.mapRegion_a, oldScore);
            }
            return true;
        }
        return false;
    }

    private static void firstTimeRankUpdate(@NotNull User u, @NotNull ArrayList<ObjectId> listGrade, HashMap<ObjectId,User> mapGrade, String region, HashMap<String, Integer> mapRegion) {
        int score = u.getCompetition(competitionId).getScore();
        // i = current pointer in the list(user's at first)
        int i = u.getGradePosition() - 1;
        int gradeRank = u.getCompetition(competitionId).getRank_in_grade();
        int regionRank = u.getCompetition(competitionId).getRank_in_reg();
        int rankBuffer = 0;
        User u_2;
        //............................................
        //new user, his position and rank is the last right now
        //....................................
        // as we are going upward we should check not to be the first!
        if(i > 0) { //if the pointer is not on the first user
            u_2 = mapGrade.get(listGrade.get(i - 1));

            /* if the new score is less than who is just before, we need
               just to take the current position as user's position
               rank should be automatically the same as the position
            if(score < u_2.getCompetition(competitionId).getScore()){
                listGrade.set(i, u.getId());
                u.setGradePosition(i + 1);
                //no need to set rank in grade!
                u.getCompetition(competitionId).setRank_in_reg(regionRank);
                u.setRegionPosition(regionRank);
                //DEBUG:.................................................
                int[] tmp = new int[3];
                tmp[0]=u.getCompetition(competitionId).getScore();
                tmp[1]=u.getGradePosition();
                tmp[2]=u.getCompetition(competitionId).getRank_in_grade();
                listGrade_Debug.set(i, tmp);
                //.......................................................
                return;
            }
            /* the new score is the same with the one who stands just before!
               we need to take the current position as users position
               and the rank of u-2 as his new rank!
            else if(score == u_2.getCompetition(competitionId).getScore()){
                listGrade.set(i, u.getId());
                u.setGradePosition(i + 1);
                u.getCompetition(competitionId)
                        .setRank_in_grade(u_2.getCompetition(competitionId).getRank_in_grade());
                u.getCompetition(competitionId).setRank_in_reg(regionRank);
                u.setRegionPosition(regionRank);
                //DEBUG:.................................................
                int[] tmp = new int[3];
                tmp[0]=u.getCompetition(competitionId).getScore();
                tmp[1]=u.getGradePosition();
                tmp[2]=u.getCompetition(competitionId).getRank_in_grade();
                listGrade_Debug.set(i, tmp);
                //.......................................................
                return;
            }
            //if (score > u_2.getCompetition(competitionId).getScore()) starts here:
            /* change the position and ranks as much as needed*/
            while(score > u_2.getCompetition(competitionId).getScore()){
                listGrade.set(i, u_2.getId()); // put u-2 at the current pointer
                u_2.getCompetition(competitionId).updateRank_in_grade(1); // his rank now is one more! of course!
                u_2.updateGradePosition(1); // the same store for the position

                if (u_2.getRegion().equals(region)) {
                    u_2.getCompetition(competitionId).updateRank_in_reg(1);
                    u_2.updateRegionPosition(1);
                    u.getCompetition(competitionId).updateRank_in_reg(-1);
                    u.updateRegionPosition(-1);
                    //regionRank--;
                }
                //DEBUG:........................................................
                int[] tmp = new int[5];
                tmp[0] = u_2.getCompetition(competitionId).getScore();
                tmp[1] = u_2.getGradePosition();
                tmp[2] = u_2.getCompetition(competitionId).getRank_in_grade();
                tmp[3] = u_2.getRegionPosition();
                tmp[4] = u_2.getCompetition(competitionId).getRank_in_reg();
                listGrade_Debug.set(i, tmp);
                //..............................................................
                // decreasing the pointer (grade position)
                i--;
                // now the rank in grade also should be declined
                u.getCompetition(competitionId).updateRank_in_grade(-1);
                //again, at current position rank and position should be the same:
                Assert.assertEquals(i + 1, u.getCompetition(competitionId).getRank_in_grade());
                if(i + 1 != u.getCompetition(competitionId).getRank_in_grade()){
                    System.out.println("kir toosh");
                }
                if (i == 0) {
                    //this user is first! congratulations!
                    listGrade.set(i, u.getId());
                    u.setGradePosition(i + 1);
                    //no need to set rank in grade!
                    //u.getCompetition(competitionId).setRank_in_reg(regionRank);
                    //u.setRegionPosition(regionRank);
                    //DEBUG:.................................................
                    int[] tmp1 = new int[5];
                    tmp1[0]=u.getCompetition(competitionId).getScore();
                    tmp1[1]=u.getGradePosition();
                    tmp1[2]=u.getCompetition(competitionId).getRank_in_grade();
                    tmp1[3] = u.getRegionPosition();
                    tmp1[4] = u.getCompetition(competitionId).getRank_in_reg();
                    listGrade_Debug.set(i, tmp1);
                    //.......................................................
                    return;
                }
                u_2 = mapGrade.get(listGrade.get(i - 1));
            }
            //ops, someone has a score equal or more than our user's

            /* again if the new score is the same with the one's score who
               stands just before, we need to take the current position as
               users position and the rank of u-2 as his new rank!*/
            if(score == u_2.getCompetition(competitionId).getScore()){
                listGrade.set(i, u.getId());
                u.setGradePosition(i + 1);
                u.getCompetition(competitionId)
                        .setRank_in_grade(u_2.getCompetition(competitionId).getRank_in_grade());
                //for regional ranking we need to check the region of people with the same score
                //the new regional rank shod be the same. or the same as position if we did not find any.
                if(u_2.getRegion().equals(region)) {
                    u.getCompetition(competitionId)
                            .setRank_in_reg(u_2.getCompetition(competitionId).getRank_in_reg());
                }else{
                    for(int j=i-2; j>=0; j--){
                        u_2 = mapGrade.get(listGrade.get(j));
                        if(score != u_2.getCompetition(competitionId).getScore()) {
                            //u.getCompetition(competitionId).setRank_in_reg(u.getRegionPosition());
                            break;
                        }
                        if(u_2.getRegion().equals(region)){
                            u.getCompetition(competitionId)
                                    .setRank_in_reg(u_2.getCompetition(competitionId).getRank_in_reg());
                            break;
                        }
                    }
                }
                //DEBUG:.................................................
                int[] tmp = new int[5];
                tmp[0]=u.getCompetition(competitionId).getScore();
                tmp[1]=u.getGradePosition();
                tmp[2]=u.getCompetition(competitionId).getRank_in_grade();
                tmp[3] = u.getRegionPosition();
                tmp[4] = u.getCompetition(competitionId).getRank_in_reg();
                listGrade_Debug.set(i, tmp);
                //.......................................................
                return;
            }
            /* just as before if the new score is less than what is just before,
               we need to take the current position as user's position
               rank should be automatically the same as the position*/
            else { //if(score < u_2.getCompetition(competitionId).getScore()){
                listGrade.set(i, u.getId());
                u.setGradePosition(i + 1);
                //no need to set rank in grade!
                //u.getCompetition(competitionId).setRank_in_reg(regionRank);
                //u.setRegionPosition(regionRank);
                //DEBUG:.................................................
                int[] tmp = new int[5];
                tmp[0]=u.getCompetition(competitionId).getScore();
                tmp[1]=u.getGradePosition();
                tmp[2]=u.getCompetition(competitionId).getRank_in_grade();
                tmp[3] = u.getRegionPosition();
                tmp[4] = u.getCompetition(competitionId).getRank_in_reg();
                listGrade_Debug.set(i, tmp);
                //.......................................................
                return;
            }
        }
        // here our use is already the first place, all his rank are already 1.
        // we have already increased the rank of other with the same previous score!
        // nothing to do then!
    }

    private static void upwardRankUpdate(@NotNull User u, @NotNull ArrayList<ObjectId> listGrade, HashMap<ObjectId, User> mapGrade, String region, HashMap<String, Integer> mapRegion, int oldScore) {
        int score = u.getCompetition(competitionId).getScore();
        // i = current pointer in the list(user's at first)
        int i = u.getGradePosition() - 1;
        int gradeRank = u.getCompetition(competitionId).getRank_in_grade();
        //int regionRank = u.getCompetition(competitionId).getRank_in_reg();
        int rankBuffer = 0;
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
                if(u_2.getCompetition(competitionId).getScore() != oldScore)
                    break;
                //n change in position
                u_2.getCompetition(competitionId).updateRank_in_grade(1);
                if(u_2.getRegion().equals(region)){
                    u_2.getCompetition(competitionId).updateRank_in_reg(1);
                    //regionRank --;
                    //u_2.updateRegionPosition(1);
                }
                //DEBUG:........................................................
                int[] tmp = new int[5];
                tmp[0]=u_2.getCompetition(competitionId).getScore();
                tmp[1]=u_2.getGradePosition();
                tmp[2]=u_2.getCompetition(competitionId).getRank_in_grade();
                tmp[3] = u_2.getRegionPosition();
                tmp[4] = u_2.getCompetition(competitionId).getRank_in_reg();
                listGrade_Debug.set(j + 1, tmp);
                //..............................................................
            }
        }

        // as we are going upward we should check not to be the first!
        if(i > 0) { //if the pointer is not on the first user
            u_2 = mapGrade.get(listGrade.get(i - 1));
            // now move the user to the top among those who had the same score todo: the next lines are redundant
            while (oldScore == u_2.getCompetition(competitionId).getScore()) {
                listGrade.set(i, u_2.getId());
                u_2.getCompetition(competitionId).updateRank_in_grade(1);
                u_2.updateGradePosition(1);
                if(u_2.getRegion().equals(region)){
                    u_2.getCompetition(competitionId).updateRank_in_reg(1);
                    u_2.updateRegionPosition(1);
                    u.updateRegionPosition(-1);
                    //regionRank --; the same as old score -> all rank are the same now
                }
                //DEBUG:........................................................
                int[] tmp = new int[5];
                tmp[0]=u_2.getCompetition(competitionId).getScore();
                tmp[1]=u_2.getGradePosition();
                tmp[2]=u_2.getCompetition(competitionId).getRank_in_grade();
                tmp[3] = u_2.getRegionPosition();
                tmp[4] = u_2.getCompetition(competitionId).getRank_in_reg();
                listGrade_Debug.set(i, tmp);
                //..............................................................
                // decreasing the pointer (grade position)
                // now gradeRank grade rank should not be changed as we are moving through the same rank
                i--;
                if (i == 0) {
                    //first place, waw!
                    listGrade.set(i, u.getId());
                    u.setGradePosition(i + 1);
                    //no need to set rank in grade!
                    Assert.assertEquals( 1, u.getCompetition(competitionId).getRank_in_grade());
                    if(1 != u.getCompetition(competitionId).getRank_in_grade()){
                        System.out.println("kir toosh");
                    }
                    Assert.assertEquals( 1, u.getCompetition(competitionId).getRank_in_reg());
                    //DEBUG:.................................................
                    int[] tmp1 = new int[5];
                    tmp1[0]=u.getCompetition(competitionId).getScore();
                    tmp1[1]=u.getGradePosition();
                    tmp1[2]=u.getCompetition(competitionId).getRank_in_grade();
                    tmp1[3] = u_2.getRegionPosition();
                    tmp1[4] = u_2.getCompetition(competitionId).getRank_in_reg();
                    listGrade_Debug.set(i, tmp1);
                    //.......................................................
                    return;
                }
                u_2 = mapGrade.get(listGrade.get(i - 1));
            }
            //Now the user position should be the first among those who have the same score as he did
            //then at current position rank and position should be the same:
            Assert.assertEquals(i + 1, u.getCompetition(competitionId).getRank_in_grade());
            // Assert.assertEquals(u.getRegionPosition(), u.getCompetition(competitionId).getRank_in_reg());
            if(i + 1 != u.getCompetition(competitionId).getRank_in_grade()){
                System.out.println("kir toosh");
            }
            /* if the new score is less than who is just before, we need
               just to take the current position as user's position
               rank should be automatically the same as the position
            if(score < u_2.getCompetition(competitionId).getScore()){
                listGrade.set(i, u.getId());
                u.setGradePosition(i + 1);
                //no need to set rank in grade!
                u.getCompetition(competitionId).setRank_in_reg(regionRank);
                u.setRegionPosition(regionRank);
                //DEBUG:.................................................
                int[] tmp = new int[3];
                tmp[0]=u.getCompetition(competitionId).getScore();
                tmp[1]=u.getGradePosition();
                tmp[2]=u.getCompetition(competitionId).getRank_in_grade();
                listGrade_Debug.set(i, tmp);
                //.......................................................
                return;
            }
            /* the new score is the same with the whone who stands just before!
               we need to take the current position as users position
               and the rank of u-2 as his new rank!
            else if(score == u_2.getCompetition(competitionId).getScore()){
                listGrade.set(i, u.getId());
                u.setGradePosition(i + 1);
                u.getCompetition(competitionId)
                        .setRank_in_grade(u_2.getCompetition(competitionId).getRank_in_grade());
                u.getCompetition(competitionId).setRank_in_reg(regionRank);
                u.setRegionPosition(regionRank);
                //DEBUG:.................................................
                int[] tmp = new int[3];
                tmp[0]=u.getCompetition(competitionId).getScore();
                tmp[1]=u.getGradePosition();
                tmp[2]=u.getCompetition(competitionId).getRank_in_grade();
                listGrade_Debug.set(i, tmp);
                //.......................................................
                return;
            }
            //if (score > u_2.getCompetition(competitionId).getScore()) starts here:
            /* change the position and ranks as much as needed*/
            while(score > u_2.getCompetition(competitionId).getScore()){
                listGrade.set(i, u_2.getId()); // put u-2 at the current pointer
                u_2.getCompetition(competitionId).updateRank_in_grade(1); // his rank now is one more! of course!
                u_2.updateGradePosition(1); // the same store for the position
                if (u_2.getRegion().equals(region)) {
                    u_2.getCompetition(competitionId).updateRank_in_reg(1);
                    u_2.updateRegionPosition(1);
                    u.getCompetition(competitionId).updateRank_in_reg(-1);
                    u.updateRegionPosition(-1);
                    //regionRank--;
                }
                //DEBUG:........................................................
                int[] tmp = new int[5];
                tmp[0] = u_2.getCompetition(competitionId).getScore();
                tmp[1] = u_2.getGradePosition();
                tmp[2] = u_2.getCompetition(competitionId).getRank_in_grade();
                tmp[3] = u_2.getRegionPosition();
                tmp[4] = u_2.getCompetition(competitionId).getRank_in_reg();
                listGrade_Debug.set(i, tmp);
                //..............................................................
                // decreasing the pointer (grade position)
                i--;
                // now the rank in grade also should be declined
                u.getCompetition(competitionId).updateRank_in_grade(-1);
                //again, at current position rank and position should be the same:
                Assert.assertEquals(i + 1, u.getCompetition(competitionId).getRank_in_grade());
                //Assert.assertEquals(u.getRegionPosition(), u.getCompetition(competitionId).getRank_in_reg());
                if(i + 1 != u.getCompetition(competitionId).getRank_in_grade()){
                    System.out.println("kir toosh");
                }
                if (i == 0) {
                    //this user is first! congratulations!
                    listGrade.set(i, u.getId());
                    u.setGradePosition(i + 1);
                    //no need to set rank in grade!
                    //u.getCompetition(competitionId).setRank_in_reg();
                    //u.setRegionPosition(regionRank);
                    //DEBUG:.................................................
                    int[] tmp1 = new int[5];
                    tmp1[0]=u.getCompetition(competitionId).getScore();
                    tmp1[1]=u.getGradePosition();
                    tmp1[2]=u.getCompetition(competitionId).getRank_in_grade();
                    tmp1[3] = u.getRegionPosition();
                    tmp1[4] = u.getCompetition(competitionId).getRank_in_reg();
                    listGrade_Debug.set(i, tmp1);
                    //.......................................................
                    return;
                }
                u_2 = mapGrade.get(listGrade.get(i - 1));
            }
            //ops, someone has a score equal or more than our user's

            /* again if the new score is the same with the one's score who
               stands just before, we need to take the current position as
               users position and the rank of u-2 as his new rank!*/
            if(score == u_2.getCompetition(competitionId).getScore()){
                listGrade.set(i, u.getId());
                u.setGradePosition(i + 1);
                u.getCompetition(competitionId)
                        .setRank_in_grade(u_2.getCompetition(competitionId).getRank_in_grade());
                //u.setRegionPosition(regionRank);
                if(u_2.getRegion().equals(region)) {
                    u.getCompetition(competitionId)
                            .setRank_in_reg(u_2.getCompetition(competitionId).getRank_in_reg());
                }else{
                    for(int j=i-2; j>=0; j--){
                        u_2 = mapGrade.get(listGrade.get(j));
                        if(score != u_2.getCompetition(competitionId).getScore()) {
                            //u.getCompetition(competitionId).setRank_in_reg(regionRank);
                            u.getCompetition(competitionId).setRank_in_reg(u.getRegionPosition()); //i+1
                            break;
                        }
                        if(u_2.getRegion().equals(region)){
                            u.getCompetition(competitionId)
                                    .setRank_in_reg(u_2.getCompetition(competitionId).getRank_in_reg());
                            break;
                        }
                    }
                }
                //DEBUG:.................................................
                int[] tmp = new int[5];
                tmp[0]=u.getCompetition(competitionId).getScore();
                tmp[1]=u.getGradePosition();
                tmp[2]=u.getCompetition(competitionId).getRank_in_grade();
                tmp[3] = u.getRegionPosition();
                tmp[4] = u.getCompetition(competitionId).getRank_in_reg();
                listGrade_Debug.set(i, tmp);
                //.......................................................
                return;
            }
            /* just as before if the new score is less than what is just before,
               we need to take the current position as user's position
               rank should be automatically the same as the position*/
            else { //if(score < u_2.getCompetition(competitionId).getScore()){
                listGrade.set(i, u.getId());
                u.setGradePosition(i + 1);
                //no need to set rank in grade!
                //u.getCompetition(competitionId).setRank_in_reg(u.getRegionPosition());
                //u.setRegionPosition(regionRank);
                //DEBUG:.................................................
                int[] tmp = new int[5];
                tmp[0]=u.getCompetition(competitionId).getScore();
                tmp[1]=u.getGradePosition();
                tmp[2]=u.getCompetition(competitionId).getRank_in_grade();
                tmp[3] = u.getRegionPosition();
                tmp[4] = u.getCompetition(competitionId).getRank_in_reg();
                listGrade_Debug.set(i, tmp);
                //.......................................................
                return;
            }
        }
        // here our use is already the first place, all his rank are already 1.
        // we have already increased the rank of other with the same previous score!
        // nothing to do then!
        //DEBUG:........................................................
        int[] tmp = new int[5];
        tmp[0] = u.getCompetition(competitionId).getScore();
        tmp[1] = u.getGradePosition();
        tmp[2] = u.getCompetition(competitionId).getRank_in_grade();
        tmp[3] = u.getRegionPosition();
        tmp[4] = u.getCompetition(competitionId).getRank_in_reg();
        listGrade_Debug.set(i, tmp);
        //..............................................................
    }

    private static void downwardRankUpdate(@NotNull User u, @NotNull ArrayList<ObjectId> listGrade, HashMap<ObjectId, User> mapGrade, String region, HashMap<String, Integer> mapRegion, int oldScore) {
        int score = u.getCompetition(competitionId).getScore();
        // i = current pointer in the list(user's at first)
        int i = u.getGradePosition() - 1;
        int gradeRank = u.getCompetition(competitionId).getRank_in_grade();
        //int regionRank = u.getRegionPosition();
        int rankBuffer = 0;
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
            while (oldScore == u_2.getCompetition(competitionId).getScore()) {
                listGrade.set(i, u_2.getId());
                //no update fo rank of u_2 is needed as we are moving along the same ranks
                u_2.updateGradePosition(-1);
                if (u_2.getRegion().equals(region)) {
                    //u_2.getCompetition(competitionId).updateRank_in_reg(-1);
                    u_2.updateRegionPosition(-1);
                    u.updateRegionPosition(1);
                    //regionRank++;
                }
                //DEBUG:........................................................
                int[] tmp = new int[5];
                tmp[0] = u_2.getCompetition(competitionId).getScore();
                tmp[1] = u_2.getGradePosition();
                tmp[2] = u_2.getCompetition(competitionId).getRank_in_grade();
                tmp[3] = u_2.getRegionPosition();
                tmp[4] = u_2.getCompetition(competitionId).getRank_in_reg();
                listGrade_Debug.set(i, tmp);
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
                    u.getCompetition(competitionId).setRank_in_grade(i + 1);
                    u.getCompetition(competitionId).setRank_in_reg(u.getRegionPosition());//absolute last person: postion and rank are the same
                    //u.setRegionPosition(regionRank);
                    //DEBUG:.................................................
                    int[] tmp1 = new int[5];
                    tmp1[0] = u.getCompetition(competitionId).getScore();
                    tmp1[1] = u.getGradePosition();
                    tmp1[2] = u.getCompetition(competitionId).getRank_in_grade();
                    tmp1[3] = u.getRegionPosition();
                    tmp1[4] = u.getCompetition(competitionId).getRank_in_reg();
                    listGrade_Debug.set(i, tmp1);
                    //.......................................................
                    return;
                }
                u_2 = mapGrade.get(listGrade.get(i + 1));
            }
            //........................................................................................
            //........................................................................................
            //Now the user position should be the last among those who have the same score as he did
            //then for u_2 we should have:
            Assert.assertEquals(i + 2, u_2.getCompetition(competitionId).getRank_in_grade());
            if(i + 2 != u_2.getCompetition(competitionId).getRank_in_grade()){
                System.out.println("kir toosh");
            }
            /* if the new score is more than who is just after, we need
               just to take the current position as user's position
               rank is one less than the rank of the next user*/
//            if (score > u_2.getCompetition(competitionId).getScore()) {
//                listGrade.set(i, u.getId());
//                u.setGradePosition(i + 1);
//                //u.getCompetition(competitionId).setRank_in_grade(i + 1);
//                u.getCompetition(competitionId)
//                        .setRank_in_grade(u_2.getCompetition(competitionId).getRank_in_grade()-1);
//                u.getCompetition(competitionId).setRank_in_reg(regionRank);
//                u.setRegionPosition(regionRank);
//                //DEBUG:.................................................
//                int[] tmp = new int[3];
//                tmp[0] = u.getCompetition(competitionId).getScore();
//                tmp[1] = u.getGradePosition();
//                tmp[2] = u.getCompetition(competitionId).getRank_in_grade();
//                listGrade_Debug.set(i, tmp);
//                //.......................................................
//                return;
//            }
            //if (score < u_2.getCompetition(competitionId).getScore()) starts here:
            /* new score is less! change the position and ranks as much as needed*/
            while (score < u_2.getCompetition(competitionId).getScore()) {
                listGrade.set(i, u_2.getId()); // put u-2 at the current pointer
                u_2.getCompetition(competitionId).updateRank_in_grade(-1); // his rank now is one less! of course!
                u_2.updateGradePosition(-1); // the same story for the position
                if (u_2.getRegion().equals(region)) {
                    u_2.getCompetition(competitionId).updateRank_in_reg(-1);
                    u_2.updateRegionPosition(-1);
                    u.updateRegionPosition(1);
                    u.getCompetition(competitionId).setRank_in_reg(u.getRegionPosition());
                }
                //DEBUG:........................................................
                int[] tmp = new int[5];
                tmp[0] = u_2.getCompetition(competitionId).getScore();
                tmp[1] = u_2.getGradePosition();
                tmp[2] = u_2.getCompetition(competitionId).getRank_in_grade();
                tmp[3] = u_2.getRegionPosition();
                tmp[4] = u_2.getCompetition(competitionId).getRank_in_reg();
                listGrade_Debug.set(i, tmp);
                //..............................................................
                // increasing the pointer (grade position)
                i++;
                // now the rank in grade is the same as position!
                u.getCompetition(competitionId).setRank_in_grade(i + 1);
                if (i == listGrade.size() - 1) {
                    //last place, :(
                    listGrade.set(i, u.getId());
                    u.setGradePosition(i + 1);
                    // his last score was the same as one above! now his score decreased!
                    // he is the absolute last person of this competition! booooooo
                    //u.getCompetition(competitionId).setRank_in_grade(i + 1);// we just did
                    //u.getCompetition(competitionId).setRank_in_reg(u.getRegionPosition());
                    //u.setRegionPosition(regionRank);
                    //DEBUG:.................................................
                    int[] tmp1 = new int[5];
                    tmp1[0] = u.getCompetition(competitionId).getScore();
                    tmp1[1] = u.getGradePosition();
                    tmp1[2] = u.getCompetition(competitionId).getRank_in_grade();
                    tmp1[3] = u.getRegionPosition();
                    tmp1[4] = u.getCompetition(competitionId).getRank_in_reg();
                    listGrade_Debug.set(i, tmp1);
                    //.......................................................
                    return;
                }
                u_2 = mapGrade.get(listGrade.get(i + 1));
            }
            /* the new score is the same with the one who stands just after!
               we need to take the current position as users position
               and rank! we should update all others with the same score
               by this new rank*/
            if (score == u_2.getCompetition(competitionId).getScore()) {
                listGrade.set(i, u.getId());
                u.setGradePosition(i + 1);
                Assert.assertEquals(u.getGradePosition(), u_2.getCompetition(competitionId).getRank_in_grade()-1);
                if((u.getGradePosition() != u_2.getCompetition(competitionId).getRank_in_grade()-1)){
                    System.out.println("kir toosh");
                }
                u.getCompetition(competitionId).setRank_in_grade(i + 1);// we just did - or we did not!! :D
                u.getCompetition(competitionId).setRank_in_reg(u.getRegionPosition());
                //u.setRegionPosition(regionRank);
                //DEBUG:.................................................
                int[] tmp = new int[5];
                tmp[0] = u.getCompetition(competitionId).getScore();
                tmp[1] = u.getGradePosition();
                tmp[2] = u.getCompetition(competitionId).getRank_in_grade();
                tmp[3] = u.getRegionPosition();
                tmp[4] = u.getCompetition(competitionId).getRank_in_reg();
                listGrade_Debug.set(i, tmp);
                //.......................................................
                // update the others with the same score: now their rank is improved by one
                //if(i < listGrade.size() - 1) { //if the pointer is not on the last user- no need we did before
                u_2.getCompetition(competitionId).updateRank_in_grade(-1);
                if(u_2.getRegion().equals(region)) {
                    u_2.getCompetition(competitionId).updateRank_in_reg(-1);
                }

                //DEBUG:.................................................
                int[] tmp1 = new int[5];
                tmp1[0] = u_2.getCompetition(competitionId).getScore();
                tmp1[1] = u_2.getGradePosition();
                tmp1[2] = u_2.getCompetition(competitionId).getRank_in_grade();
                tmp1[3] = u_2.getRegionPosition();
                tmp1[4] = u_2.getCompetition(competitionId).getRank_in_reg();
                listGrade_Debug.set(i + 1, tmp1);
                //.......................................................
                for (int j = i + 1 ; j < listGrade.size() - 1; j++) {
                    u_2 = mapGrade.get(listGrade.get(j + 1));
                    if(u_2.getCompetition(competitionId).getScore() != score)
                        return;
                    //no change in position
                    u_2.getCompetition(competitionId).updateRank_in_grade(-1);
                    if(u_2.getRegion().equals(region)) {
                        u_2.getCompetition(competitionId).updateRank_in_reg(-1);
                    }
                    //DEBUG:.................................................
                    int[] tmp2 = new int[5];
                    tmp2[0] = u_2.getCompetition(competitionId).getScore();
                    tmp2[1] = u_2.getGradePosition();
                    tmp2[2] = u_2.getCompetition(competitionId).getRank_in_grade();
                    tmp2[3] = u_2.getRegionPosition();
                    tmp2[4] = u_2.getCompetition(competitionId).getRank_in_reg();
                    listGrade_Debug.set(j + 1, tmp2);
                    //........................................................
                }
                return;
            }
            /* if the new score is more than who is just after, we need
               just to take the current position and rank as user's position
               rank is one less than the rank of the next user*/
            if (score > u_2.getCompetition(competitionId).getScore()) {
                listGrade.set(i, u.getId());
                u.setGradePosition(i + 1);
                u.getCompetition(competitionId).setRank_in_grade(i + 1);
                u.getCompetition(competitionId).setRank_in_reg(u.getRegionPosition());
                // we already have set the grade rank to be i+1, these 2 value should be the same.
                //u.getCompetition(competitionId).setRank_in_grade(u_2.getCompetition(competitionId).getRank_in_grade()-1);
                Assert.assertEquals(u.getCompetition(competitionId).getRank_in_grade(), u_2.getCompetition(competitionId).getRank_in_grade()-1);
                if((u.getCompetition(competitionId).getRank_in_grade() != u_2.getCompetition(competitionId).getRank_in_grade()-1)){
                    System.out.println("kir toosh");
                }
                //DEBUG:.................................................
                int[] tmp = new int[5];
                tmp[0] = u.getCompetition(competitionId).getScore();
                tmp[1] = u.getGradePosition();
                tmp[2] = u.getCompetition(competitionId).getRank_in_grade();
                tmp[3] = u.getRegionPosition();
                tmp[4] = u.getCompetition(competitionId).getRank_in_reg();
                listGrade_Debug.set(i, tmp);
                //.......................................................
                return;
            }
        }
        // OK, he was already the last one more negative point!
        // he is the alone person at the bottom
        else{
            listGrade.set(i, u.getId());
            u.setGradePosition(i + 1);
            // his last score is the same as one above! now his score decreased!
            // he is the absolute last person of this competition! booooooo
            u.getCompetition(competitionId).setRank_in_grade(i + 1);
            u.getCompetition(competitionId).setRank_in_reg(u.getRegionPosition());
            //u.setRegionPosition(regionRank);
            //DEBUG:.................................................
            int[] tmp = new int[5];
            tmp[0] = u.getCompetition(competitionId).getScore();
            tmp[1] = u.getGradePosition();
            tmp[2] = u.getCompetition(competitionId).getRank_in_grade();
            tmp[3] = u.getRegionPosition();
            tmp[4] = u.getCompetition(competitionId).getRank_in_reg();
            listGrade_Debug.set(i, tmp);
            //.......................................................
            return;
        }
    }

    private static void outputTest() {
        User u;
        int score;
        int gR,gP;
        int rR,rP;
        String region;
        int c =0;
        System.out.println(Main.listGrade_a.size());
        for(int i =0; i<105; i++){
            u = Main.mapGrade_a.get(Main.listGrade_a.get(i));
            score = u.getCompetition(competitionId).getScore();
            gR = u.getCompetition(competitionId).getRank_in_grade();
            gP = u.getGradePosition();
            rR = u.getCompetition(competitionId).getRank_in_reg();
            rP = u.getRegionPosition();
            region = u.getRegion();
            if(region.equals("NVS")) {
                c++;
                System.out.println("Student_ID: " + u.getId() + " | Score:" + score + " | Grade_Rank:" + gR + " | Grade_Position:" + gP + " | Region_Rank:" + rR + " | Region_Position: " + rP + " | Region:" + region);
            }
        }
        System.out.println("Count:" + c);
        System.out.println("*********************** yeaaaaahhhhh ***********************************************************");
    }

    private static void program_ids() {
        User u;
        int score;
        int gR,gP;
        int rR,rP;
        String region;
        int c =0;
        for(int i =0; i<10; i++){
            u = Main.mapGrade_b.get(Main.listGrade_b.get(i));
            score = u.getCompetition(competitionId).getScore();
            gR = u.getCompetition(competitionId).getRank_in_grade();
            gP = u.getGradePosition();
            rR = u.getCompetition(competitionId).getRank_in_reg();
            rP = u.getRegionPosition();
            region = u.getRegion();
            if(region.equals("NVS"))
                c++;
            System.out.println("Student_ID: " + u.getId() + " | Score:" + score + " | Grade_Rank:" + gR + " | Grade_Position:" + gP + " | Region_Rank:" + rR + " | Region_Position: " + rP + " | Region:" + region);
            u.getCompetition(competitionId).getTasks().forEach((key, val)->
                    System.out.print( "pid: " + key + " ,ans: " + val[1] + " | "));
            System.out.println();
        }
        System.out.println("Count:" + c);
        System.out.println("*********************** yeaaaaahhhhh ***********************************************************");
    }
}