package petproject.server;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class ServerStarter {
    public static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        int port = getPort(args);
        if (port == -1) {
            System.out.println("Please, enter port correctly in format: -p port_number");
            System.out.println("Or don't define port at all. In this case will be used default port 8080");
            return;
        }

        SimpleServer server = new SimpleServer();
        server.startServer(port);
    }

    private static int getPort(String[] args) {
        try {
            int port = StarterArgumentsHandler.getPort(args);
            return port == StarterArgumentsHandler.PROPERTY_NOT_DEFINED ? DEFAULT_PORT : port;
        } catch (PortFormatException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }
}
