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
        List<Problem> problems = mongoHandler.readProblems();
        System.out.println(problems.size());
        System.out.println(problems.get(0));
        System.out.println(problems.get(problems.size()-1));
        System.out.println(submissions.size());

//        for(int i = 0; i<submissions.size();i++){
//            System.out.println(submissions.get(i));
//        }


        System.out.println("press enter! \n ");
//        System.in.read();
    }

}
