package com.myspace.myspaceid;

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import com.myspace.myspaceid.*;
import com.myspace.myspaceid.oauth.*;

// Run using account kiammyspace
public class MySpaceTest {
	private static String id = null;
	private static MySpace ms = null;
	private static MySpace ms2 = null;
	private static PrintStream out = System.err;
	
	// Account that works with this test: kiammyspace/myspace888
	private static String key = "77f44916a5144c97ad1ddc9ec53338cc";
	private static String secret = "51951d1f872c454d8932cd5f135623ae";

	public static void setUp() throws Exception {
		// Initialize
		System.out.println("Initializing user ID");



		ms = new MySpace(key, secret, ApplicationType.OFF_SITE);
	}

	public static void testPutGlobalAppData() {
		printTitle("testPutGlobalAppData(Map)");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("animal", "cat");
		map.put("plant", "rose");
		Object appData = ms.putGlobalAppData(map);

		out.println("testPutGlobalAppData(Map) 1: " + appData);
	}

	public static void testGetGlobalAppData() {
		printTitle("testGetGlobalAppData()");
		JSONObject appData = ms.getGlobalAppData();

		out.println("testGetGlobalAppData() 1: " + appData);
	}

	public static void testGetGlobalAppData2() {
		printTitle("testGetGlobalAppData2()");
		JSONObject appData = ms.getGlobalAppData("animal;plant;name");

		out.println("testGetGlobalAppData2() 1: " + appData);
	}

	public static void testClearGlobalAppData() {
		printTitle("testClearGlobalAppData()");
		Object appData = ms.clearGlobalAppData("animal;plant");

		out.println("testClearGlobalAppData() 1: " + appData);
	}

	public static void globalTests() throws Exception {
		testPutGlobalAppData();
		
		// Put data may become available only after a delay, so sleep first
		Thread.sleep(1000);
		
		testGetGlobalAppData();
		testGetGlobalAppData2();
		testClearGlobalAppData();
		testGetGlobalAppData();
	}

	public static void setUp2() throws Exception {
		
		OAuthToken token = ms.getRequestToken();
		System.out.println(token);

//		String str = ms.getAuthorizationURL(token, "http://testcallback");
		String str = ms.getAuthorizationURL(token, "http://localhost:8080/myspaceid-sample/oauth/oauth-demo.jsp?callback=true");
		System.out.println("\nAuthorization URL (copy and access this URL using a browser and log in): \n" + str);

		System.out.print("\nEnter the new request token key from the callback: ");
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		String newTokenKey = br.readLine();
		newTokenKey = URLDecoder.decode(newTokenKey);
		System.out.println();
//		System.out.println("Using new token: " + newTokenKey);

		// To get access token, use the new request token returned in the Callback URL, and use the secret returned with the original request token
		OAuthToken token2 = new OAuthToken(newTokenKey, token.getSecret());
		OAuthToken token3 = ms.getAccessToken(token2);

		// Set static Myspace object and user id!
		ms2 = new MySpace(key, secret, ApplicationType.OFF_SITE, token3.getKey(), token3.getSecret());
		id = ms2.getUserId();
		System.out.println(">>> User id = " + id);
	}

	public static void printTitle(String str) {
		out.println();
		out.println("********************************");
		out.println(str);
		out.println("********************************");
	}

	public static void testGetAlbums() {
		// Run tests
		JSONObject data = null;
		JSONObject obj = null;

		// getAlbums(userId) 
		//  - test with valid id
		//  - test invalid id

		// First test with valid id
		printTitle("getAlbums(String) with valid user id");
		data = ms2.getAlbums(id);
		out.println("getAlbums(String) 1: " + data);
//		obj = (JSONObject) data.get("user");

		// 2nd test with invalid id
		printTitle("getAlbums(String) with invalid user id 100");
		try
		{
			data = ms2.getAlbums("100");
			out.println("getAlbums(String) 2: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getAlbums(String) 2nd test");
		}

		// 3rd test with invalid id
		printTitle("getAlbums(String) with invalid user -1");
		try
		{
			data = ms2.getAlbums("-1");
			out.println("getAlbums(String) 3: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getAlbums(String) 3rd test");
		}
	}

	public static void testGetAlbums2() {
		JSONObject data = null;
		JSONObject obj = null;
		String rc = null;

		printTitle("getAlbums(String, int, int) with valid user id and -1 for last 2 params");
		data = ms2.getAlbums(id, -1, -1);
		out.println("getAlbums(String, int, int) 1: " + data);

		printTitle("getAlbums(String, int, int) with 1 for last 2 params");
		data = ms2.getAlbums(id, 1, 1);
		out.println("getAlbums(String, int, int) 2: " + data);

		printTitle("getAlbums(String, int, int) with invalid page size");
		try
		{
			data = ms2.getAlbums(id, 1, -2);
			out.println("getAlbums(String, int, int) 3: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getAlbums(String, int, int) 1st test");
		}

		printTitle("getAlbums(String, int, int) with invalid page");
		try
		{
			data = ms2.getAlbums(id, 0, 1);
			out.println("getAlbums(String, int, int) 4: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getAlbums(String, int, int) 2nd test");
		}
	}

	public static void testGetAlbumPhoto() {
		JSONObject data = null;
		JSONObject obj = null;

		// Fetch album id to use
		data = ms2.getAlbums(id, -1, -1); // First get an album id to use
		JSONArray albums = (JSONArray) data.get("albums");
		Long idLong = (Long) ((JSONObject) albums.get(0)).get("id");
		int albumId = idLong.intValue(); // Got the album id!  Now use it...
		
		// Fetch photo id to use
		data = ms2.getAlbum(id, albumId);
		JSONArray photos = (JSONArray) data.get("photos");
		idLong = (Long) ((JSONObject) photos.get(0)).get("id");
		int photoId = idLong.intValue();
		
		printTitle("getAlbumPhoto(String, int, int) with valid user id, valid album id and valid photo id");
		data = ms2.getAlbumPhoto(id, albumId, photoId);
		out.println("getAlbumPhoto(String, int, int) 1: " + data);
	}
	
	public static void testGetAlbum() {
		JSONObject data = null;
		JSONObject obj = null;

		data = ms2.getAlbums(id, -1, -1); // First get an album id to use
		JSONArray albums = (JSONArray) data.get("albums");
		Long idLong = (Long) ((JSONObject) albums.get(0)).get("id");
		int albumId = idLong.intValue(); // Got the album id!  Now use it...
		
		printTitle("getAlbum(String, int) with valid user id and valid album id");
		data = ms2.getAlbum(id, albumId);
		out.println("getAlbum(String, int) 1: " + data);

		printTitle("getAlbum(String, int) with valid user id and invalid album id");
		try
		{
			data = ms2.getAlbum(id, -1111);
			out.println("getAlbum(String, int) 2: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getAlbum(String, int) 2nd test");
		}

		printTitle("getAlbum(String, int) with invalid user id and valid album id");
		try
		{
			data = ms2.getAlbum("-1", albumId);
			out.println("getAlbum(String, int) 3: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getAlbum(String, int) 3rd test");
		}
	}

	public static void testGetAlbumInfo() {
		JSONObject data = null;
		JSONObject obj = null;

		data = ms2.getAlbums(id, -1, -1); // First get an album id to use
		JSONArray albums = (JSONArray) data.get("albums");
		Long idLong = (Long) ((JSONObject) albums.get(0)).get("id");
		int albumId = idLong.intValue(); // Got the album id!  Now use it...
		
		printTitle("getAlbumInfo(String, int) with valid user id and valid album id");
		data = ms2.getAlbumInfo(id, albumId);
		out.println("getAlbumInfo(String, int) 1: " + data);

		printTitle("getAlbumInfo(String, int) with valid user id and invalid album id");
		try
		{
			data = ms2.getAlbumInfo(id, -1111);
			out.println("getAlbumInfo(String, int) 2: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getAlbumInfo(String, int) 2nd test");
		}

		printTitle("getAlbumInfo(String, int) with invalid user id and valid album id");
		try
		{
			data = ms2.getAlbumInfo("-1", albumId);
			out.println("getAlbumInfo(String, int) 3: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getAlbumInfo(String, int) 3rd test");
		}
	}

	public static void testGetFriends() {
		JSONObject data = null;
		JSONObject obj = null;

		// First test with valid id
		printTitle("getFriends(String) with valid user id");
		data = ms2.getFriends(id);
		out.println("getFriends(String) 1: " + data);

		// 2nd test with invalid id
		printTitle("getFriends(String) with invalid user id 100");
		try
		{
			data = ms2.getFriends("100");
			out.println("getFriends(String) 2: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getFriends(String) 2nd test");
		}
	}

	public static void testGetFriends2() {
		JSONObject data = null;
		JSONObject obj = null;

		// Test with valid id, page, pageSize, list = top, show = mood
		printTitle("getFriends(String, int, int, String, String) with valid user id, page, pageSize, list = top, show = mood");
		data = ms2.getFriends(id, 1, 1, "top", "mood");
		out.println("getFriends(String, int, int, String, String) 1: " + data);

		// Test with valid id, page, pageSize, list = top, show = mood|status
		printTitle("getFriends(String, int, int, String, String) with valid user id, page, pageSize, list = top, show = mood|status");
		data = ms2.getFriends(id, 1, 1, "top", "mood|status");
		out.println("getFriends(String, int, int, String, String) 2: " + data);

		// Test with valid id, page, pageSize, list = top, show = mood|status|online
		printTitle("getFriends(String, int, int, String, String) with valid user id, page, pageSize, list = top, show = mood|status|online");
		data = ms2.getFriends(id, 1, 1, "top", "mood|status|online");
		out.println("getFriends(String, int, int, String, String) 3: " + data);

		// Test with valid id, page, pageSize, list = top, show = mood | status | online
		printTitle("getFriends(String, int, int, String, String) with valid user id, page, pageSize, list = top, show = mood | status | online");
		try
		{
			data = ms2.getFriends(id, 1, 1, "top", "mood | status | online");
			out.println("getFriends(String, int, int, String, String) 4: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getFriends(String) 4th test");
		}

		// Test with valid id, page, pageSize, list = online, show = status
		printTitle("getFriends(String, int, int, String, String) with valid user id, page, pageSize, list = online, show = status");
		data = ms2.getFriends(id, 1, 1, "online", "status");
		out.println("getFriends(String, int, int, String, String) 5: " + data);

		// Test with valid id, page, pageSize, list = app, show = status
		printTitle("getFriends(String, int, int, String, String) with valid user id, page, pageSize, list = app, show = status");
		data = ms2.getFriends(id, 1, 1, "app", "status");
		out.println("getFriends(String, int, int, String, String) 6: " + data);

		// Test with valid id, page, pageSize, list = top, show = online|status|mood
		printTitle("getFriends(String, int, int, String, String) with valid user id, page, pageSize, list = top, show = online|status|mood");
		data = ms2.getFriends(id, 1, 1, "top", "online|status|mood");
		out.println("getFriends(String, int, int, String, String) 7: " + data);

		// Test with valid id, page, pageSize, list = top|app, show = status (list must be single-valued)
		printTitle("getFriends(String, int, int, String, String) with valid user id, page, pageSize, list = top|app, show = status");
		try
		{
			data = ms2.getFriends(id, 1, 1, "top|app", "status");
			out.println("getFriends(String, int, int, String, String) 8: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getFriends(String) 8th test");
		}
	}


	public static void testGetFriendsList() {
		JSONObject data = null;
		JSONObject obj = null;
		String friends = "6221;457099758;36452044";
		String invalidFriends = "6221;457099758;100";

		// First test with valid id
		printTitle("getFriendsList(String, String) with valid user id");
		data = ms2.getFriendsList(id, friends);
		out.println("getFriendsList(String, String) 1: " + data);

		// 2nd test with invalid id but valid friends
		printTitle("getFriendsList(String, String) with invalid user id 100");
		try
		{
			data = ms2.getFriendsList("100", friends);
			out.println("getFriendsList(String, String) 2: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getFriendsList(String, String) 2nd test");
		}

		// 3rd test with invalid id but invalid friends
		printTitle("getFriendsList(String, String) with invalid friend id 100");
		try
		{
			data = ms2.getFriendsList(id, invalidFriends);
			out.println("getFriendsList(String, String) 3: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getFriendsList(String, String) 3rd test");
		}
	}
	
	public static void testGetFriendsList2() {
		JSONObject data = null;
		JSONObject obj = null;
		String friends = "6221;457099758;36452044";
		String invalidFriends = "6221;457099758;100";
		String show = "mood|status|online";
		String invalidShow = "mood|status|onlinewrongstring";

		// 1st test with valid show string
		printTitle("getFriendsList(String, String, String) with valid user id");
		data = ms2.getFriendsList(id, friends, show);
		out.println("getFriendsList(String, String, String) 1: " + data);
		
		// 2nd test with invalid show string
		printTitle("getFriendsList(String, String, String) with invalid show string");
		try
		{
			data = ms2.getFriendsList(id, friends, invalidShow);
			out.println("getFriendsList(String, String, String) 2: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getFriendsList(String, String, String) 2nd test");
		}
	}

	public static void testGetFriendship() {
		// getFriendship(userId, friendIds)
		//  - test with valid id, valid friend id
		//  - test with valid id, invalid friend id

		JSONObject data = null;
		JSONObject obj = null;

		data = ms2.getFriends(id); // First get friend id's to use
		JSONArray friends = (JSONArray) data.get("Friends");
		Long idLong = (Long) ((JSONObject) friends.get(0)).get("userId");
		int friendId1 = idLong.intValue();
		idLong = (Long) ((JSONObject) friends.get(1)).get("userId");
		int friendId2 = idLong.intValue(); // Got the friend ids'!  Now use it...
		String friendIds = "" + friendId1 + ";" + friendId2 + ";457099751"; // This last one isn't a friend

		// Test with valid id, valid friend ID's
		printTitle("getFriendship(String, String) with valid user id, friend Id's");
		data = ms2.getFriendship(id, friendIds);
		out.println("getFriendship(String, String) 1: " + data);

		// Test with valid id, invalid friend ID
		printTitle("getFriendship(String, String) with valid user id, friend Id's");
		try
		{
			data = ms2.getFriendship(id, friendIds + ";-1");
			out.println("getFriendship(String, String) 2: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getFriends(String) 2nd test");
		}
	}

	public static void testGetMoods() {
		// getMoods(userId) 
		//  - test with valid id
		//  - test invalid id
		JSONObject data = null;
		JSONObject obj = null;

		// First test with valid id
		printTitle("getMoods(String) with valid user id");
		data = ms2.getMoods(id);
		out.println("getMoods(String) 1: " + data);

		// 2nd test with invalid id
		printTitle("getMoods(String) with invalid user id 100");
		try
		{
			data = ms2.getMoods("100");
			out.println("getMoods(String) 2: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getMoods(String) 2nd test");
		}
	}

	public static void testGetMood() {
		// getMood(userId) 
		//  - test with valid id
		//  - test invalid id
		JSONObject data = null;
		JSONObject obj = null;

		// First test with valid id
		printTitle("getMood(String) with valid user id");
		data = ms2.getMood(id);
		out.println("getMood(String) 1: " + data);

		// 2nd test with invalid id
		printTitle("getMood(String) with invalid user id 100");
		try
		{
			data = ms2.getMood("100");
			out.println("getMood(String) 2: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getMood(String) 2nd test");
		}
	}

	public static void testGetPhotos() {
		// getPhotos(userId, page, pageSize)
		//  - test with valid id, -1 for both page and pageSize
		//  - test with valid id, page and pageSize
		//  - test with valid id, invalid page, valid pageSize
		//  - test with valid id, invalid page, invalid pageSize
		//  - test with invalid id, valid page and pageSize

		JSONObject data = null;
		JSONObject obj = null;

		//  Test with valid id, -1 for both page and pageSize
		printTitle("getPhotos(String, int, int) with valid user id, -1 for both page and pageSize");
		data = ms2.getPhotos(id, -1, -1);
		out.println("getPhotos(String, int, int) 1: " + data);

		//  Test with valid id, page and pageSize
		printTitle("getPhotos(String, int, int) with valid user id, page and pageSize");
		data = ms2.getPhotos(id, 1, 10);
		out.println("getPhotos(String, int, int) 2: " + data);
	}

	public static void testGetPhoto() {
		//  - test with valid user id, valid photo id
		//  - test with valid user id, invalid photo id
		//  - test with invalid user id, valid photo id

		JSONObject data = null;
		JSONObject obj = null;
	
		data = ms2.getPhotos(id, 1, 10); // First get a photo id to use
		JSONArray photos = (JSONArray) data.get("photos");
		Long idLong = (Long) ((JSONObject) photos.get(0)).get("id");
		int photoId = idLong.intValue(); // Got the photo id!  Now use it...

		//  Test with valid id, valid photo id
		printTitle("getPhoto(String, int) with valid user id, valid photo id");
		data = ms2.getPhoto(id, photoId);
		out.println("getPhoto(String, int) 1: " + data);

		//  Test with valid id, invalid photo id
		printTitle("getPhoto(String, int) with valid user id, invalid photo id");
		try
		{
			data = ms2.getPhoto(id, -12345);
			out.println("getPhotos(String, int) 2: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getPhoto(String, int) 2nd test");
		}	
	}

	public static void testGetProfile() {
		//  - test with valid id
		//  - test invalid id

		JSONObject data = null;
		JSONObject obj = null;

		// First test with valid id
		printTitle("getProfile(String) with valid user id");
		data = ms2.getProfile(id);
		out.println("getProfile(String) 1: " + data);

		// 2nd test with invalid id
		printTitle("getProfile(String) with invalid user id 100");
		try
		{
			data = ms2.getProfile("100");
			out.println("getProfile(String) 2: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getProfile(String) 2nd test");
		}
	}

	public static void testGetProfile2() {
		//  - test with valid id and detailType = basic
		//  - test with valid id and detailType = full
		//  - test with valid id and detailType = extended

		JSONObject data = null;
		JSONObject obj = null;

		// Test with valid id
		printTitle("getProfile(String) with valid user id and detailType = basic");
		data = ms2.getProfile(id, "basic");
		out.println("getProfile(String) 1: " + data);

		// Test with valid id
		printTitle("getProfile(String) with valid user id and detailType = full");
		data = ms2.getProfile(id, "full");
		out.println("getProfile(String) 2: " + data);

		// Test with valid id
		printTitle("getProfile(String) with valid user id and detailType = extended");
		data = ms2.getProfile(id, "extended");
		out.println("getProfile(String) 3: " + data);
	}

	public static void testGetStatus() {
		//  - test with valid id
		//  - test invalid id

		JSONObject data = null;
		JSONObject obj = null;

		// First test with valid id
		printTitle("getStatus(String) with valid user id");
		data = ms2.getStatus(id);
		out.println("getStatus(String) 1: " + data);

		// 2nd test with invalid id
		printTitle("getStatus(String) with invalid user id 100");
		try
		{
			data = ms2.getStatus("100");
			out.println("getStatus(String) 2: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getStatus(String) 2nd test");
		}
	}

	public static void testPostStatus() {
		Object data = null;
		JSONObject obj = null;

		printTitle("postStatus(String) with valid user id");
//		data = ms2.postStatus(id, "abcdef 今天天气好 哈哈 --  ~!@#$%^&*()_+{}:\"<>?`-=[];',./");
		data = ms2.postStatus(id, "what a rainy day");
		out.println("postStatus(String) 1: '" + (String) data + "'");
	}

	public static void testPostMood() {
		Object data = null;
		JSONObject obj = null;

		printTitle("postStatus(String) with valid user id");
		data = ms2.postMood(id, 3);
		out.println("postMood(String) 1: '" + (String) data + "'");
	}

	public static void testGetFriendsStatus() {
		//  - test with valid id
		//  - test invalid id

		JSONObject data = null;
		JSONObject obj = null;

		// First test with valid id
		printTitle("getStatus(String) with valid user id");
		data = ms2.getFriendsStatus(id);
		out.println("getStatus(String) 1: " + data);

		// 2nd test with invalid id
		printTitle("getStatus(String) with invalid user id 100");
		try
		{
			data = ms2.getFriendsStatus("100");
			out.println("getStatus(String) 2: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getStatus(String) 2nd test");
		}
	}

	public static void testGetVideos() {
		// getVideos(userId, page, pageSize)
		//  - test with valid id
		//  - test invalid id

		JSONObject data = null;
		JSONObject obj = null;

		// First test with valid id
		printTitle("getVideos(String) with valid user id");
		data = ms2.getVideos(id);
		out.println("getVideos(String) 1: " + data);

		// 2nd test with invalid id
		printTitle("getVideos(String) with invalid user id 100");
		try
		{
			data = ms2.getVideos("100");
			out.println("getVideos(String) 2: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getVideo(String) 2nd test");
		}
	}

	public static void testGetVideo() {
		//  - test with valid user id, valid Video id
		//  - test with valid user id, invalid Video id
		//  - test with invalid user id, valid Video id

		JSONObject data = null;
		JSONObject obj = null;
	
		data = ms2.getVideos(id); // First get a Video id to use
		JSONArray videos = (JSONArray) data.get("videos");
		Long idLong = (Long) ((JSONObject) videos.get(0)).get("id");
		int videoId = idLong.intValue(); // Got the Video id!  Now use it...

		//  Test with valid id, valid Video id
		printTitle("getVideo(String, int) with valid user id, valid Video id");
		data = ms2.getVideo(id, videoId);
		out.println("getVideo(String, int) 1: " + data);

		//  Test with valid id, invalid Video id
		printTitle("getVideo(String, int) with valid user id, invalid Video id");
		try
		{
			data = ms2.getVideo(id, -12345);
			out.println("getVideos(String, int) 2: " + data);
		}
		catch (Exception e)
		{
			out.println("Exception occurred with getVideo(String, int) 2nd test");
		}	
	}

	public static void testGetUser() {
		JSONObject data = null;
		JSONObject obj = null;

		printTitle("getUser()");
		data = ms2.getUser();
		out.println("getUser() 1: " + data);
	}

	public static void testGetActivitiesAtom() {
		// First test with valid id
		printTitle("getActivitiesAtom(String) with valid user id");
		String activities = ms2.getActivitiesAtom(id);

		// Note: this prints out to System.out because it will change with time; not suitable for automatic verification
		System.out.println("getActivitiesAtom(String) 1: " + activities);
	}

	public static void testGetFriendsActivitiesAtom() {
		// First test with valid id
		printTitle("getFriendsActivitiesAtom(String) with valid user id");
		String activities = ms2.getFriendsActivitiesAtom(id);

		// Note: this prints out to System.out because it will change with time; not suitable for automatic verification
		System.out.println("getFriendsActivitiesAtom(String) 1: " + activities);
	}
	
	public static void testPutAppData() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("solid", "carbon");
		map.put("liquid", "mercury");
		map.put("gas", "helium");

		printTitle("putAppData()");
		Object data = ms2.putAppData(id, map);
		out.println("putAppData() 1: " + data);
	}

	public static void testGetAppData() {
		JSONObject data = null;
		JSONObject obj = null;

		printTitle("getAppData()");
		data = ms2.getAppData(id);
		out.println("getAppData() 1: " + data);
	}

	public static void testGetAppData2() {
		JSONObject data = null;
		JSONObject obj = null;

		printTitle("getAppData2()");
		data = ms2.getAppData(id, "solid;liquid");
		out.println("getAppData2() 1: " + data);
	}

	public static void testClearAppData() {
		Object data = null;

		printTitle("clearAppData()");
		data = ms2.clearAppData(id, "solid;liquid");
		out.println("clearAppData() 1: " + data);
	}

	public static void testGetFriendsAppData() throws Exception {
		String key = "http://perisphere.1939worldsfair.com/app.xml";
		String secret = "eda0c62773234093bea92645eea0493d";
			
		JSONArray data = null;
//		JSONObject data = null;

		ms = new MySpace(key, secret, ApplicationType.ON_SITE);

		// First put something
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("colour", "red");
		map.put("flavour", "spicy");
		ms.putAppData("146617378", map);
		Thread.sleep(1000);
		
		printTitle("getFriendsAppData(String) 1");
//		data = ms.getFriendsAppData("146617378");
		data = ms.getUserFriendsAppData("28568917");
//		data = ms.getFriendsAppData("456073223");
		out.println("getFriendsAppData() 1: " + data);

		printTitle("getFriendsAppData(String, String) 1");
		data = ms.getUserFriendsAppData("28568917", "colour");
		out.println("getFriendsAppData(String, String) 1: " + data);

		printTitle("getFriendsAppData(String, String) 2");
		data = ms.getUserFriendsAppData("28568917", "colour;flavour");
		out.println("getFriendsAppData(String, String) 2: " + data);
	}

/*
	public static void testFriendsGetAppData2() {
		JSONObject data = null;
		JSONObject obj = null;

		printTitle("getAppData2()");
		data = ms2.getAppData(id, "solid;liquid");
		out.println("getAppData2() 1: " + data);
	}
*/

	public static void testGetComments() {
		JSONObject data = null;
		JSONObject obj = null;

		printTitle("getComments()");
		data = ms2.getComments(id);
		out.println("getComments() 1: " + data);
	}

	public static void testGetIndicators() {
		JSONObject data = null;
		JSONObject obj = null;

		printTitle("getIndicators()");
		data = ms2.getIndicators(id);
		out.println("getIndicators() 1: " + data);
	}

	public static void testGetPreferences() {
		JSONObject data = null;
		JSONObject obj = null;

		printTitle("getPreferences()");
		data = ms2.getPreferences(id);
		out.println("getPreferences() 1: " + data);
	}

	public static void main(String[] args) throws Exception {
		MySpaceTest.testGetFriendsAppData();
		MySpaceTest.setUp();
		MySpaceTest.globalTests();
		MySpaceTest.setUp2();

		MySpaceTest.testGetAlbums();
		MySpaceTest.testGetAlbums2();
		MySpaceTest.testGetAlbum();
		MySpaceTest.testGetAlbumInfo();
		MySpaceTest.testGetAlbumPhoto();
		MySpaceTest.testGetFriends();
		MySpaceTest.testGetFriends2();
		MySpaceTest.testGetFriendsList();
		MySpaceTest.testGetFriendsList2();
		MySpaceTest.testGetFriendship();
		MySpaceTest.testGetMood();
		MySpaceTest.testGetMoods();
		MySpaceTest.testGetPhotos();
		MySpaceTest.testGetPhoto();
		MySpaceTest.testGetProfile();
		MySpaceTest.testGetProfile2();
		MySpaceTest.testGetStatus();
		MySpaceTest.testGetFriendsStatus();
		MySpaceTest.testGetVideos();
		MySpaceTest.testGetVideo();
		MySpaceTest.testGetUser();
		MySpaceTest.testGetActivitiesAtom();
		MySpaceTest.testGetFriendsActivitiesAtom();


		MySpaceTest.testPutAppData();
		// Put data may become available only after a delay, so sleep first
		Thread.sleep(1000);
		MySpaceTest.testGetAppData();
		MySpaceTest.testGetAppData2();
		MySpaceTest.testClearAppData();
		Thread.sleep(1000);
		MySpaceTest.testGetAppData(); // Fetch again to verify deletion

		MySpaceTest.testGetIndicators();

		MySpaceTest.testPostStatus();
		MySpaceTest.testPostMood();

/*	
		JSONObject data = ms2.getAlbums(id, -1, -1); // First get an album id to use
		JSONArray albums = (JSONArray) data.get("albums");
		Long idLong = (Long) ((JSONObject) albums.get(0)).get("id");
		int albumId = idLong.intValue(); // Got the album id!  Now use it...

		HashMap<String, String> map = new HashMap<String, String>();
		String x = "http://api.myspace.com/v1/users/%s/albums/%s/photos.xml";
		String url = x.replaceFirst("%s", id);
		url = url.replaceFirst("%s", Integer.toString(albumId));

		String reqUrl = ms2.server.generateRequestUrl(url, ms2.accessToken == null ? "" : ms2.accessToken.getSecret(), map);
		String response = ms2.server.doHttpReq(reqUrl);
	
		System.out.println(response);
*/
	}
}