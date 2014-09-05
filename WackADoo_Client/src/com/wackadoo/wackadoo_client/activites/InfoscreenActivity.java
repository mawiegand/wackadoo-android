package com.wackadoo.wackadoo_client.activites;

import com.wackadoo.wackadoo_client.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class InfoscreenActivity extends Activity {

	private TextView backBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_infoscreen);
		
		setUpBackBtn();
	}
	
	public void setUpBackBtn() {
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
}
