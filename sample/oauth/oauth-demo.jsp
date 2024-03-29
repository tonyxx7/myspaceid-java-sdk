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

  // If callback parameter not supplied, redirect to go do login.
  if (callback == null || callback.equals("")) {

	request.getSession().setAttribute("accessTokenKey", null);
	request.getSession().setAttribute("accessTokenSecret", null);

	OffsiteContext c1 = new OffsiteContext(key, secret);
	OAuthToken requestToken = c1.getRequestToken(request.getRequestURL() + "?callback=true");
	request.getSession().setAttribute("requestTokenSecret", requestToken.getSecret());
	System.out.println(requestToken);
	String authorizeUrl = c1.getAuthorizationURL(requestToken);
	response.sendRedirect(authorizeUrl);
  }
  else {
	// Check if already authorized
	String accessTokenKey = (String) request.getSession().getAttribute("accessTokenKey");
	String accessTokenSecret = (String) request.getSession().getAttribute("accessTokenSecret");

	if (accessTokenKey == null || accessTokenSecret == null) {
		String requestTokenKey = request.getParameter("oauth_token");
		String oauthVerifier = request.getParameter("oauth_verifier");
		String requestTokenSecret = (String) request.getSession().getAttribute("requestTokenSecret");
	
		OffsiteContext c2 = new OffsiteContext(key, secret, requestTokenKey, requestTokenSecret);
		OAuthToken accessToken = c2.getAccessToken(oauthVerifier); // Side effect: sets access token in OffsiteContext object
		
		accessTokenKey = accessToken.getKey();
		accessTokenSecret = accessToken.getSecret();
	
		request.getSession().setAttribute("accessTokenKey", accessTokenKey);
		request.getSession().setAttribute("accessTokenSecret", accessTokenSecret);
	}

	// Since we have the access token from before, set it into the OffsiteContext object
	OffsiteContext c = new OffsiteContext(key, secret);
	c.setAccessToken(new OAuthToken(accessTokenKey, accessTokenSecret));

	// Fetch and display user ID.
	String id = c.getUserId();
	out.println("<br/><br/>User id = " + id + "<br/>");

	// Fetch and display user's name.
	RestV1 r = new RestV1(c);
	JSONObject friends = r.getFriends(id);
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