package event_description;

import java.util.ArrayList;
import java.util.Arrays;

public class Valid_user {
    private int[] grade;

    public int[] getGrade() {
        return grade;
    }

    public void setGrade(int[] grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "users with{" +
                "grade=" + Arrays.toString(grade) +
                '}';
    }
}
