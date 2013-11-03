package de.example.androidlab;


/**
 * Class that describes a L2P Room.
 * NAME, L2P Link, Campus Link, RSS Link
 * @author blightzero
 *
 */
public class LearnRoom {
	
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
}
