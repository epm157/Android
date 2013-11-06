package de.example.androidlab;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView.OnEditorActionListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MaterialListActivity extends CommonActivity  {
	
	private ListView listView;
	final CommonActivity act=this;
	MaterialArrayAdapter adapter;
	List<MaterialItem> materialsList;
	
	private List<MaterialItem> videos;
	private List<MaterialItem> documents;
	private List<MaterialItem> workingMaterials;
	private EditText mtxt;
	private Button cancelButton;
	private List<MaterialItem> mats;
	private ListView materialList;
	private Spinner spinner1;
	List<String> checkedItems;
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_material_lists);

		materialsList=new ArrayList<MaterialItem>();
		
		 Bundle b=this.getIntent().getExtras();
	        
	        if(b != null)
	        {
	        	Toast.makeText(this,"here", Toast.LENGTH_LONG).show();
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
		
		checkedItems=new ArrayList<String>();
		materialList = (ListView) findViewById(R.id.materialListView);
		
		
		
		MaterialArrayAdapter adapter = new MaterialArrayAdapter(
				getBaseContext(), R.layout.material_list_item,
				materialsList);
		materialList.setAdapter(adapter);
		//final List<MaterialItem> materials = new Vector<MaterialItem>();
		
		
		
		/*
		spinner1 = (Spinner) findViewById(R.id.spinner1);
		spinner1.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				checkedItems.clear();
				Search(".", arg2, materials);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				checkedItems.clear();
				Search(".", 0, materials);
			}
		});
		
		
		Button sync = (Button) findViewById(R.id.syncButton);
		sync.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				String array[] =new String[checkedItems.size()];
				checkedItems.toArray(array);
				Intent i = new Intent(getBaseContext(),DBRoulette.class);
				i.putExtra("addresses", array);
				i.putExtra("flag", 1);
				startActivity(i);
				finish();
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
					Search(searchStr, 0, materials);
				} else if (searchStr.length() == 0 || searchStr.equals(" ")
						|| searchStr.equals("  ") || searchStr.equals(" ")) {
					checkedItems.clear();
					MaterialArrayAdapter adapter = new MaterialArrayAdapter(
							getBaseContext(), R.layout.material_list_item,
							materials);
					materialList.setAdapter(adapter);
				}
			}
		});
		
		cancelButton = (Button) findViewById(R.id.btnSearch);
		cancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mtxt.setText("");
				checkedItems.clear();
				MaterialArrayAdapter adapter = new MaterialArrayAdapter(
						getBaseContext(), R.layout.material_list_item,
						materials);
				materialList.setAdapter(adapter);
			}
		});
	}
	*/
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
				//cb.setChecked(materialitem.isState());
				cb.setChecked(false);
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

	
	/*
	public void Search(String str, int type, List<MaterialItem> materials) {
		checkedItems.clear();
		final List<MaterialItem> mats = new Vector<MaterialItem>(0);
			for (MaterialItem temp : materials){
			if ((temp.getName().toLowerCase()).contains(str.toLowerCase())) {
				if (type == 0) {
					//Toast.makeText(getApplicationContext(),String.valueOf(temp.isState()),Toast.LENGTH_SHORT).show();
					mats.add(new MaterialItem(1,temp.getName(), temp.getlink(),
							false));
				}
				else if (type == 1) {
					if (temp.getName().toString().toLowerCase().contains("mp4")
							|| temp.getName().toLowerCase().contains("avi")
							|| temp.getName().toLowerCase().contains("flv")
							|| temp.getName().toLowerCase().contains("swf")) {
						mats.add(new MaterialItem(1,temp.getName(), temp
								.getlink(), temp.isState()));
					}
				} else if (type == 2) {
					if (temp.getName().toString().toLowerCase().contains("pdf")
							|| temp.getName().toLowerCase().contains("doc")
							|| temp.getName().toLowerCase().contains("docx")
							|| temp.getName().toLowerCase().contains("html")) {
						mats.add(new MaterialItem(1,temp.getName(), temp
								.getlink(), temp.isState()));
					}
				}
			}
		}
		MaterialArrayAdapter adapter = new MaterialArrayAdapter(
				getBaseContext(), R.layout.material_list_item, mats);
		materialList.setAdapter(adapter);
	}
	public final void Refresh()
	{
		int sum=0;
		for (int i = 0; i < mats.size(); i++)
		{
			
		    
		
		
		}
		Toast.makeText(getApplicationContext(),String.valueOf(0),Toast.LENGTH_SHORT).show();
	}
	*/
}
