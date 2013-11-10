package de.example.androidlab;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Class that describes a L2P Room.
 * NAME, L2P Link, Campus Link, RSS Link
 * @author blightzero
 *
 */
public class LearnRoom  implements Parcelable  {
	
	private String title;
	private String id;
	
	LearnRoom(){
		
	}
	
	LearnRoom(String t, String i){
		this.title = t;
		this.id = i;
	}
	
	public void setTitle(String t){
		this.title = t;
	}
	
	
	public String getTitle(){
		return this.title;
	}
	public String getId(){
		return this.id;
	}
	
	public void setId(String i)
	{
		this.id=i;
	}

	@Override
	public int describeContents() {
		// TOD Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeStringArray(new String[] { this.title, this.id });
		//dest.writeString(title);
		//dest.writeString(id);
		
	}
	public LearnRoom(Parcel in)
	{
		String[] data=new String[2];
		in.readStringArray(data);
		title=data[0];
		id=data[1];
	}
	public static final Parcelable.Creator<LearnRoom> CREATOR = new Parcelable.Creator<LearnRoom>() {

		@Override
		public LearnRoom createFromParcel(Parcel source) {
			// TOD Auto-generated method stub
			return new LearnRoom(source);
		}

		@Override
		public LearnRoom[] newArray(int size) {
			// TOD Auto-generated method stub
			return new LearnRoom[size];
		}
	};
}
