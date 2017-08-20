package petproject.server.utils;

import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import static org.testng.Assert.*;

public class ResourceLoaderTest {
    private static final String WITH_RESOURCES = "withResources";
    private static final String INDEX_RESOURCE_NAME = "index55.html";

    private ResourceLoader resourceLoader;
    private boolean indexFileCreated;

    @BeforeGroups(groups = {WITH_RESOURCES})
    public void prepareResource() throws IOException {
        String resourceName = INDEX_RESOURCE_NAME;
        File file = new File(getPathToResource(resourceName));
        if (!file.exists()) {
            indexFileCreated = true;
            file.createNewFile();
        }
    }

    @AfterGroups(groups = {WITH_RESOURCES})
    public void clearResources() throws IOException {
        File file = new File(getPathToResource(INDEX_RESOURCE_NAME));
        if (indexFileCreated) {
            file.delete();
        }
    }

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

    @Test(groups = {WITH_RESOURCES})
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

    protected String getPathToResource(String fileName) throws IOException {
        String jarPath = ResourceLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        File jar = new File(jarPath);
        String jarDirPath = jar.getParent();
        return jarDirPath + File.separator + fileName;
    }

    protected void closeResource(Closeable resource) {
        try {
            resource.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
