/* this class is to save a competition in a user profile*/
import org.bson.BsonDocument;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Competition {
    private ObjectId _id;
    private String title;
    private int score; //the overall performance of the participant in this competition.
    private int rank_in_org; //rank in the organization
    private int rank_in_reg; //rank in the region
    private HashMap<ObjectId, Integer> problems; // to store the score of the participant for each problem


    public Competition(){

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
    public void setScore(final String title) {
        this.title = title;
    }

    public int getScore() {
        return score;
    }
    public void setScore(final int score) {
        this.score = score;
    }

    public int getRank_in_org() {
        return rank_in_org;
    }
    public void setRank_in_org(final int rank_in_org) {
        this.rank_in_org = rank_in_org;
    }

    public int getRank_in_reg() {
        return rank_in_reg;
    }
    public void setRank_in_reg(final int rank_in_reg) {
        this.rank_in_reg = rank_in_reg;
    }

    //returns the corresponding answer of user OR NULL if it does not exist/
    public int getProblem(ObjectId id) {
        return problems.get(_id);
    }

    public void addProblem(ObjectId p_id, int u_answer) {
        problems.put(p_id, u_answer);
    }

    //returns a list of problem ids in this competition.
    public List<ObjectId> getProblemList() {
        return(new ArrayList<ObjectId>(problems.keySet()));
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
        if (getRank_in_org() != competition.getRank_in_org())  {
            return false;
        }
        if (getRank_in_reg() != competition.getRank_in_reg())  {
            return false;
        }
        if (getProblemList() != null ? !getProblemList().equals(competition.getProblemList()) : competition.getProblemList() != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + getScore();
        result = 31 * result + getRank_in_org();
        return result;
    }

    @Override
    public String toString() {
        return "Competition{"
                + "c_id='" + _id + "'"
                + ", title =" + title
                + ", score =" + score
                + ", rank in organization =" + rank_in_org
                + ", rank in the region =" + rank_in_reg
                + ", list of problems =" + getProblemList()
                + "}";
    }
}
