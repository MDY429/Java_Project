import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.BooleanProperty;

/**
 * This is Client.
 * @author Ta-Yu Mar
 * @version 0.1 beta 2020-03-05
 */
public class ChatClient {

	// Create a user type to handle the client's request.
	User user = new User();

	// Store the online users.
	List<User> onlineUsersList = new ArrayList<>();

	DataHandler dataHandler = null;
	private static final int PORT_NUMBER = 50000;
	private static final int MAX_TRY_CONNECT = 20;
	private static int tryConnect = 0;

	/**
	 * Client ask for registering new account.
	 * @param userName User's username
	 * @param userPw   User's password.
	 */
	public void sendSignUp(String userName, String userPw, String email) {
		// TODO: Here shold be connected by application.
		System.out.printf("Sign up for name:%s and pw:%s\n", userName, userPw);
		DataPackage pkg = new DataPackage();
		pkg.type = 1;
		pkg.userName = userName;
		pkg.userPw = userPw;
		pkg.email = email;
        user.sendDataPackage(pkg);
	}

	/**
	 * The result from database and broadcast to GUI.
	 * @param pkg The input of data package
	 * @param booleanProperty The input of BooleanProperty
	 */
	public void registerResult(DataPackage pkg, BooleanProperty booleanProperty) {
		// Use BooleanProperty to do the broadcast.
		if(pkg.flag ==1) {
			booleanProperty.set(true);
		}
		else {
			booleanProperty.set(false);
		}
	}
	
	/**
	 * 
	 * @param pkg
	 */
	public void receiveMsg(DataPackage pkg) {
		// TODO: Waiting for GUI.
		if(pkg.flag == 1){
			System.out.println(pkg.userName + " SEND SUCCESS!!");
		}
		else{
			System.out.println(pkg.userName + " got " + pkg.receiveUserName +" :"+pkg.message);
			// TODO: Show on GUI MSG Box.
		}
	}

	public void findOnlineUsers() {
		// TODO: GUI send pkg to all online user, who is comming.
		DataPackage pkg = new DataPackage();
		pkg.type = 4;
		dataHandler.sendDataHandle(pkg.toString());
	}

	public void getOnlineUsers(DataPackage pkg){
		if(pkg.onlineUser == null) {
			return;
		}
		onlineUsersList.clear();
		for(DataPackage.OnlineUser user : pkg.onlineUser) {
			onlineUsersList.add(new User(user.userId, user.userName));
		}

		// notify Others user i am online to re-flash the online status.
		DataPackage notifyOthers = new DataPackage();
		notifyOthers.type = 5;
		notifyOthers.userId = user.userId;
		notifyOthers.userName = user.userName;
		dataHandler.sendDataHandle(notifyOthers.toString());

		// TODO: updating online user list.
	}

	public void updateOnlineUsers(DataPackage pkg) {
		onlineUsersList.clear();
		for(DataPackage.OnlineUser user : pkg.onlineUser) {
			onlineUsersList.add(new User(user.userId, user.userName));
		}
		// TODO: GUI reload the onlineUsersList.
	}

	/**
	 * The Client receives the data from server.
	 * @param booleanProperty The input of BooleanProperty
	 * @return The integer of corresponding status.
	 */
	private int receiveFromServer(BooleanProperty booleanProperty) {
		if (!user.getDataHandler().isConnected()) {
			// Try to reconnect the server.
			userConnectToServer(PORT_NUMBER);
			System.out.println("Cannot Connect Server");
			return -2;
		}

		List<DataPackage> packages = user.getDataHandler().receiveHandle();
		if (packages != null) {
			// Receive from server, set tryConnect to 0.
			tryConnect = 0;
			// System.out.printf("got %d packages\n", packages.size());
			for (DataPackage pkg : packages) {
				// System.out.println("Process pkg: " + pkg.toString());
				switch (pkg.type) {
					case 0:
						System.out.println("[" + pkg.userName + "]: SIGN IN");
						break;
					case 1:
						System.out.println("[" + pkg.userName + "] SIGN UP new account: " + pkg.toString());
						registerResult(pkg, booleanProperty);
						break;
					case 2:						
						System.out.println("Send MSG");
						System.out.println(pkg.toString());
						receiveMsg(pkg);
						break;
					case 3:
						System.out.println("Receive MSG");
						break;
					case 4:
						System.out.println("get online users");
						getOnlineUsers(pkg);
						break;
					case 5:
						System.out.println("Update online users");
						updateOnlineUsers(pkg);
						break;

					default:
						System.out.println("unknown package type: " + pkg.type + ", content: " + pkg.toString());
						break;
				}
			}
		} else {
			System.out.println("got null packages, connection may have been broken");
			// Try to reconnect the server.
			userConnectToServer(PORT_NUMBER);
			return -1;
		}
		return packages.size();
	}

	/**
	 * User provide the host IP address and specific port to connect to Server.
	 * @param port The corresponding Server port.
	 * @return boolean
	 */
	public boolean userConnectToServer(int port){
		if(user.getDataHandler() == null) {
			user.setDataHandler(new DataHandler());
		}
		return user.getDataHandler().connectToServer(port);
	}

	/**
	 * Start to run.
	 * @param booleanProperty The input of BooleanProperty.
	 */
	public void runMain(BooleanProperty booleanProperty) {

		// Connect to Server.
		// userConnectToServer(PORT_NUMBER);
		
		// while (true) {
			int num = receiveFromServer(booleanProperty);
            if (num <= 0) {
				tryConnect++;
				if(tryConnect > MAX_TRY_CONNECT){
					System.err.println("Cannot connect server, close the application.");
					System.exit(0);
				}
                // If no data or cannot connect server. Execute the sleep.
                // try {
                //     Thread.sleep(20);
                // } catch (Exception e) {
                //     System.out.println("Exception for waking up from sleep unexpectedly: " + e.toString());
                // }
            }
        // }
    }

	// public static void main(String[] args) {
	// 	ChatClient runClient = new ChatClient();
	// 	runClient.runMain();
	// }

}