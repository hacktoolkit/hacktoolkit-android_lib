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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.hacktoolkit.android.adapters.HTKContactsAdapter;
import com.hacktoolkit.android.fragments.ContactsFragment;
import com.hacktoolkit.android.models.HTKContact;

public class ContactsUtils {
	/**
	 * Wrapper for getContactsWithPhone to offload the work from the main UI activity thread and do it asynchronously
	 *
	 * @param currentActivity
	 * @param adapter the adapter to populate when contacts have been loaded
	 */
	public static void getContactsWithPhoneAsync(final Activity currentActivity, final HTKContactsAdapter adapter) {
		AsyncTask<Void, Void, ArrayList<HTKContact>> getContactsAsyncTask = new AsyncTask<Void, Void, ArrayList<HTKContact>>() {

			@Override
			protected ArrayList<HTKContact> doInBackground(Void... v) {
				if (currentActivity instanceof FragmentActivity) {
					FragmentManager fragmentManager = ((FragmentActivity) currentActivity).getSupportFragmentManager();
					ContactsFragment contactsFragment = (ContactsFragment) fragmentManager.findFragmentByTag("contacts");
					if (contactsFragment != null) {
						contactsFragment.startRetrieving();
					}
				}
				ArrayList<HTKContact> resultContacts = ContactsUtils.getContactsWithPhone(currentActivity);
				return resultContacts;
			}

			@Override
			protected void onPostExecute(ArrayList<HTKContact> resultContacts) {
				if (currentActivity != null) {
					if (currentActivity instanceof FragmentActivity) {
						FragmentManager fragmentManager = ((FragmentActivity) currentActivity).getSupportFragmentManager();
						ContactsFragment contactsFragment = (ContactsFragment) fragmentManager.findFragmentByTag("contacts");
						if (contactsFragment != null) {
							HTKContactsAdapter adapter = contactsFragment.getAdapter();
							if (adapter != null) {
								adapter.loadContacts(resultContacts);
							}
						}
					} else {
						adapter.loadContacts(resultContacts);
					}
				}
			}
		};
		getContactsAsyncTask.execute();
	}

	public static ArrayList<HTKContact> getContactsWithPhone(Activity currentActivity) {
		ContentResolver contentResolver = currentActivity.getContentResolver();

		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = {
				ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.Contacts.HAS_PHONE_NUMBER,
				ContactsContract.Contacts.STARRED,
				ContactsContract.Contacts.TIMES_CONTACTED,
				ContactsContract.Contacts.LAST_TIME_CONTACTED,
		};
		String selection = String.format("%s > 0", ContactsContract.Contacts.HAS_PHONE_NUMBER);
		String[] selectionArgs = null;
		String sortOrder = String.format(
				"%s DESC, %s DESC, %S DESC, UPPER(%s) ASC",
				ContactsContract.Contacts.STARRED,
				ContactsContract.Contacts.TIMES_CONTACTED,
				ContactsContract.Contacts.LAST_TIME_CONTACTED,
				ContactsContract.Contacts.DISPLAY_NAME
				);
		Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, sortOrder);
		
		ArrayList<HTKContact> contacts = new ArrayList<HTKContact>();
		
		if (cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				HTKContact contact = new HTKContact();
				String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
				String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String phone = "";
				String phoneType = "";
				if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
//					System.out.println("name : " + name + ", ID : " + id);

					String[] phoneData = getPhoneForContactId(contentResolver, contactId);
					phone = phoneData[0];
					phoneType = phoneData[1];

					contact.setData("id",  Integer.valueOf(contactId));
					contact.setData("name", name);
					contact.setData("phone", phone);
					contact.setData("phoneType", phoneType);
					contacts.add(contact);
				}
			}
			cursor.close();
		}
        return contacts;
	}

	public static String[] getPhoneForContactId(ContentResolver contentResolver, String contactId) {
		String[] phoneData = null;
		Cursor phoneCursor = contentResolver.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
				new String[] { contactId },
				null
				);

		String phone = "";
		String phoneType = "";
		while (phoneCursor.moveToNext()) {
			// grab the first phone number
			phone = phoneCursor.getString(
					phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
			phoneType = getPhoneType(phoneCursor);
			break;
		}
		phoneCursor.close();

		phoneData = new String[] { phone, phoneType };
		return phoneData;
	}

	public static String getPhoneType(Cursor phoneCursor) {
		String phoneType = "";
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
		return phoneType;
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
