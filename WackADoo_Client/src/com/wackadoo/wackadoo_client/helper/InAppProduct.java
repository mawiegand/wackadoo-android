package com.wackadoo.wackadoo_client.helper;

public class InAppProduct {

	private String price;
	private String productId;
	
	public InAppProduct(String price, String productId) {
		this.price = price;
		this.productId = productId;
	}
	
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
}