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
import java.util.HashMap;
import java.util.List;

public class User {
    private ObjectId _id;
    private String _role;
    private int grade;
    private String region;
    private ArrayList<ObjectId> _reg_by;
    private ObjectId regBy;
    private ArrayList<Event> events;

    public User(){
        events = new ArrayList<Event>();
    }
    public User(Event event, Competition competition){
        events = new ArrayList<Event>();
        events.add(event);
        events.get(0).addCompetition(competition);
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

    public void updateRegion() {
        region = (region != null) ? region : findRegion();
    }
    public ObjectId getRegBy() {
        if(regBy == null) {
            regBy = ((_reg_by.size() != 0) ?  _reg_by.get(0)  :  new ObjectId("000000000000000000000000"));
            //regBy = (_reg_by.size() != 0) ?  _reg_by.get(0)  :  null;
        }
        return regBy;
    }
    public void setRegBy(final ObjectId regBy) {
        this.regBy = regBy;
    }

    public ArrayList<Event> getEvents(){
        return events;
    }
    public void setEvents(ArrayList<Event> events){
        this.events = events;
    }
    public void addEvent(Event e){
        events.add(e);
    }
    public void addCompetition(Competition c){
        try {
            if(events.size() == 0){
                throw new Exception("****User's event List is empty!****");
            }
            events.get(events.size() - 1).addCompetition(c);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public Competition getCompetition(ObjectId competitionId){
        try {
            if(events.size() == 0){
                throw new Exception("****User's event List is empty!****");
            }
            return events.get(events.size() - 1).getCompetition(competitionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<ObjectId> get_reg_by(){
        return _reg_by;
    }
    public void set_reg_by(ArrayList<ObjectId> _reg_by){
        this._reg_by = _reg_by;
    }

    private String findRegion(){
        //if(getRegBy() == null || getRegBy().equals(new ObjectId("000000000000000000000000"))) {
        if(getRegBy() ==  null) {
            return null;
        }
        String s = null;
        try {
            s = Main.users.get(regBy).getRegion();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("****User's registrant id did not found in the Map****");
        }
        return s;
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
        if (getRegBy() != null ? !getRegBy().equals(user.getRegBy()) : user.getRegBy() != null) {
            return false;
        }
        if (get_reg_by() != null ? !get_reg_by().equals(user.get_reg_by()) : user.get_reg_by() != null) {
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
                + ", role=" + _role
                + ", region=" + region
                + ", grade=" + grade
                + ", registered_by=" + ((_reg_by.size() != 0) ? "'" + _reg_by.get(0) + "'" : null)
                + ", Event list=" + events
                + "}";
    }

    public User withNewId() {
        setId(new ObjectId());
        System.err.println("User with no _id found");
        return this;
    }
}
