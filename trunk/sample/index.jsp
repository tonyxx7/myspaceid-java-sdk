<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>MySpaceID Samples</title>
	<meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8" />
</head>
<body>
<a href='http://developer.myspace.com'><img border='0' src='img/Myspaceid.jpg'/></a><br>
<h2>MySpaceID Samples</h2>
<ul>
<li><a href="oauth/oauth-login.jsp">OAuth login sample</a></li>
<li><a href="hybrid/hybrid-login.jsp">OpenID-OAuth Hybrid login sample</a></li>
<li><a href="8bitmusic">OpenID-OAuth Hybrid using Directed ID (8bitmusic sample)</a></li>
</ul>

<%
String requestURL = request.getRequestURL().toString();
if (!requestURL.startsWith("http://localhost:9090")) {
%>
<font color="#ff0000">
WARNING: Out of the box, the sample consumer key used by these samples has been configured to work on a host and path of "<b>http://localhost:9090/myspaceid-sample/</b>" (realm is configured as http://localhost:9090). It looks like you are using a URL of "<b><%=request.getRequestURL()%></b>", which will not work.  To get these samples working, please first re-configure your app server to use the correct host.  Ultimately, you will need to create and configure your own applications at the <a href='http://developer.myspace.com'>MySpace developer site</a> using <a href='http://developerwiki.myspace.com/index.php?title=How_to_Set_Up_a_New_Application_for_OpenID'>the instructions here</a>.
</font>
<%
}
%>

</body>
</html>