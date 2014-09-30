package com.wackadoo.wackadoo_client.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.facebook.Session;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.interfaces.FacebookLoginCallbackInterface;

public class FacebookLoginAsyncTask extends AsyncTask<String, Integer, Double>{

	private Context context;
	    
	public FacebookLoginAsyncTask(Context context) {
	    this.context = context;
	}
		
	@Override
	protected Double doInBackground(String... params) {
		try {
			loginWithFacebook();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
		
	public  void loginWithFacebook() throws Throwable
	{
		Session loginSession = Session.getActiveSession();
		if(loginSession == null || loginSession.getState().isClosed()) {
			String appId = context.getResources().getString(R.string.facebookAppId);
			loginSession = new Session.Builder(context).setApplicationId(appId).build();
		}
		((FacebookLoginCallbackInterface) context).onFacebookRegistrationCompleted(loginSession);
	}
	
}
