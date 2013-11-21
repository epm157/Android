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
