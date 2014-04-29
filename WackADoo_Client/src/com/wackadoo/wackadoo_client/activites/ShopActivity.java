package com.wackadoo.wackadoo_client.activites;

import java.util.ArrayList;
import java.util.List;

import com.example.wackadoo_webview.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ShopActivity extends Activity {
	
	ListView listPlatinumAccount, listPlatinumCredits, listGold, listBonus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_shop);
        
        listPlatinumAccount = (ListView) findViewById(R.id.listPlatinumAccount);
        listPlatinumCredits = (ListView) findViewById(R.id.listPlatinumCredits);
        listGold = (ListView) findViewById(R.id.listGold);
        listBonus = (ListView) findViewById(R.id.listBonus);
        
        this.fillTablesWithData(); 
        
        this.setUpListPlatinumAccount();
        this.setUpListPlatinumCredits();
        this.setUpListGold();
        this.setUpListBonus();
	}

	private void fillTablesWithData() {
		///TODO: Fill with content from server -> Remove manual added content
		
		ArrayAdapter<String> platinumAccountAdapter, platinumCreditsAdapter, goldAdapter, bonusAdapter;
		List<String> valuesToAdd;
		
		// Adding values to platinum account
		valuesToAdd = new ArrayList<String>();
		for(int i = 1; i>0; i--)
		{
			valuesToAdd.add(String.format(getString(R.string.listPlatinumAccountText), 7,10));
		}
		platinumAccountAdapter = new ArrayAdapter<String>(this, R.layout.table_shop, valuesToAdd);
		listPlatinumAccount.setAdapter(platinumAccountAdapter);
		
		// Adding values to platinumCredits
		valuesToAdd = new ArrayList<String>();
		for(int i = 1; i>0; i--)
		{
			valuesToAdd.add(getString(R.string.listPlatinumCreditsText));
		}
		platinumCreditsAdapter = new ArrayAdapter<String>(this, R.layout.table_shop, valuesToAdd);
		listPlatinumCredits.setAdapter(platinumCreditsAdapter);
		
		// Adding values to listGold
		valuesToAdd = new ArrayList<String>();
		for(int i = 1; i>0; i--)
		{
			valuesToAdd.add(String.format(getString(R.string.listGoldText),15,8));
		}
		goldAdapter = new ArrayAdapter<String>(this, R.layout.table_shop, valuesToAdd);
		listGold.setAdapter(goldAdapter);
		
		// Adding values to listBonus
		valuesToAdd = new ArrayList<String>();
		for(int i = 1; i>0; i--)
		{
			valuesToAdd.add(String.format(getString(R.string.listBonusText),5,48,1));
		}
		bonusAdapter = new ArrayAdapter<String>(this, R.layout.table_shop, valuesToAdd);
		listBonus.setAdapter(bonusAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		  	return true;
		}
		
	@Override
	protected void onResume() {
		super.onResume();
		////TODO: Remove if not needed
		};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	

	private void setUpListPlatinumAccount() {
		listPlatinumAccount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		      @Override
		      public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
		    	  buyPlatinumAccount(parent.getItemAtPosition(position));
		      }
		});
	}

	private void setUpListPlatinumCredits() {
		listPlatinumCredits.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		      @Override
		      public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
		    	  buyPlatinumCredits(parent.getItemAtPosition(position));
		      }
		});
	}

	private void setUpListGold() {
		listPlatinumAccount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		      @Override
		      public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
		    	  buyGold(parent.getItemAtPosition(position));
		      }
		});
	}
	
	private void setUpListBonus() {
		listPlatinumAccount.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		      @Override
		      public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
		    	  buyBonus(parent.getItemAtPosition(position));
		      }
		});
	}
	
	private void buyBonus(Object itemAtPosition) {
		// TODO buy bonus method
	}

	private void buyPlatinumAccount(Object itemAtPosition) {
		// TODO buy platinum method
	}
	
	private void buyPlatinumCredits(Object itemAtPosition) {
		// TODO buy credits
	}

	private void buyGold(Object itemAtPosition) {
		// TODO buy gold
	}
	
	
	
}
