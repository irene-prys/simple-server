package petproject.server.callbacks;

import petproject.server.utils.HttpHandler;
import petproject.server.utils.ResourceLoader;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.text.MessageFormat;
import java.util.Map;

public class ResponseHandler implements CompletionHandler<Integer, ByteBuffer> {
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
