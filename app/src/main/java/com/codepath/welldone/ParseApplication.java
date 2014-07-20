package com.codepath.welldone;

import android.app.Application;

import com.codepath.welldone.activity.CreateReportActivity;
import com.codepath.welldone.model.Pump;
import com.codepath.welldone.model.Report;
import com.codepath.welldone.persister.PumpPersister;
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

        PushService.setDefaultPushCallback(this, CreateReportActivity.class);


        Pump pump = PumpPersister.getPumpByObjectIdSyncly("tYv6NzF9Sr");
        pump.setCurrentStatus("Blah!");
        try {
            pump.save();
        }
        catch (Exception e) {
        }
        int x = 0; x++;
	}

}

