package petproject.server;

import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import petproject.server.utils.ResourceLoader;

import java.io.File;
import java.io.IOException;

abstract public class WorkWithFileSystemTest {
    public static final String WITH_RESOURCES = "withResources";
    public static final String INDEX_RESOURCE_NAME = "indexFileForTesting.html";

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

    protected String getPathToResource(String fileName) throws IOException {
        String jarPath = ResourceLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath();

        File jar = new File(jarPath);
        String jarDirPath = jar.getParent();
        return jarDirPath + File.separator + fileName;
    }
}
