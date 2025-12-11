package model;

import java.io.Serializable;
import java.util.Date;

public class ExamSetComment implements Serializable {
    private int commentId;
    private int examSetId;
    private int userId;
    private String content;
    private Date createdAt;

    public ExamSetComment() {
    }

    public ExamSetComment(int examSetId, int userId, String content, Date createdAt) {
        this.examSetId = examSetId;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getExamSetId() {
        return examSetId;
    }

    public void setExamSetId(int examSetId) {
        this.examSetId = examSetId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ExamSetComment{" +
                "commentId=" + commentId +
                ", examSetId=" + examSetId +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

