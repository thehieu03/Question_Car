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
        String sql = "SELECT COUNT(*) AS total FROM Users";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                System.err.println("ERROR: Database connection is null!");
                return 0;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException ex) {
            System.err.println("Error getting total users: " + ex.getMessage());
            ex.printStackTrace();
        }
        return 0;
    }

    public User login(String username, String password) {
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                System.err.println("ERROR: Database connection is null after getConnection()!");
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
            ex.printStackTrace();
        }
        return null;
    }

    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                System.err.println("ERROR: Database connection is null after getConnection()!");
                return null;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
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
            ex.printStackTrace();
        }
        return null;
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                System.err.println("ERROR: Database connection is null after getConnection()!");
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
            ex.printStackTrace();
        }
        return null;
    }

    public boolean register(String username, String email, String password) {
        String sql = "INSERT INTO Users (username, email, password, role) VALUES (?, ?, ?, 0)";
        PreparedStatement ps = null;
        try {
            Connection conn = getConnection();
            if (conn == null) {
                System.err.println("ERROR: Database connection is null after getConnection()!");
                return false;
            }
            
            System.out.println("Checking if username exists: " + username);
            User existingUser = getUserByUsername(username);
            if (existingUser != null) {
                System.out.println("Username already exists: " + username);
                return false;
            }
            
            System.out.println("Checking if email exists: " + email);
            User existingEmail = getUserByEmail(email);
            if (existingEmail != null) {
                System.out.println("Email already exists: " + email);
                return false;
            }
            
            System.out.println("Attempting to register user: " + username);
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password);
            
            System.out.println("Executing INSERT statement...");
            int result = ps.executeUpdate();
            System.out.println("INSERT result: " + result + " row(s) affected");
            
            if (result > 0) {
                System.out.println("✓ User registered successfully: " + username);
                return true;
            } else {
                System.err.println("✗ No rows affected during registration");
                return false;
            }
        } catch (SQLException ex) {
            System.err.println("✗ SQL Error during registration:");
            System.err.println("  Error Code: " + ex.getErrorCode());
            System.err.println("  SQL State: " + ex.getSQLState());
            System.err.println("  Message: " + ex.getMessage());
            System.err.println("  SQL: " + sql);
            System.err.println("  Username: " + username);
            System.err.println("  Email: " + email);
            ex.printStackTrace();
            return false;
        } catch (Exception ex) {
            System.err.println("✗ Unexpected error during registration:");
            System.err.println("  Error: " + ex.getClass().getName());
            System.err.println("  Message: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    System.err.println("Error closing PreparedStatement: " + ex.getMessage());
                }
            }
        }
    }

    @Override
    public List<User> getAllUsers(int offset, int limit) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY user_id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                System.err.println("ERROR: Connection is null in getAllUsers");
                return users;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            System.out.println("Executing getAllUsers with offset=" + offset + ", limit=" + limit);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getInt("role"));
                users.add(user);
                System.out.println("Added user: " + user.getUsername() + " (role: " + user.getRole() + ")");
            }
            System.out.println("Total users retrieved: " + users.size());
        } catch (SQLException ex) {
            System.err.println("Error getting all users: " + ex.getMessage());
            ex.printStackTrace();
        }
        return users;
    }

    @Override
    public List<User> searchUsers(String keyword, int offset, int limit) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users WHERE username LIKE ? OR email LIKE ? ORDER BY user_id OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
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
            System.err.println("Error searching users: " + ex.getMessage());
            ex.printStackTrace();
        }
        return users;
    }

    @Override
    public int getTotalUsersBySearch(String keyword) {
        String sql = "SELECT COUNT(*) AS total FROM Users WHERE username LIKE ? OR email LIKE ?";
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
            System.err.println("Error getting total users by search: " + ex.getMessage());
            ex.printStackTrace();
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
            System.err.println("Error banning user: " + ex.getMessage());
            ex.printStackTrace();
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
            System.err.println("Error unbanning user: " + ex.getMessage());
            ex.printStackTrace();
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
            System.err.println("Error updating user: " + ex.getMessage());
            ex.printStackTrace();
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
            System.err.println("Error getting user by id: " + ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }
}

