package com.wackadoo.wackadoo_client.interfaces;

import java.util.List;

import com.wackadoo.wackadoo_client.adapter.ShopRowItem;

public interface ShopOffersCallbackInterface {
	
	void getShopOffersCallback(List<ShopRowItem> offers, int offerType);
}
