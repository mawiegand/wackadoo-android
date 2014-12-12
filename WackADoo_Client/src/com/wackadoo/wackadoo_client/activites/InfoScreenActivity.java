package com.wackadoo.wackadoo_client.activites;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.helper.StaticHelper;

public class InfoScreenActivity extends WackadooActivity {

	private TextView backBtn, supportBtn, wikiBtn, copyrightBtn, websiteBtn;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_infoscreen);
		
		setUpUi();
		setUpButtons();
	}

	// set up interface elements
	private void setUpUi() {
		supportBtn = (TextView) findViewById(R.id.infoscreen_supportbtn);
		websiteBtn = (TextView) findViewById(R.id.infoscreen_websitebtn);
		wikiBtn = (TextView) findViewById(R.id.infoscreen_wikibtn);
		copyrightBtn = (TextView) findViewById(R.id.infoscreen_copyright_btn);
		backBtn = (TextView) findViewById(R.id.infoscreenTopbarBack);
	}

	// set up touchlistener for buttons
	private void setUpButtons() {
		OnTouchListener touchListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				int action = e.getActionMasked();
				if (action == MotionEvent.ACTION_DOWN) {
					((TextView) v).setTextColor(getResources().getColor(R.color.textbox_orange_active));
				
				} else if (action == MotionEvent.ACTION_CANCEL) {
					((TextView) v).setTextColor(getResources().getColor(R.color.textbox_orange));
					
				} else if (action == MotionEvent.ACTION_UP){
					((TextView) v).setTextColor(getResources().getColor(R.color.textbox_orange));
					
					switch(v.getId()) {
						case R.id.infoscreen_supportbtn:
							showSupport();
							break;
							
						case R.id.infoscreen_websitebtn:
							showWebsite();
							break;
							
						case R.id.infoscreen_wikibtn:
							showWiki();
							break;
							
						case R.id.infoscreen_copyright_btn:
							showCopyrightDialog();
							break;
							
						case R.id.infoscreenTopbarBack:
							soundManager.setContinueMusic(true);
			    			finish();
							break;
					}
				}
				return true;
			}
		};
		supportBtn.setOnTouchListener(touchListener);
		websiteBtn.setOnTouchListener(touchListener);
		wikiBtn.setOnTouchListener(touchListener);
		copyrightBtn.setOnTouchListener(touchListener);
		backBtn.setOnTouchListener(touchListener);
	}
	
	protected void showSupport() {
		Intent intent = new Intent(Intent.ACTION_SENDTO); 
        intent.setType("text/plain");
        intent.setData(Uri.parse("mailto:support@5dlab.com")); 
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.infoscreen_support_subject));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        startActivity(intent);		
	}

	// show dialog with copyright information
    private void showCopyrightDialog() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(getResources().getString(R.string.infoscreen_copyright_btn))
    		   .setMessage(getResources().getString(R.string.infoscreen_copyright))
       		   .setPositiveButton(getResources().getString(R.string.alert_ok_button), null);
    		   
        AlertDialog dialog = builder.create();
	    dialog.show();
	    StaticHelper.styleDialog(this, dialog);
    }

    // open website in browser
    private void showWebsite() {
    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.wack-a-doo.com"));
    	startActivity(browserIntent);
    }
    
    // open wiki website in browser
    private void showWiki() {
    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://wiki.wack-a-doo.com"));
    	startActivity(browserIntent);
    }
}
