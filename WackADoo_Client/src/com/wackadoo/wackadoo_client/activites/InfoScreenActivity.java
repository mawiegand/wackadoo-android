package com.wackadoo.wackadoo_client.activites;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;

import com.wackadoo.wackadoo_client.R;

public class InfoScreenActivity extends Activity {

	private TextView backBtn, supportBtn, wikiBtn, copyrightBtn, websiteBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_infoscreen);
		
		supportBtn = (TextView) findViewById(R.id.infoscreen_supportbtn);
		websiteBtn = (TextView) findViewById(R.id.infoscreen_websitebtn);
		wikiBtn = (TextView) findViewById(R.id.infoscreen_wikibtn);
		copyrightBtn = (TextView) findViewById(R.id.infoscreen_copyright_btn);
		
		setUpBackBtn();
		setUpLinkBtns();
	}
	
	private void setUpLinkBtns() {
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
					}
				}
				return true;
			}
		};
		supportBtn.setOnTouchListener(touchListener);
		websiteBtn.setOnTouchListener(touchListener);
		wikiBtn.setOnTouchListener(touchListener);
		copyrightBtn.setOnTouchListener(touchListener);
	}
	
	private void setUpBackBtn() {
		backBtn = (TextView) findViewById(R.id.infoscreenTopbarBack);
		backBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				switch (e.getAction()) {
		    		case MotionEvent.ACTION_DOWN: 
		    			backBtn.setTextColor(getResources().getColor(R.color.textbox_orange_active));
		    			break;

		    		case MotionEvent.ACTION_UP: 
		    			backBtn.setTextColor(getResources().getColor(R.color.textbox_orange));
		    			finish();
		    			break;
			    }
				return true;
			}
		});
	}

    private void showCopyrightDialog() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(getResources().getString(R.string.infoscreen_copyright_btn))
    		   .setMessage(getResources().getString(R.string.infoscreen_copyright))
       		   .setPositiveButton(getResources().getString(R.string.alert_ok_button), null)
    		   .show();
    }

    private void showWebsite() {
    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.wack-a-doo.com"));
    	startActivity(browserIntent);
    }
    
    private void showWiki() {
    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://wiki.wack-a-doo.com"));
    	startActivity(browserIntent);
    }
}
