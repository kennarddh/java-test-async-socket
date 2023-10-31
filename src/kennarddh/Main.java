package kennarddh;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        try (AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open()) {

            // Bind the AsynchronousServerSocketChannel object to a local address and port
            try {
                serverSocketChannel.bind(new InetSocketAddress("localhost", 5656));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println("TCP Server Ready");

            // Start accepting connections from clients
            serverSocketChannel.accept(null, new ConnectionHandler());

            Thread.currentThread().join();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {
        @Override
        public void completed(AsynchronousSocketChannel clientChannel, Void attachment) {
            // Read data from the AsynchronousSocketChannel object
            System.out.println("TCP Server New Client");
            clientChannel.read(ByteBuffer.allocate(1024), clientChannel, new ReadCompletionHandler());
        }

        @Override
        public void failed(Throwable exc, Void attachment) {
            // Handle the error
            throw new RuntimeException(exc);
        }
    }

    private static class ReadCompletionHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {
        @Override
        public void completed(Integer bytesRead, AsynchronousSocketChannel clientChannel) {
            if (bytesRead > 0) {
                // Write the data back to the AsynchronousSocketChannel object
                clientChannel.write(ByteBuffer.wrap(new byte[bytesRead]), clientChannel, new WriteCompletionHandler());
                System.out.println("READ DATA");
            }
        }

        @Override
        public void failed(Throwable exc, AsynchronousSocketChannel clientChannel) {
            throw new RuntimeException(exc);
        }
    }

    private static class WriteCompletionHandler implements CompletionHandler<Integer, AsynchronousSocketChannel> {

        @Override
        public void completed(Integer bytesWritten, AsynchronousSocketChannel clientChannel) {
            // Read data from the AsynchronousSocketChannel object again
            System.out.println("WRITE DONE");
        }

        @Override
        public void failed(Throwable exc, AsynchronousSocketChannel clientChannel) {
            throw new RuntimeException(exc);
        }
    }
}
