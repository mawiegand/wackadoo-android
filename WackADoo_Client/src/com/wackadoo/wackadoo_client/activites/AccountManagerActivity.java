package com.wackadoo.wackadoo_client.activites;

import com.example.wackadoo_webview.R;
import com.wackadoo.wackadoo_client.model.UserCredentials;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

public class AccountManagerActivity extends Activity {
	
	private UserCredentials userCredentials;
	private TextView usernameTextField;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
	    setContentView(R.layout.activity_accountmanager);
        usernameTextField = (TextView) findViewById(R.id.usernameText);
        
	    this.userCredentials = new UserCredentials(this.getApplicationContext());
	    this.loadCredentialsToUI();
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
    
    private void loadCredentialsToUI() {
    	String username = userCredentials.getUsername();
    	if(username != null) {
    		usernameTextField.setText(username);
    	}
	}

}
