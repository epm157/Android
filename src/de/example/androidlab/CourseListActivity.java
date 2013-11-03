package de.example.androidlab;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Alex on 6/23/13.
 */
public class CourseListActivity extends Activity {
	//Define some Constants we use for Dialog-creation.
	
	private ListView listView;

	private ArrayList<LearnRoom> roomsList=new ArrayList<LearnRoom>();
	RoomArrayAdapter adapter;
	List<LearnRoom> l2pRoomslist;
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_courselist);
        listView = (ListView) findViewById(R.id.listView);
      
        
        
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
        	adapter = new RoomArrayAdapter(CourseListActivity.this, R.layout.room_list_item, l2pRoomslist);
            listView.setAdapter(adapter);
        }
        else
        {
        	Toast.makeText(this,"NO!", Toast.LENGTH_LONG).show();
        }
        
        ////////////////
        /*
        Authentication authentication;
		authentication = new Authentication(this);
		L2P_Services tempService=new L2P_Services(this,authentication);
		SoapObject obj=tempService.getCourseList();
		*/
		
		
        //LearnRoom lr1=new LearnRoom("Course1","www","www","www");
        //LearnRoom lr2=new LearnRoom("Course2","www","www","www");
        l2pRoomslist=new ArrayList<LearnRoom>();
        //l2pRoomslist.add(lr1);
        //l2pRoomslist.add(lr2);
        adapter = new RoomArrayAdapter(CourseListActivity.this, R.layout.room_list_item, l2pRoomslist);
        listView.setAdapter(adapter);
        
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, final View view,
              int position, long id) {
            final LearnRoom item = (LearnRoom) parent.getItemAtPosition(position);
            
            
          }
        });
    }
    /*
    private class DownloadParseTask extends AsyncTask<Object, Object, Object>
    {
		@Override
		protected void onPreExecute(){

		}
		
		
		@Override
		protected Object doInBackground(Object... params)
		{
			
		}
		
		@Override
		protected void onPostExecute(Object result)
		{
		}
    	
    }
	*/
	
    
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

 
    
