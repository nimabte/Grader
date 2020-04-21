package event_description;
import org.bson.types.ObjectId;

public class Task_desc {
    private ObjectId _id;
    private int points;

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "Task{" +
                "_id=" + _id +
                ", points=" + points +
                '}';
    }
}
