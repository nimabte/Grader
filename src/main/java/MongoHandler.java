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
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import static java.util.Arrays.asList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoHandler {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> usersCollection;
    private static String uri = "mongodb+srv://analytics:analytics-password@mflix-tfkan.mongodb.net/test?retryWrites=true&w=majority";
    private Document document;
    private Bson bson;
    private CodecRegistry pojoCodecRegistry;

    void mongoClientInstance() {
        ConnectionString connectionString = new ConnectionString(uri);
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applicationName("Grader")
                .applyToConnectionPoolSettings(
                        builder -> builder.maxWaitTime(1000, TimeUnit.MILLISECONDS))
                .build();

        mongoClient = MongoClients.create(mongoClientSettings);

        // create codec registry for POJOs
        pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

//        Assert.assertNotNull(mongoClient);
//        MongoClient mongoClient = MongoClients.create(uri);
    }

    void documentInstance() {
//        mongoClient = MongoClients.create(uri);

        // get handle to "bebras" database
        database = mongoClient.getDatabase("bebras").withCodecRegistry(pojoCodecRegistry);
        //database = database.withCodecRegistry(pojoCodecRegistry);

        // get a handle to the "bebras17-3-4" collection
        MongoCollection<Submission> collection = database.getCollection("bebras17-3-4", Submission.class);

        //Submission somebody = collection.find().first();
        //System.out.println(somebody);

        // get all the documents in the collection and print them out
        Consumer<Submission> submissionConsumer = new Consumer<Submission>() {
            @Override
            public void accept(Submission submission) {
                System.out.println(submission);
            }
        };

        collection.find().forEach(submissionConsumer);

//        MongoIterable<String> collections = database.listCollectionNames();
//        for (String collectionName: collections) {
//            System.out.println(collectionName);
//        }

//        usersCollection = database.getCollection("bebras17-3-4");

//        document = new Document("name", new Document("first", "NIMA").append("last", "bte"));
//        usersCollection.insertOne(document);
//        ObjectId oi = new ObjectId("5a0873e4fdb1771e6fc06fbd");
//        System.out.println(usersCollection);
//        Bson queryFilter = and(eq("u", oi));
//        Bson queryFilter = and(eq("u", oi));
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

//        FindIterable<Document> fit = usersCollection.find();
//        fit.forEach((Consumer<Document>) System.out::println);


    }

}
