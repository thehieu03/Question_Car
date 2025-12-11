package model;

import java.io.Serializable;

public class Question implements Serializable {
    private int questionId;
    private int categoryId;
    private String questionText;
    private String questionImage;
    private String explanation;
    private boolean isCritical;

    public Question() {
    }

    public Question(int categoryId, String questionText, String questionImage, String explanation, boolean isCritical) {
        this.categoryId = categoryId;
        this.questionText = questionText;
        this.questionImage = questionImage;
        this.explanation = explanation;
        this.isCritical = isCritical;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getQuestionImage() {
        return questionImage;
    }

    public void setQuestionImage(String questionImage) {
        this.questionImage = questionImage;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public boolean isCritical() {
        return isCritical;
    }

    public void setCritical(boolean critical) {
        isCritical = critical;
    }

    @Override
    public String toString() {
        return "Question{" +
                "questionId=" + questionId +
                ", categoryId=" + categoryId +
                ", questionText='" + questionText + '\'' +
                ", questionImage='" + questionImage + '\'' +
                ", isCritical=" + isCritical +
                '}';
    }
}

