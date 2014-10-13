package com.wackadoo.wackadoo_client.activites;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.helper.CustomProgressDialog;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.helper.WackadooActivity;
import com.wackadoo.wackadoo_client.interfaces.AccountManagerCallbackInterface;
import com.wackadoo.wackadoo_client.model.UserCredentials;
import com.wackadoo.wackadoo_client.tasks.AccountManagerAsyncTask;

public class AccountManagerActivity extends WackadooActivity implements AccountManagerCallbackInterface{
	
	private static final String TAG = AccountManagerActivity.class.getSimpleName();

	private UserCredentials userCredentials;
	private TextView usernameTextView, provideEmailTextView, characterLockedTextView, makeCharacterPortableTextView, emailTextView, emailInformationTextView, backBtn;
	private Button signOutButton, setEmailButton, passwordButton;
	private ImageView emailAccountCheckedImage;
	private enum AlertCallback { Email, Password }
	private boolean passwordButtonVisible, emailButtonVisible;
	private CustomProgressDialog progressDialog;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_accountmanager);
	    
	    userCredentials = new UserCredentials(getApplicationContext());
	    
	    setUpUi();
	    setUpButtons();
	    loadCredentialsToUI();
    }
	
	
	private void setUpUi() {
		usernameTextView = (TextView) findViewById(R.id.usernameText);
        setEmailButton = (Button) findViewById(R.id.setEmailButton);
		passwordButton = (Button) findViewById(R.id.passwordButton);
        signOutButton = (Button) findViewById(R.id.signOutButton);
        provideEmailTextView = (TextView) findViewById(R.id.emailText);
        characterLockedTextView = (TextView) findViewById(R.id.characterLockedText);
        makeCharacterPortableTextView = (TextView) findViewById(R.id.makeCharacterPortableText);
        emailTextView = (TextView) findViewById(R.id.emailAccountText);
        emailInformationTextView = (TextView) findViewById(R.id.emailInformationText);
        backBtn = (TextView) findViewById(R.id.accountTopbarBack);
        emailAccountCheckedImage = (ImageView) findViewById(R.id.emailAccountCheckedImage);
		
        // set up standard server communication dialog
	    progressDialog = new CustomProgressDialog(this);
        
        // handles which ui elements are shown for the current user
        emailButtonVisible = true;
		passwordButtonVisible = true;
		if (userCredentials.isFbUser()) {
			emailButtonVisible = false;
			passwordButtonVisible = false;
		} else {
			if (!userCredentials.isEmailGenerated()) { 
				emailButtonVisible = false; 
			} 
			
			if (!userCredentials.isPasswordGenerated()) {
				passwordButtonVisible = false;
			}
		}
		
		// if user has own mail, dont show setmail button
		if (!emailButtonVisible) {
			setEmailButton.setVisibility(View.GONE);
			provideEmailTextView.setVisibility(View.GONE);
			emailTextView.setVisibility(View.VISIBLE);
			emailTextView.setText(userCredentials.getEmail());
			emailInformationTextView.setVisibility(View.INVISIBLE);
			emailAccountCheckedImage.setVisibility(View.VISIBLE);
		}
		
		// if user has own password, dont show changepassword button
		if (!passwordButtonVisible) {
			passwordButton.setText(getResources().getString(R.string.account_change_password));
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) passwordButton.getLayoutParams();
			params.addRule(RelativeLayout.BELOW, R.id.emailInformationText);
			passwordButton.setLayoutParams(params); 
			characterLockedTextView.setVisibility(View.GONE);
			makeCharacterPortableTextView.setVisibility(View.GONE);
		}
		
		// character connected to facebook
		if (userCredentials.isFbUser()) {
			emailTextView.setVisibility(View.GONE);
			characterLockedTextView.setVisibility(View.VISIBLE);
			characterLockedTextView.setText(R.string.account_character_connected_fb);
			emailAccountCheckedImage.setVisibility(View.INVISIBLE);
			passwordButton.setVisibility(View.GONE);
		}
    }
    
	// sets up touchlistener for buttons
    private void setUpButtons() {
    	OnTouchListener touchListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				switch (e.getAction()) {
					case MotionEvent.ACTION_DOWN: 
						((TextView) v).setTextColor(getResources().getColor(R.color.textbox_orange_active));
						break;
						
					case MotionEvent.ACTION_CANCEL: 
						((TextView) v).setTextColor(getResources().getColor(R.color.textbox_orange));
						break;
						
					case MotionEvent.ACTION_UP: 
						((TextView) v).setTextColor(getResources().getColor(R.color.textbox_orange));
						
						switch (v.getId()) {
							case R.id.accountTopbarBack:
								StaticHelper.continueMusic = true;
								finish();
								break;
								
							case R.id.signOutButton:
								triggerSignOut();
								break;
								
							case R.id.setEmailButton:
								showInputAlertDialogWithText(getResources().getString(R.string.alert_email_change), AlertCallback.Email);
								break;
								
							case R.id.passwordButton:
								showInputAlertDialogWithText(getResources().getString(R.string.alert_change_password), AlertCallback.Password);
								break;
						}
						break;
				}
				return true;
			}
		};
		backBtn.setOnTouchListener(touchListener);
		signOutButton.setOnTouchListener(touchListener);
		
		if (userCredentials.isEmailGenerated()) {
			setEmailButton.setVisibility(View.VISIBLE);
			setEmailButton.setOnTouchListener(touchListener);
		}
		
		if (userCredentials.isPasswordGenerated()) { 
			passwordButton.setVisibility(View.VISIBLE);
			passwordButton.setOnTouchListener(touchListener);
		}
	}
    
	private void triggerSignOut() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
    	    @Override	
    	    public void onClick(DialogInterface dialog, int which) {
    	        switch (which){
    	        	case DialogInterface.BUTTON_POSITIVE: signOut(); break;
    	        	case DialogInterface.BUTTON_NEGATIVE: break;
    	        }
    	    }
    	};

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setPositiveButton(getResources().getString(R.string.account_sign_out_button), dialogClickListener)
    		   .setNegativeButton(getResources().getString(R.string.alert_cancel_button), dialogClickListener);
    		 
    	// user without mail wants to log out
    	if (userCredentials.getPassword().equals("")) {
    		builder.setTitle(getResources().getString(R.string.account_really_sign_out))
    			   .setMessage(getResources().getString(R.string.account_character_lost_when_signout));
    	} else {
    		builder.setTitle(getResources().getString(R.string.account_sign_out_button))
    			   .setMessage(getResources().getString(R.string.alert_quit));
    	}
    	AlertDialog dialog = builder.create();
	    dialog.show();
	    StaticHelper.styleDialog(this, dialog);
	}

	private void loadCredentialsToUI() {
    	String username = userCredentials.getUsername();
    	if (!username.isEmpty()) {
    		usernameTextView.setText(username);
    	}
    	if (userCredentials.isFbUser()) {
    		emailTextView.setVisibility(View.GONE);
    		
    	} else {
    		String email = userCredentials.getEmail();
    		if (!email.isEmpty()) {
    		}
    	}
	}

	// show dialog to change mail/password
    private void showInputAlertDialogWithText(String text, final AlertCallback callback) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(text);

    	final EditText input = new EditText(this);
    	if (callback.equals(AlertCallback.Password)) {
	    	input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		}
    	
    	builder.setView(input);
    	builder.setPositiveButton(getResources().getString(R.string.alert_ok_button), new DialogInterface.OnClickListener() { 
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	    	switch(callback) {
    	    		case Email : enteredNewEmail(input.getText().toString()); return;
    	    		case Password : enteredNewPassword(input.getText().toString()); return;
    	    		default : return;
    	    	}
    	    }
    	});
    	builder.setNegativeButton(getResources().getString(R.string.alert_cancel_button), new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	        dialog.cancel();
    	    }
    	});
    	AlertDialog dialog = builder.create();
	    dialog.show();
	    StaticHelper.styleDialog(this, dialog);
    }
    
    // check mail before changing it
    private void enteredNewEmail(String email) {
		if (StaticHelper.isValidMail(email)){
			progressDialog.show();
			new AccountManagerAsyncTask(this, progressDialog, userCredentials, "mail", email).execute();
		} else {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.credentials_email_not_valid), Toast.LENGTH_SHORT).show();
		}
	}
	
    // check password before changing it
	private void enteredNewPassword(String password) {
		if (password.length() > 5){
			progressDialog.show();
			new AccountManagerAsyncTask(this, progressDialog, userCredentials, "password", password).execute();
		} else {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.credentials_password_too_short), Toast.LENGTH_SHORT).show();
		}
	}
	
	// delete user credentials when signing out
	private void signOut() {
		userCredentials.clearAllCredentials();
		userCredentials = new UserCredentials(getApplicationContext());
		
		String identifier = this.userCredentials.getIdentifier();
		String email = this.userCredentials.getEmail();
	
		// closes facebook session and clears cache
		Session session = Session.getActiveSession();
		if (session != null) {
			session.closeAndClearTokenInformation();	
			session.close();
			Session.setActiveSession(null);
		}

		// if credentials been deleted, go back to login screen
	    if ((identifier.length() <= 0) && (email.length() <= 0)) {
			Intent intent = new Intent(AccountManagerActivity.this, CredentialScreenActivity.class);
			startActivity(intent);
			StaticHelper.continueMusic = true;
			finish();
		}
	}

	// callback interface for AccountManagerAsyncTask
	@Override
	public void accountManagerCallback(String type, Integer result, String newValue) {
		Toast toast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
		
		if (result == 200) {
			if (type.equals("mail")) {
				emailButtonVisible = false;
		    	userCredentials.setEmail(newValue);
		    	setUpUi();		    
		    	toast.setText(getResources().getString(R.string.alert_email_change_success));
				
			} else {
				passwordButtonVisible = false;
				userCredentials.setPassword(newValue);
				setUpUi();
				toast.setText(getResources().getString(R.string.alert_change_password_success));
			}
			
		} else if (result == 500) {
			if (type.equals("mail")) {
				toast.setText(getResources().getString(R.string.alert_email_change_error));
			} else {
				toast.setText(getResources().getString(R.string.alert_change_password_error));
			}
		
		} else if (result == 403) {
			toast.setText(getResources().getString(R.string.alert_email_change_error_403));
		
		} else if (result == 409) {
			toast.setText(getResources().getString(R.string.alert_email_change_error_409));
		}
		
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		
		toast.show();
	}
}
