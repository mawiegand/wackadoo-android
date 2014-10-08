package com.wackadoo.wackadoo_client.activites;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
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
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.interfaces.CreateAccountCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.GameLoginCallbackInterface;
import com.wackadoo.wackadoo_client.model.UserCredentials;
import com.wackadoo.wackadoo_client.tasks.CreateAccountAsyncTask;
import com.wackadoo.wackadoo_client.tasks.GameLoginAsyncTask;

public class CredentialScreenActivity extends Activity implements CreateAccountCallbackInterface, GameLoginCallbackInterface{
	
	private static final String TAG = CredentialScreenActivity.class.getSimpleName();
	
	private UserCredentials userCredentials;
	private Button signInBtn, createAccountBtn, restoreAccountBtn;
	private EditText userNameEditText, passwordEditText;
	private LoginButton loginBtn;
	private TextView backBtn;
	private ProgressDialog progressDialog;
	private UiLifecycleHelper uiHelper;			// facebook
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_credentialscreen);   
		
		userCredentials = new UserCredentials(getApplicationContext());
		
		uiHelper = new UiLifecycleHelper(this, statusCallback);
		uiHelper.onCreate(savedInstanceState);

		signInBtn = (Button) findViewById(R.id.signInButton);
		passwordEditText = (EditText) findViewById(R.id.passwordField);
		userNameEditText = (EditText) findViewById(R.id.usernameField);
		loginBtn = (LoginButton) findViewById(R.id.facebookButton);
		createAccountBtn = (Button) findViewById(R.id.createAccountButton);
		restoreAccountBtn = (Button) findViewById(R.id.recoverAccountButton);
		backBtn = (TextView) findViewById(R.id.credentialscreenTopbarBack);
		
		// set up standard server communication dialog
	    setUpDialog();
	    
	    setUpBtns();
		setUpBackBtn();
	}

	// facebook: lifecycleHelper to keep track of the session
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

	private void setUpBtns() {
		OnTouchListener touchListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getActionMasked();
				
				if (action == MotionEvent.ACTION_DOWN) {
					((TextView) v).setTextColor(getResources().getColor(R.color.textbox_orange_active));
				
				} else if (action == MotionEvent.ACTION_CANCEL) {
					((TextView) v).setTextColor(getResources().getColor(R.color.textbox_orange));
					
				} else if (action == MotionEvent.ACTION_UP) {
					((TextView) v).setTextColor(getResources().getColor(R.color.textbox_orange));
					
					switch(v.getId()) {
						case R.id.signInButton:
							triggerLogin();
							break;
						
						case R.id.createAccountButton:
							triggerCreateAccount();
							break;
							
						case R.id.recoverAccountButton:
							triggerRestoreAccount();
							break;
					}
				}
				return true;
			}
		};
		signInBtn.setOnTouchListener(touchListener);
		createAccountBtn.setOnTouchListener(touchListener);
		restoreAccountBtn.setOnTouchListener(touchListener);
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
		    			finish();
		    			break;
				}
				return true;
			}
		});
	}
	
	protected void restoreAccount(boolean success) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String title, message;
		
		if (success) {
			// TODO: dynamic character info!
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
		    	    public void onClick(DialogInterface dialog, int which) { finish(); }
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
		    	        finish();
		    	    }
		    	})
    		   .show();
	}
	
	private void triggerRestoreAccount() {
		new GameLoginAsyncTask(this, userCredentials, true, false, progressDialog).execute();
	}
	
	protected void triggerCreateAccount() {
		userCredentials.clearAllCredentials();
		progressDialog.show();
		new CreateAccountAsyncTask(this, progressDialog).execute();
	}

	private void triggerLogin() {
		if (userNameEditText.getText().length() > 0) {
			if (passwordEditText.getText().length() > 5) {
				if (StaticHelper.isValidMail(userNameEditText.getText().toString())) {
					userCredentials.setEmail(this.userNameEditText.getText().toString());
				
				} else {
					userCredentials.setUsername(this.userNameEditText.getText().toString());
				}
				userCredentials.setPassword(this.passwordEditText.getText().toString());
				progressDialog.show();

				new GameLoginAsyncTask(this, userCredentials, false, false, progressDialog).execute();
			} 
			else {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.credentials_password_too_short), Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.credentials_email_not_valid), Toast.LENGTH_SHORT).show();
		}
	}
	
	// facebook: callback interface object to handle current status
	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			if (state.isOpened()) {
				userCredentials.setFbUser(true);
				finish();
			} 
		}
	};
	
    // facebook: handles result for login 
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    // callback interface for CreateAccountAsyncTask
	@Override
	public void onRegistrationCompleted(String identifier, String nickname, String characterId) {
		userCredentials.setIdentifier(identifier);
		userCredentials.setUsername(nickname);
		userCredentials.setCharacterId(characterId);
		finish();
	}
	
	// callback interface for login/restore account task
	@Override
	public void loginCallback(boolean result, String accessToken, String expiration, String userIdentifier, boolean restoreAccount, boolean refresh) {
		userCredentials.generateNewAccessToken(accessToken, expiration);
		userCredentials.setIdentifier(userIdentifier);
		
		// if async task called to restore locale account, show dialog
		if (restoreAccount) {
			restoreAccount(true);
		
		} else {
			finish();
		}
	}
	
	// callback interface for errors in login/restore account task
	@Override
	public void loginCallbackError(String error, boolean restoreAccount, boolean refresh) {
		if (restoreAccount) {
			restoreAccount(false);
		} else {
			if (error.equals("invalid_grant")) {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_invalid_grant), Toast.LENGTH_LONG)
				.show();
			
			} else {
				Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_failed_toast), Toast.LENGTH_LONG)
				.show();
			}
		}		
	}

	// set up the standard server communiation dialog
	private void setUpDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getResources().getString(R.string.server_communication));
		progressDialog.setMessage(getResources().getString(R.string.please_wait));
	}
}
