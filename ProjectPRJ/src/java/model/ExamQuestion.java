package model;

import java.io.Serializable;

public class ExamQuestion implements Serializable {
    private int examQuestionId;
    private int examSetId;
    private int questionId;
    private int questionOrder;

    public ExamQuestion() {
    }

    public ExamQuestion(int examSetId, int questionId, int questionOrder) {
        this.examSetId = examSetId;
        this.questionId = questionId;
        this.questionOrder = questionOrder;
    }

    public int getExamQuestionId() {
        return examQuestionId;
    }

    public void setExamQuestionId(int examQuestionId) {
        this.examQuestionId = examQuestionId;
    }

    public int getExamSetId() {
        return examSetId;
    }

    public void setExamSetId(int examSetId) {
        this.examSetId = examSetId;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(int questionOrder) {
        this.questionOrder = questionOrder;
    }

    @Override
    public String toString() {
        return "ExamQuestion{" +
                "examQuestionId=" + examQuestionId +
                ", examSetId=" + examSetId +
                ", questionId=" + questionId +
                ", questionOrder=" + questionOrder +
                '}';
    }
}

