package com.myspace.myspaceid.oauth;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import com.myspace.myspaceid.*;
import com.myspace.myspaceid.util.*;

/** 
 * Encapsulates an OAuthServer against which we send requests for tokens, etc.
 */
public class OAuthServer {
	protected OAuthConsumer consumer;
	protected OAuthToken accessToken;

	/**
	 * Constructor.
	 * @param consumer The OAuthConsumer that we will use to query this OAuthServer.
	 */
	public OAuthServer(OAuthConsumer consumer) {
		this.consumer = consumer;
	}

	/**
	 * Sets the access token to use.
	 * @param accessToken The access token that has been obtained and which we will use against this server.
	 */
	public void setAccessToken(OAuthToken accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * Generates request URL with a given path (i.e., request_token, authorize, etc.).
	 * @param path The initial URL path, e.g., http://api.myspace.com/request_token.
	 * @return The URL generated.
	 */
    public String generateRequestUrl(String path) {
		return generateRequestUrl(path, "", new HashMap<String, String>());
	}

	/**
	 * Generates request URL with a given path (i.e., request_token, authorize, etc.).
	 * @param path The initial URL path, e.g., http://api.myspace.com/request_token.
	 * @param tokenSecret A token secret to use, if any.  This is required for obtaining the access token and for subsequent data access.
	 * @param args A Map containing any additional parameters required for this particular request.
	 * @return The URL generated.
	 */
	public String generateRequestUrl(String path, String tokenSecret, Map<String, String> args) {
        long randomNum = new Random().nextLong();
        long timestamp = (long) System.currentTimeMillis()/1000;
        args.put("oauth_consumer_key", consumer.getKey());
		args.put("oauth_nonce", Long.toString(randomNum));
        args.put("oauth_signature_method", "HMAC-SHA1");
        args.put("oauth_timestamp", Long.toString(timestamp));
        args.put("oauth_version", "1.0");
		if (accessToken != null && accessToken.getKey() != null) // If an access token has been stored, automatically pass it
			args.put("oauth_token", accessToken.getKey());
        List<String> argList = new ArrayList<String>();
        for (String key : args.keySet()) {
            String arg = key+"="+encode(args.get(key));
            argList.add(arg);
        }
        Collections.sort(argList);
        StringBuilder part3 = new StringBuilder();
        for (int i = 0; i < argList.size(); i++) {
            part3.append(argList.get(i));
            if (i != argList.size()-1) {
                part3.append("&");
            }
        }
        String part1 = "GET";
        String part2 = path;
        String baseString = encode(part1)+"&"+encode(part2)+"&"+encode(part3.toString());
		String combinedSecret = consumer.getSecret() + "&" + tokenSecret;
//System.out.println("CombinedSecret = '" + combinedSecret + "'");
        String sig = getHMACSHA1(combinedSecret, baseString);

        String result = part2+"?"+part3+"&oauth_signature="+encode(sig);
//System.out.println(result);
		return result;
    }

	/**
	 * Returns the HMAC-SHA1 signature of a given string.
	 * @param key Key (or secret) to use in obtaining the signature.
	 * @param data The string whose signature we want to compute.
	 * @return The computed signature.
	 */
    protected static String getHMACSHA1(String key, String data) {
        try {
            // get an hmac_sha1 key from the raw key bytes
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes("UTF-8"), "HMAC-SHA1");

            // get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            // compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(data.getBytes("UTF-8"));

            // base64-encode the hmac
            return Base64Util.encodeBytes(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Unable to generate HMAC-SHA1", e);
        }
    }

	/**
	 * URL-encodes the given string.
	 * @param value String to encode.
	 * @return The encoded string.
	 */
    protected static String encode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

	/**
	 * Does an HTTP request.
	 * @param urlStr URL to send request to.
	 * @return The response from the remote server.
	 */
	public String doHttpReq(String urlStr) {
		InputStream is = null;
		try	{
			URL url = new URL(urlStr);
			is = url.openStream();
		}
		catch (Exception e) {
//			e.printStackTrace();
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
//			throw new MySpaceException(e.getMessage(), MySpaceException.CONNECT_FAILED);
			throw new MySpaceException(sw.toString(), MySpaceException.CONNECT_FAILED);
		}

		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		StringBuffer sb = new StringBuffer();
		do {
			String line = null;
			try	{
				line = br.readLine();
			}
			catch (IOException e) {
				throw new MySpaceException(e.getMessage(), MySpaceException.REQUEST_FAILED);
			}

			if (line == null) 
				break;
			sb.append(line).append("\n");
		} while (true);

		String response = sb.toString();
//		System.out.println(response);
		return response;
	}
}