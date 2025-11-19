package de.hbz.nrw.thumbnailcreator.configuration;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.Data;

@Configuration
@PropertySource("file:/etc/thumbs/application.properties")
@ConfigurationProperties(prefix = "thumbs")
@Data
public class ThumbnailProperties {
	
	private List<String> whiteList; 
	private Resource pathToDefaultPic;
	
	public boolean isWhitelisted(String host) {
		return getWhiteList().stream().anyMatch(element -> element.equals(host));
    }
	
}


