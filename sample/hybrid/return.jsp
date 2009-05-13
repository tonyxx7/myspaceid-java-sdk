<%@ page import="com.myspace.myspaceid.*,com.myspace.myspaceid.oauth.*,org.json.simple.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>MySpaceID OpenID-OAuth Hybrid Sample</title>
	<meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8" />
</head>
<body>
<img src='../img/mdp_logo.jpg'/><br>
<h2>Hybrid OpenID-OAuth Login Success!</h2>

	<div>
<%
    String key = "18bed0f4247a4f79bc9941bfed5b534c"; // This is set also in web.xml file
    String secret = "2e8ebdafbef844a5929086e659e4188c";

	// Callback invoked.  Authorization done.  Display user info.
	// Unlike the OAuth case, getting the access token doesn't require a new secret.
	OffsiteContext c = null;
	String accessTokenKey = (String) request.getSession().getAttribute("accessTokenKey");
	String accessTokenSecret = (String) request.getSession().getAttribute("accessTokenSecret");
	if (accessTokenKey == null || accessTokenSecret == null) {
		c = new OffsiteContext(key, secret, request.getParameter("openid.oauth.request_token"), "");
		OAuthToken accessToken = c.getAccessToken();
		request.getSession().setAttribute("accessTokenKey", accessToken.getKey());
		request.getSession().setAttribute("accessTokenSecret", accessToken.getSecret());
	}
	else {
		c = new OffsiteContext(key, secret);
		c.setAccessToken(new OAuthToken(accessTokenKey, accessTokenSecret));
	}

	// Fetch and display user ID.
	String id = c.getUserId();
	out.println("User id = " + id + "<br/>");

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
%>
	</div>

</body>
</html>
