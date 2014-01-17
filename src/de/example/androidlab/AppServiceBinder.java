package de.example.androidlab;

import android.os.Binder;

public class AppServiceBinder extends Binder {
	
	AppService appService;
	
	public AppServiceBinder(AppService appService) {
		this.appService = appService;
	}
	
	public AppService getAppService() {
		return this.appService;
	}
}
