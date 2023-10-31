package kennarddh;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Client {
    public static void main(String[] args) throws InterruptedException {
        try (AsynchronousSocketChannel clientChannel = AsynchronousSocketChannel.open()) {
            // Connect to the server
            clientChannel.connect(new InetSocketAddress("localhost", 5656), clientChannel, new ConnectionHandler());

            Thread.currentThread().join();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static class ConnectionHandler implements CompletionHandler<Void, AsynchronousSocketChannel> {
        @Override
        public void completed(Void result, final AsynchronousSocketChannel clientChannel) {
            System.out.println("TCP Server Connected");

            ByteBuffer buffer = ByteBuffer.wrap("Hello, world!".getBytes());

            Future<Integer> writeResult = clientChannel.write(buffer);

            try {
                writeResult.get();
                System.out.println("DATA SENT");
                
                ByteBuffer buffer2 = ByteBuffer.wrap("Hello, world!".getBytes());
                clientChannel.write(buffer2);
                System.out.println("DATA2 SENT");
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void failed(Throwable exc, AsynchronousSocketChannel clientChannel) {
            throw new RuntimeException(exc);
        }
    }
}
