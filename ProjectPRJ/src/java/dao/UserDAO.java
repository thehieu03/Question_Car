package dao;

import dbcontext.DBContext;
import interfa.IUserDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.User;

public class UserDAO extends DBContext implements IUserDAO {

    public int getTotalUsers() {
        String sql = "SELECT COUNT(*) AS total FROM Users WHERE role != 1";
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

    public User login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }

        username = username.trim();
        password = password.trim();

        if (username.isEmpty() || password.isEmpty()) {
            return null;
        }

        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";

        try {
            Connection conn = getConnection();
            if (conn == null) {
                return null;
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getInt("role"));
                return user;
            }
        } catch (SQLException ex) {
        } catch (Exception ex) {
        }
        return null;
    }

    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT * FROM Users WHERE username = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return null;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username.trim());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getInt("role"));
                return user;
            }
        } catch (SQLException ex) {
        } catch (Exception ex) {
        }
        return null;
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return null;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getInt("role"));
                return user;
            }
        } catch (SQLException ex) {
        }
        return null;
    }

    public boolean register(String username, String email, String password) {
        String sql = "INSERT INTO Users (username, email, password, role) VALUES (?, ?, ?, 0)";
        PreparedStatement ps = null;
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return false;
            }

            User existingUser = getUserByUsername(username);
            if (existingUser != null) {
                return false;
            }

            User existingEmail = getUserByEmail(email);
            if (existingEmail != null) {
                return false;
            }

            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password);

            int result = ps.executeUpdate();

            if (result > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException ex) {
            return false;
        } catch (Exception ex) {
            return false;
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    @Override
    public List<User> getAllUsers(int offset, int limit) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE role != 1 ORDER BY user_id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return users;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getInt("role"));
                users.add(user);
            }
        } catch (SQLException ex) {
        }
        return users;
    }

    @Override
    public List<User> searchUsers(String keyword, int offset, int limit) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE role != 1 AND (username LIKE ? OR email LIKE ?) ORDER BY user_id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return users;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setInt(3, offset);
            ps.setInt(4, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getInt("role"));
                users.add(user);
            }
        } catch (SQLException ex) {
        }
        return users;
    }

    @Override
    public int getTotalUsersBySearch(String keyword) {
        String sql = "SELECT COUNT(*) AS total FROM Users WHERE role != 1 AND (username LIKE ? OR email LIKE ?)";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return 0;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException ex) {
        }
        return 0;
    }

    @Override
    public boolean banUser(int userId) {
        String sql = "UPDATE Users SET role = -1 WHERE user_id = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return false;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException ex) {
        }
        return false;
    }

    @Override
    public boolean unbanUser(int userId) {
        String sql = "UPDATE Users SET role = 0 WHERE user_id = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return false;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException ex) {
        }
        return false;
    }

    @Override
    public boolean updateUser(int userId, String username, String email, int role) {
        String sql = "UPDATE Users SET username = ?, email = ?, role = ? WHERE user_id = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return false;
            }

            User existingUser = getUserById(userId);
            if (existingUser == null) {
                return false;
            }

            if (!existingUser.getUsername().equals(username)) {
                if (getUserByUsername(username) != null) {
                    return false;
                }
            }

            if (!existingUser.getEmail().equals(email)) {
                if (getUserByEmail(email) != null) {
                    return false;
                }
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setInt(3, role);
            ps.setInt(4, userId);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException ex) {
        }
        return false;
    }

    @Override
    public User getUserById(int userId) {
        String sql = "SELECT * FROM Users WHERE user_id = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                return null;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getInt("role"));
                return user;
            }
        } catch (SQLException ex) {
        }
        return null;
    }

    public boolean deleteUser(int userId) {
        // Không cho phép xóa admin
        User user = getUserById(userId);
        if (user != null && user.isAdmin()) {
            return false;
        }

        Connection conn = null;
        try {
            conn = getConnection();
            if (conn == null) {
                return false;
            }
            conn.setAutoCommit(false);

            // Bước 1: Xóa tất cả câu trả lời của user (UserAnswers)
            // Phải xóa trước vì UserAnswers phụ thuộc vào UserExams
            String deleteUserAnswers = """
                    DELETE FROM UserAnswers
                    WHERE user_exam_id IN (SELECT user_exam_id FROM UserExams WHERE user_id = ?)
                    """;
            PreparedStatement psAnswers = conn.prepareStatement(deleteUserAnswers);
            psAnswers.setInt(1, userId);
            psAnswers.executeUpdate();
            psAnswers.close();

            // Bước 2: Xóa tất cả bài thi của user (UserExams)
            // Sau khi xóa UserExams, UserAnswers sẽ tự động bị xóa do CASCADE (nếu có)
            String deleteUserExams = "DELETE FROM UserExams WHERE user_id = ?";
            PreparedStatement psExams = conn.prepareStatement(deleteUserExams);
            psExams.setInt(1, userId);
            psExams.executeUpdate();
            psExams.close();

            // Bước 3: Xóa tất cả bình luận của user (ExamSetComments)
            String deleteComments = "DELETE FROM ExamSetComments WHERE user_id = ?";
            PreparedStatement psComments = conn.prepareStatement(deleteComments);
            psComments.setInt(1, userId);
            psComments.executeUpdate();
            psComments.close();

            // Bước 4: Cuối cùng xóa user (Users)
            String deleteUser = "DELETE FROM Users WHERE user_id = ?";
            PreparedStatement psUser = conn.prepareStatement(deleteUser);
            psUser.setInt(1, userId);
            int result = psUser.executeUpdate();
            psUser.close();

            if (result > 0) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (SQLException ex) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException e) {
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                }
            }
        }
        return false;
    }
}
