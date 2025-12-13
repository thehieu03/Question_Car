package dbcontext;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBContext {
    protected Connection connection;

    private static final String DB_URL = "jdbc:sqlserver://localhost:1433;databaseName=DrivingLicenseExam;encrypt=true;trustServerCertificate=true";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "123";

    public DBContext() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException | SQLException ex) {
            connection = null;
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                reconnect();
            }
        } catch (SQLException ex) {
            reconnect();
        }
        return connection;
    }

    private void reconnect() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (Exception ex) {
            connection = null;
        }
    }

    public static void main(String[] args) {
        try {
            DBContext dbContext = new DBContext();
            Connection conn = dbContext.connection;

            if (conn != null && !conn.isClosed()) {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM Users");

                if (rs.next()) {
                    int total = rs.getInt("total");
                }

                rs.close();
                stmt.close();
                conn.close();
            }
        } catch (SQLException ex) {
        } catch (Exception ex) {
        }
    }
}
