import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author ynz
 * create at 2020-03-07 00:54
 * @description:this is the class for
 **/

public class Database_Connect {
    private final String URL = "jdbc:postgresql://mod-msc-sw1.cs.bham.ac.uk:5432/chicago?serverTimezone=UTC&amp;characterEncoding=utf-8";
    private final String Driver = "org.postgresql.Driver";
    private final String UserName = "chicago";
    private final String Password = "vcm5e28kr0";
    private Connection connect = null;

    public Connection DB_connect() {
        try {
            Class.forName(Driver);
            connect = DriverManager.getConnection(URL, UserName, Password);
            System.out.println("connect success");
            return connect;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Driver is wrong");
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("connect fail");
            return null;
        }
    }
}