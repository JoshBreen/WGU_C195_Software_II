package Utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Joshua Breen C195 Project
 * DBQuery used for setting and getting prepared statements
 */

public class DBQuery {

    private static PreparedStatement preparedStatement;

    /**
     * Setter for Prepared Statement
     * @param conn connection to database
     * @param sqlStatement sqlstatement used to access information from the DB
     * @throws SQLException
     */
    public static void setPreparedStatement (Connection conn, String sqlStatement) throws SQLException {
        preparedStatement = conn.prepareStatement(sqlStatement);
    }

    /**
     * Getter for prepared statement
     * @return preparedStatement
     */
    public static PreparedStatement getPreparedStatement() {
        return preparedStatement;

    }
}
