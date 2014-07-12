package com.codepath.welldone.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.codepath.welldone.R;
import com.codepath.welldone.helper.DateTimeUtil;
import com.codepath.welldone.helper.StringUtil;

import java.io.File;
import java.io.IOException;

public class CreateReportActivity extends Activity {

    public static final String APP_TAG = "WellDone";
    public static final String PHOTO_FILE_EXTENSION = ".jpg";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    
    private String fixedPumpPhotoFileName;

    private ImageView ivFixedPump;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);

        setupViews();
        setupListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_report, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            final Uri fixedPumpPhotoUri = Uri.fromFile(new File(fixedPumpPhotoFileName));
            // by this point we have the camera photo on disk
            final Bitmap takenImage = BitmapFactory.decodeFile(fixedPumpPhotoUri.getPath());
            // Load the taken image into a preview
            ivFixedPump.setImageBitmap(takenImage);
        } else { // Result was a failure
            Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
        }
    }


    /* Private methods */
    private void setupViews() {

        ivFixedPump = (ImageView) findViewById(R.id.ivFixedPump);
        //ivFixedPump.setImageResource(android.R.color.transparent);
    }

    private void setupListeners() {

        ivFixedPump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startTakePictureIntent();
            }
        });
    }

    private void startTakePictureIntent() {

        final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.d("debug", "Created photo: " + photoFile.toString());
            } catch (IOException ex) {
                Toast.makeText(this, "Couldn't take picture!", Toast.LENGTH_LONG).show();
            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }

    }

    private File createImageFile() throws IOException {

        // Create an image file name
        final String timeStamp = DateTimeUtil.getFriendlyTimeStamp();
        final String imageFileName = StringUtil.getConcatenatedString(APP_TAG, "_fixedPump_", timeStamp);
        final File storageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        // Create the storage directory if it does not exist
        if (!storageDir.exists() && !storageDir.mkdirs()){
            Log.d("debug", "Failed to create directory " + storageDir);
            throw new IOException("Couldn't access external storage!");
        }

        final File image = File.createTempFile(
                imageFileName,  /* prefix */
                PHOTO_FILE_EXTENSION,         /* suffix */
                storageDir      /* directory */
        );
        Log.d("debug", "Saved image to file: " + image.toString());

        // Save a file: path for use with ACTION_VIEW intents
        fixedPumpPhotoFileName = image.getAbsolutePath();
        Log.d("debug", "fixedPumpPhotoFileName: " + fixedPumpPhotoFileName);
        return image;
    }
}
