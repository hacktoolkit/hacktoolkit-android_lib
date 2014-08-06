package com.hacktoolkit.android.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

import com.hacktoolkit.android.adapters.HTKContactsAdapter;
import com.hacktoolkit.android.models.HTKContact;

public class ContactsUtils {
	/**
	 * Wrapper for getContactsWithPhone to offload the work from the main UI activity thread and do it asynchronously
	 *
	 * @param currentActivity
	 * @param adapter the adapter to populate when contacts have been loaded
	 */
	public static void getContactsWithPhoneAsync(final Activity currentActivity, final HTKContactsAdapter adapter) {
		new AsyncTask<Void, Void, ArrayList<HTKContact>>() {

			@Override
			protected ArrayList<HTKContact> doInBackground(Void... v) {
				ArrayList<HTKContact> resultContacts = ContactsUtils.getContactsWithPhone(currentActivity);
				return resultContacts;
			}

			@Override
			protected void onPostExecute(ArrayList<HTKContact> resultContacts) {
				adapter.loadContacts(resultContacts);
			}
		}.execute();
	}

	public static ArrayList<HTKContact> getContactsWithPhone(Activity currentActivity) {
		ContentResolver contentResolver = currentActivity.getContentResolver();
		Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		
		ArrayList<HTKContact> contacts = new ArrayList<HTKContact>();
		
		if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
            		HTKContact contact = new HTKContact();
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String phone = "";
                String phoneType = "";
                if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    System.out.println("name : " + name + ", ID : " + id);

                    // get the phone number
                    Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                           ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                           new String[]{ id }, null);
                    while (phoneCursor.moveToNext()) {
                    	    // grab the first phone number
                    		phone = phoneCursor.getString(
                    				phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    		int type = Integer.parseInt(phoneCursor.getString(
                    				phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE)));
                    		if (type == ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM) {
                    			phoneType = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL));
                    		} else {
                    			switch(type) {
                    			case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                    				phoneType = "Home";
                    				break;
                    			case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                    				phoneType = "Mobile";
                    				break;
                    			case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                    				phoneType = "Work";
                    				break;
                    			case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                    				phoneType = "Work Fax";
                    				break;
                    			case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                    				phoneType = "Home Fax";
                    				break;
                    			case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                    				phoneType = "Pager";
                    				break;
                    			case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                    				phoneType = "Other";
                    				break;
                    			case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK:
                    				phoneType = "Callback";
                    				break;
                    				// other types:
                    				//http://developer.android.com/reference/android/provider/ContactsContract.CommonDataKinds.Phone.html
                    			default:	
                    				break;
                    			}
                    		}
//                    		System.out.println("phone" + phone);
                    		break;
                    }
                    phoneCursor.close();
                    contact.setData("id",  Integer.valueOf(id));
                    contact.setData("name", name);
                    contact.setData("phone", phone);
                    contact.setData("phoneType", phoneType);
                    contacts.add(contact);
//                    if (contacts.size() > 20) {
//                    		break;
//                    }
                }
            }
            cursor.close();
		}
        return contacts;
	}
	
	public static InputStream openPhoto(Activity currentActivity, long contactId) {
		if (HTKUtils.getCurrentAPIVersion() < android.os.Build.VERSION_CODES.HONEYCOMB) {
			return null;
		}
		Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
		Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);
		Cursor cursor = currentActivity.getContentResolver().query(photoUri,
				new String[] { Contacts.Photo.PHOTO}, null, null, null);
		if (cursor == null) {
			return null;
		}
		try {
			if (cursor.moveToFirst()) {
				byte[] data = cursor.getBlob(0);
				if (data != null) {
					return new ByteArrayInputStream(data);
				}
			}
		} finally {
			cursor.close();
		}
		return null;
	}

	public static InputStream openDisplayPhoto(Activity currentActivity, long contactId) {
		if (HTKUtils.getCurrentAPIVersion() < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			return null;
		}
		Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
		Uri displayPhotoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.DISPLAY_PHOTO);
		try {
			AssetFileDescriptor fd =
					currentActivity.getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
			return fd.createInputStream();
		} catch (IOException e) {
			return null;
		}
	}
	 
}
