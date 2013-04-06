package com.github.lukegjpotter.app.slideshowcreator;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class SlideshowPlayerActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slideshow_player);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.slideshow_player, menu);
		return true;
	}

}
