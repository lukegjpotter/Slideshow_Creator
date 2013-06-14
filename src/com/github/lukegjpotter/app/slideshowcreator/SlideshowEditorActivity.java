package com.github.lukegjpotter.app.slideshowcreator;

/**
 * SlideshowEditorActivity.java
 *
 * @author Luke GJ Potter - lukegjpotter
 * Date: 14/Jun/2013
 *
 * @version 1.1
 *
 * Description:
 *     This Activity is used for building and
 *     editing a slideshow.
 */

import java.util.List;

import android.os.Bundle;
import android.app.ListActivity;
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

    // Set IDs for each type of media result.
    private static final int PICTURE_ID      = 1;
    private static final int MUSIC_ID        = 2;
    private static final int VIDEO_ID        = 3;
    private static final int TAKE_PICTURE_ID = 4;

    /**
     * The onCreate method.
     *
     * @param savedInstanceState
     */
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
        slideshowEditorAdapter = new SlideshowEditorAdapter(this, slideshow.getMediaItemsList());
        getListView().setAdapter(slideshowEditorAdapter);
	}

    /**
     * This method is called when an Activity, launched from this Activity, returns.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) { // If there was no error.

            Uri selectedUri = data.getData();

            // If the Activity returns an image.
            if (requestCode == PICTURE_ID || requestCode == TAKE_PICTURE_ID || requestCode == VIDEO_ID) {

                // Determines media type.
                MediaItem.MediaType type = (requestCode == VIDEO_ID ? MediaItem.MediaType.VIDEO : MediaItem.MediaType.IMAGE);

                // Add new MediaItem to the slideshow.
                slideshow.addMediaItem(type, selectedUri.toString());

                // Refresh the ListView.
                slideshowEditorAdapter.notifyDataSetChanged();
            } else if (requestCode == MUSIC_ID) { // Activity returns music.

                slideshow.setMusicPath(selectedUri.toString());
            }
        }
    }

    /**
     * Called when the user touches the "Done" button.
     */
    private OnClickListener doneButtonListener = new OnClickListener() {

        /**
         * Return to the previous activity.
         * @param view
         */
        @Override
        public void onClick(View view) {

            finish();
        }
    };

    /**
     * Called when the user touches the "Add Picture" Button.
     */
    private OnClickListener addPictureButtonListener = new OnClickListener() {

        /**
         * Launch image choosing activity.
         * @param view
         */
        @Override
        public void onClick(View view) {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, getResources().getText(R.string.chooser_image)), PICTURE_ID);
        }
    };

    /**
     * Called when the user touches the "Add Music" Button.
     */
    private OnClickListener addMusicButtonListener = new OnClickListener() {

        /**
         * Launch music choosing activity.
         * @param view
         */
        @Override
        public void onClick(View view) {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("audio/*");
            startActivityForResult(Intent.createChooser(intent, getResources().getText(R.string.chooser_music)), MUSIC_ID);
        }
    };

    /**
     * Called when the user touches the "Play" Button.
     */
    private OnClickListener playButtonListener = new OnClickListener() {

        /**
         * Plays the current slideshow.
         * @param view
         */
        @Override
        public void onClick(View view) {

            // Create new Intent to launch the SlideshowPlayerActivity activity.
            Intent playSlideshow = new Intent(SlideshowEditorActivity.this, SlideshowPlayerActivity.class);

            // Include the slideshow's name as an extra.
            playSlideshow.putExtra(SlideshowActivity.NAME_EXTRA, slideshow.getName());

            // Launch the Activity.
            startActivity(playSlideshow);
        }
    };

    /**
     * Called when the user touches the "Take Picture" button.
     */
    private OnClickListener takePictureButtonListener = new OnClickListener() {

        /**
         * Launch the image taking activity.
         * @param view
         */
        @Override
        public void onClick(View view) {

            Intent takePicture = new Intent(SlideshowEditorActivity.this, PictureTakerActivity.class);
            startActivityForResult(takePicture, TAKE_PICTURE_ID);
        }
    };

    /**
     * Called when the user touches the "Add Video" button.
     */
    private OnClickListener addVideoButtonListener = new OnClickListener() {

        /**
         * Launch the video choosing activity.
         * @param view
         */
        @Override
        public void onClick(View view) {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("video/*");
            startActivityForResult(Intent.createChooser(intent, getResources().getText(R.string.chooser_video)), VIDEO_ID);
        }
    };

    /**
     * Called when the user touches the "Delete" button next to an ImageView.
     */
    private OnClickListener deleteButtonListener = new OnClickListener() {

        /**
         * Removes the image.
         * @param view
         */
        @Override
        public void onClick(View view) {

            slideshowEditorAdapter.remove((String) view.getTag());
        }
    };

    /**
     * Class for implementing the "ViewHolder pattern" for better ListView performance.
     */
    private static class ViewHolder {

        ImageView slideImageView; // Refers to ListView item's ImageView.
        Button deleteButton;      // Refers to ListView item's Button.
    }

    /**
     * ArrayAdapter displaying Slideshow images.
     */
    private class SlideshowEditorAdapter extends ArrayAdapter <String> {

        private List <String> items; // List of image URIs.
        private LayoutInflater inflater;

        public SlideshowEditorAdapter(Context context, List <String> items) {

            super(context, -1, items);

            this.items = items;
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder; // Holds a reference to current item's GUI.

            /*
             * If convertView is null, inflate GUI and create ViewHolder;
             * Otherwise, get existing ViewHolder.
             */
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.view_slideshow_editor_edit_item, null);

                // Setup ViewHolder for this ListView item.
                viewHolder = new ViewHolder();
                viewHolder.slideImageView = (ImageView) convertView.findViewById(R.id.slideshowImageView);
                viewHolder.deleteButton = (Button) convertView.findViewById(R.id.deleteButton);
                convertView.setTag(viewHolder);
            } else {

                // Get the ViewHolder from the convertView's tag.
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // Get and display a thumbnail Bitmap image.
            String item = items.get(position); // Get the current image.
            new LoadThumbnailTask().execute(viewHolder.slideImageView, Uri.parse(item));

            // Configure the "Delete" Button.
            viewHolder.deleteButton.setTag(item);
            viewHolder.deleteButton.setOnClickListener(deleteButtonListener);

            return convertView;
        }
    } // End class SlideshowEditorAdapter

    /**
     * Task to load thumbnails in a separate thread.
     */
    private class LoadThumbnailTask extends AsyncTask<Object, Object, Bitmap> {

        ImageView imageView; // Displays the thumbnail.

        // Load thumbnail: ImageView, MediaType and Uri as args.
        @Override
        protected Bitmap doInBackground(Object... params) {

            imageView = (ImageView) params[0];

            return SlideshowActivity.getThumbnail((MediaItem.MediaType) params[1], (Uri) params[2], getContentResolver(), new BitmapFactory.Options());
        }

        /**
         * Set thumbnail on ListView.
         */
        @Override
        protected void onPostExecute(Bitmap result) {

            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }
    } // End class LoadThumbnailTask
}
