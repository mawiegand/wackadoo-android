package com.wackadoo.wackadoo_client.activites;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.helper.UtilityHelper;
import com.wackadoo.wackadoo_client.interfaces.AccountManagerCallbackInterface;
import com.wackadoo.wackadoo_client.model.UserCredentials;
import com.wackadoo.wackadoo_client.tasks.AccountManagerAsyncTask;
import com.wackadoo.wackadoo_client.tasks.GameLoginAsyncTask;

public class AccountManagerActivity extends Activity implements AccountManagerCallbackInterface{
	
	private static final String TAG = AccountManagerActivity.class.getSimpleName();

	private UserCredentials userCredentials;
	private TextView usernameTextView, provideEmailTextView, characterLockedTextView, makeCharacterPortableTextView, emailTextView, emailInformationTextView, backBtn;
	private Button signOutButton, setEmailButton, passwordButton;
	private ImageView emailAccountCheckedImage;
	private enum AlertCallback { Email, Password }
	private boolean passwordButtonVisible, emailButtonVisible;
	private ProgressDialog progressDialog;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountmanager);
	    
	    userCredentials = new UserCredentials(getApplicationContext());
//	    checkUserIsLoggedIn();
	    
        usernameTextView = (TextView) findViewById(R.id.usernameText);
        signOutButton = (Button) findViewById(R.id.signOutButton);
        setEmailButton = (Button) findViewById(R.id.setEmailButton);
        passwordButton = (Button) findViewById(R.id.passwordButton);
        provideEmailTextView = (TextView) findViewById(R.id.emailText);
        characterLockedTextView = (TextView) findViewById(R.id.characterLockedText);
        makeCharacterPortableTextView = (TextView) findViewById(R.id.makeCharacterPortableText);
        emailTextView = (TextView) findViewById(R.id.emailAccountText);
        emailInformationTextView = (TextView) findViewById(R.id.emailInformationText);
        backBtn = (TextView) findViewById(R.id.accountTopbarBack);
        emailAccountCheckedImage = (ImageView) findViewById(R.id.emailAccountCheckedImage);
        
	    loadCredentialsToUI();
	    addButtonListeners();
	    setUpBackBtn();
    }
	
    private void setUpBackBtn() {
		backBtn.setEnabled(true);
		backBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				switch (e.getAction()) {
				case MotionEvent.ACTION_DOWN: 
					backBtn.setTextColor(getResources().getColor(R.color.textbox_orange_active));
					break;
					
				case MotionEvent.ACTION_CANCEL: 
					backBtn.setTextColor(getResources().getColor(R.color.textbox_orange));
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
    
	private void addButtonListeners() {
		if(this.userCredentials.isPasswordGenerated()) {
			this.setUpPasswordButton();
			this.passwordButtonVisible = true;
		} else {
			this.passwordButtonVisible = false;
		}
		if(this.userCredentials.getEmail().length() <= 0) {
			this.setUpEmailButton();
			this.emailButtonVisible = true;
		} else {
			this.emailButtonVisible = false;
		}
		this.setButtonVisibility(passwordButtonVisible, emailButtonVisible);
		this.setUpSignOutButton();
	}
	
	private void setButtonVisibility(boolean setPasswordButtonVisible, boolean emailButtonVisible) {
		if(!passwordButtonVisible) {
			passwordButton.setText(getResources().getString(R.string.account_change_password));
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)passwordButton.getLayoutParams();
			params.addRule(RelativeLayout.BELOW, R.id.emailInformationText);
			passwordButton.setLayoutParams(params); 
			setUpPasswordButton();
			
			characterLockedTextView.setVisibility(View.GONE);
			makeCharacterPortableTextView.setVisibility(View.GONE);
		}
		if(!emailButtonVisible) {
			setEmailButton.setEnabled(false);
			setEmailButton.setVisibility(View.GONE);
			provideEmailTextView.setVisibility(View.GONE);
			emailTextView.setVisibility(View.VISIBLE);
			emailTextView.setText(userCredentials.getEmail());
			emailInformationTextView.setVisibility(View.INVISIBLE);
			emailAccountCheckedImage.setVisibility(View.VISIBLE);
		}
	}

	private void setUpPasswordButton() {
		this.passwordButton.setOnTouchListener(new View.OnTouchListener() {
			
			@SuppressLint("NewApi")
			@Override
			   public boolean onTouch(View v, MotionEvent event) {
				   switch ( event.getAction() ) {
			    		case MotionEvent.ACTION_DOWN: 
		    				passwordButton.setTextColor(getResources().getColor(R.color.textbox_orange_active));
		    				break;
		    				
			    		case MotionEvent.ACTION_UP: 
		    				passwordButton.setTextColor(getResources().getColor(R.color.textbox_orange));
		    				break;
				   }
				   return false;
				}
		   });
		this.passwordButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startChangePassword();
			}

		});
	}

	private void setUpEmailButton() {
		this.setEmailButton.setOnTouchListener(new View.OnTouchListener() {
			
			@SuppressLint("NewApi")
			@Override
			   public boolean onTouch(View v, MotionEvent event) {
				   switch ( event.getAction() ) {
			    		case MotionEvent.ACTION_DOWN: 
		    				setEmailButton.setTextColor(getResources().getColor(R.color.textbox_orange_active));
		    				break;
		    				
			    		case MotionEvent.ACTION_UP: 
			    			setEmailButton.setTextColor(getResources().getColor(R.color.textbox_orange));
		    				break;
				   }
				   return false;
				}
		   });
		this.setEmailButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startSetEmail();
			}

		});		
	}

	private void setUpSignOutButton() {
		this.signOutButton.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("NewApi")
			@Override
			   public boolean onTouch(View v, MotionEvent event) {
				   switch (event.getAction()) {
			    		case MotionEvent.ACTION_DOWN: 
			    			signOutButton.setTextColor(getResources().getColor(R.color.textbox_orange_active));
			    			break;

			    		case MotionEvent.ACTION_UP: 
			    			signOutButton.setTextColor(getResources().getColor(R.color.textbox_orange));
			    			break;
				   }
				   return false;
				}
		   });
		this.signOutButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				triggerSignOut();
			}

		});		
	}

	private void startChangePassword() {
		this.showInputAlertDialogWithText(getResources().getString(R.string.alert_change_password), AlertCallback.Password);
	}

	private void startSetEmail() {
		this.showInputAlertDialogWithText(getResources().getString(R.string.alert_email_change), AlertCallback.Email);
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
    	if(userCredentials.getPassword().equals("")) {
    		builder.setTitle(getResources().getString(R.string.account_really_sign_out))
    			   .setMessage(getResources().getString(R.string.account_character_lost_when_signout));
    	} else {
    		builder.setTitle(getResources().getString(R.string.account_sign_out_button))
    			   .setMessage(getResources().getString(R.string.alert_quit));
    	}
	    builder.show();
	}

	private void loadCredentialsToUI() {

    	String username = userCredentials.getUsername();
    	String email = userCredentials.getEmail();
    	if(!username.isEmpty()) {
    		usernameTextView.setText(username);
    	}
    	if(!email.isEmpty()) {
    		emailTextView.setText(email);
    	}
	}

    private void showInputAlertDialogWithText(String text, final AlertCallback callback) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(text);

    	final EditText input = new EditText(this);
    	if(callback.equals(AlertCallback.Password)) {
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

    	builder.show();
    }
    
    private void enteredNewEmail(String email) {
		Log.d(TAG, "identifier vor aufruf task: " + userCredentials.getIdentifier());
		if(UtilityHelper.isValidMail(email)){
			new AccountManagerAsyncTask(this, progressDialog, userCredentials, "mail", email).execute();
		} else {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.credentials_email_not_valid), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void enteredNewPassword(String password) {
		if(password.length() > 5){
			new AccountManagerAsyncTask(this, progressDialog, userCredentials, "password", password).execute();
		} else {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.credentials_password_too_short), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void signOut() {
		userCredentials.clearAllCredentials();
		userCredentials = new UserCredentials(getApplicationContext());
		finish();
//		checkUserIsLoggedIn();
	}

	@Override
	public void accountManagerCallback(String type, Boolean result, String newValue) {
		Toast toast = Toast.makeText(this, null, Toast.LENGTH_SHORT);
		
		if(result) {
			if(type.equals("mail")) {
				emailButtonVisible = false;
				setButtonVisibility(passwordButtonVisible, emailButtonVisible);
		    	userCredentials.setEmail(newValue);
		    	toast.setText(getResources().getString(R.string.alert_email_change_success));
				
			} else {
				passwordButtonVisible = false;
				setButtonVisibility(passwordButtonVisible, emailButtonVisible);
				userCredentials.setPassword(newValue);
				toast.setText(getResources().getString(R.string.alert_change_password_success));
			}
		} else {
			if(type.equals("mail")) {
				toast.setText(getResources().getString(R.string.alert_email_change_error));
			} else {
				toast.setText(getResources().getString(R.string.alert_change_password_error));
			}
		}
		toast.show();

	}
	
//	private void checkUserIsLoggedIn() {
//		String identifier = this.userCredentials.getIdentifier();
//		String email = this.userCredentials.getEmail();
//		
//		////TODO: Might check facebook login 
//		if((identifier.length() <= 0) && (email.length() <= 0)) {
//			Intent intent = new Intent(AccountManagerActivity.this, CredentialScreenActivity.class);
//			startActivity(intent);
//			finish();
//		}
//	}
	
}
