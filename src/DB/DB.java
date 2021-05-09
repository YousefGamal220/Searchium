package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * The DB class is responsible for connecting to a MySQL Database and sending queries to it
 */
public class DB {
    public Connection conn = null;

    /*
     * The constructors gets the connection to the Crawler Database
     */
    public DB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/Crawler";
            conn = DriverManager.getConnection(url, "root", "admin123");
            System.out.println("Connected to DB");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public ResultSet selectQuery(String query) throws SQLException {
        Statement statement = conn.createStatement();
        return statement.executeQuery(query); // executeQuery() executes only SELECT statements
    }

    public void modifyQuery(String query) throws SQLException {
        Statement statement = conn.createStatement();
        statement.execute(query); // execute() executes SELECT/INSERT/UPDATE statements
    }

    /*
    * Checks if there is a DB connection and close it
    */
    public void closeDBConnection() throws Throwable {
        if (conn != null || !conn.isClosed()) conn.close();
    }
}
