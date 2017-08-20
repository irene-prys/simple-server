package petproject.server.utils;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import petproject.server.WorkWithFileSystemTest;

import java.io.IOException;
import java.util.Map;

import static org.testng.Assert.*;

public class HttpHandlerTest extends WorkWithFileSystemTest {
    private HttpHandler httpHandler;

    @BeforeClass
    public void setUp() {
        httpHandler = HttpHandler.getInstance();
    }

    @Test()
    public void shouldHandleRequestWithSupportedMethod() throws IOException {
        String request = "POST /" + INDEX_RESOURCE_NAME + " HTTP/1.1/r/nHost: www.w3.org";
        Map<String, String> requestParams = httpHandler.processRequest(request);
        assertEquals(requestParams.get(HttpHandler.REQUEST_METHOD), "POST");
        assertEquals(requestParams.get(HttpHandler.REQUEST_RESOURCE), "/" + INDEX_RESOURCE_NAME);
    }

    @Test
    public void shouldHandleRequestWithNotSupportedMethod() throws IOException {
        String request = "POST /" + INDEX_RESOURCE_NAME + " HTTP/1.1/r/nHost: www.w3.org";
        Map<String, String> requestParams = httpHandler.processRequest(request);
        assertEquals(requestParams.get(HttpHandler.REQUEST_METHOD), "POST");
        assertEquals(requestParams.get(HttpHandler.REQUEST_RESOURCE), "/" + INDEX_RESOURCE_NAME);
    }

    @Test
    public void shouldHandleRequestWithoutDefinedResource() throws IOException {
        String request = "GET / HTTP/1.1/r/nHost: www.w3.org";
        Map<String, String> requestParams = httpHandler.processRequest(request);
        assertEquals(requestParams.get(HttpHandler.REQUEST_METHOD), HttpHandler.METHOD_GET);
        assertEquals(requestParams.get(HttpHandler.REQUEST_RESOURCE), "/");
    }

    @Test(groups = {WITH_RESOURCES})
    public void shouldDetermineRequestAsValidIfParamsCorrect() {
        String request = "GET /" + INDEX_RESOURCE_NAME + " HTTP/1.1/r/nHost: www.w3.org";
        Map<String, String> requestParams = httpHandler.processRequest(request);
        assertTrue(httpHandler.validateRequest(requestParams));
    }

    @Test(groups = {WITH_RESOURCES})
    public void shouldDetermineRequestInvalidIfMethodIncorrect() {
        String request = "POST /" + INDEX_RESOURCE_NAME + " HTTP/1.1/r/nHost: www.w3.org";
        Map<String, String> requestParams = httpHandler.processRequest(request);
        assertFalse(httpHandler.validateRequest(requestParams));
    }

    @Test(groups = {WITH_RESOURCES})
    public void shouldDetermineRequestInvalidIfResourceNotExist() {
        String request = "GET /someNotExistFile.html HTTP/1.1/r/nHost: www.w3.org";
        Map<String, String> requestParams = httpHandler.processRequest(request);
        assertFalse(httpHandler.validateRequest(requestParams));
    }
}
