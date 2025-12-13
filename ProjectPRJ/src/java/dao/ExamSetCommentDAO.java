package dao;

import dbcontext.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.ExamSetComment;

public class ExamSetCommentDAO extends DBContext {

    public boolean addComment(int examSetId, int userId, String content) {
        String sql = "INSERT INTO ExamSetComments (exam_set_id, user_id, content, created_at) VALUES (?, ?, ?, GETDATE())";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return false;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, examSetId);
            ps.setInt(2, userId);
            ps.setString(3, content);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException ex) {
        }
        return false;
    }

    public List<ExamSetComment> getCommentsByExamSet(int examSetId) {
        List<ExamSetComment> comments = new ArrayList<>();
        String sql = """
                SELECT esc.*, u.username
                FROM ExamSetComments esc
                JOIN Users u ON esc.user_id = u.user_id
                WHERE esc.exam_set_id = ?
                ORDER BY esc.created_at DESC
                """;
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return comments;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, examSetId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ExamSetComment comment = new ExamSetComment();
                comment.setCommentId(rs.getInt("comment_id"));
                comment.setExamSetId(rs.getInt("exam_set_id"));
                comment.setUserId(rs.getInt("user_id"));
                comment.setContent(rs.getString("content"));
                comment.setCreatedAt(rs.getTimestamp("created_at"));
                // Store username in a custom field if needed
                comments.add(comment);
            }
        } catch (SQLException ex) {
        }
        return comments;
    }

    public ExamSetComment getCommentByUserAndExamSet(int userId, int examSetId) {
        String sql = """
                SELECT esc.*
                FROM ExamSetComments esc
                WHERE esc.user_id = ? AND esc.exam_set_id = ?
                ORDER BY esc.created_at DESC
                """;
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return null;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, examSetId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ExamSetComment comment = new ExamSetComment();
                comment.setCommentId(rs.getInt("comment_id"));
                comment.setExamSetId(rs.getInt("exam_set_id"));
                comment.setUserId(rs.getInt("user_id"));
                comment.setContent(rs.getString("content"));
                comment.setCreatedAt(rs.getTimestamp("created_at"));
                return comment;
            }
        } catch (SQLException ex) {
        }
        return null;
    }
}
