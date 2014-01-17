package de.example.androidlab;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;

public abstract class AppAsyncTask<Params, Result> extends AsyncTask<Params, Void, Result> {

	private ProgressDialog dialog;
	private Context context;
	private String title;
	private String message;
	
	
	public AppAsyncTask(Context context, String title, String message) {
		super();
		this.context = context;
		this.title = title;
		this.message = message;
	}


	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dialog = ProgressDialog.show(context, title, message );
	}
	
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
		AlertDialog.Builder builder = new AlertDialog.Builder(context)
		.setTitle("Cancelled")
		.setMessage("Following task was cancelled: " + message)
		.setNeutralButton("Close", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
	
	@Override
	protected void onPostExecute(Result result) {
		super.onPostExecute(result);
		dialog.dismiss();
	}
}
