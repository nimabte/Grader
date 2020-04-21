package event_description;

import java.util.ArrayList;

public class Valid_user {
    private ArrayList<Integer> grade;

    public ArrayList<Integer> getGrade() {
        return grade;
    }

    public void setGrade(ArrayList<Integer> grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "users with{" +
                "grade=" + grade +
                '}';
    }
}
