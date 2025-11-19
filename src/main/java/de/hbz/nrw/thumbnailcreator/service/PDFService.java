package de.hbz.nrw.thumbnailcreator.service;

import java.awt.image.BufferedImage;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;

@Service
public class PDFService {

    public BufferedImage extractImageFromPDF(byte[] pdfBytes) throws IOException {
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFRenderer renderer = new PDFRenderer(document);
            return renderer.renderImageWithDPI(0, 96);
        }
    }
}
