import java.sql.*;

/**
 * @author ynz
 * create at 2020-03-08 23:34
 * @description:this is the class for
 **/

public class DatabaseManager {
    Database_Connect dc=new Database_Connect();
    // Database_Connect ds=new Database_Connect();

    private Connection connect=null;
    //test whether table exist
    public DatabaseManager(){
        if(!table_Exist()){
            add_Table();
        }
        connect=dc.DB_connect();
    }


    private boolean table_Exist(){
        return true;
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

            Statement statement_friends=connect.createStatement();
            String user_friendlist="create table User_friendlist(User_id integer not null,username varchar(20) not null,Friend_id integer not null,Friend_username varchar(20) not null)";
            statement_friends.executeUpdate(user_friendlist);

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
    public  boolean add_users(String name,String password,String email) {
        if (connect == null) {
            return false;
        }
        if (password.indexOf(" ") != -1) {
            System.out.println("passwords are not allowed to have blank");
            return false;
        }
        if (password.length() < 5 || name.length() < 5) {
            System.out.println("you should set longer passwords or username");
           return false;
        }
        if (!isValidword(name)) {
            System.out.println("illegal usename");
            return false;
        }
        if (!email.contains("@")) {
            System.out.println("illegal email format ");
            return false;
        }
            else{
            try {
                PreparedStatement insert_user = connect.prepareStatement("insert into UserList(username,password,email) values(?,?,?)");
                insert_user.setString(1, name);
                insert_user.setString(2, password);
                insert_user.setString(3, email);
                int num = insert_user.executeUpdate();
                System.out.println("add successfully");
                return num > 0;
            } catch (SQLException e) {
                System.out.println("mistake in adding user");
                return false;
            }
        }
    }
    //isValidWord
    public boolean isValidword(String word){
        String regex="^[a-zA-Z0-9]+$";
        return word.matches(regex);
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

    //search user for add friends
    public int search_user_addfriends(String userName){
        int id_user=-1;
        if(connect==null){
            System.out.println("database do not connect");
        }
        try{
            PreparedStatement search=connect.prepareStatement("select id from UserList where username=?");
            search.setString(1,userName);
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

    //add friends to friends list
    public boolean add_friends(Integer id, String user_username,Integer ID_friend,String friends_username){
        if(connect==null){
            return false;
        }
        if(ID_friend<0){
            System.out.println("add friends unsuccessfully");
            return false;
        }
        try{
            PreparedStatement insert_content=connect.prepareStatement("insert into User_friendlist(User_id,username,Friend_id,Friend_username) values(?,?,?,?)");
            insert_content.setInt(1,id);
            insert_content.setString(2,user_username);
            insert_content.setInt(3,ID_friend);
            insert_content.setString(4,friends_username);
            int num=insert_content.executeUpdate();
            System.out.println("add friends successfully");
            return num>0;
        } catch (SQLException e) {
            System.out.println("add friends unsuccessfully");
            e.printStackTrace();
            return false;
        }
    }

    //change passwords when user forget the passwords
    public boolean change_passwords(Integer id,String passwords){
        if(connect==null){
            return false;
        }
        if(passwords.length()<5){
            System.out.println("this passwords are too short");
            return false;
        }
        try {
            PreparedStatement change_passowrds = connect.prepareStatement("UPDATE UserList set password=? where id=?");
            change_passowrds.setString(1, passwords);
            change_passowrds.setInt(2, id);
            int i = change_passowrds.executeUpdate();
            System.out.println("change passwords successfully");
            return i>0;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("change passwords unsuccessfully");
            return false;
        }
    }


    public static void main(String[] args) {
        DatabaseManager db = new DatabaseManager();
        db.change_passwords(1,"bbbbbbb");
    //    db.add_friends(1,"aaaa",-1,"bbbb");
     //   db.add_users("bbbbbb","asasda","asdasd@");
//        System.out.println(db.search_user_addfriends("bbbbbb"));
       // db.add_users("ifu","asd","asdasd");
       // db.add_users("+++++++","asd","asdasd");
        //db.add_users("2","2","333");//success
           //db.add_Table();//success
           // db.add_history(1,2,"a");//success
//            db.add_history(1,2,"abc");//success
//            db.add_history(1,2,"bac");//success
//            db.add_history(2,2,"cba");//success
//            db.add_history(3,2,"a");//success
        // db.delete_history(2,"abc");//success
        // db.delete_user(2);//success
        //System.out.println(db.search_user("5","2"));

         //db.search_history(1,2,"a");
    }
}
