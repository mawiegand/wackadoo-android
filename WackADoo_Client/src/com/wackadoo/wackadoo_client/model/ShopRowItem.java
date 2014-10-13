package com.wackadoo.wackadoo_client.model;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

import android.text.format.Time;

public class ShopRowItem implements Comparable<ShopRowItem> {
	
	private int id; 
    private int iconImageId;
    private String title;
    private int optionalIconImageId;
	private int bonus;
	@SerializedName("finished_at") 
	private Date expiresIn;
     
    public ShopRowItem(int iconImageId, String title, int optionalImageId) {
        this.iconImageId = iconImageId;
        this.title = title;
        this.optionalIconImageId = optionalImageId;
    }
    
    public ShopRowItem(int id, int iconImageId, String title, int optionalImageId, int bonus, Date expiresIn) {
    	this.id = id;
    	this.iconImageId = iconImageId;
    	this.title = title;
    	this.optionalIconImageId = optionalImageId;
    	this.setExpiresIn(expiresIn);
    	this.setBonus(bonus);
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
    
	public int getBonus() {
		return bonus;
	}

	public void setBonus(int bonus) {
		this.bonus = bonus;
	}
	

	public Date getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(Date expiresIn) {
		this.expiresIn = expiresIn;
	}
	

	public boolean isNotExpired() {
		return expiresIn != null && expiresIn.getTime() != 0 && expiresIn.after(new Date());
	}
	
	@Override
    public String toString() {
        return title + "\n" + optionalIconImageId;
    }

	
	@Override
	public int compareTo(ShopRowItem another) {
		int compareIcon = another.getIconImageId() - getIconImageId();
		if (compareIcon == 0) {
			return getBonus() - another.getBonus();
		}
		else return compareIcon;
	}   
}