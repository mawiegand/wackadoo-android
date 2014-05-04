package com.wackadoo.wackadoo_client.activites;

import java.util.ArrayList;
import java.util.List;

import com.example.wackadoo_webview.R;
import com.wackadoo.wackadoo_client.adapter.RowItem;
import com.wackadoo.wackadoo_client.adapter.ShopListViewAdapter;
import com.wackadoo.wackadoo_client.interfaces.ShopOffersCallbackInterface;
import com.wackadoo.wackadoo_client.tasks.GetShopOffersAsyncTask;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class ShopActivity extends Activity implements ShopOffersCallbackInterface{
	
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
        
        this.loadShopOffersFromServer(); 
	}

	private void loadShopOffersFromServer() {
		new GetShopOffersAsyncTask(this,getApplicationContext(), getString(R.string.platinumCreditsServerPath)).execute();
		new GetShopOffersAsyncTask(this,getApplicationContext(), getString(R.string.goldFrogsServerPath)).execute();
		new GetShopOffersAsyncTask(this,getApplicationContext(), getString(R.string.platinumAccountServerPath)).execute();
		new GetShopOffersAsyncTask(this,getApplicationContext(), getString(R.string.bonusOffersServerPath)).execute();
		
		//TODO:Remove!!!
		this.getShopOffersCallback(new ArrayList<String>(), "/game_server/shop/bonus_offers");
	}
	
	private void insertRowItemsInList(ArrayList<RowItem> items, ListView list) {
		ShopListViewAdapter adapter = new ShopListViewAdapter(this, R.layout.table_shop, items);
		list.setAdapter(adapter);
		//TODO: calculate right height
		int height=3000;
		this.updateListContainersWithHeight(height);
	}

	private void updateListContainersWithHeight(int height) {
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.listViewContainer);
		layout.setMinimumHeight(height);
		layout = (RelativeLayout) findViewById(R.id.scrollViewContainer);
		layout.setMinimumHeight(height);
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
		// TODO: buy bonus 
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

	@Override
	public void getShopOffersCallback(List<String> offers, String offerType) {
		
		///TODO: Fill with content from server -> Remove manual added content, Add right URL to server_values.xml
		ArrayList<String> valuesToAdd;
		
		if(offerType.compareTo(getString(R.string.platinumCreditsServerPath)) == 0) {
			// Adding values to platinumCredits
			valuesToAdd = new ArrayList<String>();
			for(int i = 1; i>0; i--)
			{
				valuesToAdd.add(getString(R.string.listPlatinumCreditsText));
			}
			ArrayList<RowItem> generatedItems = this.generateRowItemsWithValues(valuesToAdd, R.drawable.platinum_small, 0);
			this.insertRowItemsInList(generatedItems, listPlatinumCredits);
	        this.setUpListPlatinumCredits();
		}

		if(offerType.compareTo(getString(R.string.goldFrogsServerPath)) == 0) {
			// Adding values to listGold
			valuesToAdd = new ArrayList<String>();
			for(int i = 3; i>0; i--)
			{
				valuesToAdd.add(String.format(getString(R.string.listGoldText),15,8));
			}
			ArrayList<RowItem> generatedItems = this.generateRowItemsWithValues(valuesToAdd, R.drawable.goldkroete_128px, 0);
			this.insertRowItemsInList(generatedItems, listGold);
	        this.setUpListGold();
		}
		
		if(offerType.compareTo(getString(R.string.platinumAccountServerPath)) == 0) {
			// Adding values to platinum account
			valuesToAdd = new ArrayList<String>();
			for(int i = 1; i>0; i--)
			{
				valuesToAdd.add(String.format(getString(R.string.listPlatinumAccountText), 7,10));
			}
			ArrayList<RowItem> generatedItems = this.generateRowItemsWithValues(valuesToAdd, 0, 0);
			this.insertRowItemsInList(generatedItems, listPlatinumAccount);
			this.setUpListPlatinumAccount();
		}
		
		if(offerType.compareTo(getString(R.string.bonusOffersServerPath)) == 0) {
			// Adding values to listBonus
			valuesToAdd = new ArrayList<String>();
			for(int i = 9; i>0; i--)
			{
				valuesToAdd.add(String.format(getString(R.string.listBonusText),5,48,1));
			}
			ArrayList<RowItem> generatedItems = this.generateRowItemsWithValues(valuesToAdd, R.drawable.resource_stone, R.drawable.goldkroete_128px);
			this.insertRowItemsInList(generatedItems, listBonus);
			this.setUpListBonus();
		}
	}	
	
	public ArrayList<RowItem> generateRowItemsWithValues(List<String> offers, int leftImage, int rightImage) {
		ArrayList<RowItem> valuesToAdd = new ArrayList<RowItem>();
		for(String current : offers)
		{
			RowItem itemToAdd = new RowItem(leftImage, current, rightImage);
			valuesToAdd.add(itemToAdd);
		}
		return valuesToAdd;
	}
}
