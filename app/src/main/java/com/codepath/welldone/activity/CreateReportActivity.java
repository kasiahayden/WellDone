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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.codepath.welldone.R;
import com.codepath.welldone.helper.DateTimeUtil;
import com.codepath.welldone.helper.ImageUtil;
import com.codepath.welldone.helper.StringUtil;
import com.codepath.welldone.model.Pump;
import com.codepath.welldone.model.Report;
import com.codepath.welldone.persister.PumpPersister;
import com.codepath.welldone.persister.ReportPersister;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

import java.io.File;
import java.io.IOException;

/**
 * Class to handle creation and persistence of a report.
 */
public class CreateReportActivity extends Activity {

    public static final String APP_TAG = "WellDone";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    // Reduce the image quality by 50% to reduce performance overhead in saving/retrieving
    public static final int COMPRESSION_FACTOR = 50;
    public static final String PHOTO_FILE_EXTENSION = ".jpg";

    private EditText etReportNotes;
    private EditText etReportTitle;
    private ImageView ivFixedPump;
    private Spinner spPumpStatus;

    private Pump pumpToBeReported;
    private String fixedPumpPhotoFileName;
    private Bitmap newImageBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);

        final String pumpObjectId = getIntent().getStringExtra("pumpObjectId");
        pumpToBeReported = PumpPersister.getPumpByObjectIdSyncly(pumpObjectId);
        try {
            Log.d("debug", "Working with pump: " + pumpToBeReported.getObjectId() + " " + pumpToBeReported.getName());
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        setupViews();
        setupListeners();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // On successful return of camera activity, show the taken image
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            final Uri fixedPumpPhotoUri = Uri.fromFile(new File(fixedPumpPhotoFileName));
            // by this point we have the camera photo on disk
            final Bitmap takenImage = BitmapFactory.decodeFile(fixedPumpPhotoUri.getPath());
            // Load the taken image into a preview
            ivFixedPump.setImageBitmap(takenImage);
            newImageBitmap = takenImage;
        } else { // Result was a failure
            newImageBitmap = null;
            Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onReportSubmit(View v) {

        final String pumpStatusToBeReported = spPumpStatus.getSelectedItem().toString();
        final String reportNotes = etReportNotes.getText().toString();
        final String reportTitle = etReportTitle.getText().toString();
        final Report reportToBePersisted = new Report(pumpToBeReported, pumpStatusToBeReported,
                reportTitle, reportNotes);

        // If no new image was taken, submit report without one.
        if (newImageBitmap == null) {
            persistReport(reportToBePersisted);
            return;
        }

        // Save image in background and persist report when done.
        final byte[] newImageByteArray =
                ImageUtil.getByteArrayFromBitmap(newImageBitmap, COMPRESSION_FACTOR);
        final ParseFile imageForParse = new ParseFile("image.jpeg", newImageByteArray);
        imageForParse.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    reportToBePersisted.setPhoto(imageForParse);
                    persistReport(reportToBePersisted);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Error submitting report!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /* Private methods */
    private void setupViews() {

        ivFixedPump = (ImageView) findViewById(R.id.ivFixedPump);
        //ivFixedPump.setImageResource(android.R.color.transparent);
        etReportNotes = (EditText) findViewById(R.id.etReportNotes);
        etReportTitle = (EditText) findViewById(R.id.etReportTitle);
        spPumpStatus = (Spinner) findViewById(R.id.spPumpStatus);
    }

    private void setupListeners() {

        ivFixedPump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startTakePictureIntent();
            }
        });
    }

    // Get the newly created file name and start the camera activity
    private void startTakePictureIntent() {

        final Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
                Log.d("debug", "Created photo: " + photoFile.toString());
            } catch (IOException ex) {
                Toast.makeText(this, "Couldn't take picture!", Toast.LENGTH_LONG).show();
            }

            if (photoFile != null) {
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePhotoIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }
    }

    // Create a file name that the taken image will be saved under
    private File createImageFile() throws IOException {

        // Create an image file name
        final String timeStamp = DateTimeUtil.getFriendlyTimeStamp();
        final String imageFileName =
                StringUtil.getConcatenatedString(APP_TAG, "_", pumpToBeReported.getName(), "_",
                        timeStamp);
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

    private void persistReport(Report report) {

        Log.d("debug", "Saving report with title: " + report.getTitle());
        final Pump updatedPump =
                PumpPersister.getPumpByObjectIdSyncly(report.getPump().getObjectId());
        updatedPump.setCurrentStatus(report.getReportedStatus());
        Log.d("debug", "Updated current status of pump: " + updatedPump.getName() + " " + pumpToBeReported.getCurrentStatus());

        // Pin this report and pin the updated pump
        // saveEventually only pins until object is saved to remote store. So, pin first.
        report.pinInBackground(ReportPersister.ALL_REPORTS, new SaveCallback() {
            @Override
            public void done(ParseException e) {

                updatedPump.pinInBackground(PumpPersister.ALL_PUMPS, new SaveCallback() {
                    @Override
                    public void done(ParseException e) {

                        if (e == null) {
                            Log.d("debug", "Pump pinned successfully: " + updatedPump.getObjectId()
                                    + " " + updatedPump.getName());
                            Toast.makeText(getApplicationContext(),
                                    "Report submitted successfully.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("debug", "Could not pin pump: " + updatedPump.getObjectId()
                                    + " " + updatedPump.getName() + e.toString());
                            Toast.makeText(getApplicationContext(),
                                    "Error submitting report! Please try again later.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        startActivity(new Intent(getApplicationContext(), PumpBrowser.class));
                    }
                });
                updatedPump.saveEventually();
            }
        });
        report.saveEventually();
    }
}
