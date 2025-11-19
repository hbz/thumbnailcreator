package de.hbz.nrw.thumbnailcreator.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import de.hbz.nrw.thumbnailcreator.cache.FileSystemCache;
import de.hbz.nrw.thumbnailcreator.cache.InMemoryCacheService;
import de.hbz.nrw.thumbnailcreator.configuration.ThumbnailProperties;
import de.hbz.nrw.thumbnailcreator.model.FetchedResource;
import de.hbz.nrw.thumbnailcreator.service.ThumbnailService;
import de.hbz.nrw.thumbnailcreator.util.UrlUtil;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Controller
public class ThumbnailController {
	
	private ThumbnailService thumbnailService;
	private ThumbnailProperties properties;
	private FileSystemCache fileSystemCache;
	private InMemoryCacheService inMemoryCache;
	
	@GetMapping
    public Object getThumbnailBySize(@RequestParam(required = false) String url,
                                     @RequestParam(defaultValue = "150") int size,
                                     @RequestParam(defaultValue = "false") boolean refresh) throws IOException, URISyntaxException {
    	if (url == null || url.isEmpty()) {
            return "upload";
        }

    	URL resourceUrl = new URI(url.replace(" ", "%20")).toURL();
        //URL resourceUrl = new URL(url);
        String host = resourceUrl.getHost();
        
        // check access permission
        if (!properties.isWhitelisted(host)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.TEXT_HTML)
                    .body("Thumbs is not allowed to access this URL!".getBytes());
        }

        String cacheKey = inMemoryCache.generateCacheKey(url, size);
        
        // check caches
        if (!refresh) {
        	// first look at in-memory-cache
            if (inMemoryCache.isCached(cacheKey)) {
                log.debug("In-Memory Cache-Hit for {}", cacheKey);
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
                        .body(inMemoryCache.loadFromCache(cacheKey));
            // second look at filesystem cache
            } else if (fileSystemCache.isCached(cacheKey, host)) {
                log.debug("File-System Cache-Hit for {}", cacheKey);
                byte[] cachedThumbnail = fileSystemCache.loadFromCache(cacheKey, host);
                inMemoryCache.saveToCache(cacheKey, cachedThumbnail);
                return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(cachedThumbnail);
            }
        }

        // load resource/file and check mime-type (allow redirects)
        FetchedResource resource;
        try {
            resource = UrlUtil.fetchResource(resourceUrl);
        } catch (Exception e) {
            log.error("Error loading resource", e);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(("Error loading file: " + e.getMessage()).getBytes());
        }

        String mimeType = resource.getMimeType();
        byte[] fileBytes = resource.getData();

        log.info("Detected mime-type: {}", mimeType);
        log.info("File size (downloaded): {} KB", fileBytes.length / 1024);

        byte[] thumbnail;
        try {
            thumbnail = thumbnailService.generateThumbnail(fileBytes, size, mimeType);
        } catch (Exception e) {
            log.error("Error processing thumbnail", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Error generating thumbnail".getBytes());
        }

        // save to in-memory-cache
        inMemoryCache.saveToCache(cacheKey, thumbnail);

        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(thumbnail);
    }
}
