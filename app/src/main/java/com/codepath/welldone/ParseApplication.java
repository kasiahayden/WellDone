package com.codepath.welldone;

import android.app.Application;
import android.util.Log;

import com.codepath.welldone.activity.CreateReportActivity;
import com.codepath.welldone.activity.DemoPushActivity;
import com.codepath.welldone.model.Pump;
import com.codepath.welldone.model.Report;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

import java.util.List;

public class ParseApplication extends Application {

	@Override
	public void onCreate() {

		super.onCreate();

        //{"*":{"read":true},"role:Engineer":{"write":true,"read":true}}
        //{"ZJ3bkjPxxj":{"read":true,"write":true}}

        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Pump.class);
        ParseObject.registerSubclass(Report.class);

        Parse.initialize(this, "zs3GmrOhOzJCIyPE9Nu8k35FOfscjofe1NAa7HPP", "RBNwokWUIVKwv9dh8jtZmk90EKvYOiNRNlK2bXNP");

        PushService.setDefaultPushCallback(this, CreateReportActivity.class);

        //THIS IS ONE-TIME ONLY. SET A ROLE, ADD EXISTING USERS AND DATA TO IT
        /*final ParseACL roleACL = new ParseACL();
        roleACL.setPublicReadAccess(true);
        final ParseRole role = new ParseRole("Engineer", roleACL);

        final ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null) {
                    Log.d("debug", "Got users " + parseUsers.size());
                    for (final ParseUser user : parseUsers) {
                        role.getUsers().add(user);
                    }

                    role.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("debug", "Saved role with 6 users " + role.getName());
                            } else {
                                Log.d("debug", "Couldn't save role " + e.toString());
                            }
                        }
                    });
                } else {
                    Log.d("debug", "Couldn't get users " + e.toString());
                }
            }
        });*/


        /*final ParseQuery<ParseObject> pumpQuery = ParseQuery.getQuery("Pump");
        final ParseACL roleACL = new ParseACL();
        roleACL.setPublicReadAccess(true);
        roleACL.setRoleReadAccess("Engineer", true);
        roleACL.setRoleWriteAccess("Engineer", true);
        pumpQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                for (ParseObject obj : parseObjects) {
                    final Pump pump = (Pump) obj;
                    pump.setACL(roleACL);
                    pump.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("debug", "Saved acl to pump: " + pump.getAddress());
                            } else {
                                Log.d("debug", "Couldn't save acl to pump: " + pump.getAddress());
                            }
                        }
                    });
                }
            }
        });*/

        /*userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null) {
                    Log.d("debug", "Got users " + parseUsers.size());
                    for (final ParseUser user : parseUsers) {
                        user.setACL(postACL);
                        user.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("debug", "Saved acl to user: " + user.getUsername());
                                } else {
                                    Log.d("debug", "Couldn't save acl to user: " + user.getUsername() + " " + e.toString());
                                }
                            }
                        });
                    }
                }
            }
        });*/

    }

}

