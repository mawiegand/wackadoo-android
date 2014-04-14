package com.wackadoo.wackadoo_client.activites;

import com.example.wackadoo_webview.R;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.wackadoo.wackadoo_client.model.UserCredentials;

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
import android.widget.EditText;
import android.widget.Toast;

public class CredentialScreenActivity extends Activity {
	
	private UserCredentials userCredentials;
	private Button signInButton, createAccountButton, restoreAccountButton;
	private EditText userNameEditText, passwordEditText;
	private LoginButton loginBtn;
	private UiLifecycleHelper uiHelper;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_credentialscreen);    
		this.userCredentials = new UserCredentials(this.getApplicationContext());
		
		uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);

		this.signInButton = (Button) findViewById(R.id.signInButton);
		this.passwordEditText = (EditText) findViewById(R.id.passwordField);
		this.userNameEditText = (EditText) findViewById(R.id.usernameField);
		this.loginBtn = (LoginButton) findViewById(R.id.facebookButton);
		this.createAccountButton = (Button) findViewById(R.id.createAccountButton);
		this.restoreAccountButton = (Button) findViewById(R.id.recoverAccountButton);
		
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
		this.setUpFacebookButton();
		this.setUpCreateAccountButton();
		this.setUpRestoreAccountButton();
	}

	private void setUpFacebookButton() {
		loginBtn.setUserInfoChangedCallback(new UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                if (user != null) {
                	//TODO: Facebook Login
                } else {
                    
                }
            }
        });
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
	
	private void setUpCreateAccountButton() {
		this.createAccountButton.setOnTouchListener(new View.OnTouchListener() {
			
			@SuppressLint("NewApi")
			@Override
			   public boolean onTouch(View v, MotionEvent event) {
				   switch ( event.getAction() ) {
			    		case MotionEvent.ACTION_DOWN: 
			    			{
			    				createAccountButton.setTextColor(Color.GRAY);
			    				break;
			    			}
			    		case MotionEvent.ACTION_UP: 
			    			{
			    				createAccountButton.setTextColor(Color.parseColor("#FF7F24"));;
			    				break;
			    			}
				   }
				   return false;
				}
		   });
		this.createAccountButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				triggerCreateAccount();
			}

		});
	}
	
	private void setUpRestoreAccountButton() {
		this.restoreAccountButton.setOnTouchListener(new View.OnTouchListener() {
			
			@SuppressLint("NewApi")
			@Override
			   public boolean onTouch(View v, MotionEvent event) {
				   switch ( event.getAction() ) {
			    		case MotionEvent.ACTION_DOWN: 
			    			{
			    				restoreAccountButton.setTextColor(Color.GRAY);
			    				break;
			    			}
			    		case MotionEvent.ACTION_UP: 
			    			{
			    				restoreAccountButton.setTextColor(Color.parseColor("#FF7F24"));;
			    				break;
			    			}
				   }
				   return false;
				}
		   });
		this.restoreAccountButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				restoreAccount();
			}

		});
	}
	
	protected void restoreAccount() {
		// TODO Implement restoring
	}
	
	protected void triggerCreateAccount() {
		// TODO Create new account
	}

	
	private void triggerLogin() {
		//TODO: Check if entered value is email or username
		if(this.userNameEditText.getText().toString().length() > 0 && this.passwordEditText.getText().toString().length() > 0) {
			this.userCredentials.setEmail(this.userNameEditText.getText().toString());
			this.userCredentials.setPassword(this.userNameEditText.getText().toString());
			this.finish();
		} else {
			Toast.makeText(getApplicationContext(), "Enter valid Credentials", Toast.LENGTH_LONG).show();
		}
	}
	
	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			if (state.isOpened()) {
				///TODO: Facebook Login
			} else if (state.isClosed()) {
				
			}
		}};
	 
	    @Override
	    public void onResume() {
	        super.onResume();
	        uiHelper.onResume();
	    }
	 
	    @Override
	    public void onPause() {
	        super.onPause();
	        uiHelper.onPause();
	    }
	 
	    @Override
	    public void onDestroy() {
	        super.onDestroy();
	        uiHelper.onDestroy();
	    }
	 
	    @Override
	    public void onActivityResult(int requestCode, int resultCode, Intent data) {
	        super.onActivityResult(requestCode, resultCode, data);
	        uiHelper.onActivityResult(requestCode, resultCode, data);
	    }
	 
	    @Override
	    public void onSaveInstanceState(Bundle savedState) {
	        super.onSaveInstanceState(savedState);
	        uiHelper.onSaveInstanceState(savedState);
	    }
}
