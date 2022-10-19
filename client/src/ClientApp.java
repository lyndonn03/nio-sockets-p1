import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class ClientApp {
    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);
        SocketChannel socketChannel = null;
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        while (true) {
            System.out.print("Enter your request: ");
            String msg = scanner.nextLine();
            if(msg.isEmpty()) {
                System.out.println("Message must not be empty.");
                continue;
            }
            
            buffer.clear();
            buffer.put(msg.getBytes());
            buffer.flip();
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("localhost", 9090));

            if (socketChannel.isConnected())
                System.out.println("Client is connected");

            while (buffer.hasRemaining())
                socketChannel.write(buffer);

            buffer.compact();
            socketChannel.read(buffer);
            buffer.flip();

            while (buffer.hasRemaining())
                System.out.print((char) buffer.get());
            System.out.println();

            socketChannel.close();
        }

    }
}
