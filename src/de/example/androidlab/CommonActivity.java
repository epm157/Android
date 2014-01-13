package de.example.androidlab;


import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

public class CommonActivity extends Activity {
	
	
	private Context ctx=this;
	private AlertDialog developerMenu;
	
	protected SharedPreferences getAppPreferences() {
        return getSharedPreferences("NVD", 0 );
}
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle("L2P-Dropbox");
    	getActionBar().setIcon(R.drawable.l2p_logo);
    	this.createDeveloperMenu();
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    public void show(String toShow) {
		Toast.makeText(this, toShow, Toast.LENGTH_LONG).show();
	}
    
    public void show(String toShow, int times) {
		Toast toast = Toast.makeText(this, toShow, Toast.LENGTH_LONG);
		for(int i=0;i<times;i++) toast.show();
	}
    
    protected void log(String toLog) {
		Log.d("L2P", toLog);
	}
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.action_developer_menu:
			developerMenu.show();
			break;

		default:
			show("menu item was not found");
			break;
		}
    	
    	return true;
    }
    
    
    
    private void createDeveloperMenu() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Developer Menu");
    	CharSequence [] items = { "register Device","show token", "clear token","check validity of token" ,"refresh token","download sample file"};
    	builder.setItems(items, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					regDeviceTemp();
					break;
				case 1:
					break;
				case 2:
					getAppPreferences().edit().clear().commit();
					break;
				case 3:
					break;
				case 4:
					break;
				case 5:
					downloadSampleFile();
					break;
				default:
					break;
				}
				
			}
		});
    	
    	developerMenu = builder.create();
    }
    
    
    private void regDeviceTemp() {
    	
    	AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
    		ProgressDialog pd;
    		@Override
    		protected void onPreExecute() {
    			super.onPreExecute();
    			pd = ProgressDialog.show(CommonActivity.this, "wait", "registering Device");
    		}
    		
    		@Override
    		protected String doInBackground(Void... params) {
    			L2P_Services srv = new L2P_Services(getAppPreferences());
    			String url = null;
				try {
					url = srv.getAuthorizationURL();
				} catch (CommonException e) {
					super.cancel(true);
				}
    			return url;
    		}
    		
    		@Override
    		protected void onCancelled() {
    			super.onCancelled();
    			AlertDialog.Builder builder = new AlertDialog.Builder(CommonActivity.this)
    			.setMessage("Cancelled")
    			.setNeutralButton("close", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
    			builder.create().show();
    		}
    		
    		@Override
    		protected void onPostExecute(String result) {
    			super.onPostExecute(result);
    			pd.dismiss();
    	    	
    			AlertDialog.Builder alertDialog = new AlertDialog.Builder(CommonActivity.this);
    			
    			WebView wv = new WebView(CommonActivity.this);
    			alertDialog.setView(wv);
    			alertDialog.setNeutralButton("close", new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						
					}
				});
    		    wv.loadUrl(result);
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
    		    myAlert.show();
    		}
    	};
    	
    	task.execute();

    }
    
	
    private void downloadSampleFile() {
    	
    	AsyncTask<Void, Void, SoapObject> task = new AsyncTask<Void, Void, SoapObject>(){
			ProgressDialog pd;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pd = ProgressDialog.show(CommonActivity.this, "Please Wait", "Getting sample file");
			}
			
			@Override
			protected SoapObject doInBackground(Void... params) {
				L2P_Services tempService=new L2P_Services(getAppPreferences());
				SoapObject obj=null;
					try {
						obj = tempService.downloadDocumentItem("13ws-40107", "2");
						Log.d("NAVID",String.valueOf(obj.toString().length()));
						Log.d("NAVID",obj.getPropertyAsString("filename"));
					} catch (CommonException e) {
						// TODO handle error, top level
						e.printStackTrace();
					}
				return obj;
			}
			
			
			@Override
			protected void onPostExecute(SoapObject result) {
				super.onPostExecute(result);
				pd.dismiss();
			}
		};
		task.execute();
    	
    }
	
}
