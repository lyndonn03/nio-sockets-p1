import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class App {
    public static void main(String[] args) throws Exception {

        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(9090));
        serverSocket.configureBlocking(false);

        final Selector selector = Selector.open();
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);

        while (true) {
            selector.selectNow();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> selectionKeyIterator = selectedKeys.iterator();

            while (selectionKeyIterator.hasNext()) {

                SelectionKey key = selectionKeyIterator.next();
                if (key.isAcceptable()) {
                    connect(key);
                } else if (key.isReadable()) {
                    read(key);
                    selectionKeyIterator.remove();
                } else if (key.isWritable()) {
                    write(key);
                    selectionKeyIterator.remove();
                }

            }

        }

    }

    private static void connect(SelectionKey serverSocketKey) throws IOException {

        ServerSocketChannel channel = (ServerSocketChannel) serverSocketKey.channel();
        SocketChannel clientSocket = channel.accept();

        if (clientSocket != null) {
            clientSocket.configureBlocking(false);
            SelectionKey key = clientSocket.register(serverSocketKey.selector(), SelectionKey.OP_READ | SelectionKey.OP_WRITE);
            key.interestOps(SelectionKey.OP_READ);
        }

    }

    private static void read(SelectionKey clientSocketKey) throws IOException {

        ByteBuffer buffIn = ByteBuffer.allocate(1024);
        SocketChannel socketChannel = (SocketChannel) clientSocketKey.channel();
        int bytesRead = socketChannel.read(buffIn);

        if (bytesRead == -1) {
            socketChannel.close();
            return;
        }

        if (bytesRead > 0) {
            buffIn.flip();
            buffIn.mark();

            while (buffIn.hasRemaining())
                System.out.print((char) buffIn.get());
            System.out.println();

            buffIn.compact();
            clientSocketKey.interestOps(SelectionKey.OP_WRITE);

        } else {
            System.out.println("SHIT");
            socketChannel.close();
            clientSocketKey.cancel();
        }

    }

    private static void write(SelectionKey clientSocketKey) throws IOException {

        ByteBuffer buffOut = ByteBuffer.allocate(1024);
        buffOut.put("Here is my response".getBytes());
        buffOut.flip();
        SocketChannel socketChannel = (SocketChannel) clientSocketKey.channel();

        while (buffOut.hasRemaining())
            socketChannel.write(buffOut);

        socketChannel.close();
        clientSocketKey.cancel();

    }

}
