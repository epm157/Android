/*
 * Copyright (c) 2010-11 Dropbox, Inc.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

package de.example.androidlab;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ksoap2.serialization.SoapObject;

import roboguice.util.Ln;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.android.AuthActivity;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;

public class DBRoulette extends BaseActivity implements API_Listener {
	private static final String TAG = "DBRoulette";

	// /////////////////////////////////////////////////////////////////////////
	// Your app-specific settings. //
	// /////////////////////////////////////////////////////////////////////////

	// Replace this with your app key and secret assigned by Dropbox.
	// Note that this is a really insecure way to do this, and you shouldn't
	// ship code which contains your key & secret in such an obvious way.
	// Obfuscation is good.
	final static private String APP_KEY = "u7wjc7dtt7nzvmm";
	final static private String APP_SECRET = "1shsa0ki6188si9";

	// If you'd like to change the access type to the full Dropbox instead of
	// an app folder, change this value.
	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;

	// /////////////////////////////////////////////////////////////////////////
	// End app-specific settings. //
	// /////////////////////////////////////////////////////////////////////////

	// You don't need to change these, leave them alone.
	final static private String ACCOUNT_PREFS_NAME = "prefs";
	final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
	final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";

	static int num = 0;
	DropboxAPI<AndroidAuthSession> mApi;

	private boolean mLoggedIn;
	// Android widgets
	public  final Activity mcontext = this;
	Button l2p2dropbox;
	Button dropbox2l2p;
	Button l2p2device;
	Button device2l2p;
	// Button link;
	Button autoSync;
	private String addCourse="false";
	private Button mSubmit;

	final static private int NEW_PICTURE = 1;
	private String mCameraFileName;

	File[] listFile;
	private int index;
	
	ArrayList<String> f = new ArrayList<String>();
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// mApi.getSession().startAuthentication(DBRoulette.this);

		// We create a new AuthSession so that we can use the Dropbox API.
		AndroidAuthSession session = buildSession();
		mApi = new DropboxAPI<AndroidAuthSession>(session);

		// Basic Android widgets
		setContentView(R.layout.main);
		checkAppKeySetup();
		
		

		index=0;
		Intent x = this.getIntent();
		int flag = x.getIntExtra("flag", 0);
		addCourse=x.getStringExtra("addcourse");
		if(addCourse!=null)
			if(addCourse.startsWith("t"))
			{
				getCourseList(1);
			}
		
		String[] addresses = x.getStringArrayExtra("addresses");
		if (flag == 10) {
			String path = "/sdcard/l2p/";

			File folder = new File(path);
			
			listFile = folder.listFiles();
			for (int i = 0; i < listFile.length; i++) {

				f.add(listFile[i].getAbsolutePath());

			}

			Upload upload = new Upload(Constants.UploadPhotos_Code,
					DBRoulette.this, mApi, "", listFile);
			upload.execute();
			//Toast.makeText(getApplicationContext(),String.valueOf(addresses.length), Toast.LENGTH_SHORT).show();
		}
		if(flag==2)
		{
			Bundle b=this.getIntent().getExtras();
			if(b != null)
			{
				ArrayList<Parcelable> Materials=b.getParcelableArrayList("materials");
				Toast.makeText(getApplicationContext(),"Size: "+Materials.size(), Toast.LENGTH_LONG).show();
			}
			
			
			ArrayList<File> fileArray=new ArrayList<File>();
			ArrayList<String> test = x.getStringArrayListExtra("test");
			for(int i=0;i<test.size();i++)
			{
				File t = new File(test.get(i));
				fileArray.add(t);
				
				//listFile[index++]=t;
				
				//Toast.makeText(getApplicationContext(),test.get(i), Toast.LENGTH_LONG).show();
			}
				
			//listFile = (File[]) fileArray.toArray();
			File[] fSorted = fileArray.toArray(new File[fileArray.size()]);
			for(int i=0;i<fSorted.length;i++)
				Toast.makeText(getApplicationContext(),fSorted[i].toString(), Toast.LENGTH_LONG).show();
			Upload upload = new Upload(Constants.UploadPhotos_Code,DBRoulette.this, mApi, "", fSorted);
			upload.execute();
		}

		mSubmit = (Button) findViewById(R.id.link);
		mSubmit.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// Toast.makeText(getApplicationContext(), "msg msg",
				// Toast.LENGTH_LONG).show();
				// This logs you out if you're logged in, or vice versa
				if (mLoggedIn) {
					logOut();
					mSubmit.setText("Link Device to Dropbox");

				} else {
					// Start the remote authentication
					mApi.getSession().startAuthentication(DBRoulette.this);
					// mSubmit.setText("Unlink Device from Dropbox");
				}
			}
		});

		dropbox2l2p = (Button) findViewById(R.id.dropbox2ltp);
		dropbox2l2p.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// This logs you out if you're logged in, or vice versa
				 Intent i = new Intent(getBaseContext(),ChooserActivity.class);
				 startActivity(i);
			}
		});

		l2p2dropbox = (Button) findViewById(R.id.l2p2dropbox);
		l2p2dropbox.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// This logs you out if you're logged in, or vice versa
				Intent i = new Intent(getBaseContext(),
						CourseListActivity.class);
				startActivity(i);
			}
		});
		l2p2device=(Button) findViewById(R.id.l2p2device);
		l2p2device.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				getCourseList(0);
			}
		});
		
		device2l2p = (Button) findViewById(R.id.device2l2p);
		device2l2p.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// This logs you out if you're logged in, or vice versa
				Intent i = new Intent(getBaseContext(),
						ExplorerActivity.class);
				startActivity(i);

			}
		});
		
		autoSync=(Button) findViewById(R.id.autoSync);
		autoSync.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// This logs you out if you're logged in, or vice versa
				Intent i = new Intent(getBaseContext(),
						WatchCoursesSelectionActivity.class);
				startActivity(i);

			}
		});

		setLoggedIn(mApi.getSession().isLinked());
		// checkLoggedIn();

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("mCameraFileName", mCameraFileName);
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		super.onResume();
		AndroidAuthSession session = mApi.getSession();

		// The next part must be inserted in the onResume() method of the
		// activity from which session.startAuthentication() was called, so
		// that Dropbox authentication completes properly.
		if (session.authenticationSuccessful()) {
			try {
				// Mandatory call to complete the auth
				session.finishAuthentication();

				// Store it locally in our app for later use
				TokenPair tokens = session.getAccessTokenPair();
				storeKeys(tokens.key, tokens.secret);
				setLoggedIn(true);

			} catch (IllegalStateException e) {
				showToast("Couldn't authenticate with Dropbox:"
						+ e.getLocalizedMessage());
				Log.i(TAG, "Error authenticating", e);
			}
		}
	}

	// This is what gets called on finishing a media piece to import
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == NEW_PICTURE) {
			// return from file upload
			if (resultCode == Activity.RESULT_OK) {
				Uri uri = null;
				if (data != null) {
					uri = data.getData();
				}
				if (uri == null && mCameraFileName != null) {
					uri = Uri.fromFile(new File(mCameraFileName));
				}
				// File file = new File("/data/aa/s.gif");
				//
				// if (uri != null) {
				// UploadPicture upload = new UploadPicture(this, mApi,
				// PHOTO_DIR, file);
				// upload.execute();
				// }
			} else {
				Log.w(TAG, "Unknown Activity Result from mediaImport: "
						+ resultCode);
			}
		}
	}

	private void logOut() {
		// Remove credentials from the session
		mApi.getSession().unlink();

		// Clear our stored keys
		clearKeys();
		// Change UI state to display logged out version
		setLoggedIn(false);
	}

	/**
	 * Convenience function to change UI state based on being logged in
	 */
	private void setLoggedIn(boolean loggedIn) {
		mLoggedIn = loggedIn;
		if (mLoggedIn) {
			mSubmit.setText("Unlink from Dropbox");
			dropbox2l2p.setEnabled(true);
			l2p2dropbox.setEnabled(true);
			// mUpload.setEnabled(true);

		} else {
			mSubmit.setText("Link with Dropbox");
			// mUpload.setEnabled(false);
			dropbox2l2p.setEnabled(false);
			l2p2dropbox.setEnabled(false);
		}
	}

	public void checkLoggedIn() {
		if (mLoggedIn) {
			mSubmit.setText("Unlink from Dropbox");
			dropbox2l2p.setEnabled(true);
			l2p2dropbox.setEnabled(true);
			// mUpload.setEnabled(true);
		} else {
			mSubmit.setText("Link with Dropbox");
			dropbox2l2p.setEnabled(false);
			l2p2dropbox.setEnabled(false);
			// mUpload.setEnabled(false);
		}
	}

	private void checkAppKeySetup() {
		// Check to make sure that we have a valid app key
		if (APP_KEY.startsWith("CHANGE") || APP_SECRET.startsWith("CHANGE")) {
			showToast("You must apply for an app key and secret from developers.dropbox.com, and add them to the DBRoulette ap before trying it.");
			finish();
			return;
		}

		// Check if the app has set up its manifest properly.
		Intent testIntent = new Intent(Intent.ACTION_VIEW);
		String scheme = "db-" + APP_KEY;
		String uri = scheme + "://" + AuthActivity.AUTH_VERSION + "/test";
		testIntent.setData(Uri.parse(uri));
		PackageManager pm = getPackageManager();
		if (0 == pm.queryIntentActivities(testIntent, 0).size()) {
			showToast("URL scheme in your app's "
					+ "manifest is not set up correctly. You should have a "
					+ "com.dropbox.client2.android.AuthActivity with the "
					+ "scheme: " + scheme);
			finish();
		}
	}

	public void getCourseList(final int num)
	{

		
		AsyncTask<Void, Void, List<Course>> task = new AsyncTask<Void, Void, List<Course>>(){
			ProgressDialog pd;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pd = ProgressDialog.show(DBRoulette.this, "Please Wait", "Getting List Of Courses");
			}
			
			@Override
			protected List<Course> doInBackground(Void... params) {
				return getAppService().l2pService_allCourses();
			}
			
			
			@Override
			protected void onPostExecute(List<Course> result) {
				super.onPostExecute(result);
				pd.dismiss();
			
				
				Bundle b = new Bundle();
				b.putParcelableArrayList("rooms", (ArrayList<Course>) result);
				Intent i = new Intent(DBRoulette.this,CourseListActivity.class);
				i.putExtras(b);
				if(num==1)
				{
					Ln.v("DB Autosync");
					i.putExtra("addcourse", "true");
					DBRoulette.this.startActivity(i);
					finish();
				}
					
				else
				{
					Ln.v("DB not-Autosync");
					i.putExtra("addcourse", "false");
					DBRoulette.this.startActivity(i);
				}
					
			}
		};
		task.execute();
		
	}
	private void showToast(String msg) {
		Toast error = Toast.makeText(this, msg, Toast.LENGTH_LONG);
		error.show();
	}

	/**
	 * Shows keeping the access keys returned from Trusted Authenticator in a
	 * local store, rather than storing user name & password, and
	 * re-authenticating each time (which is not to be done, ever).
	 * 
	 * @return Array of [access_key, access_secret], or null if none stored
	 */
	private String[] getKeys() {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		String key = prefs.getString(ACCESS_KEY_NAME, null);
		String secret = prefs.getString(ACCESS_SECRET_NAME, null);
		if (key != null && secret != null) {
			String[] ret = new String[2];
			ret[0] = key;
			ret[1] = secret;
			return ret;
		} else {
			return null;
		}
	}

	/**
	 * Shows keeping the access keys returned from Trusted Authenticator in a
	 * local store, rather than storing user name & password, and
	 * re-authenticating each time (which is not to be done, ever).
	 */
	private void storeKeys(String key, String secret) {
		// Save the access key for later
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.putString(ACCESS_KEY_NAME, key);
		edit.putString(ACCESS_SECRET_NAME, secret);
		edit.commit();
	}

	private void clearKeys() {
		SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.clear();
		edit.commit();
	}

	private AndroidAuthSession buildSession() {
		AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session;

		String[] stored = getKeys();
		if (stored != null) {
			AccessTokenPair accessToken = new AccessTokenPair(stored[0],
					stored[1]);
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE,
					accessToken);
		} else {
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
		}

		return session;
	}

	@Override
	public void onSuccess(int requestnumber, Object obj) {

		try {
			if (requestnumber == Constants.UploadPhotos_Code) {
				boolean sucess = (Boolean) obj;
				if (sucess) {
					Toast.makeText(DBRoulette.this,
							"Files Syncronized successfully", Toast.LENGTH_LONG)
							.show();

					File directory = new File(
							"/sdcard/l2p_to_dropbox_syncronizer");
					File destination = new File("/sdcard/l2p");
					//copyFiles(directory, destination);
					if (directory.exists()) {
						try {
							//delete(directory);
						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					//Intent i = new Intent(DBRoulette.this, DBRoulette.class);
					//startActivity(i);
					//finish();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onFail(String errormessage) {
		// TOD Auto-generated method stub

	}

	public static void copyFiles(File sourceLocation, File targetLocation)
			throws IOException {

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}
			File[] files = sourceLocation.listFiles();
			for (File file : files) {
				InputStream in = new FileInputStream(file);
				OutputStream out = new FileOutputStream(targetLocation + "/"
						+ file.getName());

				// Copy the bits from input stream to output stream
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			}
		}
	}

	public static void delete(File file) throws IOException {

		if (file.isDirectory()) {

			// directory is empty, then delete it
			if (file.list().length == 0) {

				file.delete();
			} else {

				// list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);

					// recursive delete
					delete(fileDelete);
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					// file.delete();

				}
			}

		} else {
			// if file, then delete it
			file.delete();
			System.out.println("File is deleted : " + file.getAbsolutePath());
		}
	}

}
