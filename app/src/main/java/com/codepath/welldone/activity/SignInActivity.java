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
import com.codepath.welldone.helper.NetworkUtil;
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

    @Override
    public void onResume() {

        super.onResume();

        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Log.d("debug", "Current user is " + currentUser.toString());
            startActivity(new Intent(getApplicationContext(), PumpBrowser.class));
        } else {
            Log.d("debug", "No user logged in!");
        }
    }

    public void onSignIn(View v) {

        username = etUsername.getText().toString();
        password = etPassword.getText().toString();

        if (isInvalidInput()) {
            return;
        }

        pbLoading.setVisibility(ProgressBar.VISIBLE);
        // If no network is available, Toast the user and do nothing.
        if (!NetworkUtil.isNetworkAvailable(this)) {
            Toast.makeText(this,
                           "Not connected to network. This app needs Internet connection to start.",
                           Toast.LENGTH_LONG).show();
            pbLoading.setVisibility(ProgressBar.INVISIBLE);
            return;
        }

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
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
