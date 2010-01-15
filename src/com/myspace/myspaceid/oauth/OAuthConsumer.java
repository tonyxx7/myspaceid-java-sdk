package com.myspace.myspaceid.oauth;

import java.util.TimeZone;

/**
 * Class that encapsulates an OAuth consumer.
 */
public class OAuthConsumer
{
	protected String 	key;
	protected String 	secret;
	protected TimeZone	timeZone;  // Added by Shashi 10/08/2009 
								   // Calculate oauth_timestamp for given timezone 
	                               // otherwise OAuth server will consider default TimeZone 
	
	public OAuthConsumer(String key, String secret,TimeZone timeZone) 
	{
		this.key      = key;
		this.secret   = secret;
		this.timeZone = timeZone;
	}
	protected TimeZone	timeZone;  // Added by Shashi 10/08/2009 
								   // Calculate oauth_timestamp for given timezone 
	                               // otherwise OAuth server will consider default TimeZone 


	public OAuthConsumer(String key, String secret,TimeZone timeZone) 
	{
		this.key = key;
		this.secret = secret;

	public OAuthConsumer(String key, String secret) 
	{
		this(key,secret,TimeZone.getDefault());
		this.timeZone = timeZone;
	}

	public OAuthConsumer(String key, String secret) 
	{
		this(key,secret,TimeZone.getDefault());
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
	



	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public String toString() {
		return "OAuthConsumer: key = " + key + " , secret = " + secret;
	}
}