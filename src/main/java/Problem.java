import org.bson.BsonDocument;
import org.bson.types.ObjectId;
import java.util.Date;

public final class Problem {
    private ObjectId _id;
    private BsonDocument p;
    private int score;

    public Problem(){

    }
    public Problem(ObjectId problemId, BsonDocument problem){
        _id = problemId;
        p = problem;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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