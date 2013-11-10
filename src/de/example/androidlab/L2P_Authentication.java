package de.example.androidlab;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;


public class L2P_Authentication {
	
	private static String OUR_CLIENT_ID = "GdvlnJjnOshxeJ3FNnXbitLUnBwbUfhunvzqD8DHZIlx2YDgUltMB2rDsu8xOTZE.apps.rwth-aachen.de";
	private static String OAUTH_URL = "https://oauth.campus.rwth-aachen.de/oauth2waitress/oauth2.svc/code";
	private static String POLL_URL = "https://oauth.campus.rwth-aachen.de/oauth2waitress/oauth2.svc/token";
	private static String VALIDITY_URL = "https://oauth.campus.rwth-aachen.de/oauth2waitress/oauth2.svc/tokeninfo";
	private static String SCOPE_VALUE = "l2p.rwth";
	private static final String CLIENT_ID = "client_id";
    private static final String DEVICE_CODE = "device_code";
    private static final String USER_CODE = "user_code";
    private static final String VERIFICATION_URL = "verification_url";
    private static final String EXPIRES_IN = "expires_in";
    private static final String INTERVAL = "interval";
    private static final String SCOPE = "scope";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String TOKEN_TYPE = "token_type";
    private static final String REFRESH_TOKEN = "refresh_token";
	
    private static final String TAG = L2P_Authentication.class.getSimpleName();
    
    SharedPreferences pref;
    
    public L2P_Authentication(SharedPreferences pref) {
    	this.pref = pref;
	}
    
    
    public void registerDevice() throws CommonException{
    	AuthenticationRequestBuilder request = new AuthenticationRequestBuilder()
    	.addAttribute(CLIENT_ID, OUR_CLIENT_ID)
    	.addAttribute(SCOPE, SCOPE_VALUE);
    	JSONObject json = request.connectToServer(OAUTH_URL);
    	readJson(json, DEVICE_CODE, USER_CODE, VERIFICATION_URL, EXPIRES_IN, INTERVAL);		
    	pref.edit().remove(ACCESS_TOKEN).commit(); //remove old access token
    }
    
	private void requestAccessToken() throws CommonException {
		AuthenticationRequestBuilder request = new AuthenticationRequestBuilder()
		.addAttribute(CLIENT_ID, OUR_CLIENT_ID)
		.addAttribute("code", pref.getString(DEVICE_CODE, null))
		.addAttribute("grant_type", "device");
		JSONObject json = request.connectToServer(POLL_URL);
		readJson(json, ACCESS_TOKEN,TOKEN_TYPE, EXPIRES_IN, REFRESH_TOKEN);
	}
	
	private void checkAccessTokenValidity() throws CommonException {
		AuthenticationRequestBuilder request = new AuthenticationRequestBuilder()
		.addAttribute(CLIENT_ID, OUR_CLIENT_ID)
		.addAttribute(ACCESS_TOKEN, pref.getString(ACCESS_TOKEN, null));
		JSONObject json = request.connectToServer(VALIDITY_URL);
		readJson(json, "state", EXPIRES_IN, SCOPE,"audience" ,"status");
	}
    
	private void refreshAccessToken() throws CommonException{
		AuthenticationRequestBuilder request = new AuthenticationRequestBuilder()
		.addAttribute(CLIENT_ID, OUR_CLIENT_ID)
		.addAttribute(REFRESH_TOKEN, pref.getString(REFRESH_TOKEN, null))
		.addAttribute("grant_type", REFRESH_TOKEN);
		JSONObject json = request.connectToServer(POLL_URL);
		readJson(json, ACCESS_TOKEN, TOKEN_TYPE, EXPIRES_IN);

	}
    
	//TODO what happens if we have device_code but user de-authorizes the app?
	public String getAccessToken() throws CommonException {
		if ( pref.getString(ACCESS_TOKEN, null) == null) {
			if(pref.getString(DEVICE_CODE, null)==null) {
				throw new CommonException(AuthenticationErrors.APP_IS_NOT_AUTHORIZED); 
			}
			else { 
				requestAccessToken();
			}
		} else {
			checkAccessTokenValidity();
			String expire = pref.getString(EXPIRES_IN, null);
			if( Integer.parseInt(expire) < 30 ) refreshAccessToken();
		} 
		
		return pref.getString(ACCESS_TOKEN, null);
	}
	
	
	private void readJson(JSONObject json,String ... keys) throws CommonException {
		try {
		Editor editor = pref.edit();
		for(String key:keys){
			String value = json.getString(key);
			if(value.equals("null")) throw new CommonException(AuthenticationErrors.APP_IS_NOT_AUTHORIZED);
			editor.putString(key,value );
		}
		editor.commit();
		}catch (JSONException e) {
			throw new CommonException(AuthenticationErrors.CANNOT_PARSE_RETURNED_JSON) 
			.addRuntimeValue("Json Object", json);
		}
	}
	
	
	private class AuthenticationRequestBuilder{
		List<NameValuePair> attributes;
		public AuthenticationRequestBuilder() {
			attributes = new ArrayList<NameValuePair>();
		}
		
		public AuthenticationRequestBuilder addAttribute(String key, String value) {
			attributes.add(new BasicNameValuePair(key,value));
			return this;
		}
		
		
		public JSONObject connectToServer(String linkToConnect) {
			JSONObject result = null;
			try {
				HttpClient client = new DefaultHttpClient();
				HttpPost post = new HttpPost(linkToConnect);					
				post.setEntity(new UrlEncodedFormEntity(attributes));
				HttpResponse response = client.execute(post);
				StatusLine status = response.getStatusLine();
				if (status.getStatusCode() != 200) {
					throw new HttpResponseException(
		                    status.getStatusCode(),
		                    status.getReasonPhrase());
				}
				HttpEntity entity = response.getEntity();
				InputStream ist = entity.getContent();
				ByteArrayOutputStream content = new ByteArrayOutputStream();
				int count = 0;
				byte[] buff = new byte[1024];
				while ((count = ist.read(buff)) != -1)
					content.write(buff, 0, count);
				result = new JSONObject( new String(content.toByteArray()) );
			
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
	}
}

