package de.example.androidlab;

public class MaterialItem 
{
	private int id;
	private String name;
	private String link;
	private boolean state; 
	MaterialItem(){
		
	}
	
	MaterialItem(int i,String name, String link, boolean state){
		this.id=i;
		this.name = name;
		this.link = link;
		this.setState(state);
	}
	public void setId(int i)
	{
		this.id=i;
	}
	public int getId()
	{
		return this.id;
	}
	public void setName(String name){
		this.name = name;
	}
	
	public void setlink(String link){
		this.link = link;
	}
	
	
	public String getName(){
		return this.name;
	}
	public String getlink(){
		return this.link;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}
}
