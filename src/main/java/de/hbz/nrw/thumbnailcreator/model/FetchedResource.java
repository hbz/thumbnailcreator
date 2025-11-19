package de.hbz.nrw.thumbnailcreator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Container for downloaded resources/files
 * (i.e. PDF or image files), incl. mime-type.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FetchedResource {
    private byte[] data;
    private String mimeType;
}

