<%@ page import="com.myspace.myspaceid.*,com.myspace.myspaceid.oauth.*" %>
<%
String CONSUMER_KEY = "18bed0f4247a4f79bc9941bfed5b534c";
String CONSUMER_SECRET = "2e8ebdafbef844a5929086e659e4188c";

//	OAuthToken token2 = new OAuthToken((String) request.getSession().getAttribute("openid.oauth.request_token"), "");
//System.out.println("*************" + (String) request.getSession().getAttribute("openid.oauth.request_token"));
//   MySpace ms = new MySpace(CONSUMER_KEY, CONSUMER_KEY);
//	OAuthToken accessToken = ms.getAccessToken(token2);
//System.out.println("*************2222222" + accessToken.getKey());

// Get access token
OAuthToken token2 = new OAuthToken(request.getParameter("openid.oauth.request_token"), "");
OAuthToken accessToken = new MySpace(CONSUMER_KEY, CONSUMER_SECRET).getAccessToken(token2);
request.getSession().setAttribute("accessToken", accessToken);
%>
<html>
<head>
<title>8-Bit Music</title>
	
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.6.0/build/reset/reset-min.css" />
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.6.0/build/fonts/fonts-min.css" />
<link rel="stylesheet" type="text/css" href="http://x.myspacecdn.com/modules/activity/static/css/activities_tyq3zov4.css" />
<link rel="stylesheet" type="text/css" href="/static/main.css" />

<script type="text/javascript" src="/static/js/myspaceid.rev.0.js" ></script>
<!--[if IE]><script type="text/javascript" src="http://remysharp.com/downloads/html5.js"></script><![endif]-->
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.js" ></script>

</head>
<body>

<script>
function closeWin() {
  window.opener.success();
  self.close();
}
</script>



    <h1>Finishing Log In</h1>



<script>closeWin();</script>
  </body>
</html>
