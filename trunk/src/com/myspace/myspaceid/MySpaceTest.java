package com.myspace.myspaceid;

import java.io.*;
import java.net.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import com.myspace.myspaceid.*;
import com.myspace.myspaceid.oauth.*;

public class MySpaceTest {
	private static String id = null;
	private static MySpace ms2 = null;
	private static PrintStream out = System.err;
	
	public static void setUp() throws Exception {
		// Initialize
		System.out.println("Initializing user ID");

		// Account that works with this test: kiammyspace/myspace888

		String key = "77f44916a5144c97ad1ddc9ec53338cc";
		String secret = "51951d1f872c454d8932cd5f135623ae";

		MySpace ms = new MySpace(key, secret, ApplicationType.OFF_SITE);
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
		catch (MySpaceException e)
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
		catch (MySpaceException e)
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
		catch (MySpaceException e)
		{
			out.println("Exception occurred with getAlbums(String, int, int) 1st test");
		}

		printTitle("getAlbums(String, int, int) with invalid page");
		try
		{
			data = ms2.getAlbums(id, 0, 1);
			out.println("getAlbums(String, int, int) 4: " + data);
		}
		catch (MySpaceException e)
		{
			out.println("Exception occurred with getAlbums(String, int, int) 2nd test");
		}
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
		catch (MySpaceException e)
		{
			out.println("Exception occurred with getAlbums(String, int, int) 2nd test");
		}

		printTitle("getAlbum(String, int) with invalid user id and valid album id");
		try
		{
			data = ms2.getAlbum("-1", albumId);
			out.println("getAlbum(String, int) 3: " + data);
		}
		catch (MySpaceException e)
		{
			out.println("Exception occurred with getAlbums(String, int, int) 3rd test");
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
		catch (MySpaceException e)
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
		catch (MySpaceException e)
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
		catch (MySpaceException e)
		{
			out.println("Exception occurred with getFriends(String) 8th test");
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
		catch (MySpaceException e)
		{
			out.println("Exception occurred with getFriends(String) 2nd test");
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
		catch (MySpaceException e)
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
		catch (MySpaceException e)
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
		catch (MySpaceException e)
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
		catch (MySpaceException e)
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
		catch (MySpaceException e)
		{
			out.println("Exception occurred with getVideo(sString) 2nd test");
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
		catch (MySpaceException e)
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
		printTitle("getActvitiesAtom(String) with valid user id");
		String activities = ms2.getActvitiesAtom(id);

		// Note: this prints out to System.out because it will change with time; not suitable for automatic verification
		System.out.println("getActvitiesAtom(String) 1: " + activities);
	}

	public static void testGetFriendsActivitiesAtom() {
		// First test with valid id
		printTitle("getFriendsActvitiesAtom(String) with valid user id");
		String activities = ms2.getFriendsActvitiesAtom(id);

		// Note: this prints out to System.out because it will change with time; not suitable for automatic verification
		System.out.println("getFriendsActvitiesAtom(String) 1: " + activities);
	}

	public static void main(String[] args) throws Exception {
		MySpaceTest.setUp();
		MySpaceTest.testGetAlbums();
		MySpaceTest.testGetAlbums2();
		MySpaceTest.testGetAlbum();
		MySpaceTest.testGetFriends();
		MySpaceTest.testGetFriends2();
		MySpaceTest.testGetFriendship();
		MySpaceTest.testGetMood();
		MySpaceTest.testGetPhotos();
		MySpaceTest.testGetPhoto();
		MySpaceTest.testGetProfile();
		MySpaceTest.testGetProfile2();
		MySpaceTest.testGetStatus();
		MySpaceTest.testGetVideos();
		MySpaceTest.testGetVideo();
		MySpaceTest.testGetUser();
		MySpaceTest.testGetActivitiesAtom();
		MySpaceTest.testGetFriendsActivitiesAtom();
	}
}