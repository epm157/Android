package de.example.androidlab;

import java.util.Observable;
import java.util.Observer;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class Authentication {

	private void showAuthenticationRequirementDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this.commonActivity);
		builder.setMessage("The application is not Authenticated with L2P!")
		       .setCancelable(false)
		       .setPositiveButton("Authenticate now!", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	
	private static final String TAG = "NVD_Auth";
	
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
	
    private CommonActivity commonActivity;
	private String tok=null;
	public Authentication(CommonActivity commonActivity) {
		this.commonActivity = commonActivity;
	}
	
	public String getAccessToken() {
		return getAppPreferences().getString(ACCESS_TOKEN, null);
	}
	
	public void clearAccessToken() {
		this.getAppPreferences().edit().clear().commit();
	}
	
	public void prepareAccessToken() {
		String token = this.getAccessToken();
		//TODO
		
		if(getAccessToken()!=null) return;
		registerDevice();
	}
	
	public void refreshAccessToken() {
		ConnectionToServer connection = new ConnectionToServer();
		connection.addAttribute(CLIENT_ID, OUR_CLIENT_ID);
		connection.addAttribute(REFRESH_TOKEN, getAppPreferences().getString(REFRESH_TOKEN, null));
		connection.addAttribute("grant_type", REFRESH_TOKEN);
		connection.setLinkToConnect(POLL_URL);
		connection.setContext(this.commonActivity);
		connection.setObserver(new Observer() {
			
			@Override
			public void update(Observable observable, Object data) {
				String result = (String) data;
				try{
				JSONObject json=new JSONObject(result);
				Editor editor = getAppPreferences().edit();
				editor.putString(ACCESS_TOKEN, json.getString(ACCESS_TOKEN));
				editor.putString(TOKEN_TYPE, json.getString(TOKEN_TYPE));
				editor.putString(EXPIRES_IN, json.getString(EXPIRES_IN));
				editor.commit();
				commonActivity.show(json.toString());
				}catch (JSONException e) {
					//TODO fill catch code for all catch's in this class
				}
				
			}
		});
		connection.execute();
	}
	
	//TODO : if expires_in is less than 50 then refresh token should be called
	public void checkAccessTokenValidity() {
		ConnectionToServer connection = new ConnectionToServer();
		connection.addAttribute(CLIENT_ID, OUR_CLIENT_ID);
		connection.addAttribute(ACCESS_TOKEN, getAccessToken());
		connection.setLinkToConnect(VALIDITY_URL);
		connection.setContext(this.commonActivity);
		connection.setObserver(new Observer() {
			
			@Override
			public void update(Observable observable, Object data) {
				String result = (String) data;
				
				try{
				JSONObject json=new JSONObject(result);
				commonActivity.show(json.toString());
				
				}catch (JSONException e) {
				}
				
			}
		});
		connection.execute();
		
	}
	
	
	private SharedPreferences getAppPreferences() {
		return commonActivity.getSharedPreferences("NVD", 0 );
	}
	
	public boolean isAppAuthorized() {
		return getAppPreferences().getString("access_token", null) != null;
	}
	
	
	public void registerDevice() {
		
		ConnectionToServer oAuthConnection = new ConnectionToServer();
		oAuthConnection.addAttribute(CLIENT_ID, OUR_CLIENT_ID);
		oAuthConnection.addAttribute(SCOPE, SCOPE_VALUE);
		oAuthConnection.setLinkToConnect(OAUTH_URL);
		oAuthConnection.setContext(this.commonActivity);
		oAuthConnection.setObserver(new Observer() {
			
			@Override
			public void update(Observable observable, Object data) {
				String result = (String) data;
				
				try{
				JSONObject json=new JSONObject(result);
				commonActivity.show(json.toString());
				Editor editor = getAppPreferences().edit();
				editor.putString(DEVICE_CODE, json.getString(DEVICE_CODE));
				editor.putString(USER_CODE, json.getString(USER_CODE));
				editor.putString(VERIFICATION_URL, json.getString(VERIFICATION_URL));
				editor.putString(EXPIRES_IN, json.getString(EXPIRES_IN));
				editor.putString(INTERVAL, json.getString(INTERVAL));
				editor.commit();
				
				ClipboardManager clipboard = (ClipboardManager) commonActivity.getSystemService(Context.CLIPBOARD_SERVICE); 
				ClipData clip = ClipData.newPlainText("", getAppPreferences().getString(USER_CODE, null));
				clipboard.setPrimaryClip(clip);

				
				Authentication.this.ask_for_authorization(getAppPreferences().getString(USER_CODE, null));
				
				}catch (JSONException e) {
				}
			}
		});
		oAuthConnection.execute();
	}
	
	
	public void requestAccessToken() {
		ConnectionToServer connection = new ConnectionToServer();
		connection.addAttribute(CLIENT_ID, OUR_CLIENT_ID);
		connection.addAttribute("code", getAppPreferences().getString(DEVICE_CODE, null));
		connection.addAttribute("grant_type", "device");
		connection.setLinkToConnect(POLL_URL);
		connection.setContext(this.commonActivity);
		connection.setObserver(new Observer() {
			
			@Override
			public void update(Observable observable, Object data) {
				String result = (String) data;
				Log.d(TAG,"result returned:"+result);
				
				try{
				JSONObject json=new JSONObject(result);
				commonActivity.show(json.toString());
				
				Editor editor = getAppPreferences().edit();
				editor.putString(ACCESS_TOKEN, json.getString(ACCESS_TOKEN));
				editor.putString(TOKEN_TYPE, json.getString(TOKEN_TYPE));
				editor.putString(EXPIRES_IN, json.getString(EXPIRES_IN));
				editor.putString(REFRESH_TOKEN, json.getString(REFRESH_TOKEN));
				editor.commit();
				
				}catch (JSONException e) {
				}
				
			}
		});
		connection.execute();
		
	}
	
	private void ask_for_authorization(String u_code) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.commonActivity);
		
		String url = getAppPreferences().getString(VERIFICATION_URL, "www.google.com");
		
	    LayoutInflater inflater = this.commonActivity.getLayoutInflater();
	    alertDialog.setTitle(u_code);
	    
	    View dialogView = inflater.inflate(R.layout.customdialog, null);
	    alertDialog.setView(dialogView);
	    WebView wv = (WebView) dialogView.findViewById(R.id.webView1);
	    wv.loadUrl(url);
	    
	    wv.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                view.loadUrl(url);

                return true;
            }
        });
	    final AlertDialog myAlert = alertDialog.create(); //returns an AlertDialog from a Builder.
	    
	    Button button = (Button) dialogView.findViewById(R.id.afterAuthorize);
	    button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				myAlert.dismiss();
				Authentication.this.requestAccessToken();
			}
				
		});
	    myAlert.show();
	}
}
