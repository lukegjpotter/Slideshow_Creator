package com.github.lukegjpotter.app.slideshowcreator;

/**
 * @author Luke GJ Potter - lukegjpotter
 * Date: 22/May/2013
 *
 * @version 1.0
 *
 * Description:
 *     This is the activity for taking a picture
 *     with the device's camera.
 */

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.View.OnTouchListener;

import java.io.IOException;
import java.util.List;

public class PictureTakerActivity extends Activity {

    // Tag for logging.
    private static final String TAG = "PICTURE_TAKER";

    private SurfaceView surfaceView;     // Used to display the camera preview.
    private SurfaceHolder surfaceHolder; // Manages the SurfaceView changes.
    private boolean isPreviewing;        // Is the previewing running?

    private Camera camera;                                 // Used to capture image data.
    private List <String> effects;                         // Supported colour effects for Camera.
    private List <Camera.Size> sizes;                      // Supported preview sizes for Camera.
    private String effect = Camera.Parameters.EFFECT_NONE; // Default effect.

    /**
     * Called when the activity is first created.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_taker);

        // Initialise the surfaceView and set its touch listener.
        surfaceView = (SurfaceView) findViewById(R.id.cameraSurfaceView);
        surfaceView.setOnTouchListener(touchListener);

        // Initialise the surfaceHolder and set object to handles its callbacks.
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(surfaceCallback);
    }

    /**
     * Create the Activity's menu from list of supported colour effects.
     *
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        super.onCreateOptionsMenu(menu);

        // Create menu items dor each supported effect.
        for (String effect : effects) {
            menu.add(effect);
        }

        return true;
    }

    /**
     * Handle choice from options menu.
     *
     * @param item
     * @return true
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Camera.Parameters p = camera.getParameters(); // Get Parameters.
        p.setColorEffect(item.getTitle().toString()); // Set Colour Effect.
        camera.setParameters(p);                      // Apply the new parameters.

        return true;
    }

    /**
     * Handles SurfaceHolder.Callback events.
     */
    private SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        /**
         * Initialise the camera when SurfaceView is created.
         *
         * @param surfaceHolder
         */
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {

            camera = Camera.open(); // Defaults to back facing camera.
            effects = camera.getParameters().getSupportedColorEffects();
            sizes = camera.getParameters().getSupportedPreviewSizes();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

            if (isPreviewing) {       // If there's already a preview running.
                camera.stopPreview(); // Stop the preview.
            }

            // Configure and set the camera parameters.
            Camera.Parameters p = camera.getParameters();
            p.setPreviewSize(sizes.get(0).width, sizes.get(0).height);
            p.setColorEffect(effect); // Use the current selected effect.
            camera.setParameters(p);  // Apply the new parameters.

            try {
                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                Log.v(TAG, e.toString());
            }

            camera.stopPreview(); // Begin the preview.
            isPreviewing = true;
        }

        /**
         * Release resources after the SurfaceView is destroyed.
         *
         * @param surfaceHolder
         */
        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            camera.stopPreview(); // Stop the Camera preview.
            isPreviewing = false;
            camera.release();     // Release the Camera's Object resources.
        }
    };

    /**
     * Handles Camera callbacks.
     */
    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {

        }
    };

    /**
     * Takes picture when the user touches the screen.
     */
    private OnTouchListener touchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            // Take a picture.
            camera.takePicture(null, null, pictureCallback);
            return false;
        }
    };
}