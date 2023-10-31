package kennarddh;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

public class Server {
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

            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

            clientChannel.read(byteBuffer, byteBuffer, new ReadCompletionHandler(clientChannel));
        }

        @Override
        public void failed(Throwable exc, Void attachment) {
            // Handle the error
            throw new RuntimeException(exc);
        }
    }

    private static class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
        AsynchronousSocketChannel clientChannel;

        public ReadCompletionHandler(AsynchronousSocketChannel clientChannel){
            this.clientChannel=clientChannel;
        }

        @Override
        public void completed(Integer bytesRead, ByteBuffer byteBuffer) {
            if (bytesRead > 0) {
                // Write the data back to the AsynchronousSocketChannel object
                clientChannel.write(byteBuffer, clientChannel, new WriteCompletionHandler());
                System.out.println("READ DATA "+bytesRead+" "+new String(byteBuffer.array(), Charset.defaultCharset()));
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer byteBuffer) {
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
