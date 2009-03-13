<%@ page import="com.myspace.myspaceid.*,com.myspace.myspaceid.oauth.*,org.json.simple.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8"/>
<title>8-Bit Music</title>
<body>
<%
request.getSession().setAttribute("openid.oauth.request_token", null);
%>
You are now logged out.  <a href='index.jsp'>Click here to return to sample.</a>
</body>
</html>