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
import android.view.SurfaceHolder;
import android.view.SurfaceView;

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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}