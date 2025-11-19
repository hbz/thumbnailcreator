package de.hbz.nrw.thumbnailcreator.cache;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileSystemCache {
	
	@Value("${thumbs.cache.location}")
    private String cacheLocation;

	public File getCacheFile(String key, String host) {
        String hostDirectory = host.replaceAll("\\.", "_");
        
        File hostDir = new File(cacheLocation, hostDirectory);
        return new File(hostDir, key + ".jpg");
    }

    public boolean isCached(String key, String host) {
        return getCacheFile(key, host).exists();
    }

    public void saveToCache(String key, byte[] data, String host) throws IOException {
        File cacheFile = getCacheFile(key, host);
        cacheFile.getParentFile().mkdirs();
        Files.write(cacheFile.toPath(), data);
    }

    public byte[] loadFromCache(String key, String host) throws IOException {
        return Files.readAllBytes(getCacheFile(key, host).toPath());
    }

}
