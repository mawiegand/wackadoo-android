package com.wackadoo.wackadoo_client.activites;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.GetChars;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fivedlab.sample.sample_java.Sample;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.analytics.AutoPing;
import com.wackadoo.wackadoo_client.helper.UtilityHelper;
import com.wackadoo.wackadoo_client.interfaces.CharacterCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.CreateAccountCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.CurrentGamesCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.GameLoginCallbackInterface;
import com.wackadoo.wackadoo_client.model.CharacterInformation;
import com.wackadoo.wackadoo_client.model.GameInformation;
import com.wackadoo.wackadoo_client.model.UserCredentials;
import com.wackadoo.wackadoo_client.tasks.GameLoginAsyncTask;
import com.wackadoo.wackadoo_client.tasks.GetCharacterAsyncTask;
import com.wackadoo.wackadoo_client.tasks.GetCurrentGamesAsyncTask;

public class MainActivity extends Activity implements
		CreateAccountCallbackInterface, GameLoginCallbackInterface, CurrentGamesCallbackInterface, CharacterCallbackInterface, Runnable {

	private static final String TAG = MainActivity.class.getSimpleName();
	
	private ImageButton playBtn, accountmanagerBtn, selectGameBtn, 
			facebookBtn, shopBtn, soundBtn, infoBtn;
	private Button characterFrame;
	private boolean soundOn, lastWorldAccessible, loggedIn;
	private AnimationDrawable playButtonAnimation;
	private UserCredentials userCredentials;
	private Handler customHandler;
	private ProgressDialog progressDialog;
	
	@SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
	    playBtn = (ImageButton) findViewById(R.id.loginButton);
	    accountmanagerBtn = (ImageButton) findViewById(R.id.accountmanagerButton);
	    selectGameBtn = (ImageButton) findViewById(R.id.chooseworldButton);
	    shopBtn = (ImageButton) findViewById(R.id.shopButton);
	    facebookBtn = (ImageButton) findViewById(R.id.facebookButton);
	    soundBtn = (ImageButton) findViewById(com.wackadoo.wackadoo_client.R.id.title_sound_button);
	    infoBtn = (ImageButton) findViewById(R.id.title_info_button);
	    characterFrame = (Button) findViewById(R.id.characterFrame);
	    
	    userCredentials = new UserCredentials(this.getApplicationContext());
	    
	    // sound is off by default
	    soundOn = false;
	    
	    // user logged outby default
	    loggedIn = false;

	    // set up standard server communication dialog
	    setUpDialog();
	    
	    // put version number in textview
		try {
			String versionName = "Version " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			((TextView) findViewById(R.id.title_version_text)).setText(versionName);
		} 
		catch (NameNotFoundException e) {
			e.printStackTrace(); 
		} 
	    
	    setUpButtons();
	    setUpPlayButtonAnimation();


		// Start tracking
		Sample.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.ZZZZZ"));
		Sample.setServerSide(false);
		Sample.setAppToken("wad-rt82-fhjk-18");
		AutoPing.getInstance().startAutoPing();
		tokenHandler = new Handler();
		tokenHandler.post(this);
	}

	

	private void setUpPlayButtonAnimation() {
		// start animation of glance
		playBtn.setImageResource(R.anim.animationlist_loginbutton);
		playButtonAnimation = (AnimationDrawable) playBtn.getDrawable();
		playButtonAnimation.start();

		// start scale animation
		customHandler = new android.os.Handler();
		customHandler.postDelayed(updateTimerThread, 0);
	}

	// runs the scale animation repeatedly
	private Runnable updateTimerThread = new Runnable() {
		public void run() {
			Animation scaleAnimation = AnimationUtils.loadAnimation(
					getApplicationContext(), R.anim.scale_loginbutton);
			playBtn.startAnimation(scaleAnimation);
			customHandler.postDelayed(this, 6000);
		}
	};

	private Handler tokenHandler;

	private void setUpButtons() {
		setUpPlayBtn();
	   	setUpShopBtn();
	   	setUpAccountmanagerBtn();
	   	setUpSelectgameBtn();
	   	setUpFacebookBtn();
	   	setUpSoundBtn();
	   	setUpInfoBtn();
	   	setUpCharacterFrame();
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.userCredentials = new UserCredentials(getApplicationContext());
		if (!UtilityHelper.isOnline(this)) {
			Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
		}
		else {
			triggerLogin();
			triggerGetGames();
		}				
	}
	 
	@Override
	public void onDestroy() {
		super.onDestroy();
		AutoPing.getInstance().stopAutoPing();
	}
	
	private void setUpFacebookBtn() {
		facebookBtn.setEnabled(true);
		facebookBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				switch (e.getAction()) {
				case MotionEvent.ACTION_DOWN:
					facebookBtn.setImageResource(R.drawable.title_facebook_button_active);
					break;

				case MotionEvent.ACTION_UP:
					facebookBtn.setImageResource(R.drawable.title_facebook_button);
					break;
				}
				return false;
			}
		});

		facebookBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// facebookButton.setEnabled(false);
				triggerFacebook();
			}
		});
	}

	private void setUpPlayBtn() {
		playBtn.setEnabled(true);
		playBtn.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					playBtn.setImageResource(R.drawable.title_play_button_active);
					customHandler.removeCallbacks(updateTimerThread);
					break;

				case MotionEvent.ACTION_UP:
					// playBtn.setImageResource(R.drawable.title_play_button);
					setUpPlayButtonAnimation();
					break;
				}
				return false;
			}
		});

		playBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(loggedIn) {
					triggerPlayGame();
				} else {
					Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_necessary), Toast.LENGTH_SHORT)
						 .show();
				}
			}
		});
	}

	private void setUpShopBtn() {
		shopBtn.setEnabled(true);
		shopBtn.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					shopBtn.setImageResource(R.drawable.title_shop_button_active);
					break;

				case MotionEvent.ACTION_UP:
					shopBtn.setImageResource(R.drawable.title_shop_button);
					break;
				}
				return false;
			}
		});

		shopBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, ShopActivity.class);
				startActivity(intent);
			}
		});
	}

	private void setUpSoundBtn() {
		soundBtn.setEnabled(true);
		soundBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (soundOn) {
					soundBtn.setImageResource(R.drawable.title_sound_off);
					soundOn = false;

				} else {
					soundBtn.setImageResource(R.drawable.title_sound_on);
					soundOn = true;
				}
			}
		});
	}

	private void setUpInfoBtn() {
		infoBtn.setEnabled(true);
		infoBtn.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					infoBtn.setImageResource(R.drawable.title_info_button_active);
					break;

				case MotionEvent.ACTION_UP:
					infoBtn.setImageResource(R.drawable.title_info_button);
					break;
				}
				return false;
			}
		});

		infoBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						InfoScreenActivity.class);
				startActivity(intent);
			}
		});
	}

	private void setUpAccountmanagerBtn() {
		accountmanagerBtn.setVisibility(View.VISIBLE);
		accountmanagerBtn.setEnabled(true);
		accountmanagerBtn.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					accountmanagerBtn
							.setImageResource(R.drawable.title_change_button_active);
					break;

				case MotionEvent.ACTION_UP:
					accountmanagerBtn
							.setImageResource(R.drawable.title_change_button);
					break;
				}
				return false;
			}
		});

		accountmanagerBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						AccountManagerActivity.class);
				startActivity(intent);
			}
		});
	}
	
	private void setUpSelectgameBtn() {
		selectGameBtn.setVisibility(View.VISIBLE);
		selectGameBtn.setEnabled(true);
		
		// TODO: is last world accessible?
		lastWorldAccessible = true;
		
		if(lastWorldAccessible) {
			selectGameBtn.setImageResource(R.drawable.title_changegame_button);
		} else {
			selectGameBtn.setImageResource(R.drawable.title_changegame_warn_button);
		}
			
		
		selectGameBtn.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: 
						if(lastWorldAccessible) {
							selectGameBtn.setImageResource(R.drawable.title_changegame_button_active);
						} else {
							selectGameBtn.setImageResource(R.drawable.title_changegame_warn_button_active);
						}
						break;
						
					case MotionEvent.ACTION_UP: 
						if(lastWorldAccessible) {
							selectGameBtn.setImageResource(R.drawable.title_changegame_button);
						} else {
							selectGameBtn.setImageResource(R.drawable.title_changegame_warn_button);
						}
						break;
				}
				return false;
			}
		});
		selectGameBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						SelectGameActivity.class);
				startActivity(intent);
			}
		});
	}

	private void setUpCharacterFrame() {
		characterFrame.setEnabled(true);
		characterFrame.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: 
					characterFrame.setBackgroundResource(R.drawable.title_character_frame_active);
					break;
					
				case MotionEvent.ACTION_UP: 
					characterFrame.setBackgroundResource(R.drawable.title_character_frame);
					break;
				}
				return false;
			}
		});

		characterFrame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = null;
				if(loggedIn) {
					intent = new Intent(MainActivity.this, AccountManagerActivity.class);
				} else {
					intent = new Intent(MainActivity.this, CredentialScreenActivity.class);
				}
				startActivity(intent);
			}
		});

	}
	
	private void triggerGetGames() {
		new GetCurrentGamesAsyncTask(this, getApplicationContext(), userCredentials).execute();		
	}
	
	private void triggerFacebook() {
		// TODO Login using Facebook
		Toast toast = Toast.makeText(this,
				"Facebook Login derzeit nicht möglich", Toast.LENGTH_SHORT);
		toast.show();

	}

	private void triggerLogin() {
		String identifier = userCredentials.getIdentifier();		
		String accessToken = userCredentials.getAccessToken().getToken();
		String email = userCredentials.getEmail();
		
		if(identifier.length() > 0 || accessToken.length() > 0 || email.length() > 0){
			if(userCredentials.getAccessToken().isExpired()) {
				progressDialog.show();
				new GameLoginAsyncTask(this, getApplicationContext(), this.userCredentials, false, false, progressDialog).execute();
			
			} else {
				loggedIn = true;
			}
		} else if (email.contains("generic") && email.contains("@5dlab.com") ) {
			progressDialog.show();
			new GameLoginAsyncTask(this, getApplicationContext(), this.userCredentials, false, false, progressDialog).execute();
			
		} else {
			loggedIn = false;
		}
		
		updateUi();
	}

	private void triggerPlayGame() {
		String accessToken = this.userCredentials.getAccessToken().getToken();
		String tokenExpiration = this.userCredentials.getAccessToken()
				.getExpireCode();
		String userId = this.userCredentials.getIdentifier();
		if(accessToken != null && tokenExpiration != null && !this.userCredentials.getAccessToken().isExpired()) {
			startGame(accessToken, tokenExpiration, userId);
		
		} else {
			progressDialog.show();
			new GameLoginAsyncTask(this, getApplicationContext(), userCredentials, false, false, progressDialog).execute();
		}
	}

	private void startGame(String accessToken, String expiration, String userId) {
		Intent intent = new Intent(MainActivity.this,
				WackadooWebviewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("accessToken", accessToken);
		bundle.putString("expiration", expiration);
		bundle.putString("userId", userId);
		bundle.putString("hostname", "https://gs05.wack-a-doo.de");
		//bundle.putString("hostname", userCredentials.getHostname());
		intent.putExtras(bundle);
		startActivity(intent);
	}
	



	@Override
	public void onRegistrationCompleted(String identifier, String clientID,	String nickname) {
		userCredentials.setIdentifier(identifier);
		userCredentials.setClientID(clientID);
		userCredentials.setUsername(nickname);
		triggerLogin();
		triggerGetGames();
	}

	@Override
	public void loginCallback(boolean result, String accessToken, String expiration, String identifier, boolean restoreAccount, boolean refresh) {
		userCredentials.generateNewAccessToken(accessToken, expiration);
		userCredentials.setIdentifier(identifier);
		
		if (refresh) return;
		
		triggerGetGames();
		
		if (result) Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_success_toast), Toast.LENGTH_LONG).show();
		else Toast.makeText(getApplicationContext(), getResources().getString(R.string.login_failed_toast), Toast.LENGTH_LONG).show();
		
		// TODO: loggedIn if GetGames failed?
		loggedIn = true;
		updateUi();
	}
	
	@Override
	public void getCurrentGamesCallback(ArrayList<GameInformation> games) {
		if (userCredentials.getHostname() == "" || !isGameOnline(games, userCredentials.getGameId())) {
			for (int i = 0; i < games.size(); i++) {
				if (games.get(i).isDefaultGame()) {
					userCredentials.setHostname(games.get(i).getServer());
					userCredentials.setGameId(games.get(i).getId());
					new GetCharacterAsyncTask(this, userCredentials, games.get(i), true).execute();
				}
			}
		}		
	}	
	
	@Override
	public void getCharacterCallback(GameInformation game, boolean createNew) {
		userCredentials.setUsername(game.getCharacter().getName());
		updateUi();
	}
	
	private boolean isGameOnline(ArrayList<GameInformation> games, int gameId) {
		boolean gameOnline = false;
		for (int i = 0; i < games.size(); i++) {
			if (games.get(i).getId() == gameId) {
				gameOnline = true;
				break;
			}
		}
		return gameOnline;
	}

	@Override
	public void loginCallbackError(String error, boolean restoreAccount, boolean refresh) {}
	
	// update views in UI depending on user logged in or not
	private void updateUi() {
		if(loggedIn) {
			setUpAccountmanagerBtn();
			setUpSelectgameBtn();
			((RelativeLayout) findViewById(R.id.headerShopContainer)).setVisibility(View.VISIBLE);
			characterFrame.setText("");	
			((ImageView) findViewById(R.id.characterFrameImageView)).setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.characterFrameTextview)).setText(userCredentials.getUsername());
			
		} else {
			accountmanagerBtn.setVisibility(View.INVISIBLE);
			selectGameBtn.setVisibility(View.INVISIBLE);
			((RelativeLayout) findViewById(R.id.headerShopContainer)).setVisibility(View.INVISIBLE);
			characterFrame.setText(getResources().getString(R.string.credentials_headline));
			((ImageView) findViewById(R.id.characterFrameImageView)).setVisibility(View.INVISIBLE);
			((TextView) findViewById(R.id.characterFrameTextview)).setText("");
		}
	}

	// Set up the standard server communiation dialog
	private void setUpDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setTitle(getResources().getString(R.string.server_communication));
		progressDialog.setMessage(getResources().getString(R.string.please_wait));
	}



	
	@Override
	public void run() {
		if (userCredentials.getAccessToken().isExpired() && (userCredentials.getUsername().length() > 0 || userCredentials.getEmail().length() > 0)) 
			new GameLoginAsyncTask(this, getApplicationContext(), this.userCredentials, false, true, progressDialog).execute();
		Log.d(TAG, "Token refresh handler");
		tokenHandler.postDelayed(this, 10000);		
	}





	
}