package com.github.lukegjpotter.app.slideshowcreator;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SlideshowActivity extends ListActivity {

	public static final String NAME_EXTRA = "NAME";
	
	static List<SlideshowInfo> slideshowList;
	private ListView slideshowListView;
	private SideshowAdapter slideshowAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		slideshowListView = getListView();
		
		slideshowList = new ArrayList<SlideshowInfo>();
		slideshowAdapter = new SideshowAdapter(this, slideshowList);
		slideshowListView.setAdapter(slideshowAdapter);
		
		// Create an alert Dialog to welcome the user to the app.
		// TODO: Use preferences to only display this the first time the app is launched.
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.welcome_message_title);
		builder.setMessage(R.string.welcome_message);
		builder.setPositiveButton(R.string.button_ok, null);
		builder.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		super.onCreateOptionsMenu(menu);
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.slideshow, menu);
		return true;
	}

	private class SideshowAdapter extends ArrayAdapter<SlideshowInfo> {
		
		private List<SlideshowInfo> items;
		private LayoutInflater inflater;
		
		public SideshowAdapter(Context context, List<SlideshowInfo> items) {
			
			super(context, -1, items);
		}
	}
}
