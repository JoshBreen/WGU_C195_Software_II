package Utilities;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Joshua Breen C195 Project
 * DBConnection to establish, and close the connection to the database
 */
public class DBConnection{

    private static final String protocol = "jdbc";
    private static final String vendorName = ":mysql:";
    private static final String domainName = "//wgudb.ucertify.com:3306/";
    private static final String dbName = "WJ06MXf";

    private static final String jdbcURL = protocol + vendorName + domainName + dbName;

    private static final String MYSQLJDBCDriver = "com.mysql.jdbc.Driver";

    private static final String username = "U06MXf";
    private static final String password = "53688806933";
    private static Connection conn = null;

    /**
     * Starts the connection and returns conn
     * @return conn
     */
    public static Connection startConnection(){
        try {
            Class.forName(MYSQLJDBCDriver);
            conn = DriverManager.getConnection(jdbcURL, username, password);

            System.out.println("Connection Successful");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * Closes the connection
     */
    public static void closeConnection(){
        try {
            conn.close();
        }
        catch (Exception e){
        }
    }

    /**
     * Getter for connection
     * @return conn
     */
    public static Connection getConnection() {
        return conn;
    }
}
