package petproject.server;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpHandler {
    public static final String RESPONSE_HEADER = "HTTP/1.1 {0} OK\r\n" +
            "Content-Type: text/html\r\n\r\n";

    public static final String METHOD_GET = "GET";
    public static final int STATUS_OK = 200;
    public static final int STATUS_NOT_FOUND = 404;

    public static final String REQUEST_METHOD = "method";
    public static final String REQUEST_RESOURCE = "resource";
    private static final int STARTING_LINE_EXPECTED_PARAMS_LENGTH = 2;

    private HttpHandler(){}

    private static class SingletonHolder {
        private static final HttpHandler instance = new HttpHandler();
    }

    public static HttpHandler getInstance() {
        return SingletonHolder.instance;
    }

    public Map<String, String> processRequest(String request) {//tested
        String[] requestLines = request.split("\n");
        String startingLine = requestLines[0];

        Map<String, String> startingLineParts = new HashMap<>();
        if (!Objects.isNull(startingLine) && !startingLine.isEmpty()
                && startingLine.length() >= STARTING_LINE_EXPECTED_PARAMS_LENGTH) {
            String[] parts = startingLine.split(" ");
            startingLineParts.put(REQUEST_METHOD, parts[0]);
            startingLineParts.put(REQUEST_RESOURCE, parts[1]);
        }

        return startingLineParts;
    }

    public boolean validateRequest(Map<String, String> requestParts) {
        if (!isMethodSupported(requestParts.get(REQUEST_METHOD))) {
            return false;
        }
        if (!ResourceLoader.getInstance().isResourceExist(requestParts.get(REQUEST_RESOURCE))) {
            return false;
        }
        return true;
    }

    private boolean isMethodSupported(String method) {
        return METHOD_GET.equals(method);
    }
}
