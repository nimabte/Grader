import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) throws Exception{
        System.out.println("Hello\n this is nima kiiir");
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.WARNING);
        MongoHandler mongoHandler = new MongoHandler();
        mongoHandler.mongoClientInstance();
        mongoHandler.documentInstance();

        System.out.println("press enter! \n ");
//        System.in.read();
    }

}
