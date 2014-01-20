package de.example.androidlab;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ksoap2.serialization.SoapObject;

import roboguice.util.Ln;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Alex on 6/23/13.
 */
public class CourseListActivity extends BaseActivity {
	private ListView listView;

	private String addCourse="false";
	RoomArrayAdapter adapter;
	List<Course> l2pRoomslist;
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courselist);
        listView = (ListView) findViewById(R.id.listView);
      
        l2pRoomslist=new ArrayList<Course>();
        Intent x = this.getIntent();
        Bundle b=this.getIntent().getExtras();
        addCourse=x.getStringExtra("addcourse");
        if(b != null)
        {
        	ArrayList<Parcelable> rooms=b.getParcelableArrayList("rooms");
        	for(int i=0;i<rooms.size();i++)
        	{
        		Course lm=(Course)rooms.get(i);
        		l2pRoomslist.add(lm);
        	}
        }
        else
        {
        	Toast.makeText(this,"NO!", Toast.LENGTH_LONG).show();
        }
        
	
        adapter = new RoomArrayAdapter(this, R.layout.room_list_item, l2pRoomslist);
        listView.setAdapter(adapter);
        
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, final View view,
              final int position, long id) {
        	
        	final AdapterView<?> fp = parent;
        	
        	
      		final Course item = (Course) fp.getItemAtPosition(position);
      		
    		if(addCourse.startsWith("t"))
    		{
    			
        		Intent i = new Intent(getBaseContext(),WatchCoursesSelectionActivity.class);
				 startActivity(i);
				 finish();
    		}
    		else
    		{
    			Ln.v("CourseList not-add course");
    			AsyncTask<Void, Void, List<MaterialItem>> task = new AsyncTask<Void, Void, List<MaterialItem>>() {
                	
                	private String courseId=null;
                	private String courseName=null;
                	@Override
                	protected void onPreExecute() {
                		// TODO Auto-generated method stub
                		super.onPreExecute();
                	}        	
                	@Override
                	protected List<MaterialItem> doInBackground(Void... params) {
                		final Course item = (Course) fp.getItemAtPosition(position);
                        courseId = item.getId();
                        courseName=item.getTitle();
                        List<MaterialItem> materialItems = getAppService().l2pService_listOfFilesofCourse(courseId);
            			
            			return materialItems;
                	}
                	@Override
                	protected void onPostExecute(List<MaterialItem> materials) {
                		super.onPostExecute(materials);
                	
                        
                        Bundle b = new Bundle();
                        b.putParcelableArrayList("materials", (ArrayList<MaterialItem>) materials);
                        Intent intnt = new Intent(CourseListActivity.this,MaterialListActivity.class);
                        intnt.putExtras(b);
                        intnt.putExtra("courseid", courseId);
                        intnt.putExtra("coursename", courseName);
                        CourseListActivity.this.startActivity(intnt);
                	}
                	
                };
                
                task.execute();
    		}
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
				row = inflater.inflate(R.layout.room_list_item, parent, false);
			}
			TextView tw1 = (TextView)row.findViewById(R.id.courseItemTextView);
			String str = ((Course)objects.get(position)).getTitle();
			tw1.setText(str);
			return row;
		}
    }
    
	
}

 
    
