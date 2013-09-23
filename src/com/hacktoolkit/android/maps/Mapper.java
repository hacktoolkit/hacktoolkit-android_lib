package com.hacktoolkit.android.maps;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class Mapper {
	public static Mapper _instance;
	public static Mapper getInstance(GoogleMap googleMap) {
		if (Mapper._instance == null && googleMap != null) {
			Mapper._instance = new Mapper(googleMap);
		}
		return Mapper._instance;
	}

	private GoogleMap googleMap;
	private MarkerOptions currentLocationMarker;
		
	public Mapper(GoogleMap googleMap) {
		this.googleMap = googleMap;
		// TODO: not working for some reason?
//		UiSettings settings = googleMap.getUiSettings();
//		settings.setMyLocationButtonEnabled(true);
	}
	
	public boolean updateMap(String label, double latitude, double longitude) {
		boolean updated = false;
		if (googleMap != null) {
			updateCurrentLocationMarker(label, latitude, longitude);

			LatLngBounds.Builder builder = new LatLngBounds.Builder();
//			for each (Marker m : markers) {
//				builder.include(m.getPosition());
//			}
			builder.include(currentLocationMarker.getPosition());
			LatLngBounds bounds = builder.build();

			int padding = 0; // offset from edges of the map in pixels
			CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
			//googleMap.animateCamera(cameraUpdate);
			googleMap.moveCamera(cameraUpdate);
			googleMap.moveCamera(CameraUpdateFactory.zoomTo(10));
			updated = true;
		}
		return updated;
	}
	
	private void updateCurrentLocationMarker(String label, double latitude, double longitude) {
		if (currentLocationMarker == null) {
			currentLocationMarker = new MarkerOptions()
			.position(new LatLng(latitude, longitude))
			.title(label);
			googleMap.addMarker(currentLocationMarker);
		} else {
			LatLng coord = new LatLng(latitude, longitude);
			currentLocationMarker.position(coord);
		}
	}
}
