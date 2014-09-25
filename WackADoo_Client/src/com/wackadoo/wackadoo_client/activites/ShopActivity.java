 package com.wackadoo.wackadoo_client.activites;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

import com.android.vending.billing.IabHelper;
import com.android.vending.billing.IabHelper.OnConsumeFinishedListener;
import com.android.vending.billing.IabHelper.OnIabPurchaseFinishedListener;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Purchase;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.adapter.ShopListViewAdapter;
import com.wackadoo.wackadoo_client.adapter.ShopRowItem;
import com.wackadoo.wackadoo_client.fragments.ShopCreditsFragment;
import com.wackadoo.wackadoo_client.fragments.ShopInfoFragment;
import com.wackadoo.wackadoo_client.helper.UtilityHelper;
import com.wackadoo.wackadoo_client.interfaces.CreditsFragmentCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.ShopOffersCallbackInterface;
import com.wackadoo.wackadoo_client.model.UserCredentials;
import com.wackadoo.wackadoo_client.tasks.GetShopOffersAsyncTask;

public class ShopActivity extends Activity implements ShopOffersCallbackInterface, CreditsFragmentCallbackInterface, OnIabPurchaseFinishedListener, OnConsumeFinishedListener {
	
	private static final String TAG = ShopActivity.class.getSimpleName();
	
	private TextView doneBtn;
	private ImageButton platinumCreditsInfoBtn, goldInfoBtn, platinumAccountInfoBtn, bonusInfoBtn;
	private ListView listPlatinumAccount, listPlatinumCredits, listGold, listBonus;
	private Fragment fragment;
	private UserCredentials userCredentials;
	private ProgressDialog progressDialog;	
	private IabHelper billingHelper;
	private ArrayList<String> stringProductList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        
        userCredentials = new UserCredentials(this);
        
        doneBtn = (TextView) findViewById(R.id.shopTopbarDone);
        platinumCreditsInfoBtn = (ImageButton) findViewById(R.id.platinumCreditsInfoBtn);
        goldInfoBtn = (ImageButton) findViewById(R.id.goldInfoBtn);
        platinumAccountInfoBtn = (ImageButton) findViewById(R.id.platinumAccountInfoBtn);
        bonusInfoBtn = (ImageButton) findViewById(R.id.bonusInfoBtn);
        
        listPlatinumAccount = (ListView) findViewById(R.id.listPlatinumAccount);
        listPlatinumCredits = (ListView) findViewById(R.id.listPlatinumCredits);
        listGold = (ListView) findViewById(R.id.listGold);
        listBonus = (ListView) findViewById(R.id.listBonus);

        setUpBtns();
        loadShopOffersFromServer(); 
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    if(billingHelper != null) {
	    	billingHelper.dispose();
	    }
	}
	
	public void setUpBtns() {
		setUpDoneBtn();
		setUpPlatinumCreditsInfoBtn();
		setUpGoldInfoBtn();
		setUpPlatinumAccountInfoBtn();
		setUpBonusInfoBtn();
		setUpCreditsBtn();
	}
	
	private void setUpDoneBtn() {
		doneBtn.setEnabled(true);
		doneBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				   switch (e.getAction()) {
			    		case MotionEvent.ACTION_DOWN: 
			    			doneBtn.setTextColor(getResources().getColor(R.color.textbox_orange_active));
			    			break;
	
			    		case MotionEvent.ACTION_UP: 
			    			doneBtn.setTextColor(getResources().getColor(R.color.textbox_orange));
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
			    			
			    		case MotionEvent.ACTION_CANCEL: 
			    			platinumCreditsInfoBtn.setImageResource(R.drawable.title_info_button);
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
				openShopInfoFragment("platinumCredits");
			}
	   	});
   
	}
	
	private void setUpGoldInfoBtn() {
		goldInfoBtn.setEnabled(true);
		goldInfoBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				switch (e.getAction()) {
				case MotionEvent.ACTION_DOWN: 
					goldInfoBtn.setImageResource(R.drawable.title_info_button_active);
					break;
					
				case MotionEvent.ACTION_CANCEL: 
					goldInfoBtn.setImageResource(R.drawable.title_info_button);
					break;
					
				case MotionEvent.ACTION_UP: 
					goldInfoBtn.setImageResource(R.drawable.title_info_button);
					break;
				}
				return false;
			}
		});
		
		goldInfoBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openShopInfoFragment("gold");
			}
		});
		
	}
		
	private void setUpPlatinumAccountInfoBtn() {
		platinumAccountInfoBtn.setEnabled(true);
		platinumAccountInfoBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				switch (e.getAction()) {
				case MotionEvent.ACTION_DOWN: 
					platinumAccountInfoBtn.setImageResource(R.drawable.title_info_button_active);
					break;
					
				case MotionEvent.ACTION_CANCEL: 
					platinumAccountInfoBtn.setImageResource(R.drawable.title_info_button);
					break;
					
				case MotionEvent.ACTION_UP: 
					platinumAccountInfoBtn.setImageResource(R.drawable.title_info_button);
					break;
				}
				return false;
			}
		});
		
		platinumAccountInfoBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openShopInfoFragment("platinumAccount");
			}
		});
	}
	
	private void setUpBonusInfoBtn() {
		bonusInfoBtn.setEnabled(true);
		bonusInfoBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				switch (e.getAction()) {
				case MotionEvent.ACTION_DOWN: 
					bonusInfoBtn.setImageResource(R.drawable.title_info_button_active);
					break;
					
				case MotionEvent.ACTION_CANCEL: 
					bonusInfoBtn.setImageResource(R.drawable.title_info_button);
					break;
					
				case MotionEvent.ACTION_UP: 
					bonusInfoBtn.setImageResource(R.drawable.title_info_button);
					break;
				}
				return false;
			}
		});
		
		bonusInfoBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openShopInfoFragment("bonus");
			}
		});
	}
	
	private void setUpCreditsBtn() {
		// TODO: fill in credit amount
		RelativeLayout shopCreditsBtn = (RelativeLayout) findViewById(R.id.shopCreditsButton); 
		shopCreditsBtn.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
		    	int action = event.getActionMasked();
		    	
		    	if(action == (MotionEvent.ACTION_DOWN)) {
		    		v.setBackgroundColor(getResources().getColor(R.color.shop_listitem_active));
		    		return true;
		    	} else if (action == MotionEvent.ACTION_CANCEL) {
		    		v.setBackgroundColor(getResources().getColor(R.color.white));
		    		return true;
		    	} else if (action == MotionEvent.ACTION_UP) {
		    		v.setBackgroundColor(getResources().getColor(R.color.white));
		    		setUpBilling();
		    		return false;
		    	} 
		    	return false;
		    }
		});
	}
	
	// handles click on info button for category
	private void openShopInfoFragment(String category) {
		fragment = null;
		
		if(category.equals("platinumCredits")) {
			fragment = new ShopInfoFragment("platinumCredits");
		
		} else if (category.equals("gold")) {
			fragment = new ShopInfoFragment("gold");
		
		} else if (category.equals("platinumAccount")) {
			fragment = new ShopInfoFragment("platinumAccount");
		
		} else {
			fragment = new ShopInfoFragment("bonus");
		}

		// slide shop info fragment in window
        getFragmentManager().beginTransaction()
         	.setCustomAnimations(R.anim.slide_from_right,
         						R.anim.slide_to_right)
        	.add(R.id.activityContainer, fragment)
        	.commit();
	}
	
	// handles click on credit category --> Google Play
	private void openCreditsFragment(ArrayList<ShopRowItem> rowItemsList) {
		fragment = new ShopCreditsFragment(this, userCredentials, rowItemsList);
		
		// slide shop info fragment in window
		getFragmentManager().beginTransaction()
		.setCustomAnimations(R.anim.slide_from_right,
				R.anim.slide_to_right)
				.add(R.id.activityContainer, fragment)
				.commit();
	}
	
	public void removeShopFragment() {
		// slide shop info fragment out of window
        getFragmentManager().beginTransaction()
        	.setCustomAnimations(R.anim.slide_from_right,
         						R.anim.slide_to_right)
			.remove(fragment)
			.commit();
	}
	
	private void loadShopOffersFromServer() {
		new GetShopOffersAsyncTask(this, getApplicationContext(), getString(R.string.platinumCreditsServerPath)).execute();
		new GetShopOffersAsyncTask(this, getApplicationContext(), getString(R.string.goldFrogsServerPath)).execute();
		new GetShopOffersAsyncTask(this, getApplicationContext(), getString(R.string.platinumAccountServerPath)).execute();
		new GetShopOffersAsyncTask(this, getApplicationContext(), getString(R.string.bonusOffersServerPath)).execute();
		
		//TODO:Remove!!!
		getShopOffersCallback(new ArrayList<String>(), "/game_server/shop/bonus_offers");
	}
	
	private void insertRowItemsInList(ArrayList<ShopRowItem> items, ListView list) {
		ShopListViewAdapter adapter = new ShopListViewAdapter(this, R.layout.table_item_shop, items);
		list.setAdapter(adapter);
		UtilityHelper.setListViewHeightBasedOnChildren(list);
	}

	// handle clicks on platinum account list
	private void setUpListPlatinumAccount() {
		listPlatinumAccount.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);
		    	builder.setMessage("Weiterleitung zum Platinum Account Item " +  (position+1))
		  		       .setPositiveButton("Ok", null)
		  			   .show();
		  		// TODO buy platinum account
		    	return true;
			}
		});
	}
	
	// handle clicks on gold list
	private void setUpListGold() {
		listGold.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);
		    	builder.setMessage("Weiterleitung zum Gold Item " + (position+1))
		  		       .setPositiveButton("Ok", null)
		  			   .show();
		  		// TODO buy gold
		    	return true;
			}
		});
	}
	
	// handle clicks on bonus list
	private void setUpListBonus() {
		listBonus.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);
		    	builder.setMessage("Weiterleitung zum Bonus Item " + (position+1))
		  		       .setPositiveButton("Ok", null)
		  			   .show();
		  		// TODO buy bonus
		    	return true;
			}
		});
	}

	// callback interface for GetShopOffersAsyncTask
	@Override
	public void getShopOffersCallback(List<String> offers, String offerType) {
		
		///TODO: Fill with content from server -> Remove manual added content, Add right URL to server_values.xml
		ArrayList<String> valuesToAdd;
		
//		if(offerType.compareTo(getString(R.string.platinumCreditsServerPath)) == 0) {
//			// Adding values to platinumCredits
//			valuesToAdd = new ArrayList<String>();
//			for(int i = 1; i>0; i--)
//			{
//				valuesToAdd.add(getString(R.string.list_platinum_credits_text));
//			}
//			ArrayList<ShopRowItem> generatedItems = this.generateRowItemsWithValues(valuesToAdd, R.drawable.platinum_small, 0);
//			insertRowItemsInList(generatedItems, listPlatinumCredits);
//	        setUpListPlatinumCredits();
//		}

		if(offerType.compareTo(getString(R.string.goldFrogsServerPath)) == 0) {
			// Adding values to listGold
			valuesToAdd = new ArrayList<String>();
			for(int i = 3; i>0; i--)
			{
				valuesToAdd.add(String.format(getString(R.string.list_gold_text),15,8));
			}
			ArrayList<ShopRowItem> generatedItems = this.generateRowItemsWithValues(valuesToAdd, R.drawable.goldkroete_128px, 0);
			insertRowItemsInList(generatedItems, listGold);
	        setUpListGold();
		}
		
		if(offerType.compareTo(getString(R.string.platinumAccountServerPath)) == 0) {
			// Adding values to platinum account
			valuesToAdd = new ArrayList<String>();
			for(int i = 1; i>0; i--)
			{
				valuesToAdd.add(String.format(getString(R.string.list_platinum_account_text),7,10));
			}
			ArrayList<ShopRowItem> generatedItems = this.generateRowItemsWithValues(valuesToAdd, 0, 0);
			insertRowItemsInList(generatedItems, listPlatinumAccount);
			setUpListPlatinumAccount();
		}
		
		if(offerType.compareTo(getString(R.string.bonusOffersServerPath)) == 0) {
			// Adding values to listBonus
			valuesToAdd = new ArrayList<String>();
			for(int i = 9; i>0; i--)
			{
				valuesToAdd.add(String.format(getString(R.string.list_bonus_text),5,48,1));
			}
			ArrayList<ShopRowItem> generatedItems = this.generateRowItemsWithValues(valuesToAdd, R.drawable.resource_stone, R.drawable.goldkroete_128px);
			insertRowItemsInList(generatedItems, listBonus);
			setUpListBonus();
		}
	}	
	
	// generates list of ShopRowItem for given list of Strings
	public ArrayList<ShopRowItem> generateRowItemsWithValues(List<String> offers, int leftImage, int rightImage) {
		ArrayList<ShopRowItem> valuesToAdd = new ArrayList<ShopRowItem>();
		
		for(String current : offers) {
			ShopRowItem itemToAdd = new ShopRowItem(leftImage, current, rightImage);
			valuesToAdd.add(itemToAdd);
		}
		
		return valuesToAdd;
	}

	// set up the standard server communiation dialog
	private void setUpDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getResources().getString(R.string.server_communication));
		progressDialog.setMessage(getResources().getString(R.string.please_wait));
	}

	// play store: set up store billing
	private void setUpBilling(){
		setUpDialog();
		progressDialog.show();
		
		// establish connection to play store
		String base64PublicKey = getResources().getString(R.string.playstorePublicKey); 	
		billingHelper = new IabHelper(this, base64PublicKey);
		
		// TODO: remove before publishing
		billingHelper.enableDebugLogging(true);
	
		Log.d(TAG, "Starting setup.");
		billingHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
	        public void onIabSetupFinished(IabResult result) {
	            if (!result.isSuccess()) {
	            	Log.d(TAG, "-----> Error: " + result.getMessage());
	                return;
	            }
	
	            if (billingHelper == null) {
	            	Log.d(TAG, "-----> Error: billingHelper is null");
	            	return;
	            }
	            // IAB is fully set up 
	            Log.d(TAG, "-----> Setup successful");
	            
	            // get platinum credit packages from play store
	            billingHelper.getProductsAsyncInternal(ShopActivity.this);
	        }
        });
	}
	
	// callback interface for credits fragment -> which product was clicked
	public void creditsFragmentCallback(int clickedPackage) {
		try {
			JSONObject jsonItem = new JSONObject(stringProductList.get(clickedPackage));
//			billingHelper.launchPurchaseFlow(this, "android.test.purchased", 1337, this);
			billingHelper.launchPurchaseFlow(this, jsonItem.getString("productId"), 1337, this); // TODO: fixing
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	// play store: handle result of play store fragment
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		billingHelper.handleActivityResult(requestCode, resultCode, data);
	}
	
	// play store: callback interface for billingHelper.getProductsAsyncInternal
	@Override
	public void getProductsCallback(Bundle skuDetails){
		int response = skuDetails.getInt("RESPONSE_CODE");
		
		if(response == 0) {
			Log.d(TAG, "-----> PRODUKTE ERFOLGREICH VOM SERVER GEHOLT!");
			if(progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			Log.d(TAG, "**** skudDetails: " + skuDetails.toString());
			stringProductList = skuDetails.getStringArrayList("DETAILS_LIST");
			ArrayList<ShopRowItem> rowItems = produceRowItemList();
			openCreditsFragment(rowItems);
		}
	}

	// play store: returns list of ShopRowItems for given list of json products
	private ArrayList<ShopRowItem> produceRowItemList() {
		ArrayList<ShopRowItem> rowItemList = new ArrayList<ShopRowItem>();
		try {
			for(String stringProduct : stringProductList) {
				JSONObject jsonObject = new JSONObject(stringProduct);
				String title = jsonObject.getString("title");
				title = title.replace("(Wackadoo)", "/ " + jsonObject.getString("price"));
				ShopRowItem item = new ShopRowItem(R.drawable.platinum_small, title, 0);
				rowItemList.add(item);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rowItemList;
	}
	
	// play store: callback interface for finished purchase 
	@Override
	public void onIabPurchaseFinished(IabResult result, Purchase info) {
		if(result.getResponse() == 7) {
    		Log.d(TAG, "Unable to buy item, Error Code 7 ---> Consume and Buy again");
    		billingHelper.consumeAsync(info, this);

		} else if(result.getResponse() == 0) {
			Log.d(TAG, "Item successfully bought ---> Consume");
			billingHelper.consumeAsync(info, this);
		}
	}

	// play store: callback interface for finshed consumption
	@Override
	public void onConsumeFinished(Purchase purchase, IabResult result) {
		Log.d(TAG, "Consume Finished: " + result.getResponse());
	}

}