package de.example.androidlab;

import android.os.Parcel;
import android.os.Parcelable;

public class MaterialItem implements Parcelable
{
	private String id;
	private String name;
	private String url;
	private String fileType;
	private String lastUpdated;  
	private String state; 
	MaterialItem(){
		
	}
	
	MaterialItem(String i,String name, String link,String ft,String lu, String state){
		this.id=i;
		this.name = name;
		this.url = link;
		this.fileType=ft;
		this.lastUpdated=lu;
		this.setState(state);
	}
	
	MaterialItem(String i,String name, String link,String ft, String state){
		this.id=i;
		this.name = name;
		this.url = link;
		this.fileType=ft;
		//this.lastUpdated=lu;
		this.setState(state);
	}
	
	public void setId(String i)
	{
		this.id=i;
	}
	public String getId()
	{
		return this.id;
	}
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setUrl(String link){
		this.url = link;
	}
	
	public void setFileType(String ft)
	{
		this.fileType=ft;
	}
	public String getFileType()
	{
		return this.fileType;
	}
	
	public void setLastUpdated(String lu)
	{
		this.lastUpdated=lu;
	}
	public String getLastUpdated()
	{
		return this.lastUpdated;
	}
	
	public String getUrl(){
		return this.url;
	}

	public String isState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public int describeContents() {
		// TOD Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TOD Auto-generated method stub
		dest.writeStringArray(new String[] { this.id, this.name,  this.url, this.fileType, this.lastUpdated, this.state });
		
	}
	
	public MaterialItem(Parcel in)
	{
		String[] data=new String[6];
		in.readStringArray(data);
		id=data[0];
		name=data[1];
		url=data[2];
		fileType=data[3];
		lastUpdated=data[4];
		state=data[5];
	}
	
	public static final Parcelable.Creator<MaterialItem> CREATOR = new Parcelable.Creator<MaterialItem>() {

		@Override
		public MaterialItem createFromParcel(Parcel source) {
			// TOD Auto-generated method stub
			return new MaterialItem(source);
		}

		@Override
		public MaterialItem[] newArray(int size) {
			// TOD Auto-generated method stub
			return new MaterialItem[size];
		}
	};
}
