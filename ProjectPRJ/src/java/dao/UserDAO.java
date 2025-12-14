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

    /**
     * Lấy tổng số lượng người dùng trong hệ thống (không bao gồm admin).
     * Hàm này đếm tất cả các user có role khác 1 (role = 1 là admin).
     * 
     * @return Tổng số lượng người dùng (user), trả về 0 nếu có lỗi hoặc không có
     *         kết nối database
     */
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

    /**
     * Xác thực đăng nhập người dùng bằng username và password.
     * Hàm này kiểm tra thông tin đăng nhập và trả về đối tượng User nếu hợp lệ.
     * 
     * @param username Tên đăng nhập của người dùng (sẽ được trim để loại bỏ khoảng
     *                 trắng)
     * @param password Mật khẩu của người dùng (sẽ được trim để loại bỏ khoảng
     *                 trắng)
     * @return Đối tượng User nếu đăng nhập thành công, null nếu thông tin không hợp
     *         lệ hoặc không tìm thấy
     */
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

    /**
     * Lấy thông tin người dùng theo tên đăng nhập (username).
     * Hàm này tìm kiếm user trong database dựa trên username chính xác.
     * 
     * @param username Tên đăng nhập cần tìm (sẽ được trim để loại bỏ khoảng trắng)
     * @return Đối tượng User nếu tìm thấy, null nếu không tìm thấy hoặc username
     *         rỗng/null
     */
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

    /**
     * Lấy thông tin người dùng theo địa chỉ email.
     * Hàm này tìm kiếm user trong database dựa trên email chính xác.
     * 
     * @param email Địa chỉ email cần tìm
     * @return Đối tượng User nếu tìm thấy, null nếu không tìm thấy hoặc có lỗi
     */
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

    /**
     * Đăng ký tài khoản người dùng mới.
     * Hàm này kiểm tra username và email có tồn tại chưa, nếu chưa thì tạo tài
     * khoản mới với role = 0 (USER).
     * 
     * @param username Tên đăng nhập mới (phải là duy nhất)
     * @param email    Địa chỉ email mới (phải là duy nhất)
     * @param password Mật khẩu của tài khoản mới
     * @return true nếu đăng ký thành công, false nếu username/email đã tồn tại hoặc
     *         có lỗi xảy ra
     */
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

    /**
     * Lấy danh sách tất cả người dùng với phân trang (không bao gồm admin).
     * Hàm này trả về danh sách user được sắp xếp theo user_id, bỏ qua các user có
     * role = 1 (admin).
     * 
     * @param offset Số lượng bản ghi cần bỏ qua (dùng cho phân trang)
     * @param limit  Số lượng bản ghi tối đa cần lấy
     * @return Danh sách các đối tượng User, danh sách rỗng nếu không có dữ liệu
     *         hoặc có lỗi
     */
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

    /**
     * Tìm kiếm người dùng theo từ khóa (username hoặc email) với phân trang.
     * Hàm này tìm kiếm user có username hoặc email chứa từ khóa (không phân biệt
     * hoa thường).
     * 
     * @param keyword Từ khóa tìm kiếm (có thể là một phần của username hoặc email)
     * @param offset  Số lượng bản ghi cần bỏ qua (dùng cho phân trang)
     * @param limit   Số lượng bản ghi tối đa cần lấy
     * @return Danh sách các đối tượng User khớp với từ khóa, danh sách rỗng nếu
     *         không tìm thấy
     */
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

    /**
     * Lấy tổng số lượng người dùng khớp với từ khóa tìm kiếm (không bao gồm admin).
     * Hàm này đếm số user có username hoặc email chứa từ khóa, dùng để tính tổng số
     * trang khi phân trang.
     * 
     * @param keyword Từ khóa tìm kiếm
     * @return Tổng số lượng user khớp với từ khóa, trả về 0 nếu không tìm thấy hoặc
     *         có lỗi
     */
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

    /**
     * Cấm người dùng bằng cách đặt role = -1.
     * Hàm này vô hiệu hóa tài khoản user, ngăn họ đăng nhập vào hệ thống.
     * 
     * @param userId ID của người dùng cần cấm
     * @return true nếu cấm thành công, false nếu có lỗi hoặc không tìm thấy user
     */
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

    /**
     * Gỡ cấm người dùng bằng cách đặt role = 0 (USER).
     * Hàm này khôi phục quyền truy cập cho user đã bị cấm.
     * 
     * @param userId ID của người dùng cần gỡ cấm
     * @return true nếu gỡ cấm thành công, false nếu có lỗi hoặc không tìm thấy user
     */
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

    /**
     * Cập nhật thông tin người dùng (username, email, role).
     * Hàm này kiểm tra username và email mới có trùng với user khác không trước khi
     * cập nhật.
     * 
     * @param userId   ID của người dùng cần cập nhật
     * @param username Tên đăng nhập mới (phải là duy nhất nếu thay đổi)
     * @param email    Địa chỉ email mới (phải là duy nhất nếu thay đổi)
     * @param role     Vai trò mới của user (0 = USER, 1 = ADMIN, -1 = BANNED)
     * @return true nếu cập nhật thành công, false nếu username/email trùng hoặc có
     *         lỗi
     */
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

    /**
     * Lấy thông tin người dùng theo ID.
     * Hàm này trả về đầy đủ thông tin của user bao gồm user_id, username, email,
     * password, role.
     * 
     * @param userId ID của người dùng cần lấy thông tin
     * @return Đối tượng User nếu tìm thấy, null nếu không tìm thấy hoặc có lỗi
     */
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

    /**
     * Xóa người dùng và tất cả dữ liệu liên quan.
     * Hàm này xóa user cùng với tất cả bài thi, câu trả lời và bình luận của user
     * đó.
     * Sử dụng transaction để đảm bảo tính toàn vẹn dữ liệu.
     * Không cho phép xóa admin (role = 1).
     * 
     * Thứ tự xóa:
     * 1. Xóa tất cả câu trả lời (UserAnswers) của user
     * 2. Xóa tất cả bài thi (UserExams) của user
     * 3. Xóa tất cả bình luận (ExamSetComments) của user
     * 4. Cuối cùng xóa user (Users)
     * 
     * @param userId ID của người dùng cần xóa
     * @return true nếu xóa thành công, false nếu user là admin hoặc có lỗi xảy ra
     */
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
