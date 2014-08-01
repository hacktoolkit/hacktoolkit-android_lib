package com.hacktoolkit.android.utils;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.text.Html;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class HTKUtils {

	public static int getCurrentAPIVersion() {
		int currentAPIVersion = android.os.Build.VERSION.SDK_INT;
		return currentAPIVersion;
	}

	public static Intent getActivityIntent(String packageName, String className) {
		Intent intent = new Intent();
		intent.setClassName(packageName, className);
		return intent;
	}

	/**
	 * Wrapper for switching to another activity
	 * @param currentActivity
	 * @param packageName
	 * @param className
	 */
	public static void switchActivity(Activity currentActivity, String packageName, String className) {
		Intent intent = getActivityIntent(packageName, className);
		HTKUtils.switchActivity(currentActivity, intent);
	}
	
	public static void switchActivity(Activity currentActivity, Intent intent) {
		currentActivity.finish();
		currentActivity.startActivity(intent);
	}

	public static void sendSMS(String phoneNumber, String message) {
		SmsManager sms = SmsManager.getDefault();
		sms.sendTextMessage(phoneNumber, null, message, null, null);
	}

	/**
	 * Implicit Intents
	 * https://github.com/thecodepath/android_guides/wiki/Common-Implicit-Intents
	 */

	/**
	 *
	 * @param phoneNumber
	 *
	 * <uses-permission android:name="android.permission.CALL_PHONE" />
	 */
	public static void callPhone(Activity currentActivity, String phoneNumber) {
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		callIntent.setData(Uri.parse("tel:" + phoneNumber));
		switchActivity(currentActivity, callIntent);
	}

	public static void sendEmail(Activity currentActivity, String email, String subject, String message) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("plain/text");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "some@email.address" });
		intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
		intent.putExtra(Intent.EXTRA_TEXT, "mail body");
		Intent chooserIntent = Intent.createChooser(intent, "");
		switchActivity(currentActivity, chooserIntent);
	}

	public static void launchUrlInBrowser(Activity currentActivity, String url) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		switchActivity(currentActivity, browserIntent);
	}

	public static void openGooglePlay(Activity currentActivity) {
		Context context = (Context) currentActivity;
		Intent intent = new Intent(Intent.ACTION_VIEW, 
				  Uri.parse("market://details?id=" + context.getPackageName()));
		switchActivity(currentActivity, intent);
	}

	public static void composeSMS(Activity currentActivity, String phoneNumber, String message) {
		Uri smsUri = Uri.parse("tel:" + phoneNumber);
		Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
		intent.putExtra("address", phoneNumber);
		intent.putExtra("sms_body", message);
		intent.setType("vnd.android-dir/mms-sms");
		switchActivity(currentActivity, intent);
	}

	public static void showLocationInMaps(Activity currentActivity, String latitude, String longitude, Integer zoomLevel) {
		 Intent intent = new Intent();
		 intent.setAction(Intent.ACTION_VIEW);
		 String data = String.format("geo:%s,%s", latitude, longitude);
		 if (zoomLevel != null) {
		     data = String.format("%s?z=%s", data, zoomLevel);
		 }
		 intent.setData(Uri.parse(data));
		 switchActivity(currentActivity, intent);
	}

	public static void capturePhoto(Activity currentActivity, String filename) {
		Uri uri = Uri.fromFile(new File(filename));
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		switchActivity(currentActivity, intent);
	}

	public static void shareBinary(Activity currentActivity, String contentType, String filename, String shareMessage) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType(contentType);
		Uri uri = Uri.fromFile(new File(currentActivity.getFilesDir(), filename));
		shareIntent.putExtra(Intent.EXTRA_STREAM, uri.toString());
		Intent chooserIntent = Intent.createChooser(shareIntent, shareMessage);
		switchActivity(currentActivity, chooserIntent);
	}

	public static void shareHtml(Activity currentActivity, String content, String shareMessage) {
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("text/html");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(content));
		Intent chooserIntent = Intent.createChooser(sharingIntent, shareMessage);
		switchActivity(currentActivity, chooserIntent);
	}

	public static void showSoftKeyboard(Context context, View view) {
	    if (view.requestFocus()) {
	        InputMethodManager imm =(InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
	        imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
	    }
	}

	public static void hideSoftKeyboard(Context context, View view) {
		  InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		  imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

}
