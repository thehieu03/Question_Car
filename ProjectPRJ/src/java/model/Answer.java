package model;

import java.io.Serializable;

public class Answer implements Serializable {
    private int answerId;
    private int questionId;
    private String answerText;
    private boolean isCorrect;
    private int answerOrder;

    public Answer() {
    }

    public Answer(int questionId, String answerText, boolean isCorrect, int answerOrder) {
        this.questionId = questionId;
        this.answerText = answerText;
        this.isCorrect = isCorrect;
        this.answerOrder = answerOrder;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public int getAnswerOrder() {
        return answerOrder;
    }

    public void setAnswerOrder(int answerOrder) {
        this.answerOrder = answerOrder;
    }

    public String getAnswerLabel() {
        switch (answerOrder) {
            case 1: return "A";
            case 2: return "B";
            case 3: return "C";
            case 4: return "D";
            default: return String.valueOf(answerOrder);
        }
    }

    @Override
    public String toString() {
        return "Answer{" +
                "answerId=" + answerId +
                ", questionId=" + questionId +
                ", answerText='" + answerText + '\'' +
                ", isCorrect=" + isCorrect +
                ", answerOrder=" + answerOrder +
                '}';
    }
}

