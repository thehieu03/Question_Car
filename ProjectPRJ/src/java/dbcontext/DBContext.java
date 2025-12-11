package dbcontext;

import java.io.IOException;
import java.io.InputStream;
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

    public DBContext() {
        try {
            Properties properties = new Properties();
            InputStream inputStream = null;
            
            String[] possiblePaths = {
                "ConnectDB.properties",
                "../ConnectDB.properties",
                "../../ConnectDB.properties",
                "conf/ConnectDB.properties",
                "../conf/ConnectDB.properties"
            };
            
            for (String path : possiblePaths) {
                inputStream = getClass().getClassLoader().getResourceAsStream(path);
                if (inputStream != null) {
                    System.out.println("Found properties file at: " + path);
                    break;
                }
            }
            
            if (inputStream == null) {
                throw new IOException("Cannot find ConnectDB.properties file. Tried paths: " + String.join(", ", possiblePaths));
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
                System.out.println("Database connection established successfully!");
            }
        } catch (ClassNotFoundException | SQLException | IOException ex) {
            Logger.getLogger(DBContext.class.getName()).log(Level.SEVERE, "Failed to connect to database", ex);
            System.err.println("Database connection error: " + ex.getMessage());
            ex.printStackTrace();
            connection = null;
        }
    }
    
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("Connection is null or closed, attempting to reconnect...");
                reconnect();
            }
        } catch (SQLException ex) {
            System.err.println("Error checking connection: " + ex.getMessage());
            reconnect();
        }
        return connection;
    }
    
    private void reconnect() {
        try {
            Properties properties = new Properties();
            InputStream inputStream = null;
            
            String[] possiblePaths = {
                "ConnectDB.properties",
                "../ConnectDB.properties",
                "../../ConnectDB.properties",
                "conf/ConnectDB.properties",
                "../conf/ConnectDB.properties"
            };
            
            for (String path : possiblePaths) {
                inputStream = getClass().getClassLoader().getResourceAsStream(path);
                if (inputStream != null) {
                    System.out.println("Reconnecting - Found properties file at: " + path);
                    break;
                }
            }
            
            if (inputStream == null) {
                System.err.println("ERROR: Cannot find ConnectDB.properties file for reconnection!");
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
                System.out.println("Reconnection successful!");
            }
        } catch (Exception ex) {
            System.err.println("Reconnection failed: " + ex.getMessage());
            ex.printStackTrace();
            connection = null;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Testing Database Connection ===");
        
        try {
            DBContext dbContext = new DBContext();
            Connection conn = dbContext.connection;
            
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Connection successful!");
                System.out.println("✓ Database: " + conn.getCatalog());
                System.out.println("✓ Connection URL: " + conn.getMetaData().getURL());
                System.out.println("✓ Driver: " + conn.getMetaData().getDriverName());
                System.out.println("✓ Driver Version: " + conn.getMetaData().getDriverVersion());
                
                System.out.println("\n=== Testing Query ===");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM Users");
                
                if (rs.next()) {
                    int total = rs.getInt("total");
                    System.out.println("✓ Query successful!");
                    System.out.println("✓ Total users in database: " + total);
                }
                
                rs.close();
                stmt.close();
                conn.close();
                System.out.println("\n✓ Connection closed successfully!");
            } else {
                System.out.println("✗ Connection failed: Connection is null or closed");
            }
        } catch (SQLException ex) {
            System.out.println("✗ Connection failed!");
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } catch (Exception ex) {
            System.out.println("✗ Unexpected error!");
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}

