package com.hacktoolkit.android.utils;

import android.app.Activity;
import android.content.Intent;

public class HTKUtils {
	
	/**
	 * Wrapper for switching to another activity
	 * @param currentActivity
	 * @param packageName
	 * @param className
	 */
	public static void switchActivity(Activity currentActivity, String packageName, String className) {
		Intent intent = new Intent();
		intent.setClassName(packageName, className);
		HTKUtils.switchActivity(currentActivity, intent);
	}
	
	public static void switchActivity(Activity currentActivity, Intent intent) {
		currentActivity.finish();
		currentActivity.startActivity(intent);
	}
}
