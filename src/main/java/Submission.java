import org.bson.BsonDocument;
import org.bson.types.ObjectId;
import java.util.Date;

public final class Submission {
    private ObjectId _id;
    private ObjectId pid;
    private ObjectId u;
    private Date st;
    private BsonDocument a;
    private int lt;

    public Submission(){

    }

    public ObjectId getId() {
        return _id;
    }
    public void setId(final ObjectId id) {
        this._id = id;
    }

    public ObjectId getPid() {
        return pid;
    }
    public void setPid(final ObjectId pid) {
        this.pid = pid;
    }

    public ObjectId getU() {
        return u;
    }
    public void setU(final ObjectId u) {
        this.u = u;
    }

    public BsonDocument getA() {
        return a;
    }
    public void setA(BsonDocument a) {
        this.a = a;
    }

    public int getLt() {
        return lt;
    }
    public void setLt(final int time) {
        this.lt = time;
    }

    public Date getSt() {
        return st;
    }
    public void setSt(final Date time) {
        this.st = time;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Submission submission = (Submission) o;

        if (getPid() != null ? !getPid().equals(submission.getPid()) : submission.getPid() != null) {
            return false;
        }
        if (getId() != null ? !getId().equals(submission.getId()) : submission.getId() != null) {
            return false;
        }
        if (getU() != null ? !getU().equals(submission.getU()) : submission.getU() != null) {
            return false;
        }
        if (getA() != null ? !getA().equals(submission.getA()) : submission.getA() != null) {
            return false;
        }
        if (getLt() != submission.getLt())  {
            return false;
        }
        if (getSt() != submission.getSt())  {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getU() != null ? getU().hashCode() : 0);
        result = 31 * result + getLt();
        result = 31 * result + (getA() != null ? getA().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Submission{"
                + "id='" + _id + "'"
                + ", uid='" + u + "'"
                + ", pid='" + pid + "'"
                + ", localTime=" + lt
                + ", serverTime=" + st
                + ", answer=" + a
                + "}";
    }
}



