package de.hbz.nrw.thumbnailcreator.service;

import org.springframework.stereotype.Service;

import de.hbz.nrw.thumbnailcreator.configuration.ThumbnailProperties;
import lombok.AllArgsConstructor;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

@AllArgsConstructor
@Service
public class ThumbnailService {

    private PDFService pdfService;
    private ImageService imageService;
    private ThumbnailProperties properties;

    public byte[] generateThumbnail(byte[] fileBytes, int size, String mimeType) throws IOException {
        BufferedImage resizedImage;

        if ("application/pdf".equalsIgnoreCase(mimeType)) {
            try {
                BufferedImage imageFromPDF = pdfService.extractImageFromPDF(fileBytes);
                resizedImage = imageService.resizeImage(imageFromPDF, size);
                imageFromPDF.flush();
            } catch (Exception e) {
                throw new IOException("Failed to process PDF", e);
            }
        } else if (mimeType.startsWith("image/")) {
            try (InputStream imageStream = new ByteArrayInputStream(fileBytes)) {
                BufferedImage originalImage = ImageIO.read(imageStream);
                resizedImage = imageService.resizeImage(originalImage, size);
                originalImage.flush();
            }
        } else {
            try (InputStream defaultImageStream = properties.getPathToDefaultPic().getInputStream()) {
                BufferedImage defaultImage = ImageIO.read(defaultImageStream);
                resizedImage = imageService.resizeImage(defaultImage, size);
                defaultImage.flush();
            }
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(resizedImage, "jpg", baos);
            return baos.toByteArray();
        } finally {
            resizedImage.flush();
        }
    }
}

