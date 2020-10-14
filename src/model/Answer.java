package model;

public class Answer {
    private int question;
    private long maxMiliseconds;
    private long answerMiliseconds;
    private long extraMiliseconds;
    private String option;
    private boolean answered;
    private boolean penalty;

    public Answer() {}

    public int getQuestion() {
        return question;
    }

    public void setQuestion(int question) {
        this.question = question;
    }
    
    public boolean isPenalty() {
        return penalty;
    }

    public void setPenalty(boolean penalty) {
        this.penalty = penalty;
    }

    public long getMaxMiliseconds() {
        return maxMiliseconds;
    }

    public void setMaxMiliseconds(long maxMiliseconds) {
        this.maxMiliseconds = maxMiliseconds;
    }

    public long getAnswerMiliseconds() {
        return answerMiliseconds;
    }

    public void setAnswerMiliseconds(long answerMiliseconds) {
        this.answerMiliseconds = answerMiliseconds;
    }

    public long getExtraMiliseconds() {
        return extraMiliseconds;
    }

    public void setExtraMiliseconds(long extraMiliseconds) {
        this.extraMiliseconds = extraMiliseconds;
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
