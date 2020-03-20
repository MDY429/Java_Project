import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This is Client.
 * 
 * @author Ta-Yu Mar
 * @version 0.2 beta 2020-03-18
 */
public class ChatClient {

	// Create a user type to handle the client's request.
	User user = new User();

	// Store the online users.
	List<User> onlineUsersList = new ArrayList<>();

	// The data handler.
	DataHandler dataHandler = null;

	/**
	 * Client enter the information to register new account.
	 * 
	 * @param userName User's username
	 * @param userPw   User's password.
	 */
	public void sendSignUp(String userName, String userPw, String email) {

		// Create a new package to pack the register information.
		DataPackage pkg = new DataPackage();
		pkg.type = 1;
		pkg.flag = 0;
		pkg.userName = userName;
		pkg.userPw = userPw;
		pkg.email = email;

		user.sendDataPackage(pkg);
	}

	/**
	 * The result from database and Server and return to GUI.
	 * 
	 * @param pkg             The input of data package
	 * @param IntegerProperty The input of IntegerProperty
	 */
	public void registerResult(DataPackage pkg, IntegerProperty integerProperty) {

		// Use IntegerProperty to do the broadcast.
		if(pkg.flag == 1) {
			// Create success.
			integerProperty.set(2);
		}
		else if(pkg.flag == 2) {
			// Cannot connect DB.
			integerProperty.set(3);
		}
		else if(pkg.flag == 3) {
			// Wrong syntax.
			integerProperty.set(4);
		}
		else if(pkg.flag == 4) {
			// DB exp (Duplicate).
			integerProperty.set(5);
		}
		else {
			integerProperty.set(1);
		}
	}

	/**
	 * Client enter userName and password to login account.
	 * 
	 * @param userName The input of userName.
	 * @param userPw   The input of user password.
	 */
	public void sendSignIn(String userName, String userPw) {

		// Create a new package to pack user login information.
		DataPackage pkg = new DataPackage();
		pkg.type = 0;
		pkg.userName = userName;
		pkg.userPw = userPw;

		user.sendDataPackage(pkg);
	}

	/**
	 * The sign in result, return back to gui.
	 * 
	 * @param pkg             The input of data package.
	 * @param integerProperty The input of integerProperty.
	 */
	public void signInResult(DataPackage pkg, IntegerProperty integerProperty) {

		// Update global variable.
		user.userName = pkg.userName;
		user.userId = pkg.userId;
		
		// Use IntegerProperty to do the broadcast.
		if(pkg.flag == 1) {
			// Login success.
			integerProperty.set(2);
		}
		else if(pkg.flag == 2) {
			// Login duplicate.
			integerProperty.set(3);
		}
		else {
			// Login fail.
			integerProperty.set(1);
		}
	}

	/**
	 * Client send the chatting information from gui to Server.
	 * 
	 * @param chatId   The userId you want to communicate.
	 * @param chatName The userName you want to communicate.
	 * @param msg      The chatting message.
	 */
	public void sendMsg(int chatId, String chatName, String msg) {

		// Create a new package which pack the chatting information.
		DataPackage chatPkg = new DataPackage();
		chatPkg.type = 2;
		chatPkg.userId = user.userId;
		chatPkg.userName = user.userName;
		chatPkg.receiveUserId = chatId;
		chatPkg.receiveUserName = chatName;
		chatPkg.message = msg;

		user.sendDataPackage(chatPkg);
	}
	
	/**
	 * The result of receiving message from other users, and whether you send msg
	 * success.
	 * 
	 * @param pkg             The input of chatting data package.
	 * @param stringProperty  The input of stringProperty.
	 * @param integerProperty The input of integerProperty.
	 */
	public void receiveMsg(DataPackage pkg, StringProperty stringProperty, IntegerProperty integerProperty) {

		if(pkg.flag == 1) {
			// Send succeed.
			System.out.println(pkg.userName + " SEND SUCCESS!!");
		}
		else if(pkg.flag == 2) {
			// Send Failed.
			System.out.println(pkg.userName + " SEND FAIL!!");
			stringProperty.set(pkg.receiveUserName +": I am not ONLINE.\n");
			stringProperty.set("");
		}
		else {
			// Init integerProperty to -1.
			integerProperty.set(-1);
			// Set integerProperty to sending userId.
			integerProperty.set(pkg.receiveUserId);
			// Set stringProperty to sending userName and their message.
			stringProperty.set(pkg.receiveUserName +": "+ pkg.message + "\n");
			// clean the stringProperty.
			stringProperty.set("");
		}
	}

	/**
	 * After loggingIn, ask for newest online user list.
	 */
	public void findOnlineUsers() {

		// Create a new package to ask the newest online user list.
		DataPackage pkg = new DataPackage();
		pkg.type = 4;
		user.sendDataPackage(pkg);
	}

	/**
	 * Get online user list from server.
	 * 
	 * @param pkg The input of data package.
	 */
	public void getOnlineUsers(DataPackage pkg){

		if(pkg.onlineUser == null) {
			return;
		}

		// Init online user list.
		onlineUsersList.clear();
		for(DataPackage.OnlineUser user : pkg.onlineUser) {
			onlineUsersList.add(new User(user.userId, user.userName));
		}

		// Create a new package to notify others user to re-flash the online status.
		DataPackage notifyOthers = new DataPackage();
		notifyOthers.type = 5;

		user.sendDataPackage(notifyOthers);
	}

	/**
	 * Update online user list and broadcast to every online user.
	 * 
	 * @param pkg          The input of data package.
	 * @param listProperty The input of listProperty.
	 */
	public void updateOnlineUsers(DataPackage pkg, ListProperty<User> listProperty) {

		// Init online user list.
		onlineUsersList.clear();
		for(DataPackage.OnlineUser user : pkg.onlineUser) {
			onlineUsersList.add(new User(user.userId, user.userName));
		}
		
		// Broadcast to every user.
		ObservableList<User> observableList = FXCollections.observableList(onlineUsersList);
		listProperty.setValue(observableList);
	}

	/**
	 * Force logout duplicated login account.
	 */
	public void forceLogoutProcess() {

		// Create a new package to notify duplicated account to exit app.
		DataPackage pkg = new DataPackage();
		pkg.type = 6;
		pkg.userId = user.userId;
		pkg.userName = user.userName;
		user.sendDataPackage(pkg);
	}

	/**
	 * Execute exit.
	 */
	public void forceExit() {
		System.exit(0);
	}

	/**
	 * The Client receives the data from server.
	 * 0. Sign In
     * 1. Sign Up
     * 2. Chat
     * 3. --
     * 4. Get online users
     * 5. Notify online users.
	 * 6. Force LogOut.
	 * 
	 * @param integerProperty The input of IntegerProperty
	 * @param listProperty    The input of ListProperty
	 * @param stringProperty  The input of StringProperty
	 * @return The integer of corresponding status.
	 */
	private int receiveFromServer(IntegerProperty integerProperty,
								  ListProperty<User> listProperty,
								  StringProperty stringProperty) {

		// If cannot connect to server.
		if(!user.getDataHandler().isConnected()) {
			System.out.println("Cannot Connect Server");
			return -2;
		}

		List<DataPackage> packages = user.getDataHandler().receiveHandle();
		if (packages != null) {
			// System.out.printf("got %d packages\n", packages.size());
			for (DataPackage pkg : packages) {
				// System.out.println("Process pkg: " + pkg.toString());
				switch (pkg.type) {
					case 0:
						System.out.println("[" + pkg.userName + "]: SIGN IN");
						signInResult(pkg, integerProperty);
						break;
					case 1:
						System.out.println("[" + pkg.userName + "] SIGN UP new account: " + pkg.toString());
						registerResult(pkg, integerProperty);
						break;
					case 2:						
						System.out.println("Send MSG");
						System.out.println(pkg.toString());
						receiveMsg(pkg, stringProperty, integerProperty);
						break;
					case 3:
						break;
					case 4:
						System.out.println("get online users");
						getOnlineUsers(pkg);
						break;
					case 5:
						System.out.println("Update online users");
						updateOnlineUsers(pkg, listProperty);
						break;
					case 6:
						System.out.println("Force Logout");
						forceExit();
						break;

					default:
						System.out.println("unknown package type: " + pkg.type + ", content: " + pkg.toString());
						break;
				}
			}
		} else {
			System.out.println("got null packages, connection may have been broken");
			return -3;
		}
		return packages.size();
	}

	/**
	 * User provide the host IP address and specific port to connect to Server.
	 * 
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
	 * 
	 * @param integerProperty The input of IntegerProperty.
	 * @param listProperty    The input of listProperty.
	 * @param stringProperty  The input of stringProperty.
	 * @return The package size from server.
	 */
	public int runMain(IntegerProperty integerProperty, ListProperty<User> listProperty, StringProperty stringProperty) {
		int num = receiveFromServer(integerProperty, listProperty, stringProperty);
		if (num < 0) {
			integerProperty.set(num);
		}
		return num;
    }

}