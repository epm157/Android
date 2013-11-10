package de.example.androidlab;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.dropbox.chooser.android.DbxChooser;

public class ChooserActivity extends ListActivity {
	 static final String APP_KEY = "yddybp75i7468u9"/* This is for you to fill in! */;
	 static final int DBX_CHOOSER_REQUEST = 0;  // You can change this if needed
	 
	 private Button mChooserButton;
	 private DbxChooser mChooser;
	 
	 private List<String> item = null;
		private List<String> path = null;
		private List<String> addresses;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chooser);
		
		updateList();
		
		mChooser = new DbxChooser(APP_KEY);
		
		mChooserButton = (Button) findViewById(R.id.chooser_button);
		 mChooserButton.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                DbxChooser.ResultType resultType;
	                resultType = DbxChooser.ResultType.FILE_CONTENT;
	                mChooser.forResultType(resultType)
	                        .launch(ChooserActivity.this, DBX_CHOOSER_REQUEST);
	            }
	        });
	}
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == DBX_CHOOSER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                DbxChooser.Result result = new DbxChooser.Result(data);
                Log.d("main", "Link to selected file: " + result.getLink());
                
                File source = new File("/sdcard/android/data/com.dropbox.android/cache/tmp/ch");
                File destination = new File("/sdcard/l2p_to_dropbox_syncronizer");
                try {
                	TextView show=(TextView)findViewById(R.id.textView1);
                	
					copyFiles(source, destination);
				} catch (Exception e) {
					// TOD Auto-generated catch block
					e.printStackTrace();
				}
                //Toast.makeText(getApplicationContext(),String.valueOf(result.getLink()), Toast.LENGTH_SHORT).show();
                
                
            } else {
                // Failed or was cancelled by the user.
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        
        updateList();
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.chooser, menu);
		return true;
	}
	
	
	public static void copyFiles(File sourceLocation, File targetLocation)
			throws IOException {

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}
			File[] files = sourceLocation.listFiles();
			for (File file : files) {
				InputStream in = new FileInputStream(file);
				OutputStream out = new FileOutputStream(targetLocation + "/"
						+ file.getName());

				// Copy the bits from input stream to output stream
				byte[] buf = new byte[1024];
				int len;
				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
			}
			for (File file : files)
			{
				delete(file);
			}
		}
	}

	public static void delete(File file) throws IOException {

		if (file.isDirectory()) {

			// directory is empty, then delete it
			if (file.list().length == 0) {

				file.delete();
			} else {

				// list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);

					// recursive delete
					delete(fileDelete);
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					// file.delete();

				}
			}

		} else {
			// if file, then delete it
			file.delete();
			System.out.println("File is deleted : " + file.getAbsolutePath());
		}
	}
	
	public void updateList()
	{
		File destination = new File("/sdcard/l2p_to_dropbox_syncronizer");
		File[] files = destination.listFiles();
        item = new ArrayList<String>();
        for(File file : files)
        {
        	item.add(file.getName());
        }
        
        ArrayAdapter<String> fileList =new ArrayAdapter<String>(this, R.layout.explorer_row, item);
    	setListAdapter(fileList);	
	}

}
