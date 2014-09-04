package com.wackadoo.wackadoo_client.adapter;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
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
 
public class ShopListViewAdapter extends ArrayAdapter<RowItem> implements OnTouchListener {
 
    Context context;
 
    public ShopListViewAdapter(Context context, int resourceId, List<RowItem> items) {
        super(context, resourceId, items);
        this.context = context;
    }
     
    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView title;
        ImageView optionalImageView;
    }
     
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RowItem rowItem = getItem(position);
         
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.table_shop, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.shopListViewItemText);
            holder.imageView = (ImageView) convertView.findViewById(R.id.shopListViewItemIcon);
            holder.optionalImageView = (ImageView) convertView.findViewById(R.id.shopListViewItemOptionalImage);
            convertView.setTag(holder); 
        } else
            holder = (ViewHolder) convertView.getTag();

        holder.title.setText(rowItem.getTitle());
        if(rowItem.getOptionalIconImageId() != 0){
        	holder.optionalImageView.setImageResource(rowItem.getOptionalIconImageId());
        } else {
        	holder.optionalImageView.setVisibility(View.GONE);
        }
        if(rowItem.getIconImageId() != 0){
        	holder.imageView.setImageResource(rowItem.getIconImageId());
        } else {
        	holder.imageView.setVisibility(View.GONE);;
        }
         
        convertView.setOnTouchListener(this);
        return convertView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
    	int action = event.getActionMasked();
    	
    	if(action == (MotionEvent.ACTION_DOWN)) {
    		v.setBackgroundColor(context.getResources().getColor(R.color.shop_listitem_active));
    		return true;
    	} else if (action == MotionEvent.ACTION_CANCEL) {
    		v.setBackgroundColor(context.getResources().getColor(R.color.white));
    		return true;
    	} else if (action == MotionEvent.ACTION_UP) {
    		v.setBackgroundColor(context.getResources().getColor(R.color.white));
    		return false;
    	} 
    	return false;
    }
}