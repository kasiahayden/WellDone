package com.codepath.welldone.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.codepath.welldone.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class SignInActivity extends Activity {

    private EditText etUsername;
    private EditText etPassword;
    private ProgressBar pbLoading;

    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        getActionBar().hide();
        setupViews();
    }

    /* Leaving some of these overrides here for now even though they don't have any code.
       I'm having to look into activity life cycles to do certain things reliably. */
    @Override
    public void onStart() {

        super.onStart();
        Log.d("debug", "onStart() called");
    }

    @Override
    public void onResume() {

        super.onResume();
        Log.d("debug", "onResume() called");

        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Log.d("debug", "Current user is " + currentUser.toString());
            startActivity(new Intent(getApplicationContext(), PumpBrowser.class));
        } else {
            Log.d("debug", "No user logged in!");
        }
    }

    @Override
    public void onPause() {

        super.onPause();
        Log.d("debug", "onPause() called");
    }

    @Override
    public void onStop() {

        super.onStop();
        Log.d("debug", "onStop() called");
    }

    @Override
    public void onRestart() {

        super.onRestart();
        Log.d("debug", "onStart() called");
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Log.d("debug", "onDestroy() called");
    }

    public void onSignIn(View v) {

        username = etUsername.getText().toString();
        password = etPassword.getText().toString();

        if (isInvalidInput()) {
            return;
        }

        pbLoading.setVisibility(ProgressBar.VISIBLE);
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    //Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
                    Log.d("Info", "Current Parse User: " + ParseUser.getCurrentUser().toString());
                    startActivity(new Intent(getApplicationContext(), PumpBrowser.class));
                } else {
                    pbLoading.setVisibility(ProgressBar.INVISIBLE);
                    if (e != null) {
                        Log.d("info", "Parse login failed: " + e.toString());
                        if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                            Toast.makeText(getApplicationContext(),
                                           "The username and password did not match.",
                                           Toast.LENGTH_SHORT).show();
                            etPassword.selectAll();
                            etPassword.requestFocus();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                           "Could not login. Please try again later.",
                                           Toast.LENGTH_LONG).show();
                        }
                    }
                } // else failed login
            } //done
        });
    }

    /* Private methods */
    private void setupViews() {

        etUsername = (EditText) findViewById(R.id.etUsername);
        etPassword = (EditText) findViewById(R.id.etPassword);
        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
    }

    private boolean isInvalidInput() {

        if (username.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty.", Toast.LENGTH_SHORT).show();
            return true;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return false;
    }
}
