package de.hbz.nrw.thumbnailcreator.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

import de.hbz.nrw.thumbnailcreator.model.FetchedResource;

public class UrlUtil {
  
    private static final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    /**
     * Load resource/file and check mime-type
     */
    public static FetchedResource fetchResource(URL url)
            throws IOException, InterruptedException, URISyntaxException {

        HttpRequest req = HttpRequest.newBuilder(url.toURI())
                .GET()
                .header("User-Agent", "Mozilla/5.0 (thumbnailer)")
                .timeout(Duration.ofSeconds(30))
                .build();

        HttpResponse<byte[]> resp = client.send(req, HttpResponse.BodyHandlers.ofByteArray());

        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new IOException("HTTP " + resp.statusCode() + " for " + url);
        }

        byte[] body = resp.body();

        // 1. content-type from Header
        Optional<String> ct = resp.headers().firstValue("content-type");
        if (ct.isPresent()) {
            String mime = ct.get().split(";")[0].trim();
            if (!mime.isEmpty() && !mime.equalsIgnoreCase("application/octet-stream")) {
                return new FetchedResource(body, mime);
            }
        }

        // 2. guess content-type
        try (InputStream is = new ByteArrayInputStream(body)) {
            String guessed = URLConnection.guessContentTypeFromStream(is);
            if (guessed != null) {
                return new FetchedResource(body, guessed);
            }
        }

        // 3. fallback by file suffix
        String path = url.getPath().toLowerCase();
        if (path.endsWith(".pdf")) return new FetchedResource(body, "application/pdf");
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return new FetchedResource(body, "image/jpeg");
        if (path.endsWith(".png")) return new FetchedResource(body, "image/png");

        return new FetchedResource(body, "application/octet-stream");
    }

    
}
