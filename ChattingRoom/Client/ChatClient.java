import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ChatClient {

	public void startClient() throws IOException, InterruptedException {

		InetSocketAddress hostAddress = new InetSocketAddress("10.113.193.133", 50000);
		SocketChannel client = SocketChannel.open(hostAddress);

		System.out.println("Client... started");

		String threadName = Thread.currentThread().getName();

		// Send messages to server
		String[] messages = new String[] { threadName + ": msg1", threadName + ": msg2", threadName + ": msg3" };

		for (int i = 0; i < messages.length; i++) {
			ByteBuffer buffer = ByteBuffer.allocate(74);
			buffer.put(messages[i].getBytes());
			buffer.flip();
			client.write(buffer);
			System.out.println(messages[i]);
			buffer.clear();
			Thread.sleep(1000);
		}

		DataPackage pkg = new DataPackage();
		pkg.type = 1;
		pkg.str = threadName + "AA";
		// pkg.str = "QAQ";
		ByteBuffer buffer = ByteBuffer.allocate(74);
		buffer.put(pkg.toString().getBytes());
		buffer.flip();
		client.write(buffer);
		System.out.println(pkg.toString());
		buffer.clear();
		Thread.sleep(1000);

		client.close();
	}

	public static void main(String[] args) {
		// ChatClient a = new ChatClient();
		// try {
		// 	a.startClient();
		// } catch (IOException e) {
		// 	// TODO Auto-generated catch block
		// 	e.printStackTrace();
		// } catch (InterruptedException e) {
		// 	// TODO Auto-generated catch block
		// 	e.printStackTrace();
		// }
		Runnable client = new Runnable() {
			@Override
			public void run() {
				try {
					new ChatClient().startClient();
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println(e.toString());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		};
		new Thread(client, "client-A").start();
		new Thread(client, "client-B").start();
	}
}