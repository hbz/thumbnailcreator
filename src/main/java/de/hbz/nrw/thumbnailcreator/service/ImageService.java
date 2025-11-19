package de.hbz.nrw.thumbnailcreator.service;

import java.awt.image.BufferedImage;

import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;

@Service
public class ImageService {

	public BufferedImage resizeImage(BufferedImage originalImage, int targetSize) {
		return Scalr.resize(originalImage, targetSize);
	}
}
