package de.example.androidlab;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class ConnectForService extends
		AsyncTask<String, Integer, String> {

	public final static String TAG = "ConnectForService";
	private ProgressDialog progDialog;
	private String access_token;
	SoapObject result;
	private Context context;
	
	public void setContext(Context context) {
		this.context = context;
	}

	protected void onPreExecute() {
		super.onPreExecute();
		progDialog = ProgressDialog.show(context, "Posting",
				"Connecting the server", true, false);
		Log.d(TAG, "onPreExecute");

	}

	@Override
	protected String doInBackground(String... arg) {
		access_token = arg[0];
//		courseId=arg[1];
//		title=arg[2];
//		body=arg[3];
//		Log.d(TAG, "userToken passed= " + access_token);
//		Log.d(TAG, "courseId passed= " + courseId);
//		Log.d(TAG, "title passed= " + title);
//		Log.d(TAG, "body passed= " + body);
		
		String SOAP_ACTION = "http://cil.rwth-aachen.de/l2p/services/DownloadDocumentItem";
		String NAMESPACE = "http://cil.rwth-aachen.de/l2p/services";
		String METHOD_NAME = "DownloadDocumentItem";
		String URL = "https://www2.elearning.rwth-aachen.de/L2PWebservicesDev/L2PLearningMaterialService.asmx?WSDL"; //try ?WSDL at end
		SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
		request.addProperty("userToken", access_token);
		request.addProperty("courseId", "13ws-04405");
		request.addProperty("fileId", "4");
	//	request.addProperty("body",  body);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;

		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
			androidHttpTransport.call(SOAP_ACTION, envelope);
			 result = (SoapObject) envelope.getResponse();
			Log.d(TAG, "result from envelope= " + result.toString());			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void onPostExecute(String s) {
		super.onPostExecute(s);
		progDialog.dismiss();
		Toast.makeText(context, String.valueOf(result.toString().length()), Toast.LENGTH_LONG).show();
		
		String str=result.toString();
		String str2=str.substring(0, 10000);
		for (int i=0; i < 10; i++)
		{
			Toast.makeText(context, str2, Toast.LENGTH_LONG).show();
		}
		
		//writeToFile(str2);
		
		
	}

	private void writeToFile(String data) {
	    try {
	        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.getApplicationContext().openFileOutput("config.txt", Context.MODE_WORLD_READABLE));
	        outputStreamWriter.write(data);
	        outputStreamWriter.close();
	    	
	    	Toast.makeText(context, "OK", Toast.LENGTH_LONG).show();
	    }
	    catch (Exception e) {
	        Log.e("Exception", "File write failed: " + e.toString());
	    } 
	}

}

