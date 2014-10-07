package com.wackadoo.wackadoo_client.activites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IabHelper.OnConsumeFinishedListener;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Purchase;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.adapter.ShopListViewAdapter;
import com.wackadoo.wackadoo_client.adapter.ShopRowItem;
import com.wackadoo.wackadoo_client.fragments.ShopCreditsFragment;
import com.wackadoo.wackadoo_client.fragments.ShopInfoFragment;
import com.wackadoo.wackadoo_client.helper.CustomIabHelper;
import com.wackadoo.wackadoo_client.helper.UtilityHelper;
import com.wackadoo.wackadoo_client.interfaces.BuyPlayStoreCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.BuyShopOfferCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.CharacterCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.CreditsFragmentCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.ShopDataCallbackInterface;
import com.wackadoo.wackadoo_client.model.GameInformation;
import com.wackadoo.wackadoo_client.model.UserCredentials;
import com.wackadoo.wackadoo_client.tasks.BuyPlayStoreAsyncTask;
import com.wackadoo.wackadoo_client.tasks.BuyShopOfferAsyncTask;
import com.wackadoo.wackadoo_client.tasks.GetCharacterAsyncTask;
import com.wackadoo.wackadoo_client.tasks.GetShopDataAsyncTask;

public class ShopActivity extends Activity implements ShopDataCallbackInterface, CreditsFragmentCallbackInterface, BuyShopOfferCallbackInterface, BuyPlayStoreCallbackInterface, OnConsumeFinishedListener {
	
	private static final String TAG = ShopActivity.class.getSimpleName();
	
	private TextView doneBtn;
	private ImageButton platinumCreditsInfoBtn, goldInfoBtn, platinumAccountInfoBtn, bonusInfoBtn;
	private ListView listViewPlatinumAccount, listViewGold, listViewBonus, listViewSpecial;
	private List<ShopRowItem> listGold, listAccount, listBonus, listSpecial;
	private Fragment fragment;
	private UserCredentials userCredentials;
	private ProgressDialog progressDialog;	
	private CustomIabHelper billingHelper;
	private ArrayList<String> stringProductList;
	private String shopCharacterId;

	private int platinCredits;
	
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
        listViewSpecial = (ListView) findViewById(R.id.listSpecial);

        setUpBtns();
        loadShopOffersFromServer(); 
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    if (billingHelper != null) {
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
				
				if (action == MotionEvent.ACTION_DOWN) {
					((ImageView) v).setImageResource(R.drawable.title_info_button_active);
					
				} else if (action == MotionEvent.ACTION_CANCEL) {
					((ImageView) v).setImageResource(R.drawable.title_info_button);
					
				} else if ((action == MotionEvent.ACTION_UP)) {
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
		RelativeLayout shopCreditsBtn = (RelativeLayout) findViewById(R.id.shopCreditsButton); 
		shopCreditsBtn.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
		    	int action = event.getActionMasked();
		    	
		    	if (action == (MotionEvent.ACTION_DOWN)) {
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
		
		if (category.equals("platinumCredits")) {
			fragment = new ShopInfoFragment("platinumCredits");
		
		} else if (category.equals("gold")) {
			fragment = new ShopInfoFragment("gold");
		
		} else if (category.equals("platinumAccount")) {
			fragment = new ShopInfoFragment("platinumAccount");
		
		} else if (category.equals("special")) {
			fragment = new ShopInfoFragment("special");
		
		} else {
			fragment = new ShopInfoFragment("bonus");
		}

		// slide shop info fragment in window
        getFragmentManager().beginTransaction()
         	.setCustomAnimations(R.anim.slide_from_right,
         						R.anim.slide_to_right,
         						R.anim.slide_from_right,
         						R.anim.slide_to_right)         	
        	.add(R.id.activityContainer, fragment)
        	.addToBackStack(category)
        	.commit();
	}
	
	// handles click on credit category --> Google Play
	private void openCreditsFragment(ArrayList<ShopRowItem> rowItemsList) {
		fragment = new ShopCreditsFragment(this, userCredentials, rowItemsList, platinCredits);
		
		// slide shop info fragment in window
		getFragmentManager().beginTransaction()
		.setCustomAnimations(R.anim.slide_from_right,
         					R.anim.slide_to_right,
         					R.anim.slide_from_right,
         					R.anim.slide_to_right)  
				.add(R.id.activityContainer, fragment)
				.addToBackStack("buyCredits")
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
	
	// load shop offers from bytro server
	private void loadShopOffersFromServer() {
		setUpDialog();
		progressDialog.show();
		new GetShopDataAsyncTask(this, userCredentials, 1, "").execute();		// get golden frog offers
		new GetShopDataAsyncTask(this, userCredentials, 2, "").execute();		// get platinum account offers
		new GetShopDataAsyncTask(this, userCredentials, 3, "").execute();		// get bonus offers
		new GetShopDataAsyncTask(this, userCredentials, 4, "").execute();		// get special offers
		new GetShopDataAsyncTask(this, userCredentials, 5, "").execute();		// get character shop data	
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
				int offerId = listAccount.get(position).getId();
				setUpDialog();
				progressDialog.show();
				new BuyShopOfferAsyncTask(ShopActivity.this, userCredentials.getAccessToken().getToken(), offerId, shopCharacterId, "platinum").execute();
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
				int offerId = listGold.get(position).getId();
				setUpDialog();
				progressDialog.show();
				new BuyShopOfferAsyncTask(ShopActivity.this, userCredentials.getAccessToken().getToken(), offerId, shopCharacterId, "resource").execute();
				return true;
			}
		});
	}
	
	// handle clicks on special list
	private void setUpListSpecial(List<ShopRowItem> offers) {
		listSpecial = offers;
		if (offers.isEmpty()) {
			findViewById(R.id.subheadingSpecial).setVisibility(View.GONE);
			findViewById(R.id.specialInfoBtn).setVisibility(View.GONE);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) findViewById(R.id.subheadingGold).getLayoutParams();
			params.addRule(RelativeLayout.BELOW, R.id.shopCreditsAmountText);
			findViewById(R.id.subheadingGold).setLayoutParams(params);
		} else {
			findViewById(R.id.subheadingSpecial).setVisibility(View.VISIBLE);
			findViewById(R.id.specialInfoBtn).setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) findViewById(R.id.subheadingGold).getLayoutParams();
			params.addRule(RelativeLayout.BELOW, R.id.listSpecial);
			findViewById(R.id.subheadingGold).setLayoutParams(params);
		}
		insertRowItemsInList(listSpecial, listViewSpecial);
		listViewSpecial.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				int offerId = listSpecial.get(position).getId();
				setUpDialog();
				progressDialog.show();
				new BuyShopOfferAsyncTask(ShopActivity.this, userCredentials.getAccessToken().getToken(), offerId, shopCharacterId, "special").execute();
				return true;
			}
		});
	}
	
	
	
	// handle clicks on bonus list
	private void setUpListBonus(List<ShopRowItem> offers) {
		listBonus = offers;
		Collections.sort(offers);
		insertRowItemsInList(listBonus, listViewBonus);
		listViewBonus.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				int offerId = listBonus.get(position).getId();
				setUpDialog();
				progressDialog.show();
				new BuyShopOfferAsyncTask(ShopActivity.this, userCredentials.getAccessToken().getToken(), offerId, shopCharacterId, "bonus").execute();
				return true;
			}
		});
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	// callback interface for GetShopDataAsyncTask
	@Override
	public void getShopDataCallback(List<ShopRowItem> offers, int data, String customerId, int offerType) {
		switch(offerType) {
			case 1: setUpListGold(offers); break;
			case 2:	setUpListPlatinumAccount(offers); break;
			case 3: setUpListBonus(offers); break;
			case 4: setUpListSpecial(offers); break;
			case 5: 				
				platinCredits = data;
				String textAmount = String.format(getString(R.string.current_credit_text), data);
				((TextView) findViewById(R.id.shopCreditsAmountText)).setText(textAmount);
				shopCharacterId = customerId;	
				new GetShopDataAsyncTask(this, userCredentials, 6, shopCharacterId).execute();		// get gold frogs amount
				break;
			case 6:
				textAmount = String.format(getString(R.string.current_frog_text), data);
				((TextView) findViewById(R.id.currentFrogText)).setText(textAmount);
		}
		((ScrollView) findViewById(R.id.scrollView)).fullScroll(ScrollView.FOCUS_UP);
	}	
	
	
	// callback interface for BuyShopOfferAsyncTask
	@Override
	public void buyShopOfferCallback(boolean result, String message) {
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		
		if (result) {
			Toast.makeText(this, getString(R.string.buy_item_success), Toast.LENGTH_LONG)
			.show();
			
		} else {
			Toast.makeText(this, getString(R.string.buy_item_fail), Toast.LENGTH_LONG)
				 .show();
		}
		
	}
	
	// set up the standard server communiation dialog
	private void setUpDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getResources().getString(R.string.server_communication));
		progressDialog.setMessage(getResources().getString(R.string.please_wait));
	}
	
	// play store: set up in app billing
	private void setUpBilling(){
		setUpDialog();
		progressDialog.show();
		
		// establish connection to play store
		String base64PublicKey = getResources().getString(R.string.playstorePublicKey); 	
		billingHelper = new CustomIabHelper(this, base64PublicKey);
		
		billingHelper.enableDebugLogging(true);		// TODO: remove before publishing
		billingHelper.startUp();
	}
	
	// callback interface for credits fragment -> which item was clicked
	public void creditsFragmentCallback(int clickedPackage) {
		try {
			JSONObject jsonItem = new JSONObject(stringProductList.get(clickedPackage));
			billingHelper.launchPurchaseFlow(ShopActivity.this, jsonItem.getString("productId"), 1337); 
			
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
		Log.d(TAG, "---> response: " + response);
		
		if (response == 0) {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			Log.d(TAG, "skuDetails: " + skuDetails.toString());
			
			stringProductList = skuDetails.getStringArrayList("DETAILS_LIST");
			ArrayList<ShopRowItem> rowItems = produceRowItemList();
			openCreditsFragment(rowItems);
		}
	}
	

	
	// play store: returns list of platinum credits ShopRowItems for given list of json products (sorted by price)
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
				
					if (priceA > priceB) {
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
				ShopRowItem item = new ShopRowItem(R.drawable.platinum_big, title, 0);
				rowItemList.add(item);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rowItemList;
	}

	@Override
	public void buyPlayStoreCallback(boolean result, String message) {
		Log.d(TAG, "Verification Feedback: "+message);		
	}

	@Override
	public void onConsumeFinished(Purchase purchase, IabResult result) {
		Log.d(TAG, "start play store verification");
		new BuyPlayStoreAsyncTask(ShopActivity.this, userCredentials.getAccessToken().getToken(), purchase.getOrderId(), purchase.getToken(), purchase.getSku()).execute();
	}



}
