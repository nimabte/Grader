import org.bson.types.ObjectId;

public class Submission {

    private ObjectId pid;
    private ObjectId uid;
    private ObjectId ans;
    private int localTime;

    public Submission(){

    }

    public ObjectId getPid() {
        return pid;
    }

    public void setPid(final ObjectId pid) {
        this.pid = pid;
    }

    public ObjectId getUid() {
        return uid;
    }

    public void setUid(final ObjectId uid) {
        this.uid = uid;
    }

    public ObjectId getAns() {
        return ans;
    }

    public void setAns(final ObjectId ans) {
        this.ans = ans;
    }

    public int getLocalTime() {
        return localTime;
    }

    public void setLocalTime(final int time) {
        this.localTime = time;
    }

}



