package com.hacktoolkit.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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
	
	public static void showSoftKeyboard(Context context, View view){
	    if (view.requestFocus()) {
	        InputMethodManager imm =(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
	    }
	}
	
	public static void hideSoftKeyboard(Context context, View view){
		  InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		  imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
}
