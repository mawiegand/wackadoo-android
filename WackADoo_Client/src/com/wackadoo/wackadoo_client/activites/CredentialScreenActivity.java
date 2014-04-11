package com.wackadoo.wackadoo_client.activites;

import com.example.wackadoo_webview.R;
import com.wackadoo.wackadoo_client.interfaces.LoginCallbackInterface;
import com.wackadoo.wackadoo_client.model.UserCredentials;
import com.wackadoo.wackadoo_client.tasks.LoginAsyncTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class CredentialScreenActivity extends Activity {
	
	private UserCredentials userCredentials;
	private Button signInButton;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_credentialscreen);    
		this.userCredentials = new UserCredentials(this.getApplicationContext());
		
		this.signInButton = (Button) findViewById(R.id.signInButton);
		
		this.setUpButtonListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	getMenuInflater().inflate(R.menu.main, menu);
	 	return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
	    if (id == R.id.action_settings) {
	    	return true;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	private void setUpButtonListener() {
		this.setUpSignInButton();
	}

	
	private void setUpSignInButton() {
		this.signInButton.setOnTouchListener(new View.OnTouchListener() {
			
			@SuppressLint("NewApi")
			@Override
			   public boolean onTouch(View v, MotionEvent event) {
				   switch ( event.getAction() ) {
			    		case MotionEvent.ACTION_DOWN: 
			    			{
			    				signInButton.setTextColor(Color.GRAY);
			    				break;
			    			}
			    		case MotionEvent.ACTION_UP: 
			    			{
			    				signInButton.setTextColor(Color.parseColor("#FF7F24"));;
			    				break;
			    			}
				   }
				   return false;
				}
		   });
		this.signInButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				triggerLogin();
			}

		});

	}
	

	protected void triggerLogin() {
		//TODO: trigger login
	}
}
