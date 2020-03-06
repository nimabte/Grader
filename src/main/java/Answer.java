import java.util.Objects;

public class Answer {
    private String type;
    private Double answer;

    public String getType() {
        return type;
    }

    public Answer setType(String type) {
        this.type = type;
        return this;
    }

    public Double getAnswer() {
        return answer;
    }

    public Answer setAnswer(Double answer) {
        this.answer = answer;
        return this;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Answer{");
        sb.append("type='").append(type).append('\'');
        sb.append(", answer=").append(answer);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Answer answer1 = (Answer) o;
        return Objects.equals(type, answer1.type) && Objects.equals(answer, answer1.answer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, answer);
    }
}
