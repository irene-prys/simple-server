package petproject.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class SimpleServer {
    private final String HTTP_RESPONSE = "HTTP/1.1 200 OK\r\n" +
            "Content-Type: text/html\r\n\r\n";

    private final String MESSAGE = "Hello... world...";

    public void startServer(int port) throws IOException, ExecutionException, InterruptedException {
        final int cpuCores = Runtime.getRuntime().availableProcessors();
        AsynchronousChannelGroup group = AsynchronousChannelGroup
                .withThreadPool(Executors.newFixedThreadPool(cpuCores));

        AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(group);
        server.bind(new InetSocketAddress(port));

        server.accept(null, new ConnectionHandler(server));

        System.in.read();
        server.close();
    }

    private class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, Object> {
        private AsynchronousServerSocketChannel server;

        ConnectionHandler(AsynchronousServerSocketChannel server) {
            this.server = server;
        }

        @Override
        public void completed(AsynchronousSocketChannel clientSocketChannel, Object attachment) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            clientSocketChannel.read(buffer, buffer, new ReadWriteHandler(clientSocketChannel));
            server.accept(null, this);
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
        }
    }

    private class ReadWriteHandler implements CompletionHandler<Integer, ByteBuffer> {
        private AsynchronousSocketChannel clientSocketChannel;

        ReadWriteHandler(AsynchronousSocketChannel clientSocketChannel) {
            this.clientSocketChannel = clientSocketChannel;
        }

        @Override
        public void completed(Integer result, ByteBuffer request) {
            String responseMsg = HTTP_RESPONSE + MESSAGE;
            if (true) {
                ByteBuffer responseByteBuffer = ByteBuffer.wrap(responseMsg.getBytes());
                request.flip();
                clientSocketChannel.write(responseByteBuffer, null, new ResponseCompletionHandler(clientSocketChannel));
                System.out.println("http request is: " + new String(request.array()));
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            System.out.println("something wrong");
        }
    }

    private class ResponseCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
        private AsynchronousSocketChannel clientSocketChannel;

        ResponseCompletionHandler(AsynchronousSocketChannel clientSocketChannel) {
            this.clientSocketChannel = clientSocketChannel;
        }

        @Override
        public void completed(Integer result, ByteBuffer attachment) {
            try {
                clientSocketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
        }
    }


}
