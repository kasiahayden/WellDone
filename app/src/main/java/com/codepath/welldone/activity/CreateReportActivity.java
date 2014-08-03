package com.codepath.welldone.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.welldone.R;
import com.codepath.welldone.helper.AddressUtil;
import com.codepath.welldone.helper.DateTimeUtil;
import com.codepath.welldone.helper.ImageUtil;
import com.codepath.welldone.helper.NetworkUtil;
import com.codepath.welldone.helper.StringUtil;
import com.codepath.welldone.model.Pump;
import com.codepath.welldone.model.Report;
import com.codepath.welldone.persister.PumpPersister;
import com.codepath.welldone.persister.ReportPersister;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Date;

/**
 * Class to handle creation and persistence of a report.
 */
public class CreateReportActivity extends Activity {

    public static final String APP_TAG = "WellDone";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    // Reduce the image quality by 50% to reduce performance overhead in saving/retrieving
    public static final int COMPRESSION_FACTOR = 50;
    public static final String PHOTO_FILE_EXTENSION = ".jpg";
    public static final String EXTRA_PUMP_OBJECT_ID = "pumpObjectId";
    public static final int CREATE_REPORT_SUCCESSFUL_OR_NOT_REQUEST_CODE = 1234;
    public static final int CREATE_REPORT_SUCCESSFUL_OR_NOT_REQUEST_CODE_SUCCESS = RESULT_FIRST_USER;

    ViewPager mUpdateStatusPager;
    private Pump pumpToBeReported;
    private Pump pumpToNavigateToAfterReporting;

    private int mCurrentStatusIndex;

    private ImageButton fabSubmitReport;



    private TextView mPumpHandleSelector;
    private ImageView mPumpHandleCheck;

    private TextView mCloggedPipeSelector;
    private ImageView mCloggedPipeCheck;

    private TextView mBrokenPipeSelector;
    private ImageView mBrokenPipeCheck;


    private TextView mPumpNameLabel;


    private EditText mAdditionalNotesField;

    private ProgressBar pbLoading;


    private TextView mBrokenPagerSubview;
    private TextView mInProgressPagerSubview;
    private TextView mOperationalPagerSubview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_report);
        mUpdateStatusPager = (ViewPager)findViewById(R.id.vpUpdateStatus);
        mUpdateStatusPager.setAdapter(getPagerAdapter());
        mUpdateStatusPager.setPageMargin(-200);
        mUpdateStatusPager.setOnPageChangeListener(getOnPageChangeListener());
        mUpdateStatusPager.setOffscreenPageLimit(2);
        pbLoading = (ProgressBar)findViewById(R.id.pbLoading);
        populateRepairTypeFields();
        mAdditionalNotesField = (EditText)findViewById(R.id.etNotesField);
        setupSubmitReportButton();

        mPumpNameLabel = (TextView)findViewById(R.id.tvPumpNameTopLabelCreateReport);

        getDataFromIntent();
    }

    private void populateRepairTypeFields() {
        mPumpHandleSelector = (TextView)findViewById(R.id.tvPumpHandleSelector);
        mPumpHandleCheck = (ImageView)findViewById(R.id.ivCheckPumpHandle);

        mCloggedPipeCheck = (ImageView)findViewById(R.id.ivCheckCloggedPipe);
        mCloggedPipeSelector = (TextView)findViewById(R.id.tvCloggedPipeSelector);

        mBrokenPipeCheck = (ImageView)findViewById(R.id.ivCheckBrokenPipe);
        mBrokenPipeSelector = (TextView)findViewById(R.id.tvBrokenPipeSelector);
    }

    private void setupSubmitReportButton() {
        fabSubmitReport = (ImageButton)findViewById(R.id.fabSubmitReport);

        int size = getResources().getDimensionPixelSize(R.dimen.fab_size);
        Outline outline = new Outline();
        outline.setOval(0, 0, size, size);
        fabSubmitReport.setOutline(outline);
        fabSubmitReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmitReport();
            }
        });
    }

    public void onRepairTypeSelectorClicked(View v) {
        TextView clickedView = (TextView)v;
        if (clickedView == mBrokenPipeSelector) {
            mBrokenPipeCheck.setVisibility(View.VISIBLE);
            disableChecksBeside(mBrokenPipeCheck);
        }
        else if (clickedView == mCloggedPipeSelector) {
            mCloggedPipeCheck.setVisibility(View.VISIBLE);
            disableChecksBeside(mCloggedPipeCheck);
        }
        else {
            mPumpHandleCheck.setVisibility(View.VISIBLE);
            disableChecksBeside(mPumpHandleCheck);
        }
    }

    private void disableChecksBeside(ImageView someCheck) {
        ImageView[] imageViews = {mPumpHandleCheck, mCloggedPipeCheck, mBrokenPipeCheck};
        for (ImageView iv : imageViews) {
            if (iv != someCheck) {

                iv.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setFirstVisibleChecksToGreen() {
        ImageView[] imageViews = {mPumpHandleCheck, mCloggedPipeCheck, mBrokenPipeCheck};
        for (ImageView iv : imageViews) {
            if (iv.getVisibility() == View.VISIBLE) {
                iv.setImageResource(R.drawable.ic_report_check_green);
            }
        }
    }


    private int getPagerStatusValueForStatusString(String statusString) {
        if (statusString.equalsIgnoreCase("Broken")) {
            return 0;
        }
        else if (statusString.equalsIgnoreCase("Fix in progress")) {
            return 1;
        }
        else {
            return 2;
        }
    }

    private String getCurrentPumpStatusString() {
        switch (mCurrentStatusIndex) {
            case 0:
                return "Broken";
            case 1:
                return "Fix in progress";
            case 2:
                return "Operational";
        }
        return null;
    }

    private ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                mCurrentStatusIndex = i;
                if (i == 0) {
                    resetTextColorOnPagerSubviews();
                    mBrokenPagerSubview.setTextColor(getResources().getColor(R.color.createDarkGrayText));
                }
                if (i == 1) {
                    resetTextColorOnPagerSubviews();
                    mInProgressPagerSubview.setTextColor(getResources().getColor(R.color.createDarkGrayText));
                }
                else if (i == 2) {
                    resetTextColorOnPagerSubviews();
                    mOperationalPagerSubview.setTextColor(getResources().getColor(R.color.createDarkGrayText));
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        };
    }

    private void resetTextColorOnPagerSubviews() {
        int disabledColor = getResources().getColor(R.color.createLightGrayText);
        if (mBrokenPagerSubview != null) {
            mBrokenPagerSubview.setTextColor(disabledColor);
        }
        if (mInProgressPagerSubview != null) {
            mInProgressPagerSubview.setTextColor(disabledColor);
        }
        if (mOperationalPagerSubview != null) {
            mOperationalPagerSubview.setTextColor(disabledColor);
        }
    }

    private PagerAdapter getPagerAdapter() {
        return new PagerAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            /*
            A very simple PagerAdapter may choose to use the page Views themselves as key objects,
            returning them from instantiateItem(ViewGroup, int) after creation and adding them to
            the parent ViewGroup. A matching destroyItem(ViewGroup, int, Object) implementation
            would remove the View from the parent ViewGroup and isViewFromObject(View, Object)
            could be implemented as return view == object;.
             */
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                final TextView tv = new TextView(CreateReportActivity.this);
                switch (position) {
                    case 0:
                        mBrokenPagerSubview = tv;
                        tv.setText("BROKEN");
                        break;
                    case 1:
                        mInProgressPagerSubview = tv;
                        tv.setText("IN PROGRESS");
                        break;
                    case 2:
                        mOperationalPagerSubview = tv;
                        tv.setText("FUNCTIONAL");
                        break;
                }
                tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32);
                tv.setTextColor(getResources().getColor(R.color.createDarkGrayText));
                container.addView(tv);
                return tv;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                Log.d("DBG", String.format("Deleting at index %d", position));
                container.removeView((View) object);
            }

        };
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void onSubmitReport() {
        setFirstVisibleChecksToGreen();
        final String pumpStatusToBeReported = getCurrentPumpStatusString();
        final String reportNotes = mAdditionalNotesField.getText().toString();
        final String reportTitle = StringUtil.getConcatenatedString("Pump_",
                AddressUtil.getConcatenatedCityFromAddress(pumpToBeReported.getAddress()), "_",
                DateTimeUtil.getLocalDateTimeForFileName(new Date()), ".html");

        // Set the new status and priority on the pump
        pumpToBeReported.setCurrentStatus(pumpStatusToBeReported);
        pumpToBeReported.setPriority(Pump.getPriorityFromStatus(pumpStatusToBeReported));

        // Create a new report to be pinned locally and persisted remotely
        final Report reportToBePersisted = new Report();
        reportToBePersisted.setReportDetails(pumpToBeReported,
                ParseUser.getCurrentUser(),
                pumpStatusToBeReported,
                reportTitle,
                reportNotes);

        pinReportLocally(reportToBePersisted, null);

        pbLoading.setVisibility(ProgressBar.VISIBLE);

        disableInterfaceElements();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /// TODO Put this in a more sensible place
                Intent result = new Intent();
                result.putExtra(PumpBrowser.EXTRA_PUMP_OBJECT_ID, pumpToBeReported.getObjectId());
                setResult(CREATE_REPORT_SUCCESSFUL_OR_NOT_REQUEST_CODE_SUCCESS, result);
                finishActivity(CREATE_REPORT_SUCCESSFUL_OR_NOT_REQUEST_CODE);
            }
        }, 3000);

    }

    private void disableInterfaceElements() {
        fabSubmitReport.setImageResource(R.drawable.ic_sendreport_disabled);
        fabSubmitReport.setClickable(false);

        mPumpHandleSelector.setClickable(false);
        mCloggedPipeSelector.setClickable(false);
        mBrokenPipeCheck.setClickable(false);

        mAdditionalNotesField.setEnabled(false);

        mUpdateStatusPager.setEnabled(false);

    }

    private void getDataFromIntent() {

        final String pumpObjectId = getIntent().getStringExtra(EXTRA_PUMP_OBJECT_ID);
        Log.d("CreateReportActivity", String.format("Retrieving pumpObjectId = %s", pumpObjectId));
        pumpToBeReported = PumpPersister.getPumpByObjectIdSyncly(pumpObjectId);
        if (pumpToBeReported == null) {
            Toast.makeText(this, "No pump selected for creating report!", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("DBG", String.format("Working with pump: %s %s", pumpToBeReported.getObjectId(), pumpToBeReported.getName()));

        updatePumpStatusSpinner(pumpToBeReported.getCurrentStatus());
        mPumpNameLabel.setText(String.format("%s > Add Report", pumpToBeReported.getAddress()));

    }

    private void updatePumpStatusSpinner(String newStatus) {
        mCurrentStatusIndex = getPagerStatusValueForStatusString(newStatus);
        /// Scroll to the right position
        mUpdateStatusPager.setCurrentItem(mCurrentStatusIndex, true);
    }

    // Persist a given report locally and check if it was pinned
    private void pinReportLocally(final Report report, final Bitmap newImageBitmap) {

        final String pumpName = report.getPump().getName();
        Log.d("debug", "Pinning report for pump: " + pumpName);

        // IMPORTANT: Set the ACL for the new report to that of the logged in user's role.
        // XXX: Ideally, the role name shouldn't be hard-coded. But will do here, shamelessly.
        // Also, turn on public readability for local datastore to wrk properly.
        final ParseACL reportACL = new ParseACL();
        reportACL.setPublicReadAccess(true);
        reportACL.setRoleReadAccess("Engineer", true);
        reportACL.setRoleWriteAccess("Engineer", true);
        report.setACL(reportACL);

        report.pinInBackground(ReportPersister.ALL_REPORTS, new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {

                    //checkIfReportPersistedLocally(report);
                    Log.d("debug", "Report pinned successfully: " + pumpName);
                    showToastBasedOnNetworkAvailability();

                    // Try the persist the report remotely
                    persistReportRemotely(report, newImageBitmap);

                    // Go back to pump list browser
                    pbLoading.setVisibility(ProgressBar.INVISIBLE);

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
            Log.d("debug", "Persisting report without image.");
            persistReport(report);

        } else {
            Log.d("debug", "Persisting report with image.");
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

    // IMPORTANT: The underlying pump will be saved automatically.
    private void persistReport(final Report report) {

        report.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if (e == null) {
                    Toast.makeText(getApplicationContext(),
                            "Report successfully uploaded to server.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("error", "Couldn't persist report for pump " + report.getPump().getName()
                            + ", exception: " + e);
                }
            }
        });
    }

    // This is primarily to distinguish between online and offline toast messages during demo.
    private void showToastBasedOnNetworkAvailability() {
        if (!NetworkUtil.isNetworkAvailable(this)) {
            Toast.makeText(getApplicationContext(),
                    "No network found. Report will be uploaded when Internet is available.",
                    Toast.LENGTH_LONG).show();
        }
    }
}
