package com.wackadoo.wackadoo_client;

import com.example.wackadoo_webview.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class LoginScreenActivity extends Activity{
	
		private Button loginButton;
	
		@Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.activity_loginscreen);
		    loginButton = (Button) findViewById(R.id.loginButton);
		    loginButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(LoginScreenActivity.this, WackadooWebviewActivity.class);
			        startActivity(intent);
				}
			});
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
}