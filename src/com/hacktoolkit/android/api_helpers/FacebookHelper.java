package com.hacktoolkit.android.api_helpers;

import java.util.ArrayList;
import java.util.List;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.model.GraphUser;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class FacebookHelper {

	public static void getFriends(final HTKCallback callback) {
		Request.newMyFriendsRequest(ParseFacebookUtils.getSession(), new Request.GraphUserListCallback() {

			@Override
			public void onCompleted(List<GraphUser> users, Response response) {
				if (users != null) {
					List<String> friendsList = new ArrayList<String>();
					for (GraphUser user : users) {
						friendsList.add(user.getId());
					}

					// Construct a ParseUser query that will find friends whose
					// Facebook IDs are contained in the current user's friend list.
					ParseQuery<ParseUser> friendQuery = ParseQuery.getQuery("ParseUser");
					friendQuery.whereContainedIn("fbId", friendsList);

					// findObjects will return a list of ParseUsers that are friends with
					// the current user
					try {
						List<ParseUser> friendUsers = friendQuery.find();
						callback.execute(friendUsers);
					} catch (ParseException e) {
						// poop!
					}
				}
			}
		}).executeAsync();
	}
}
