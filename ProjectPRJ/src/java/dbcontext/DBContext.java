package dbcontext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBContext {
    protected Connection connection;
    private static final Logger logger = Logger.getLogger(DBContext.class.getName());

    private InputStream findPropertiesFile() {
        String[] classpathPaths = {
            "ConnectDB.properties",
            "../ConnectDB.properties",
            "../../ConnectDB.properties",
            "conf/ConnectDB.properties",
            "../conf/ConnectDB.properties",
            "../../conf/ConnectDB.properties",
            "src/conf/ConnectDB.properties"
        };
        
        for (String path : classpathPaths) {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
            if (inputStream != null) {
                logger.info("Found properties file in classpath at: " + path);
                return inputStream;
            }
        }
        
        String[] fileSystemPaths = {
            "src/conf/ConnectDB.properties",
            "../src/conf/ConnectDB.properties",
            "../../src/conf/ConnectDB.properties",
            "conf/ConnectDB.properties",
            "../conf/ConnectDB.properties",
            "ConnectDB.properties",
            "../ConnectDB.properties"
        };
        
        String userDir = System.getProperty("user.dir");
        logger.info("Current working directory: " + userDir);
        
        for (String path : fileSystemPaths) {
            try {
                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    logger.info("Found properties file in filesystem at: " + file.getAbsolutePath());
                    return new FileInputStream(file);
                }
                
                File fileFromUserDir = new File(userDir, path);
                if (fileFromUserDir.exists() && fileFromUserDir.isFile()) {
                    logger.info("Found properties file in filesystem at: " + fileFromUserDir.getAbsolutePath());
                    return new FileInputStream(fileFromUserDir);
                }
            } catch (IOException ex) {
                logger.fine("Could not read file at: " + path);
            }
        }
        
        return null;
    }

    public DBContext() {
        try {
            Properties properties = new Properties();
            InputStream inputStream = findPropertiesFile();
            
            if (inputStream == null) {
                throw new IOException("Cannot find ConnectDB.properties file. Please ensure the file exists in src/conf/ or build/web/WEB-INF/classes/");
            }
            
            try {
                properties.load(inputStream);
            } catch (IOException ex) {
                Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, null, ex);
                throw ex;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }

            String user = properties.getProperty("userID");
            String pass = properties.getProperty("password");
            String url = properties.getProperty("url");

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url, user, pass);
            if (connection != null) {
                logger.info("Database connection established successfully!");
            }
        } catch (ClassNotFoundException | SQLException | IOException ex) {
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
            Properties properties = new Properties();
            InputStream inputStream = findPropertiesFile();
            
            if (inputStream == null) {
                logger.severe("ERROR: Cannot find ConnectDB.properties file for reconnection!");
                return;
            }
            
            properties.load(inputStream);
            inputStream.close();

            String user = properties.getProperty("userID");
            String pass = properties.getProperty("password");
            String url = properties.getProperty("url");

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(url, user, pass);
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

