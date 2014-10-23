package com.wackadoo.wackadoo_client.activites;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
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
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.helper.CustomProgressDialog;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.helper.WackadooActivity;
import com.wackadoo.wackadoo_client.interfaces.CreateAccountCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.GameLoginCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.GetAccountCallbackInterface;
import com.wackadoo.wackadoo_client.model.UserCredentials;
import com.wackadoo.wackadoo_client.tasks.CreateAccountAsyncTask;
import com.wackadoo.wackadoo_client.tasks.GameLoginAsyncTask;
import com.wackadoo.wackadoo_client.tasks.GetAccountAsyncTask;

public class CredentialScreenActivity extends WackadooActivity implements CreateAccountCallbackInterface, GameLoginCallbackInterface, GetAccountCallbackInterface, StatusCallback {
	
	private static final String TAG = CredentialScreenActivity.class.getSimpleName();
	
	private UserCredentials userCredentials;
	private Button signInBtn, createAccountBtn, restoreAccountBtn;
	private EditText userNameEditText, passwordEditText;
	private TextView backBtn;
	private CustomProgressDialog progressDialog;
	private UiLifecycleHelper uiHelper;		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_credentialscreen);
		
		userCredentials = new UserCredentials(getApplicationContext());
		
		//facebook lifecycleHelper to keep track of the session
		uiHelper = new UiLifecycleHelper(this, this);
		uiHelper.onCreate(savedInstanceState);

	    setUpUi();
	    setUpButtons();
	}

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

	// set up interface elements
    private void setUpUi() {
    	signInBtn = (Button) findViewById(R.id.signInButton);
		passwordEditText = (EditText) findViewById(R.id.passwordField);
		userNameEditText = (EditText) findViewById(R.id.usernameField);
		createAccountBtn = (Button) findViewById(R.id.createAccountButton);
		restoreAccountBtn = (Button) findViewById(R.id.recoverAccountButton);
		backBtn = (TextView) findViewById(R.id.credentialscreenTopbarBack);
		
		// set up standard server communication dialog
	    progressDialog = new CustomProgressDialog(CredentialScreenActivity.this);
    }
    
	// set up touchlistener for buttons
	private void setUpButtons() {
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
							progressDialog.show();
							new GameLoginAsyncTask(CredentialScreenActivity.this, userCredentials, true, false).execute();
							break;
							
						case R.id.credentialscreenTopbarBack:
							StaticHelper.continueMusic = true;
							finish();
							break;
					}
				}
				return true;
			}
		};
		signInBtn.setOnTouchListener(touchListener);
		createAccountBtn.setOnTouchListener(touchListener);
		restoreAccountBtn.setOnTouchListener(touchListener);
		backBtn.setOnTouchListener(touchListener);
	}
	
	// set up and show dialog when account was restored
	private void showRestoreAccountDialog(boolean success) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String title, message;
		
		if (success) {
			title = userCredentials.getUsername();
			message = String.format(getResources().getString(R.string.account_character_restore_success), title);
		} else {
			title = getResources().getString(R.string.account_character_restore_failed);
			message = getResources().getString(R.string.account_character_restore_failure_message);
		}
		
    	builder.setTitle(title)
    		   .setMessage(message)
    		   .setPositiveButton(getResources().getString(R.string.alert_ok_button), new DialogInterface.OnClickListener() { 
		    	    @Override
		    	    public void onClick(DialogInterface dialog, int which) { 
						StaticHelper.continueMusic = true;
		    	    	finish(); 
		    	    }
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
		    	});
		
		AlertDialog dialog = builder.create();
	    dialog.show();
	    StaticHelper.styleDialog(this, dialog);
	}
	
	// trigger create account process
	private void triggerCreateAccount() {
		userCredentials.clearAllCredentials();
		progressDialog.show();
		new CreateAccountAsyncTask(this).execute();
	}

	// controll login data and trigger login process
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
				new GameLoginAsyncTask(this, userCredentials, false, false).execute();
			} 
			else {
				Toast.makeText(this, getResources().getString(R.string.credentials_password_too_short), Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, getResources().getString(R.string.credentials_email_not_valid), Toast.LENGTH_SHORT).show();
		}
	}
	
	// facebook: callback interface to handle facebook login
	@Override
	public void call(Session session, SessionState state, Exception exception) {
		if (state.isOpened()) {
			userCredentials.setFbUser(true);
			finish();
		} 
	}
	
    // facebook: handles result for login 
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    // callback interface for CreateAccountAsyncTask
	@Override
	public void onRegistrationCompleted(boolean success, String identifier, String nickname, String accountId) {
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		
		if (success) {
			userCredentials.setIdentifier(identifier);
			userCredentials.setUsername(nickname);
			userCredentials.setAccountId(accountId);
			StaticHelper.continueMusic = true;
			finish();
		} else {
			Toast.makeText(this, getString(R.string.error_server_communication), Toast.LENGTH_SHORT)
			 	 .show();
		}
	}
	
	// callback interface for login/restore account task
	@Override
	public void loginCallback(boolean result, String accessToken, String expiration, String userIdentifier, boolean restoreAccount, boolean refresh) {
		userCredentials.generateNewAccessToken(accessToken, expiration);
		userCredentials.setIdentifier(userIdentifier);
		// fetch account data to show in imterface
		new GetAccountAsyncTask(this, userCredentials, restoreAccount).execute();
	}
	
	// callback interface for errors in login/restore account task
	@Override
	public void loginCallbackError(String error, boolean restoreAccount, boolean refresh) {
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		
		if (restoreAccount) {
			showRestoreAccountDialog(false);
		} else {
			if (error.equals("invalid_grant")) {
				Toast.makeText(this, getResources().getString(R.string.login_invalid_grant), Toast.LENGTH_LONG)
					 .show();
			
			} else {
				Toast.makeText(this, getResources().getString(R.string.login_failed_toast), Toast.LENGTH_LONG)
					 .show();
			}
		}		
	}

	// callback interface for GetAccountAsyncTask
	@Override
	public void getAccountCallback(String identifier, String nickname, String accountId, boolean restoreAccount) {
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		
		userCredentials.setUsername(nickname);
		userCredentials.setAccountId(accountId);
		
		// if async task called to restore locale account, show dialog
		if (restoreAccount) {
			showRestoreAccountDialog(true);
		} else {
			StaticHelper.continueMusic = true;
			finish();
		}		
	}
}
