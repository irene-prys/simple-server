package petproject.server;

import org.mockito.invocation.InvocationOnMock;
import org.testng.annotations.Test;
import petproject.server.callbacks.ResponseHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class ResponseHandlerTest extends WorkWithFileSystemTest {

    @Test(groups = {WITH_RESOURCES})
    public void shouldGenerateCorrectResponseForValidRequest() throws IOException, TimeoutException, InterruptedException, ExecutionException {
        //given
        String request = "GET /" + INDEX_RESOURCE_NAME + " HTTP/1.1/r/nHost: www.w3.org";
        final ByteBuffer[] answers = new ByteBuffer[1];
        AsynchronousSocketChannel client = getClientSocket(answers);

        ResponseHandler handler = new ResponseHandler(client);
        ByteBuffer clientRequest = ByteBuffer.wrap(request.getBytes());

        //when
        handler.completed(1, clientRequest);
        String serverResponse = new String(answers[0].array());
        String[] responseParts = serverResponse.split(" ");

        //then
        assertTrue(responseParts.length > 0);
        assertEquals(Integer.parseInt(responseParts[1]), 200);
    }

    @Test(groups = {WITH_RESOURCES})
    public void shouldGenerate404ResponseForInvalidRequest() throws IOException, TimeoutException, InterruptedException, ExecutionException {
        //given
        String request = "POST /" + INDEX_RESOURCE_NAME + " HTTP/1.1/r/nHost: www.w3.org";
        final ByteBuffer[] answers = new ByteBuffer[1];
        AsynchronousSocketChannel client = getClientSocket(answers);

        ResponseHandler handler = new ResponseHandler(client);
        ByteBuffer clientRequest = ByteBuffer.wrap(request.getBytes());

        //when
        handler.completed(1, clientRequest);
        String serverResponse = new String(answers[0].array());
        String[] responseParts = serverResponse.split(" ");

        //then
        assertTrue(responseParts.length > 0);
        assertEquals(Integer.parseInt(responseParts[1]), 404);
    }

    @Test(groups = {WITH_RESOURCES})
    public void shouldGenerate404ResponseIfResourceNotFound() throws IOException, TimeoutException, InterruptedException, ExecutionException {
        //given
        String request = "GET /fileWhichNotExists.html HTTP/1.1/r/nHost: www.w3.org";
        final ByteBuffer[] answers = new ByteBuffer[1];
        AsynchronousSocketChannel client = getClientSocket(answers);

        ResponseHandler handler = new ResponseHandler(client);
        ByteBuffer clientRequest = ByteBuffer.wrap(request.getBytes());

        //when
        handler.completed(1, clientRequest);
        String serverResponse = new String(answers[0].array());
        String[] responseParts = serverResponse.split(" ");

        //then
        assertTrue(responseParts.length > 0);
        assertEquals(Integer.parseInt(responseParts[1]), 404);
    }

    private AsynchronousSocketChannel getClientSocket(ByteBuffer[] answers) {
        AsynchronousSocketChannel client = mock(AsynchronousSocketChannel.class);
        when(client.isOpen()).thenReturn(true);

        doAnswer((InvocationOnMock invocation) -> {
                    Object[] args = invocation.getArguments();
                    ByteBuffer buffer = (ByteBuffer) args[0];
                    answers[0] = buffer;
                    return null;
                }
        ).when(client).
                write(any(ByteBuffer.class), anyLong(), any(TimeUnit.class), any(), any(CompletionHandler.class));
        return client;
    }
}
