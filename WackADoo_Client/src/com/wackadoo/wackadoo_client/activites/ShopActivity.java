package com.wackadoo.wackadoo_client.activites;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Intent;
import android.media.MediaPlayer;
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
import android.widget.Toast;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.adapter.ShopListViewAdapter;
import com.wackadoo.wackadoo_client.fragments.ShopCreditsFragment;
import com.wackadoo.wackadoo_client.fragments.ShopInfoFragment;
import com.wackadoo.wackadoo_client.helper.CustomIabHelper;
import com.wackadoo.wackadoo_client.helper.CustomProgressDialog;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.helper.WackadooActivity;
import com.wackadoo.wackadoo_client.interfaces.BuyShopOfferCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.CreditsFragmentCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.ShopDataCallbackInterface;
import com.wackadoo.wackadoo_client.model.ShopRowItem;
import com.wackadoo.wackadoo_client.model.UserCredentials;
import com.wackadoo.wackadoo_client.tasks.BuyShopOfferAsyncTask;
import com.wackadoo.wackadoo_client.tasks.GetShopDataAsyncTask;

public class ShopActivity extends WackadooActivity implements ShopDataCallbackInterface, CreditsFragmentCallbackInterface, BuyShopOfferCallbackInterface {
	
	private static final String TAG = ShopActivity.class.getSimpleName();
	
	private TextView doneBtn;
	private ImageButton platinumCreditsInfoBtn, goldInfoBtn, platinumAccountInfoBtn, bonusInfoBtn;
	private ListView listViewPlatinumAccount, listViewGold, listViewBonus;
	private List<ShopRowItem> listGold, listAccount, listBonus;
	private Fragment fragment;
	private UserCredentials userCredentials;
	private CustomProgressDialog progressDialog;	
	private CustomIabHelper billingHelper;
	private ArrayList<String> stringProductList;
	private String shopCharacterId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState, R.layout.activity_shop);
        
        userCredentials = new UserCredentials(this);
        
        doneBtn = (TextView) findViewById(R.id.shopTopbarDone);
        platinumCreditsInfoBtn = (ImageButton) findViewById(R.id.platinumCreditsInfoBtn);
        goldInfoBtn = (ImageButton) findViewById(R.id.goldInfoBtn);
        platinumAccountInfoBtn = (ImageButton) findViewById(R.id.platinumAccountInfoBtn);
        bonusInfoBtn = (ImageButton) findViewById(R.id.bonusInfoBtn);
        
        listViewPlatinumAccount = (ListView) findViewById(R.id.listPlatinumAccount);
        listViewGold = (ListView) findViewById(R.id.listGold);
        listViewBonus = (ListView) findViewById(R.id.listBonus);

		progressDialog = new CustomProgressDialog(this);
        
        setUpButtons();
        loadShopOffersFromServer(); 
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    if (billingHelper != null) {
	    	billingHelper.dispose();
	    }
	}
	
	public void setUpButtons() {
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
		    			StaticHelper.continueMusic = true;
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
		// TODO: fill in credit amount
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
	
	// load shop offers from bytro server
	private void loadShopOffersFromServer() {
		progressDialog.show();
		new GetShopDataAsyncTask(this, userCredentials, 1).execute();		// get golden frog offers
		new GetShopDataAsyncTask(this, userCredentials, 2).execute();		// get platinum account offers
		new GetShopDataAsyncTask(this, userCredentials, 3).execute();		// get bonus offers
		new GetShopDataAsyncTask(this, userCredentials, 4).execute();		// get character shop data
	}
	
	private void insertRowItemsInList(List<ShopRowItem> items, ListView list) {
		ShopListViewAdapter adapter = new ShopListViewAdapter(this, R.layout.table_item_shop, items);
		list.setAdapter(adapter);
		StaticHelper.setListViewHeightBasedOnChildren(list);
		
	}

	// handle clicks on platinum account list
	private void setUpListPlatinumAccount(List<ShopRowItem> offers) {
		listAccount = offers;
		insertRowItemsInList(listAccount, listViewPlatinumAccount);
		
		listViewPlatinumAccount.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				int offerId = listAccount.get(position).getId();
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
				progressDialog.show();
				new BuyShopOfferAsyncTask(ShopActivity.this, userCredentials.getAccessToken().getToken(), offerId, shopCharacterId, "resource").execute();
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
				int offerId = listBonus.get(position).getId();
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
			case 4: 
				String textAmount = String.format(getString(R.string.current_frog_text), data);
				((TextView) findViewById(R.id.currentFrogText)).setText(textAmount);			// TODO: correct value?
				textAmount = String.format(getString(R.string.current_credit_text), data);
				((TextView) findViewById(R.id.shopCreditsAmountText)).setText(textAmount);
				shopCharacterId = customerId;
				
				break;
		}
	}	
	
	// callback interface for BuyShopOfferAsyncTask
	@Override
	public void buyShopOfferCallback(boolean result, String message) {
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		
		if (result) {
			Toast.makeText(this, "GEKLAPPT!", Toast.LENGTH_LONG)
			.show();
			
		} else {
			Toast.makeText(this, getString(R.string.buy_item_fail), Toast.LENGTH_LONG)
				 .show();
		}
		
	}
	
	// play store: set up in app billing
	private void setUpBilling(){
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
		
		// play store dialog stops background music
		StaticHelper.backgroundMusicPlayer = MediaPlayer.create(this, R.raw.themesong);
		StaticHelper.backgroundMusicPlayer.setLooping(true);
		StaticHelper.backgroundMusicPlayer.setVolume(100, 100);
		StaticHelper.backgroundMusicPlayer.start();
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

}