package com.wackadoo.wackadoo_client.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.activites.ShopActivity;

public class ShopInfoFragment extends Fragment {

	private TextView shopBtn;
	private String type;
	private int ressourceArray[];

	public ShopInfoFragment(String type) {
		this.type = type;
		ressourceArray = new int[4];
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_shop_info, container, false);
		
		setUpRessources();
		setUpTextviews(view);
		setUpShopBtn(view);
		
        return view;
    }
	
	public void setUpShopBtn(View view) {
		shopBtn = (TextView) view.findViewById(R.id.shopinfoTopbarShop);
		
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
	
	// get strings from ressources
	private void setUpRessources() {
		if(type.equals("platinumCredits")) {
			ressourceArray[0] = R.string.shopInfoText_credits;
			ressourceArray[1] = R.string.shopInfoHeadingText_credits;
			ressourceArray[2] = R.string.shopInfoDescText_credits;
			ressourceArray[3] = R.drawable.head_girl_colored_small;
			 
		} else if (type.equals("gold")) {
			ressourceArray[0] = R.string.shopInfoText_gold;
			ressourceArray[1] = R.string.shopInfoHeadingText_gold;
			ressourceArray[2] = R.string.shopInfoDescText_gold;
			ressourceArray[3] = R.drawable.head_warrior_colored_small;
			
		} else if (type.equals("platinumAccount")) {
			ressourceArray[0] = R.string.shopInfoText_account;
			ressourceArray[1] = R.string.shopInfoHeadingText_account;
			ressourceArray[2] = R.string.shopInfoDescText_account;
			ressourceArray[3] = R.drawable.head_chef_colored_small;
			
		} else {
			ressourceArray[0] = R.string.shopInfoText_bonus;
			ressourceArray[1] = R.string.shopInfoHeadingText_bonus;
			ressourceArray[2] = R.string.shopInfoDescText_bonus;
			ressourceArray[3] = R.drawable.head_girl_colored_small;
		}
	}

	// set texts to textviews
	private void setUpTextviews(View view) {
		(((TextView) view.findViewById(R.id.shopInfoText))).setText(ressourceArray[0]);	
		(((TextView) view.findViewById(R.id.shopInfoHeadingText))).setText(ressourceArray[1]);	
		(((TextView) view.findViewById(R.id.shopInfoDescText))).setText(ressourceArray[2]);
		(((ImageView) view.findViewById(R.id.shopInfoHeaderIcon))).setImageResource(ressourceArray[3]);
	}
}
