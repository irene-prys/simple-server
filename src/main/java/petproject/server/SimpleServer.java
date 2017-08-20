package petproject.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class SimpleServer {
    public void startServer(int port) throws IOException, ExecutionException, InterruptedException {
        final int cpuCores = Runtime.getRuntime().availableProcessors();
        AsynchronousChannelGroup group = AsynchronousChannelGroup
                .withThreadPool(Executors.newFixedThreadPool(cpuCores));

        AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(group);
        server.bind(new InetSocketAddress(port));

        server.accept(null, new RequestHandler(server));
    }

    private class RequestHandler implements CompletionHandler<AsynchronousSocketChannel, Object> {// todo: think over the name
        private AsynchronousServerSocketChannel server;

        public RequestHandler(AsynchronousServerSocketChannel server) {
            this.server = server;
        }

        @Override
        public void completed(AsynchronousSocketChannel clientSocketChannel, Object attachment) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            clientSocketChannel.read(buffer, buffer, new ResponseHandler(clientSocketChannel));
            server.accept(null, this);
        }

        @Override
        public void failed(Throwable exc, Object attachment) {
            exc.printStackTrace();
        }
    }

    private class ResponseHandler implements CompletionHandler<Integer, ByteBuffer> {// todo: think over the name
        private AsynchronousSocketChannel clientSocketChannel;

        public ResponseHandler(AsynchronousSocketChannel clientSocketChannel) {
            this.clientSocketChannel = clientSocketChannel;
        }

        @Override
        public void completed(Integer result, ByteBuffer request) {
            ByteBuffer response = generateResponse(new String(request.array()));
            request.flip();
            clientSocketChannel.write(response, response, new ResponseSendingCompletedHandler(clientSocketChannel));

        }

        private ByteBuffer generateResponse(String request) {
            HttpHandler httpHandler = HttpHandler.getInstance();
            Map<String, String> requestParts = httpHandler.processRequest(request);
            boolean isRequestValid = httpHandler.validateRequest(requestParts);
            if (isRequestValid) {
                try {
                    return generateSuccessfulResponse(requestParts.get(HttpHandler.REQUEST_RESOURCE));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return generateFailedResponse();
        }

        private ByteBuffer generateSuccessfulResponse(String resourceName) throws IOException {
            ByteBuffer header = ByteBuffer.wrap(MessageFormat.format(HttpHandler.RESPONSE_HEADER, HttpHandler.STATUS_OK).getBytes());
            ByteBuffer resource = ResourceLoader.getInstance().load(resourceName);
            ByteBuffer response = ByteBuffer.allocate(header.capacity() + resource.capacity()).put(header).put(resource);
            header.clear();
            resource.clear();
            response.flip();
            return response;
        }

        private ByteBuffer generateFailedResponse() {
            ByteBuffer resource = ByteBuffer.wrap("Something went wrong".getBytes());
            ByteBuffer header = ByteBuffer.wrap(MessageFormat.format(HttpHandler.RESPONSE_HEADER, HttpHandler.STATUS_NOT_FOUND).getBytes());
            try {
                resource = ResourceLoader.getInstance().loadResource404();
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
            ByteBuffer response = ByteBuffer.allocate(header.capacity() + resource.capacity()).put(header).put(resource);
            header.clear();
            resource.clear();
            response.flip();
            return response;
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            System.out.println("something wrong");
            exc.printStackTrace();
        }

        private class ResponseSendingCompletedHandler implements CompletionHandler<Integer, ByteBuffer> {
            private AsynchronousSocketChannel clientSocketChannel;

            ResponseSendingCompletedHandler(AsynchronousSocketChannel clientSocketChannel) {
                this.clientSocketChannel = clientSocketChannel;
            }

            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                try {
                    attachment.clear();
                    clientSocketChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                exc.printStackTrace();
            }
        }
    }
}
