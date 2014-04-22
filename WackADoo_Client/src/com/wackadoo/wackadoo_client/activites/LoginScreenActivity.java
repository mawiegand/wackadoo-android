package com.wackadoo.wackadoo_client.activites;

import com.example.wackadoo_webview.R;
import com.wackadoo.wackadoo_client.interfaces.GameLoginCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.CreateAccountCallbackInterface;
import com.wackadoo.wackadoo_client.model.UserCredentials;
import com.wackadoo.wackadoo_client.tasks.GameLoginAsyncTask;

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
import android.widget.Toast;

public class LoginScreenActivity extends Activity implements CreateAccountCallbackInterface, GameLoginCallbackInterface{
	
		private ImageButton playButton, accountmanagerButton;
		private Button shopButton;
		private AnimationDrawable playButtonAnimation;
		private UserCredentials userCredentials;
	
		@SuppressLint("NewApi")
		@Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
		    requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.activity_loginscreen);
		    playButton = (ImageButton) findViewById(R.id.loginButton);
		    accountmanagerButton = (ImageButton) findViewById(R.id.accountmanagerButton);
		    shopButton = (Button) findViewById(R.id.shopButton);
		    
		    userCredentials = new UserCredentials(this.getApplicationContext());
		    
		    this.setUpButtonListeners();
		    this.setUpPlayButtonAnimation();
		    this.triggerLogin();
		}

	private void setUpPlayButtonAnimation() {
			playButton.setImageResource(R.anim.animationlist_loginbutton);
			playButtonAnimation = (AnimationDrawable) playButton.getDrawable();
		}

	   private void setUpButtonListeners() {
		   this.setUpPlayButton();
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
		playButton.setEnabled(true);
		shopButton.setEnabled(true);
		playButtonAnimation.start();
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
	   

		private void setUpPlayButton() {
			playButton.setEnabled(true);
			playButton.setOnTouchListener(new View.OnTouchListener() {
					
				@SuppressLint("NewApi")
				@Override
				   public boolean onTouch(View v, MotionEvent event) {
					   switch ( event.getAction() ) {
				    		case MotionEvent.ACTION_DOWN: 
				    			{
				    				playButton.setImageResource(R.drawable.title_play_button_active);
				    				break;
				    			}
				    		case MotionEvent.ACTION_UP: 
				    			{
				    				setUpPlayButtonAnimation();
				    				playButtonAnimation.start();
				    				break;
				    			}
					   }
					   return false;
					}
			   });
			   
			playButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						playButton.setEnabled(false);
						triggerPlayGame();
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
			String identifier = userCredentials.getIdentifier();
			String accessToken = userCredentials.getAccessToken().getToken();
			String email = userCredentials.getEmail();
			if(identifier.length() > 0 || accessToken.length() > 0 || email.length() > 0){
				if(userCredentials.getAccessToken().isExpired()) {
					new GameLoginAsyncTask(this, getApplicationContext(), this.userCredentials).execute();
				}
			}
			else {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_failed_toast), Toast.LENGTH_LONG).show();	
			}
		}

		private void triggerPlayGame() {
			String accessToken = this.userCredentials.getAccessToken().getToken();
			String tokenExpiration = this.userCredentials.getAccessToken().getExpireCode();
			String userId = this.userCredentials.getClientID();
			if(accessToken != null && tokenExpiration != null && !this.userCredentials.getAccessToken().isExpired()) {
				this.startGame(accessToken, tokenExpiration, userId);
			} else {
				new GameLoginAsyncTask(this, getApplicationContext(), userCredentials).execute();
			}
		}
		
		private void startGame(String accessToken, String expiration, String userId) {
			Intent intent = new Intent(LoginScreenActivity.this, WackadooWebviewActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("accessToken", accessToken);
			bundle.putString("expiration", expiration);
			bundle.putString("userId", userId);
			intent.putExtras(bundle);
			startActivity(intent);
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
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_success_toast), Toast.LENGTH_LONG).show();
		}
}