import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;

/*
competitions": // [        //a array of competitions, that the user participated in
    competition-id[         // contains user submissions and scores, as well as his overall score and rank.
       "rank":
       "score":
       "tasks"("problems") : [     //array of problems the userresponded and his score
            "pid":
            "score":
       ]
    ]
]}
 */
public class User {
    private ObjectId _id;
    private int grade;
    private ArrayList<Competitiion> competitions;
    private int rank;

    private BsonDocument p;
    //private int lt;

    public User(){

    }

    public ObjectId getId() {
        return _id;
    }
    public void setId(final ObjectId id) {
        this._id = id;
    }

    public BsonDocument getP() {
        return p;
    }
    public void setP(BsonDocument p) {
        this.p = p;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Problem problem = (Problem) o;

        if (getId() != null ? !getId().equals(problem.getId()) : problem.getId() != null) {
            return false;
        }
        if (getP() != null ? !getP().equals(problem.getP()) : problem.getP() != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getP() != null ? getP().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Problem{"
                + "id='" + _id + "'"
                + ", p =" + p
                + "}";
    }
}
