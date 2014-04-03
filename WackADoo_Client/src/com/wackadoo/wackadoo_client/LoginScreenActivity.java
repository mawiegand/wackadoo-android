package com.wackadoo.wackadoo_client;

import com.example.wackadoo_webview.R;

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

public class LoginScreenActivity extends Activity{
	
		private Button loginButton;
		private Button shopButton;
		private AnimationDrawable loginButtonAnimation;
	
		@Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_loginscreen);
		    loginButton = (Button) findViewById(R.id.loginButton);
		    shopButton = (Button) findViewById(R.id.shopButton);
		    
		    this.setUpButtonListeners();
		    this.setUpLoginButtonAnimation();
		    loginButtonAnimation.start();
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
						Intent intent = new Intent(LoginScreenActivity.this, WackadooWebviewActivity.class);
				        startActivity(intent);
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
}