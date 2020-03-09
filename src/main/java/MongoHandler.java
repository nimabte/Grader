import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Indexes;
import org.bson.BsonNull;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import static com.mongodb.client.model.Sorts.ascending;
import com.mongodb.client.AggregateIterable;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.orderBy;
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
    private CodecRegistry codecRegistry;
    MongoCollection<Submission> submissionMongoCollection;


    void mongoClientInstance() {
        ConnectionString connectionString = new ConnectionString(uri);

        // create codec registry for POJOs
         pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
         codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);

        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .applicationName("Grader")
                .codecRegistry(codecRegistry)
                .applyToConnectionPoolSettings(
                        builder -> builder.maxWaitTime(1000, TimeUnit.MILLISECONDS))
                .build();

        mongoClient = MongoClients.create(mongoClientSettings);

//        Assert.assertNotNull(mongoClient);
//        MongoClient mongoClient = MongoClients.create(uri);
    }

    void documentInstance() {
//        mongoClient = MongoClients.create(uri);

        // get handle to "bebras" database
        database = mongoClient.getDatabase("bebras");
//        database = mongoClient.getDatabase("bebras").withCodecRegistry(codecRegistry);
        //database = database.withCodecRegistry(pojoCodecRegistry);

        // get a handle to the "bebras17-3-4" collection
        MongoCollection<Submission> collection = database.getCollection("bebras17-3-4", Submission.class);

//        Submission somebody = collection.find().first();
//        System.out.println(somebody);

        // get all the documents in the collection and print them out
        Consumer<Submission> submissionConsumer = new Consumer<Submission>() {
            @Override
            public void accept(Submission submission) {
                System.out.println(submission);
            }
        };
//
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
    public  void getCollections(){
        database = mongoClient.getDatabase("bebras");
        submissionMongoCollection =  database.getCollection("bebras17-3-4", Submission.class);
    }
    List<Submission> readSubmissions(){
        List<Submission> subs = new LinkedList<>();
        Bson queryFilter = and(ne("pid", null));
       // submissionMongoCollection.find().sort(orderBy(ascending("x", "y"))).limit(10000).
//        submissionMongoCollection.find(queryFilter).sort(orderBy(ascending("st"))).
//        forEach((Consumer<Submission>) submission -> {
//                    //System.out.println(submission);
//                    //System.out.println(subs.add(submission));
//                    subs.add(submission);
//                });


//        AggregateIterable<Submission> aggregateIterable;
//        aggregateIterable = submissionMongoCollection.aggregate
//                (Arrays.asList(sort(ascending("st")))).allowDiskUse(true);
//        aggregateIterable  = submissionMongoCollection.aggregate(Arrays.asList(sort(ascending("st")), match(ne("pid",
//                new BsonNull()))));
        // to sole the error 96, (Not enough RAM)
        submissionMongoCollection.createIndex(Indexes.ascending("st"));
        FindIterable<Submission> findIterable = submissionMongoCollection.find(queryFilter);
//        findIterable = findIterable.sort(orderBy(ascending("st")));
        findIterable = findIterable.sort(Indexes.ascending("st"));
        MongoCursor<Submission> cursor = findIterable.iterator();
        try {
            while (cursor.hasNext()) {
                //Submission s = cursor.next();
                //System.out.println(s);
                //subs.add(s);
                subs.add(cursor.next());
            }
        } finally {
            cursor.close();
        }
        return subs;
    }
}
