package de.example.androidlab;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import roboguice.service.RoboService;
import roboguice.util.Ln;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.IBinder;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.UploadRequest;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.google.inject.Inject;

public class AppService extends RoboService {

	private static final String WATCH_LIST_KEY = "WatchedCourses";

	@Inject
	L2PAuthentication auth;
	@Inject
	L2PServices srv;
	@Inject
	SharedPreferences pref;

	ScheduledThreadPoolExecutor scheduledExecutor;
	ThreadPoolExecutor downloadereExecutor;
	ThreadPoolExecutor dropboxExecutor;

	
	Map<String,String> courseMap;
	Set<String> watchedCourses;

	public void stopMe() {
		stopSelf();
		scheduledExecutor.shutdown();
	}

	// called once during the service life cycle only when it is created.
	@Override
	public void onCreate() {
		super.onCreate();
		Ln.v("AppService.onCreat()");

		scheduledExecutor = new ScheduledThreadPoolExecutor(1);
		downloadereExecutor = new ScheduledThreadPoolExecutor(1);
		dropboxExecutor = new ScheduledThreadPoolExecutor(1);

		watchedCourses = pref.getStringSet(WATCH_LIST_KEY, null);
		if (watchedCourses == null)
			watchedCourses = new HashSet<String>();

		scheduledExecutor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {

				try {
					Ln.v("Getting List of courses for watched courses");

					for (String courseId : watchedCourses) {
						SoapObject courseList = srv.getDocumentsOverview(
								getToken(), courseId);
						Ln.v("SOAP object of courses of %s:", courseId);
						Ln.v(courseList.toString());
					}

				} catch (AppException e) {
					Ln.e(e,
							"Exception happended while updating course list of watched courses");
				}

			}
		}, 0, 4, TimeUnit.SECONDS);

		// TODO search for all classes listed in documentation to replace them
		// with Robo?? version
	}

	public void setWatchedCourses(Set<String> newWatchList) {
		pref.edit().putStringSet(WATCH_LIST_KEY, newWatchList).commit();
		watchedCourses = newWatchList;
	}

	public Set<String> getWatchedCourses() {
		return watchedCourses;
	}

	// No need to implement this if bound model is used ( Service should end if
	// there is no client)
	// But as long as we want to be able to run in background even when all
	// activities finish
	// then this method should be implemented
	// TODO Our Service should also make notifications if L2P or Dropbox are
	// disconnected.

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Ln.v("AppService.onStartCommand()");
		return super.onStartCommand(intent, flags, startId);
	}

	// Only called when the first client bounds. Any client that bounds
	// afterwards will get the same IBinder Object.
	@Override
	public IBinder onBind(Intent arg0) {
		return new AppServiceBinder(AppService.this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		scheduledExecutor.shutdown();
	}

	public boolean isDeviceRegistered() {
		return auth.isDeviceRegistered();
	}

	public void ping() {
		Ln.v("AppService.ping()");
	}

	public String getToken() throws AppException {
		return auth.getAccessToken();
	}

	public void clearL2pTokens() {
		auth.clearAuthentication();
	}

	public String getAuthrizationURL() throws AppException {
		auth.registerDevice();
		String url = auth.getAuthorizationURL();
		return url;
	}

	public SoapObject l2pService_downloadFile(String courseId, String fileId)
			throws AppException {
		SoapObject toRet = srv.downloadDocumentItem(getToken(), courseId,
				fileId);
		return toRet;
	}

	public SoapObject l2pService_listOfFilesofCourse(String courseId)
			throws AppException {
		return srv.getDocumentsOverview(getToken(), courseId);
	}
	
	
	public String getNameForCourseById(String id) {
		if(courseMap == null) {
			try {
				l2pService_allCourses();
			} catch (AppException e) {
				Ln.e(e,"Exception while calling list of courses");
			}
		}
		return  courseMap.get(id); 
	}

	public List<Course> l2pService_allCourses() throws AppException {
		// TODO save file id and courses to a map for lookup, also for method
		// getCourseNameforId()
		// TODO : save via JSONObject ot preferences (shared)
		courseMap = new HashMap<String, String>();
		
		ArrayList<Course> courses = new ArrayList<Course>();
		SoapObject result = srv.getCourseList(getToken());
		int count = result.getPropertyCount();
		
		for (int i = 0; i < count; i++) {
			SoapObject current = (SoapObject) result.getProperty(i);
			String title = current.getPropertyAsString("Title");
			String id = current.getPropertyAsString("ID");
			Course course = new Course(title, id);
			courseMap.put(id, title);
			courses.add(course);
		}
		return courses;
	}

	public void requestDownloadRequest(final String courseId,
			final String fileId) {
		downloadereExecutor.execute(new Runnable() {

			@Override
			public void run() {

				Ln.v("downloading from download executor");
				try {
					SoapObject obj = srv.downloadDocumentItem(getToken(),
							courseId, fileId);

					String name = getNameForCourseById(courseId);
					String fileName = obj.getPropertyAsString("filename");
					String data = obj.getPropertyAsString("filedata");
					byte[] btDataFile = android.util.Base64.decode(data,
							android.util.Base64.DEFAULT);
					// String dir_path = String.format("%s/l2p_to_temp/%s",
					// Environment.getExternalStorageDirectory().getPath());
					String dir_path = String.format("/sdcard/l2p_to_temp/%s",
							name);
					File applicationDirectory = new File(dir_path);
					if (!applicationDirectory.exists())
						applicationDirectory.mkdirs(); // return false, if
														// folder already exists
					File file = new File(applicationDirectory + "/" + fileName);

					Ln.v("file path to save:%s", file.getAbsolutePath());
					FileOutputStream fos = null;
					fos = new FileOutputStream(file);
					fos.write(btDataFile);
					fos.close();

					
					requestUploadToDrobbox(name, fileName); // TODO : upload by dropbox execurotor
 					

				} catch (AppException e) {
					Ln.e(e,"App exception while downloading the file");
				} catch (IOException e) {
					Ln.e(e,"IOException while downloading the file");
					e.printStackTrace();
				}

			}
		});
	}

	public void requestUploadToDrobbox(String courseName, String fileName) {

		Ln.v("requestUpload(%s,%s) was called", courseName, fileName);

		try {

			File f = new File(String.format("/sdcard/l2p_to_temp/%s/%s",
					courseName, fileName));
			FileInputStream fis = new FileInputStream(f);

			final String APP_KEY = "u7wjc7dtt7nzvmm";
			final String APP_SECRET = "1shsa0ki6188si9";
			final AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
			final String ACCOUNT_PREFS_NAME = "prefs";
			final String ACCESS_KEY_NAME = "ACCESS_KEY";
			final String ACCESS_SECRET_NAME = "ACCESS_SECRET";
			AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);

			String[] stored = null;

			SharedPreferences prefs = getSharedPreferences(ACCOUNT_PREFS_NAME,
					0);
			String key = prefs.getString(ACCESS_KEY_NAME, null);
			String secret = prefs.getString(ACCESS_SECRET_NAME, null);
			if (key != null && secret != null) {
				stored = new String[2];
				stored[0] = key;
				stored[1] = secret;
			}

			AndroidAuthSession session;

			if (stored != null) {
				AccessTokenPair accessToken = new AccessTokenPair(stored[0],
						stored[1]);
				session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE,
						accessToken);
			} else {
				session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
			}

			DropboxAPI<AndroidAuthSession> mApi;

			mApi = new DropboxAPI<AndroidAuthSession>(session);
			UploadRequest mRequest = mApi.putFileOverwriteRequest(
					String.format("%s/%s", courseName, fileName), fis,
					f.length(), null);

			mRequest.upload();

		} catch (IOException e) {
			Ln.e(e, "Error while uploading to dropbox");
		} catch (DropboxException e) {
			Ln.e(e, "Error while uploading to dropbox");
		}

	}

}
