package de.example.androidlab;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Environment;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ExplorerActivity extends ListActivity {
	
	private List<String> item = null;
	private List<String> path = null;
	private List<String> addresses;
	private int []flags;
	private String root;
	private TextView myPath;
	
	private Button uploadButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explorer);
        
        
        
         addresses=new ArrayList<String>();
        
        myPath = (TextView)findViewById(R.id.path);
        
        root = Environment.getExternalStorageDirectory().getPath();
        
        getDir(root);
        
        uploadButton=(Button) findViewById(R.id.uploadButton);
        uploadButton.setEnabled(false);
        uploadButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// This logs you out if you're logged in, or vice versa
				
			}
		});
    }
    
    private void getDir(String dirPath)
    {
    	myPath.setText("Location: " + dirPath);
    	item = new ArrayList<String>();
    	path = new ArrayList<String>();
    	File f = new File(dirPath);
    	File[] files = f.listFiles();
    	
    	if(!dirPath.equals(root))
    	{
    		item.add(root);
    		path.add(root);
    		item.add("../");
    		path.add(f.getParent());	
    	}
    	
    	for(int i=0; i < files.length; i++)
    	{
    		File file = files[i];
    		
    		if(!file.isHidden() && file.canRead()){
    			path.add(file.getPath());
        		if(file.isDirectory()){
        			item.add(file.getName() + "/");
        		}else{
        			item.add(file.getName());
        		}
    		}	
    	}

    	flags=new int[item.size()];
    	for(int i=0;i<flags.length;i++)
    		flags[i]=0;
    	ArrayAdapter<String> fileList =
new ArrayAdapter<String>(this, R.layout.explorer_row, item);
    	setListAdapter(fileList);	
    }

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		//uploadButton.setEnabled(false);
		
		File file = new File(path.get(position));
		
		if (file.isDirectory())
		{
			if(file.canRead()){
				getDir(path.get(position));
			}else{
				new AlertDialog.Builder(this)
					.setIcon(R.drawable.ic_launcher)
					.setTitle("[" + file.getName() + "] folder can't be read!")
					.setPositiveButton("OK", null).show();	
			}	
		}else {
			
			if(flags[position]==0)
			{
				v.setBackgroundColor(Color.CYAN);
				addresses.add(file.getAbsolutePath());
				flags[position]=1;
				
			}
			else if(flags[position]==1)
			{
				v.setBackgroundColor(Color.WHITE);
				addresses.remove(file.getAbsolutePath());
				flags[position]=0;
			}
			String str="";
			int sum=0;
			for(int i=0;i<addresses.size();i++)
			{
				str+="\n"+addresses.get(i).toString();
				sum++;
			}
					
			Toast error = Toast.makeText(this, String.valueOf(str), Toast.LENGTH_LONG);
			error.show();
			if(sum>0)
				uploadButton.setEnabled(true);
			else
				uploadButton.setEnabled(false);
				
			/*new AlertDialog.Builder(this)
					.setIcon(R.drawable.ic_launcher)
					.setTitle("[" + file.getName() + "]")
					.setPositiveButton("OK", null).show();*/
			/*for(int i=0;i<flags.length;i++)
				if(flags[i]==true)
					uploadButton.setEnabled(true);*/
		  }
	}

}
