package edu.dartmouth.pxxzz.chatclient;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;
import edu.dartmouth.pxxzz.helper.Constants;

public class ChatClient {

	private DefaultHttpClient mHttpClient;

	public String mUserName;
	public String mPassword;

	public ChatClient(String username, String password) {
		mUserName = username;
		mPassword = password;
		mHttpClient = new DefaultHttpClient();

		mHttpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
				CookiePolicy.BROWSER_COMPATIBILITY);
		mHttpClient.getParams().setParameter(
				HttpConnectionParams.CONNECTION_TIMEOUT, 1000);
		mHttpClient.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT,
				1000);
	}

	public String sendMessage(String url, String msg) {
		String sendResult = "";
		try {
			HttpResponse response;
			HttpEntity entity;
			HttpPost httpost = new HttpPost(url);
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			nvps.add(new BasicNameValuePair(Constants.PARAM_NAME_USERNAME,
					mUserName));
			nvps.add(new BasicNameValuePair(Constants.PARAM_NAME_PASSWORD,
					mPassword));
			nvps.add(new BasicNameValuePair(Constants.PARAM_NAME_MESSAGE, msg));
			httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			response = sendRequest(mHttpClient, httpost);
			entity = response.getEntity();
			sendResult = getResponseContent(entity);
			entity.consumeContent();
			
		} catch (Exception ex) {
			Log.e("sendResult",ex.toString() );
			return sendResult = ex.getMessage();
		}

		return sendResult;
	}

	public HttpResponse sendRequest(HttpClient httpclient,
			HttpUriRequest request) throws ClientProtocolException, IOException {
		HttpResponse response = httpclient.execute(request);
		int respCode = response.getStatusLine().getStatusCode();
		if (respCode == HttpStatus.SC_MOVED_TEMPORARILY
				|| respCode == HttpStatus.SC_MOVED_PERMANENTLY) {
			Header[] headers = response.getAllHeaders();
			String location = null;
			for (Header header : headers) {
				if (header.getName().equals("Location")) {
					String redirUrl = header.getValue();
					if (redirUrl.startsWith("https://")
							|| redirUrl.startsWith("http://")) {
						location = header.getValue();
					} else {
						URL url = new URL(new URL(request.getURI()
								.toASCIIString()), redirUrl);
						location = url.toString();
					}
					break;
				}
			}

			if (location != null) {
				response.getEntity().consumeContent();
				HttpGet httpget = new HttpGet(location);
				response = sendRequest(httpclient, httpget);
			}
		}

		return response;
	}

	public void sendMessage(ChatMessage chatMessage) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("username", mUserName);
		params.put("password", mPassword);
		params.put("source", chatMessage.mUserName);
		params.put("target", chatMessage.mFriendName);
		params.put("profile_img", chatMessage.mProfileImage);
		params.put("cheese_img", chatMessage.mCheeseImage);
		params.put("nickname", chatMessage.mNickName);
		params.put("timestamp", (chatMessage.mDate.getTime() / 1000)+"");
		params.put("flag", chatMessage.mFlag);
		params.put("countdown", chatMessage.mCountDown);
		params.put("ttl",chatMessage.mTTL);
		Log.e("in chatMessage", "from" + chatMessage.mUserName +chatMessage.mNickName+" to"+ chatMessage.mFriendName+","+ chatMessage.mFlag);
		
		try {
			ServerUtilities.post(Constants.CHAT_SERVER_URL + "/"
					+ Constants.POST_MSG_ACTION_NAME,params);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	public String getResponseContent(HttpEntity entity) {
		byte[] buf = new byte[2 * 1024 * 1024];
		StringBuilder builder = new StringBuilder();
		int len = 0;
		try {
			len = entity.getContent().read(buf);
			while (len > 0) {
				builder.append(new String(buf, 0, len));
				len = entity.getContent().read(buf);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return builder.toString();
	}

}
