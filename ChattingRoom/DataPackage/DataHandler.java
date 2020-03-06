import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;

/**
 * This class would help server to process the data transmission.
 * This DataHandler would send/recieve to/from client and server.
 * @author Ta-Yu Mar
 * @version 0.1 beta 2020-03-05 
 */
public class DataHandler {

    private SocketChannel socketChannel = null;
    private ByteBuffer sendBuffer;
    private ByteBuffer recieveBuffer;

    // Create a package header.
    public static final short PACKAGE_HEADER = (short)0xffff;
    public static final int HEADER_LENGTH = 4;

    /**
     * Consturctor for DataHandler
     */
    public DataHandler() {
        this.sendBuffer = ByteBuffer.allocate(0x4000);
        this.recieveBuffer = ByteBuffer.allocate(0x4000);
    }

    /**
     * When client comes in, connect them to server.
     * @param host The host IP address.
     * @param port The connection port number.
     * @return boolean
     */
    public boolean connectToServer(String host, int port) {
        try {
            socketChannel = SocketChannel.open();
            // Set non-blocking mode
            socketChannel.configureBlocking(false);

            // Wait to finish the connection
            socketChannel.connect(new InetSocketAddress(host, port));
            while (!socketChannel.finishConnect()) {}

            // Disable Nagle¡¦s Algorithm to send tcp data immediately
            socketChannel.socket().setTcpNoDelay(true);
        } catch (IOException e) {
            System.out.println("Exception when connecting to server: " + e);
            return false;
        }
        return true;
    }

    /**
     * To handle the sending data and guarantee the sending data is correct by adding the header and length. 
     * @param msg The input of data.
     * @return boolean
     */
    public boolean sendDataHandle(String msg) {
        System.out.println("[DataHandler]sendDataHandle - " + msg);

        byte[] msgByte = msg.getBytes(Charset.forName("UTF-8"));
        sendBuffer.clear();
        sendBuffer.putShort(PACKAGE_HEADER);
        sendBuffer.putShort((short) msgByte.length);
        sendBuffer.put(msgByte);
        sendBuffer.flip();

        try {
            socketChannel.write(sendBuffer);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Exception when sending data: " + e);
            return false;
        }

        return true;
    }

    /**
     * To handle the recieved data.
     * @return Arraylist of DataPackage.
     */
    public ArrayList<DataPackage> recieveHandle() {
        ArrayList<DataPackage> pkgs = new ArrayList<>(4);
        int numByteRead = 0;

        try {
            // The number of bytes read.
            numByteRead = socketChannel.read(recieveBuffer);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // Check the numByteRead
        if(numByteRead < 0) {
            return null;
        }
        else if(numByteRead == 0) {
            return pkgs;
        }
        else if(numByteRead < HEADER_LENGTH) {
            return pkgs;
        }

        // The limit is set to the current position and then the position is set to zero.
        recieveBuffer.flip();

        // Checking wether it had receieved the whole package.
        while(recieveBuffer.remaining() >= HEADER_LENGTH) {
            recieveBuffer.mark();
            short header = recieveBuffer.getShort();
            short length = recieveBuffer.getShort();
            System.out.println("header: " + header + ", length: "+ length);
            if(header == (short) 0xffff) {
                if(recieveBuffer.position() + length > recieveBuffer.limit()) {
                    // Only get partial package data.
                    recieveBuffer.reset();
                    break;
                }
                else {
                    // Separate package data to header and body, read data start at 4 to (4+length-1).
                    ByteBuffer partialBuf = getPartialBuffer(recieveBuffer, recieveBuffer.position(), length);
                    DataPackage pkg = handlePackage(partialBuf);
                    if(pkg != null) {
                        pkgs.add(pkg);
                    }
                    recieveBuffer.position(recieveBuffer.position() + length);
                }
            }
            else {
                // Get bad package data.
                recieveBuffer.clear();
                break;
            }
        }
        recieveBuffer.compact();

        return pkgs;
    }

    /**
     * Set the corresponding SocketChannel.
     * @param sc The input SocketChannel.
     */
    public void setSocketChannel(SocketChannel sc) {
        socketChannel = sc;
    }

    /**
     * Checking the SoketChannel is connected or not.
     * @return boolean
     */
    public boolean isConnected() {
        return (socketChannel != null) && socketChannel.isConnected();
    }

    /**
     * To separate the whole data to be partial from specific position and length.
     * @param byteBuffer The input of whole data buffer.
     * @param pos The input of specific position.
     * @param length The input of specific length.
     * @return The part of byteBuffer.
     */
    private ByteBuffer getPartialBuffer(ByteBuffer byteBuffer, int pos, int length) {
        // Record the original position.
        int orgPos = byteBuffer.position();
        byteBuffer.position(pos);
        
        // New buffer will start at this buffer's current position.
        ByteBuffer sliceBuf = recieveBuffer.slice();
        recieveBuffer.position(orgPos);
        sliceBuf.limit(length);
        return sliceBuf.asReadOnlyBuffer();
    }

    /**
     * Convert byteBuffer to data package.
     * @param byteBuffer The input data buffer.
     * @return DataPackage
     */
    private DataPackage handlePackage(ByteBuffer byteBuffer) {
        // Convert the byteBuffer to String.
        String msg = byteBufferToString(byteBuffer);
        // System.out.println("Got a msg: " + msg);
        return DataPackage.fromString(msg);
    }

    /**
     * Conver the byteBuffer to string.
     * @param byteBuffer The input byteBuffer.
     * @return String.
     */
    private String byteBufferToString(ByteBuffer byteBuffer) {
        Charset charset = Charset.forName("UTF-8");
        CharsetDecoder decoder = charset.newDecoder();
        try {
            return decoder.decode(byteBuffer).toString();
        } catch (Exception e) {
            System.out.println("Excpetion for decoding message buffer failed: " + e);
            return "";
        }
    }
    
}