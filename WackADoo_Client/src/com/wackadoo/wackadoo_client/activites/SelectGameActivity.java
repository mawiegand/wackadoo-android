package com.wackadoo.wackadoo_client.activites;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.adapter.GamesListViewAdapter;
import com.wackadoo.wackadoo_client.helper.UtilityHelper;
import com.wackadoo.wackadoo_client.interfaces.CurrentGamesCallbackInterface;
import com.wackadoo.wackadoo_client.model.GameInformation;
import com.wackadoo.wackadoo_client.model.UserCredentials;
import com.wackadoo.wackadoo_client.tasks.GetCurrentGamesAsyncTask;

public class SelectGameActivity extends Activity implements CurrentGamesCallbackInterface {
	
	private ListView listView;
	private ArrayList<GameInformation> games;
	private TextView doneBtn;
	private UserCredentials userCredentials;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_selectgame);
		
		userCredentials = new UserCredentials(this.getApplicationContext());
		
		games = new ArrayList<GameInformation>();
		
		setUpButtons();
		setUpListView();
	
		// TODO: find a nicer solution for this hack. problem: scrollview is not scrolled to top on start of activity
		final ScrollView scrollview = ((ScrollView) findViewById(R.id.scrollView)); 
		scrollview.post(new Runnable() {
			  @Override public void run() {
			    scrollview.fullScroll(ScrollView.FOCUS_UP);
			  }
		});
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
		new GetCurrentGamesAsyncTask(this, getApplicationContext(), userCredentials).execute();
		
		// TODO: remove generateTestItems() and fetch items from server in GetCurrentGamesAsyncTask();
		//generateTestItems();
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				GameInformation clickedGame = games.get(position);
				
				Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
				Calendar c = Calendar.getInstance();		// date today
				
				// game is full
				if(clickedGame.getMaxPlayers() == clickedGame.getPresentPlayers()){
					toast.setText("Das Spiel ist bereits voll!");
				
				// already joined
				} else if(clickedGame.isJoined() && clickedGame.isSigninEnabled()) {
					toast.setText("Einloggen in Welt " + clickedGame.getName());
					
				// signup is disabled 	
				} else if(!clickedGame.isJoined() && !clickedGame.isSignupEnabled()) {
					toast.setText("Du kannst derzeit nicht in dieser Welt starten!");
					
				// signin is disabled 	
				} else if(clickedGame.isJoined() && !clickedGame.isSigninEnabled()) {
					toast.setText("Du kannst dich derzeit nicht einloggen!");
				
				// game not started yet	
				} else if(clickedGame.getStartedAt().after(c.getTime())) {
					toast.setText("Das Spiel hat noch nicht begonnen!");
				
				// game already finished
				} else if(clickedGame.getEndedAt().before(c.getTime())) {
					toast.setText("Das Spiel ist bereits vorbei!");
					
				// join world	
				} else {
					toast.setText("Erstelle neuen Account in Welt " + clickedGame.getName()); 
				}
				toast.show();
				return true;
			}
		});
	}

	@Override
	public void getCurrentGamesCallback(ArrayList<GameInformation> games) {
		GamesListViewAdapter adapter = new GamesListViewAdapter(getApplicationContext(), R.layout.table_item_game, games);
		listView.setAdapter(adapter);
		UtilityHelper.setListViewHeightBasedOnChildren(listView);
	}
	
	// TODO: remove method to generate test items
	private void generateTestItems() {
		for(int i=0; i<10; i++) {
			GameInformation newGame = new GameInformation();
			newGame.setuId(i);
			newGame.setPresentPlayers(i+11);
			newGame.setMaxPlayers((i+5)*10);
			newGame.setName("Welt " + (i+1));
			newGame.setScope("Testwelt | Insider");
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, 2013);
			newGame.setStartedAt(c.getTime());
			c.add(Calendar.YEAR, 13);
			newGame.setEndedAt(c.getTime());
			newGame.setJoined(false);
			newGame.setSigninEnabled(true);
			newGame.setSignupEnabled(true);
			games.add(newGame);
		}
		
		// test: full game
		games.get(4).setName("Game full");
		games.get(4).setPresentPlayers(games.get(4).getMaxPlayers());
		
		// test: game already joined
		games.get(5).setName("Already joined");
		games.get(5).setJoined(true);
		Calendar c = Calendar.getInstance(); 
		c.set(Calendar.MONTH, c.get(Calendar.MONTH)-4);
		games.get(5).setStartedAt(c.getTime());
		
		// test: game not joined, but also signup disabled
		games.get(6).setName("Sigup disabled");
		games.get(6).setSignupEnabled(false);
		
		// test: game joined, but signin is disabled
		games.get(7).setName("Signin disabled");
		games.get(7).setJoined(true);
		games.get(7).setSigninEnabled(false);
		
		// test: game is not open yet
		games.get(8).setName("Game not open yet");
		c = Calendar.getInstance(); 
		c.set(Calendar.MONTH, c.get(Calendar.MONTH)+2);
		games.get(8).setStartedAt(c.getTime());
		
		// test: game is already finished
		games.get(9).setName("Game finished");
		c = Calendar.getInstance(); 
		c.set(Calendar.MONTH, c.get(Calendar.MONTH)-4);
		games.get(9).setEndedAt(c.getTime());
		
		getCurrentGamesCallback(games);
	}
}
