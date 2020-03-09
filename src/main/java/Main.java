import java.util.List;
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
        List<Submission> submissions = mongoHandler.readSubmissions();
        System.out.println(submissions.size());
        System.out.println(submissions.get(0));
        System.out.println(submissions.get(submissions.size()-1));



        System.out.println("press enter! \n ");
//        System.in.read();
    }

}
