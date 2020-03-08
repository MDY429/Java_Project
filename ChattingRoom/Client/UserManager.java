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

    /**
     * Server handle user sign up new account.
     * 
     * @param pkg         The input of data package.
     * @param dataHandler The input of corresponding data handler.
     */
    public void userSignIn(DataPackage pkg, DataHandler dataHandler){
        boolean checkdatabase = true;
        // TODO: Here will connect Database and check other details.
        if(checkdatabase){
            System.out.println("UserManager - signIn");

            int id = 1; // TODO: This value should get from Database.

            User user = new User(id, pkg.userName);
            user.setPassword(pkg.userPw);

            // Get correspond data handler.
            user.setDataHandler(dataHandler);

            // Set user status.
            user.setUserStatus(User.UserStatus.ONLINE);

            onlineUsers.put(user.userId, user);

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
        boolean checkdatabase = true;
        // TODO: Here will connect Database and check other details.
        if (checkdatabase) {
            System.out.println("UserManager - signUp");
            
            int id = 1; // TODO: This value should get from Database.
            
            User user = new User(pkg.userId, pkg.userName);
            user.setPassword(pkg.userPw);

            // Get correspond data handler.
            user.setDataHandler(dataHandler);

            // Set user status.
            user.setUserStatus(User.UserStatus.ONLINE);

            onlineUsers.put(user.userId, user);

            pkg.flag = 1;
            pkg.userId = user.userId;
            pkg.userName = user.userName;
            pkg.userPw = user.getPassword();
            
            // Send back to Client and return success.
            user.sendDataPackage(pkg);
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
     * @param pkg
     * @param dataHandler
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
        if(sendToUser != null && sendToUser.sendDataPackage(sendMsg)) {
            pkg.flag = 1;
            // Return success back.
            dataHandler.sendDataHandle(pkg.toString());
        }
        else {
            System.out.println("Sending message fail.");
        }

    }
}