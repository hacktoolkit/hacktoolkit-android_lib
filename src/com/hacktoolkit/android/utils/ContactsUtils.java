package com.hacktoolkit.android.utils;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.hacktoolkit.android.models.HTKContact;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;

public class ContactsUtils {
	public static ArrayList<JSONObject> getContactsWithPhone(Activity currentActivity) {
		ContentResolver cr = currentActivity.getContentResolver();
		Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,  null,  null, null, null);
		
		ArrayList<JSONObject> contacts = new ArrayList<JSONObject>();
		
		if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
            		HTKContact contact2 = new HTKContact();
            		JSONObject contact = new JSONObject();
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String phone = "";
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    System.out.println("name : " + name + ", ID : " + id);

                    // get the phone number
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                           ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                           new String[]{id}, null);
                    while (pCur.moveToNext()) {
                          phone = pCur.getString(
                                 pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                          System.out.println("phone" + phone);
                          break;
                    }
                    pCur.close();
                    try {
                        contact.put("name", name);
                        contact.put("phone", phone);
                        contacts.add(contact);
                    } catch (JSONException jsone) {
                    	   // oh well
                    }
                
                }
            }
		}
        return contacts;
	}
}
