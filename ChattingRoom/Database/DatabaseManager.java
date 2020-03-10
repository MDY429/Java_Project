import java.sql.*;

/**
 * @author ynz
 * create at 2020-03-08 23:34
 * @description:this is the class for
 **/

public class DatabaseManager {
    private final String URL="jdbc:postgresql://mod-msc-sw1.cs.bham.ac.uk:5432/chicago";
    private final String Driver="org.postgresql.Driver";
    private final String UserName="chicago";
    private final String Password="vcm5e28kr0";
    private Connection connect=null;

    //test whether table exist
    public DatabaseManager(){
        if(!table_Exist()){
            add_Table();
        }
        DB_connect();
    }

    private boolean table_Exist(){
        return true;
    }

    //method for test database connect
    public boolean DB_connect(){
        try {
            Class.forName(Driver);
            connect= DriverManager.getConnection(URL,UserName,Password);
            System.out.println("connect success");
            return true;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Driver is wrong");
            return false;
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("connect fail");
            return false;
        }
    }
    //method for adding table
    public boolean add_Table(){
        if(connect==null){
            return false;
        }
        try{
            Statement statement_createTable=connect.createStatement();
            String postgre_friendlist="create table UserList(ID serial primary key,username  varchar(20) not null unique,password varchar(20) not null,email varchar(20) not null)";
            statement_createTable.executeUpdate(postgre_friendlist);

            String postgre_history="create table history(ID integer not null,ID_friend integer not null,content varchar(200),time_history TIMESTAMP(14) default current_timestamp)";
            statement_createTable.executeUpdate(postgre_history);
            System.out.println("tables are created successfully");
            return true;
        } catch (SQLException e) {
            System.out.println("tables are created unsuccessfully ");
            return false;
        }

    }

    //insert user
    public boolean add_users(String name,String password,String email){
        if(connect==null){
            return false;
        }
        try{
            PreparedStatement insert_user=connect.prepareStatement("insert into UserList(username,password,email) values(?,?,?)");
            insert_user.setString(1,name);
            insert_user.setString(2,password);
            insert_user.setString(3,email);
            int num=insert_user.executeUpdate();
            System.out.println("add successfully");
            return num>0;
        }catch (SQLException e) {
            System.out.println("mistake in adding user");
            return false;
        }
    }
    //search user
    public int search_user(String userName,String password){
        int id_user=-1;
        if(connect==null){
            System.out.println("database do not connect");
        }
        try{
            PreparedStatement search=connect.prepareStatement("select id from UserList where username=? and password=?");
            search.setString(1,userName);
            search.setString(2,password);
            ResultSet resultSet=search.executeQuery();
            while(resultSet.next()){
                id_user=resultSet.getInt(1);
            }
            return id_user;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //add history
    public boolean add_history(Integer id, Integer ID_friend,String content){
//        java.util.Date date1 = new java.util.Date();
//        long time2=date1.getTime();
//        Timestamp timestamp=new Timestamp(time2);
//        Date date=new Date(time2);
        if(connect==null){
            return false;
        }
        try{
            PreparedStatement insert_content=connect.prepareStatement("insert into history(ID,ID_friend,content) values(?,?,?)");
            insert_content.setInt(1,id);
            insert_content.setInt(2,ID_friend);
            insert_content.setString(3,content);
            //insert_content.setTimestamp(4, timestamp);
            int num=insert_content.executeUpdate();
            System.out.println("add history successfully");
            return num>0;
        } catch (SQLException e) {
            System.out.println("add history unsuccessfully");
            e.printStackTrace();
            return false;
        }
    }
    //delete user
    public void delete_user(Integer id){
        if(connect==null){
            System.out.println("database is not connected");;
        }
        try{
            //String delete_DB="delete from UserList where ID= ?";
            PreparedStatement delete_user=connect.prepareStatement("delete from UserList where ID=?");
            delete_user.setInt(1,id);
            delete_user.executeUpdate();
            System.out.println("delete user successfully");

        } catch (SQLException e) {
            System.out.println("delete user unsuccessfully");
            e.printStackTrace();
        }
    }


    //delete history
    public void delete_history(Integer id_friend,String content){
        if(connect==null){
            System.out.println("database is not connected");;
        }
        try{
            PreparedStatement delete_user=connect.prepareStatement("delete from history where ID_friend=?and content=?");
            delete_user.setInt(1,id_friend);
            delete_user.setString(2,content);
            delete_user.executeUpdate();
            System.out.println("delete history successfully");

        } catch (SQLException e) {
            System.out.println("delete history unsuccessfully");
            e.printStackTrace();
        }
    }


    public boolean search_history(Integer id,Integer id_friend,String content){
        if(connect==null){
            return false;
        }
        try{
            PreparedStatement history=connect.prepareStatement("select*from history where id=? and id_friend=? and content like ? order by time_history ");
            history.setInt(1,id);
            history.setInt(2,id_friend);
            history.setString(3,"%"+content+"%");
            history.executeQuery();
            ResultSet resultSet=history.executeQuery();
            while(resultSet.next()){
                int id_user=resultSet.getInt(1);
                int friend_id=resultSet.getInt(2);
                String history_content=resultSet.getString(3);
                Timestamp history_time=resultSet.getTimestamp(4);
                System.out.println(id_user+"---------"+friend_id+"--------"+history_content+"-------"+history_time.toString().substring(0,19));
            }
            System.out.println("search history successfully");
            return true;
        } catch (SQLException e) {
            System.out.println("search history unsuccessfully");
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();
        //db.add_users("2","2","333");//success
           //db.add_Table();//success
//            db.add_history(1,2,"a");//success
//            db.add_history(1,2,"abc");//success
//            db.add_history(1,2,"bac");//success
//            db.add_history(2,2,"cba");//success
//            db.add_history(3,2,"a");//success
        // db.delete_history(2,"abc");//success
        // db.delete_user(2);//success
        System.out.println(db.search_user("5","2"));

         //db.search_history(1,2,"a");
    }
}
