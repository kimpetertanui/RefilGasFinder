package com.libraries.asynctask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public class MGAsyncTaskNoDialog extends AsyncTask<Void, Void, String> {

	public Activity activity;
	public Object object;

	public int tag = 0;
	public int listTag = 0;

	OnMGAsyncTaskListenerNoDialog mCallback;

	public interface OnMGAsyncTaskListenerNoDialog {
        public void onAsyncTaskDoInBackground(MGAsyncTaskNoDialog asyncTask);
        public void onAsyncTaskProgressUpdate(MGAsyncTaskNoDialog asyncTask);
        public void onAsyncTaskPostExecute(MGAsyncTaskNoDialog asyncTask);
        public void onAsyncTaskPreExecute(MGAsyncTaskNoDialog asyncTask);
    }

	public void setMGAsyncTaskListener(OnMGAsyncTaskListenerNoDialog listener) {
		try {
            mCallback = (OnMGAsyncTaskListenerNoDialog) listener;
        } catch (ClassCastException e) {
            throw new ClassCastException(this.toString() + "Did not implement OnMGAsyncTaskListenerNoDialog");
        }
	}

	public MGAsyncTaskNoDialog(Activity activity) {
		this.activity = activity;
	}
	
	public void startAsyncTask() {
		this.execute();
	}
	
	@Override
	protected String doInBackground(Void... params)  {
		mCallback.onAsyncTaskDoInBackground(this);
		return "";
	}
	
	@Override
	protected void onPostExecute(String result)  {
		// execution of result of Long time consuming operation. parse json data
		mCallback.onAsyncTaskPostExecute(this);
	}

	@Override
	protected void onPreExecute() {
		// Things to be done before execution of long running operation. For example showing ProgessDialog
		mCallback.onAsyncTaskPreExecute(this);
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		// Things to be done while execution of long running operation is in progress. For example updating ProgessDialog
		mCallback.onAsyncTaskProgressUpdate(this);
	}
}
