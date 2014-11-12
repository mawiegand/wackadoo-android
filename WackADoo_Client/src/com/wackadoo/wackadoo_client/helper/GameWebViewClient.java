package com.wackadoo.wackadoo_client.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.MailTo;
import android.net.Uri;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.activites.ShopActivity;

public class GameWebViewClient extends WebViewClient { 

	private static final String TAG = GameWebViewClient.class.getSimpleName();
	private static final String ACTIVITY_SCHEME = "show:";
	private static final String ACTIVITY_SCHEME_SHOP = "show:shop_activity";
	private static final String SOUND_SCHEME = "playsound:";
	private static final String SOUND_SCHEME_CLICK = "playsound:clicksound";
	private static final String SOUND_SCHEME_THEME = "playsound:themesong";
	private static final String MAIL_SCHEME = "mailto:";
	
	private Context context;
	
	public GameWebViewClient(Context context) {
		this.context = context;
	}
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// -----------------------------------
		// ---------- open activity ----------
		if (url.startsWith(ACTIVITY_SCHEME)) {
			// stop loading of web view in order to stay on the current page
			view.stopLoading();
			
//			if (url.contains("shop_activity")) {
			if (url.equals(ACTIVITY_SCHEME_SHOP)) {
				Intent intent = new Intent(context, ShopActivity.class);
				context.startActivity(intent);
				return false;
			}
		
		// ---------------------------------------
		// ----------- play sound file -----------
		} else if (url.startsWith(SOUND_SCHEME)) {
			view.stopLoading();
			
			// play click sound
			if (url.contains("clicksound")) {
				MediaPlayer.create(context, R.raw.click)
						   .start();
				
			// play theme song
			} else if (url.contains("themesong")) {
				MediaPlayer.create(context, R.raw.themesong)
				           .start();
			}
			return false;

		
		// ---------------------------------------
		// ------------- send mail  -------------- 
		} else if (url.startsWith(MAIL_SCHEME)) {
			view.stopLoading();
			
			// get url arguments of mailto link
			MailTo mt = MailTo.parse(url);
			
			// create intent for mail application
	        Intent intent = new Intent(Intent.ACTION_SENDTO); 
	        intent.setType("message/rfc822");
	        intent.setData(Uri.parse("mailto:" + mt.getTo()));
	        intent.putExtra(Intent.EXTRA_SUBJECT, mt.getSubject());
	        intent.putExtra(Intent.EXTRA_TEXT, mt.getBody());
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
	        ((Activity) context).startActivity(intent);
			return false;
		} 
		view.loadUrl(url);
		return true;
	}

	@Override
	public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
		handler.proceed();
	}
	
}
