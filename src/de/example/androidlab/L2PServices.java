package de.example.androidlab;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class L2PServices {
	
	public L2PServices() {
	}
	
	public SoapObject downloadDocumentItem(String token,String courseId, String fileId)throws AppException  {
		String methodName = "DownloadDocumentItem";
		String namespace = "http://cil.rwth-aachen.de/l2p/services";
		String soapAction = "http://cil.rwth-aachen.de/l2p/services/DownloadDocumentItem";
		String urlToConnect = "https://www2.elearning.rwth-aachen.de/L2PWebservicesDev/L2PLearningMaterialService.asmx"; //try ?WSDL at end
		
		ServiceRequestBuilder request = new ServiceRequestBuilder(methodName, namespace)
		.addProperty("userToken", token)
		.addProperty("courseId", courseId ) 
		.addProperty("fileId", fileId);
		return request.callService(urlToConnect, soapAction);
	}
	
	public SoapObject getDocumentsOverview(String token, String courseId)throws AppException  {
		String methodName = "GetDocumentsOverview";
		String namespace = "http://cil.rwth-aachen.de/l2p/services";
		String soapAction = "http://cil.rwth-aachen.de/l2p/services/GetDocumentsOverview";
		String urlToConnect = "https://www2.elearning.rwth-aachen.de/L2PWebservicesDev/L2PLearningMaterialService.asmx?WSDL"; //try ?WSDL at end
		
		ServiceRequestBuilder request = new ServiceRequestBuilder(methodName,namespace)
		.addProperty("userToken", token)
		.addProperty("courseId", courseId);
		return request.callService(urlToConnect, soapAction);
	}
	
	
	
	public SoapObject getCourseList(String token) throws AppException {

		String methodName = "GetCourseRooms";
		String namespace = "http://cil.rwth-aachen.de/l2p/services";
		String soapAction = "http://cil.rwth-aachen.de/l2p/services/GetCourseRooms";
		String urlToConnect = "https://www2.elearning.rwth-aachen.de/L2PWebservicesDev/L2PFoyerService.asmx"; //try ?WSDL at end
		
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
