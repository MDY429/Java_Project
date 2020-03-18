import java.util.HashMap;
import java.util.Map;

/**
 * The UserManager is middle role of client and server.
 * @author Ta-Yu Mar
 * @version 0.1 beta 2020-03-05
 */
public class UserManager {

    // Record online users.
    Map<Integer, User> onlineUsers = new HashMap<>();
    Map<DataHandler, User> userHandler = new HashMap<>();

    DatabaseManager db;
    public UserManager(){
        this.db = new DatabaseManager();
    }

    /**
     * Server handle user sign up new account.
     * 
     * @param pkg         The input of data package.
     * @param dataHandler The input of corresponding data handler.
     */
    public void userSignIn(DataPackage pkg, DataHandler dataHandler){
        boolean checkDatabase = db.add_Table();
        if(checkDatabase){
            System.out.println("Create new table.");
        }
        if(!checkDatabase){
            int id = db.search_user(pkg.userName, pkg.userPw);
            System.out.println("id:----"+id);
            if(id <= 0) {
                System.out.println("Get User information fail.");
                pkg.flag = 0;
                dataHandler.sendDataHandle(pkg.toString());
                return;
            }

            User user = new User(id, pkg.userName);
            user.setPassword(pkg.userPw);

            // Get correspond data handler.
            user.setDataHandler(dataHandler);

            // Set user status.
            user.setUserStatus(User.UserStatus.ONLINE);

            onlineUsers.put(user.userId, user);
            userHandler.put(dataHandler, user);

            pkg.flag = 1;
            pkg.userId = user.userId;
            pkg.userName = user.userName;
            pkg.userPw = user.getPassword();

            // Send back to Client and return success.
            user.sendDataPackage(pkg);
        } else {
            // Send back login fail.
            System.out.println("User sign In Error!!!");
            pkg.flag = 0;

            dataHandler.sendDataHandle(pkg.toString());
            return;
        }
    }

    /**
     * Server handle user sign up new account.
     * 
     * @param pkg         The input of data package.
     * @param dataHandler The input of corresponding data handler.
     */
    public void userSignUp(DataPackage pkg, DataHandler dataHandler) {
        boolean checkDatabase = db.add_Table();
        if(checkDatabase){
            System.out.println("Create new table.");
        }
        if(!checkDatabase && db.add_users(pkg.userName, pkg.userPw, pkg.email)) {
            pkg.flag = 1;
            
            dataHandler.sendDataHandle(pkg.toString());
        }
        else {
            // Send back sign up fail.
            System.out.println("User sign up Error!!!");
            pkg.flag = 0;
            
            dataHandler.sendDataHandle(pkg.toString());
            return;
        }
    }

    /**
     * Server handle sending message to specific user. 
     * @param pkg The input of data package.
     * @param dataHandler The input of corresponding data handler.
     */
    public void sendPrivateChat(DataPackage pkg, DataHandler dataHandler) {

        DataPackage sendMsg = new DataPackage();
        sendMsg.type = 2;
        sendMsg.userId = pkg.receiveUserId;
        sendMsg.userName = pkg.receiveUserName;
        sendMsg.receiveUserId = pkg.userId;
        sendMsg.receiveUserName = pkg.userName;
        sendMsg.message = pkg.message;
        System.out.println(sendMsg.toString());

        User sendToUser = onlineUsers.get(pkg.receiveUserId);
        if(sendToUser == null) {
            // TODO: offline message.
        }
        if(sendToUser != null && sendToUser.sendDataPackage(sendMsg)) {
            pkg.flag = 1;
            // Return success back.
            dataHandler.sendDataHandle(pkg.toString());
        }
        else {
            System.out.println("Sending message fail.");
        }

    }

    /**
     * Server handle. 
     * @param pkg The input of data package.
     * @param dataHandler The input of corresponding data handler.
     */
    public void sendReadStatus(DataPackage pkg, DataHandler dataHandler) {

    }

    /**
     * Server handle the user to get others user online status.
     * @param pkg The input of data package.
     * @param dataHandler The input of corresponding data handler.
     */
    public void getOnlineUsers(DataPackage pkg, DataHandler dataHandler) {
        DataPackage getUsers = new DataPackage();
        getUsers.type = 4;
        onlineUsers.forEach((id, user) -> {
            if(user.getUserStatus() == User.UserStatus.ONLINE) {
                getUsers.addOnlineUserInfo(user.userId, user.userName);
            }
        });

        dataHandler.sendDataHandle(getUsers.toString());
    }

    /**
     * Server handle the user disconnect and notify other users.
     * @param dataHandler The input of corresponding data handler.
     */
    public void userDisconnect(DataHandler dataHandler) {
        User user = userHandler.get(dataHandler);
        if(user != null) {
            user.setUserStatus(User.UserStatus.OFFLINE);
            onlineUsers.remove(user.userId);
            userHandler.remove(dataHandler);
        }
        notifyOthersUsers(null, dataHandler);
    }

    /**
     * Server handle inform others users, there a new user get online.
     * @param pkg The input of data package.
     * @param dataHandler The input of corresponding data handler.
     */
    public void notifyOthersUsers(DataPackage pkg, DataHandler dataHandler) {
        DataPackage notify = new DataPackage();
        notify.type = 5;
        onlineUsers.forEach((id, user) -> {
            if(user.getUserStatus() == User.UserStatus.ONLINE) {
                notify.addOnlineUserInfo(user.userId, user.userName);
            }
        });
        onlineUsers.forEach((id, user) -> {
            if(pkg == null) {
                user.sendDataPackage(notify);
            }
            else if(pkg.userId != user.userId) {
                user.sendDataPackage(notify);
            }
        });
    
    }

    
}