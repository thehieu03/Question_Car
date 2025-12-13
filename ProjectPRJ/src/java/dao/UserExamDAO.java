package dao;

import dbcontext.DBContext;
import interfa.IUserExamDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.UserExam;

public class UserExamDAO extends DBContext implements IUserExamDAO {

    @Override
    public int getTotalUserExams() {
        String sql = "SELECT COUNT(*) AS total FROM UserExams";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return 0;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException ex) {
        }
        return 0;
    }

    @Override
    public List<UserExam> getUserExams(int offset, int limit) {
        List<UserExam> list = new ArrayList<>();
        String sql = "SELECT * FROM UserExams ORDER BY user_exam_id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return list;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserExam ue = mapUserExam(rs);
                list.add(ue);
            }
        } catch (SQLException ex) {
        }
        return list;
    }

    @Override
    public List<UserExam> getUserExamsWithInfo(int offset, int limit) {
        List<UserExam> list = new ArrayList<>();
        String sql = """
                SELECT ue.*, u.username, u.email, es.exam_name
                FROM UserExams ue
                JOIN Users u ON ue.user_id = u.user_id
                JOIN ExamSets es ON ue.exam_set_id = es.exam_set_id
                ORDER BY ue.user_exam_id DESC
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """;
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return list;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserExam ue = mapUserExam(rs);
                ue.setUsername(rs.getString("username"));
                ue.setEmail(rs.getString("email"));
                ue.setExamName(rs.getString("exam_name"));
                list.add(ue);
            }
        } catch (SQLException ex) {
        }
        return list;
    }

    @Override
    public int getTotalUserExamsByUser(int userId) {
        String sql = "SELECT COUNT(*) AS total FROM UserExams WHERE user_id = ?";
        try {
            Connection conn = getConnection();
            if (conn == null)
                return 0;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt("total");
        } catch (SQLException ex) {
        }
        return 0;
    }

    @Override
    public int getPassedUserExamsByUser(int userId) {
        String sql = "SELECT COUNT(*) AS total FROM UserExams WHERE user_id = ? AND is_passed = 1";
        try {
            Connection conn = getConnection();
            if (conn == null)
                return 0;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                return rs.getInt("total");
        } catch (SQLException ex) {
        }
        return 0;
    }

    @Override
    public Integer getLastScoreByUser(int userId) {
        String sql = """
                SELECT TOP 1 total_score
                FROM UserExams
                WHERE user_id = ?
                ORDER BY user_exam_id DESC
                """;
        try {
            Connection conn = getConnection();
            if (conn == null)
                return null;
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (Integer) rs.getObject("total_score");
            }
        } catch (SQLException ex) {
        }
        return null;
    }

    @Override
    public int startExam(int userId, int examSetId) {
        String sql = "INSERT INTO UserExams (user_id, exam_set_id, start_time, status) OUTPUT INSERTED.user_exam_id VALUES (?, ?, GETDATE(), 'IN_PROGRESS')";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return 0;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, examSetId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userExamId = rs.getInt("user_exam_id");
                return userExamId;
            }
        } catch (SQLException ex) {
        }
        return 0;
    }

    @Override
    public boolean saveUserAnswer(int userExamId, int questionId, Integer answerId, Boolean isCorrect) {
        // Kiểm tra xem đã có câu trả lời chưa
        String checkSql = "SELECT COUNT(*) AS cnt FROM UserAnswers WHERE user_exam_id = ? AND question_id = ?";
        String updateSql = "UPDATE UserAnswers SET answer_id = ?, is_correct = ? WHERE user_exam_id = ? AND question_id = ?";
        String insertSql = "INSERT INTO UserAnswers (user_exam_id, question_id, answer_id, is_correct) VALUES (?, ?, ?, ?)";

        try {
            Connection conn = getConnection();
            if (conn == null) {
                return false;
            }

            // Kiểm tra xem đã có record chưa
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, userExamId);
            checkPs.setInt(2, questionId);
            ResultSet rs = checkPs.executeQuery();
            boolean exists = false;
            if (rs.next()) {
                exists = rs.getInt("cnt") > 0;
            }
            rs.close();
            checkPs.close();

            PreparedStatement ps;
            if (exists) {
                // Update
                ps = conn.prepareStatement(updateSql);
                ps.setObject(1, answerId);
                ps.setObject(2, isCorrect);
                ps.setInt(3, userExamId);
                ps.setInt(4, questionId);
            } else {
                // Insert
                ps = conn.prepareStatement(insertSql);
                ps.setInt(1, userExamId);
                ps.setInt(2, questionId);
                ps.setObject(3, answerId);
                ps.setObject(4, isCorrect);
            }

            int result = ps.executeUpdate();
            ps.close();
            return result > 0;
        } catch (SQLException ex) {
        }
        return false;
    }

    @Override
    public boolean submitExam(int userExamId, int totalScore, int correctAnswers, int wrongAnswers, boolean isPassed) {
        String sql = "UPDATE UserExams SET end_time = GETDATE(), total_score = ?, correct_answers = ?, wrong_answers = ?, is_passed = ?, status = 'COMPLETED' WHERE user_exam_id = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return false;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, totalScore);
            ps.setInt(2, correctAnswers);
            ps.setInt(3, wrongAnswers);
            ps.setBoolean(4, isPassed);
            ps.setInt(5, userExamId);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException ex) {
        }
        return false;
    }

    @Override
    public UserExam getUserExamById(int userExamId) {
        String sql = "SELECT * FROM UserExams WHERE user_exam_id = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return null;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userExamId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapUserExam(rs);
            }
        } catch (SQLException ex) {
        }
        return null;
    }

    @Override
    public UserExam getInProgressExam(int userId, int examSetId) {
        String sql = "SELECT TOP 1 * FROM UserExams WHERE user_id = ? AND exam_set_id = ? AND status = 'IN_PROGRESS' ORDER BY user_exam_id DESC";
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
                return mapUserExam(rs);
            }
        } catch (SQLException ex) {
        }
        return null;
    }

    @Override
    public List<UserExam> getUserExamsByUser(int userId, int offset, int limit) {
        List<UserExam> list = new ArrayList<>();
        String sql = """
                SELECT ue.*, es.exam_name
                FROM UserExams ue
                JOIN ExamSets es ON ue.exam_set_id = es.exam_set_id
                WHERE ue.user_id = ? AND ue.status = 'COMPLETED'
                ORDER BY ue.end_time DESC
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """;
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return list;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, offset);
            ps.setInt(3, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UserExam ue = mapUserExam(rs);
                ue.setExamName(rs.getString("exam_name"));
                list.add(ue);
            }
        } catch (SQLException ex) {
        }
        return list;
    }

    private UserExam mapUserExam(ResultSet rs) throws SQLException {
        UserExam ue = new UserExam();
        ue.setUserExamId(rs.getInt("user_exam_id"));
        ue.setUserId(rs.getInt("user_id"));
        ue.setExamSetId(rs.getInt("exam_set_id"));
        ue.setStartTime(rs.getTimestamp("start_time"));
        ue.setEndTime(rs.getTimestamp("end_time"));
        ue.setTotalScore((Integer) rs.getObject("total_score"));
        ue.setCorrectAnswers((Integer) rs.getObject("correct_answers"));
        ue.setWrongAnswers((Integer) rs.getObject("wrong_answers"));
        ue.setIsPassed((Boolean) rs.getObject("is_passed"));
        ue.setStatus(rs.getString("status"));
        return ue;
    }
}
