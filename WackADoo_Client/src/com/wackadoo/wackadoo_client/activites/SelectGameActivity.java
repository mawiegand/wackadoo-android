package com.wackadoo.wackadoo_client.activites;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.adapter.GamesListViewAdapter;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.interfaces.CharacterCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.CurrentGamesCallbackInterface;
import com.wackadoo.wackadoo_client.model.GameInformation;
import com.wackadoo.wackadoo_client.model.UserCredentials;
import com.wackadoo.wackadoo_client.tasks.GetCharacterAsyncTask;
import com.wackadoo.wackadoo_client.tasks.GetCurrentGamesAsyncTask;

public class SelectGameActivity extends Activity implements CurrentGamesCallbackInterface, CharacterCallbackInterface {
	
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
		
		setUpDoneBtn();
		setUpListView();
	
		// TODO: find a nicer solution for this hack. problem: scrollview is not scrolled to top on start of activity
		final ScrollView scrollview = ((ScrollView) findViewById(R.id.scrollView)); 
		scrollview.post(new Runnable() {
			  @Override public void run() {
			    scrollview.fullScroll(ScrollView.FOCUS_UP);
			  }
		});
		
		// fetch current games from server
		new GetCurrentGamesAsyncTask(this, getApplicationContext(), userCredentials).execute();
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
						finish();
						break;
				}
				return true;
			}
		});
	}

	private void setUpListView() {
		listView = (ListView) findViewById(R.id.listGames);

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				GameInformation clickedGame = games.get(position);
				
				Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
				Calendar c = Calendar.getInstance();		// date today
				
				// game is full
				if (clickedGame.getMaxPlayers() == clickedGame.getPresentPlayers()){
					toast.setText(getResources().getString(R.string.selectgame_game_full));
					
				// signup is disabled 	
				} else if (!clickedGame.isJoined() && !clickedGame.isSignupEnabled()) {
					toast.setText(getResources().getString(R.string.selectgame_signup_disabled));
					
				// signin is disabled 	
				} else if (clickedGame.isJoined() && !clickedGame.isSigninEnabled()) {
					toast.setText(getResources().getString(R.string.selectgame_signin_disabled));
				
				// game not started yet	
				} else if (clickedGame.getStartedAt().after(c.getTime())) {
					toast.setText(getResources().getString(R.string.selectgame_game_not_startet));
				
				// game already finished
				} else if (clickedGame.getEndedAt().before(c.getTime())) {
					toast.setText(getResources().getString(R.string.selectgame_game_finished));
					
				// join world	
				} else {
					userCredentials.setGameId(clickedGame.getId());
					userCredentials.setHostname(clickedGame.getServer());
					if (clickedGame.isJoined()) {
						toast.setText(getResources().getString(R.string.selectgame_game_login) + clickedGame.getName());
						finish();
					
					} else {
						toast.setText(getResources().getString(R.string.selectgame_create_account) + clickedGame.getName()); 
						new GetCharacterAsyncTask(SelectGameActivity.this, userCredentials, clickedGame, true).execute();
					}					
					
				}
				toast.show();
				return true;
			}
		});
	}

	// callback interface for GetCurrentGamesAsyncTask
	@Override
	public void getCurrentGamesCallback(ArrayList<GameInformation> games) {
		this.games = games;
		for (int i = 0; i < games.size(); i++) new GetCharacterAsyncTask(this, userCredentials, games.get(i), false).execute();
		GamesListViewAdapter adapter = new GamesListViewAdapter(getApplicationContext(), R.layout.table_item_game, games);
		listView.setAdapter(adapter);
		StaticHelper.setListViewHeightBasedOnChildren(listView);
	}

	// callback interface for GetCharacterAsyncTask
	@Override
	public void getCharacterCallback(GameInformation game, boolean createNew) {
		if (createNew) {
			userCredentials.setUsername(game.getCharacter().getName());
			finish();
		}
	}
}
