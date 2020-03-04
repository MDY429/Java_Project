import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class ChatServer {
    private int serverPort;

    public ChatServer() {
        this.serverPort = 50000;
    }

    public void serverRun() {
        try {
            Selector selector = Selector.open();

            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);

            InetSocketAddress hostAddress = new InetSocketAddress(this.serverPort);
            serverChannel.socket().bind(hostAddress);

            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (true) {
                int count = selector.select();
                if (count > 0) {
                    // process selected key.
                    Set<SelectionKey> keys = selector.selectedKeys();
                    // System.out.println(keys);
                    for (SelectionKey key : keys) {
                        // client requires a connection.
                        if (key.isAcceptable()) {
                            // ServerSocketChannel server = (ServerSocketChannel) key.channel();
                            SocketChannel server = (SocketChannel) serverChannel.accept();
                            if (server == null) {
                                System.out.println("Got a new connection, but sc is null");
                                continue;
                            }
                            
                            // None Blocking IO
                            server.configureBlocking(false);
                            server.register(key.selector(), SelectionKey.OP_READ);
                        }

                        // server is ready to read data from client.
                        else if (key.isReadable()) {

                            SocketChannel channel = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(1024);
                            int numRead = -1;
                            numRead = channel.read(buffer);
                            System.out.println(numRead);
                            if (numRead == -1) {
                                Socket socket = channel.socket();
                                SocketAddress remoteAddr = socket.getRemoteSocketAddress();
                                System.out.println("Connection closed by client: " + remoteAddr);
                                channel.close();
                                key.cancel();
                                return;
                            }
                            
                            byte[] data = new byte[numRead];
                            System.arraycopy(buffer.array(), 0, data, 0, numRead);
                            System.out.println("Got: " + new String(data));
                            
                            
                        }
                        keys.clear();
                    }
                } else {
                    /* sleep a while */
                    try { Thread.sleep(2); } catch (Exception e) {};
                }
            }

        } catch (IOException e) {
            // System.out.println(e.toString());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatServer a = new ChatServer();
        a.serverRun();
    }
}