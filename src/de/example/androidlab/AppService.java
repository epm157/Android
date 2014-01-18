package de.example.androidlab;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.ksoap2.serialization.SoapObject;

import roboguice.service.RoboService;
import roboguice.util.Ln;
import android.content.Intent;
import android.os.IBinder;

import com.google.inject.Inject;

public class AppService extends RoboService {

	@Inject
	L2PAuthentication auth;
	@Inject
	L2PServices srv;

	ScheduledThreadPoolExecutor scheduledExecutor;
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
					Ln.e(e,"Exception happended while updating course list of watched courses");
				}

			}
		}, 0, 4, TimeUnit.SECONDS);

		// TODO search for all classes listed in documentation to replace them
		// with Robo?? version
	}
	
	
	
	public void addCourseToWatchList(String courseId) {
		watchedCourses.add(courseId);
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
		addCourseToWatchList("13ws-40107");
	}

	public String getToken() throws AppException {
		return auth.getAccessToken();
	}

	public void clearL2pTokens() {
		auth.clearAuthentication();
	}

	public SoapObject downloadDocumentItem(String courseId, String fileId)
			throws AppException {
		SoapObject toRet = srv.downloadDocumentItem(getToken(), courseId,
				fileId);
		return toRet;
	}

	public L2PAuthentication getAuth() {
		return auth;
	}

	public L2PServices getSrv() {
		return srv;
	}

	public String getAuthrizationURL() throws AppException {
		auth.registerDevice();
		String url = auth.getAuthorizationURL();
		return url;
	}

	public SoapObject getDocumentsOverview(String courseId) throws AppException {
		return srv.getDocumentsOverview(getToken(), courseId);
	}

	public SoapObject getCourseList() throws AppException {
		return srv.getCourseList(getToken());
	}

}
