package petproject.server;

import petproject.server.callbacks.RequestHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
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
}
