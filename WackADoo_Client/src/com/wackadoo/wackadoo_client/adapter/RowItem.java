package com.wackadoo.wackadoo_client.adapter;

public class RowItem {
    private int iconImageId;
    private String title;
    private int optionalIconImageId;
     
    public RowItem(int iconImageId, String title, int optionalImageId) {
        this.iconImageId = iconImageId;
        this.title = title;
        this.optionalIconImageId = optionalImageId;
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