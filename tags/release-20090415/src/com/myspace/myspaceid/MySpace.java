package com.myspace.myspaceid;

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import com.myspace.myspaceid.oauth.*;

/**
 * Class that encapsulates MySpace service against which you can perform OAuth requests as well as fetch user data.
 */
public class MySpace
{
	protected static final String OAUTH_REQUEST_TOKEN_URL = "http://api.myspace.com/request_token";
	protected static final String OAUTH_AUTHORIZATION_URL = "http://api.myspace.com/authorize";
	protected static final String OAUTH_ACCESS_TOKEN_URL  = "http://api.myspace.com/access_token";

	protected static final String API_USERINFO_URL   = "http://api.myspace.com/v1/user.json";
	protected static final String API_ALBUMS_URL     = "http://api.myspace.com/v1/users/%s/albums.json";
	protected static final String API_ALBUM_URL      = "http://api.myspace.com/v1/users/%s/albums/%s/photos.json";
	protected static final String API_ALBUMPHOTO_URL = "http://api.myspace.com/v1/users/%s/albums/%s/photos/%s.json";
	protected static final String API_ALBUMINFO_URL  = "http://api.myspace.com/v1/users/%s/albums/%s.json";
	protected static final String API_FRIENDS_URL    = "http://api.myspace.com/v1/users/%s/friends.json";
	protected static final String API_FRIENDSLIST_URL = "http://api.myspace.com/v1/users/%s/friendslist/%s.json";
	protected static final String API_FRIENDSHIP_URL = "http://api.myspace.com/v1/users/%s/friends/%s.json";
	protected static final String API_MOOD_URL       = "http://api.myspace.com/v1/users/%s/mood.json";
	protected static final String API_MOODS_URL      = "http://api.myspace.com/v1/users/%s/moods.json";
	protected static final String API_PHOTOS_URL     = "http://api.myspace.com/v1/users/%s/photos.json";
	protected static final String API_PHOTO_URL      = "http://api.myspace.com/v1/users/%s/photos/%s.json";
	protected static final String API_PROFILE_URL    = "http://api.myspace.com/v1/users/%s/profile.json";
	protected static final String API_FRIENDS_STATUS_URL = "http://api.myspace.com/v1/users/%s/friends/status.json";
	protected static final String API_STATUS_URL     = "http://api.myspace.com/v1/users/%s/status.json";
	protected static final String API_PUT_STATUS_URL = "http://api.myspace.com/v1/users/%s/status";
	protected static final String API_PUT_MOOD_URL = "http://api.myspace.com/v1/users/%s/mood";
	protected static final String API_VIDEOS_URL     = "http://api.myspace.com/v1/users/%s/videos.json";
	protected static final String API_VIDEO_URL      = "http://api.myspace.com/v1/users/%s/videos/%s.json";
	protected static final String API_ACTIVITIES_URL = "http://api.myspace.com/v1/users/%s/activities.atom";
	protected static final String API_FRIENDSACTIVITIES_URL = "http://api.myspace.com/v1/users/%s/friends/activities.atom";
	protected static final String API_GLOBAL_APP_DATA_URL	 = "http://api.myspace.com/v1/appdata/global.json";
	protected static final String API_GLOBAL_APP_DATA_KEYS_URL	 = "http://api.myspace.com/v1/appdata/global/%s.json";
	protected static final String API_GLOBAL_APP_DATA_XML_URL	 = "http://api.myspace.com/v1/appdata/global";
	protected static final String API_GLOBAL_APP_DATA_KEYS_XML_URL	 = "http://api.myspace.com/v1/appdata/global/%s";
	protected static final String API_APP_DATA_URL	 = "http://api.myspace.com/v1/users/%s/appdata.json";
	protected static final String API_APP_DATA_KEYS_URL	 = "http://api.myspace.com/v1/users/%s/appdata/%s.json";
	protected static final String API_APP_DATA_XML_URL	= "http://api.myspace.com/v1/users/%s/appdata";
	protected static final String API_APP_DATA_KEYS_XML_URL	 = "http://api.myspace.com/v1/users/%s/appdata/%s";
	protected static final String API_FRIENDS_APP_DATA_URL	 = "http://api.myspace.com/v1/users/%s/friends/appdata.json";
	protected static final String API_FRIENDS_APP_DATA_KEYS_URL	 = "http://api.myspace.com/v1/users/%s/friends/appdata/%s.json";
	protected static final String API_COMMENTS_URL   = "http://api.myspace.com/v1/users/%s/comments.json";
	protected static final String API_INDICATORS_URL   = "http://api.myspace.com/v1/users/%s/indicators.json";
	protected static final String API_PREFERENCES_URL   = "http://api.myspace.com/v1/users/%s/preferences.json";

	// Member variables
	protected OAuthConsumer consumer;
	protected OAuthToken accessToken;
	protected OAuthServer server; // This is not set directly by the developer.  Represents the MySpace server viewed as an OAuthServer.
	protected int appType; // One of the values in ApplicationType class

    /**
	 * Constructor.  Use this to construct a MySpace object for onsite applications (application type = ApplicationType.OFF_SITE).
	 * @param consumerKey Consumer key to use.  You should have obtained the consumer key and consumer secret from developer.myspace.com.
	 * @param consumerSecret Consumer secret to use.
	 */
    public MySpace(String consumerKey, String consumerSecret) {
		appType = ApplicationType.OFF_SITE;
		consumer = new OAuthConsumer(consumerKey, consumerSecret);
		server = new OAuthServer(consumer);
	}

    /**
	 * Constructor.  Use this to construct a MySpace object for 2 scenarios:
	 * <ul>
	 * <li> For offsite apps, use this to construcut the initial MySpace object for obtaining the request token and to obtain the authorization URL that you would use to authenticate the user.
	 * <li> For onsite apps, use this to construct the MySpace object that you can use immediately to obtain user data without needing to go through user authentication.
	 * </ul>
	 * @param consumerKey Consumer key to use.  You should have obtained the consumer key and consumer secret from developer.myspace.com.
	 * @param consumerSecret Consumer secret to use.
	 * @param applicationType ApplicationType.ON_SITE for apps that run within MySpace, and ApplicationType.OFF_SITE for apps that run outside MySpace.  See {@link ApplicationType}.
	 */
    public MySpace(String consumerKey, String consumerSecret, int applicationType) {
		appType = applicationType;
		consumer = new OAuthConsumer(consumerKey, consumerSecret);
		server = new OAuthServer(consumer);
	}
	
    /**
	 * Mature constructor that accepts access token key and secret.  Use to construct a mature offsite MySpace object that includes the access token key and secret.  Such a "mature" MySpace 
	 * object can be used to obtain the access token and user data for offsite applications.  This constructor automatically assumes a value of ApplicationType.OFF_SITE for the application type.
	 * @param consumerKey Consumer key to use.  You should have obtained the consumer key and consumer secret from developer.myspace.com.
	 * @param consumerSecret Consumer secret to use.
	 * @param applicationType ApplicationType.ON_SITE for apps that run within MySpace, and ApplicationType.OFF_SITE for apps that run outside MySpace.  See {@link ApplicationType}.
	 * @param accessTokenKey Key of access token.
	 * @param accessTokenSecret Secret of access token.
	 */
	public MySpace(String consumerKey, String consumerSecret, int applicationType, String accessTokenKey, String accessTokenSecret) {
		this(consumerKey, consumerSecret, ApplicationType.OFF_SITE);
		accessToken = new OAuthToken(accessTokenKey, accessTokenSecret);
		server.setAccessToken(accessToken);
	}

	/**
	 * Returns the request token.  This should be your first step.  After this, allow the user to authenticate himself by redirecting 
	 * to the URL found by calling {@link #getAuthorizationURL}.
	 * @return A request token.
	 */
    public OAuthToken getRequestToken() {
		String reqUrl = server.generateRequestUrl(OAUTH_REQUEST_TOKEN_URL);
		String response = server.doHttpReq(reqUrl);
		OAuthToken token = new OAuthToken(response);
		return token;
	}

	/**
	 * Returns the authorization URL that you should redirect to for the user to authenticate.  After successful authentication, the user's 
	 * browser is redirected back to the callback URL.  The callback URL receives an oauth_token parameter that you need to pick up and then 
	 * call {@link #getAccessToken}.
	 * @param requestToken The request token obtained by calling {@link #getRequestToken}.
	 * @param callbackURL The callback URL that MySpace redirects the user to after she authenticates successfully.
	 * @return the authorization URL that you should redirect to for the user to authenticate.
	 */
    public String getAuthorizationURL(OAuthToken requestToken, String callbackURL) { 
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("oauth_token", requestToken.getKey());
		map.put("oauth_callback", callbackURL);
		String result = server.generateRequestUrl(OAUTH_AUTHORIZATION_URL, "", map);
		return result;
	}
 
	/**
	 * After successful authentication, the user's browser is redirected back to the callback URL.  The callback URL receives an oauth_token 
	 * parameter that you need to pick up.  Create an OAuthToken object using it as key; for secret, use the previous request token's secret.
	 * @param requestToken A token token to use to get an access token.
	 * @return An access token; you can now start getting user data with this access token.
	 */
    public OAuthToken getAccessToken(OAuthToken requestToken) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("oauth_token", requestToken.getKey()); // This needs to be populated from the outside!
		String reqUrl = server.generateRequestUrl(OAUTH_ACCESS_TOKEN_URL, requestToken.getSecret(), map);
//System.out.println("+++++ " + reqUrl);
		String response = server.doHttpReq(reqUrl);
		OAuthToken token = new OAuthToken(response);
		return token;
	}

	//
    // Wrappers for MySpace REST APIs
    //

	/**
	 * Returns global app data for this app.
	 * @return app data.
	 */
    public JSONObject getGlobalAppData() {
		String url = API_GLOBAL_APP_DATA_URL;
		return (JSONObject) getUserData(url, new HashMap<String, String>());
	}

	/**
	 * Returns global app data for this app under the given keys.
	 * @param keys semicolon-separated keys
	 * @return Values fetched for the given keys
	 */
    public JSONObject getGlobalAppData(String keys) {
		String url = API_GLOBAL_APP_DATA_KEYS_URL.replaceFirst("%s", keys);
		return (JSONObject) getUserData(url, new HashMap<String, String>());
	}

	/**
	 * Sets the given key-value pairs into the global app data.
	 * @param appParams HashMap containing key-value pairs to store.
	 * @return 
	 */
    public Object putGlobalAppData(Map<String, String> appParams) {
		String url = API_GLOBAL_APP_DATA_XML_URL;
		return putUserData(url, new HashMap<String, String>(), appParams);
	}

	/**
	 * Sets the given key-value pairs into the global app data.
	 * @param appParams HashMap containing key-value pairs to store.
	 * @return 
	 */
    public Object clearGlobalAppData(String keys) {
		String url = API_GLOBAL_APP_DATA_KEYS_XML_URL.replaceFirst("%s", keys);
		return putUserData("DELETE", url, new HashMap<String, String>(), new HashMap<String, String>());
	}

	/**
	 * Sets the given key-value pairs into a user's app data.
	 * @param userId ID of user to retrieve app data for.
	 * @param appParams HashMap containing key-value pairs to store.
	 * @return 
	 */
    public Object putAppData(String userId, Map<String, String> appParams) {
		requireAccessToken();
		String url = API_APP_DATA_XML_URL.replaceFirst("%s", userId);
		return putUserData(url, new HashMap<String, String>(), appParams);
	}

	/**
	 * Returns app data for specified user.
	 * @param userId ID of user to retrieve app data for.
	 * @return app data of specified user
	 */
    public JSONObject getAppData(String userId) {
		requireAccessToken();
//		String url = API_APP_DATA_URL.replaceFirst("%s", userId);
//		return getUserData(url, new HashMap<String, String>());
		return getAppData(userId, null);
	}

	/**
	 * Returns app data for specified user.
	 * @param userId ID of user to retrieve app data for.
	 * @param keys semicolon-separated keys
	 * @return app data corresponding to given keys
	 */
    public JSONObject getAppData(String userId, String keys) {
		requireAccessToken();
		String url = null;
		if (keys == null)
			url = API_APP_DATA_URL.replaceFirst("%s", userId);
		else
			url = API_APP_DATA_KEYS_URL.replaceFirst("%s", userId).replaceFirst("%s", keys);
		return (JSONObject) getUserData(url, new HashMap<String, String>());
	}


    /**
	 * Sets the given key-value pairs into the global app data.
	 * @param appParams HashMap containing key-value pairs to store.
	 * @return 
	 */
    public Object clearAppData(String userId, String keys) {
		requireAccessToken();
		String url = API_APP_DATA_KEYS_XML_URL.replaceFirst("%s", userId).replaceFirst("%s", keys);
		return putUserData("DELETE", url, new HashMap<String, String>(), new HashMap<String, String>());
	}

    //! To verify after server side is fixed
    /**
     * This apparently has a bug on the server side.
	 * Returns app data for specified user's friends.
	 * @param userId ID of user to retrieve app data for.
	 * @return app data of specified user's friends
	 */
    public JSONArray getUserFriendsAppData(String userId) {
		requireAccessToken();
//		String url = API_APP_DATA_URL.replaceFirst("%s", userId);
//		return getUserData(url, new HashMap<String, String>());
		return getUserFriendsAppData(userId, null);
	}

    //! To test!
	/**
	 * This method is untested!
	 * Returns app data for specified user's friends
	 * @param userId ID of user to retrieve app data for.
	 * @param keys semicolon-separated keys
	 * @return app data corresponding to given keys
	 */
    public JSONArray getUserFriendsAppData(String userId, String keys) {
		requireAccessToken();
		String url = null;
		if (keys == null)
			url = API_FRIENDS_APP_DATA_URL.replaceFirst("%s", userId);
		else
			url = API_FRIENDS_APP_DATA_KEYS_URL.replaceFirst("%s", userId).replaceFirst("%s", keys);
		return (JSONArray) getUserData(url, new HashMap<String, String>());
	}

	/**
	 * Returns comments for a user.
	 * @return comments for a user.
	 */
    public JSONObject getComments(String userId) {
		requireAccessToken();
		String url = API_COMMENTS_URL.replaceFirst("%s", userId);
		return (JSONObject) getUserData(url, new HashMap<String, String>());
	}

	/**
	 * Returns indicators for a user.
	 * @return indicators for a user.
	 */
    public JSONObject getIndicators(String userId) {
		requireAccessToken();
		String url = API_INDICATORS_URL.replaceFirst("%s", userId);
		return (JSONObject) getUserData(url, new HashMap<String, String>());
	}

	/**
	 * Returns preferences for a user.
	 * @return preferences for a user.
	 */
    public JSONObject getPreferences(String userId) {
		requireAccessToken();
		String url = API_PREFERENCES_URL.replaceFirst("%s", userId);
		return (JSONObject) getUserData(url, new HashMap<String, String>());
	}

	/**
	 * Returns the id of the user for whom we have an access token.
	 * This method requires that the access token has been stored in its MySpace object.  Use the "mature" constructor that 
	 * takes the access token key and secret as parameters.
	 * @return the id of the user for whom we have an access token.
	 */
    public String getUserId() {
		requireAccessToken();
		HashMap<String, String> map = new HashMap<String, String>();
		String reqUrl = server.generateRequestUrl(API_USERINFO_URL, accessToken == null ? "" : accessToken.getSecret(), map);
		String response = server.doHttpReq(reqUrl);

		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		try
		{
			obj = (JSONObject) parser.parse(response);
		}
		catch (ParseException pe)
		{
			throw new MySpaceException(pe.getMessage(), MySpaceException.REMOTE_ERROR);
		}

		Long id = (Long) obj.get("userId");
		return id.toString();
	}


	/**
	 * Returns the albums of the given user.
	 * This method requires that the access token has been stored in its MySpace object.
	 * @param userId ID of user to query.
	 * @return the albums of the given user.
	 */
    public JSONObject getAlbums(String userId) {
		return getAlbums(userId, -1, -1);
	}

	/**
	 * Returns the albums of the given user.
	 * This method requires that the access token has been stored in its MySpace object.
	 * @param userId ID of user to query.
	 * @param page Which page.  Pass -1 to not specify.
	 * @param pageSize Number of items per page.  Pass -1 to not specify.
	 * @return the albums of the given user.
	 */
	public JSONObject getAlbums(String userId, int page, int pageSize) {
		requireAccessToken();
		HashMap<String, String> map = new HashMap<String, String>();
		if (page != -1)
			map.put("page", String.valueOf(page));
		if (pageSize != -1)
			map.put("page_size", String.valueOf(pageSize));
		String url = API_ALBUMS_URL.replaceFirst("%s", userId);
		return (JSONObject) getUserData(url, map);
	}

	/**
	 * Returns an album of the given user.
	 * This method requires that the access token has been stored in its MySpace object.
	 * @param userId ID of user to query.
	 * @param albumId Which album to return.
	 * @return an album of the given user.
	 */
	public JSONObject getAlbum(String userId, int albumId) {
		requireAccessToken();
		HashMap<String, String> map = new HashMap<String, String>();
		String url = API_ALBUM_URL.replaceFirst("%s", userId);
		url = url.replaceFirst("%s", Integer.toString(albumId));
		return (JSONObject) getUserData(url, map);
	}

	/**
	 * Returns a photo in an album of the given user.
	 * This method requires that the access token has been stored in its MySpace object.
	 * @param userId ID of user to query.
	 * @param albumId Which album.
	 * @param photoId Which photo to return.
	 * @return a photo in an album of the given user.
	 */
	public JSONObject getAlbumPhoto(String userId, int albumId, int photoId) {
		requireAccessToken();
		HashMap<String, String> map = new HashMap<String, String>();
		String url = API_ALBUMPHOTO_URL.replaceFirst("%s", userId);
		url = url.replaceFirst("%s", Integer.toString(albumId)).replaceFirst("%s", Integer.toString(photoId));
		return (JSONObject) getUserData(url, map);
	}

	/**
	 * Returns an album's info for the given user.
	 * This method requires that the access token has been stored in its MySpace object.
	 * @param userId ID of user to query.
	 * @param albumId Which album to return.
	 * @return an album's info for the given user.
	 */
	public JSONObject getAlbumInfo(String userId, int albumId) {
		requireAccessToken();
		HashMap<String, String> map = new HashMap<String, String>();
		String url = API_ALBUMINFO_URL.replaceFirst("%s", userId);
		url = url.replaceFirst("%s", Integer.toString(albumId));
		return (JSONObject) getUserData(url, map);
	}

	/**
	 * Returns the friends of the given user.
	 * This method requires that the access token has been stored in its MySpace object.
	 * @param userId ID of user to query.
	 * @return an album of the given user.
	 */
    public JSONObject getFriends(String userId) {
		return getFriends(userId, -1, -1, null, null);
	}

    protected void validateShowString(String show) throws MySpaceException {
    	int i = 0;
		String[] validShowValues = {"mood", "status", "online"};
		if (show != null) {
			String[] showParams = show.split("\\|");
			for (int j = 0; j < showParams.length; j++) {
//System.out.println("j = " + j + ", " + showParams[j]);
				for (i = 0; i < validShowValues.length && !showParams[j].equals(validShowValues[i]); i++) {}
				if (i == validShowValues.length)
					throw new MySpaceException("Invalid value '" + showParams[j] + "' for show paramater.  Must be one of mood, status or online.");
			}
		}
    }
    
	/**
	 * Returns the friends of the given user.
	 * This method requires that the access token has been stored in its MySpace object.
	 * @param userId ID of user to query.
	 * @param page Which page.  First page is numbered '1'.  Pass -1 to not specify.
	 * @param pageSize Number of items per page.  Pass -1 to not specify.
	 * @param list Can be one of 'top', 'online' or 'app'
	 * @param show can be a combination of 'mood', 'status', 'online' separated by '|'.  Do not put spaces in this string.
	 * @return an album of the given user.
	 */
    public JSONObject getFriends(String userId, int page, int pageSize, String list, String show) { 
		requireAccessToken();
		HashMap<String, String> map = new HashMap<String, String>();

		// Validate list and show parameters
		int i;
		String[] validListValues = {"top", "online", "app"};
		for (i = 0; list != null && i < validListValues.length && !list.equals(validListValues[i]); i++) {}
		if (i == validListValues.length)
			throw new MySpaceException("Invalid value '" + list + "' for list paramater.  Must be one of top, online or app.");

		validateShowString(show);

		// Prep params and then send request
		if (page != -1)
			map.put("page", String.valueOf(page));
		if (pageSize != -1)
			map.put("page_size", String.valueOf(pageSize));
		if (list != null)
			map.put("list", list);
		if (show != null)
			map.put("show", show);
		String url = API_FRIENDS_URL.replaceFirst("%s", userId);
		return (JSONObject) getUserData(url, map);
	}

    /**
     * Returns the specified friends of the given user.
     * @param userId ID of user to query.
     * @param friends List of friend ID's separated by semicolons.
     * @return the specified friends of the given user.
     */
    public JSONObject getFriendsList(String userId, String friends) {
    	return getFriendsList(userId, friends, null);
    }
    
    /**
     * Returns the specified friends of the given user.
     * @param userId ID of user to query.
     * @param friends List of friend ID's separated by semicolons.
     * @param show can be a combination of 'mood', 'status', 'online' separated by '|'.  Do not put spaces in this string.
     * @return the specified friends of the given user.
     */
    public JSONObject getFriendsList(String userId, String friends, String show) {
		requireAccessToken();
		HashMap<String, String> map = new HashMap<String, String>();

		validateShowString(show);

		// Prep params and then send request
		if (show != null)
			map.put("show", show);
		String url = API_FRIENDSLIST_URL.replaceFirst("%s", userId).replaceFirst("%s", friends);
		return (JSONObject) getUserData(url, map);
    }
    
	/**
	 * Returns the friendship of the given user with other users.
	 * This method requires that the access token has been stored in its MySpace object.
	 * @param userId ID of user to query.
	 * @param friendIds IDs of friends to check, separated by semicolons.
	 * @return the friendship of the given user with other users.
	 */
	public JSONObject getFriendship(String userId, String friendIds) {
		requireAccessToken();
		String url = API_FRIENDSHIP_URL.replaceFirst("%s", userId).replaceFirst("%s", friendIds);
		return (JSONObject) getUserData(url, new HashMap<String, String>());
	}

	/**
	 * Returns the mood of the given user.
	 * This method requires that the access token has been stored in its MySpace object.
	 * @param userId ID of user to query.
	 * @return the mood of the given user.
	 */
    public JSONObject getMood(String userId) {
		requireAccessToken();
		String url = API_MOOD_URL.replaceFirst("%s", userId);
		return (JSONObject) getUserData(url, new HashMap<String, String>());
	}

	/**
	 * Returns the moods available to the given user.
	 * This method requires that the access token has been stored in its MySpace object.
	 * @param userId ID of user to query.
	 * @return the moods available to the given user.
	 */
    public JSONObject getMoods(String userId) {
		requireAccessToken();
		String url = API_MOODS_URL.replaceFirst("%s", userId);
		return (JSONObject) getUserData(url, new HashMap<String, String>());
	}

	/**
	 * Returns the photos of the given user.
	 * This method requires that the access token has been stored in its MySpace object.
	 * @param userId ID of user to query.
	 * @param page Which page.  Pass -1 to not specify.
	 * @param pageSize Number of items per page.  Pass -1 to not specify.
	 * @return the photos of the given user.
	 */
	 public JSONObject getPhotos(String userId, int page, int pageSize) {
		requireAccessToken();
		HashMap<String, String> map = new HashMap<String, String>();
		if (page != -1)
			map.put("page", String.valueOf(page));
		if (pageSize != -1)
			map.put("page_size", String.valueOf(pageSize));
		String url = API_PHOTOS_URL.replaceFirst("%s", userId);
		return (JSONObject) getUserData(url, map);
	}

	/**
	 * Returns a photo of the given user.
	 * This method requires that the access token has been stored in its MySpace object.
	 * @param userId ID of user to query.
	 * @param photoId Id of photo to get.
	 * @return a photo of the given user.
	 */
    public JSONObject getPhoto(String userId, int photoId) {
		requireAccessToken();
		String url = API_PHOTO_URL.replaceFirst("%s", userId).replaceFirst("%s", String.valueOf(photoId));
		return (JSONObject) getUserData(url, new HashMap<String, String>());
	}

	/**
	 * Returns the profile of a given user.
	 * This method requires that the access token has been stored in its MySpace object.
	 * @param userId ID of user to query.
	 * @return the profile of the given user.
	 */
    public JSONObject getProfile(String userId) {
		return getProfile(userId, "full"); 
	}
 
	/**
	 * Returns the profile of a given user.
	 * This method requires that the access token has been stored in its MySpace object.
	 * @param userId ID of user to query.
	 * @param detailType "basic", "full" or "extended"
	 * @return the profile of the given user.
	 */
    public JSONObject getProfile(String userId, String detailType) {
		requireAccessToken();
		String url = API_PROFILE_URL.replaceFirst("%s", userId);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("detailtype", detailType);
		return (JSONObject) getUserData(url, map);
	} 
 
	/**
	 * Returns the status of a given user.
	 * This method requires that the access token has been stored in its MySpace object.
	 * @param userId ID of user to query.
	 * @return the status of the given user.
	 */
    public JSONObject getStatus(String userId) {
		requireAccessToken();
		String url = API_STATUS_URL.replaceFirst("%s", userId);
		return (JSONObject) getUserData(url, new HashMap<String, String>());
	}

	/**
	 * Posts a status update.
	 * @param userId ID of user to query.
	 * @param Status update to post.
	 * @return the return string from the server.
	 * @deprecated Please use {@link #setStatus}
	 */
    @Deprecated public Object postStatus(String userId, String status) {
		requireAccessToken();
		String url = API_PUT_STATUS_URL.replaceFirst("%s", userId);
		HashMap<String, String> map = new HashMap<String, String>();
		HashMap<String, String> appParams = new HashMap<String, String>();
		appParams.put("status", status);
		return putUserData(url, map, appParams);
	}
	
	/**
	 * Posts a mood update.
	 * @param userId ID of user to query.
	 * @param mood update to post.
	 * @return the return string from the server.
	 * @deprecated Please use {@link #setMood}
	 */
    @Deprecated public Object postMood(String userId, int mood) {
		requireAccessToken();
		String url = API_PUT_MOOD_URL.replaceFirst("%s", userId);
		HashMap<String, String> map = new HashMap<String, String>();
		HashMap<String, String> appParams = new HashMap<String, String>();
		appParams.put("mood", "" + mood);
		return putUserData(url, map, appParams);
	}

	/**
	 * Posts a mood update.
	 * @param userId ID of user to query.
	 * @param mood update to post.
	 * @return the return string from the server.
	 */
	public Object setMood(String userId, int mood) {
		return postMood(userId, mood);
	}

	/**
	 * Posts a status update.
	 * @param userId ID of user to query.
	 * @param Status update to post.
	 * @return the return string from the server.
	 */
    public Object setStatus(String userId, String status) {
		return postStatus(userId, status);
	}

	/**
	 * Returns the status of a given user's friends.
	 * This method requires that the access token has been stored in its MySpace object.
	 * @param userId ID of user to query.
	 * @return the status of the given user's friends.
	 */
    public JSONObject getFriendsStatus(String userId) {
		requireAccessToken();
		String url = API_FRIENDS_STATUS_URL.replaceFirst("%s", userId);
		return (JSONObject) getUserData(url, new HashMap<String, String>());
	}

	
	/**
	 * Returns the videos of a given user.
	 * This method requires that the access token has been stored in its MySpace object.
	 * @param userId ID of user to query.
	 * @return the videos of the given user.
	 */
    public JSONObject getVideos(String userId) {
		requireAccessToken();
		String url = API_VIDEOS_URL.replaceFirst("%s", userId);
		return (JSONObject) getUserData(url, new HashMap<String, String>());
	}

	/**
	 * Returns a video of the given user.
	 * This method requires that the access token has been stored in its MySpace object.
	 * @param userId ID of user to query.
	 * @param videoId Id of photo to get.
	 * @return a video of the given user.
	 */
    public JSONObject getVideo(String userId, int videoId) {
		requireAccessToken();
		String url = API_VIDEO_URL.replaceFirst("%s", userId).replaceFirst("%s", String.valueOf(videoId));
		return (JSONObject) getUserData(url, new HashMap<String, String>());
	}

	/**
	 * Returns the current user as an object.  For most calls, you would first need to call {@link #getUserId} 
	 * to obtain the current user's id before you can access data.
	 * @return the current user.
	 */
	public JSONObject getUser() {
		requireAccessToken();
		String url = API_USERINFO_URL;
		return (JSONObject) getUserData(url, new HashMap<String, String>());
	}

	/**
	 * Returns the activities of the user.
	 * @param userId The ID of the user
	 * @return the activity stream of the user.
	 */
    public String getActivitiesAtom(String userId) {
		return getActivitiesAtom(userId, null, null, null);
	}

	/**
	 * Returns the activities of the user.
	 * @param userId The ID of the user
	 * @param culture (not used yet)
	 * @param lastRetrievalTimeStamp (not used yet)
	 * @param activityTypes (not used yet)
	 * @return the activity stream of the user.
	 */
	public String getActivitiesAtom(String userId, String culture, String lastRetrievalTimeStamp, String activityTypes) {
		requireAccessToken();
		String url = API_ACTIVITIES_URL.replaceFirst("%s", userId);
		String reqUrl = server.generateRequestUrl(url, accessToken == null ? "" : accessToken.getSecret(), new HashMap<String, String>());
		String response = server.doHttpReq(reqUrl);
		return response;
	}
	//! Test this

	/**
	 * Returns the activities of the user's friends.
	 * @param userId The ID of the user
	 * @return the activity stream of the user's friends.
	 */
    public String getFriendsActivitiesAtom(String userId) {
		return getFriendsActivitiesAtom(userId, null, null, null);
	}

	/**
	 * Returns the activities of the user's friends.
	 * @param userId The ID of the user
	 * @param culture (not used yet)
	 * @param lastRetrievalTimeStamp (not used yet)
	 * @param activityTypes (not used yet)
	 * @return the activity stream of the user's friends.
	 */
	public String getFriendsActivitiesAtom(String userId, String culture, String lastRetrievalTimeStamp, String activityTypes) {
		requireAccessToken();
		String url = API_FRIENDSACTIVITIES_URL.replaceFirst("%s", userId);
		String reqUrl = server.generateRequestUrl(url, accessToken == null ? "" : accessToken.getSecret(), new HashMap<String, String>());
		String response = server.doHttpReq(reqUrl);
		return response;
	}
	//! Test this
	
	/**
	 * Sends REST request to get user data.
	 * @param url URL prefix to use, e.g., http://api.myspace.com/v1/users/123456/albums.json
	 * @param map HashMap of additional parameters to send that are specific to this request
	 * @return user data in a {@link UserData} object.
	 */
	protected Object getUserData(String url, HashMap<String, String> map) {
		String reqUrl = server.generateRequestUrl(url, accessToken == null ? "" : accessToken.getSecret(), map);
		String response = server.doHttpReq(reqUrl);
		JSONParser parser = new JSONParser();
		Object obj = null;
		try
		{
			obj = parser.parse(response);
		}
		catch (ParseException e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			throw new MySpaceException(sw.toString(), MySpaceException.REMOTE_ERROR);
		}

		return obj;
	}


	/**
	 * Sends REST request to put user data.
	 * @param url URL prefix to use, e.g., http://api.myspace.com/v1/users/28568917/status
	 * @param map HashMap of additional parameters to send that are specific to this request
	 * @param putParam Name of parameter to put
	 * @param putValue Value of parameter to put
	 * @return user data in a {@link UserData} object.
	 */
	 //! Change putParam and putValue to a single hashmap
//	protected Object putUserData(String url, HashMap<String, String> map, String putParam, String putValue) {
	protected Object putUserData(String url, HashMap<String, String> map, Map<String, String> appParams) {
		return putUserData("PUT", url, map, appParams);
	}

	protected Object putUserData(String method, String url, HashMap<String, String> map, Map<String, String> appParams) {

//		if (putParam == null || putValue == null)
//			throw new MySpaceException("Put parameter or value cannot be null.");

		if (appParams != null)
			map.putAll(appParams);
		
		//
		// Note: for signing, the base string has to include the value being put, but the value is 
		// taken out of the parameter string itself and sent in the body of the request.  For instance, 
		// the below is a working HTTP conversation.
		// 
		// POST /v1/users/28568917/status?oauth_consumer_key=77f44916a5144c97ad1ddc9ec53338cc&oauth_nonce=8783759987300271273&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1237853013&oauth_token=8QLGnqFugwmCbIz6pcFbNEMPkG%252FCsZrg4fdqIzXpj88FsZaysd7wJ4eBonbvpAG7MOCFhzDjcM1yp6wvO%252BRaeyruy95QdfpFIHQaHvHL7ak%253D&oauth_version=1.0&oauth_signature=TvlXbt%2FNS0U7SrtUvUfu%2BfJ3kyo%3D HTTP/1.1
		// X-HTTP-Method-Override: PUT
		// User-Agent: Java/1.6.0_12
		// Host: api.myspace.com
		// Accept: text/html, image/gif, image/jpeg, *; q=.2, */*; q=.2
		// Proxy-Connection: keep-alive
		// Content-type: application/x-www-form-urlencoded
		// Content-Length: 264
		// 
		// status=Hello%20World%20%20%E7%BB%88%E4%BA%8E%E6%88%90%E5%8A%9F%E4%BA%86%EF%BC%81%21%21%20Can%20you%20believe%20that%20I%27m%20now%20posting%20status%20updates%20successfully%21%3F%20~%21%40%23%24%25%5E%26%2A%28%29_%2B%7B%7D%3A%22%3C%3E%3F%60-%3D%5B%5D%3B%27%2C.%2F
		//

		HashMap<String, String> headerMap = new HashMap<String, String>();
		headerMap.put("Content-type", "application/x-www-form-urlencoded");
//		String reqUrl = server.generateRequestUrl(url, accessToken == null ? "" : accessToken.getSecret(), map, "PUT", putParam);
		String reqUrl = server.generateRequestUrl(url, accessToken == null ? "" : accessToken.getSecret(), map, method, appParams.keySet());
		String response = null;
		try
		{
//			String bodyStr = putParam == null ? "" : putParam + "=" + OAuthServer.encode(putValue);
			String bodyStr = OAuthServer.buildParams(appParams);
			response = server.doHttpMethodReq(reqUrl, method, bodyStr, headerMap);
		}
		catch (Exception e)
		{
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			throw new MySpaceException(sw.toString(), MySpaceException.REMOTE_ERROR);
		}

		return (String) response;
	}


	/**
	 * Checks that an access token has been set up (done by using the constructor with 4 arguments).  Throws an exception if not.
	 */
	private void requireAccessToken() {
		if (appType == ApplicationType.OFF_SITE && accessToken == null) {
			StringBuffer sb = new StringBuffer();
			sb.append("Access token not set.  ");
			sb.append("You are using a MySpace object with applicationType = ApplicationType.OFF_SITE, which requires an access token for data access.  ");
			sb.append("After obtaining the access token, use the constructor that has parameters for access token key and secret.");
			sb.append("If you are running an onsite application, use a MySpace object with applicationType set to ApplicationType.ON_SITE.  ");
			throw new MySpaceException(sb.toString(), MySpaceException.TOKEN_REQUIRED);
		}
	}

	/**
	 * @param args Arguments passed in.
	 */
//	public static void main(String[] args) throws Exception {
//	}
}