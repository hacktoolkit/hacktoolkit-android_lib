package com.hacktoolkit.android.models;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import com.hacktoolkit.android.utils.BitmapUtils;
import com.hacktoolkit.android.utils.ContactsUtils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

public class HTKContact implements Parcelable {
	HashMap<String, Object> data;

	public static final Parcelable.Creator<HTKContact> CREATOR = new Parcelable.Creator<HTKContact>() {
		public HTKContact createFromParcel(Parcel in) {
			HTKContact contact = new HTKContact(in);
			return contact;
		}

		public HTKContact[] newArray(int size) {
			HTKContact[] arr = new HTKContact[size];
			return arr;
		}
	};

	public HTKContact() {
		this.data = new HashMap<String, Object>();
		this.setMetaData("selected", false);
	}

	public HTKContact(Parcel parcel) {
		this();
		String jsonString = parcel.readString();
		JSONObject json = null;
		try {
			json = new JSONObject(jsonString);
		} catch (JSONException jsone) {
			// unable to convert string to JSON
		}

		if (json != null) {
			Iterator<String> keysIter = json.keys();

			while (keysIter.hasNext()) {
				String key = keysIter.next();
				try {
					Object value = json.get(key);
					this.setData(key, value);
				} catch (JSONException jsone) {
					// unable to read this key
				}
			}
		}
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		for (String key : data.keySet()) {
			try {
				json.put(key, this.getData(key));
			} catch (JSONException jsone) {
				// unable to serialize this key
			}
		}
		return json;
	}

	public void setData(String key, Object value) {
		data.put(key, value);
	}

	public Object getData(String key) {
		Object value = data.get(key);
		return value;
	}

	private String getMetaKey(String key) {
		String metaKey = String.format("meta_%s", key);
		return metaKey;
	}

	public void setMetaData(String key, Object value) {
		data.put(getMetaKey(key), value);
	}

	public Object getMetaData(String key) {
		Object value = data.get(getMetaKey(key));
		return value;
	}

	public long getId() {
		long id = (Integer) this.getData("id");
		return id;
	}

	public Bitmap getAvatar(Activity activity) {
		Bitmap avatar = getAvatar(activity, true);
		return avatar;
	}

	public Bitmap getAvatar(Activity activity, boolean rounded) {
		InputStream photoInputStream = ContactsUtils.openPhoto(activity, this.getId());
		Bitmap avatar = BitmapFactory.decodeStream(photoInputStream);
		if (rounded) {
			Bitmap roundedAvatar = BitmapUtils.getRoundedShape(avatar);
			avatar = roundedAvatar;
		}
		return avatar;
	}

	public void setSelected(boolean selected) {
		setMetaData("selected", selected);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(this.toJSON().toString());
	}
}
