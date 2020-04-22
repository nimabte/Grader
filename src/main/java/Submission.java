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
    private ObjectId c_id;

    public Submission(){

    }

    public ObjectId get_id() {
        return _id;
    }
    public void set_id(ObjectId _id) {
        this._id = _id;
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

    public ObjectId getC_id() {
        return c_id;
    }

    public void setC_id(ObjectId c_id) {
        this.c_id = c_id;
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
        if (get_id() != null ? !get_id().equals(submission.get_id()) : submission.get_id() != null) {
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
        int result = get_id() != null ? get_id().hashCode() : 0;
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
                + ", competition_id=" + c_id
                + "}";
    }
}



