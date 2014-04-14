package com.wackadoo.wackadoo_client.activites;

import com.example.wackadoo_webview.R;
import com.wackadoo.wackadoo_client.interfaces.LoginCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.RegistrationCallbackInterface;
import com.wackadoo.wackadoo_client.model.UserCredentials;
import com.wackadoo.wackadoo_client.tasks.LoginAsyncTask;
import com.wackadoo.wackadoo_client.tasks.RegisterAsyncTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;

public class LoginScreenActivity extends Activity implements RegistrationCallbackInterface, LoginCallbackInterface{
	
		private ImageButton loginButton, accountmanagerButton;
		private Button shopButton;
		private AnimationDrawable loginButtonAnimation;
		private UserCredentials userCredentials;
	
		@SuppressLint("NewApi")
		@Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
		    requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.activity_loginscreen);
		    loginButton = (ImageButton) findViewById(R.id.loginButton);
		    accountmanagerButton = (ImageButton) findViewById(R.id.accountmanagerButton);
		    shopButton = (Button) findViewById(R.id.shopButton);
		    
		    userCredentials = new UserCredentials(this.getApplicationContext());
		    
		    this.setUpButtonListeners();
		    this.setUpLoginButtonAnimation();
		}

	private void setUpLoginButtonAnimation() {
			loginButton.setImageResource(R.anim.animationlist_loginbutton);
			loginButtonAnimation = (AnimationDrawable) loginButton.getDrawable();
		}

	   private void setUpButtonListeners() {
		   this.setUpLoginButton();
		   this.setUpShopButton();
		   this.setUpAccountmanagerButton();
	   }


	@Override
	   public boolean onCreateOptionsMenu(Menu menu) {
	        getMenuInflater().inflate(R.menu.main, menu);
	        return true;
	   }
	
	@Override
	protected void onResume() {
		super.onResume();
		this.userCredentials = new UserCredentials(getApplicationContext());
		loginButton.setEnabled(true);
		shopButton.setEnabled(true);
		loginButtonAnimation.start();
		//TODO: If(user not logged in && credentials available)
		// -> Login
	};

	   @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        int id = item.getItemId();
	        if (id == R.id.action_settings) {
	        	return true;
	        }
		    return super.onOptionsItemSelected(item);
	    }
	   

		private void setUpLoginButton() {
			loginButton.setEnabled(true);
			loginButton.setOnTouchListener(new View.OnTouchListener() {
					
				@SuppressLint("NewApi")
				@Override
				   public boolean onTouch(View v, MotionEvent event) {
					   switch ( event.getAction() ) {
				    		case MotionEvent.ACTION_DOWN: 
				    			{
				    				loginButton.setImageResource(R.drawable.title_play_button_active);
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
						loginButton.setEnabled(false);
						triggerLogin();
					}
				});
		}

		private void setUpShopButton() {
			shopButton.setEnabled(true);
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
						Intent intent = new Intent(LoginScreenActivity.this, ShopActivity.class);
						startActivity(intent);
						shopButton.setEnabled(false);
					}
				});
	}
	
		private void setUpAccountmanagerButton() {
			accountmanagerButton.setEnabled(true);
			accountmanagerButton.setOnTouchListener(new View.OnTouchListener() {
				
				@SuppressLint("NewApi")
				@Override
				   public boolean onTouch(View v, MotionEvent event) {
					   switch ( event.getAction() ) {
				    		case MotionEvent.ACTION_DOWN: 
				    			{
				    				accountmanagerButton.setImageResource(R.drawable.title_change_button_active);
				    				break;
				    			}
				    		case MotionEvent.ACTION_UP: 
				    			{
				    				accountmanagerButton.setImageResource(R.drawable.title_change_button);
				    				break;
				    			}
					   }
					   return false;
					}
			   });
			   
			accountmanagerButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(LoginScreenActivity.this, AccountManagerActivity.class);
						startActivity(intent);
					}
				});
	}
		
		
		private void triggerLogin() {
			if(userCredentials.getIdentifier().length() > 0){
				if(userCredentials.getAccessToken().isExpired()) {
					new LoginAsyncTask(this, getApplicationContext(), this.userCredentials).execute();
				}
				else {
					this.startLogin(this.userCredentials.getAccessToken().getToken(), this.userCredentials.getAccessToken().getExpireCode(), this.userCredentials.getClientID());
				}
			}
			else {
				new RegisterAsyncTask(this).execute();	
			}
		}

		@Override
		public void onRegistrationCompleted(String identifier, String clientID, String nickname) {
			userCredentials.setIdentifier(identifier);
			userCredentials.setClientID(clientID);
			userCredentials.setUsername(nickname);
			this.triggerLogin();
		}
		
		@Override
		public void loginCallback(String accessToken, String expiration) {
			this.userCredentials.generateNewAccessToken(accessToken, expiration);
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