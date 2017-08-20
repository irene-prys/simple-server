package petproject.server.callbacks;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class RequestHandler  implements CompletionHandler<AsynchronousSocketChannel, Object> {// todo: think over the name
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
