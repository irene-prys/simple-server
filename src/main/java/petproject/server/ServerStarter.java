package petproject.server;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ServerStarter {
    public static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        SimpleServer server = new SimpleServer();
        server.startServer(DEFAULT_PORT);
    }
}
