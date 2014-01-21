package de.example.androidlab;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import roboguice.util.Ln;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.google.inject.Inject;

public class WatchCoursesSelectionActivity extends BaseActivity {

	RoomArrayAdapter adapter;
	List<Course> l2pRoomslist;
	Set<String> watchedCoursesIds;
	SharedPreferences app_preferences;
	SharedPreferences.Editor editor;
	Button submitWatchList;
	//@InjectView(R.id.watched_course_selection_list)
	private ListView listView;
	
	//@InjectView(R.id.submit_watchList)
	private Button submitButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.watched_course_selection);
		
		Intent x = this.getIntent();
        Bundle b=this.getIntent().getExtras();
		ArrayList<Parcelable> rooms=b.getParcelableArrayList("rooms");
		l2pRoomslist=new ArrayList<Course>();
		l2pRoomslist.clear();
    	for(int i=0;i<rooms.size();i++)
    	{
    		Course lm=(Course)rooms.get(i);
    		l2pRoomslist.add(lm);
    	}
    	watchedCoursesIds=new HashSet<String>();
    	 app_preferences =  PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	 editor= app_preferences.edit();
    	 if(app_preferences.getStringSet("courses", null) != null)
    	 {
    		 watchedCoursesIds=app_preferences.getStringSet("courses", null);
    	 }
    		 
    	 if(watchedCoursesIds == null)
    	 {
    		watchedCoursesIds=new HashSet<String>();
 	    	Toast.makeText(getApplicationContext(),"No course for auto sync", Toast.LENGTH_SHORT).show();
    	 }
    	 else
    	 {
    		 Toast.makeText(getApplicationContext(),"Number of courses for syncing:  "+watchedCoursesIds.size(), Toast.LENGTH_SHORT).show();
    	 }
    	 
		
		listView = (ListView) findViewById(R.id.watched_course_selection_list); 
		 adapter = new RoomArrayAdapter(getApplicationContext(), R.layout.room_list_item_checkbox, l2pRoomslist);
	        
		if(listView == null ) Ln.d("it is null"); else Ln.d("it is NOT null");
		listView.setAdapter(adapter);
		
		
		submitWatchList=(Button)findViewById(R.id.submit_watchList);
   	 	submitWatchList.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				editor.clear();
				editor.putStringSet("courses", watchedCoursesIds);
				editor.commit();
				finish();
			}
		});
	}

	private class RoomArrayAdapter extends ArrayAdapter<Course> 
    {
		private Context context;
		private List<Course> objects;
		
		public RoomArrayAdapter(Context context, int textViewResourceId, List<Course> objects) {
			super(context, textViewResourceId, objects);
			this.context = context;
			this.objects = objects;
		}

		public int getCount() {
			return this.objects.size();
		}

		public Course getItem(int index) {
			return this.objects.get(index);
		}

		public View getView(int position, View convertView, ViewGroup parent) 
		{
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
				row = inflater.inflate(R.layout.room_list_item_checkbox, parent, false);
			}
			TextView tw1 = (TextView)row.findViewById(R.id.courseItemTextView);
			String str = ((Course)objects.get(position)).getTitle();
			tw1.setText(str);
			CheckBox cb = (CheckBox) row.findViewById(R.id.courseCheckBox);
			final String Id=((Course)objects.get(position)).getId();
			if(watchedCoursesIds != null)
				if(watchedCoursesIds.contains(Id))
					cb.setChecked(true);
				else
					cb.setChecked(false);
			cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					 //Toast.makeText(getApplicationContext(),Id, Toast.LENGTH_SHORT).show();
					
					if(isChecked)
						 watchedCoursesIds.add(Id);
					 else
						 watchedCoursesIds.remove(Id);
					 
					 Toast.makeText(getApplicationContext(),String.valueOf(watchedCoursesIds.size()+" "+Id), Toast.LENGTH_SHORT).show();
				
				}
			});
			return row;
		}
    }
	
}