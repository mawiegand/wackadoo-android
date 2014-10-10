package com.wackadoo.wackadoo_client.model;

public class ShopRowItem {
	
	private int id; 
    private int iconImageId;
    private String title;
    private int optionalIconImageId;
     
    public ShopRowItem(int iconImageId, String title, int optionalImageId) {
        this.iconImageId = iconImageId;
        this.title = title;
        this.optionalIconImageId = optionalImageId;
    }
    
    public ShopRowItem(int id, int iconImageId, String title, int optionalImageId) {
    	this.id = id;
    	this.iconImageId = iconImageId;
    	this.title = title;
    	this.optionalIconImageId = optionalImageId;
    }
    
    public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public int getIconImageId() {
		return iconImageId;
	}
	public void setIconImageId(int iconImageId) {
		this.iconImageId = iconImageId;
	}
	
	public int getOptionalIconImageId() {
		return optionalIconImageId;
	}
	public void setOptionalIconImageId(int optionalIconImageId) {
		this.optionalIconImageId = optionalIconImageId;
	}
	
	public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    
	@Override
    public String toString() {
        return title + "\n" + optionalIconImageId;
    }   
}