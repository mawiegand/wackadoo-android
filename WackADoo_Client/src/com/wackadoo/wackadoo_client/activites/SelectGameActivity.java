package com.wackadoo.wackadoo_client.activites;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.adapter.GamesListViewAdapter;
import com.wackadoo.wackadoo_client.helper.UtilityHelper;
import com.wackadoo.wackadoo_client.interfaces.CurrentGamesCallbackInterface;
import com.wackadoo.wackadoo_client.model.GameInformation;

public class SelectGameActivity extends Activity implements CurrentGamesCallbackInterface {
	
	private ListView listView;
	private ArrayList<GameInformation> games;
	private TextView doneBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectgame);
		
		games = new ArrayList<GameInformation>();
		
		setUpButtons();
		setUpListView();
	}
	
	private void setUpButtons() {
		setUpDoneBtn();
	}
	
	private void setUpDoneBtn() {
		doneBtn = (TextView) findViewById(R.id.selectgamesTopbarDone);
		
		doneBtn.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getActionMasked();
				
				switch(action) {
					case MotionEvent.ACTION_DOWN:
						doneBtn.setTextColor(getResources().getColor(R.color.textbox_orange_active));
						break;
						
					case MotionEvent.ACTION_UP:
						doneBtn.setTextColor(getResources().getColor(R.color.textbox_orange));
						break;
				}
				return false;
			}
		});
		
		doneBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void setUpListView() {
		listView = (ListView) findViewById(R.id.listGames);

		// fetch current games from server
//		new GetCurrentGamesAsyncTask(this, getApplicationContext()).execute();
		
		// TODO: remove generateTestItems() and fetch items from server in GetCurrentGamesAsyncTask();
		generateTestItems();
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	    		view.setBackgroundColor(getResources().getColor(R.color.white));
				
				Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
				GameInformation clickedGame = games.get(position);
				
				// game is full
				if(clickedGame.getMaxPlayers() == clickedGame.getPresentPlayers()){
					toast.setText("Das Spiel ist bereits voll!");
				
				// already joined
				} else if(clickedGame.isJoined()) {
					toast.setText("Einloggen in Welt " + clickedGame.getName());
					
				// signup is disabled 	
				} else if(!clickedGame.isJoined() && !clickedGame.isSignupEnabled()) {
					toast.setText("Du kannst derzeit nicht in dieser Welt starten!");
					
				// signin is disabled 	
				} else if(clickedGame.isJoined() && !clickedGame.isSignupEnabled()) {
					toast.setText("Du kannst dich derzeit nicht einloggen!");
				
				// game not started yet	
				// TODO
					
				// game already finished
				// TODO
					
				// join world	
				} else {
					toast.setText("Erstelle neuen Account in Welt " + clickedGame.getName()); 
				}
				toast.show();
			}
		});
	}

	@Override
	public void getCurrentGamesCallback(ArrayList<GameInformation> games) {
		GamesListViewAdapter adapter = new GamesListViewAdapter(getApplicationContext(), R.layout.table_item_game, games);
		listView.setAdapter(adapter);
		UtilityHelper.setListViewHeightBasedOnChildren(listView);
	}
	
	private void generateTestItems() {
		for(int i=0; i<10; i++) {
			GameInformation newGame = new GameInformation();
			newGame.setuId(i);
			newGame.setPresentPlayers(i+11);
			newGame.setMaxPlayers((i+5)*10);
			newGame.setName("Welt " + (i+1));
			newGame.setScope("Testwelt | Insider");
			newGame.setStartedAt(new Date());
			newGame.setEndedAt(new Date());
			newGame.setJoined(false);
			newGame.setSigninEnabled(true);
			newGame.setSignupEnabled(true);
			games.add(newGame);
		}
		
		// test: game already ended
		games.get(4).setName("GAME ALREADY ENDED");
		Calendar c = Calendar.getInstance(); 
        c.set(Calendar.MONTH, c.get(Calendar.MONTH)-2);
		games.get(4).setEndedAt(c.getTime());
		
		// test: full game
		games.get(5).setName("FULL");
		games.get(5).setPresentPlayers(games.get(5).getMaxPlayers());
		
		// test: game already joined
		games.get(6).setName("ALREADY JOINED");
		games.get(6).setJoined(true);
		c = Calendar.getInstance(); 
		c.set(Calendar.MONTH, c.get(Calendar.MONTH)-4);
		games.get(5).setStartedAt(c.getTime());
		
		// test: game not joined, but also signup disabled
		games.get(7).setName("SIGNUP DISABLED");
		games.get(7).setSignupEnabled(false);
		
		// test: game joined, but signin is disabled
		games.get(8).setName("SIGNIN DISABLED");
		games.get(8).setJoined(true);
		games.get(8).setSigninEnabled(false);
		
		// test: game is not open yet
		games.get(9).setName("GAME NOT OPEN YET");
		c = Calendar.getInstance(); 
		c.set(Calendar.MONTH, c.get(Calendar.MONTH)+2);
		games.get(9).setStartedAt(c.getTime());
		
		getCurrentGamesCallback(games);
	}
}
