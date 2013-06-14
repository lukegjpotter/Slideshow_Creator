package com.github.lukegjpotter.app.slideshowcreator;

/**
 * @author Luke GJ Potter - lukepotter
 * Date: 23/May/2013
 *
 * Version 1.0
 *
 * Description:
 *     Represents an image or video in a slideshow.
 */

import java.io.Serializable;

public class MediaItem implements Serializable {

    private static final long serialVersionUID = 1L; // Class's version number.

    // Constants for media types.
    public static enum MediaType { IMAGE, VIDEO }

    private final MediaType type; // This MediaItem is an IMAGE or VIDEO.
    private final String path;    // Location of this MediaItem.

    /**
     * Constructor for MediaItem.
     *
     * @param mediaType
     * @param location
     */
    public MediaItem(MediaType mediaType, String location) {

        type = mediaType;
        path = location;
    }

    /**
     * Get the MediaType of this image or video.
     *
     * @return type
     */
    public MediaType getType() {

        return type;
    }

    /**
     * Return the description of this image or video.
     *
     * @return path
     */
    public String getPath() {

        return path;
    }
}
