import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The ChatServer, here we use the non-blocking I/O way to implement the server.
 * With non-blocking I/O, we can use a single thread to handle multiple
 * concurrent connections.
 * 
 * @author Ta-Yu Mar
 * @version 0.2 beta 2020-03-18
 */
public class ChatServer {
    
    // Server port number.
    private int serverPort;

    // User manager to handle user's information.
    private final UserManager userManager = new UserManager();

    // Use map to correspond the SocketChannel to data handler.
    private Map<SocketChannel, DataHandler> mapSocketToHandlers = new HashMap<SocketChannel, DataHandler>();

    /**
     * Constructor for CharServer.
     */
    public ChatServer() {
        this.serverPort = 50000;
    }

    /**
     * Server Start.
     */
    public void serverRun() {
        
        try {
            Selector selector = Selector.open();

            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);

            InetSocketAddress hostAddress = new InetSocketAddress(this.serverPort);
            serverChannel.socket().bind(hostAddress);

            // Server socket channel is ready to accept a new connection from a client.
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                int count = selector.select();
                if (count > 0) {
                    // process selected key.
                    Set<SelectionKey> keys = selector.selectedKeys();
                    try {
                        for (SelectionKey key : keys) {
                            // Whether this key's channel is ready to accept a new socket connection.
                            if (key.isAcceptable()) {
                                // Get client socket channel
                                SocketChannel client = (SocketChannel) serverChannel.accept();
                                if (client == null) {
                                    System.out.println("Got a new connection, but server is null");
                                    continue;
                                }
                                
                                // None Blocking IO
                                client.configureBlocking(false);
                                // Record it for read operations
                                client.register(key.selector(), SelectionKey.OP_READ);
    
                                // Make the handler to process the data.
                                DataHandler handler = new DataHandler();
                                handler.setSocketChannel(client);
                                mapSocketToHandlers.put(client, handler);
                            }
    
                            // Whether this key's channel is ready for reading.
                            else if (key.isReadable()) {
    
                                // Returns the channel for which this key was created.
                                SocketChannel client = (SocketChannel) key.channel();
    
                                // Find the correspond handler.
                                DataHandler handler = mapSocketToHandlers.get(client);
                                ArrayList<DataPackage> dataPkgs = handler.receiveHandle();
    
                                if (dataPkgs == null) {
                                    System.out.println("No data, close connection: " + client.toString());
                                    key.cancel();
    
                                    // Delete the handler in the map
                                    mapSocketToHandlers.remove(client);

                                    // Client disconnect.
                                    userManager.userDisconnect(handler);
    
                                    // Close the socket.
                                    Socket s = client.socket();
                                    try {
                                        s.close();
                                    } catch( IOException e ) {
                                        System.err.println("Error closing socket " + s + ": " + e );
                                    }
                                } else {
                                    // Process the data package.
                                    dataPackageHandler(dataPkgs, handler);
                                }
                            }
                            keys.clear();
                        }
                    } catch (ConcurrentModificationException e) {
                        // Retrying selector keys after ConcurrentModificationException caught.
                        continue;
                    }
                } else {
                    // sleep a while.
                    try { Thread.sleep(2); } catch (Exception e) {};
                }
            }

        } catch (IOException e) {
            // System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Process the data package and handle it to call the corresponding method.
     * 0. Sign In
     * 1. Sign Up
     * 2. Chat
     * 3. --
     * 4. Get online users
     * 5. Notify online users.
     * 
     * @param dataPkgs The DataPackage.
     * @param handler  The DataHandler.
     */
    public void dataPackageHandler(ArrayList<DataPackage> dataPkgs, DataHandler handler){

        for (DataPackage pkg: dataPkgs) {
            System.out.println("[dataPackageHandler]Process pkg: " + pkg.toString());
            switch (pkg.type) {
                case 0:
                    System.out.println("[Server]signIn:" + pkg.type);
                    userManager.userSignIn(pkg, handler);
                    break;
                case 1:
                    System.out.println("[Server]signUp:" + pkg.type);
                    userManager.userSignUp(pkg, handler);
                    break;
                case 2:
                    System.out.println("[Server]Chat:" + pkg.type);
                    userManager.sendPrivateChat(pkg, handler);
                    break;
                case 3:
                    System.out.println("[Server]IsRead" + pkg.type);
                    userManager.sendReadStatus(pkg, handler);
                    break;
                case 4:
                    System.out.println("[Server]Get online users" + pkg.type);
                    userManager.getOnlineUsers(pkg, handler);
                    break;
                case 5:
                    System.out.println("[Server]Notify other users." + pkg.type);
                    userManager.notifyOthersUsers(pkg, handler);
                    break;
                
                default:
                    System.err.println("Unknown package type: " + pkg.type + ", content: " + pkg.toString());
                    break;
            }
        }
    }

    
    public static void main(String[] args) {
        ChatServer a = new ChatServer();
        a.serverRun();
    }
}