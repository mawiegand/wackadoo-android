package com.wackadoo.wackadoo_client.Activites;

import com.example.wackadoo_webview.R;
import com.wackadoo.wackadoo_client.Interfaces.LoginCallbackInterface;
import com.wackadoo.wackadoo_client.Interfaces.RegistrationCallbackInterface;
import com.wackadoo.wackadoo_client.Model.UserCredentials;
import com.wackadoo.wackadoo_client.Tasks.LoginAsyncTask;
import com.wackadoo.wackadoo_client.Tasks.RegisterAsyncTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class LoginScreenActivity extends Activity implements RegistrationCallbackInterface, LoginCallbackInterface{
	
		private Button loginButton;
		private Button shopButton;
		private AnimationDrawable loginButtonAnimation;
		private UserCredentials userCredentials;
	
		@Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_loginscreen);
		    loginButton = (Button) findViewById(R.id.loginButton);
		    shopButton = (Button) findViewById(R.id.shopButton);
		    
		    userCredentials = new UserCredentials(this.getApplicationContext());
		    
		    this.setUpButtonListeners();
		    this.setUpLoginButtonAnimation();
		    loginButtonAnimation.start();
		    userCredentials.loadCredentials();
		}

	   private void setUpLoginButtonAnimation() {
		   loginButton.setBackgroundResource(R.drawable.animationlist_loginbutton);
		   loginButtonAnimation = (AnimationDrawable) loginButton.getBackground();
		}

	   private void setUpButtonListeners() {
		   this.setUpLoginButton();
		   this.setUpShopButton();
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
	   

		private void setUpLoginButton() {
			   loginButton.setOnTouchListener(new View.OnTouchListener() {
					
				@SuppressLint("NewApi")
				@Override
				   public boolean onTouch(View v, MotionEvent event) {
					   switch ( event.getAction() ) {
				    		case MotionEvent.ACTION_DOWN: 
				    			{
				    				loginButton.setBackground(getResources().getDrawable(R.drawable.title_play_button_active));
				    				break;
				    			}
				    		case MotionEvent.ACTION_UP: 
				    			{
				    				setUpLoginButtonAnimation();
				    				loginButtonAnimation.start();
				    				break;
				    			}
					   }
					   return false;
					}
			   });
			   
			   loginButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						triggerLogin();
					}
				});
		}

		private void setUpShopButton() {
		   shopButton.setOnTouchListener(new View.OnTouchListener() {
				
				@SuppressLint("NewApi")
				@Override
				   public boolean onTouch(View v, MotionEvent event) {
					   switch ( event.getAction() ) {
				    		case MotionEvent.ACTION_DOWN: 
				    			{
				    				shopButton.setBackground(getResources().getDrawable(R.drawable.title_shop_button_active));
				    				break;
				    			}
				    		case MotionEvent.ACTION_UP: 
				    			{
				    				shopButton.setBackground(getResources().getDrawable(R.drawable.title_shop_button));
				    				break;
				    			}
					   }
					   return false;
					}
			   });
			   
		   shopButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						///// TODO: Insert Intent to Shop
					}
				});
	}
	

		private void triggerLogin() {
			if(userCredentials.getIdentifier().length() > 0)
			{
				new LoginAsyncTask(this, getApplicationContext()).execute();
			}
			else
			{
				new RegisterAsyncTask(this).execute();	
			}
		}

		@Override
		public void onRegistrationCompleted(String identifier, String clientID) {
			userCredentials.setIdentifier(identifier);
			userCredentials.setClientID(clientID);
			this.triggerLogin();
		}
		
		@Override
		public void loginCallback(String accessToken, String expiration) {
			this.startLogin(accessToken, expiration, userCredentials.getClientID());
		}
		
		private void startLogin(String accessToken, String expiration, String userId) {
			Intent intent = new Intent(LoginScreenActivity.this, WackadooWebviewActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("accessToken", accessToken);
			bundle.putString("expiration", expiration);
			bundle.putString("userId", userId);
			intent.putExtras(bundle);
			startActivity(intent);
		}
		
}