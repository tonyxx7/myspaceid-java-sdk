<%@ page import="com.myspace.myspaceid.*,com.myspace.myspaceid.oauth.*,org.json.simple.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>MySpaceID OAuth Sample</title>
	<meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8" />
</head>
<body>
<img src='../img/mdp_logo.jpg'/><br>
<%
  String callback = request.getParameter("callback");

  String key = "18bed0f4247a4f79bc9941bfed5b534c";
  String secret = "2e8ebdafbef844a5929086e659e4188c";

  MySpace ms = new MySpace(key, secret);

  // If callback parameter no supplied, redirect to go do login.
  if (callback == null || callback.equals("")) {
	OAuthToken token = ms.getRequestToken();

	String authorizeUrl = ms.getAuthorizationURL(token, request.getRequestURL() + "?callback=true");
	request.getSession().setAttribute("requestTokenSecret", token.getSecret());
	response.sendRedirect(authorizeUrl);
  }
  else {
	// Check if final MySpace object already available
	MySpace ms2 = (MySpace) request.getSession().getAttribute("ms2");
	if (ms2 == null) {
		// Callback has just been invoked from MySpace after authorization.
		String newTokenKey = request.getParameter("oauth_token");
		System.out.println("Using new token: " + newTokenKey);
	
		// To get access token, use the new request token returned in the Callback URL, and use the secret returned with the original request token.
		String requestTokenSecret = (String) request.getSession().getAttribute("requestTokenSecret");
		OAuthToken token2 = new OAuthToken(newTokenKey, requestTokenSecret);
		OAuthToken accessToken = ms.getAccessToken(token2);
	
		// Now that we have the access token, create a MySpace object using the "mature" constructor (with 4 arguments).  
		// This object lets us fetch user data.
		ms2 = new MySpace(key, secret, ApplicationType.OFF_SITE, accessToken.getKey(), accessToken.getSecret());
		request.getSession().setAttribute("ms2", ms2);
	}
	
	// Fetch and display user ID.
	String id = ms2.getUserId();
	out.println("<br/><br/>User id = " + id + "<br/>");

	// Fetch and display user's name.
	JSONObject friends = ms2.getFriends(id);
	JSONObject obj = (JSONObject) friends.get("user");
	out.println("<h2>" + obj.get("name") + "'s friends:</h2>");

	// Fetch and display user's friends.
	Object f = friends.get("Friends");
	JSONArray fa = (JSONArray) f;
	for (int i = 0; i < fa.size(); i++) {
		JSONObject friend = (JSONObject) fa.get(i);
		out.println("<img height='75' src='" + friend.get("largeImage") + "'/><br/>");
		out.println(friend.get("name") + "<br/><br/>");
	}
  }
%>
</body>
</html>