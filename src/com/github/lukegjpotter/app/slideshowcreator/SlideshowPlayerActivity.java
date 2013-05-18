package com.github.lukegjpotter.app.slideshowcreator;

/**
 * SlideshowPlayerActivity.java
 *
 * @author Luke GJ Potter - lukegjpotter
 * Date: 18/May/2013
 *
 * @version 1.0
 *
 * Descrtiption:
 *     This activity plays the selected slideshow
 *     that's passed as an Intent extra.
 */

import java.io.FileNotFoundException;
import java.io.InputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

public class SlideshowPlayerActivity extends Activity {

    private static final String MEDIA_TIME = "MEDIA_TIME";
    private static final String IMAGE_INDEX = "IMAGE_INDEX";
    private static final String SLIDESHOW_NAME = "SLIDESHOW_NAME";

    private static final int DURATION = 5000; // Five seconds per slide.
    private ImageView imageView;              // Displays the name of the current image.
    private String slideshowName;             // Name of the current slideshow.
    private SlideshowInfo slideshow;          // Slideshow being played.
    private BitmapFactory.Options options;    // Options for loading images.
    private Handler handler;                  // Used to update the slideshow.
    private int nextItemIndex;                // Index of the next image to display.
    private int mediaTime;                    // Time in milliseconds from which media should play.
    private MediaPlayer mediaPlayer;          // Plays the background music, if any.

    /**
     * Initializes the SlideshowPlayerActivity
     * @param savedInstanceState
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slideshow_player);
	}
}
