package com.wackadoo.wackadoo_client.activites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
	private ListView listViewPlatinumAccount, listViewGold, listViewBonus;
	private List<ShopRowItem> listGold, listAccount, listBonus;
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
        
        listViewPlatinumAccount = (ListView) findViewById(R.id.listPlatinumAccount);
        listViewGold = (ListView) findViewById(R.id.listGold);
        listViewBonus = (ListView) findViewById(R.id.listBonus);

        setUpBtns();
        loadShopOffersFromServer(); 
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		removeShopFragment();
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
		setUpCreditsBtn();
		setUpInfoBtns();
	}
	
	private void setUpDoneBtn() {
		doneBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				switch (e.getAction()) {
		    		case MotionEvent.ACTION_DOWN: 
		    			doneBtn.setTextColor(getResources().getColor(R.color.textbox_orange_active));
		    			break;
	
		    		case MotionEvent.ACTION_UP: 
		    			doneBtn.setTextColor(getResources().getColor(R.color.textbox_orange));
		    			ShopActivity.this.finish();
		    			break;
				}
				return true;
			}
		});
	}
	
	// touch listener for info buttons
	private void setUpInfoBtns() {
		OnTouchListener touchListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				int action = e.getActionMasked(); 
				
				if(action == MotionEvent.ACTION_DOWN) {
					((ImageView) v).setImageResource(R.drawable.title_info_button_active);
					
				} else if(action == MotionEvent.ACTION_CANCEL) {
					((ImageView) v).setImageResource(R.drawable.title_info_button);
					
				} else if((action == MotionEvent.ACTION_UP)) {
					((ImageView) v).setImageResource(R.drawable.title_info_button);
					switch(v.getId()) {
		    			case R.id.platinumCreditsInfoBtn:
		    				openShopInfoFragment("platinumCredits");
		    				break;
		    			case R.id.goldInfoBtn:
		    				openShopInfoFragment("gold");
		    				break;
		    			case R.id.platinumAccountInfoBtn:
		    				openShopInfoFragment("platinumAccount");
		    				break;
		    			case R.id.bonusInfoBtn:
		    				openShopInfoFragment("bonus");
		    				break;
					}
				}
				return true;
			}
		};
		platinumCreditsInfoBtn.setOnTouchListener(touchListener);
		goldInfoBtn.setOnTouchListener(touchListener);
		platinumAccountInfoBtn.setOnTouchListener(touchListener);
		bonusInfoBtn.setOnTouchListener(touchListener);
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
		    	return true;
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
		setUpDialog();
		progressDialog.show();
		String token = userCredentials.getAccessToken().getToken();
		new GetShopOffersAsyncTask(this, getApplicationContext(), token, 1).execute();		// get golden frog offers
		new GetShopOffersAsyncTask(this, getApplicationContext(), token, 2).execute();		// get platinum account offers
		new GetShopOffersAsyncTask(this, getApplicationContext(), token, 3).execute();		// get bonus offers
	}
	
	private void insertRowItemsInList(List<ShopRowItem> items, ListView list) {
		ShopListViewAdapter adapter = new ShopListViewAdapter(this, R.layout.table_item_shop, items);
		list.setAdapter(adapter);
		UtilityHelper.setListViewHeightBasedOnChildren(list);
	}

	// handle clicks on platinum account list
	private void setUpListPlatinumAccount(List<ShopRowItem> offers) {
		listAccount = offers;
		insertRowItemsInList(listAccount, listViewPlatinumAccount);
		
		listViewPlatinumAccount.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);
		    	builder.setMessage("Weiterleitung zum Platinum Account Item " +  listAccount.get(position).getId())
		  		       .setPositiveButton("Ok", null)
		  			   .show();
		  		// TODO buy platinum account
		    	return true;
			}
		});
	}
	
	// handle clicks on gold list
	private void setUpListGold(List<ShopRowItem> offers) {
		listGold = offers;
		insertRowItemsInList(listGold, listViewGold);
		listViewGold.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);
		    	builder.setMessage("Kauf von " + listGold.get(position).getId())
		  		       .setPositiveButton("Ok", null)
		  			   .show();
		  		// TODO buy gold
		    	return true;
			}
		});
	}
	
	// handle clicks on bonus list
	private void setUpListBonus(List<ShopRowItem> offers) {
		listBonus = offers;
		insertRowItemsInList(listBonus, listViewBonus);
		listViewBonus.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(ShopActivity.this);
		    	builder.setMessage("Weiterleitung zum Bonus Item " + listBonus.get(position).getId())
		  		       .setPositiveButton("Ok", null)
		  			   .show();
		  		// TODO buy bonus
		    	return true;
			}
		});
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	// callback interface for GetShopOffersAsyncTask
	@Override
	public void getShopOffersCallback(List<ShopRowItem> offers, int offerType) {
		switch(offerType) {
			case 1: setUpListGold(offers); break;
			case 2:	setUpListPlatinumAccount(offers); break;
			case 3: setUpListBonus(offers); break;
		}
	}	
	
	// set up the standard server communiation dialog
	private void setUpDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getResources().getString(R.string.server_communication));
		progressDialog.setMessage(getResources().getString(R.string.please_wait));
	}

	// play store: first set up store billing
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
			if(progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			Log.d(TAG, "skuDetails: " + skuDetails.toString());
			stringProductList = skuDetails.getStringArrayList("DETAILS_LIST");
			ArrayList<ShopRowItem> rowItems = produceRowItemList();
			openCreditsFragment(rowItems);
		}
	}

	// play store: returns list of ShopRowItems for given list of json products (sorted by price)
	private ArrayList<ShopRowItem> produceRowItemList() {
		ArrayList<ShopRowItem> rowItemList = new ArrayList<ShopRowItem>();
		
		// sort by price, cheapest item first
		Comparator<String> comparator = new Comparator<String>() {
			@Override
			public int compare(String itemA, String itemB) {
				try {
					// compare and sort by price in micro-format
					int priceA = Integer.valueOf(new JSONObject(itemA).getString("price_amount_micros"));
					int priceB = Integer.valueOf(new JSONObject(itemB).getString("price_amount_micros"));
				
					if(priceA > priceB) {
						return 1;
					} else if (priceA < priceB) {
						return -1;
					} 
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				return 0;
			}
		};
		Collections.sort(stringProductList, comparator);
		
		// create list of ShopRowItem out of Strings
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