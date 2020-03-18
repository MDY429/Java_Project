import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;

/**
 * This class would help server to process the data transmission. This
 * DataHandler would send/receive to/from client and server.
 * 
 * @author Ta-Yu Mar
 * @version 0.2 beta 2020-03-18
 */
public class DataHandler {

    private SocketChannel socketChannel = null;
    private ByteBuffer sendBuffer;
    private ByteBuffer receiveBuffer;

    // Create a package header.
    public static final short PACKAGE_HEADER = (short)0xffff;
    public static final int HEADER_LENGTH = 4;

    /**
     * Constructor for DataHandler
     */
    public DataHandler() {
        this.sendBuffer = ByteBuffer.allocate(0x4000);
        this.receiveBuffer = ByteBuffer.allocate(0x4000);
    }

    /**
     * When client comes in, connect them to server.
     * 
     * @param port The connection port number.
     * @return boolean
     */
    public boolean connectToServer(int port) {
        try {
            socketChannel = SocketChannel.open();

            // Set non-blocking mode
            socketChannel.configureBlocking(false);

            // Wait to finish the connection
            socketChannel.connect(new InetSocketAddress(InetAddress.getByName("localhost"), port));
            while (!socketChannel.finishConnect()) {}

            // Disable Nagle's Algorithm to send tcp data immediately
            socketChannel.socket().setTcpNoDelay(true);
        } catch (IOException e) {
            System.out.println("Exception when connecting to server: " + e);
            return false;
        }
        return true;
    }

    /**
     * To handle the sending data and guarantee the sending data is correct by
     * adding the header and length.
     * 
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
            // e.printStackTrace();
            System.out.println("Exception when sending data: " + e);
            return false;
        }

        return true;
    }

    /**
     * To handle the received data.
     * 
     * @return ArrayList of DataPackage.
     */
    public ArrayList<DataPackage> receiveHandle() {
        ArrayList<DataPackage> pkgs = new ArrayList<>(4);
        int numByteRead = 0;

        try {
            // The number of bytes read.
            numByteRead = socketChannel.read(receiveBuffer);
        } catch (IOException e) {
            // e.printStackTrace();
            System.out.println("Exception for receiving data: " + e);
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
        receiveBuffer.flip();

        // Checking wether it had received the whole package.
        while(receiveBuffer.remaining() >= HEADER_LENGTH) {
            receiveBuffer.mark();
            short header = receiveBuffer.getShort();
            short length = receiveBuffer.getShort();
            System.out.println("header: " + header + ", length: "+ length);
            if(header == (short) 0xffff) {
                if(receiveBuffer.position() + length > receiveBuffer.limit()) {
                    // Only get partial package data.
                    receiveBuffer.reset();
                    break;
                }
                else {
                    // Separate package data to header and body, read data start at 4 to (4+length-1).
                    ByteBuffer partialBuf = getPartialBuffer(receiveBuffer, receiveBuffer.position(), length);
                    DataPackage pkg = handlePackage(partialBuf);
                    if(pkg != null) {
                        pkgs.add(pkg);
                    }
                    receiveBuffer.position(receiveBuffer.position() + length);
                }
            }
            else {
                // Get bad package data.
                receiveBuffer.clear();
                break;
            }
        }
        receiveBuffer.compact();

        return pkgs;
    }

    /**
     * Set the corresponding SocketChannel.
     * 
     * @param sc The input SocketChannel.
     */
    public void setSocketChannel(SocketChannel sc) {
        socketChannel = sc;
    }

    /**
     * Checking the SocketChannel is connected or not.
     * 
     * @return boolean
     */
    public boolean isConnected() {
        return (socketChannel != null) && socketChannel.isConnected();
    }

    /**
     * To separate the whole data to be partial from specific position and length.
     * 
     * @param byteBuffer The input of whole data buffer.
     * @param pos        The input of specific position.
     * @param length     The input of specific length.
     * @return The part of byteBuffer.
     */
    private ByteBuffer getPartialBuffer(ByteBuffer byteBuffer, int pos, int length) {
        // Record the original position.
        int orgPos = byteBuffer.position();
        byteBuffer.position(pos);
        
        // New buffer will start at this buffer's current position.
        ByteBuffer sliceBuf = receiveBuffer.slice();
        receiveBuffer.position(orgPos);
        sliceBuf.limit(length);
        return sliceBuf.asReadOnlyBuffer();
    }

    /**
     * Convert byteBuffer to data package.
     * 
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
     * Convert the byteBuffer to string.
     * 
     * @param byteBuffer The input byteBuffer.
     * @return String.
     */
    private String byteBufferToString(ByteBuffer byteBuffer) {
        Charset charset = Charset.forName("UTF-8");
        CharsetDecoder decoder = charset.newDecoder();
        try {
            return decoder.decode(byteBuffer).toString();
        } catch (Exception e) {
            System.out.println("Exception for decoding message buffer failed: " + e);
            return "";
        }
    }
    
}