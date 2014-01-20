package de.example.androidlab;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

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
import android.widget.Button;
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

		bindService(intent2, connection, BIND_AUTO_CREATE);

		this.createDeveloperMenu();
	}

	@Override
	protected void onDestroy() {
		unbindService(connection);
		super.onDestroy();
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

	public void doDeviceRegistrationAndAuthorization() {

		new AppAsyncTask<Void, String>(this, "Please Wait",
				"Registering Device") {
			@Override
			protected String doInBackground(Void... params) {
				try {
					String url = getAppService().getAuthrizationURL();
					return url;
				} catch (AppException e) {
					Ln.e(e,
							"Exception During Device registration and authorization");
					// TODO Application exceptions should not only be logged,
					// but also do something about it
				} catch (Exception e) {
					Ln.e(e,
							"Exception During Device registration and authorization");
				}
				return null;
			}

			@Override
			protected void onPostExecute(String url) {
				super.onPostExecute(url);
				AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(
						BaseActivity.this);
				
				LayoutInflater inflater = BaseActivity.this.getLayoutInflater();
				View dialogView = inflater.inflate(R.layout.customdialog, null);
				dialogBuilder.setView(dialogView);
				WebView wv = (WebView) dialogView.findViewById(R.id.webView1);
				Button button = (Button) dialogView
						.findViewById(R.id.closeAfterAuthorize);

				wv.loadUrl(url);
				wv.setWebViewClient(new WebViewClient() {
					@Override
					public boolean shouldOverrideUrlLoading(WebView view,
							String url) {
						view.loadUrl(url);
						return true;
					}
				});
				
				final AlertDialog myAlert = dialogBuilder.create();

				button.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						myAlert.dismiss();
					}
				});

				myAlert.show();

			}
		}.execute();
	}

	private void createDeveloperMenu() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Developer Menu");
		CharSequence[] items = { "register Device", "ping sevice",
				"clear l2p tokens", "Stop Watching", "start watch list activity",
				"Add Sample Course To Watch List" };
		builder.setItems(items, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					doDeviceRegistrationAndAuthorization();

					break;
				case 1:
					getAppService().ping();
					break;
				case 2:
					getAppService().clearL2pTokens();
					break;
				case 3:
					getAppService().setStillWatching(false);
					break;
				case 4:
					Intent i = new Intent(BaseActivity.this,
							WatchCoursesSelectionActivity.class);
					BaseActivity.this.startActivity(i);
					break;
				case 5:
					Set<String> watchedIds = new HashSet<String>();
					watchedIds.add("13ws-40107");
					getAppService().setWatchedCourses(watchedIds);
					break;
				default:
					break;
				}

			}
		});

		developerMenu = builder.create();
	}

}
