package de.example.androidlab;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.content.SharedPreferences;

public class L2P_Services {
	
	private SharedPreferences pref;
	
	public L2P_Services( SharedPreferences pref) {this.pref = pref;}
	
	public String getAuthorizationURL() throws CommonException {
		L2P_Authentication l2pAuth = new L2P_Authentication(pref);
		l2pAuth.registerDevice();
		
		String s1 = pref.getString("verification_url", null);
		String s2 = pref.getString("user_code", null);
		return s1+"/?q=verify&d=" + s2;

	}
	
	public SoapObject downloadDocumentItem(String courseId)throws CommonException  {
		String methodName = "DownloadDocumentItem";
		String namespace = "http://cil.rwth-aachen.de/l2p/services";
		String soapAction = "http://cil.rwth-aachen.de/l2p/services/DownloadDocumentItem";
		String urlToConnect = "https://www2.elearning.rwth-aachen.de/L2PWebservicesDev/L2PLearningMaterialService.asmx"; //try ?WSDL at end
		
		L2P_Authentication l2p_auth = new L2P_Authentication(pref);
		String token = l2p_auth.getAccessToken();
		ServiceRequestBuilder request = new ServiceRequestBuilder(methodName, namespace)
		.addProperty("userToken", token)
		.addProperty("courseId", courseId ) //courseID
		.addProperty("fileId", "4");
		return request.callService(urlToConnect, soapAction);
	}
	
	public SoapObject getDocumentsOverview(String courseId)throws CommonException  {
		String methodName = "GetDocumentsOverview";
		String namespace = "http://cil.rwth-aachen.de/l2p/services";
		String soapAction = "http://cil.rwth-aachen.de/l2p/services/GetDocumentsOverview";
		String urlToConnect = "https://www2.elearning.rwth-aachen.de/L2PWebservicesDev/L2PLearningMaterialService.asmx?WSDL"; //try ?WSDL at end
		L2P_Authentication l2p_auth = new L2P_Authentication(pref);
		String token = l2p_auth.getAccessToken();
		
		ServiceRequestBuilder request = new ServiceRequestBuilder(methodName,namespace)
		.addProperty("userToken", token)
		.addProperty("courseId", courseId);
		return request.callService(urlToConnect, soapAction);

		/*
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
		*/
		

	}
	
	
	
	public SoapObject getCourseList() throws CommonException {

		String methodName = "GetCourseRooms";
		String namespace = "http://cil.rwth-aachen.de/l2p/services";
		String soapAction = "http://cil.rwth-aachen.de/l2p/services/GetCourseRooms";
		String urlToConnect = "https://www2.elearning.rwth-aachen.de/L2PWebservicesDev/L2PFoyerService.asmx"; //try ?WSDL at end
		
		L2P_Authentication l2p_auth = new L2P_Authentication(pref);
		String token = l2p_auth.getAccessToken();
		
		ServiceRequestBuilder request = new ServiceRequestBuilder(methodName,namespace)
		.addProperty("userToken", token);
		return request.callService(urlToConnect, soapAction);
	}
	

	private class ServiceRequestBuilder {
		private SoapObject request;
		public ServiceRequestBuilder(String methodName, String namespace) {
			this.request = new SoapObject(namespace, methodName);
		}
		public ServiceRequestBuilder addProperty( String key, String value ) {
			this.request.addProperty(key,value);
			return this;
		}
		
		public SoapObject callService(String urlToConnect,String soapAction) {
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);
			envelope.dotNet = true;
			SoapObject result = null;
			try {
				HttpTransportSE androidHttpTransport = new HttpTransportSE(urlToConnect);
				androidHttpTransport.call(soapAction, envelope);
				 result = (SoapObject) envelope.getResponse();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}
		
	}
	
}
