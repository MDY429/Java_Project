import java.util.HashMap;
import java.util.Map;

/**
 * The UserManager is middle role of client and server.
 * 
 * @author Ta-Yu Mar
 * @version 0.2 beta 2020-03-18
 */
public class UserManager {

    // Record online users.
    Map<Integer, User> onlineUsers = new HashMap<>();

    // Record handler and users.
    Map<DataHandler, User> userHandler = new HashMap<>();
    
    // Database Manager.
    DatabaseManager db;
    public UserManager() {
        this.db = new DatabaseManager();
    }

    /**
     * Server handle user sign up new account.
     * 
     * @param pkg         The input of data package.
     * @param dataHandler The input of corresponding data handler.
     */
    public void userSignIn(DataPackage pkg, DataHandler dataHandler) {

        boolean checkDatabase = db.add_Table();
        if(checkDatabase) {
            System.out.println("Create new table.");
        }
        if(!checkDatabase) {
            int id = db.search_user(pkg.userName, pkg.userPw);
            if(id <= 0) {
                System.out.println("Get User information fail.");
                pkg.flag = 0;
                dataHandler.sendDataHandle(pkg.toString());
                return;
            }

            if(onlineUsers.get(id) != null) {
                // System.out.println("Login Duplicate");
                pkg.flag = 2;
                pkg.userId = id;
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
        int checkAddUser = db.add_users(pkg.userName, pkg.userPw, pkg.email);
        if(checkDatabase) {
            System.out.println("Create new table.");
        }
        if(!checkDatabase && checkAddUser > 0) {
            pkg.flag = 1;            
        }
        else if(!checkDatabase && checkAddUser == -1) {
            // Cannot connect DB.
            pkg.flag = 2;
        }
        else if(!checkDatabase && checkAddUser == -2) {
            // Error syntax.
            pkg.flag = 3;
        }
        else if(!checkDatabase && checkAddUser == -3) {
            // DB exception (duplicate)
            pkg.flag = 4; 
        }
        dataHandler.sendDataHandle(pkg.toString());
    }

    /**
     * Server handle sending message to specific user.
     * 
     * @param pkg         The input of data package.
     * @param dataHandler The input of corresponding data handler.
     */
    public void sendPrivateChat(DataPackage pkg, DataHandler dataHandler) {

        // Create a new package to put the chatting information.
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
            // If the person who is disconnected.
            pkg.flag = 2;
            dataHandler.sendDataHandle(pkg.toString());
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
     * 
     * @param pkg         The input of data package.
     * @param dataHandler The input of corresponding data handler.
     */
    public void sendReadStatus(DataPackage pkg, DataHandler dataHandler) {

        // TODO: Here could implement "user read" status.
    }

    /**
     * Server handle the user to get others user online status.
     * 
     * @param pkg         The input of data package.
     * @param dataHandler The input of corresponding data handler.
     */
    public void getOnlineUsers(DataPackage pkg, DataHandler dataHandler) {

        // Create a new package to collect online users.
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
     * 
     * @param dataHandler The input of corresponding data handler.
     */
    public void userDisconnect(DataHandler dataHandler) {

        User user = userHandler.get(dataHandler);
        if(user != null) {
            user.setUserStatus(User.UserStatus.OFFLINE);
            onlineUsers.remove(user.userId);
            userHandler.remove(dataHandler);
        }

        // Notify to other users, someone logout.
        notifyOthersUsers(null, dataHandler);
    }

    /**
     * Server handle inform others users, there a new user get online.
     * 
     * @param pkg         The input of data package.
     * @param dataHandler The input of corresponding data handler.
     */
    public void notifyOthersUsers(DataPackage pkg, DataHandler dataHandler) {

        // Create new package to notify every online user to update online list.
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

    /**
     * Server handle duplicate login, force the account exit. And notify other
     * users.
     * 
     * @param pkg         The input of data package.
     * @param dataHandler The input of corresponding data handler.
     */
    public void forceLogout(DataPackage pkg, DataHandler dataHandler) {

        User user = onlineUsers.get(pkg.userId);
        DataHandler userDataHandler = user.getDataHandler();

        if(user != null) {
            user.setUserStatus(User.UserStatus.OFFLINE);
            onlineUsers.remove(user.userId);
            userHandler.remove(userDataHandler);
        }

        // Create a new package to force logout.
        DataPackage kickUser = new DataPackage();
        kickUser.type = 6;
        userDataHandler.sendDataHandle(kickUser.toString());

        // Notify to other users, someone logout.
        notifyOthersUsers(null, userDataHandler);
    }
    
}