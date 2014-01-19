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
	//Define some Constants we use for Dialog-creation.
	private ListView listView;

	private String addCourse="false";
	//private ArrayList<LearnRoom> roomsList=new ArrayList<LearnRoom>();
	RoomArrayAdapter adapter;
	List<Course> l2pRoomslist;
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
    			AsyncTask<Void, Void, SoapObject> task = new AsyncTask<Void, Void, SoapObject>() {
                	
                	private String courseId=null;
                	private String courseName=null;
                	@Override
                	protected void onPreExecute() {
                		// TODO Auto-generated method stub
                		super.onPreExecute();
                	}        	
                	@Override
                	protected SoapObject doInBackground(Void... params) {
                		final Course item = (Course) fp.getItemAtPosition(position);
                        courseId = item.getId();
                        courseName=item.getTitle();
                        SoapObject obj=null;
            			try {
            				obj = getAppService().l2pService_listOfFilesofCourse(courseId);
            			} catch (AppException e) {
            				// TODO handle error here
            				e.printStackTrace();
            			}
            			return obj;
                	}
                	@Override
                	protected void onPostExecute(SoapObject result) {
                		super.onPostExecute(result);
                		ArrayList<MaterialItem> materials=new ArrayList<MaterialItem>();
                        materials.clear();
                        
                        int count=result.getPropertyCount();
                        for(int i=0;i<count;i++)
                        {
                                SoapObject first =(SoapObject)result.getProperty(i);
                                String idd=first.getPropertyAsString("Id");
                                String name=first.getPropertyAsString("Name");
                                String url=first.getPropertyAsString("Url");
                                String ft=first.getPropertyAsString("FileType");
                                String lu=first.getPropertyAsString("LastUpdated").toString();
                                String state="0";
                                MaterialItem lr=new MaterialItem(idd, name, url, ft, lu, state);        
                                materials.add(lr);
                        }
                        
                        Bundle b = new Bundle();
                        b.putParcelableArrayList("materials", materials);
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
		//private static final String tag = "L2pRoomArrayAdapter";
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
				//Log.d(tag, "Starting XML Row Inflation ... ");
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;
				row = inflater.inflate(R.layout.room_list_item, parent, false);
				//Log.d(tag, "Successfully completed XML Row Inflation!");
			}
			TextView tw1 = (TextView)row.findViewById(R.id.courseItemTextView);
			String str = ((Course)objects.get(position)).getTitle();
			tw1.setText(str);
			return row;
		}
    }
    
	
}

 
    
