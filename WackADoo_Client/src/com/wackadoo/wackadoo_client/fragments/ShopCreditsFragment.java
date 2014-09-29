package com.wackadoo.wackadoo_client.fragments;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.activites.ShopActivity;
import com.wackadoo.wackadoo_client.adapter.ShopListViewAdapter;
import com.wackadoo.wackadoo_client.adapter.ShopRowItem;
import com.wackadoo.wackadoo_client.helper.UtilityHelper;
import com.wackadoo.wackadoo_client.interfaces.CreditsFragmentCallbackInterface;
import com.wackadoo.wackadoo_client.model.UserCredentials;

public class ShopCreditsFragment extends Fragment {

	private static final String TAG = ShopCreditsFragment.class.getSimpleName();
	
	private Context context;
	private UserCredentials userCredentials;
	private TextView shopBtn;
	private ListView listView;
	private ArrayList<ShopRowItem> rowItemList;

	public ShopCreditsFragment(){
		
	}
	
	public ShopCreditsFragment(Context context, UserCredentials userCredentials, ArrayList<ShopRowItem> rowItemList) {
		this.context = context;
		this.userCredentials = userCredentials;
		this.rowItemList = rowItemList;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_shop_credits, container, false);
		listView = (ListView) view.findViewById(R.id.listPlatinumCredits);
		
		String headerText = context.getResources().getString(R.string.fragment_platinum_credits_heading);
		headerText = String.format(headerText, 0);	// TODO: fill in credit amount
		((TextView) view.findViewById(R.id.creditsFragmentHeaderText)).setText(headerText);
		
		String footerText = context.getResources().getString(R.string.fragment_platinum_credits_footer);
		footerText = String.format(footerText, userCredentials.getUsername());
		((TextView) view.findViewById(R.id.creditsFragmentFooterText)).setText(footerText);
		
		setUpShopBtn(view);
		setUpListView(listView);
		return view;
    }
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	private void setUpShopBtn(View view) {
		shopBtn = (TextView) view.findViewById(R.id.shopCreditsFragmentTopbarShop);
		
		shopBtn.setEnabled(true);
		shopBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				switch (e.getAction()) {
		    		case MotionEvent.ACTION_DOWN: 
		    			shopBtn.setTextColor(getResources().getColor(R.color.textbox_orange_active));
		    			break;

		    		case MotionEvent.ACTION_UP: 
		    			shopBtn.setTextColor(getResources().getColor(R.color.textbox_orange));
		    			break;
			    }
				return false;
			}
		});
		   
		shopBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((ShopActivity) getActivity()).removeShopFragment();
			}
	   	});
	}

	// add products and clicklistener to listview
	private void setUpListView(ListView listView) {
		ShopListViewAdapter adapter = new ShopListViewAdapter(context, R.drawable.platinum_small, rowItemList);
		listView.setAdapter(adapter);
		UtilityHelper.setListViewHeightBasedOnChildren(listView);
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				((ShopActivity) getActivity()).creditsFragmentCallback(position);
				return false;
			}
			
		});
	}
}
