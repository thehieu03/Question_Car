package dao;

import dbcontext.DBContext;
import interfa.IUserDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;

public class UserDAO extends DBContext implements IUserDAO {
    
    private static final Logger logger = Logger.getLogger(UserDAO.class.getName());

    public int getTotalUsers() {
        String sql = "SELECT COUNT(*) AS total FROM Users WHERE role != 1";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                logger.severe("ERROR: Database connection is null!");
                return 0;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error getting total users", ex);
        }
        return 0;
    }

    public User login(String username, String password) {
        logger.info("=== UserDAO.login() called ===");
        logger.info("Username: " + (username != null ? "'" + username + "'" : "null"));
        logger.info("Password: " + (password != null ? "***" : "null"));
        
        if (username == null || password == null) {
            logger.warning("Login failed: Username or password is null");
            return null;
        }
        
        username = username.trim();
        password = password.trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            logger.warning("Login failed: Username or password is empty");
            return null;
        }
        
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
        logger.info("Executing login query for username: '" + username + "'");
        
        try {
            Connection conn = getConnection();
            if (conn == null) {
                logger.severe("ERROR: Database connection is null after getConnection()!");
                return null;
            }
            logger.info("Database connection established successfully");
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            logger.info("PreparedStatement created with username: '" + username + "'");
            
            ResultSet rs = ps.executeQuery();
            logger.info("Query executed successfully");
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getInt("role"));
                logger.info("Login successful for user: " + user.getUsername() + " (Role: " + user.getRole() + ")");
                return user;
            } else {
                logger.warning("No user found with username: '" + username + "' and provided password");
                // Check if username exists
                User userByUsername = getUserByUsername(username);
                if (userByUsername != null) {
                    logger.warning("Username exists but password doesn't match");
                    logger.warning("Stored password in DB: '" + userByUsername.getPassword() + "'");
                    logger.warning("Provided password: '" + password + "'");
                    logger.warning("Passwords match: " + userByUsername.getPassword().equals(password));
                } else {
                    logger.warning("Username does not exist");
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "SQL Error during login", ex);
            logger.severe("Error Code: " + ex.getErrorCode());
            logger.severe("SQL State: " + ex.getSQLState());
            logger.severe("Message: " + ex.getMessage());
            logger.severe("SQL: " + sql);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Unexpected error during login", ex);
            logger.severe("Error: " + ex.getClass().getName());
            logger.severe("Message: " + ex.getMessage());
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
                logger.severe("ERROR: Database connection is null after getConnection() in getUserByUsername!");
                logger.severe("  Username: " + username);
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
            logger.log(Level.SEVERE, "SQL Error in getUserByUsername", ex);
            logger.severe("  Error Code: " + ex.getErrorCode());
            logger.severe("  SQL State: " + ex.getSQLState());
            logger.severe("  Message: " + ex.getMessage());
            logger.severe("  Username: " + username);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Unexpected error in getUserByUsername", ex);
            logger.severe("  Error: " + ex.getClass().getName());
            logger.severe("  Message: " + ex.getMessage());
            logger.severe("  Username: " + username);
        }
        return null;
    }

    public User getUserByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ?";
        try {
            Connection conn = getConnection();
            if (conn == null) {
                logger.severe("ERROR: Database connection is null after getConnection()!");
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
            logger.log(Level.SEVERE, "Error getting user by email", ex);
        }
        return null;
    }

    public boolean register(String username, String email, String password) {
        String sql = "INSERT INTO Users (username, email, password, role) VALUES (?, ?, ?, 0)";
        PreparedStatement ps = null;
        try {
            Connection conn = getConnection();
            if (conn == null) {
                logger.severe("ERROR: Database connection is null after getConnection()!");
                return false;
            }
            
            logger.info("Checking if username exists: " + username);
            User existingUser = getUserByUsername(username);
            if (existingUser != null) {
                logger.warning("Username already exists: " + username);
                return false;
            }
            
            logger.info("Checking if email exists: " + email);
            User existingEmail = getUserByEmail(email);
            if (existingEmail != null) {
                logger.warning("Email already exists: " + email);
                return false;
            }
            
            logger.info("Attempting to register user: " + username);
            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, password);
            
            logger.info("Executing INSERT statement...");
            int result = ps.executeUpdate();
            logger.info("INSERT result: " + result + " row(s) affected");
            
            if (result > 0) {
                logger.info("✓ User registered successfully: " + username);
                return true;
            } else {
                logger.warning("✗ No rows affected during registration");
                return false;
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "✗ SQL Error during registration", ex);
            logger.severe("  Error Code: " + ex.getErrorCode());
            logger.severe("  SQL State: " + ex.getSQLState());
            logger.severe("  Message: " + ex.getMessage());
            logger.severe("  SQL: " + sql);
            logger.severe("  Username: " + username);
            logger.severe("  Email: " + email);
            return false;
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "✗ Unexpected error during registration", ex);
            logger.severe("  Error: " + ex.getClass().getName());
            logger.severe("  Message: " + ex.getMessage());
            return false;
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException ex) {
                    logger.warning("Error closing PreparedStatement: " + ex.getMessage());
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
                logger.severe("ERROR: Connection is null in getAllUsers");
                return users;
            }
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            logger.info("Executing getAllUsers with offset=" + offset + ", limit=" + limit);
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
            logger.info("Total users retrieved: " + users.size());
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Error getting all users", ex);
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
            logger.log(Level.SEVERE, "Error searching users", ex);
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
            logger.log(Level.SEVERE, "Error getting total users by search", ex);
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
            logger.log(Level.SEVERE, "Error banning user", ex);
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
            logger.log(Level.SEVERE, "Error unbanning user", ex);
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
            logger.log(Level.SEVERE, "Error updating user", ex);
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
            logger.log(Level.SEVERE, "Error getting user by id", ex);
        }
        return null;
    }
}

