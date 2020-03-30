import org.bson.BsonReader;
import org.bson.BsonString;
import org.bson.BsonWriter;
import org.bson.Document;
import org.bson.codecs.*;
import org.bson.types.ObjectId;

import java.util.ArrayList;

public class UserCodec implements CollectibleCodec<User> {

  private final Codec<Document> documentCodec;

  public UserCodec() {
    super();
    this.documentCodec = new DocumentCodec();
  }

  public void encode(
          BsonWriter bsonWriter, User u, EncoderContext encoderContext) {

    Document userDoc = new Document();
    ObjectId u_id = u.getId();
    String role = u.getRole();
    int grade = u.getGrade();
    String region = u.getRegion();
    ArrayList<ObjectId> _reg_by = u.get_reg_by();

    if (null != u_id) {
      userDoc.put("_id", u_id);
    }

    if (null != role) {
      userDoc.put("_role", role);
    }

    if (0 != grade) {
      userDoc.put("grade", grade);
    }

    if (null != region) {
      userDoc.put("region", region);
    }

    if (null != _reg_by) {
      userDoc.put("_reg_by", _reg_by);
    }

    documentCodec.encode(bsonWriter, userDoc, encoderContext);
  }

  @SuppressWarnings("unchecked")
  @Override
  public User decode(BsonReader bsonReader, DecoderContext decoderContext) {
    Document userDoc = documentCodec.decode(bsonReader, decoderContext);
    User u = new User();
    u.setId(userDoc.getObjectId("_id"));
    Object obj = "grade";
    if(userDoc.containsKey(obj)) {
      u.setGrade(userDoc.getInteger("grade", 0));
    }else {
      u.setGrade(0);
    }
    u.setRole(userDoc.getString("_role"));
    Object obj2 = "region";
    if(userDoc.containsKey(obj2)) {
      u.setRegion(userDoc.getString("region"));
    }else {
      u.setRegion(null);
    }
    u.set_reg_by((ArrayList<ObjectId>) userDoc.get("_reg_by"));
    return u;
  }

  @Override
  public Class<User> getEncoderClass() {
    return User.class;
  }

  @Override
  public User generateIdIfAbsentFromDocument(User u) {
    return !documentHasId(u) ? u.withNewId() : u;
  }

  @Override
  public boolean documentHasId(User u) {
    return null != u.getId();
  }

  @Override
  public BsonString getDocumentId(User u) {
    if (!documentHasId(u)) {
      throw new IllegalStateException("This document does not have an " + "_id");
    }

    return new BsonString(u.getId().toString());
  }
}
