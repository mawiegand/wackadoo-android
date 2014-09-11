package com.wackadoo.wackadoo_client.adapter;

import java.util.Calendar;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Days;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.model.GameInformation;
 
public class GamesListViewAdapter extends ArrayAdapter<GameInformation> implements OnTouchListener {
 
    private Context context;
 
    public GamesListViewAdapter(Context context, int resourceId, List<GameInformation> items) {
        super(context, resourceId, items);
        this.context = context;
    }
     
    // private view holder class 
    private class ViewHolder {
        ImageView worldItemIcon;
        TextView worldItemNameText, worldItemPlayerCount, worldItemScopeText, worldItemDayText;
    }
     
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder holder = null;
    	GameInformation rowItem = getItem(position);
    	
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.table_item_game, null);
            holder = new ViewHolder();
            holder.worldItemIcon = (ImageView) convertView.findViewById(R.id.worldItemIcon);
            holder.worldItemNameText = (TextView) convertView.findViewById(R.id.worldItemNameText);
            holder.worldItemPlayerCount = (TextView) convertView.findViewById(R.id.worldItemPlayerCount);
            holder.worldItemScopeText = (TextView) convertView.findViewById(R.id.worldItemScopeText);
            holder.worldItemDayText = (TextView) convertView.findViewById(R.id.worldItemDayText);
            convertView.setTag(holder); 
            
        } else {
        	holder = (ViewHolder) convertView.getTag();
        }

        // game icon
        holder.worldItemIcon.setImageResource(R.drawable.check_marked_box);
        
        // game name
        holder.worldItemNameText.setText(rowItem.getName());					
        
        // game player
        String playerText = rowItem.getPresentPlayers() + "/" + rowItem.getMaxPlayers();	
        holder.worldItemPlayerCount.setText(playerText);
        
        // game scope
        holder.worldItemScopeText.setText(rowItem.getScope());	
        
        // game day
        Calendar c = Calendar.getInstance();
        Calendar gameStart = Calendar.getInstance();
        gameStart.setTime(rowItem.getStartedAt());
        String daysString = null;
        
        if(gameStart.before(c)) {
        	int days = Days.daysBetween(new DateTime(gameStart.getTime()), new DateTime(c.getTime())).getDays(); 
        	daysString = String.format(context.getResources().getString(R.string.selectgame_item_days), days); 	
        
        } else {
        	int days = Days.daysBetween(new DateTime(c.getTime()), new DateTime(gameStart.getTime())).getDays(); 
        	daysString = String.format(context.getResources().getString(R.string.selectgame_item_days_till_start), days); 	
        }
        
        holder.worldItemDayText.setText(daysString);

        convertView.setOnTouchListener(this);
        return convertView;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
    	int action = event.getActionMasked();
    	
    	if(action == (MotionEvent.ACTION_DOWN)) {
    		v.setBackgroundColor(context.getResources().getColor(R.color.shop_listitem_active));
    		
    	} else if (action == MotionEvent.ACTION_CANCEL) {
    		v.setBackgroundColor(context.getResources().getColor(R.color.white));
    		
    	} else if (action == MotionEvent.ACTION_UP) {
    		v.setBackgroundColor(context.getResources().getColor(R.color.white));
    		v.performClick();
    	} 
    	return false;
    }
}