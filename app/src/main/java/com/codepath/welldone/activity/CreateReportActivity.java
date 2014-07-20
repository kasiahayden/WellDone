package com.codepath.welldone.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

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
        Log.d("CreateReportActivity", "pumpObjectId passed in intents: " + pumpObjectId);
        pumpToBeReported = PumpPersister.getPumpByObjectIdSyncly(pumpObjectId);
        if (pumpToBeReported == null) {
            Toast.makeText(this, "No pump selected for creating report!", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("debug", "Working with pump: " + pumpToBeReported.getObjectId() + " "
                + pumpToBeReported.getName());
        getActionBar().setTitle("New Report for " + pumpToBeReported.getAddress());
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
            newImageBitmap = BitmapFactory.decodeFile(fixedPumpPhotoUri.getPath());
            Drawable newImageDrawable;
            try {
                InputStream inputStream = getContentResolver().openInputStream(fixedPumpPhotoUri);
                newImageDrawable = Drawable.createFromStream(inputStream, fixedPumpPhotoUri.toString());
                ivFixedPump.setBackground(newImageDrawable); //TODO possible resize here before setting so doesn't stretch
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else { // Result was a failure
            newImageBitmap = null;
            Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onReportSubmit(View v) {

        final String pumpStatusToBeReported = spPumpStatus.getSelectedItem().toString();
        final String reportNotes = etReportNotes.getText().toString();
        final String reportTitle = "Pump_" + pumpStatusToBeReported.toString() + "_"
                + DateFormat.getDateTimeInstance().format(new Date()); //TODO is this the report title format we want?

        // Set the new status and priority on the pump
        pumpToBeReported.setCurrentStatus(pumpStatusToBeReported);
        pumpToBeReported.setPriority(Pump.getPriorityFromStatus(pumpStatusToBeReported));

        // Create a new report to be pinned locally and persisted remotely
        final Report reportToBePersisted = new Report();
        reportToBePersisted.setReportDetails(pumpToBeReported,
                pumpStatusToBeReported,
                reportTitle,
                reportNotes);

        // Pin the report locally
        pinReportLocally(reportToBePersisted, newImageBitmap);
    }

    /* Private methods */
    private void setupViews() {

        ivFixedPump = (ImageView) findViewById(R.id.ivFixedPump);
        etReportNotes = (EditText) findViewById(R.id.etReportNotes);
        //etReportTitle = (EditText) findViewById(R.id.etReportTitle);
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

    // Persist a given report locally and check if it was pinned
    private void pinReportLocally(final Report report, final Bitmap newImageBitmap) {

        final String pumpName = report.getPump().getName();
        Log.d("debug", "Pinning report for pump: " + pumpName);
        report.pinInBackground(ReportPersister.ALL_REPORTS, new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {

                    //checkIfReportPersistedLocally(report);
                    Log.d("debug", "Report pinned successfully: " + pumpName);
                    Toast.makeText(getApplicationContext(),
                            "Report cached successfully!",
                            Toast.LENGTH_SHORT).show();

                    // Try the persist the report remotely
                    persistReportRemotely(report, newImageBitmap);

                    // Go back to pump list browser
                    startActivity(new Intent(getApplicationContext(), PumpBrowser.class));

                } else {
                    // We should never get here! But just in case we do, show a toast
                    Log.d("debug", "Report could not be pinned successfully: " + pumpName);
                    Toast.makeText(getApplicationContext(),
                            "Report could not be cached! Please try again.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Persist a report remotely.
    // If a new photo was taken, save it in background first and then persist the pump.
    // Else, persist only the pump.
    private void persistReportRemotely(final Report report, final Bitmap newImageBitmap) {

        final String pumpName = report.getPump().getName();
        Log.d("debug", "Persisting report for pump: " + pumpName);

        if (newImageBitmap == null) {
            Log.d("debug", "Persisting report without image");
            persistReport(report);

        } else {
            Log.d("debug", "Persisting report with image");
            final byte[] newImageByteArray =
                    ImageUtil.getByteArrayFromBitmap(newImageBitmap, COMPRESSION_FACTOR);
            final ParseFile imageForParse = new ParseFile("image.jpeg", newImageByteArray);
            imageForParse.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("debug", "Image saved for report for pump: "
                                + report.getPump().getName());
                        report.setPhoto(imageForParse);
                        persistReport(report);
                    } else {
                        Log.e("debug", "Couldn't persist image for pump " + pumpName
                                + ", exception: " + e);
                    }
                }
            });
        }
    }

    // IMPORTANT: To save the underlying pump linked to the report, the pump has to be fetched
    // first, then updated. It won't be updated automatically.
    private void persistReport(final Report report) {

        report.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {
                    final ParseQuery pumpQuery = ParseQuery.getQuery("Pump");
                    Log.d("debug", "getting pump with ID: " + report.getPump().getObjectId());
                    //pumpQuery.whereEqualTo("objectId", report.getPump().getObjectId());
                    try {
                        final Pump fetchedPump = (Pump) pumpQuery.get(report.getPump().getObjectId());
                        Log.d("debug", "Fetched pump name: " + fetchedPump.getName());
                        Log.d("debug", "Fetched pump status: " + fetchedPump.getCurrentStatus());
                        Log.d("debug", "Fetched pump priority: " + fetchedPump.getPriority());

                        Log.d("debug", "Pump priority in report: " + pumpToBeReported.getPriority());
                        Log.d("debug", "Pump status in report: " + pumpToBeReported.getCurrentStatus());
                        fetchedPump.setPriority(pumpToBeReported.getPriority());
                        fetchedPump.setCurrentStatus(pumpToBeReported.getCurrentStatus());
                        Log.d("debug", "Saving fetched pump: " + fetchedPump.getName());
                        fetchedPump.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(getApplicationContext(),
                                            "Report successfully uploaded to server.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Log.e("error", "Couldn't persist report for pump "
                                            + report.getPump().getName() + ", exception: " + e);
                                }
                            }
                        });

                    } catch (ParseException pe) {
                        Log.e("error", "Couldn't persist report for pump "
                                + report.getPump().getName() + ", exception: " + pe);
                    }

                    // THIS WORKS! It creates new pump with the new status and priority.
                    /*Pump pump = new Pump();
                    pump.setPriority(report.getPump().getPriority());
                    pump.setCurrentStatus(report.getPump().getCurrentStatus());
                    pump.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Toast.makeText(getApplicationContext(),
                                        "Report successfully uploaded to server.",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("CreateReportActivity", "report.saveInBackground failed: " + e.toString());
                            }
                        }
                    });*/

                } else {
                    Log.e("error", "Couldn't persist report for pump " + report.getPump().getName()
                            + ", exception: " + e);
                }
            }
        });
    }

    // This is just a test method to save a report AFTER saving the corresponding pump first.
    // This does not update the pump either.
    private void persistReport2(final Report report) {

        final ParseQuery pumpQuery = ParseQuery.getQuery("Pump");
        pumpQuery.getInBackground(report.getPump().getObjectId(), new GetCallback() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {

                if (e == null) {
                    final Pump fetchedPump = (Pump) parseObject;
                    Log.d("debug", "Fetched pump name: " + fetchedPump.getName());
                    Log.d("debug", "Fetched pump status: " + fetchedPump.getCurrentStatus());
                    Log.d("debug", "Fetched pump priority: " + fetchedPump.getPriority());

                    Log.d("debug", "Pump priority in report: " + pumpToBeReported.getPriority());
                    Log.d("debug", "Pump status in report: " + pumpToBeReported.getCurrentStatus());
                    fetchedPump.setPriority(report.getPump().getPriority());
                    fetchedPump.setCurrentStatus(report.getPump().getCurrentStatus());
                    Log.d("debug", "Saving fetched pump: " + fetchedPump.getName());
                    fetchedPump.saveEventually(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            report.saveEventually(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e == null) {
                                        Toast.makeText(getApplicationContext(),
                                                "Report successfully uploaded to server.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Log.e("error", "Couldn't persist report for pump "
                                                + report.getPump().getName() + ", exception: " + e);
                                    }
                                }
                            });
                        }
                    });
                } else {
                    Log.e("error", "Couldn't persist pump "
                            + report.getPump().getName() + ", exception: " + e);
                }
            }
        });

    }

    // Check if a report was persisted locally
    // Added just for debugging.
    private void checkIfReportPersistedLocally(Report report) {

        final ParseQuery query = ParseQuery.getQuery("Pump");
        final Pump updatedPump = report.getPump();
        query.whereEqualTo("objectId", updatedPump.getObjectId());
        query.fromPin(PumpPersister.ALL_PUMPS);

        query.findInBackground(new FindCallback<ParseObject>() {

            public void done(final List<ParseObject> pumpList, ParseException e) {

                if (e == null) {

                    Log.d("debug", "Checking if updated pump was pinned locally " + updatedPump.getName());
                    Log.d("debug", "Query result size " + pumpList.size());
                    final Pump locallyFetchedPump = (Pump) pumpList.iterator().next();
                    Log.d("debug", "Locally queried pump name: " + locallyFetchedPump.getName());
                    Log.d("debug", "Locally queried pump status: " + locallyFetchedPump.getCurrentStatus());

                } else {
                    Log.d("debug", "Pump was not pinned locally " + updatedPump.getName());
                }
            }
        });
    }
}
