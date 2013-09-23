package com.hacktoolkit.android.user;

import java.util.Date;

import com.hacktoolkit.android.constants.TimeConstants;
import com.hacktoolkit.android.maps.MapUtils;
import com.parse.ParseUser;

/**
 * HTKUser
 * 
 * Represents the user of this app, and acts as a wrapper for ParseUser
 * @author jontsai
 *
 */
public class HTKUser {
	public static HTKUser getCurrentUser() {
		ParseUser parseUser = ParseUser.getCurrentUser();
		HTKUser user = new HTKUser(parseUser);
		return user;
	}
	
	public static void logout() {
		ParseUser.logOut();
	}
	
	private ParseUser parseUser;
	public static final long LOCATION_UPDATE_THRESHOLD = 3 * TimeConstants.ONE_MINUTE_MILLIS;
	public static final int MOVEMENT_THRESHOLD_METERS = 25;
	
	public HTKUser(ParseUser parseUser) {
		this.parseUser = parseUser;
	}
	
	public boolean isAuthenticated() {
		boolean auth = parseUser.isAuthenticated();
		return auth;
	}
	
	public String getName() {
		String firstName = parseUser.getString("firstName");
		String lastName = parseUser.getString("lastName");
		StringBuffer sb = new StringBuffer();
		sb.append(firstName);
		if (!firstName.equals("")) {
			sb.append(" ");
			sb.append(lastName);
		}
		String name = sb.toString();
		return name;
	}
	
	/**
	 * updateLocation
	 * 
	 * @param latitude
	 * @param longitude
	 * @return true if location was updated, false otherwise
	 */
	public boolean updateLocation(double latitude, double longitude) {
		boolean updated = false;
		if (shouldUpdateLocation(latitude, longitude)) {
			parseUser.put("latitude", latitude);
			parseUser.put("longitude", longitude);
			parseUser.put("locationLastUpdatedAt", new Date());
			parseUser.saveEventually();
			updated = true;
		}
		return updated;
	}
	
	/**
	 * 
	 * @param latitude
	 * @param longitude
	 * @return true if the user's location should be updated
	 */
	private boolean shouldUpdateLocation(double latitude, double longitude) {
		boolean shouldUpdate = isSignificantMovement(latitude, longitude) &&
					hasLocationUpdateThresholdElapsed();
		return shouldUpdate;
	}
	
	/**
	 * Helps determine whether an update should happen
	 * @param latitude
	 * @param longitude
	 * @return true if the new coordinates represent a significant movement
	 */
	private boolean isSignificantMovement(double newLatitude, double newLongitude) {
		double previousLatitude = parseUser.getDouble("latitude");
		double previousLongitude = parseUser.getDouble("longitude");
		boolean significantMovement = false;
		if (previousLatitude == 0 && previousLongitude == 0) {
			significantMovement = true;
		} else {
			double distanceKm = MapUtils.geographicalGreatCircleDistance(previousLatitude, previousLongitude, newLatitude, newLongitude);
			significantMovement = distanceKm / 1000 > MOVEMENT_THRESHOLD_METERS;
		}
		return significantMovement;
	}
	
	/**
	 * Helps determine whether an update should happen
	 * Essentially rate-limits the number of updates
	 * @return true if LOCATION_UPDATE_THRESHOLD has elapsed
	 */
	private boolean hasLocationUpdateThresholdElapsed() {
		long sinceLastUpdate = 0;
		Date locationLastUpdatedAt = parseUser.getDate("locationLastUpdatedAt");
		Date now = new Date();
		boolean elapsed = false;
		if (locationLastUpdatedAt == null) {
			elapsed = true;
			sinceLastUpdate = 0;
		} else {
			sinceLastUpdate = now.getTime() - locationLastUpdatedAt.getTime();
			if (sinceLastUpdate > LOCATION_UPDATE_THRESHOLD) {
				elapsed = true;
			}
		}
		return elapsed;
	}
}
