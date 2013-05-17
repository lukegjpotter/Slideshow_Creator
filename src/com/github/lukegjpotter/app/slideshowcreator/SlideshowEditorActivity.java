package com.github.lukegjpotter.app.slideshowcreator;

import java.util.List;

import android.os.Bundle;
import android.app.ListActivity;
import android.view.Menu;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

public class SlideshowEditorActivity extends ListActivity {

    // SlideshowEditorAdapter to display slideshow in ListView.
    private SlideshowEditorAdapter slideshowEditorAdapter;
    private SlideshowInfo slideshow; // Contains Slideshow Data.

	@Override
	protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slideshow_editor);

        // Retrieve the slideshow.
        String name = getIntent().getStringExtra(SlideshowActivity.NAME_EXTRA);
        slideshow = SlideshowActivity.getSlideshowInfo(name);

        // Set appropriate OnClickListeners for each Button.
        Button doneButton = (Button) findViewById(R.id.doneButton);
        doneButton.setOnClickListener(doneButtonListener);

        Button addPictureButton = (Button) findViewById(R.id.addPictureButton);
        doneButton.setOnClickListener(addPictureButtonListener);

        Button addMusicButton = (Button) findViewById(R.id.addMusicButton);
        doneButton.setOnClickListener(addMusicButtonListener);

        Button playButton = (Button) findViewById(R.id.playButton);
        doneButton.setOnClickListener(playButtonListener);

        // Get ListView and set it's adapter for displaying list of images.
        slideshowEditorAdapter = new SlideshowEditorAdapter(this, slideshow.getImageList());
        getListView().setAdapter(slideshowEditorAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.slideshow_editor, menu);
		return true;
	}

    private class SlideshowEditorAdapter extends ArrayAdapter <String> {

        private List <String> items; // List of image URIs.
        private LayoutInflater inflater;

        public SlideshowEditorAdapter(Context context, List <String> items) {

            super(context, -1, items);

            this.items = items;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
    }
}
