package com.codepath.welldone;

import android.app.Application;
import android.util.Log;

import com.codepath.welldone.model.Pump;
import com.codepath.welldone.model.PumpStatus;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ParseApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Pump.class);
        ParseObject.registerSubclass(PumpStatus.class);

        // Add your initialization code here
        Parse.initialize(this, "zs3GmrOhOzJCIyPE9Nu8k35FOfscjofe1NAa7HPP", "RBNwokWUIVKwv9dh8jtZmk90EKvYOiNRNlK2bXNP");

		ParseUser.enableAutomaticUser();
        Log.d("DBG", ParseUser.getCurrentUser().toString());
	}

}
