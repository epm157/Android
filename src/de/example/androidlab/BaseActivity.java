package de.example.androidlab;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.ksoap2.serialization.SoapObject;

import roboguice.activity.RoboActivity;
import roboguice.util.Ln;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;

public class BaseActivity extends RoboActivity {
	
	
	private AlertDialog developerMenu;
	private AppService appService;  
	private ServiceConnection connection;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = new Intent(getApplicationContext(), AppService.class);
		startService(intent);
		
		Intent intent2 = new Intent(getApplicationContext(), AppService.class);
		connection = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				Ln.v("onServiceDisconnected()");
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Ln.v("onServiceConnected()");
				AppServiceBinder binder = (AppServiceBinder) service;
				appService = binder.getAppService();
			}
		};
				
		bindService(intent2,connection , BIND_AUTO_CREATE);
        
    	this.createDeveloperMenu();
    }
    
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	unbindService(connection);
    }
    
    protected AppService getAppService() {
    	return appService;
    }
   
    


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.action_developer_menu:
			developerMenu.show();
			break;

		default:
			Ln.v("menu item was not found");
			break;
		}
    	
    	return true;
    }
    
    
	public void doDeviceRegistrationAndAuthorization() throws AppException {
		
		new AppAsyncTask<Void, String>(this, "Please Wait", "Registering Device") {
			@Override
			protected String doInBackground(Void... params) {
				try {
					String url = getAppService().getAuthrizationURL();
					return url;
				} catch (AppException e) {
					Ln.v(e,"Exception During Device registration and authorization");
					//TODO Application exceptions should not only be logged, but also do something about it
				} catch (Exception e) {
					Ln.v(e,"Exception During Device registration and authorization");
				}
				return null;
			}
			
			
			@Override
			protected void onPostExecute(String url) {
				super.onPostExecute(url);
				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BaseActivity.this);
				LayoutInflater inflater = BaseActivity.this.getLayoutInflater();
	            View dialogView = inflater.inflate(R.layout.customdialog, null);
	            dialogBuilder.setView(dialogView);
	            WebView wv = (WebView) dialogView.findViewById(R.id.webView1);
				wv.loadUrl(url);
				wv.setWebViewClient(new WebViewClient() {
					@Override
					public boolean shouldOverrideUrlLoading(WebView view, String url) {
						view.loadUrl(url);
						return true;
					}
				});
				final AlertDialog myAlert = dialogBuilder.create();
				myAlert.show();
			}
		}.execute();
	}
    
    
    
    private void createDeveloperMenu() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle("Developer Menu");
    	CharSequence [] items = { "register Device","ping sevice", "clear l2p tokens","Stop Service" ,"start watch list","download sample file"};
    	builder.setItems(items, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					regDeviceTemp();
					break;
				case 1:
					getAppService().ping();
					break;
				case 2:
					getAppService().clearL2pTokens();
					break;
				case 3:
					getAppService().stopMe();
					break;
				case 4:
					Intent i = new Intent(BaseActivity.this, WatchCoursesSelectionActivity.class);
					BaseActivity.this.startActivity(i);
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
    	try {
			doDeviceRegistrationAndAuthorization();
		} catch (AppException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    private void downloadSampleFile() {
    	
    	AsyncTask<Void, Void, SoapObject> task = new AsyncTask<Void, Void, SoapObject>(){
			ProgressDialog pd;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pd = ProgressDialog.show(BaseActivity.this, "Please Wait", "Getting sample file");
			}
			
			@Override
			protected SoapObject doInBackground(Void... params) {
				SoapObject obj=null;
					try {
						obj = getAppService().l2pService_downloadFile("13ss-23347", "2");
						String fileName = obj.getPropertyAsString("filename");
						String data = obj.getPropertyAsString("filedata");
						byte[] btDataFile=android.util.Base64.decode(data, android.util.Base64.DEFAULT);
						File file = new File("/sdcard/l2p_to_temp/"+fileName);
						FileOutputStream fos = null;
			            fos = new FileOutputStream(file);
			            fos.write(btDataFile);
			            fos.close();

					} catch (AppException e) {
						// TODO handle error, top level
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
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

