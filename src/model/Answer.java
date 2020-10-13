package model;

public class Answer {
    private long max_miliseconds;
    private long answer_miliseconds;
    private long extra_miliseconds;
    private String option;
    private boolean answered;
    private boolean penalty;

    public boolean hasPenalty() {
        return penalty;
    }

    public void setPenalty(boolean penalty) {
        this.penalty = penalty;
    }
    
    public long getExtra_miliseconds() {
        return extra_miliseconds;
    }

    public void setExtra_miliseconds(long extra_miliseconds) {
        this.extra_miliseconds = extra_miliseconds;
    }

    public long getMax_miliseconds() {
        return max_miliseconds;
    }

    public void setMax_miliseconds(long max_miliseconds) {
        this.max_miliseconds = max_miliseconds;
    }

    public long getAnswer_miliseconds() {
        return answer_miliseconds;
    }

    public void setAnswer_miliseconds(long answer_miliseconds) {
        this.answer_miliseconds = answer_miliseconds;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }
}
