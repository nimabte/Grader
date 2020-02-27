import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.concurrent.TimeUnit;

public class MongoHandler {
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> collection;
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
        database = mongoClient.getDatabase("test");
        collection = database.getCollection("users");

    /*
    The basic data structures in MongoDB are documents. The document
    model is what we consider to be the best way to represent data.
    Using documents, makes your data definition as close as possible to
    your OOP object models.

    Since we are dealing with an Object-Oriented Programing language (OOP)
    like Java, having a class that expresses the documents structure,
    becomes imperative.
    */

        document = new Document("name", new Document("first", "NIMA").append("last", "bte"));

    /*
    This document defines a MongoDB document that looks like this in its
    json format:

     {
        "name": {
                "first": "Norberto",
                "last": "Leite"
        }
     }
    */
        collection.insertOne(document);
    /*
    We use documents for everything in MongoDB.
    - define data objects
    - define queries
    - define update operations
    - define configuration settings
    ...

    At the Java layer we have the Document class but also the Bson class.
    The Document class implements the Bson interface, because Documents
    are BSON data structures.
    */

//        Assert.assertTrue(document instanceof Bson);

    /*
    We will also use instances of Bson, throughout the course, to define
    fine tune aspects of our queries like query operators and aggregation
    stages. More on that in the next lectures.
    */

    }
}
