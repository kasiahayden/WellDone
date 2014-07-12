package com.codepath.welldone.activity;

import com.codepath.welldone.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
<<<<<<< HEAD

import com.codepath.welldone.R;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.PushService;
import com.parse.RefreshCallback;
import com.parse.SaveCallback;
=======
>>>>>>> 33d825fdebb36f9faf20c87609b355f3d2666d6c

public class DemoPushActivity extends Activity {

    private RadioButton genderFemaleButton;
    private RadioButton genderMaleButton;
    private EditText ageEditText;
    private RadioGroup genderRadioGroup;

    public static final String GENDER_MALE = "male";
    public static final String GENDER_FEMALE = "female";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_push);


        // Track app opens.
        ParseAnalytics.trackAppOpened(getIntent());

        // Set up our UI member properties.
        this.genderFemaleButton = (RadioButton) findViewById(R.id.gender_female_button);
        this.genderMaleButton = (RadioButton) findViewById(R.id.gender_male_button);
        this.ageEditText = (EditText) findViewById(R.id.age_edit_text);
        this.genderRadioGroup = (RadioGroup) findViewById(R.id.gender_radio_group);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.demo_push, menu);
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
    public void onStart() {
        super.onStart();

        // Display the current values for this user, such as their age and gender.
        displayUserProfile();
        refreshUserProfile();
    }

    // Save the user's profile to their installation.
    public void saveUserProfile(View view) {
        String ageTextString = ageEditText.getText().toString();

        if (ageTextString.length() > 0) {
            ParseInstallation.getCurrentInstallation().put("age", Integer.valueOf(ageTextString));
            //Toast.makeText(this, "age: " + ageTextString, Toast.LENGTH_SHORT).show();
        }

        if (genderRadioGroup.getCheckedRadioButtonId() == genderFemaleButton.getId()) {
            ParseInstallation.getCurrentInstallation().put("gender", GENDER_FEMALE);
            //Toast.makeText(this, "gender: female", Toast.LENGTH_SHORT).show();
        } else if (genderRadioGroup.getCheckedRadioButtonId() == genderMaleButton.getId()) {
            ParseInstallation.getCurrentInstallation().put("gender", GENDER_MALE);
            //Toast.makeText(this, "gender: male", Toast.LENGTH_SHORT).show();
        } else {
            ParseInstallation.getCurrentInstallation().remove("gender");
            //Toast.makeText(this, "gender: none", Toast.LENGTH_SHORT).show();
        }

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(ageEditText.getWindowToken(), 0);

        ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Saved!", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    e.printStackTrace();

                    Toast toast = Toast.makeText(getApplicationContext(), "Failed with error.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    // Refresh the UI with the data obtained from the current ParseInstallation object.
    private void displayUserProfile() {
        String gender = ParseInstallation.getCurrentInstallation().getString("gender");
        int age = ParseInstallation.getCurrentInstallation().getInt("age");

        if (gender != null) {
            genderMaleButton.setChecked(gender.equalsIgnoreCase(GENDER_MALE));
            genderFemaleButton.setChecked(gender.equalsIgnoreCase(GENDER_FEMALE));
        } else {
            genderMaleButton.setChecked(false);
            genderFemaleButton.setChecked(false);
        }
        if (age > 0) {
            ageEditText.setText(Integer.valueOf(age).toString());
        }

    }

    // Get the latest values from the ParseInstallation object.
    private void refreshUserProfile() {
        ParseInstallation.getCurrentInstallation().refreshInBackground(new RefreshCallback() {

            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    displayUserProfile();
                }
            }
        });
    }
}

