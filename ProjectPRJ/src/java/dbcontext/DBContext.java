package dbcontext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBContext {
    protected Connection connection;
    private static final Logger logger = Logger.getLogger(DBContext.class.getName());

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=DrivingLicenseExam;encrypt=true;trustServerCertificate=true";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "123";

    public DBContext() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            if (connection != null) {
                logger.info("Database connection established successfully!");
            }
        } catch (ClassNotFoundException | SQLException ex) {
            logger.log(Level.SEVERE, "Failed to connect to database", ex);
            logger.severe("Database connection error: " + ex.getMessage());
            connection = null;
        }
    }
    
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                logger.info("Connection is null or closed, attempting to reconnect...");
                reconnect();
            }
        } catch (SQLException ex) {
            logger.warning("Error checking connection: " + ex.getMessage());
            reconnect();
        }
        return connection;
    }
    
    private void reconnect() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            if (connection != null) {
                logger.info("Reconnection successful!");
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Reconnection failed", ex);
            logger.severe("Reconnection failed: " + ex.getMessage());
            connection = null;
        }
    }

    public static void main(String[] args) {
        logger.info("=== Testing Database Connection ===");
        
        try {
            DBContext dbContext = new DBContext();
            Connection conn = dbContext.connection;
            
            if (conn != null && !conn.isClosed()) {
                logger.info("✓ Connection successful!");
                logger.info("✓ Database: " + conn.getCatalog());
                logger.info("✓ Connection URL: " + conn.getMetaData().getURL());
                logger.info("✓ Driver: " + conn.getMetaData().getDriverName());
                logger.info("✓ Driver Version: " + conn.getMetaData().getDriverVersion());
                
                logger.info("=== Testing Query ===");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM Users");
                
                if (rs.next()) {
                    int total = rs.getInt("total");
                    logger.info("✓ Query successful!");
                    logger.info("✓ Total users in database: " + total);
                }
                
                rs.close();
                stmt.close();
                conn.close();
                logger.info("✓ Connection closed successfully!");
            } else {
                logger.severe("✗ Connection failed: Connection is null or closed");
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "✗ Connection failed!", ex);
            logger.severe("Error: " + ex.getMessage());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "✗ Unexpected error!", ex);
            logger.severe("Error: " + ex.getMessage());
        }
    }
}

