package event_description;
import org.bson.types.ObjectId;

public class Task_desc {
    private ObjectId _id;
    private int score;

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Task{" +
                "_id=" + _id +
                ", score=" + score +
                '}';
    }
}
