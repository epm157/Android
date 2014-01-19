package de.example.androidlab;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import roboguice.util.Ln;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;

import com.google.inject.Inject;

public class WatchCoursesSelectionActivity extends BaseActivity {

	//@InjectView(R.id.watched_course_selection_list)
	private ListView listView;
	
	//@InjectView(R.id.submit_watchList)
	private Button submitButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.watched_course_selection);
		listView = (ListView) findViewById(R.id.watched_course_selection_list);
		submitButton = (Button) findViewById(R.id.submit_watchList);
		ArrayList<Course> tempAll = new ArrayList<Course>();
		tempAll.add(new Course("titl1","id1"));
		tempAll.add(new Course("titl2","id2"));
		tempAll.add(new Course("titl3","id3"));
		
		Set<String> tempSel = new HashSet<String>();
		tempSel.add("id2");
		tempSel.add("id3");
		
		WatchedCoursesAdapter listAdapter = new WatchedCoursesAdapter(getApplicationContext(), tempAll, tempSel);
		if(listView == null ) Ln.d("it is null"); else Ln.d("it is NOT null");
		listView.setAdapter(listAdapter);
		
	}

	private class WatchedCoursesAdapter extends ArrayAdapter<String> {

		private Context ctx;
		List<Course> allCourses;
		Set<String> selectedCourseIds;
		
		@Inject LayoutInflater inflater;

		public WatchedCoursesAdapter(Context context, List<Course> allCourses,
				Set<String> selectedCourseIds) {
			super(getApplicationContext(), R.layout.checkbox_layout);
			ctx = context;
			this.allCourses = allCourses;
			this.selectedCourseIds = selectedCourseIds;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//View rowView = inflater.inflate(R.layout.checkbox_layout, parent, false);
//			CheckBox box = (CheckBox) rowView.findViewById(R.id.watchedCourseCheckbox);
			Ln.d("getView is called hereeeeee");
			if(convertView != null) return convertView;
			Ln.d("getView is called hereeeeee");
			CheckBox box = new  CheckBox(ctx);
			String courseId = allCourses.get(position).getId();
			box.setText(allCourses.get(position).getTitle());
			box.setSelected(selectedCourseIds.contains(courseId));
			box.setFocusable(false);
			box.setFocusableInTouchMode(false);
			return box;
		}

	}

}