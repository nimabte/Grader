package event_description;

import org.bson.types.ObjectId;

import java.util.ArrayList;


public class Event_desc {
    private ObjectId _id;
    private String title;
    private ArrayList<Competition_desc> competition_list;
    private ArrayList<ObjectId> participant_list;

    public ObjectId get_id() {
        return _id;
    }

    public void set_id(ObjectId _id) {
        this._id = _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Competition_desc> getCompetition_list() {
        return competition_list;
    }

    public void setCompetition_list(ArrayList<Competition_desc> competition_list) {
        this.competition_list = competition_list;
    }

    public ArrayList<ObjectId> getParticipant_list() {
        return participant_list;
    }

    public void setParticipant_list(ArrayList<ObjectId> participant_list) {
        this.participant_list = participant_list;
    }

    @Override
    public String toString() {
        return "Event_desc{" +
                "_id=" + _id +
                ", title='" + title + '\'' +
                ", competition_list=" + competition_list +
                ", participant_list=" + participant_list +
                '}';
    }
}