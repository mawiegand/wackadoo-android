package com.wackadoo.wackadoo_client.interfaces;

import java.util.List;

import com.wackadoo.wackadoo_client.model.ShopRowItem;

public interface ShopDataCallbackInterface {
	void getShopDataCallback(boolean result, List<ShopRowItem> offers, int data, String shopCharacterId, int offerType);
}
