import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ServerApp {
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
                Client client = (Client) key.attachment();
                if (key.isAcceptable()) {
                    connect(key);
                } else if (key.isReadable()) {
                    client.pullRequest();
                    selectionKeyIterator.remove();
                } else if (key.isWritable()) {
                    client.pushResponse();
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
            key.attach(new Client(clientSocket.getRemoteAddress().toString(), clientSocket, key));
        }

    }

}
