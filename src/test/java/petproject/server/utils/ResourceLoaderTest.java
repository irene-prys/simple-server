package petproject.server.utils;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import petproject.server.WorkWithFileSystemTest;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import static org.testng.Assert.*;

public class ResourceLoaderTest extends WorkWithFileSystemTest {

    private ResourceLoader resourceLoader;

    @BeforeClass
    public void setUp() {
        resourceLoader = ResourceLoader.getInstance();
    }

    @Test
    public void shouldReturnNullIfFileNotFound() throws IOException, URISyntaxException {
        String resourceName = "fileThatNotExist.html";
        File file = new File(getPathToResource(resourceName));
        assertFalse(file.exists());

        ByteBuffer result = resourceLoader.load(resourceName);
        assertNull(result);
    }

    @Test(groups = {WorkWithFileSystemTest.WITH_RESOURCES})
    public void shouldLoadResource() throws IOException {
        ByteBuffer resource = resourceLoader.load(INDEX_RESOURCE_NAME);
        assertNotNull(resource);
        resource.clear();
    }

    @Test(groups = {WITH_RESOURCES}, threadPoolSize = 3, invocationCount = 10, timeOut = 1000)
    public void shouldBeLoadedByMultipleThreads() throws IOException {
        ByteBuffer resource = resourceLoader.load(INDEX_RESOURCE_NAME);
        assertNotNull(resource);
        resource.clear();
    }

    @Test(groups = {WITH_RESOURCES})
    public void shouldLoad404() throws IOException, URISyntaxException {
        ByteBuffer resource = resourceLoader.loadResource404();
        assertNotNull(resource);
        resource.clear();
    }
}
