import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;

public class Client {

    private ByteBuffer buffer;
    private String ip;
    private SocketChannel channel;
    private SelectionKey key;
    private String requestMessage;

    public Client(String ip, SocketChannel channel, SelectionKey key) {
        this.ip = ip;
        this.channel = channel;
        this.key = key;
        this.buffer = ByteBuffer.allocate(1024);
    }

    public void pullRequest() throws IOException {
        buffer.clear();
        int byteRead = channel.read(buffer);
        if(byteRead == -1)
            this.disconnect();

        if(byteRead > 0) {
            buffer.flip();
            StringBuilder requestBuilder = new StringBuilder();
            while(buffer.hasRemaining())
                requestBuilder.append((char) buffer.get());
            this.requestMessage = requestBuilder.toString();
            System.out.println("Request from CLIENT-" + ip + ": " + this.requestMessage);
            key.interestOps(SelectionKey.OP_WRITE);
        }

    }

    public void pushResponse() throws IOException {
        String response = "Request received from the server at " + LocalDateTime.now().toString();
        buffer.clear();
        buffer.put(response.getBytes());
        buffer.flip();
        
        while(buffer.hasRemaining())
            channel.write(buffer);
        buffer.compact();
        this.disconnect();

    }

    public void disconnect() throws IOException {
        channel.close();
        key.cancel();
    }

    
}
