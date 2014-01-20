package de.example.androidlab;

//TODO search for all classes listed in documentation to replace them with Robo?? version

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.ksoap2.serialization.SoapObject;

import roboguice.service.RoboService;
import roboguice.util.Ln;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.text.format.DateFormat;

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
	private static final int WATCHED_COURSES_LOOKUP_DELAY = 15;
	private boolean stillWatching;

	ThreadPoolExecutor downloadereExecutor;
	ThreadPoolExecutor dropboxExecutor;

	Map<String, String> courseMap;
	Set<String> watchedCourses;

	public void setStillWatching(boolean b) {
		stillWatching = b;
	}

	public void shutDownExecutors() {
		Ln.v("Shutting Down Executors");
		scheduledExecutor.shutdown();
		downloadereExecutor.shutdown();
		dropboxExecutor.shutdown();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		shutDownExecutors();
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

		stillWatching = true;
		scheduledExecutor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {

				if (!stillWatching)
					return;

				Ln.v("Getting List of Material for watched courses");

				for (String courseId : watchedCourses) {
					List<MaterialItem> materialsOfThisCourse = l2pService_listOfFilesofCourse(courseId);

					Ln.v("Looking into course with id: %s", courseId);
					for (MaterialItem currentItem : materialsOfThisCourse) {
						requestFileDownload(courseId, currentItem.getId(),
								currentItem.getLastUpdated());
					}
				}
			}
		}, 0, WATCHED_COURSES_LOOKUP_DELAY, TimeUnit.SECONDS);

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

	public List<MaterialItem> l2pService_listOfFilesofCourse(String courseId) {

		SoapObject result = null;
		try {
			result = srv.getDocumentsOverview(getToken(), courseId);
		} catch (AppException e) {
			Ln.e(e, "Exception while getting list of files for courseId: %s",
					courseId);
		}

		ArrayList<MaterialItem> materials = new ArrayList<MaterialItem>();

		int count = result.getPropertyCount();
		for (int i = 0; i < count; i++) {
			SoapObject first = (SoapObject) result.getProperty(i);
			String idd = first.getPropertyAsString("Id");
			String name = first.getPropertyAsString("Name");
			String url = first.getPropertyAsString("Url");
			String ft = first.getPropertyAsString("FileType");
			String lu = first.getPropertyAsString("LastUpdated").toString(); // TODO
																				// because
																				// we
																				// have
																				// lastupdate,
																				// we
																				// should
																				// save
																				// and
																				// use
																				// it
																				// for
																				// later
																				// checks
			String state = "0";
			MaterialItem lr = new MaterialItem(idd, name, url, ft, lu, state);
			materials.add(lr);
		}

		return materials;

	}

	public String getNameForCourseById(String id) {
		if (courseMap == null)
			l2pService_allCourses();
		return courseMap.get(id);
	}

	public List<Course> l2pService_allCourses() {

		// TODO : save via JSONObject ot preferences (shared)

		ArrayList<Course> courses = new ArrayList<Course>();
		SoapObject result = null;
		try {
			result = srv.getCourseList(getToken());
		} catch (AppException e) {
			Ln.e(e, "Exception while getting list of all courses");
		}
		int count = result.getPropertyCount();

		for (int i = 0; i < count; i++) {
			SoapObject current = (SoapObject) result.getProperty(i);
			String title = current.getPropertyAsString("Title");
			String id = current.getPropertyAsString("ID");
			Course course = new Course(title, id);
			courses.add(course);
		}

		// if no exception happened save new list of courses to map :
		courseMap = new HashMap<String, String>();
		for (Course c : courses)
			courseMap.put(c.getId(), c.getTitle());

		return courses;
	}

	public void requestFileDownload(final String courseId, final String fileId,
			final String lastUpdated) {

		Ln.v("requestFileDownload(%s,%s,%s)", courseId, fileId, lastUpdated);

		Date newDate = stringToDate(lastUpdated);
		String fileKey = courseId + fileId;
		String previous = pref.getString(fileKey, null);
		if (previous != null && stringToDate(previous).equals(newDate)) {
			Ln.v("[courseId:%s and fileId:%s] no need to download again",
					courseId, fileId);
			return;
		}

		Ln.v("adding to downloader [courseId:%s and fileId:%s]",
				courseId, fileId);
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

					requestUploadToDrobbox(name, fileName, lastUpdated,
							courseId, fileId);

				} catch (AppException e) {
					Ln.e(e, "App exception while downloading the file");
				} catch (IOException e) {
					Ln.e(e, "IOException while downloading the file");
				}

			}
		});
	}

	private Date stringToDate(String str) {

		try {

			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			return format.parse(str);

		} catch (ParseException e) {
			Ln.e(e, "Exception Parsing date: <%s>", str);
			return null;
		}
	}

	public void requestUploadToDrobbox(final String courseName,
			final String fileName, String lastUpdate, String courseId,
			String fileId) {
		// TODO : upload by dropbox execurotor
		Ln.v("requestUploadToDrobbox(%s,%s,%s) ", courseName, fileName,
				lastUpdate);

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

			// if no exception occured while uploading to dropbox, then put file
			// key in sharedPreferences
			String fileKey = courseId + fileId;
			pref.edit().putString(fileKey, lastUpdate).commit();

		} catch (IOException e) {
			Ln.e(e,
					"Error while uploading to dropbox the file with courseName:%s and fileName:%s",
					courseName, fileName);
		} catch (DropboxException e) {
			Ln.e(e,
					"Error while uploading to dropbox the file with courseName:%s and fileName:%s",
					courseName, fileName);
		}

	}

}
