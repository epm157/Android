package de.example.androidlab;

import java.util.ArrayList;
import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
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
public class CourseListActivity extends CommonActivity {
	//Define some Constants we use for Dialog-creation.
	private ListView listView;

	
	//private ArrayList<LearnRoom> roomsList=new ArrayList<LearnRoom>();
	RoomArrayAdapter adapter;
	List<LearnRoom> l2pRoomslist;
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_courselist);
        listView = (ListView) findViewById(R.id.listView);
      
        l2pRoomslist=new ArrayList<LearnRoom>();
        
        Bundle b=this.getIntent().getExtras();
        
        if(b != null)
        {
        	//Toast.makeText(this,"here", Toast.LENGTH_LONG).show();
        	ArrayList<Parcelable> rooms=b.getParcelableArrayList("rooms");
        	for(int i=0;i<rooms.size();i++)
        	{
        		LearnRoom lm=(LearnRoom)rooms.get(i);
        		l2pRoomslist.add(lm);
        		
        		//Toast.makeText(this,lm.getTitle(), Toast.LENGTH_LONG).show();
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
            		final LearnRoom item = (LearnRoom) fp.getItemAtPosition(position);
                    courseId = item.getId();
                    courseName=item.getTitle();
                    L2P_Services tempService=new L2P_Services(getAppPreferences());
                    SoapObject obj=null;
        			try {
        				obj = tempService.getDocumentsOverview(courseId);
        			} catch (CommonException e) {
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
                    intnt.putExtra("CourseId", courseId);
                    intnt.putExtra("coursename", courseName);
                    CourseListActivity.this.startActivity(intnt);
            	}
            	
            };
            
            task.execute();
          }
        });
    }
    
    private class RoomArrayAdapter extends ArrayAdapter<LearnRoom> 
    {
		//private static final String tag = "L2pRoomArrayAdapter";
		private Context context;
		private List<LearnRoom> objects;
		
		public RoomArrayAdapter(Context context, int textViewResourceId, List<LearnRoom> objects) {
			super(context, textViewResourceId, objects);
			this.context = context;
			this.objects = objects;
		}

		public int getCount() {
			return this.objects.size();
		}

		public LearnRoom getItem(int index) {
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
			String str = ((LearnRoom)objects.get(position)).getTitle();
			tw1.setText(str);
			return row;
		}
    }
    
	
}

 
    
