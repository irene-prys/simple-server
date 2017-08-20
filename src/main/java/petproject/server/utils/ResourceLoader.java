package petproject.server.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourceLoader {
    private static final String FILE_NOT_FOUND_RESOURCE = "404.html";
    private static final String INDEX_RESOURCE = "index.html";

    private ResourceLoader(){}

    private static class SingletonHolder {
        private static final ResourceLoader instance = new ResourceLoader();
    }

    public static ResourceLoader getInstance(){
        return SingletonHolder.instance;
    }

    public boolean isResourceExist(String resourceName) {
        Path path = Paths.get(getPathToResource(resourceName));
        return Files.exists(path) && !Files.isDirectory(path);
    }

    public ByteBuffer load(String resourceName) throws IOException {
        if (!isResourceExist(resourceName)) {
            return null;
        }

        RandomAccessFile aFile = new RandomAccessFile(getPathToResource(resourceName), "r");
        FileChannel inChannel = aFile.getChannel();
        MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        buffer.load();

        inChannel.close();
        aFile.close();
        return buffer;
    }

    public ByteBuffer loadResource404() throws IOException, URISyntaxException {
        if (isResourceExist(FILE_NOT_FOUND_RESOURCE)) {
            return load(FILE_NOT_FOUND_RESOURCE);
        }

        return loadDefault404();
    }

    private String getPathToResource(String resourceName) {
        String resource = resourceName.isEmpty() || "/".equals(resourceName) ? INDEX_RESOURCE : resourceName;
        String jarFullPath = ResourceLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        Path jarPath = Paths.get(jarFullPath);
        String jarDirPath = jarPath.getParent().toString();

        return jarDirPath + FileSystems.getDefault().getSeparator() + resource;
    }

    private ByteBuffer getDefaultResource(String resourceName) throws IOException, URISyntaxException {
        System.out.println("    " + ResourceLoader.class.getClassLoader().getResource(resourceName));
        InputStream initialStream = ResourceLoader.class.getClassLoader().getResourceAsStream(resourceName);

        byte[] buffer = new byte[1024];
        initialStream.read(buffer);
        return ByteBuffer.wrap(buffer);
    }

    private ByteBuffer loadDefault404() throws IOException, URISyntaxException {
        return getDefaultResource(FILE_NOT_FOUND_RESOURCE);
    }
}
