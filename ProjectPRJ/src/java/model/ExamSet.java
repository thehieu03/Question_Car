package model;

import java.io.Serializable;

public class ExamSet implements Serializable {
    private int examSetId;
    private String examName;
    private int totalQuestions;
    private int durationMinutes;
    private int passingScore;

    public ExamSet() {
    }

    public ExamSet(String examName, int totalQuestions, int durationMinutes, int passingScore) {
        this.examName = examName;
        this.totalQuestions = totalQuestions;
        this.durationMinutes = durationMinutes;
        this.passingScore = passingScore;
    }

    public int getExamSetId() {
        return examSetId;
    }

    public void setExamSetId(int examSetId) {
        this.examSetId = examSetId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public int getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(int passingScore) {
        this.passingScore = passingScore;
    }

    @Override
    public String toString() {
        return "ExamSet{" +
                "examSetId=" + examSetId +
                ", examName='" + examName + '\'' +
                ", totalQuestions=" + totalQuestions +
                ", durationMinutes=" + durationMinutes +
                ", passingScore=" + passingScore +
                '}';
    }
}

