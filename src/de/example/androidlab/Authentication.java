package de.example.androidlab;

import java.util.Observable;
import java.util.Observer;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

public class Authentication {

	private static final String TAG = "NVD_Auth";
	
	private static String OUR_CLIENT_ID = "GdvlnJjnOshxeJ3FNnXbitLUnBwbUfhunvzqD8DHZIlx2YDgUltMB2rDsu8xOTZE.apps.rwth-aachen.de";
	private static String OAUTH_URL = "https://oauth.campus.rwth-aachen.de/oauth2waitress/oauth2.svc/code";
	private static String POLL_URL = "https://oauth.campus.rwth-aachen.de/oauth2waitress/oauth2.svc/token";
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
	
    private Activity context;
	private String tok=null;
	public Authentication(Activity context) {
		this.context = context;
	}
	
	public String getAccessToken() {
		return getAppPreferences().getString(ACCESS_TOKEN, null);
	}
	
	
	
	/**
	 * This method should be called every time an activity starts, to make sure we have a valid access token.
	 * It makes necessary connections to get access token, or refresh it if necessary.
	 */
	public void prepareAccessToken() {
		//TODO: this line should be removed
		this.getAppPreferences().edit().clear().commit();
		
		if(getAccessToken()!=null) return;
		//TODO: should see if it needs to be refreshed
		
		connectToOAuthServer();
	}
	
	private void refreshToken() {
		
	}
	
	private SharedPreferences getAppPreferences() {
		return context.getSharedPreferences("NVD", 0 );
	}
	
	public boolean isAppAuthorized() {
		return getAppPreferences().getString("access_token", null) != null;
	}
	
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) private void connectToOAuthServer() {
		
		ConnectionToServer oAuthConnection = new ConnectionToServer();
		oAuthConnection.addAttribute(CLIENT_ID, OUR_CLIENT_ID);
		oAuthConnection.addAttribute(SCOPE, SCOPE_VALUE);
		oAuthConnection.setLinkToConnect(OAUTH_URL);
		oAuthConnection.setContext(this.context);
		oAuthConnection.setObserver(new Observer() {
			
			@Override
			public void update(Observable observable, Object data) {
				String result = (String) data;
				Log.d(TAG,"result returned:"+result);
				
				try{
				JSONObject json=new JSONObject(result);
				Editor editor = getAppPreferences().edit();
				editor.putString(DEVICE_CODE, json.getString(DEVICE_CODE));
				editor.putString(USER_CODE, json.getString(USER_CODE));
				editor.putString(VERIFICATION_URL, json.getString(VERIFICATION_URL));
				editor.putString(EXPIRES_IN, json.getString(EXPIRES_IN));
				editor.putString(INTERVAL, json.getString(INTERVAL));
				editor.commit();
				
				ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE); 
				ClipData clip = ClipData.newPlainText("", getAppPreferences().getString(USER_CODE, null));
				clipboard.setPrimaryClip(clip);

				
				Authentication.this.ask_for_authorization(getAppPreferences().getString(USER_CODE, null));
				
				}catch (JSONException e) {
					Log.d(TAG,"error parsing JSON after oauth");
				}
			}
		});
		oAuthConnection.execute();
	}
	
	
	private void connectToPollServer() {
		ConnectionToServer connection = new ConnectionToServer();
		connection.addAttribute(CLIENT_ID, OUR_CLIENT_ID);
		connection.addAttribute("code", getAppPreferences().getString(DEVICE_CODE, null));
		connection.addAttribute("grant_type", "device");
		connection.setLinkToConnect(POLL_URL);
		connection.setContext(this.context);
		connection.setObserver(new Observer() {
			
			@Override
			public void update(Observable observable, Object data) {
				String result = (String) data;
				Log.d(TAG,"result returned:"+result);
				
				try{
				JSONObject json=new JSONObject(result);
				Editor editor = getAppPreferences().edit();
				editor.putString(ACCESS_TOKEN, json.getString(ACCESS_TOKEN));
				editor.putString(TOKEN_TYPE, json.getString(TOKEN_TYPE));
				editor.putString(EXPIRES_IN, json.getString(EXPIRES_IN));
				editor.putString(REFRESH_TOKEN, json.getString(REFRESH_TOKEN));
				editor.commit();
				
				//TODO: what now?
				Toast.makeText(context, "token_code: "+getAppPreferences().getString(ACCESS_TOKEN, null), Toast.LENGTH_LONG).show();
				
				tok=getAppPreferences().getString(ACCESS_TOKEN, null);
				if(tok != null && tok.toString().length()>15)
				{
					//context.finish();
					//Toast.makeText(context,"OK", Toast.LENGTH_LONG).show();
					Intent i = new Intent(context.getBaseContext(),DBRoulette.class);
	                context.startActivity(i);
				}
				else
				{
					//Toast.makeText(context,"failed", Toast.LENGTH_LONG).show();
					Intent i = new Intent(context.getBaseContext(),LoginActivity.class);
	                context.startActivity(i);
				}
				
				}catch (JSONException e) {
					Log.d(TAG,"error parsing JSON after oauth");
				}
				
			}
		});
		connection.execute();
		
	}
	
	private void ask_for_authorization(String u_code) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.context);
		
		String url = getAppPreferences().getString(VERIFICATION_URL, "www.google.com");
		
	    LayoutInflater inflater = this.context.getLayoutInflater();
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
				Authentication.this.connectToPollServer();
				 //tok=getAccessToken();
				//Toast.makeText(context,tok, Toast.LENGTH_LONG).show();
				context.finish();
				
				
			}
				
		});
	    
	    
	    myAlert.show();
		
	}
}
