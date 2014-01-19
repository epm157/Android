package de.example.androidlab;

import android.os.Parcel;
import android.os.Parcelable;


public class Course  implements Parcelable  {
	
	private String title;
	private String id;
	
	Course(){
		
	}
	
	public Course(String title, String id) {
		this.title = title;
		this.id = id;
	}



	public void setTitle(String title) {
		this.title = title;
	}
	
	
	public String getTitle() {
		return title;
	}
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] { this.title, this.id });
	}
	
	public Course(Parcel in)
	{
		String[] data=new String[2];
		in.readStringArray(data);
		title=data[0];
		id=data[1];
	}
	
	public static final Parcelable.Creator<Course> CREATOR = new Parcelable.Creator<Course>() {

		@Override
		public Course createFromParcel(Parcel source) {
			return new Course(source);
		}

		@Override
		public Course[] newArray(int size) {
			return new Course[size];
		}
	};
}
