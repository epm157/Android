package de.example.androidlab;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import roboguice.util.Ln;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
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
	
	//@InjectView(R.id.watched_course_selection_list)
	private ListView listView;
	
	//@InjectView(R.id.submit_watchList)
	private Button submitButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		Intent x = this.getIntent();
        Bundle b=this.getIntent().getExtras();
		ArrayList<Parcelable> rooms=b.getParcelableArrayList("rooms");
		l2pRoomslist=new ArrayList<Course>();
    	for(int i=0;i<rooms.size();i++)
    	{
    		Course lm=(Course)rooms.get(i);
    		l2pRoomslist.add(lm);
    	}
		
		
		
		setContentView(R.layout.watched_course_selection);
		listView = (ListView) findViewById(R.id.watched_course_selection_list);
		submitButton = (Button) findViewById(R.id.submit_watchList);
		//List<Course> tempAll = new ArrayList<Course>();
		
		
		
		//loadCourses();
		//tempAll=getAppService().l2pService_allCourses();
		//Toast.makeText(getApplicationContext(),String.valueOf(tempAll.size()), Toast.LENGTH_SHORT).show();
		 
		 adapter = new RoomArrayAdapter(getApplicationContext(), R.layout.room_list_item_checkbox, l2pRoomslist);
	        //listView.setAdapter(adapter);
	        
		if(listView == null ) Ln.d("it is null"); else Ln.d("it is NOT null");
		listView.setAdapter(adapter);
		
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
			cb.setChecked(true);
			return row;
		}
    }
	
	
	
	public void loadCourses()
	{
		
		AsyncTask<Void, Void, List<Course>> task = new AsyncTask<Void, Void, List<Course>>(){
			ProgressDialog pd;
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				pd = ProgressDialog.show(WatchCoursesSelectionActivity.this, "Please Wait", "Getting List Of Courses");
			}
			
			@Override
			protected List<Course> doInBackground(Void... params) {
				return getAppService().l2pService_allCourses();
			}
			
			
			@Override
			protected void onPostExecute(List<Course> result) {
				super.onPostExecute(result);
				pd.dismiss();
				//Toast.makeText(getApplicationContext(),String.valueOf(result.size()), Toast.LENGTH_SHORT).show();
					
			}
		};
		task.execute();
		
	}
	
    
	
}