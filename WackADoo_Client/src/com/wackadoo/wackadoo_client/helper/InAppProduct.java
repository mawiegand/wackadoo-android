package com.wackadoo.wackadoo_client.helper;

public class InAppProduct {

	private String price;
	private String productId;
	private String priceCurrencyCode;
	
	public InAppProduct(String price, String productId, String priceCurrencyCode) {
		this.price = price;
		this.productId = productId;
		this.priceCurrencyCode = priceCurrencyCode;
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
	
	public String getPriceCurrencyCode() {
		return priceCurrencyCode;
	}
	public void setPriceCurrencyCode(String priceCurrencyCode) {
		this.priceCurrencyCode = priceCurrencyCode;
	}

	public String getPriceAsNumber() {
		String[] parts = price.split("\\s+");
		String result = parts[0].replace(",", ".");
		return result;
	}
}