package de.example.androidlab;

import java.util.ArrayList;
import java.util.List;
import android.widget.AdapterView.OnItemSelectedListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
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
	private int spinnerValue;
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_material_lists);

		materialsList=new ArrayList<MaterialItem>();
		spinnerValue=0;
		
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
		
		checkedItems=new ArrayList<String>();
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

				checkedItems.clear();
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
		
		/*
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
				final int state=Integer.parseInt(materialitem.isState());
				boolean b=false;
				if(state==1)
					b=true;
				cb.setChecked(b);
				//cb.setChecked(false);
				cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(),materialitem.getName(),Toast.LENGTH_SHORT).show();
						int id=Integer.parseInt(materialitem.getId());
						for(int i=0; i<materialsList.size(); i++)
						{
							MaterialItem mt=materialsList.get(i);
							int tid=Integer.parseInt(mt.getId());
							if(tid==id)
							{
								if(state==1)
									mt.setState("0");
								else
									mt.setState("1");
								
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
	
}
