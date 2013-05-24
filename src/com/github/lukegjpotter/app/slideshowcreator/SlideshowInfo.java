package com.github.lukegjpotter.app.slideshowcreator;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Luke Potter - lukegjpotter
 * 
 * @version 1.0
 * 
 * <p>Date: 07/Apr/2013</p>
 * <p>Description:</p>
 * <p>
 * This class stores the information for a slideshow object.
 * Information stored is:
 *     * Name of the slideshow,
 *     * List of MediaItems in the slideshow,
 *     * Path to the music file that the slideshow uses.
 * </p>
 */
public class SlideshowInfo implements Serializable {

    // Class's version number.
    private static final long serialVersionUID = 1L;

	private String name;
	private List<MediaItem> mediaItemsList;
	private String musicPath;

	public SlideshowInfo(String slideshowName) {

		name = slideshowName;
        mediaItemsList = new ArrayList<MediaItem>();
		setMusicPath(null); // Currently there isn't any music in the slideshow.
	}

	public String getName() {
		return name;
	}

	public List<MediaItem> getMediaItemsList() {
		return mediaItemsList;
	}

	public void addMediaItem(MediaItem.MediaType type, String path) {
        mediaItemsList.add(new MediaItem(type, path));
	}

	public MediaItem getMediaItemAt(int index) {
		if (index >= 0 && index < size())
			return mediaItemsList.get(index);
		else
			return null;
	}

	public int size() {
		return mediaItemsList.size();
	}

	public String getMusicPath() {
		return musicPath;
	}

	public void setMusicPath(String musicPath) {
		this.musicPath = musicPath;
	}

}
