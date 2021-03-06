package com.wackadoo.wackadoo_client.adapter;

import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.model.ShopRowItem;
 
public class ShopListViewAdapter extends ArrayAdapter<ShopRowItem> implements OnTouchListener {
 
    Context context;
 
    public ShopListViewAdapter(Context context, int resourceId, List<ShopRowItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }
     
    // private view holder class 
    private class ViewHolder {
        ImageView imageView;
        TextView title;
        ImageView optionalImageView;
        TextView expiresIn;
        int color;
    }
     
    // set up the view and fill in data
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        ShopRowItem rowItem = getItem(position);
         
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.table_item_shop, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.shopListViewItemText);
            holder.imageView = (ImageView) convertView.findViewById(R.id.shopListViewItemIcon);
            holder.optionalImageView = (ImageView) convertView.findViewById(R.id.shopListViewItemOptionalImage);
            holder.expiresIn = (TextView) convertView.findViewById(R.id.shopListViewItemExpiresIn);            
            convertView.setTag(holder); 
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.title.setText(rowItem.getTitle());
        if (rowItem.isNotExpired()) {
        	holder.color = context.getResources().getColor(R.color.shop_item_buyed);
        	holder.expiresIn.setText(context.getString(R.string.shop_expires_in)+new SimpleDateFormat().format(rowItem.getExpiresIn()));
        } else {
        	holder.color = context.getResources().getColor(R.color.white);
        }
        if (rowItem.getOptionalIconImageId() != 0){
        	holder.optionalImageView.setImageResource(rowItem.getOptionalIconImageId());
        } else {
        	holder.optionalImageView.setVisibility(View.GONE);
        }
        if (rowItem.getIconImageId() != 0){
        	holder.imageView.setImageResource(rowItem.getIconImageId());
        } else {
        	holder.imageView.setVisibility(View.GONE);
        }
        convertView.setBackgroundColor(holder.color);
         
        convertView.setOnTouchListener(this);
        StaticHelper.overrideFonts(context, convertView);
        return convertView;
    }

    // handle touch events on this item
    @Override
    public boolean onTouch(View v, MotionEvent event) {
    	int action = event.getActionMasked();

    	if (action == (MotionEvent.ACTION_DOWN)) {
    		v.setBackgroundColor(context.getResources().getColor(R.color.shop_listitem_active));
    		return true;
    	} else if (action == MotionEvent.ACTION_CANCEL) {    		
    		v.setBackgroundColor(((ViewHolder)v.getTag()).color);
    		return true;
    	} else if (action == MotionEvent.ACTION_UP) {
    		v.setBackgroundColor(((ViewHolder)v.getTag()).color);
    		v.performLongClick();
    		return false;
    	} 
    	return false;
    }
}