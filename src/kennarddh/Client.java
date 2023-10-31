package kennarddh;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

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
        public void completed(Void result, AsynchronousSocketChannel clientChannel) {
            System.out.println("TCP Server Connected");
            System.out.println("E");

            ByteBuffer buffer = ByteBuffer.wrap("Hello, world!".getBytes());

            clientChannel.write(buffer);
            System.out.println("DATA SENT");
        }

        @Override
        public void failed(Throwable exc, AsynchronousSocketChannel clientChannel) {
            throw new RuntimeException(exc);
        }
    }
}
