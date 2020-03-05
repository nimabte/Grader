import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.*;

public class MongoHandler {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> usersCollection;
    private static String uri = "mongodb+srv://analytics:analytics-password@mflix-tfkan.mongodb.net/test?retryWrites=true&w=majority";
    private Document document;
    private Bson bson;

    void mongoClientInstance() {
        ConnectionString connectionString = new ConnectionString(uri);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applicationName("mflix")
                .applyToConnectionPoolSettings(
                        builder -> builder.maxWaitTime(1000, TimeUnit.MILLISECONDS))
                .build();

        mongoClient = MongoClients.create(mongoClientSettings);

//        Assert.assertNotNull(mongoClient);
//        MongoClient mongoClient = MongoClients.create(uri);
    }

    void documentInstance() {
        mongoClient = MongoClients.create(uri);
        database = mongoClient.getDatabase("bebras");

//        MongoIterable<String> collections = database.listCollectionNames();
//        for (String collectionName: collections) {
//            System.out.println(collectionName);
//        }
        usersCollection = database.getCollection("bebras17-3-4");

//        document = new Document("name", new Document("first", "NIMA").append("last", "bte"));
//        usersCollection.insertOne(document);
        ObjectId oi = new ObjectId("5a0873e4fdb1771e6fc06fbd");
        System.out.println(usersCollection);
//        Bson queryFilter = and(eq("u", oi));
        Bson queryFilter = and(eq("u", oi));
//        Document submissionData17 =
//                usersCollection
//                        .find()
//                        // this feels much more declarative
//                        .projection(fields(include("_id", "u", "st", "a")))
//                        .iterator()
//                        .tryNext();
//        System.out.println(submissionData17);

//        usersCollection.find().forEach((Consumer<Document>) doc ->
//                System.out.println(doc.toJson()));
        FindIterable<Document> fit = usersCollection.find();
        fit.forEach((Consumer<Document>) System.out::println);


    }

}
