package de.example.androidlab;

import java.io.OutputStreamWriter;
import java.util.Observer;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class ConnectForService extends AsyncTask<String, Integer, SoapObject> {
	
	private String methodName, namespace, soapAction, urlToConnect;
	private ProgressDialog progDialog;
	private SoapObject result;
	private Context context;
	private SoapObject request;
	private Observer observer;
	
	public ConnectForService(Context context, String methodName, String namespace, String soapAction, String urlToConnect) {
		this.context = context;
		this.methodName = methodName;
		this.namespace = namespace;
		this.soapAction = soapAction;
		this.urlToConnect = urlToConnect;
		this.request = new SoapObject(this.namespace, this.methodName);
	}
	
	public void addProperty( String key, String value ) {
		this.request.addProperty(key,value);
	}
	
	public void setObserver(Observer observer) {
		this.observer = observer;
	}
	
	
	protected void onPreExecute() {
		super.onPreExecute();
		progDialog = ProgressDialog.show(context, "Posting",
				"Connecting the server", true, false);

	}

	@Override
	protected SoapObject doInBackground(String... arg) {
		
		
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.setOutputSoapObject(request);
		envelope.dotNet = true;

		try {
			HttpTransportSE androidHttpTransport = new HttpTransportSE(this.urlToConnect);
			androidHttpTransport.call(this.soapAction, envelope);
			 result = (SoapObject) envelope.getResponse();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	protected void onPostExecute(SoapObject s) {
		super.onPostExecute(s);
		progDialog.dismiss();
		observer.update(null, s);
		//Toast.makeText(context, String.valueOf("Test:" +s.toString()), Toast.LENGTH_LONG).show();
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

