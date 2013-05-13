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

	// SlideshowEditor request code passed to startActivityForResult.
	private static final int EDIT_ID = 0;
	
	/**
	 * Handle choice from options menu.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		// Get a reference to the LayoutInflator service.
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		// Inflate alert_dialog_set_slideshow_name.xml to create an EditText.
		View view = inflater.inflate(R.layout.alert_dialog_set_slideshow_name, null);
		final EditText nameEditText = (EditText) view.findViewById(R.id.nameEditText);
		
		// Create an input dialog to get the slideshow's name from the user.
		AlertDialog.Builder inputBuilder = new AlertDialog.Builder(this);
		inputBuilder.setView(view); // Set the dialog's custom view.
		inputBuilder.setTitle(R.string.dialog_set_name_title);
		
		inputBuilder.setPositiveButton(R.string.button_set_slideshow_name, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				// Create a SlideshowInfo object for the new slideshow.
				String name = nameEditText.getText().toString().trim();
				
				if (name.length() != 0) {
					
					slideshowList.add(new SlideshowInfo(name));
					
					// Create Intent to launch the SlideshowEditor Activity,
					// add slideshow name as an extra and start the Activity.
					Intent editSlideshowIntent = new Intent(SlideshowActivity.this, SlideshowEditorActivity.class);
					editSlideshowIntent.putExtra("NAME_EXTRA", name);
					startActivityForResult(editSlideshowIntent, 0);
				} else {
					
					// Display message that slideshow must have a name.
					Toast message = Toast.makeText(SlideshowActivity.this, R.string.message_name, Toast.LENGTH_SHORT);
					message.setGravity(Gravity.CENTER, message.getXOffset() / 2, message.getYOffset() / 2);
					message.show();
				}
			}
		});
		
		inputBuilder.setNegativeButton(R.string.button_cancel, null);
		inputBuilder.show();
		
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Refresh ListView after slideshow editing in complete.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		slideshowAdapter.notifyDataSetChanged(); // Refresh the Adapter.
	}

	private class SideshowAdapter extends ArrayAdapter<SlideshowInfo> {
		
		private List<SlideshowInfo> items;
		private LayoutInflater inflater;
		
		public SideshowAdapter(Context context, List<SlideshowInfo> items) {
			
			super(context, -1, items);
		}
	}
}
