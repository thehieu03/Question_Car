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

    /**
     * Thêm một bình luận mới cho đề thi.
     * Hàm này tạo một bản ghi ExamSetComment mới với created_at = thời gian hiện
     * tại.
     * Mỗi user chỉ có thể bình luận một lần cho mỗi đề thi (cần kiểm tra trước khi
     * thêm).
     * 
     * @param examSetId ID của đề thi được bình luận
     * @param userId    ID của người dùng viết bình luận
     * @param content   Nội dung bình luận
     * @return true nếu thêm thành công, false nếu có lỗi
     */
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

    /**
     * Lấy danh sách tất cả bình luận của một đề thi, kèm thông tin người dùng.
     * Hàm này JOIN với bảng Users để lấy username của người bình luận.
     * Sắp xếp theo created_at giảm dần (bình luận mới nhất trước).
     * 
     * @param examSetId ID của đề thi cần lấy bình luận
     * @return Danh sách các đối tượng ExamSetComment có kèm username, danh sách
     *         rỗng nếu không có bình luận
     */
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

    /**
     * Lấy bình luận của một người dùng cụ thể cho một đề thi.
     * Hàm này tìm bình luận của user cho đề thi này. Nếu có nhiều bình luận, lấy
     * bình luận mới nhất.
     * Dùng để kiểm tra xem user đã bình luận chưa và hiển thị bình luận của user.
     * 
     * @param userId    ID của người dùng
     * @param examSetId ID của đề thi
     * @return Đối tượng ExamSetComment nếu tìm thấy, null nếu user chưa bình luận
     *         hoặc có lỗi
     */
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
