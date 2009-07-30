<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<title>Login -- MySpaceID OpenID-OAuth Hybrid Sample</title>
	<meta http-equiv="Content-Type" content="application/xhtml+xml; charset=UTF-8" />
</head>
<body>
<img src='../img/Myspaceid.jpg'/><br>
<h2>MySpaceID OpenID-OAuth Hybrid Sample</h2>
<form action="consumer" name="f">
<table>
<tr><td><img src="/myspaceid-sample/img/openid.jpg"/></td><td></td></tr>
<tr><td>Enter your OpenID login: </td><td><input type="text" name="openid_identifier" size="60"/> </td></tr>
<tr><td></td><td><a href="javascript:document.f.submit();"><img border="0" src="../img/Blue_150_Loginwithmyspaceid.png"/></a></td></tr>
<tr><td></td><td>(<a target=_blank href="http://wiki.developer.myspace.com/index.php?title=MySpaceID_Logo_Kit">logo kit</a>)</td></tr>
</table>
</form>
</body>
</html>
<%
	request.getSession().setAttribute("accessTokenKey", null);
	request.getSession().setAttribute("accessTokenSecret", null);
%>
