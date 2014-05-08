package com.wackadoo.wackadoo_client.activites;

import com.example.wackadoo_webview.R;
import com.wackadoo.wackadoo_client.model.UserCredentials;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AccountManagerActivity extends Activity {
	
	private UserCredentials userCredentials;
	private TextView usernameTextView, provideEmailTextView, characterLockedTextView, makeCharacterPortableTextView, emailTextView, emailInformationTextView;
	private Button signOutButton, setEmailButton, passwordButton;
	private ImageView emailAccountCheckedImage;
	private enum AlertCallback { Email, Password }
	private boolean passwordButtonVisible, emailButtonVisible;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
	    this.userCredentials = new UserCredentials(this.getApplicationContext());
	    this.checkUserIsLoggedIn();
	    
	    setContentView(R.layout.activity_accountmanager);
        usernameTextView = (TextView) findViewById(R.id.usernameText);
        signOutButton = (Button) findViewById(R.id.signOutButton);
        setEmailButton = (Button) findViewById(R.id.setEmailButton);
        passwordButton = (Button) findViewById(R.id.passwordButton);
        provideEmailTextView = (TextView) findViewById(R.id.emailText);
        characterLockedTextView = (TextView) findViewById(R.id.characterLockedText);
        makeCharacterPortableTextView = (TextView) findViewById(R.id.makeCharacterPortableText);
        emailTextView = (TextView) findViewById(R.id.emailAccountText);
        emailInformationTextView = (TextView) findViewById(R.id.emailInformationText);
        emailAccountCheckedImage = (ImageView) findViewById(R.id.emailAccountCheckedImage);
        
	    this.loadCredentialsToUI();
	    this.addButtonListeners();
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
	
	private void setButtonVisibility (boolean setPasswordButtonVisible, boolean emailButtonVisible) {
		if(!passwordButtonVisible) {
			this.passwordButton.setText(getResources().getString(R.string.account_change_password));
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)passwordButton.getLayoutParams();
			params.addRule(RelativeLayout.BELOW, R.id.emailInformationText);
			passwordButton.setLayoutParams(params); 
			this.setUpPasswordButton();
			
			this.characterLockedTextView.setVisibility(View.GONE);
			this.makeCharacterPortableTextView.setVisibility(View.GONE);
		}
		if(!emailButtonVisible) {
			this.setEmailButton.setEnabled(false);
			this.setEmailButton.setVisibility(View.GONE);
			this.provideEmailTextView.setVisibility(View.GONE);
			this.emailTextView.setVisibility(View.VISIBLE);
			this.emailInformationTextView.setVisibility(View.VISIBLE);
			this.emailAccountCheckedImage.setVisibility(View.VISIBLE);
		}
	}

	private void setUpPasswordButton() {
		this.passwordButton.setOnTouchListener(new View.OnTouchListener() {
			
			@SuppressLint("NewApi")
			@Override
			   public boolean onTouch(View v, MotionEvent event) {
				   switch ( event.getAction() ) {
			    		case MotionEvent.ACTION_DOWN: 
			    			{
			    				passwordButton.setTextColor(Color.GRAY);
			    				break;
			    			}
			    		case MotionEvent.ACTION_UP: 
			    			{
			    				passwordButton.setTextColor(Color.parseColor("#FF7F24"));;
			    				break;
			    			}
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
			    			{
			    				setEmailButton.setTextColor(Color.GRAY);
			    				break;
			    			}
			    		case MotionEvent.ACTION_UP: 
			    			{
			    				setEmailButton.setTextColor(Color.parseColor("#FF7F24"));;
			    				break;
			    			}
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
				   switch ( event.getAction() ) {
			    		case MotionEvent.ACTION_DOWN: 
			    			{
			    				signOutButton.setTextColor(Color.GRAY);
			    				break;
			    			}
			    		case MotionEvent.ACTION_UP: 
			    			{
			    				signOutButton.setTextColor(Color.parseColor("#FF7F24"));;
			    				break;
			    			}
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
		this.showSignOutAlertDialog();
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
    	input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
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

    private void showSignOutAlertDialog() {
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
    	builder.setMessage(getResources().getString(R.string.alert_quit)).setPositiveButton(getResources().getString(R.string.alert_quit_yes), dialogClickListener)
    	    .setNegativeButton(getResources().getString(R.string.alert_quit_no), dialogClickListener).show();
    }
    
    private void enteredNewEmail(String email) {
		userCredentials.setEmail(email);
		this.emailButtonVisible = false;
		this.setButtonVisibility(passwordButtonVisible, emailButtonVisible);
	}
	
	private void enteredNewPassword(String password) {
		userCredentials.setPassword(password);
		this.passwordButtonVisible = false;
		this.setButtonVisibility(passwordButtonVisible, emailButtonVisible);
	}
	
	private void signOut() {
		this.userCredentials.clearAllCredentials();
		this.userCredentials = new UserCredentials(this.getApplicationContext());
		this.checkUserIsLoggedIn();
	}
	
	private void checkUserIsLoggedIn() {
		String identifier = this.userCredentials.getIdentifier();
		String email = this.userCredentials.getEmail();
		
		////TODO: Might check facebook login 
		if((identifier.length() <= 0) && (email.length() <= 0)) {
			Intent intent = new Intent(AccountManagerActivity.this, CredentialScreenActivity.class);
			startActivity(intent);
			this.finish();
		}
	}
	
}
