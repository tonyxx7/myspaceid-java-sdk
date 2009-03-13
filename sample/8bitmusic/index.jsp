<%@ page import="com.myspace.myspaceid.*,com.myspace.myspaceid.oauth.*,org.json.simple.*" %>
<!--jsp:include page="userStatus.jsp"/-->

<%!
private String CONSUMER_KEY = "77f44916a5144c97ad1ddc9ec53338cc";
private String CONSUMER_SECRET = "51951d1f872c454d8932cd5f135623ae";

private  boolean isLoggedin(HttpServletRequest req) {
	return req.getSession().getAttribute("openid.oauth.request_token") != null;
}
%>

<%

// Set realm
String fullURL = request.getRequestURL().toString();
int j = fullURL.indexOf("index.jsp");
String realm = fullURL;
if (j != -1)
	realm = fullURL.substring(0, j);
System.out.println("************* " + realm);

JSONObject profile_ext_data = null;
JSONObject profile_data = null;
JSONObject friends_data = null;
String activities_data = null;
String friendsActivities_data = null;
if(isLoggedin(request) && request.getParameter("newrun") == null){
	OAuthToken token2 = new OAuthToken((String) request.getSession().getAttribute("openid.oauth.request_token"), "");
	OAuthToken accessToken = new MySpace(CONSUMER_KEY, CONSUMER_SECRET).getAccessToken(token2);

	//create new myspace object to make requests.
	MySpace ms = new MySpace(CONSUMER_KEY,CONSUMER_SECRET,ApplicationType.OFF_SITE,accessToken.getKey(),accessToken.getSecret());
	String userid = ms.getUserId();
	
	// Use the userID (fetched in the previous step) to get user's profile, friends and other info
	profile_ext_data = ms.getProfile(userid, "extended");
	profile_data = ms.getProfile(userid);
	
	friends_data = ms.getFriends(userid);
	
	activities_data = ms.getActivitiesAtom(userid);
	friendsActivities_data = ms.getFriendsActivitiesAtom(userid);
}
%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8"/>
<title>8-Bit Music</title>
	
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.6.0/build/reset/reset-min.css" />
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.6.0/build/fonts/fonts-min.css" />
<link rel="stylesheet" type="text/css" href="http://x.myspacecdn.com/modules/activity/static/css/activities_tyq3zov4.css" />
<link rel="stylesheet" type="text/css" href="static/main.css" />
<script type="text/javascript">
var msOptions = {
	api_base:'http://api.myspace.com/openid',
	realm:'<%=realm%>',
	returnTo:'finish_auth.jsp',
	consumer:'77f44916a5144c97ad1ddc9ec53338cc',
	popupSize:{ width:580, height:600}
};
</script>
<script type="text/javascript" src="static/js/myspaceid.rev.0.js" ></script>
<!--[if IE]><script type="text/javascript" src="http://remysharp.com/downloads/html5.js"></script><![endif]-->
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.3.2/jquery.js" ></script>

</head>
<body>

<div id="login" class='rounded-corner-bottom'>
	<div class='logout'>
	<span class="<%= isLoggedin(request) ? "logout_isSignedin" : "logout_isSignedout" %>" >
		<a href="logout.jsp" title="signout">[x] signout</a>
	</span>
	</div>
	
	<div class='login'>
	<span class=""<%=  isLoggedin(request) ? "login_isSignedin" : "login_isSignedout" %>">
	<b>please login:</b><br><br>
	<a href="#login" 
		class="msid__login"
		onclick="p2()">
		<img src="static/images/myspaceid.png" alt="Login with MySpaceID" />
	</a>
	</span>
	</div>
</div>

<div id="branding" class='rounded-corner-bottom'>8-Bit Music</div>

<ul id="nav">
<li><a href="/">HOME</a></li>
<li><a href="#">MAP</a></li>
<li><a href="#">SONGS</a></li>
<li><a href="#">PROFILE</a></li>
<li><a href="#">FEATURED</a></li>
</ul>

<div id="content_loggedOut" class="<%= isLoggedin(request) ? "welcome_LoggedIn" : "welcome_LoggedOut" %>">
	<div style="background-color: #CCCCCC; padding: 3px;">
		<div style="background-color: #FFFFFF; text-align: center; padding: 25px;">
			Welcome to 8-Bit Music: a community to share and discover video game tunes. Please login!<br><br>
			<img src="static/images/profile_pic.png" />
		</div>
	</div>	
</div>

<div id="content" class="<%= isLoggedin(request) ? "content_loggedIn" : "content_loggedOut" %>">
<ul id="cols">

<li id="profile" class='rounded-corner-left'>

<%
if(isLoggedin(request) && profile_ext_data!= null && profile_data != null ){

String html_aboutme = (String) (profile_data.get("aboutme") != null ? profile_data.get("aboutme") : "8-bit kid is a cool chap.  well above lorem ipsum text for his about me.  this should be filled in dynamically from the profile about me.");
String link_profilePic = (String) ((JSONObject) profile_data.get("basicprofile")).get("image");
String link_profileMorePics = "http://viewmorepics.myspace.com/index.cfm?fuseaction=user.viewAlbums&friendID=" + ((JSONObject) profile_data.get("basicprofile")).get("userId");
String link_profileMoreVids = "http://vids.myspace.com/index.cfm?fuseaction=vids.channel&channelID=" + ((JSONObject)  profile_data.get("basicprofile")).get("userId");
String link_profileMorePlay = "http://music.myspace.com/index.cfm?fuseaction=music.singleplaylist&friendid=" + ((JSONObject) profile_data.get("basicprofile")).get("userId") + "&plid=";
String html_desiretomeet = (String) profile_ext_data.get("desiretomeet");
%>
	<div id="profile-view">
	<img id="profile-pic" src="<%=link_profilePic%>" alt="<%=((JSONObject) profile_data.get("basicprofile")).get("name")%>" />
	<div id="caption">
	<div id="caption-left">
		<a href="$link_profileMorePics" style="margin-right: 3px;">pics</a>/
		<a href="$link_profileMoreVids" style="margin-left: 3px;">video</a>/
		<a href="$link_profileMorePlay" style="margin-left: 3px;">music</a>
	</div>
	<div id="caption-right">
		<img src="mock/profile_icon.png" alt="yay blue" />
		<img src="mock/level_icon.png" alt="uber" />
	</div>
	</div>
	</div>
	
	<h1><span class="display-name"><%=((JSONObject) profile_data.get("basicprofile")).get("name")%></span></h1>
	<h2><%=profile_data.get("age")%>, <%=profile_data.get("city")%> </h2>
	
	<h3>LAST LOGIN:</h3>
	<p id="member-date"><%=((JSONObject) profile_data.get("basicprofile")).get("lastUpdatedDate")%></p>
	
	<h3>HEADLINE:</h3>
	<p id="headline"><%=profile_ext_data.get("headline")%></p>
	
	<p id="about-me">
	<%=html_aboutme%>
	</p>
	
	<p id="desiretomeet">
	<%=html_desiretomeet%>
	</p>
	

<%
}else{
	out.println("PROFILE ERROR");
}
%>

</li>
<li id="basic-info">
<%
if(isLoggedin(request) && profile_ext_data != null && profile_data != null){
	
String html_name = (String) ((JSONObject) profile_data.get("basicprofile")).get("name");
String html_interests = (String) profile_ext_data.get("interests");
String html_music = (String) profile_ext_data.get("music");
String html_movies = (String) profile_ext_data.get("movies");
String html_tv = (String) profile_ext_data.get("television");
String html_books = (String) profile_ext_data.get("books");
String html_heroes = (String) profile_ext_data.get("heroes");
%>
	<h1><span class="display-name"><%=html_name%></span>'s Basic Info</h1>
	
	<h2>General:</h2>
	<p id="General"><%=html_interests%></p>
	
	<h2>Music:</h2>
	<p id="music"><%=html_music%></p>
	
	<h2>Movies:</h2>
	<p id="movies"><%=html_movies%></p>
	
	<h2>Television:</h2>
	<p id="television"><%=html_tv%></p>
	
	<h2>Books:</h2>
	<p id="books"><%=html_books%></p>
	
	<h2>Heroes:</h2>
	<p id="heroes"><%=html_heroes%></p>

<%
}else{
	out.println("EXT_PROFILE ERROR");
}
%>
</li>

<li id="activities" class='rounded-corner-right'>
<%

if(isLoggedin(request) && profile_data != null && activities_data != null && friendsActivities_data != null){
	String xml = friendsActivities_data;
	out.println(xml);
}else{
	out.println("ACTIVITIES ERROR");
}
%>
</li>
</ul>
</div>

<script>
function p2(){
	var ms = new MySpaceID(msOptions);
}

function success(tok) {
	window.location.reload();
}

function failed(rand) {
	$('#login div.logout span').addClass("logout_isSignedout");
	$('#login div.logout span').removeClass("logout_isSignedin");
}
</script>
<div id="copyright">&copy; 2008-2009. 8-Bit Music. All Rights Reserved.<br>This site supports OpenID authentication. <a href="http://www.openid.net" target="_blank">Learn More</a></div>
</body>
</html>
