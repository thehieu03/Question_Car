package model;

import java.io.Serializable;
import java.util.Date;

public class UserExam implements Serializable {
    private int userExamId;
    private int userId;
    private int examSetId;
    private Date startTime;
    private Date endTime;
    private Integer totalScore;
    private Integer correctAnswers;
    private Integer wrongAnswers;
    private Boolean isPassed;
    private String status;

    public UserExam() {
    }

    public UserExam(int userId, int examSetId, Date startTime, Date endTime, 
                    Integer totalScore, Integer correctAnswers, Integer wrongAnswers, 
                    Boolean isPassed, String status) {
        this.userId = userId;
        this.examSetId = examSetId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalScore = totalScore;
        this.correctAnswers = correctAnswers;
        this.wrongAnswers = wrongAnswers;
        this.isPassed = isPassed;
        this.status = status;
    }

    public int getUserExamId() {
        return userExamId;
    }

    public void setUserExamId(int userExamId) {
        this.userExamId = userExamId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getExamSetId() {
        return examSetId;
    }

    public void setExamSetId(int examSetId) {
        this.examSetId = examSetId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public Integer getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(Integer wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public Boolean getIsPassed() {
        return isPassed;
    }

    public void setIsPassed(Boolean isPassed) {
        this.isPassed = isPassed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isInProgress() {
        return "IN_PROGRESS".equals(status);
    }

    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }

    @Override
    public String toString() {
        return "UserExam{" +
                "userExamId=" + userExamId +
                ", userId=" + userId +
                ", examSetId=" + examSetId +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", totalScore=" + totalScore +
                ", correctAnswers=" + correctAnswers +
                ", wrongAnswers=" + wrongAnswers +
                ", isPassed=" + isPassed +
                ", status='" + status + '\'' +
                '}';
    }
}

