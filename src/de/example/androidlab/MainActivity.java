package de.example.androidlab;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.Menu;
import android.widget.Button;

public class MainActivity extends BaseActivity {
	SharedPreferences preferences;
	Button l2p2dropbox;
	Button dropbox2l2p;
	Button l2p2device;
	Button device2l2p;
	Button link;
	Button pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		preferences= PreferenceManager.getDefaultSharedPreferences(this);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
