package petproject.server;


import org.testng.annotations.Test;

import java.io.IOException;

import static org.testng.Assert.assertEquals;

public class StarterArgumentsHandlerTest {
    private final int PORT = 8080;

    @Test
    public void shouldNotFindPortIfItNotPassed() throws IOException, PortFormatException {
        int port = StarterArgumentsHandler.getPort(new String[]{"aaa"});
        assertEquals(StarterArgumentsHandler.PROPERTY_NOT_DEFINED, port);
    }

    @Test
    public void shouldNotFindPortIfParamsNotPassed() throws IOException, PortFormatException {
        int port = StarterArgumentsHandler.getPort(new String[]{});
        assertEquals(StarterArgumentsHandler.PROPERTY_NOT_DEFINED, port);
    }

    @Test
    public void shouldFindPortWhenOtherParamsBeforePort() throws IOException, PortFormatException {
        int port = StarterArgumentsHandler.getPort(new String[]{"aaa", "-p", String.valueOf(PORT)});
        assertEquals(PORT, port);
    }

    @Test
    public void shouldFindPortWhenOtherParamsAfterPort() throws IOException, PortFormatException {
        int port = StarterArgumentsHandler.getPort(new String[]{"-p", "8080", "aaa"});
        assertEquals(PORT, port);
    }

    @Test
    public void shouldFindPortOnlyPortParamsPassed() throws IOException, PortFormatException {
        int port = StarterArgumentsHandler.getPort(new String[]{"-p", "8080"});
        assertEquals(PORT, port);
    }

    @Test(expectedExceptions = PortFormatException.class)
    public void shouldThrowEcxeptionIfPortInWrongFormat() throws IOException, PortFormatException {
        StarterArgumentsHandler.getPort(new String[]{"-p", "aaa", "8080"});
    }

    @Test(expectedExceptions = PortFormatException.class)
    public void shouldThrowExceptionIfPortValueIsNotNumber() throws IOException, PortFormatException {
        StarterArgumentsHandler.getPort(new String[]{"-p", "aaa"});
    }

    @Test(expectedExceptions = PortFormatException.class)
    public void shouldThrowEcxeptionIfPortValueNotPassed() throws IOException, PortFormatException {
        StarterArgumentsHandler.getPort(new String[]{"-p"});
    }
}
