import org.bson.BsonDocument;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Event {
    private ObjectId _id;
    private HashMap<ObjectId, Competition> competitions;

    public Event(){
    }
    public Event(ObjectId eventId){
        _id = eventId;
        competitions = new HashMap<ObjectId, Competition>();
    }

    public ObjectId getId() {
        return _id;
    }
    public void setId(final ObjectId id) {
        this._id = id;
    }

    public HashMap<ObjectId, Competition> getCompetitions(){
        return competitions;
    }
    public void setCompetitions(HashMap<ObjectId, Competition> competitions){
        this.competitions = competitions;
    }

    //returns the corresponding competition OR NULL if it does not exist/
    public Competition getCompetition(ObjectId id) {
        return competitions.get(_id);
    }

    //returns a list of competition ids in this event.
    public List<ObjectId> getCompetitionList() {
        return(new ArrayList<ObjectId>(competitions.keySet()));
    }

    public void addCompetition(Competition c) {
        competitions.put(c.getId(),c);
    }

    public void removeCompetition(Competition c) {
        competitions.remove(c.getId());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Event event = (Event) o;

        if (getId() != null ? !getId().equals(event.getId()) : event.getId() != null) {
            return false;
        }
        if (getCompetitionList() != null ? !getCompetitionList().equals(event.getCompetitionList()) : event.getCompetitionList() != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getCompetitionList() != null ? getCompetitionList().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Event{"
                + "id='" + _id + "'"
                + ", competitions list =" + getCompetitionList()
                + "}";
    }
}
