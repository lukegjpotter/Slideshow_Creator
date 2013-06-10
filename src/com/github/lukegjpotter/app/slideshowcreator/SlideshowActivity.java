package com.github.lukegjpotter.app.slideshowcreator;

/**
 * SlideshowActivity.java
 *
 * @author Luke GJ Potter - lukegjpotter
 * Date: 07/Apr/2013
 *
 * @version 1.0
 *
 * Descrtiption:
 *     This is the main activity for the Slideshow.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
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
import android.util.Log;
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
	
	static List<SlideshowInfo> slideshowList; // List of slideshows.
	private ListView slideshowListView;       // This ListActivity's ListView.
	private SideshowAdapter slideshowAdapter; // Adapter for the ListView.
    private File slideshowFile;               // File representing location of the slideshows.
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		slideshowListView = getListView(); // Get the build-in ListView.
		
		// Get File location and start task to load slideshows.
        slideshowFile = new File(getExternalFilesDir(null).getAbsolutePath() + "/SlideshowData.ser");
        new LoadSlideshowsTask().execute((Object []) null);
		
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

	/**
	 * Class for implementing the "ViewHolder pattern"
	 * for better ListView performance.
	 */
	private static class ViewHolder {
		
		TextView nameTextView; // Refers to ListView item's TextView.
		ImageView imageView;   // Refers to ListView item's ImageView.
		Button playButton;     // Refers to ListView item's Play Button.
		Button editButton;     // Refers to ListView item's Edit Button.
		Button deleteButton;   // Refers to ListView item's Delete Button.
	}
	
	/**
	 * ArrayAdapter subclass that displays a slideshow's name,
	 * first image and "Play", "Edit" and "Delete" Buttons. 
	 */
	private class SideshowAdapter extends ArrayAdapter<SlideshowInfo> {
		
		private List<SlideshowInfo> items;
		private LayoutInflater inflater;
		
		public SideshowAdapter(Context context, List<SlideshowInfo> items) {
			
			super(context, -1, items);
			this.items = items;
			inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		/**
		 * Returns the View to display at a given position.
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder viewHolder; // Holds a reference to current item's GUI.
			
			// If convertView is null, inflate GUI and create ViewHolder; otherwise, get existing ViewHolder.
			if (convertView == null) {
				
				convertView = inflater.inflate(R.layout.view_slideshow_list_item, null);
				
				// Set up ViewHolder for this ListView item.
				viewHolder = new ViewHolder();
				viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.nameTextView);
				viewHolder.imageView = (ImageView) convertView.findViewById(R.id.slideshowImageView);
				viewHolder.playButton = (Button) convertView.findViewById(R.id.playButton);
				viewHolder.editButton = (Button) convertView.findViewById(R.id.editButton);
				viewHolder.deleteButton = (Button) convertView.findViewById(R.id.deleteButton);
				convertView.setTag(viewHolder); // Store as View's tag.
			} else { // Get the ViewHolder from the convertView's tag.
				
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			// Get the slideshow the display its name in nameTextView.
			SlideshowInfo slideshowInfo = items.get(position);
			viewHolder.nameTextView.setText(slideshowInfo.getName());
			
			// If there is at least one image in this slideshow.
			if (slideshowInfo.size() > 0) {
				
				// Create a bitmap using the slideshow's first image or video.
				String firstItem = slideshowInfo.getImageAt(0);
				new LoadThumbnailTask().execute(viewHolder.imageView, Uri.parse(firstItem));
			}
			
			// Set tag and OnClickListener for the "Play" button.
			viewHolder.playButton.setTag(slideshowInfo);
			viewHolder.playButton.setOnClickListener(playButtonListener);
			
			// Create and set OnClickListener for the "Edit" button.
			viewHolder.editButton.setTag(slideshowInfo);
			viewHolder.editButton.setOnClickListener(editButtonListener);
			
			// Create and set OnClickListener for the "Delete" button.
			viewHolder.deleteButton.setTag(slideshowInfo);
			viewHolder.deleteButton.setOnClickListener(deleteButtonListener);
			
			return convertView; // Return the View for this position.
		}
	}

	/**
	 * Task to load thumbnails in a separate thread.
	 */
	private class LoadThumbnailTask extends AsyncTask<Object, Object, Bitmap> {
		
		ImageView imageView; // Displays the thumbnail.

		// Load thumbnail: ImageView and Uri as args.
		@Override
		protected Bitmap doInBackground(Object... params) {
			
			imageView = (ImageView) params[0];
			
			return SlideshowActivity.getThumbnail((Uri) params[1], getContentResolver(), new BitmapFactory.Options());
		}
		
		/**
		 * Set thumbnail on ListView.
		 */
		@Override
		protected void onPostExecute(Bitmap result) {
			
			super.onPostExecute(result);
			imageView.setImageBitmap(result);
		}
	}

    /**
     * Class to load the List<SlideshowInfo> object from the device's filesystem.
     */
    private class LoadSlideshowsTask extends AsyncTask<Object, Object, Object> {

        // Load from non-GUI thread.
        @Override
        protected Object doInBackground(Object... arg0) {

            // If the file exists.
            if (slideshowFile.exists()) {

                try {

                    ObjectInputStream input = new ObjectInputStream(new FileInputStream(slideshowFile));
                    // Populate the SlideshowList from the previously serialized file.
                    slideshowList = (List<SlideshowInfo>) input.readObject();

                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            // Display error message.
                            Toast message = Toast.makeText(SlideshowActivity.this, R.string.message_error_reading, Toast.LENGTH_LONG);
                            message.setGravity(Gravity.CENTER, message.getXOffset() / 2, message.getYOffset() / 2);
                            message.show();

                            Log.v(TAG, e.toString());
                        }
                    });
                }
            }

            // If the SlideshowList doesn't exist, create it.
            if (slideshowList == null) {
                slideshowList = new ArrayList<SlideshowInfo>();
            }

            return (Object) null; // Method must satisfy the return type.
        }

        // Create the ListView's adapter on the GUI thread.
        @Override
        protected void onPostExecute(Object result) {

            super.onPostExecute(result);

            // Create and set the ListView's adapter.
            slideshowAdapter = new SlideshowAdapter(SlideshowActivity.this, slideshowList);
            slideshowListView.setAdapter(slideshowAdapter);
        }
    }

	/**
	 * Respond to events generated by the "Play" Button.
	 */
	OnClickListener playButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			// Create an Intent to launch the SlideshowPlayer Activity.
			Intent playSlideshow = new Intent(SlideshowActivity.this, SlideshowPlayerActivity.class);
			playSlideshow.putExtra(NAME_EXTRA, ((SlideshowInfo) v.getTag()).getName());
			
			startActivity(playSlideshow);
		}
	};

	/**
	 * Respond to events generated by the "Edit" Button.
	 */
	OnClickListener editButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			// Create an Intent to launch the SlideshowEditor Activity.
			Intent editSlideshow = new Intent(SlideshowActivity.this, SlideshowEditorActivity.class);
			editSlideshow.putExtra(NAME_EXTRA, ((SlideshowInfo) v.getTag()).getName());

			startActivityForResult(editSlideshow, 0);
		}
	};

	/**
	 * Respond to events generated by the "Delete" Button.
	 */
	OnClickListener deleteButtonListener = new OnClickListener() {
		
		@Override
		public void onClick(final View v) {
			
			// Create a new AlertDialog Builder.
			AlertDialog.Builder builder = new AlertDialog.Builder(SlideshowActivity.this);
			builder.setTitle(R.string.dialog_confirm_delete);
			builder.setMessage(R.string.dialog_confirm_delete_message);
			builder.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					SlideshowActivity.slideshowList.remove((SlideshowInfo) v.getTag());
					slideshowAdapter.notifyDataSetChanged();
				}
			});
			
			builder.setNegativeButton(R.string.button_cancel, null);
			builder.show();
		}
	};

	/**
	 * Utility method to locate SlideshowInfo object by slideshow name.
	 */
	public static SlideshowInfo getSlideshowInfo(String name) {
		
		// Foreach SlideshowInfo.
		for (SlideshowInfo slideshowInfo : slideshowList) {
			
			if (slideshowInfo.getName().equals(name)) {
				
				return slideshowInfo; // Matching object.
			}
		}
		
		return null; // No matching object.
	}

	/**
	 * Utility method to get a thumbnail image bitmap.
	 */
	public static Bitmap getThumbnail(Uri uri, ContentResolver cr, BitmapFactory.Options options) {
		
		int id = Integer.parseInt(uri.getLastPathSegment());
		
		Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, options);
		
		return bitmap;
	}
}