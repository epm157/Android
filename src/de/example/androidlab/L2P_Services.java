package de.example.androidlab;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.ksoap2.serialization.SoapObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class L2P_Services {
	private Activity activity;
	private Authentication authentication;
	
	public L2P_Services(Activity act,Authentication aut )
	{
		this.activity=act;
		this.authentication=aut;
	}
	
	public String downloadDocumentItem(String courseId) {
		String methodName = "DownloadDocumentItem";
		String namespace = "http://cil.rwth-aachen.de/l2p/services";
		String soapAction = "http://cil.rwth-aachen.de/l2p/services/DownloadDocumentItem";
		String urlToConnect = "https://www2.elearning.rwth-aachen.de/L2PWebservicesDev/L2PLearningMaterialService.asmx"; //try ?WSDL at end
		String token = authentication.getAccessToken();
		ConnectForService serviceCall = new ConnectForService(this.activity,methodName , namespace, soapAction, urlToConnect);
		serviceCall.addProperty("userToken", token);
		serviceCall.addProperty("courseId", "13ws-04405"); //courseID
		serviceCall.addProperty("fileId", "4");
		serviceCall.execute();
		return null;
	}
	
	public String getDocumentsOverview(String courseId) {
		String methodName = "GetDocumentsOverview";
		String namespace = "http://cil.rwth-aachen.de/l2p/services";
		String soapAction = "http://cil.rwth-aachen.de/l2p/services/GetDocumentsOverview";
		String urlToConnect = "https://www2.elearning.rwth-aachen.de/L2PWebservicesDev/L2PLearningMaterialService.asmx?WSDL"; //try ?WSDL at end
		String token = authentication.getAccessToken();
		ConnectForService serviceCall = new ConnectForService(this.activity,methodName , namespace, soapAction, urlToConnect);
		serviceCall.addProperty("userToken", token);
		serviceCall.addProperty("courseId", courseId);
		
		/////
		
		
		
		serviceCall.setObserver(new Observer() {
			SoapObject obj=null;
			@Override
			public void update(Observable observable, Object data) {
				
				//Toast.makeText(activity, String.valueOf("Test:" +data.toString().length()), Toast.LENGTH_LONG).show();
				
				obj=(SoapObject)data;
				ArrayList<MaterialItem> materials=new ArrayList<MaterialItem>();
				materials.clear();
				
				int count=obj.getPropertyCount();
				Toast.makeText(activity, "number: "+ count, Toast.LENGTH_LONG).show();
				for(int i=0;i<count;i++)
				{
					SoapObject first =(SoapObject)obj.getProperty(i);
					String id=first.getPropertyAsString("Id");
					String name=first.getPropertyAsString("Name");
					String url=first.getPropertyAsString("Url");
					String ft=first.getPropertyAsString("FileType");
					String lu=first.getPropertyAsString("LastUpdated").toString();
					String state="0";
					MaterialItem lr=new MaterialItem(id, name, url, ft, lu, state);	
					materials.add(lr);
					//Toast.makeText(activity,"Material: "+ name, Toast.LENGTH_LONG).show();
				}
				
				Bundle b = new Bundle();
				b.putParcelableArrayList("materials", materials);
				Intent i = new Intent(activity,MaterialListActivity.class);
				i.putExtras(b);
				activity.startActivity(i);
				
			}
		});
		////
		
		
		serviceCall.execute();
		return null;
	}
	
	
	
	public SoapObject getCourseList() {

		String methodName = "GetCourseRooms";
		String namespace = "http://cil.rwth-aachen.de/l2p/services";
		String soapAction = "http://cil.rwth-aachen.de/l2p/services/GetCourseRooms";
		String urlToConnect = "https://www2.elearning.rwth-aachen.de/L2PWebservicesDev/L2PFoyerService.asmx"; //try ?WSDL at end
		String token = authentication.getAccessToken();
		ConnectForService serviceCall = new ConnectForService(this.activity,methodName , namespace, soapAction, urlToConnect);
		serviceCall.addProperty("userToken", token);
		
		serviceCall.setObserver(new Observer() {
			SoapObject obj=null;
			@Override
			public void update(Observable observable, Object data) {
				obj=(SoapObject)data;
				ArrayList<LearnRoom> rooms=new ArrayList<LearnRoom>();
				
				
				int count=obj.getPropertyCount();
				for(int i=0;i<count;i++)
				{
					SoapObject first =(SoapObject)obj.getProperty(i);
					String t=first.getPropertyAsString("Title");
					String id=first.getPropertyAsString("ID");
					LearnRoom lr=new LearnRoom(t,id);	
					rooms.add(lr);
				}
				
				Bundle b = new Bundle();
				b.putParcelableArrayList("rooms", rooms);
				Intent i = new Intent(activity,CourseListActivity.class);
				i.putExtras(b);
				activity.startActivity(i);
			}
		});
		
		serviceCall.execute();
		
		return null;
	}
	
	
	
}
