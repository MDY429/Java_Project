import java.util.List;
import java.util.Observable;

/**
 * This is Client.
 * @author Ta-Yu Mar
 * @version 0.1 beta 2020-03-05
 */
public class ChatClient extends Observable {

	// Create a user type to handle the client's request.



	User user = new User();
	DataHandler dataHandler = null;
	private static final int PORT_NUMBER = 50000;
	private static final int MAX_TRY_CONNECT = 20;
	private static int tryConnect = 0;

	/**
	 * Client ask for registering new account.
	 * @param userName User's username
	 * @param userPw   User's password.
	 */
	public void sendSignUp(String userName, String userPw) {
		// TODO: Here shold be connected by application.
		System.out.printf("Sign up for name:%s and pw:%s\n", userName, userPw);
		DataPackage pkg = new DataPackage();
		pkg.type = 1;
		pkg.userName = userName;
		pkg.userPw = userPw;
        user.sendDataPackage(pkg);
	}
	
	public void receiveMsg(DataPackage pkg) {
		if(pkg.flag == 1){
			System.out.println(pkg.userName + " SEND SUCCESS!!");
		}
		else{
			System.out.println(pkg.userName + " got " + pkg.receiveUserName +" :"+pkg.message);
		}
	}

	/**
	 * The Client receives the data from server.
	 * @return The integer of corresponding status.
	 */
	private int receiveFromServer() {
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
				System.out.println("Process pkg: " + pkg.toString());
				switch (pkg.type) {
					case 0:
						System.out.println();
						break;
					case 1:
						System.out.println("Sign up new account: " + pkg.toString());
						break;
					case 2:						
						System.out.println("Send MSG");
						System.out.println(pkg.toString());
						receiveMsg(pkg);
						break;
					case 3:
						System.out.println("Receive MSG");
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
	 * @param host The host IP address.
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
	 */
	public void runMain() {

		// Connect to Server.
		userConnectToServer(PORT_NUMBER);
		
		while (true) {
			int num = receiveFromServer();
            if (num <= 0) {
				tryConnect++;
				if(tryConnect > MAX_TRY_CONNECT){
					System.err.println("Cannot connect server, close the application.");
					break;
				}
                // If no data or cannot connect server. Execute the sleep.
                try {
                    Thread.sleep(20);
                } catch (Exception e) {
                    System.out.println("Exception for waking up from sleep unexpectedly: " + e.toString());
                }
            }
        }
    }

	public static void main(String[] args) {	
		ChatClient runClient = new ChatClient();
		runClient.runMain();

	}

}