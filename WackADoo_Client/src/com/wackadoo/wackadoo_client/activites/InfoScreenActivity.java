package com.wackadoo.wackadoo_client.activites;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.wackadoo.wackadoo_client.R;

public class InfoScreenActivity extends Activity {

	private TextView backBtn, supportBtn, wikiBtn, copyrightBtn, websiteBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_infoscreen);
		
		setUpButtons();
	}
	
	private void setUpButtons() {
		setUpBackBtn();
		setUpSupportBtn();
		setUpWebsiteBtn();
		setUpWikiBtn();
		setUpCopyrightBtn();
		
	}
	
	private void setUpBackBtn() {
		backBtn = (TextView) findViewById(R.id.infoscreenTopbarBack);
		
		backBtn.setEnabled(true);
		backBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				switch (e.getAction()) {
		    		case MotionEvent.ACTION_DOWN: 
		    			backBtn.setTextColor(getResources().getColor(R.color.textbox_orange_active));
		    			break;

		    		case MotionEvent.ACTION_UP: 
		    			backBtn.setTextColor(getResources().getColor(R.color.textbox_orange));
		    			break;
			    }
				return false;
			}
		});
		   
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
	   	});
			
	}

	private void setUpCopyrightBtn() {
		copyrightBtn = (TextView) findViewById(R.id.infoscreen_copyright_btn);
		
		copyrightBtn.setEnabled(true);
		copyrightBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				switch (e.getAction()) {
		    		case MotionEvent.ACTION_DOWN: 
		    			copyrightBtn.setTextColor(getResources().getColor(R.color.textbox_orange_active));
		    			break;

		    		case MotionEvent.ACTION_UP: 
		    			copyrightBtn.setTextColor(getResources().getColor(R.color.textbox_orange));
		    			break;
			    }
				return false;
			}
		});
		   
		copyrightBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showCopyrightDialog();
			}
	   	});
	}

	private void setUpSupportBtn() {
		supportBtn = (TextView) findViewById(R.id.infoscreen_supportbtn);
		
		supportBtn.setEnabled(true);
		supportBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				switch (e.getAction()) {
		    		case MotionEvent.ACTION_DOWN: 
		    			supportBtn.setTextColor(getResources().getColor(R.color.textbox_orange_active));
		    			break;

		    		case MotionEvent.ACTION_UP: 
		    			supportBtn.setTextColor(getResources().getColor(R.color.textbox_orange));
		    			break;
			    }
				return false;
			}
		});
		   
		supportBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
	   	});
	}

	private void setUpWebsiteBtn() {
		websiteBtn = (TextView) findViewById(R.id.infoscreen_websitebtn);
		
		websiteBtn.setEnabled(true);
		websiteBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				switch (e.getAction()) {
		    		case MotionEvent.ACTION_DOWN: 
		    			websiteBtn.setTextColor(getResources().getColor(R.color.textbox_orange_active));
		    			break;

		    		case MotionEvent.ACTION_UP: 
		    			websiteBtn.setTextColor(getResources().getColor(R.color.textbox_orange));
		    			break;
			    }
				return false;
			}
		});
		   
		websiteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showWebsite();
			}
	   	});
	}

	private void setUpWikiBtn() {
		wikiBtn = (TextView) findViewById(R.id.infoscreen_wikibtn);
		
		wikiBtn.setEnabled(true);
		wikiBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				switch (e.getAction()) {
		    		case MotionEvent.ACTION_DOWN: 
		    			wikiBtn.setTextColor(getResources().getColor(R.color.textbox_orange_active));
		    			break;

		    		case MotionEvent.ACTION_UP: 
		    			wikiBtn.setTextColor(getResources().getColor(R.color.textbox_orange));
		    			break;
			    }
				return false;
			}
		});
		   
		wikiBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showWiki();
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
