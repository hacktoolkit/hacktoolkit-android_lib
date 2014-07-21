package com.hacktoolkit.android.api_helpers;

import java.util.Arrays;

import android.app.Activity;
import android.util.Log;

import com.hacktoolkit.android.user.HTKUser;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFacebookUtils.Permissions;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ParseHelper {
	public static boolean initialized = false;
	
	public static void init(Activity activity, String appId, String clientId) {
	    Parse.initialize(activity, appId, clientId);
	    ParseAnalytics.trackAppOpened(activity.getIntent());
	    ParseHelper.initialized = true; 
	}
	
	public static void fbInit(String fbAppId) {
	    ParseFacebookUtils.initialize(fbAppId);
	}
	
	public static void test() {
	    ParseObject testObject = new ParseObject("TestObject");
	    testObject.put("foo", "bar");
	    testObject.saveInBackground();
	}
	
	public static void facebookLogin(String fbAppId, Activity activity, final HTKCallback callback) {
		ParseHelper.fbInit(fbAppId);
	    ParseFacebookUtils.logIn(Arrays.asList("email", Permissions.Friends.ABOUT_ME),
	    		activity, new LogInCallback() {
	    	  @Override
	    	  public void done(ParseUser parseUser, ParseException err) {
	    	    if (parseUser == null) {
	    	      Log.d("Hacktoolkit", "Uh oh. The user cancelled the Facebook login.");
	    	    } else {
	    	    		HTKUser.getCurrentUser(parseUser).updateWithGraphUser();
	    	    		if (parseUser.isNew()) {
	    	    			Log.d("Hacktoolkit", "User signed up and logged in through Facebook!");
	    	    		} else {
	    	    			Log.d("Hacktoolkit", "User logged in through Facebook!");
	    	    		}
	    	    		callback.execute(null);
	    	    }
	    	  }
	    	});
	}
}
