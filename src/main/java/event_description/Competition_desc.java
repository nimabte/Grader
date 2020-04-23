package event_description;

import org.bson.types.ObjectId;

import java.util.ArrayList;

public class Competition_desc {
    private ObjectId _id;
    private String title;
    private Valid_user valid_user;
    private int check_submission_method;
    private ArrayList<Task_desc> task_list;

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

    public int getCheck_submission_method() {
        return check_submission_method;
    }

    public void setCheck_submission_method(int check_submission_method) {
        this.check_submission_method = check_submission_method;
    }

    public Valid_user getValid_user() {
        return valid_user;
    }

    public void setValid_user(Valid_user valid_user) {
        this.valid_user = valid_user;
    }

    public ArrayList<Task_desc> getTask_list() {
        return task_list;
    }

    public void setTask_list(ArrayList<Task_desc> task_list) {
        this.task_list = task_list;
    }

    @Override
    public String toString() {
        return "Competition{" +
                "_id=" + _id +
                ", title='" + title + '\'' +
                ", valid_user=" + valid_user +
                ", task_list=" + task_list +
                '}';
    }
}
