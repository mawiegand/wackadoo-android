package com.wackadoo.wackadoo_client.activites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.adjust.sdk.Adjust;
import com.android.vending.billing.IabHelper.OnConsumeFinishedListener;
import com.android.vending.billing.IabHelper.OnIabPurchaseFinishedListener;
import com.android.vending.billing.IabResult;
import com.android.vending.billing.Inventory;
import com.android.vending.billing.Purchase;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.adapter.ShopListViewAdapter;
import com.wackadoo.wackadoo_client.analytics.SampleHelper;
import com.wackadoo.wackadoo_client.fragments.ShopCreditsFragment;
import com.wackadoo.wackadoo_client.fragments.ShopInfoFragment;
import com.wackadoo.wackadoo_client.helper.CustomIabHelper;
import com.wackadoo.wackadoo_client.helper.CustomProgressDialog;
import com.wackadoo.wackadoo_client.helper.InAppProduct;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.interfaces.BuyPlayStoreCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.BuyShopOfferCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.CreditsFragmentCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.ShopDataCallbackInterface;
import com.wackadoo.wackadoo_client.model.AdjustProperties;
import com.wackadoo.wackadoo_client.model.ShopRowItem;
import com.wackadoo.wackadoo_client.model.UserCredentials;
import com.wackadoo.wackadoo_client.tasks.BuyPlayStoreAsyncTask;
import com.wackadoo.wackadoo_client.tasks.BuyShopOfferAsyncTask;
import com.wackadoo.wackadoo_client.tasks.GetShopDataAsyncTask;

public class ShopActivity extends WackadooActivity implements ShopDataCallbackInterface, CreditsFragmentCallbackInterface,
		BuyShopOfferCallbackInterface, BuyPlayStoreCallbackInterface, OnIabPurchaseFinishedListener, OnConsumeFinishedListener {
	
	private static final String TAG = ShopActivity.class.getSimpleName();
	
	private TextView doneBtn;
	private ImageButton platinumCreditsInfoBtn, goldInfoBtn, platinumAccountInfoBtn, bonusInfoBtn, specialInfoBtn;
	private ListView listViewPlatinumAccount, listViewGold, listViewBonus;
	private List<ShopRowItem> listGold, listAccount, listBonus;
	private Fragment fragment;
	private UserCredentials userCredentials;
	private CustomProgressDialog progressDialog;	
	private CustomIabHelper billingHelper;
	private ArrayList<String> stringProductList;
	private String shopCharacterId;
	private int platinCredits;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_shop);
        
        userCredentials = new UserCredentials(this);

        String test = "9,99 €";
        convertPriceToCents(test);
        test = "9,99 US¢";
        convertPriceToCents(test);
        
        
        setUpUi();
        setUpButtons();
        loadShopOffersFromServer(); 
	}
	
	// remove displayed fragment and progressdialog to avoid NullpointerException when activity is resumed
	@Override
	protected void onPause() {
		super.onPause();
		
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		getFragmentManager().popBackStack();
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    if (billingHelper != null) {
	    	billingHelper.dispose();
	    }
	}
	
	// set up interface elements
	private void setUpUi() {
		doneBtn = (TextView) findViewById(R.id.shopTopbarDone);
        platinumCreditsInfoBtn = (ImageButton) findViewById(R.id.platinumCreditsInfoBtn);
        goldInfoBtn = (ImageButton) findViewById(R.id.goldInfoBtn);
        platinumAccountInfoBtn = (ImageButton) findViewById(R.id.platinumAccountInfoBtn);
        bonusInfoBtn = (ImageButton) findViewById(R.id.bonusInfoBtn);
        specialInfoBtn = (ImageButton) findViewById(R.id.specialInfoBtn);
        
        listViewPlatinumAccount = (ListView) findViewById(R.id.listPlatinumAccount);
        listViewGold = (ListView) findViewById(R.id.listGold);
        listViewBonus = (ListView) findViewById(R.id.listBonus);
        
        // set up standard server communication dialog
	    progressDialog = new CustomProgressDialog(this);
	}
	
	// start setup methods for each button in UI
	public void setUpButtons() {
		setUpDoneBtn();
		setUpCreditsBtn();
		setUpInfoBtns();
	}
	
	// set up touchlistener for done button
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
		    			soundManager.setContinueMusic(true);
		    			finish();
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
					((ImageView) v).setImageResource(R.drawable.btn_info_active);
					
				} else if (action == MotionEvent.ACTION_CANCEL) {
					((ImageView) v).setImageResource(R.drawable.btn_info);
					
				} else if ((action == MotionEvent.ACTION_UP)) {
					((ImageView) v).setImageResource(R.drawable.btn_info);
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
		    			case R.id.specialInfoBtn:
		    				openShopInfoFragment("special");
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
		specialInfoBtn.setOnTouchListener(touchListener);
	}
	
	//set up special offer button
	private void setUpSpecialOfferButton(final List<ShopRowItem> offers) {
		if (!offers.isEmpty()) {
			findViewById(R.id.shopSpecialOfferButton).setVisibility(View.VISIBLE);
			findViewById(R.id.specialInfoBtn).setVisibility(View.VISIBLE);
			findViewById(R.id.specialImage).setVisibility(View.VISIBLE);
			findViewById(R.id.subheadingSpecial).setVisibility(View.VISIBLE);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) findViewById(R.id.subheadingGold).getLayoutParams();
			params.addRule(RelativeLayout.BELOW, R.id.shopSpecialOfferButton);
			findViewById(R.id.subheadingGold).setLayoutParams(params);
			
	        // set custom font to special offer elements
	        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/obelix.ttf");
	        ((TextView)findViewById(R.id.subheadingSpecial)).setTypeface(tf);
	        ((TextView)findViewById(R.id.shopSpecialOfferButton)).setTypeface(tf);
			
			TextView shopCreditsBtn = (TextView) findViewById(R.id.shopSpecialOfferButton);
			shopCreditsBtn.setOnTouchListener(new View.OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					int action = event.getActionMasked();

					if (action == (MotionEvent.ACTION_DOWN)) {
						v.setBackgroundColor(getResources().getColor(
								R.color.textbox_orange_active));
						return true;
					} else if (action == MotionEvent.ACTION_CANCEL) {
						v.setBackgroundColor(getResources().getColor(
								R.color.textbox_orange));
						return true;
					} else if (action == MotionEvent.ACTION_UP) {
						v.setBackgroundColor(getResources().getColor(
								R.color.textbox_orange));
						final Dialog dialog = new Dialog(ShopActivity.this);
						dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
						dialog.setContentView(R.layout.dialog_special_offer);
						dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
					    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
						dialog.getWindow().setFlags(
							    WindowManager.LayoutParams.FLAG_FULLSCREEN, 
							    WindowManager.LayoutParams.FLAG_FULLSCREEN);
						((TextView) dialog.findViewById(R.id.price))
								.setText(offers.get(0).getPrice() + " Credits");
						((TextView) dialog.findViewById(R.id.price))
								.setTypeface(Typeface.createFromAsset(
										getAssets(), "fonts/obelix.ttf"));
						((TextView) dialog.findViewById(R.id.changeFont))
								.setTypeface(Typeface.createFromAsset(
										getAssets(), "fonts/obelix.ttf"));
						((TextView) dialog.findViewById(R.id.buyButton))
								.setTypeface(Typeface.createFromAsset(
										getAssets(), "fonts/obelix.ttf"));

						dialog.findViewById(R.id.buyButton).setOnTouchListener(
								new View.OnTouchListener() {
									public boolean onTouch(View v,
											MotionEvent event) {
										int action = event.getActionMasked();
										if (action == MotionEvent.ACTION_UP) {
											int offerId = offers.get(0).getId();
											progressDialog.show();
											new BuyShopOfferAsyncTask(
													ShopActivity.this,
													userCredentials, offerId,
													shopCharacterId, "special_offer")
													.execute();
											return true;
										}
										return true;
									}
								});
						dialog.show();
						return false;
					}
					return true;
				}
			});
	
		} else {
			findViewById(R.id.shopSpecialOfferButton).setVisibility(View.GONE);
			findViewById(R.id.specialInfoBtn).setVisibility(View.GONE);
			findViewById(R.id.specialImage).setVisibility(View.GONE);
			findViewById(R.id.subheadingSpecial).setVisibility(View.GONE);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) findViewById(R.id.subheadingGold).getLayoutParams();
			params.addRule(RelativeLayout.BELOW, R.id.shopCreditsAmountText);
			findViewById(R.id.subheadingGold).setLayoutParams(params);
		}
	}
	
	// set up touchlistener for credits button
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
	
	// start slideIn animation of fragment and open it
	private void openShopInfoFragment(String category) {
		fragment = new ShopInfoFragment(category);;

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
	
	// handle click on credit category --> Google Play
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
	
	// start slideOut animation of fragment to remove it from screen
	public void removeShopFragment() {
		// slide shop info fragment out of window
        getFragmentManager().beginTransaction()
        	.setCustomAnimations(R.anim.slide_from_right,
         						R.anim.slide_to_right)
			.remove(fragment)
			.commit();
	}
	
	// callback interface for credits fragment -> which item was clicked
	public void creditsFragmentCallback(int clickedPackage) {
		try {
			JSONObject jsonItem = new JSONObject(stringProductList.get(clickedPackage));
			billingHelper.launchPurchaseFlow(ShopActivity.this, jsonItem.getString("productId"), 1337, this); 	// requestCode 1337 is not used, so this number is not important
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	// start tasks to load shop offers from bytro server
	private void loadShopOffersFromServer() {
		progressDialog.show();
		new GetShopDataAsyncTask(this, userCredentials, 1, "").execute();		// get golden frog offers
		new GetShopDataAsyncTask(this, userCredentials, 2, "").execute();		// get platinum account offers
		new GetShopDataAsyncTask(this, userCredentials, 3, "").execute();		// get bonus offers
		new GetShopDataAsyncTask(this, userCredentials, 4, "").execute();		// get special offers
		new GetShopDataAsyncTask(this, userCredentials, 5, "").execute();		// get character shop data	
	}
	
	// set up and fill given listview with items of given list
	private void insertRowItemsInList(List<ShopRowItem> items, ListView list) {
		ShopListViewAdapter adapter = new ShopListViewAdapter(this, R.layout.table_item_shop, items);
		list.setAdapter(adapter);
		StaticHelper.setListViewHeightBasedOnChildren(list);
	}

	// handle clicks on items in platinum account list
	private void setUpListPlatinumAccount(List<ShopRowItem> offers) {
		listAccount = offers;
		insertRowItemsInList(listAccount, listViewPlatinumAccount);
		listViewPlatinumAccount.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				int offerId = listAccount.get(position).getId();
				progressDialog.show();
				new BuyShopOfferAsyncTask(ShopActivity.this, userCredentials, offerId, shopCharacterId, "platinum").execute();
				return true;
			}
		});
	}
	
	// handle clicks on items in gold list
	private void setUpListGold(List<ShopRowItem> offers) {
		listGold = offers;
		insertRowItemsInList(listGold, listViewGold);
		listViewGold.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				int offerId = listGold.get(position).getId();
				progressDialog.show();
				new BuyShopOfferAsyncTask(ShopActivity.this, userCredentials, offerId, shopCharacterId, "resource").execute();
				return true;
			}
		});
	}
	
	// handle clicks on items in bonus list
	private void setUpListBonus(List<ShopRowItem> offers) {
		listBonus = offers;
		Collections.sort(offers);
		insertRowItemsInList(listBonus, listViewBonus);
		listViewBonus.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				int offerId = listBonus.get(position).getId();
				progressDialog.show();
				new BuyShopOfferAsyncTask(ShopActivity.this, userCredentials, offerId, shopCharacterId, "bonus").execute();
				return true;
			}
		});
	}

	// callback interface for GetShopDataAsyncTask
	@Override
	public void getShopDataCallback(boolean result, List<ShopRowItem> offers, int data, String customerId, int offerType) {
		if (result) {
			switch(offerType) {
			case 1: setUpListGold(offers); break;
			case 2:	setUpListPlatinumAccount(offers); break;
			case 3: setUpListBonus(offers); break;
			case 4: setUpSpecialOfferButton(offers); break;
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
				((ScrollView) findViewById(R.id.scrollView)).fullScroll(ScrollView.FOCUS_UP);
				if (progressDialog.isShowing()) {
					progressDialog.dismiss();
				}
				break;
			}
			((ScrollView) findViewById(R.id.scrollView)).fullScroll(ScrollView.FOCUS_UP);
			
		} else {
			Toast.makeText(this, getResources().getString(R.string.error_server_communication), Toast.LENGTH_SHORT)
			 	 .show();
			
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		}
	}	
	
	// callback interface for BuyShopOfferAsyncTask
	@Override
	public void buyShopOfferCallback(boolean result, String message) {
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		
		if (result) {
			Toast.makeText(this, getString(R.string.buy_item_success), Toast.LENGTH_SHORT)
				 .show();
			loadShopOffersFromServer();		//TODO: Should the shop be reloaded?
			
		} else {
			Toast.makeText(this, getString(R.string.buy_item_fail), Toast.LENGTH_LONG)
				 .show();
		}
	}
	
	// callback interface for purchase of play store item
	@Override
	public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
		if (purchase == null) {
			Toast.makeText(this, getString(R.string.buy_credits_fail), Toast.LENGTH_SHORT)
			 .show();
			return;
		}
		
		// code 0 = successful purchase
		if(result.getResponse() == 0) {
			InAppProduct inAppProductData = billingHelper.getInAppProduct(purchase.getSku());
			new BuyPlayStoreAsyncTask(this, userCredentials, purchase, inAppProductData).execute();
		
		// code 7 = already purchased
		} else if (result.getResponse() == 7){
			Toast.makeText(this, getString(R.string.buy_credits_fail), Toast.LENGTH_SHORT)
				 .show();
		} 
	}
	
	// check for every unconsumed item if its already validated by server and consume it afterwards
	public void handleUnconsumedItems(Inventory inv) {
		for (Purchase purchase : inv.getAllPurchases()) {
			InAppProduct inAppProductData = billingHelper.getInAppProduct(purchase.getSku());
			if (inAppProductData != null) {
				new BuyPlayStoreAsyncTask(this, userCredentials, purchase, inAppProductData).execute();
			}
		}
	}
	
	// callback interface for communication with backend after successful play store purchase
	@Override
	public void buyPlayStoreCallback(int responseCode, Purchase purchase, String message) {
		
		if (StaticHelper.debugEnabled) {
			Log.d(TAG, "playstore response code: " + responseCode);
		}
		
		if (responseCode == 200) {
			billingHelper.consumeAsync(purchase, this);
			Toast.makeText(this, getString(R.string.buy_credits_success), Toast.LENGTH_SHORT)
			     .show();
			
			// get character new credit amount
			new GetShopDataAsyncTask(this, userCredentials, 5, "").execute();	
			
			// remove credit fragment and shop shop again
			getFragmentManager().popBackStack();		
		
			String price = billingHelper.getPrice(purchase.getSku());

			// Only track if there is a product
			if (price.length() > 0) { 
				// adjust.io track gross revenue
				Adjust.trackRevenue(convertPriceToCents(price));
			
				SampleHelper sh = SampleHelper.getInstance(getApplicationContext());
				sh.setUserId(userCredentials.getIdentifier());
				Map<String, String> params = new HashMap<String, String>();
				
				// format price
				price = price.replace(",", ".");
				String[] parts = price.split("\\s+");
				price = parts[0];
				params.put("pur_gross", price);
				params.put("pur_earnings", String.valueOf(Double.valueOf(price)*0.81));
				params.put("pur_currency", billingHelper.getInAppProduct(purchase.getSku()).getPriceCurrencyCode());
				params.put("pur_receipt_identifier", purchase.getToken());
				sh.track("purchase", "revenue", params);
			}
		// product already validated by server -> consume
		} else if (responseCode == 403){
			billingHelper.consumeAsync(purchase, ShopActivity.this);

		// 400 = missing parameters in request | 422 = technical server error 
		} else if (responseCode == 400 || responseCode == 422){
			Toast.makeText(this, getString(R.string.buy_credits_fail), Toast.LENGTH_LONG)
				 .show();
		} 
	}
	
	public double convertPriceToCents(String price) {
		String result = price.replace(",", "");
		String[] parts = result.split("\\s+");
		return Double.parseDouble(parts[0]);
	}
	
	// play store: set up in app billing
	private void setUpBilling() {
		progressDialog.show();
		
		// establish connection to play store
		String base64PublicKey = getResources().getString(R.string.playstorePublicKey); 	
		billingHelper = new CustomIabHelper(this, base64PublicKey);
		
		if (StaticHelper.debugEnabled) {
			billingHelper.enableDebugLogging(true);		
		} 
		billingHelper.startUp();
	}
	
	// play store: handle result of play store fragment
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		billingHelper.handleActivityResult(requestCode, resultCode, data);
		
		// play store dialog stops background music, so prepare to start again
		soundManager.prepare();
	}
	
	// play store: callback interface for billingHelper.getProductsAsyncInternal
	@Override
	public void getProductsCallback(ArrayList<String> productList){
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		
		this.stringProductList = productList;

		ArrayList<ShopRowItem> rowItems = produceRowItemList();
		openCreditsFragment(rowItems);
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
				ShopRowItem item = new ShopRowItem(R.drawable.resource_platinum_big, title, 0);
				rowItemList.add(item);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rowItemList;
	}
	
	// play store: callback interface for consume finished
	@Override
	public void onConsumeFinished(Purchase purchase, IabResult result) {
		if (StaticHelper.debugEnabled) {
			Log.d(TAG, "Play Store product consumed -> purchase successful");
		}
	}		

}
