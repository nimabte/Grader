/*
Participant JSON form:
{
    "_id":
    {
        "$oid": "59aea4216276ee44b5c9192d"
    },
    "grade": "6",
    "_role": "PARTICIPANT",
    "_reg_by": [
        {
            "$oid": "59ae9f816276ee44b5c918e1"
        }
    ]
}
 */
/*
Organizer JSON form:
{
    "_id":
    {
        "$oid": "59afb7976276eeaca5b9b300"
    },
    "region": "ALT",
    "_role": "SCHOOL_ORG",
    "_reg_by": []
}
 */

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.bson.BsonDocument;
import java.util.ArrayList;
import java.util.List;

public class User {
    private ObjectId _id;
    private String _role;
    private int grade;
    private String region;
    private ArrayList<Event> events;

    public User(){

    }

    public ObjectId getId() {
        return _id;
    }
    public void setId(final ObjectId id) {
        this._id = id;
    }

    public String getRole() {
        return _role;
    }
    public void setRole(final String _role) {
        this._role = _role;
    }

    public int getGrade() {
        return grade;
    }
    public void setGrade(final int grade) {
        this.grade = grade;
    }

    public String getRegion() {
        return region;
    }
    public void setRegion(final String region) {
        this.region = region;
    }

    public ArrayList<Event> getEvents(){
        return events;
    }
    public void setEvents(ArrayList<Event> events){
        this.events = events;
    }

    //TODO implement the methods to access to competitions and tasks

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        User user = (User) o;

        if (getId() != null ? !getId().equals(user.getId()) : user.getId() != null) {
            return false;
        }
        if (!getRole().equals(user.getRole()))  {
            return false;
        }
        if (getGrade() != user.getGrade())  {
            return false;
        }
        if (!getRegion().equals(user.getRegion()))  {
            return false;
        }
        if (getEvents() != null ? !getEvents().equals(user.getEvents()) : user.getEvents() != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + getGrade();
        result = 31 * result + (getRole() != null ? getRole().hashCode() : 0);
        result = 31 * result + (getRegion() != null ? getRegion().hashCode() : 0);
        result = 31 * result + (getEvents() != null ? getEvents().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "user{"
                + "id='" + _id + "'"
                + ", role =" + _role
                + ", region =" + region
                + ", grade =" + grade
                + ", Event list =" + getEvents()
                + "}";
    }
}
