import org.bson.types.ObjectId;
import java.util.Date;
import java.util.List;

public final class Submission {
    private ObjectId _id;
    private ObjectId pid;
    private ObjectId u;
    private Date st;
    private List<Answer> a;
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

    public Object getA() {
        return a;
    }

    public void setA(List<Answer> a) {
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
}



