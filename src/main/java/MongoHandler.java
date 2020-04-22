import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Indexes;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.ascending;

import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Sorts.orderBy;
import static com.mongodb.client.model.Updates.set;
import static org.bson.codecs.configuration.CodecRegistries.*;

public class MongoHandler {
    private MongoClient mongoClient;
    private MongoDatabase database;
    //private MongoCollection<Document> usersCollection;
    private static String uri = "mongodb+srv://analytics:analytics-password@mflix-tfkan.mongodb.net/test?retryWrites=true&w=majority";
    private Document document;
    private Bson bson;
    private CodecRegistry pojoCodecRegistry;
    private CodecRegistry codecRegistry;
    private MongoCollection<Submission> submissionCollection;
    private ArrayList<MongoCollection<Submission>> submissionCollections;
    private MongoCollection<Problem> problemCollection;
    private CodecRegistry codecRegistryUser;
    private MongoCollection<User> userCollection;



    public static int testFunction(){
        return 0;
    }
    void mongoClientInstance() {
        ConnectionString connectionString = new ConnectionString(uri);
        // establish the use of our new custom codec
        UserCodec userCodec = new UserCodec();
        // create a codec registry with this codec
        codecRegistryUser =
                fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), fromCodecs(userCodec));
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

    void userGrade2Int() {
        mongoClient = MongoClients.create(uri);

        // get handle to "bebras" database
        database = mongoClient.getDatabase("bebras");
//        database = mongoClient.getDatabase("bebras").withCodecRegistry(codecRegistry);
        //database = database.withCodecRegistry(pojoCodecRegistry);

        // get a handle to the "bebras17_3_4" collection
        //MongoCollection<Submission> collection = database.getCollection("bebras17_3_4", Submission.class);

//        Submission somebody = collection.find().first();
//        System.out.println(somebody);

        // get all the documents in the collection and print them out
//        Consumer<Submission> submissionConsumer = new Consumer<Submission>() {
//            @Override
//            public void accept(Submission submission) {
//                System.out.println(submission);
//            }
//        };
////
//        collection.find().forEach(submissionConsumer);


        MongoIterable<String> collections = database.listCollectionNames();
        for (String collectionName: collections) {
            System.out.println(collectionName);
        }

        MongoCollection<Document> usersCol = database.getCollection("users");

//        document = new Document("name", new Document("first", "NIMA").append("last", "bte"));
//        usersCollection.insertOne(document);
//        ObjectId oi = new ObjectId("5a0873e4fdb1771e6fc06fbd");
//        System.out.println(usersCollection);
//        Bson queryFilter = and(eq("u", oi));
//        Bson queryFilter = and(eq("u", oi));
//        Document u =
//                usersCol
//                        .find()
//                        // this feels much more declarative
//                        .projection(fields(include("_id", "grade")))
//                        .iterator()
//                        .tryNext();
//        System.out.println(u);

        usersCol.find().projection(fields(include("_id", "grade"))).forEach((Consumer<Document>) doc -> {
            String st = (String) doc.get("grade");
            if(st != null) {
                int obj = Integer.parseInt(st);
                //doc.put("grade", obj);
                usersCol.updateOne(doc, set("grade",obj));
            }
            System.out.println(doc.toJson());
        });

//        FindIterable<Document> fit = usersCollection.find();
//        fit.forEach((Consumer<Document>) System.out::println);


    }
    public  void getCollections(){
        database = mongoClient.getDatabase("bebras");
        //submissionCollection =  database.getCollection("bebras17_3_4", Submission.class);
        problemCollection =  database.getCollection("problems", Problem.class);
        userCollection =  database.getCollection("users", User.class).withCodecRegistry(codecRegistryUser);
        //Consumer<User> userConsumer = new Consumer<User>;
//        User u =
//                userCollection
//                .find()
//                        // this feels much more declarative
//                        .projection(fields(include("_id", "u", "st", "a")))
//                        .iterator()
//                        .tryNext();
//        System.out.println(u);
    }

    public void getCompetitions(){
        submissionCollections = new ArrayList<>();
        //submissionCollections.add( database.getCollection(competitionTitle, Submission.class));
        submissionCollections.add( database.getCollection("bebras17_3_4", Submission.class));
        submissionCollections.add( database.getCollection("bebras17_5_6", Submission.class));
        //submissionCollection = database.getCollection(competitionTitle, Submission.class);
        //submissionCollection = submissionCollections.get(0);
    }

    Queue<Submission> readSubmissions(){
        Queue<Submission> submissionQueue = new ArrayDeque<>();
        Bson queryFilter = and(ne("pid", null));
        for (MongoCollection<Submission> collection : submissionCollections) {
            //collection.createIndex(Indexes.ascending("st"));
            FindIterable<Submission> findIterable =
                    collection
                            .find(queryFilter)
                            .sort(Indexes.ascending("st"));
            try (MongoCursor<Submission> cursor = findIterable.iterator()) {
                while (cursor.hasNext()) {
                    submissionQueue.add(cursor.next());
                }
            }
        }
        return submissionQueue;
    }

    Queue<Submission> readSubmission(){
        Queue<Submission> submissionQueue = new ArrayDeque<>();
//        Queue<Submission> submissionQueue2 = new LinkedList<>();
        Bson queryFilter = and(ne("pid", null));
//        submissionCollection.find().sort(orderBy(ascending("x", "y"))).limit(10000).
//        submissionCollection.find(queryFilter).sort(orderBy(ascending("st"))).
//        forEach((Consumer<Submission>) submission -> {
//                    //System.out.println(submission);
//                    //System.out.println(subs.add(submission));
//                    subs.add(submission);
//                });


//        AggregateIterable<Submission> aggregateIterable;
//        aggregateIterable = submissionCollection.aggregate
//                (Arrays.asList(sort(ascending("st")))).allowDiskUse(true);
//        aggregateIterable  = submissionCollection.aggregate(Arrays.asList(sort(ascending("st")), match(ne("pid",
//                new BsonNull()))));
        // to sole the error 96, (Not enough RAM)
        submissionCollection.createIndex(Indexes.ascending("st"));
        FindIterable<Submission> findIterable =
                submissionCollection
                        .find(queryFilter)
//                      .sort(orderBy(ascending("st")));
                        .sort(Indexes.ascending("st"));

        MongoCursor<Submission> cursor = findIterable.iterator();
        try {
            while (cursor.hasNext()) {
                //Submission s = cursor.next();
                //System.out.println(s);
                //subs.add(s);
                submissionQueue.add(cursor.next());
            }
        } finally {
            cursor.close();
        }
        return submissionQueue;
    }

    HashMap<ObjectId, BsonDocument> readProblems(){
        List<Problem> problemList = new ArrayList<>();
        HashMap<ObjectId, BsonDocument> problemHashMap = new HashMap<>();
        FindIterable<Problem> findIterable = problemCollection.find();
        MongoCursor<Problem> cursor = findIterable.iterator();
        try {
            while (cursor.hasNext()) {
//                System.out.println(cursor.next());
//                problemList.add(p);
                Problem p = cursor.next();
                problemHashMap.put(p.getId(),p.getP());
//                System.out.println(problemHashMap.get(p.getId()));
//                System.out.println("\n-----\n");
//                System.out.println(p);
//                System.out.println("\n **************************************\n");
//                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //System.out.println(problemHashMap);
            cursor.close();
        }
        return problemHashMap;
    }

    HashMap<ObjectId, User> getUsers(){
        List<Problem> userList = new ArrayList<>();
        HashMap<ObjectId, User> userHashMap = new HashMap<>();
        FindIterable<User> findIterable = userCollection.find();
        MongoCursor<User> cursor = findIterable.iterator();
        try {
            while (cursor.hasNext()) {
//                System.out.println(cursor.next());
//                problemList.add(p);
                User u = cursor.next();
                userHashMap.put(u.getId(), u);
//                System.out.println(u);
//                System.out.println("**************************************");
//                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //System.out.println(problemHashMap);
            cursor.close();
        }
        return userHashMap;
    }
}
