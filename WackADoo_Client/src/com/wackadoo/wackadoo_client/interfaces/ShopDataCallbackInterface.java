package com.wackadoo.wackadoo_client.interfaces;

import java.util.List;

import com.wackadoo.wackadoo_client.adapter.ShopRowItem;

public interface ShopDataCallbackInterface {
	void getShopDataCallback(List<ShopRowItem> offers, int data, String shopCharacterId, int offerType);
}
