package com.wackadoo.wackadoo_client.activites;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.interfaces.CreateAccountCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.GameLoginCallbackInterface;
import com.wackadoo.wackadoo_client.model.UserCredentials;
import com.wackadoo.wackadoo_client.tasks.CreateAccountAsyncTask;
import com.wackadoo.wackadoo_client.tasks.GameLoginAsyncTask;

public class CredentialScreenActivity extends Activity implements CreateAccountCallbackInterface, GameLoginCallbackInterface{
	
	private static final String TAG = CredentialScreenActivity.class.getSimpleName();
	
	private UserCredentials userCredentials;
	private Button signInButton, createAccountButton, restoreAccountButton;
	private EditText userNameEditText, passwordEditText;
	private LoginButton loginBtn;
	private UiLifecycleHelper uiHelper;
	private TextView backBtn;
	private ProgressDialog progressDialog;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credentialscreen);   
		
		this.userCredentials = new UserCredentials(getApplicationContext());
		
//		uiHelper = new UiLifecycleHelper(this, statusCallback);
//      uiHelper.onCreate(savedInstanceState);

		signInButton = (Button) findViewById(R.id.signInButton);
		passwordEditText = (EditText) findViewById(R.id.passwordField);
		userNameEditText = (EditText) findViewById(R.id.usernameField);
//		this.loginBtn = (LoginButton) findViewById(R.id.facebookButton);
		createAccountButton = (Button) findViewById(R.id.createAccountButton);
		restoreAccountButton = (Button) findViewById(R.id.recoverAccountButton);
		backBtn = (TextView) findViewById(R.id.credentialscreenTopbarBack);
		
		// set up standard server communication dialog
	    setUpDialog();
	    
		setUpButtonListener();
	}

	private void setUpButtonListener() {
		setUpSignInButton();
//		this.setUpFacebookButton();
		setUpCreateAccountButton();
		setUpRestoreAccountButton();
		setUpBackBtn();
	}

	private void setUpBackBtn() {
		backBtn.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
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
		signInButton.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
		    		case MotionEvent.ACTION_DOWN: 
	    				signInButton.setTextColor(getResources().getColor(R.color.textbox_orange_active));
	    				break;
	    				
		    		case MotionEvent.ACTION_UP: 
		    			signInButton.setTextColor(getResources().getColor(R.color.textbox_orange));
	    				break;
				}
				return false;
			}
		});
		signInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				triggerLogin();
			}

		});
	}
	
	private void setUpCreateAccountButton() {
		createAccountButton.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("NewApi")
			@Override
		    public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
		    		case MotionEvent.ACTION_DOWN: 
	    				createAccountButton.setTextColor(getResources().getColor(R.color.textbox_orange_active));
	    				break;
	    				
		    		case MotionEvent.ACTION_UP: 
		    			createAccountButton.setTextColor(getResources().getColor(R.color.textbox_orange));
						break;
				}
				return false;
			}
		});
		createAccountButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				triggerCreateAccount();
			}
		});
	}
	
	private void setUpRestoreAccountButton() {
		restoreAccountButton.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
		    		case MotionEvent.ACTION_DOWN: 
	    				restoreAccountButton.setTextColor(getResources().getColor(R.color.textbox_orange_active));
	    				break;
		    				
		    		case MotionEvent.ACTION_UP: 
		    			restoreAccountButton.setTextColor(getResources().getColor(R.color.textbox_orange));
	    				break;
			   }
			   return false;
			}
		});
		restoreAccountButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				restoreAccount();
			}

		});
	}
	
	protected void restoreAccount() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// TODO: restore account
		String title, message;
		boolean success = false;
		
		if(success) {
			title = "WackyUser1337";
			message = String.format(getResources().getString(R.string.account_character_restore_success), "WackyUser1337");

		} else {
			title = getResources().getString(R.string.account_character_restore_failed);
			message = getResources().getString(R.string.account_character_restore_failure_message);
		}
		
    	builder.setTitle(title)
    		   .setMessage(message)
    		   .setPositiveButton(getResources().getString(R.string.alert_ok_button), new DialogInterface.OnClickListener() { 
		    	    @Override
		    	    public void onClick(DialogInterface dialog, int which) { }
		    	})
	    	   .setNegativeButton(getResources().getString(R.string.infoscreen_support_btn), new DialogInterface.OnClickListener() {
		    	    @Override
		    	    public void onClick(DialogInterface dialog, int which) {
		    	        dialog.cancel();
		    	        
		    	        Intent intent = new Intent(Intent.ACTION_SENDTO); 
		    	        intent.setType("text/plain");
		    	        intent.setData(Uri.parse("mailto:support@5dlab.com")); 
		    	        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.credentials_lost_access_mail_subject));
		    	        intent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.credentials_lost_access_mail));
		    	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
		    	        startActivity(intent);
		    	    }
		    	})
    		   .show();
	}
	
	protected void triggerCreateAccount() {
		userCredentials.clearAllCredentials();
		progressDialog.show();
		new CreateAccountAsyncTask(this, progressDialog).execute();
	}

	private void triggerLogin() {
		if(userNameEditText.getText().length() > 0){
			if(passwordEditText.getText().length() > 5) {
				userCredentials.setEmail(this.userNameEditText.getText().toString());
				userCredentials.setPassword(this.passwordEditText.getText().toString());
				progressDialog.show();
				new GameLoginAsyncTask(this, getApplicationContext(), userCredentials, progressDialog).execute();
			} else {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.credentials_password_too_short), Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.credentials_email_not_valid), Toast.LENGTH_SHORT).show();
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
//	        uiHelper.onResume();
    }
 
    @Override
    public void onPause() {
        super.onPause();
//	        uiHelper.onPause();
    }
 
    @Override
    public void onDestroy() {
        super.onDestroy();
//	        uiHelper.onDestroy();
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }
 
    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
//        uiHelper.onSaveInstanceState(savedState);
    }

	@Override
	public void onRegistrationCompleted(String identifier, String clientID, String nickname) {
		userCredentials.setIdentifier(identifier);
		userCredentials.setClientID(clientID);
		userCredentials.setUsername(nickname);
		Log.d(TAG, "----------> vor finish(): " + userCredentials.getIdentifier());
		finish();
	}
	
	@Override
	public void loginCallback(String accessToken, String expiration, String userIdentifier) {
		userCredentials.generateNewAccessToken(accessToken, expiration);
		userCredentials.setClientID(userIdentifier);
		finish();
	}
	
	@Override
	public void loginCallbackError(String error) {
		if(error.equals("invalid_grant")) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_invalid_grant), Toast.LENGTH_LONG)
			.show();
			
		} else {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_failed_toast), Toast.LENGTH_LONG)
			.show();
		}
	}

	// Set up the standard server communiation dialog
	private void setUpDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getResources().getString(R.string.server_communication));
		progressDialog.setMessage(getResources().getString(R.string.please_wait));
	}
}
