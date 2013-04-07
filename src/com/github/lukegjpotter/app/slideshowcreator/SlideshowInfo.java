package com.github.lukegjpotter.app.slideshowcreator;

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
 *     * List of Images in the slideshow,
 *     * Path to the music file that the slideshow uses.
 * </p>
 */
public class SlideshowInfo {

	private String name;
	private List<String> imageList;
	private String musicPath;

	public SlideshowInfo(String slideshowName) {

		name = slideshowName;
		imageList = new ArrayList<String>();
		setMusicPath(null);
	}

	public String getName() {
		return name;
	}

	public List<String> getImageList() {
		return imageList;
	}

	public void addImage(String path) {
		imageList.add(path);
	}

	public String getImageAt(int index) {
		if (index >= 0 && index < size())
			return imageList.get(index);
		else
			return null;
	}

	public int size() {
		return imageList.size();
	}

	public String getMusicPath() {
		return musicPath;
	}

	public void setMusicPath(String musicPath) {
		this.musicPath = musicPath;
	}

}
