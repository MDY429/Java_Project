import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

	/**
	 * Client ask for registering new account.
	 * @param userName User's username
	 * @param userPw   User's password.
	 */
	public void sendSignUp(String userName, String userPw, String email) {
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
	 * @param IntegerProperty The input of IntegerProperty
	 */
	public void registerResult(DataPackage pkg, IntegerProperty integerProperty) {
		// Use IntegerProperty to do the broadcast.
		if(pkg.flag == 1) {
			integerProperty.set(2);
		}
		else {
			integerProperty.set(1);
		}
	}

	/**
	 * Client enter userName and password to login account.
	 * @param userName	The input of userName.
	 * @param userPw	The input of user password.
	 */
	public void sendSignIn(String userName, String userPw) {
		System.out.printf("Sign In for name:%s and pw:%s\n", userName, userPw);
		DataPackage pkg = new DataPackage();
		pkg.type = 0;
		pkg.userName = userName;
		pkg.userPw = userPw;
		user.sendDataPackage(pkg);
	}

	public void signInResult(DataPackage pkg, IntegerProperty integerProperty) {
		user.userName = pkg.userName;
		user.userId = pkg.userId;
		
		if(pkg.flag == 1) {
			integerProperty.set(2);
		}
		else {
			integerProperty.set(1);
		}
	}

	public void sendMsg(int chatId, String chatName, String msg) {
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
	 * 
	 * @param pkg
	 */
	public void receiveMsg(DataPackage pkg, StringProperty stringProperty, IntegerProperty integerProperty) {
		if(pkg.flag == 1) {
			System.out.println(pkg.userName + " SEND SUCCESS!!");
		}
		else if(pkg.flag == 2) {
			System.out.println(pkg.userName + " SEND FAIL!!");
			stringProperty.set(pkg.receiveUserName +": I am not ONLINE.\n");
			stringProperty.set("");
		}
		else {			
			integerProperty.set(-1);
			integerProperty.set(pkg.receiveUserId);
			System.out.println(pkg.userName + " got " + pkg.receiveUserName +" :"+pkg.message);
			stringProperty.set(pkg.receiveUserName +": "+ pkg.message + "\n");
			stringProperty.set("");
		}
	}

	public void findOnlineUsers() {
		System.out.println("find online users");
		DataPackage pkg = new DataPackage();
		pkg.type = 4;
		user.sendDataPackage(pkg);
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
		user.sendDataPackage(notifyOthers);
	}

	public void updateOnlineUsers(DataPackage pkg, ListProperty<User> listProperty) {
		onlineUsersList.clear();
		for(DataPackage.OnlineUser user : pkg.onlineUser) {
			onlineUsersList.add(new User(user.userId, user.userName));
		}

		for(User s : onlineUsersList){
			System.out.println(s.userId + ", " + s.userName);
		}
		
		ObservableList<User> observableList = FXCollections.observableList(onlineUsersList);
		listProperty.setValue(observableList);
	}

	public List<User> getOnlineList() {
		return onlineUsersList;
	}

	/**
	 * The Client receives the data from server.
	 * @param integerProperty The input of IntegerProperty
	 * @return The integer of corresponding status.
	 */
	private int receiveFromServer(IntegerProperty integerProperty, ListProperty<User> listProperty, StringProperty stringProperty) {
		if (!user.getDataHandler().isConnected()) {
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
						System.out.println("Receive MSG");
						break;
					case 4:
						System.out.println("get online users");
						getOnlineUsers(pkg);
						break;
					case 5:
						System.out.println("Update online users");
						updateOnlineUsers(pkg, listProperty);
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
	 * @param integerProperty The input of IntegerProperty.
	 * @param listProperty The input of listProperty.
	 * @param stringProperty The input of stringProperty.
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