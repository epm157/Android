package de.example.androidlab;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.widget.AdapterView.OnItemSelectedListener;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MaterialListActivity extends CommonActivity  {
	
	final Context context = this;
	private ListView listView;
	final CommonActivity act=this;
	MaterialArrayAdapter adapter;
	List<MaterialItem> materialsList;
	private ArrayList<MaterialItem> checkedItems;
	private List<MaterialItem> workingMaterials;
	private EditText mtxt;
	private Button cancelButton;
	private List<MaterialItem> mats;
	private ListView materialList;
	private Spinner spinner1;
	private List fileAddresses;
	
	private int spinnerValue;
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_material_lists);

		fileAddresses=new ArrayList<String>();
		materialsList=new ArrayList<MaterialItem>();
		spinnerValue=0;
		checkedItems=new ArrayList<MaterialItem>();
		
		 Bundle b=this.getIntent().getExtras();
	        
	        if(b != null)
	        {
	        	
	        	ArrayList<Parcelable> Materials=b.getParcelableArrayList("materials");
	        	for(int i=0;i<Materials.size();i++)
	        	{
	        		MaterialItem lm=(MaterialItem)Materials.get(i);
	        		//Materials.add(lm);
	        		materialsList.add(lm);
	        		//Toast.makeText(this,lm.getName(), Toast.LENGTH_LONG).show();
	        	}
	        }
	        else
	        {
	        	Toast.makeText(this,"NO!", Toast.LENGTH_LONG).show();
	        }
		
		materialList = (ListView) findViewById(R.id.materialListView);
		
		workingMaterials=materialsList;
		
		MaterialArrayAdapter adapter = new MaterialArrayAdapter(
				getBaseContext(), R.layout.material_list_item,
				materialsList);
		materialList.setAdapter(adapter);
		//final List<MaterialItem> materials = new Vector<MaterialItem>();		
		
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				spinnerValue=arg2;
				Search("null", arg2, materialsList);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

				//Search(".", 0, materials);
			}
		});
		
		
		mtxt = (EditText) findViewById(R.id.edSearch);
		mtxt.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String searchStr = String.valueOf(mtxt.getText());
				if (searchStr.length() > 0 && !searchStr.equals(" ")
						&& !searchStr.equals("  ") && !searchStr.equals(" ")) {
					
					Search(searchStr, spinnerValue, materialsList);
				} 
				else if (searchStr.length() == 0 || searchStr.equals(" ")
						|| searchStr.equals("  ") || searchStr.equals(" ")) {
					
					Search("null", spinnerValue, materialsList);

				}
			}
		});
		
		
		Button sync = (Button) findViewById(R.id.syncButton);
		sync.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				
				
				LayoutInflater li = LayoutInflater.from(context);
				View promptsView = li.inflate(R.layout.dialog_signin, null);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
				alertDialogBuilder.setView(promptsView);
				final EditText userInput = (EditText) promptsView.findViewById(R.id.username);
				final EditText passInput = (EditText) promptsView.findViewById(R.id.password);
				alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK",
				  new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
				    	
				    	SharedPreferences app_preferences =	PreferenceManager.getDefaultSharedPreferences(context);
						SharedPreferences.Editor editor = app_preferences.edit();
				    	String username=userInput.getText().toString();
				    	String password=passInput.getText().toString();
				    	editor.putString("LoginL2P", username);
				       	editor.putString("PassL2P", password );
				       	editor.commit(); // Very important
				       	
				       	for(int i=0;i<checkedItems.size();i++)
				       		fileAddresses.add((String)checkedItems.get(i).getUrl());
				       	
				       	new DownloadFile().execute(fileAddresses.get(fileAddresses.size()-1).toString(),"temp");
				       	
				       	Bundle b = new Bundle();
	                    b.putParcelableArrayList("materials", checkedItems);
				       	//Toast.makeText(getApplicationContext(),"Size: "+checkedItems.size(),Toast.LENGTH_SHORT).show();
				       	
				       	
				       	Intent intnt = new Intent(MaterialListActivity.this,Upload.class);
	                    intnt.putExtras(b);
	                    //MaterialListActivity.this.startActivity(intnt);
				    	//finish();
				    }
				  })
				.setNegativeButton("Cancel",
				  new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog,int id) {
					dialog.cancel();
				    }
				  });

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
			}
		});
	}
	
	
	private class MaterialArrayAdapter extends ArrayAdapter<MaterialItem> {
		private static final String tag = "MaterialArrayAdapter";
		private Context context;
		private List<MaterialItem> objects;

		public MaterialArrayAdapter(Context context, int textViewResourceId,
				List<MaterialItem> objects) {
			super(context, textViewResourceId, objects);
			this.context = context;
			this.objects = objects;
		}
		public int getCount() {
			return this.objects.size();
		}
		public MaterialItem getItem(int index) {
			return this.objects.get(index);
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				// Log.d(tag, "Starting XML Row Inflation ... ");
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.material_list_item, parent,
						false);
				// Log.d(tag, "Successfully completed XML Row Inflation!");
			}
			final MaterialItem materialitem = getItem(position);
			if (materialitem != null) {
				// Get reference to Buttons
				TextView l2pnameText = (TextView) row.findViewById(R.id.materialItemTextView);
				// Set Text and Tags
				CheckBox cb = (CheckBox) row.findViewById(R.id.materialCheckBox);
				final int state=Integer.parseInt(materialitem.isState());
				boolean b=true;
				if(state==0)
					b=false;
				cb.setChecked(b);
				//cb.setChecked(false);
				cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if(isChecked)
							checkedItems.add(materialitem);
						else
							checkedItems.remove(materialitem);
						
						
						
						int id=Integer.parseInt(materialitem.getId());
						for(int i=0; i<materialsList.size(); i++)
						{
							MaterialItem mt=materialsList.get(i);
							int tid=Integer.parseInt(mt.getId());
							if(tid==id)
							{
								if(state==0)
									mt.setState("1");
								else if(state==1)
									mt.setState("0");
								materialsList.set(i, mt);	
							}		
						}
					}
				});
				
						
						/*
						
				cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Toast.makeText(getApplicationContext(),String.valueOf(materialitem.getName()),Toast.LENGTH_SHORT).show();
						materialitem.setState(isChecked);
						if(isChecked)
							checkedItems.add(materialitem.getName());
						else
							checkedItems.remove(materialitem.getName());
						//Toast.makeText(getApplicationContext(),String.valueOf(materialitem.getName()),Toast.LENGTH_SHORT).show();
					}
				});
				*/
				// We need the Tag in order to identify the button later.
				if (l2pnameText != null) {
					l2pnameText.setText(materialitem.getName());
				}
			}
			return row;
		}
	}

	
	
	
	
	
	public void Search(String str, int selectedType, List<MaterialItem> materials) {
		
		List<MaterialItem> mats=new ArrayList<MaterialItem>();
		mats.clear();
		for(int i=0; i<materials.size();i++)
		{
			MaterialItem temp=materials.get(i);
			String materialType=temp.getFileType().toLowerCase();
			String materialName=temp.getName().toLowerCase();
			if(str.equals(null))
				str="null";
			str=str.toLowerCase();
			if(selectedType==0)
			{
				if(str.equals("null"))
					mats.add(temp);
				else if(materialName.contains(str))
					mats.add(temp);
			}
			if(selectedType==1)
			{
				if(materialType.equals("mp4") || materialType.equals("avi"))
					{
						if(str.equals("null"))
							mats.add(temp);
						else if(materialName.contains(str))
							mats.add(temp);
					}
			}
				
			
			if(selectedType==2)
			{
				//Toast.makeText(getApplicationContext(),str,Toast.LENGTH_LONG).show();
				if(materialType.equals("pdf") || materialType.equals("doc"))
				{
					
					if(str.equals("null"))
						mats.add(temp);
					else if(materialName.contains(str))
					{
						mats.add(temp);
						
					}
						
				}
			}	
			
		}
		
		
		workingMaterials=mats;
		
		//Toast.makeText(getApplicationContext(),String.valueOf(workingMaterials.size()),Toast.LENGTH_LONG).show();
		Refresh(workingMaterials);
		
	}
	public final void Refresh(List<MaterialItem> materials)
	{
		
		MaterialArrayAdapter adapter = new MaterialArrayAdapter(
				getBaseContext(), R.layout.material_list_item,
				materials);
		materialList.setAdapter(adapter);
	}
	
	
	
	private class DownloadFile extends AsyncTask<String, Integer, String>{

		public void downloadHTTPC(String fileURL) {
		    DefaultHttpClient httpclient = new DefaultHttpClient();
		    
		    try {
		    	String tempURL=fileURL;
		    	String fileName = tempURL.substring(tempURL.lastIndexOf('/')+1, tempURL.length() );
		    	//Toast.makeText(context,fileURL.toString(), Toast.LENGTH_LONG).show();
		    	String path=tempURL.replace("https://www2.elearning.rwth-aachen.de", "");
		    	path=path.replace("/"+fileName, "");
		    	path=path.substring(26,path.length());
		    	path=path.replace("%20", " ");
		    	//standard directory for our application
		    	File applicationDirectory = new File("/sdcard/l2p_to_temp"+path);
		        if (!applicationDirectory.exists()) {
		    		applicationDirectory.mkdirs(); //return false, if folder already exists
		    	}
		        
		        File file = new File(applicationDirectory + "/" +fileName.replace("%20", " "));
		        String later="/sdcard/l2p_to_temp"+path+fileName.replace("%20", " ");
		        //newFiles.add(later);
		        httpclient.getCredentialsProvider().setCredentials(
		                new AuthScope(null, -1),
		                new UsernamePasswordCredentials("ep203401", "tehrantehran2"));

		        HttpGet httpget = new HttpGet(fileURL);
		        HttpResponse response = httpclient.execute(httpget);
		        HttpEntity entity = response.getEntity();
		        if (entity != null) {
		            final int BUFFER_SIZE = 23 * 1024;
		            InputStream is = entity.getContent();
		            BufferedInputStream bis = new BufferedInputStream(is, BUFFER_SIZE);
		            FileOutputStream fos = new FileOutputStream(file);
		            byte[] baf = new byte[BUFFER_SIZE];
		            int actual = 0;
		            while (actual != -1) {
		                fos.write(baf, 0, actual);
		                actual = bis.read(baf, 0, BUFFER_SIZE);
		            }
		            fos.close();
		        }
		    } catch (IOException e) {
		        Log.d("ImageManager", "Error: " + e);
		        

		    } 
		    catch (Exception e)
		    {
		    }
		    
		    finally {
		        httpclient.getConnectionManager().shutdown();
		    }
		}
		
		//private File SDCardRoot = Environment.getExternalStorageDirectory();
		@Override
	    protected String doInBackground(String... sUrl) {
	        try {
	        	
	        	String stringUrl = sUrl[0];
	        	downloadHTTPC(stringUrl);
	        } catch (Exception e) {
	        }
	        return null;
		}
		
		 @Override
		    protected void onPreExecute() {
			 //Toast.makeText(getApplicationContext(),"Download started for file", Toast.LENGTH_LONG).show();
		        super.onPreExecute();
		    }

		 @Override
		 protected void onProgressUpdate(Integer... progress) {
			 Log.d("ANDRO_ASYNC", "Progress: " + progress[0]);
			 Toast.makeText(getApplicationContext(), "Status Updated", Toast.LENGTH_LONG).show();
		     super.onProgressUpdate(progress);
		 }

		@Override
		protected void onPostExecute(String result) {
			
			
			/*
			if(result != null)
			{
				newFiles.add(result);
				Toast.makeText(getApplicationContext(),result, Toast.LENGTH_LONG).show();
				SharedPreferences app_preferences =	PreferenceManager.getDefaultSharedPreferences(context);
				SharedPreferences.Editor editor = app_preferences.edit();
				editor.putString(result, "false");
		       	editor.commit();
			}
			else if(result == null)
			{
				 Toast.makeText(getApplicationContext(), "null Returned", Toast.LENGTH_LONG).show();
				 SharedPreferences app_preferences =	PreferenceManager.getDefaultSharedPreferences(context);
					SharedPreferences.Editor editor = app_preferences.edit();
					editor.putString(result, "false");
			       	editor.commit();
			}
			super.onPostExecute(result);
			*/
			
			////////////////////////
			
			String str=null;
			if(fileAddresses.size()>0)
				str=(String)fileAddresses.get(fileAddresses.size()-1);
			fileAddresses.remove(fileAddresses.size()-1);
			
			if(fileAddresses.size()>=0)
			{
				Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
				SharedPreferences app_preferences =	PreferenceManager.getDefaultSharedPreferences(context);
				SharedPreferences.Editor editor = app_preferences.edit();
				editor.putString(str, "false");
		       	editor.commit();
			}
			if(fileAddresses.size()==0)
			{
				//for(int i=0;i<newFiles.size();i++)
//					/Toast.makeText(getApplicationContext(),(String)newFiles.get(i), Toast.LENGTH_LONG).show();
				//newFiles.clear();
			}
			else
			{
				new DownloadFile().execute(fileAddresses.get(fileAddresses.size()-1).toString(),"temp");
				super.onPostExecute(result);
			}
			
			
			
			
			
			
		}
	}
	
}

