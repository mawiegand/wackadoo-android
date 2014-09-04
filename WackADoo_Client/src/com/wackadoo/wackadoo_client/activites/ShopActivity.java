package com.wackadoo.wackadoo_client.activites;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.adapter.RowItem;
import com.wackadoo.wackadoo_client.adapter.ShopListViewAdapter;
import com.wackadoo.wackadoo_client.interfaces.ShopOffersCallbackInterface;
import com.wackadoo.wackadoo_client.tasks.GetShopOffersAsyncTask;

public class ShopActivity extends Activity implements ShopOffersCallbackInterface{
	
	private ImageButton platinumCreditsInfoBtn;
	private TextView doneBtn;
	ListView listPlatinumAccount, listPlatinumCredits, listGold, listBonus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        
        doneBtn = (TextView) findViewById(R.id.shop_topbar_done);
        platinumCreditsInfoBtn = (ImageButton) findViewById(R.id.platinumCreditsInfoBtn);
        listPlatinumAccount = (ListView) findViewById(R.id.listPlatinumAccount);
        listPlatinumCredits = (ListView) findViewById(R.id.listPlatinumCredits);
        listGold = (ListView) findViewById(R.id.listGold);
        listBonus = (ListView) findViewById(R.id.listBonus);

        setUpBtns();
        this.loadShopOffersFromServer(); 
	}
	public void setUpBtns() {
		setUpDoneBtn();
		setUpPlatinumCreditsInfoBtn();
	}
	
	private void setUpDoneBtn() {
		doneBtn.setEnabled(true);
		doneBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				   switch (e.getAction()) {
			    		case MotionEvent.ACTION_DOWN: 
			    			doneBtn.setTextColor(getResources().getColor(R.color.topbar_textbox_orange_active));
			    			break;
	
			    		case MotionEvent.ACTION_UP: 
			    			doneBtn.setTextColor(getResources().getColor(R.color.topbar_textbox_orange));
			    			break;
				   }
				   return false;
				}
		});
		   
		doneBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
	   	});
   
	}
	
	private void setUpPlatinumCreditsInfoBtn() {
		platinumCreditsInfoBtn.setEnabled(true);
		platinumCreditsInfoBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				   switch (e.getAction()) {
			    		case MotionEvent.ACTION_DOWN: 
			    			platinumCreditsInfoBtn.setImageResource(R.drawable.title_info_button_active);
			    			break;
	
			    		case MotionEvent.ACTION_UP: 
			    			platinumCreditsInfoBtn.setImageResource(R.drawable.title_info_button);
			    			break;
				   }
				   return false;
				}
		});
		   
		platinumCreditsInfoBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
	   	});
   
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
//		this.updateListContainers();
		setListViewHeightBasedOnChildren(list);
	}

//	private void updateListContainers() {
//		RelativeLayout listView = (RelativeLayout) findViewById(R.id.listViewContainer);
//		RelativeLayout scroll = (RelativeLayout) findViewById(R.id.scrollViewContainer);
//		listView.invalidate();
//		listView.requestLayout();
//		scroll.invalidate();
//		scroll.requestLayout();
//	}
	
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
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Nicht genug Credits!")
			   .setPositiveButton("Ok", null)
			   .show();
		// TODO: buy bonus 
	}
	
	private void buyPlatinumAccount(Object itemAtPosition) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Weiterleitung zum Platinum Account Kauf")
			   .setPositiveButton("Ok", null)
			   .show();
		// TODO buy platinum method
	}
	
	private void buyPlatinumCredits(Object itemAtPosition) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Weiterleitung zum Platinum Credits Kauf")
			   .setPositiveButton("Ok", null)
			   .show();
		// TODO buy credits
	}
	
	private void buyGold(Object itemAtPosition) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Weiterleitung zum Gold Kauf")
			   .setPositiveButton("Ok", null)
			   .show();
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
				valuesToAdd.add(String.format(getString(R.string.listPlatinumAccountText),7,10));
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
			Log.d("Test", "Schleife (generateItemsWithValues) müsste 3 mal RowItem erzeugen");
			RowItem itemToAdd = new RowItem(leftImage, current, rightImage);
			valuesToAdd.add(itemToAdd);
		}
		return valuesToAdd;
	}

	// Workaround for dynamic height of the ListView. Fixes issue of not showing every item in listviews when in a scrollview 
	private void setListViewHeightBasedOnChildren(ListView listView) {
	    ListAdapter listAdapter = listView.getAdapter();
	    if (listAdapter == null)
	        return;
	
	    int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
	    int totalHeight = 0;
	    View view = null;
	    for (int i = 0; i < listAdapter.getCount(); i++) {
	        view = listAdapter.getView(i, view, listView);
	        if (i == 0) {
	        	view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));
	        }
	        view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
	        totalHeight += view.getMeasuredHeight();
	    }
	    ViewGroup.LayoutParams params = listView.getLayoutParams();
	    params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	    listView.setLayoutParams(params);
	    listView.requestLayout();
	}
}