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
	private String hostIP;
	private static final int MAX_TRY_CONNECT = 20;
	private static int tryConnect = 0;

	// FOR TESTING !!!!!!
	public void testSendData() {
		System.out.println("[send data]");
		String threadName = Thread.currentThread().getName();
		DataPackage pkg = new DataPackage();
		pkg.type = 1;
		pkg.username = threadName;
		pkg.userpw = "1234";
		pkg.message = "QAQ";
		// System.out.println("Send PKG: " + user.getDataHandler().sendDataHandle(pkg.toString()));
		System.out.println("Send PKG: " + user.sendDataPackage(pkg));
	}

	/**
     * Client ask for registering new account.
     * @param username User's username
     * @param userpw User's password.
     */
    public void sendSignUp(String username, String userpw) {
		// TODO: Here shold be connected by application.
        System.out.printf("Singn up for name:%s and pw:%s\n", username, userpw);
        DataPackage pkg = new DataPackage();
        pkg.type = 1;
		pkg.username = username;
		pkg.userpw = userpw;
        user.sendDataPackage(pkg);
    }

	/**
	 * The Client receives the data from server.
	 * @return The integer of corresponding status.
	 */
	private int receiveFromServer() {
		if (!user.getDataHandler().isConnected()) {
			// Try to reconnect the server.
			userConnectToServer(hostIP, 50000);
			System.out.println("Cannot Connect Server");
			return -2;
		}

		List<DataPackage> packages = user.getDataHandler().receiveHandle();
		if (packages != null) {
			// Receieve from server, set tryConnect to 0.
			tryConnect = 0;
			// System.out.printf("got %d packages\n", packages.size());
			for (DataPackage pkg : packages) {
				System.out.println("Process pkg: " + pkg.toString());
				switch (pkg.type) {
					case 1:
						System.out.println("Sign up new account: " + pkg.toString());
						break;

					default:
						System.out.println("unknown package type: " + pkg.type + ", content: " + pkg.toString());
						break;
				}
			}
		} else {
			System.out.println("got null packages, connection may have been broken");
			// Try to reconnect the server.
			userConnectToServer(hostIP, 50000);
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
	public boolean userConnectToServer(String host, int port){
		if(user.getDataHandler() == null) {
			user.setDataHandler(new DataHandler());
		}
		return user.getDataHandler().connectToServer(host, port);
	}

	/**
	 * Set hostIP address.
	 * @param hostIP The input of IP address.
	 */
	private void setHostIP(String hostIP){
		this.hostIP = hostIP;
	}

	/**
	 * Start to run.
	 * @param host The host IP address.
	 * @param port The corresponding Server port.
	 */
	public void runMain(String host, int port) {

		setHostIP(host);

		// Connect to Server.
		userConnectToServer(hostIP, 50000);
		
		int i = 0; //TESTING
		while (true) {
			// ------ TESTING ------
			// Every 200 cycle will send a request to server.
			if(i%200 == 1)
			testSendData();
			i++;
			// ---------------------
			int num = receiveFromServer();
			System.out.println(num+","+i);
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
		int numOfThread = 1;
		String hostIP = "10.113.193.133";
		// String hostIP = "172.22.9.176";
		int portNum = 50000;

		Runnable client = new Runnable() {
			@Override
			public void run() {
				new ChatClient().runMain(hostIP, portNum);
			}
		};
		for(int i=0; i<numOfThread; i++){
			new Thread(client, "client-"+i).start();
		}
	}
}