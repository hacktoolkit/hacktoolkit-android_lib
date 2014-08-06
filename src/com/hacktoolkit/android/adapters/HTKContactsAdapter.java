package com.hacktoolkit.android.adapters;

import java.util.ArrayList;

import android.app.Activity;
import android.widget.ArrayAdapter;

import com.hacktoolkit.android.models.HTKContact;

public class HTKContactsAdapter extends ArrayAdapter<HTKContact> {
	private boolean loaded = false;
	protected final Activity context;
	protected final ArrayList<HTKContact> contacts;

	public HTKContactsAdapter(Activity context, int layoutId, ArrayList<HTKContact> contacts) {
		super(context, layoutId, contacts);
		this.context = context;
		this.contacts = contacts;
	}

	public void setItemSelected(int position, boolean isSelected) {
		HTKContact contact = this.getItem(position);
		contact.setSelected(isSelected);
	}

	public ArrayList<HTKContact> getSelectedContacts() {
		ArrayList<HTKContact> selectedContacts = new ArrayList<HTKContact>();
		for (int i=0; i < this.getCount(); ++i) {
			HTKContact contact = this.getItem(i);
			if ((Boolean) contact.getMetaData("selected")) {
				selectedContacts.add(contact);
			}
		}
		return selectedContacts;
	}

	public void loadContacts(ArrayList<HTKContact> contacts) {
		for (HTKContact contact : contacts) {
			this.add(contact);
		}
		this.loadComplete();		
	}

	public void loadComplete() {
		loaded = true;
	}

	public boolean isLoaded() {
		return loaded;
	}
}
