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

  String key = "77f44916a5144c97ad1ddc9ec53338cc";
  String secret = "51951d1f872c454d8932cd5f135623ae";

  MySpace ms = new MySpace(key, secret);

  // If callback parameter no supplied, redirect to go do login.
  if (callback == null || callback.equals("")) {
	OAuthToken token = ms.getRequestToken();

	String authorizeUrl = ms.getAuthorizationURL(token, request.getRequestURL() + "?callback=true");
	request.getSession().setAttribute("requestTokenSecret", token.getSecret());
	response.sendRedirect(authorizeUrl);
  }
  else {
    // Callback invoked.  Authorization done.  Display user info.
	String newTokenKey = request.getParameter("oauth_token");
	System.out.println("Using new token: " + newTokenKey);

	// To get access token, use the new request token returned in the Callback URL, and use the secret returned with the original request token.
	String requestTokenSecret = (String) request.getSession().getAttribute("requestTokenSecret");
	OAuthToken token2 = new OAuthToken(newTokenKey, requestTokenSecret);
	OAuthToken accessToken = ms.getAccessToken(token2);

	// Now that we have the access token, create a MySpace object using the "mature" constructor (with 4 arguments).  
	// This object lets us fetch user data.
	MySpace ms2 = new MySpace(key, secret, accessToken.getKey(), accessToken.getSecret());

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