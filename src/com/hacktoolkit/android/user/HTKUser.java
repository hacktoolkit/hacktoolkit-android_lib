package com.hacktoolkit.android.user;

import java.util.Date;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.hacktoolkit.android.constants.TimeConstants;
import com.hacktoolkit.android.maps.MapUtils;
import com.parse.ParseFacebookUtils;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

/**
 * HTKUser
 * 
 * Represents the user of this app, and acts as a wrapper for ParseUser
 * @author Hacktoolkit
 *
 */
public class HTKUser {
	public static HTKUser getCurrentUser() {
		ParseUser parseUser = ParseUser.getCurrentUser();
		HTKUser user = getCurrentUser(parseUser);
		return user;
	}
	public static HTKUser getCurrentUser(ParseUser parseUser) {
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
		boolean auth = false;
		if (parseUser != null) {
			auth = parseUser.isAuthenticated();
		}
		return auth;
	}
	
	public String getName() {
		String firstName = parseUser.getString("firstName");
		String lastName = parseUser.getString("lastName");
		StringBuffer sb = new StringBuffer();
		sb.append(firstName);
		if (firstName != null && !firstName.equals("")) {
			sb.append(" ");
			sb.append(lastName);
		}
		String name = sb.toString();
		return name;
	}
	
	public void updateWithGraphUser() {
		Request.newMeRequest(ParseFacebookUtils.getSession(), new Request.GraphUserCallback() {
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if (user != null) {
					String fbId = user.getId();
					String firstName = user.getFirstName();
					String lastName = user.getLastName();
					String email = (String) user.getProperty("email");
					parseUser.put("fbId", fbId);
					parseUser.put("firstName", firstName);
					parseUser.put("lastName", lastName);
					parseUser.put("email", email);
					parseUser.saveEventually();
				}
			}
		}).executeAsync();
	}
	
	/**
	 * updateLocation
	 * 
	 * @param latitude
	 * @param longitude
	 * @param forceUpdate whether we should force an update
	 * @return true if location was updated, false otherwise
	 */
	public boolean updateLocation(double latitude, double longitude, boolean forceUpdate) {
		boolean updated = false;
		if (forceUpdate || shouldUpdateLocation(latitude, longitude)) {
			ParseGeoPoint geoPoint = new ParseGeoPoint(latitude, longitude);
			parseUser.put("location", geoPoint);
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
