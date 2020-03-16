import java.io.FileNotFoundException;
import java.sql.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author ynz
 * create at 2020-03-07 00:54
 * @description:this is the class for
 **/

public class Database_Conntect {
    private final String URL = "jdbc:postgresql://mod-msc-sw1.cs.bham.ac.uk:5432/chicago?serverTimezone=UTC&amp;characterEncoding=utf-8";
    private final String Driver = "org.postgresql.Driver";
    private final String UserName = "chicago";
    private final String Password = "vcm5e28kr0";
    private Connection conntect = null;

    public Connection DB_conntect() {
        try {
            Class.forName(Driver);
            conntect = DriverManager.getConnection(URL, UserName, Password);
            System.out.println("conntect success");
            return conntect;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Driver is wrong");
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("conntect fail");
            return null;
        }
    }
}