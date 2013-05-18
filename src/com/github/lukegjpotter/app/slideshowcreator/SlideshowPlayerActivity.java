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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slideshow_player);
	}
}
