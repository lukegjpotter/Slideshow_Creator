package com.github.lukegjpotter.app.slideshowcreator;

/**
 * SlideshowPlayerActivity.java
 *
 * @author Luke GJ Potter - lukegjpotter
 * Date: 17/Jun/2013
 *
 * @version 1.1
 *
 * Description:
 *     This activity plays the selected slideshow
 *     that's passed as an Intent extra.
 */

import java.io.FileNotFoundException;
import java.io.IOException;
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
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

public class SlideshowPlayerActivity extends Activity {

    // Used for logging.
    private static final String TAG = "SLIDESHOW";

    // Used for resuming the slideshow's position.
    private static final String MEDIA_TIME = "MEDIA_TIME";
    private static final String IMAGE_INDEX = "IMAGE_INDEX";
    private static final String SLIDESHOW_NAME = "SLIDESHOW_NAME";

    // Class level variables.
    private static final int DURATION = 5000; // Five seconds per slide.
    private ImageView imageView;              // Displays the current image.
    private VideoView videoView;              // Displays the current video.
    private String slideshowName;             // Name of the current slideshow.
    private SlideshowInfo slideshow;          // Slideshow being played.
    private BitmapFactory.Options options;    // Options for loading images.
    private Handler handler;                  // Used to update the slideshow.
    private int nextItemIndex;                // Index of the next image to display.
    private int mediaTime;                    // Time in milliseconds from which media should play.
    private MediaPlayer mediaPlayer;          // Plays the background music, if any.

    /**
     * Initializes the SlideshowPlayerActivity.
     *
     * @param savedInstanceState
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slideshow_player);

        imageView = (ImageView) findViewById(R.id.imageView);
        videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            /**
             * Update the slideshow after the video has finished playing.
             *
             * @param mediaPlayer
             */
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                handler.post(updateSlideshow);
            }
        });

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
     * Called after onCreate() and sometimes onStop().
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
     * Save slideshow state so ti can be restored in onCreate().
     *
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        // If there is a mediaPlayer, store media's current position.
        if (mediaPlayer != null) outState.putInt(MEDIA_TIME, mediaPlayer.getCurrentPosition());

        // Save nextItemIndex and slideshowName.
        outState.putInt(IMAGE_INDEX, nextItemIndex - 1);
        outState.putString(SLIDESHOW_NAME, slideshowName);
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

            if (nextItemIndex >= slideshow.size()) {

                // If there is music playing.
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {

                    mediaPlayer.reset();
                }

                // Return to the launching Activity.
                finish();
            } else {

                MediaItem item = slideshow.getMediaItemAt(nextItemIndex);

                if (item.getType() == MediaItem.MediaType.IMAGE) {
                    imageView.setVisibility(View.VISIBLE);
                    videoView.setVisibility(View.INVISIBLE);
                    new LoadImageTask().execute(Uri.parse(item.getPath()));
                } else {
                    imageView.setVisibility(View.INVISIBLE);
                    videoView.setVisibility(View.VISIBLE);
                    playVideo(Uri.parse(item.getPath()));
                }

                ++nextItemIndex;
            }
        }

        /**
         * Task to load thumbnails in a separate thread.
         */
        class LoadImageTask extends AsyncTask <Uri, Object, Bitmap> {

            /**
             * Load Images.
             *
             * @param uris
             * @return bitmap
             */
            @Override
            protected Bitmap doInBackground(Uri... uris) {

                return getBitmap(uris[0], getContentResolver(), options);
            }

            /**
             * Set thumbnail on ListView.
             *
             * @param bitmap
             */
            @Override
            protected void onPostExecute(Bitmap bitmap) {

                super.onPostExecute(bitmap);

                BitmapDrawable next = new BitmapDrawable(bitmap);
                next.setGravity(Gravity.CENTER);
                Drawable previous = imageView.getDrawable();

                // If previous is a TransitionDrawable, get its second Drawable item.
                if (previous instanceof TransitionDrawable) {

                    previous = ((TransitionDrawable) previous).getDrawable(1);
                }

                if (previous == null) {

                    imageView.setImageDrawable(next);
                } else {

                    Drawable[] drawables = {previous, next};
                    TransitionDrawable transition = new TransitionDrawable(drawables);
                    imageView.setImageDrawable(transition);
                    transition.startTransition(1000);
                }

                handler.postDelayed(updateSlideshow, DURATION);
            }

            /**
             * Utility method to get a Bitmap from a Uri.
             *
             * @param uri
             * @param cr
             * @param options
             * @return bitmap
             */
            public Bitmap getBitmap(Uri uri, ContentResolver cr, BitmapFactory.Options options) {

                Bitmap bitmap = null;

                // Get the image.
                try {

                    InputStream input = cr.openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(input, null, options);
                    input.close();
                } catch (FileNotFoundException e) {

                    Log.v(TAG, e.toString());
                } catch (IOException e) {

                    Log.v(TAG, e.toString());
                }

                return bitmap;
            }

            /**
             * Play a video.
             *
             * @param videoUri
             */
            private void playVideo(Uri videoUri) {

                // Configure the video view and play video.
                videoView.setVideoURI(videoUri);
                videoView.setMediaController(new MediaController(SlideshowPlayerActivity.this));
                videoView.start();
            }
        }
    };
}
