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
import android.widget.TextView;

public class AccountManagerActivity extends Activity {
	
	private UserCredentials userCredentials;
	private TextView usernameTextField;
	private Button signOutButton, emailButton, passwordButton;
	private enum AlertCallback { Email, Password }
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    
	    this.userCredentials = new UserCredentials(this.getApplicationContext());
	    this.checkUserIsLoggedIn();
	    
	    setContentView(R.layout.activity_accountmanager);
        usernameTextField = (TextView) findViewById(R.id.usernameText);
        signOutButton = (Button) findViewById(R.id.signOutButton);
        emailButton = (Button) findViewById(R.id.emailButton);
        passwordButton = (Button) findViewById(R.id.passwordButton);
        
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
		} else {
			this.passwordButton.setEnabled(false);
			this.passwordButton.setVisibility(View.GONE);
		}
		if(this.userCredentials.getEmail().length() <= 0) {
			this.setUpEmailButton();
		} else {
			this.emailButton.setEnabled(false);
			this.emailButton.setVisibility(View.GONE);
		}
		if(this.userCredentials.getUsername().length() > 0) {
			this.setUpSignOutButton();
		} else {
			this.signOutButton.setEnabled(false);
			this.signOutButton.setVisibility(View.GONE);
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
		this.emailButton.setOnTouchListener(new View.OnTouchListener() {
			
			@SuppressLint("NewApi")
			@Override
			   public boolean onTouch(View v, MotionEvent event) {
				   switch ( event.getAction() ) {
			    		case MotionEvent.ACTION_DOWN: 
			    			{
			    				emailButton.setTextColor(Color.GRAY);
			    				break;
			    			}
			    		case MotionEvent.ACTION_UP: 
			    			{
			    				emailButton.setTextColor(Color.parseColor("#FF7F24"));;
			    				break;
			    			}
				   }
				   return false;
				}
		   });
		this.emailButton.setOnClickListener(new View.OnClickListener() {
			
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
				signOut();
			}

		});		
	}


	protected void startChangePassword() {
		this.showInputAlertDialogWithText("Change Password", AlertCallback.Password);
	}

	protected void startSetEmail() {
		this.showInputAlertDialogWithText("Set E-Mail", AlertCallback.Email);
	}
	
	protected void signOut() {
		this.checkUserIsLoggedIn();
	}

	private void loadCredentialsToUI() {
    	String username = userCredentials.getUsername();
    	if(username != null) {
    		usernameTextField.setText(username);
    	}
	}

    private void showInputAlertDialogWithText(String text, final AlertCallback callback) {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(text);

    	final EditText input = new EditText(this);
    	input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    	builder.setView(input);

    	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	    	switch(callback) {
    	    		case Email : enteredNewEmail(input.getText().toString()); return;
    	    		case Password : enteredNewPassword(input.getText().toString()); return;
    	    		default : return;
    	    	}
    	    }
    	});
    	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	    @Override
    	    public void onClick(DialogInterface dialog, int which) {
    	        dialog.cancel();
    	    }
    	});

    	builder.show();
    }

	private void enteredNewEmail(String email) {
		userCredentials.setEmail(email);
	}
	
	private void enteredNewPassword(String password) {
		userCredentials.setPassword(password);
	}
	
	private void checkUserIsLoggedIn() {
		////TODO: Might check facebook or e-mail login 
		if(this.userCredentials.getIdentifier().length() <= 0 ) {
			Intent intent = new Intent(AccountManagerActivity.this, CredentialScreenActivity.class);
			startActivity(intent);
			this.finish();
		}
		
	}
    
}
