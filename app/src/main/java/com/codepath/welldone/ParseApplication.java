package com.codepath.welldone;

import android.app.Application;
import android.util.Log;

import com.codepath.welldone.model.Pump;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ParseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Pump.class);

        Parse.initialize(this, "AFAHbFnBBMRWBl8A1BynRocSsRJfopRDC7pkPuEO", "azgmOiqrSKjiSaKzn0x9nODajWJEGH6A90t1Yfew");

		ParseUser.enableAutomaticUser();
        Log.d("DBG", ParseUser.getCurrentUser().toString());
	}

}
