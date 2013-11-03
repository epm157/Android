package de.example.androidlab;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Observer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class ConnectionToServer extends AsyncTask<Void, Void, String> {
	
	private String linkToConnect;
	private List<NameValuePair> attributes;
	private Observer observer;
	private ProgressDialog progressDialog;
	private String waitTitle;
	private String waitBody;
	private Context context;
	
	
	public ConnectionToServer() {
		attributes = new ArrayList<NameValuePair>();
	}
	
	
	public void setLinkToConnect(String linkToConnect) {
		this.linkToConnect = linkToConnect;
	}
	
	public void setObserver(Observer observer) {
		this.observer = observer;
	}
	
	public void setContext(Context context) {
		this.context = context;
	}
	
	public void setWaitBody(String waitBody) {
		this.waitBody = waitBody;
	}
	
	public void setWaitTitle(String waitTitle) {
		this.waitTitle = waitTitle;
	}
	
	public void addAttribute(String key, String value) {
		this.attributes.add(new BasicNameValuePair(key, value));
	}
	
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		this.progressDialog = ProgressDialog.show(this.context, this.waitTitle, this.waitBody);
	}
	
	@Override
	protected String doInBackground(Void... params) {
		String result = null;
		try {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(this.linkToConnect);					
			post.setEntity(new UrlEncodedFormEntity(this.attributes));
			HttpResponse response = client.execute(post);
			StatusLine status = response.getStatusLine();
			if (status.getStatusCode() != 200) {
				throw new HttpResponseException(
	                    status.getStatusCode(),
	                    status.getReasonPhrase());
			}
			HttpEntity entity = response.getEntity();
			InputStream ist = entity.getContent();
			ByteArrayOutputStream content = new ByteArrayOutputStream();
			int count = 0;
			byte[] buff = new byte[1024];
			while ((count = ist.read(buff)) != -1)
				content.write(buff, 0, count);
			result = new String(content.toByteArray());
			
		
		} catch (Exception e) {
			
			e.printStackTrace();

		}
		return result;
	}
	
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		progressDialog.dismiss();
		observer.update(null, result);
	}
	
}
