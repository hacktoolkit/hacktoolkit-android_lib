package com.hacktoolkit.android.models;

import java.util.HashMap;

public class HTKContact {
	HashMap<String, Object> data;
	HashMap<String, Object> metaData;

	public HTKContact() {
		this.data = new HashMap<String, Object>();
		this.metaData = new HashMap<String, Object>();
		this.setMetaData("selected", false);
	}

	public void setData(String key, Object value) {
		data.put(key, value);
	}

	public Object getData(String key) {
		Object value = data.get(key);
		return value;
	}

	public void setMetaData(String key, Object value) {
		metaData.put(key, value);
	}

	public Object getMetaData(String key) {
		Object value = metaData.get(key);
		return value;
	}
}
