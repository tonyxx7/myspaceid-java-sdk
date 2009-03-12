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
	protected static final String API_FRIENDS_URL    = "http://api.myspace.com/v1/users/%s/friends.json";
	protected static final String API_FRIENDSHIP_URL = "http://api.myspace.com/v1/users/%s/friends/%s.json";
	protected static final String API_MOOD_URL       = "http://api.myspace.com/v1/users/%s/mood.json";
	protected static final String API_PHOTOS_URL     = "http://api.myspace.com/v1/users/%s/photos.json";
	protected static final String API_PHOTO_URL      = "http://api.myspace.com/v1/users/%s/photos/%s.json";
	protected static final String API_PROFILE_URL    = "http://api.myspace.com/v1/users/%s/profile.json";
	protected static final String API_STATUS_URL     = "http://api.myspace.com/v1/users/%s/status.json";
	protected static final String API_VIDEOS_URL     = "http://api.myspace.com/v1/users/%s/videos.json";
	protected static final String API_VIDEO_URL      = "http://api.myspace.com/v1/users/%s/videos/%s.json";
	protected static final String API_ACTIVITIES_URL = "http://api.myspace.com/v1/users/%s/activities.atom";

	// Member variables
	protected OAuthConsumer consumer;
	protected OAuthToken accessToken;
	protected OAuthServer server; // This is not set directly by the developer.  Represents the MySpace server viewed as an OAuthServer.

    /**
	 * Constructor.  Use to construct an initial MySpace object for obtaining the request token and to obtain the authorization URL.
	 * @param consumerKey Consumer key to use.  You should have obtained the consumer key and consumer secret from developer.myspace.com.
	 * @param consumerSecret Consumer secret to use.
	 */
    public MySpace(String consumerKey, String consumerSecret) {
		consumer = new OAuthConsumer(consumerKey, consumerSecret);
		server = new OAuthServer(consumer);
	}

    /**
	 * Mature constructor that accepts access token key and secret.  Use to construct a mature MySpace object that includes the access token key and secret.  Such a "mature" MySpace 
	 * object can be used to obtain the access token and user data.
	 * @param consumerKey Consumer key to use.  You should have obtained the consumer key and consumer secret from developer.myspace.com.
	 * @param consumerSecret Consumer secret to use.
	 * @param accessTokenKey Key of access token.
	 * @param accessTokenSecret Secret of access token.
	 */
	public MySpace(String consumerKey, String consumerSecret, String accessTokenKey, String accessTokenSecret) {
		this(consumerKey, consumerSecret);
		accessToken = new OAuthToken(accessTokenKey, accessTokenSecret);
		server.setAccessToken(accessToken);
	}

	/**
	 * Returns the request token.  This should be your first step.  After this, allow the user to authenticate himself by redirecting 
	 * to the URL found by calling {@link #getAuthorizationURL}.
	 * @return A request token.
	 */
    public OAuthToken getRequestToken() throws Exception {
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
		String response = server.doHttpReq(reqUrl);
		OAuthToken token = new OAuthToken(response);
		return token;
	}

	//
    // Wrappers for MySpace REST APIs
    //

	/**
	 * Returns the id of the user for whom we have an access token.
	 * This method requires that the access token has been stored in its MySpace object.  Use the "mature" constructor that 
	 * takes the access token key and secret as parameters.
	 * @return the id of the user for whom we have an access token.
	 */
    public String getUserId() {
		requireAccessToken();
		HashMap<String, String> map = new HashMap<String, String>();
		String reqUrl = server.generateRequestUrl(API_USERINFO_URL, accessToken.getSecret(), map);
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
		return getUserData(url, map);
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
		return getUserData(url, map);
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
		return getUserData(url, map);
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
		return getUserData(url, new HashMap<String, String>());
	}

	/**
	 * Returns the mood of the given user.
	 * This method requires that the access token has been stored in its MySpace object.
	 * @param userId ID of user to query.
	 * @return the friendship of the given user with other users.
	 */
    public JSONObject getMood(String userId) {
		requireAccessToken();
		String url = API_MOOD_URL.replaceFirst("%s", userId);
		return getUserData(url, new HashMap<String, String>());
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
		return getUserData(url, map);
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
		return getUserData(url, new HashMap<String, String>());
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
	 * @parama detailType "basic", "full" or "extended"
	 * @return the profile of the given user.
	 */
    public JSONObject getProfile(String userId, String detailType) {
		requireAccessToken();
		String url = API_PROFILE_URL.replaceFirst("%s", userId);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("detailtype", detailType);
		return getUserData(url, map);
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
		return getUserData(url, new HashMap<String, String>());
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
		return getUserData(url, new HashMap<String, String>());
	}

	/**
	 * Returns a video of the given user.
	 * This method requires that the access token has been stored in its MySpace object.
	 * @param userId ID of user to query.
	 * @param photoId Id of photo to get.
	 * @return a video of the given user.
	 */
    public JSONObject getVideo(String userId, int videoId) {
		requireAccessToken();
		String url = API_VIDEO_URL.replaceFirst("%s", userId).replaceFirst("%s", String.valueOf(videoId));
		return getUserData(url, new HashMap<String, String>());
	}

	/**
	 * Returns the current user as an object.  For most calls, you would first need to call {@link getUserId()} 
	 * to obtain the current user's id before you can access data.
	 * @return the current user.
	 */
	public JSONObject getUser() {
		requireAccessToken();
		String url = API_USERINFO_URL;
		return getUserData(url, new HashMap<String, String>());
	}

	/**
	 *
	 */
    public String getActvitiesAtom(String userId) {
		requireAccessToken();
		String url = API_ACTIVITIES_URL.replaceFirst("%s", userId);
		String reqUrl = server.generateRequestUrl(url, accessToken.getSecret(), new HashMap<String, String>());
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
	protected JSONObject getUserData(String url, HashMap<String, String> map) {
		String reqUrl = server.generateRequestUrl(url, accessToken.getSecret(), map);
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

		return obj;
//		return new UserData(obj);
	}

	/**
	 * Checks that an access token has been set up (done by using the constructor with 4 arguments).  Throws an exception if not.
	 */
	private void requireAccessToken() {
		if (accessToken == null) {
			throw new MySpaceException("Access token not set.  After obtaining the access token, use the constructor that has parameters for access token key and secret.", MySpaceException.TOKEN_REQUIRED);
		}
	}

	/**
	 * Test driver.
	 * @param args Arguments passed in.
	 */
	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			String consumerKey = args[0];
			String tokenKey = args[1];
			String baseString = args[2];

			String combinedKey = consumerKey + "&" + tokenKey;

			System.out.println("Using consumerKey = " + consumerKey);
			System.out.println("Using tokenKey = " + tokenKey);
			System.out.println("Using baseString = " + baseString);
			System.out.println("Using combinedKey = " + combinedKey);

//			System.out.println(getHMACSHA1(combinedKey, baseString));
			return;
		}
		
		// Account that works with this test: kiammyspace/myspace888
/*
		String key = "77f44916a5144c97ad1ddc9ec53338cc";
		String secret = "51951d1f872c454d8932cd5f135623ae";

		MySpace ms = new MySpace(key, secret);
		OAuthToken token = ms.getRequestToken();
		System.out.println(token);

		String str = ms.getAuthorizationURL(token, "http://testcallback");
		System.out.println("\nAuthorization URL (copy and access this URL using a browser and log in): \n" + str);

		System.out.print("\nEnter the new request token key from the callback: ");
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		String newTokenKey = br.readLine();
		System.out.println();
//		System.out.println("Using new token: " + newTokenKey);

		// To get access token, use the new request token returned in the Callback URL, and use the secret returned with the original request token
		OAuthToken token2 = new OAuthToken(newTokenKey, token.getSecret());
		OAuthToken token3 = ms.getAccessToken(token2);

		MySpace ms2 = new MySpace(key, secret, token3.getKey(), token3.getSecret());
		String id = ms2.getUserId();
		System.out.println(">>> User id = " + id);

		// Run tests
		JSONObject data = null;
		JSONObject obj = null;

		// getAlbums(userId) 
		//  - test with valid id
		//  - test invalid id

		// First test with valid id
		data = ms2.getAlbums(id);
		System.out.println("getAlbums() 1: " + data);
		obj = (JSONObject) data.get("user");
//		System.out.println(obj.getClass().getName());
//		UserData u = data.get("user");
		System.out.println(">>> User = " + obj.get("name"));

		// 2nd test with invalid id
		try
		{
			data = ms2.getAlbums("100");
		}
		catch (MySpaceException e)
		{
			System.out.println("Exception occurred with getAlbums() 2nd test");
		}

		// 3rd test with invalid id
		try
		{
			data = ms2.getAlbums("-1");
		}
		catch (MySpaceException e)
		{
			System.out.println("Exception occurred with getAlbums() 3rd test");
		}

*/

		// Test cases
		// getAlbums(userId, page, pageSize)
		//  - test with valid id, -1 for both page and pageSize
		//  - test with valid id, page and pageSize
		//  - test with valid id, invalid page, valid pageSize
		//  - test with valid id, invalid page, invalid pageSize
		//  - test with invalid id, valid page and pageSize
		// getAlbum(userId, albumId)
		//  - test with valid user id, valid album id
		//  - test with valid user id, invalid album id
		//  - test with invalid user id, valid album id
		// getFriends(userId)
		//  - test with valid id
		//  - test with invalid id
		// getFriends(userId, page, pageSize, list, show)
		//  - test with valid id, page, pageSize, list = top, show = mood
		//  - test with valid id, page, pageSize, list = top, show = mood|status
		//  - test with valid id, page, pageSize, list = top, show = mood|status|online
		//  - test with valid id, page, pageSize, list = top, show = mood | status  |online
		//  - test with valid id, page, pageSize, list = online, show = status
		//  - test with valid id, page, pageSize, list = app, show = status
		// getFriendship(userId, friendIds)
		//  - test with valid id, valid friend id
		//  - test with valid id, invalid friend id
		//  - test with invalid id, valid friend id
		//  - test with invalid id, invalid friend id
		// getMood(userId) 
		//  - test with valid id
		//  - test invalid id
		// getPhotos(userId, page, pageSize)
		//  - test with valid id, -1 for both page and pageSize
		//  - test with valid id, page and pageSize
		//  - test with valid id, invalid page, valid pageSize
		//  - test with valid id, invalid page, invalid pageSize
		//  - test with invalid id, valid page and pageSize
		// getPhoto(userId, photoId)
		//  - test with valid user id, valid photo id
		//  - test with valid user id, invalid photo id
		//  - test with invalid user id, valid photo id
		// getProfile(userId) 
		//  - test with valid id
		//  - test invalid id
		// getStatus(userId) 
		//  - test with valid id
		//  - test invalid id
		// getVideos(userId) 
		//  - test with valid id
		//  - test invalid id
		// getVideo(userId, videoId)
		//  - test with valid user id, valid video id
		//  - test with valid user id, invalid video id
		//  - test with invalid user id, valid video id
		// 

/*

		JSONObject friends = ms2.getFriends(id);
//		System.out.println(friends);
		Object f = friends.get("Friends");
//		System.out.println(f.getClass().getName());
//		System.out.println(f);
		JSONArray fa = (JSONArray) f;
		for (int i = 0; i < fa.size(); i++) {
			JSONObject friend = (JSONObject) fa.get(i);
			System.out.println(">>> Friend " + i + ":  " + friend.get("name"));
		}
//		System.out.println(((JSONArray) f).get(0));

		JSONObject status = ms2.getStatus(id);
		System.out.println(">>> Mood: " + status.get("mood"));
		
//		String response = ms.doHttpReq("http://api.myspace.com/request_token?oauth_consumer_key=77f44916a5144c97ad1ddc9ec53338cc&oauth_nonce=633707752256535691&oauth_signature=JgizpmzJL%2BT7L7L6Ctg3ObVmf8w%3D&oauth_signature_method=HMAC-SHA1&oauth_timestamp=1235178414");
//		System.out.println(response);
*/
	}
}