package model;

import java.io.Serializable;

public class UserAnswer implements Serializable {
    private int userAnswerId;
    private int userExamId;
    private int questionId;
    private Integer answerId;
    private Boolean isCorrect;

    public UserAnswer() {
    }

    public UserAnswer(int userExamId, int questionId, Integer answerId, Boolean isCorrect) {
        this.userExamId = userExamId;
        this.questionId = questionId;
        this.answerId = answerId;
        this.isCorrect = isCorrect;
    }

    public int getUserAnswerId() {
        return userAnswerId;
    }

    public void setUserAnswerId(int userAnswerId) {
        this.userAnswerId = userAnswerId;
    }

    public int getUserExamId() {
        return userExamId;
    }

    public void setUserExamId(int userExamId) {
        this.userExamId = userExamId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public Integer getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Integer answerId) {
        this.answerId = answerId;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public boolean isSkipped() {
        return answerId == null;
    }

    @Override
    public String toString() {
        return "UserAnswer{" +
                "userAnswerId=" + userAnswerId +
                ", userExamId=" + userExamId +
                ", questionId=" + questionId +
                ", answerId=" + answerId +
                ", isCorrect=" + isCorrect +
                '}';
    }
}

