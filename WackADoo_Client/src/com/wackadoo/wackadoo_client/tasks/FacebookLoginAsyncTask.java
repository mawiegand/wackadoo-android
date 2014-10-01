package com.wackadoo.wackadoo_client.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.facebook.Session;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.interfaces.FacebookLoginCallbackInterface;

public class FacebookLoginAsyncTask extends AsyncTask<String, Integer, Boolean>{

	private Context context;
	    
	public FacebookLoginAsyncTask(Context context) {
	    this.context = context;
	}
		
	@Override
	protected Boolean doInBackground(String... params) {
		Session loginSession = Session.getActiveSession();
		if (loginSession == null || loginSession.getState().isClosed()) {
			String appId = context.getResources().getString(R.string.facebookAppId);
			loginSession = new Session.Builder(context).setApplicationId(appId).build();
		}
		return true;
	}
		
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		
		if (result) {
//			((FacebookLoginCallbackInterface) context).onFacebookRegistrationCompleted(loginSession);
		}
	}
}
