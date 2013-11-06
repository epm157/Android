package de.example.androidlab;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class CommonActivity extends Activity {
	
	private Authentication authentication;
	private AlertDialog developerMenu;
	
	
	public Authentication getAuthentication() {
		return authentication;
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle("L2P-Dropbox");
    	getActionBar().setIcon(R.drawable.l2p_logo);
    	authentication = new Authentication(this);
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
    	CharSequence [] items = { "register Device","show token", "clear token","check validity of token" ,"refresh token","sync"};
    	builder.setItems(items, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					authentication.registerDevice();
					break;
				case 1:
					show(authentication.getAccessToken());
					break;
				case 2:
					authentication.clearAccessToken();
					break;
				case 3:
					authentication.checkAccessTokenValidity();
					break;
				case 4:
					authentication.refreshAccessToken();
					break;
				case 5:
					show("sync");
					break;
				default:
					break;
				}
				
			}
		});
    	
    	developerMenu = builder.create();
    }
    
    
    
    
	
	
}
