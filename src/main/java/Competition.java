/* this class is to save a competition in a user profile*/
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Competition {
    private ObjectId _id;
    private String title;
    private int score; //the overall performance of the participant in this competition.
    private int rank_in_grade; //rank in the organization
    private int rank_in_reg; //rank in the region
    private int valid_grade;
    private HashMap<ObjectId, int[]> tasks; // to store the score of the participant for each task (task = evaluated answer to the problem with the same p-id)


    public Competition(){

    }
    public Competition(ObjectId competitionId, String title){
        _id = competitionId;
        this.title = title;
        score = 0;
        rank_in_grade = 0;
        rank_in_reg = 0;
        tasks = new HashMap<>();
    }

    public ObjectId getId() {
        return _id;
    }
    public void setId(final ObjectId id) {
        this._id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(final String title) {
        this.title = title;
    }

    public int getScore() {
        return score;
    }
    public void setScore(final int score) {
        this.score = score;
    }
    public void updateScore(final int value){
        score += value;
    }

    public int getRank_in_grade() {
        return rank_in_grade;
    }
    public void setRank_in_grade(final int rank_in_grade) {
        this.rank_in_grade = rank_in_grade;
    }
    public void updateRank_in_grade(final int value){
        rank_in_grade += value;
    }

    public int getRank_in_reg() {
        return rank_in_reg;
    }
    public void setRank_in_reg(final int rank_in_reg) {
        this.rank_in_reg = rank_in_reg;
    }
    public void updateRank_in_reg(final int value){
        rank_in_reg += value;
    }

    public HashMap<ObjectId, int[]> getTasks(){
        return tasks;
    }
    public void setTasks(HashMap<ObjectId, int[]> tasks){
        this.tasks = tasks;
    }

    public int getValid_grade() {
        return valid_grade;
    }

    public void setValid_grade(int valid_grade) {
        this.valid_grade = valid_grade;
    }

    //returns the corresponding answer of user OR NULL if it does not exist/
    public int[] getTask(ObjectId id) {
        return tasks.get(id);
    }
    public int getTaskLt(ObjectId id) {
        return tasks.get(id)[0];
    }
    public int getTaskAns(ObjectId id) {
        return tasks.get(id)[1];
    }

    public void updateTask(ObjectId p_id, int lt, int mark) throws Exception {
        int[] ans = tasks.get(p_id);
        if(ans != null){
            //if the saved answer is newer do nothing!
            if(ans[0]>lt)
                return;
            updateScore(ans[1], mark * ans[2]);
            ans[0]=lt;
            ans[1]=mark * ans[2];
            return;
        }
        // should not be null as all task now initialized
        throw new Exception("Competition Class, line 109, not a valid problem for this competition!");
//        ans = new int[2];
//        ans[0]=lt;
//        ans[1]=mark;
//        tasks.put(p_id, ans);
//        updateScore(0, mark);
    }

    private void updateScore(int previousTaskMark, int newTaskMark) {
        score -= previousTaskMark;
        score += newTaskMark;
    }

    //returns a list of task ids in this competition.
    public List<ObjectId> getTaskList() {
        return(new ArrayList<ObjectId>(tasks.keySet()));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Competition competition = (Competition) o;

        if (getId() != null ? !getId().equals(competition.getId()) : competition.getId() != null) {
            return false;
        }
        if (!getTitle().equals(competition.getTitle()))  {
            return false;
        }
        if (getScore() != competition.getScore())  {
            return false;
        }
        if (getRank_in_grade() != competition.getRank_in_grade())  {
            return false;
        }
        if (getRank_in_reg() != competition.getRank_in_reg())  {
            return false;
        }
        if (getTaskList() != null ? !getTaskList().equals(competition.getTaskList()) : competition.getTaskList() != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + getScore();
        result = 31 * result + getRank_in_grade();
        return result;
    }

    @Override
    public String toString() {
        return "Competition{"
                + "c_id='" + _id + "'"
                + ", title =" + title
                + ", score =" + score
                + ", rank in grade =" + rank_in_grade
                + ", rank in the region =" + rank_in_reg
                + ", list of tasks =" + getTaskList()
                + "}";
    }
}
