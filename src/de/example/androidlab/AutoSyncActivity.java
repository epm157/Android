package de.example.androidlab;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class AutoSyncActivity extends BaseActivity {
	
	private ListView listView;
	RoomArrayAdapter adapter;
	Button addCourse;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto_sync);
		
		
		listView = (ListView) findViewById(R.id.listView);
		
		listView.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				return false;
			}
		}); 
		
		Set<String> pairs = new HashSet<String>();
		SharedPreferences app_preferences =	PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		Set<String> strs=app_preferences.getStringSet("courses", null);
		
		
			ArrayList<String> l2pRoomslist=new ArrayList<String>();
			if(strs != null)
			{
				int i=0;
				for(String s:strs)
				{
					l2pRoomslist.add(s.substring(10));
				}
			}
				
					
			adapter = new RoomArrayAdapter(this, R.layout.room_list_item, l2pRoomslist);
			if(l2pRoomslist.size()>0)
				listView.setAdapter(adapter);
		
        addCourse=(Button) findViewById(R.id.addcourse);
        addCourse.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				 Intent i = new Intent(getBaseContext(),DBRoulette.class);
				 i.putExtra("addcourse", "true");
				 startActivity(i);
				 finish();
			}
		});
		
	}

}



class RoomArrayAdapter extends ArrayAdapter<String> 
{
	//private static final String tag = "L2pRoomArrayAdapter";
	private Context context;
	private List<String> objects;
	
	public RoomArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.objects = objects;
	}

	public int getCount() {
		return this.objects.size();
	}

	public String getItem(int index) {
		return this.objects.get(index);
	}

	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View row = convertView;
		if (row == null) {
			//Log.d(tag, "Starting XML Row Inflation ... ");
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
			row = inflater.inflate(R.layout.room_list_item, parent, false);
			//Log.d(tag, "Successfully completed XML Row Inflation!");
		}
		TextView tw1 = (TextView)row.findViewById(R.id.courseItemTextView);
		String str = ((String)objects.get(position));
		tw1.setText(str);
		return row;
	}
}



/*
 
  Set<String> pairs = new HashSet<String>();
		pairs.add("13ws-38186");
		pairs.add("13ws-38186");
		
		SharedPreferences app_preferences =	PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = app_preferences.edit();
		editor.putStringSet("courses", pairs);
		editor.commit();

*/