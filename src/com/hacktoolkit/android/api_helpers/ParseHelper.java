package com.hacktoolkit.android.api_helpers;

import android.app.Activity;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;

public class ParseHelper {
	public static void init(Activity activity, String appId, String clientId) {
	    Parse.initialize(activity, appId, clientId);
	    ParseAnalytics.trackAppOpened(activity.getIntent());
	}
	
	public static void test() {
	    ParseObject testObject = new ParseObject("TestObject");
	    testObject.put("foo", "bar");
	    testObject.saveInBackground();
	}
}
