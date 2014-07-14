package com.codepath.welldone;

import android.app.Application;

import com.codepath.welldone.activity.DemoPushActivity;
import com.codepath.welldone.model.Pump;
import com.codepath.welldone.model.Report;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.PushService;

public class ParseApplication extends Application {

	@Override
	public void onCreate() {

		super.onCreate();

        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Pump.class);
        ParseObject.registerSubclass(Report.class);

        Parse.initialize(this, "zs3GmrOhOzJCIyPE9Nu8k35FOfscjofe1NAa7HPP", "RBNwokWUIVKwv9dh8jtZmk90EKvYOiNRNlK2bXNP");

        PushService.setDefaultPushCallback(this, DemoPushActivity.class); //TODO change to activity where saveInBackground is called
                                                                          // on current installation of Parse (then import)
	}

}
