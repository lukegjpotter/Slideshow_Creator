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

    private static final String TAG = "SLIDESHOW";

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

        imageView = (ImageView) findViewById(R.id.imageView);

        if (savedInstanceState == null) {

            // Get slideshow name from Intent's extras.
            slideshowName = getIntent().getStringExtra(SlideshowActivity.NAME_EXTRA);
            mediaTime = 0;     // Position in media clip.
            nextItemIndex = 0; // Start from first image.
        } else { // Activity Resuming.

            // Get the parameters that were saved when config changed.
            slideshowName = savedInstanceState.getString(SLIDESHOW_NAME);
            mediaTime = savedInstanceState.getInt(MEDIA_TIME);
            nextItemIndex = savedInstanceState.getInt(IMAGE_INDEX);
        }

        // Get SlideshowInfo for slideshow to play.
        slideshow = SlideshowActivity.getSlideshowInfo(slideshowName);

        // Configure BitmapFactory.Options for loading images.
        options = new BitmapFactory.Options();
        options.inSampleSize = 4; // Sample at 1/4 original width/height.

        // If there is music to play.
        if (slideshow.getMusicPath() != null) {

            // Try to create the MediaPlayer to play the music.
            try {

                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(this, Uri.parse(slideshow.getMusicPath()));
                mediaPlayer.prepare();         // Prepare the MediaPlayer to play.
                mediaPlayer.setLooping(true);  // Loop the music.
                mediaPlayer.seekTo(mediaTime); // Seek to mediaTime.
            } catch (Exception e) {

                Log.v(TAG, e.toString());
            }
        }

        // Control the slideshow via the handler.
        handler = new Handler();
	}

    /**
     * Called after onCreate() and somethimes onStop().
     */
    @Override
    protected void onStart() {

        super.onStart();
        // Post updateSlideshow to execute.
        handler.post(updateSlideshow);
    }

    /**
     * Called when the Activity is paused.
     */
    @Override
    protected void onPause() {

        super.onPause();

        if (mediaPlayer != null) mediaPlayer.pause();
    }

    /**
     * Called after onStart() or onPause().
     */
    @Override
    protected void onResume() {

        super.onResume();

        if (mediaPlayer != null) mediaPlayer.start();
    }

    /**
     * Called when the Activity stops.
     */
    @Override
    protected void onStop() {

        super.onStop();

        // Prevent slideshow from operating when in background.
        handler.removeCallbacks(updateSlideshow);
    }

    /**
     * Called when the Activity is destroyed.
     */
    @Override
    protected void onDestroy() {

        super.onDestroy();

        // Release the MediaPlayer resources.
        if (mediaPlayer != null) mediaPlayer.release();
    }

    /**
     * Anonymous inner class that implements Runnable to control the slideshow.
     */
    private Runnable updateSlideshow = new Runnable() {

        /**
         * Run method.
         *
         * This gets called automatically.
         */
        @Override
        public void run() {

        }
    };
}
