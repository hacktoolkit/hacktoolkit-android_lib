package com.hacktoolkit.android.fragments;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.hacktoolkit.android.adapters.HTKContactsAdapter;
import com.hacktoolkit.android.models.HTKContact;

public class ContactsFragment extends Fragment {
	private 	ArrayList<HTKContact> contacts;
	private HTKContactsAdapter adapter;
	private boolean started;

	public ContactsFragment() {
		this.contacts = new ArrayList<HTKContact>();
		this.started = false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	public void setAdapter(HTKContactsAdapter adapter) {
		this.adapter = adapter;
	}

	public HTKContactsAdapter getAdapter() {
		return this.adapter;
	}

	public void setContacts(ArrayList<HTKContact> contacts) {
		this.contacts = contacts;
	}

	public ArrayList<HTKContact> getContacts() {
		return this.contacts;
	}

	public void startRetrieving() {
		this.started = true;
	}

	public boolean hasStarted() {
		return this.started;
	}
}
