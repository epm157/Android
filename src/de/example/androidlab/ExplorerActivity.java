package de.example.androidlab;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.os.Environment;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.graphics.Color;
import android.util.Base64;
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
	private List<String> addresses = null;
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
        uploadButton=(Button) findViewById(R.id.uploadButton);
        uploadButton.setEnabled(false);
        uploadButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				tryFiles();
			}
		});    
        getDir(root);
    }
    private void getDir(String dirPath)
    {
    	uploadButton.setEnabled(false);
    	addresses.clear();
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
    	ArrayAdapter<String> fileList =new ArrayAdapter<String>(this, R.layout.explorer_row, item);
    	setListAdapter(fileList);	
    }
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
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
			Toast error = Toast.makeText(this, String.valueOf(addresses.size()), Toast.LENGTH_LONG);
			error.show();
			if(addresses.size()>0)
				uploadButton.setEnabled(true);
			else
				uploadButton.setEnabled(false);
		  }
	}
	
	public void tryFiles()
	{
		long size=0;
		String base=null;
		for(int i=0;i<addresses.size();i++)
		{
			File file23 = new File(addresses.get(i).toString());
			size+=file23.length();
			base=encodeZipToBase64(file23);
		}
		Toast.makeText(this, String.valueOf(base.length()), Toast.LENGTH_LONG).show();
	}
	
	public String encodeZipToBase64(File zip) {

	    StringBuffer sb = new StringBuffer();

	    byte fileContent[] = new byte[1024];
	    try 
	    {
	        FileInputStream fin = new FileInputStream(zip);
	        while(fin.read(fileContent) >= 0) {
	             sb.append(Base64.encodeToString(fileContent, Base64.DEFAULT)); //exception here
	        }
	    } catch(OutOfMemoryError e) {
	        e.printStackTrace();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }

	    return sb.toString();

	}
}
