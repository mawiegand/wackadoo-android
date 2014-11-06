package com.wackadoo.wackadoo_client.activites;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
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

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.fivedlab.sample.sample_java.Sample;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.analytics.AutoPing;
import com.wackadoo.wackadoo_client.helper.Avatar;
import com.wackadoo.wackadoo_client.helper.CustomProgressDialog;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.interfaces.CharacterCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.CurrentGamesCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.FacebookTaskCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.GameLoginCallbackInterface;
import com.wackadoo.wackadoo_client.model.GameInformation;
import com.wackadoo.wackadoo_client.model.ResponseResult;
import com.wackadoo.wackadoo_client.model.UserCredentials;
import com.wackadoo.wackadoo_client.tasks.FacebookAccountAsyncTask;
import com.wackadoo.wackadoo_client.tasks.FacebookLoginAsyncTask;
import com.wackadoo.wackadoo_client.tasks.GameLoginAsyncTask;
import com.wackadoo.wackadoo_client.tasks.GetCharacterAsyncTask;
import com.wackadoo.wackadoo_client.tasks.GetCurrentGamesAsyncTask;

public class MainActivity extends Activity implements GameLoginCallbackInterface, FacebookTaskCallbackInterface, 
		CurrentGamesCallbackInterface, CharacterCallbackInterface, Runnable, StatusCallback {

	private static final String TAG = MainActivity.class.getSimpleName();
	
	private ImageButton playBtn, accountmanagerBtn, selectGameBtn, facebookBtn, shopBtn, soundBtn, infoBtn;
	private Button characterFrame;
	private boolean lastWorldAccessible, loggedIn, tryConnect;
	private AnimationDrawable playButtonAnimation;
	private UserCredentials userCredentials;
	private Handler customHandler;
	private CustomProgressDialog progressDialog;
	private Handler tokenHandler;
	private UiLifecycleHelper uiHelper;		
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //facebook lifecycleHelper to keep track of the session
	    uiHelper = new UiLifecycleHelper(this, this);
		uiHelper.onCreate(savedInstanceState);
        
	    // create set up player for background music
	    StaticHelper.setUpPlayer(this);
	    
	    setUpUi();
	    setUpButtons();
	    setUpPlayButtonAnimation();

		// start tracking
		Sample.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.ZZZZZ"));
		Sample.setServerSide(false);
		Sample.setAppToken("wad-rt82-fhjk-18");
		AutoPing.getInstance().startAutoPing();
		tokenHandler = new Handler();
		tokenHandler.post(this);
	}
	
	@Override
	public void onResume() {
        super.onResume();
        uiHelper.onResume();	
        
        // get updated userCredentials
        userCredentials = new UserCredentials(getApplicationContext());
        
        // warning if no internet connection
		if (!StaticHelper.isOnline(this)) {
			Toast.makeText(this, getResources().getString(R.string.no_connection), Toast.LENGTH_LONG).show();
			userCredentials.clearAllCredentials();
			userCredentials = new UserCredentials(getApplicationContext());
			loggedIn = false;
			updateUi();
		} else {
			// facebook: if activity is launched and session not null -> trigger stateChange manually
			if (userCredentials.isFbUser()) {
				Session session = Session.getActiveSession();
				if (session != null && (session.isOpened() || session.isClosed())) {
					onSessionStateChange(session, session.getState(), null);
				}
				
			} else {
				handleLogin();
			}
		}
		
		// start background music, if its enabled
		StaticHelper.continueMusic = false;
		if (StaticHelper.soundOn && !StaticHelper.backgroundMusicPlayer.isPlaying()) {
			StaticHelper.backgroundMusicPlayer.start();
		}
    }
    @Override
    public void onPause() {
        super.onPause();
	    uiHelper.onPause();		
	    
	    if (!StaticHelper.continueMusic && StaticHelper.backgroundMusicPlayer.isPlaying()) {
	    	StaticHelper.backgroundMusicPlayer.pause();
	    }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
	    uiHelper.onDestroy();					
	    AutoPing.getInstance().stopAutoPing();	// tracking
    }

	// set up interface elements
    private void setUpUi() {
    	playBtn = (ImageButton) findViewById(R.id.loginButton);
	    accountmanagerBtn = (ImageButton) findViewById(R.id.accountmanagerButton);
	    selectGameBtn = (ImageButton) findViewById(R.id.chooseworldButton);
	    shopBtn = (ImageButton) findViewById(R.id.shopButton);
	    facebookBtn = (ImageButton) findViewById(R.id.facebookButton);
	    soundBtn = (ImageButton) findViewById(com.wackadoo.wackadoo_client.R.id.title_sound_button);
	    infoBtn = (ImageButton) findViewById(R.id.title_info_button);
	    characterFrame = (Button) findViewById(R.id.characterFrame);
	    
	    // set up standard server communication dialog
	    progressDialog = new CustomProgressDialog(this);
	    
		// test variable for attempt to connect to facebook
		tryConnect = false;
		
	    // sound is on by default
	    StaticHelper.soundOn = true;
	    
	    // user logged out by default
	    loggedIn = false;
	    
	    // put version number in textview
		try {
			String versionName = "Version " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			((TextView) findViewById(R.id.title_version_text)).setText(versionName);
			((TextView) findViewById(R.id.title_version_text)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf"));
		} 
		catch (NameNotFoundException e) {
			e.printStackTrace(); 
		} 
    }
    
    // start animation of play button
	private void setUpPlayButtonAnimation() {
		
		// start animation of glance
		playBtn.setImageResource(R.anim.animationlist_loginbutton);

		customHandler = new android.os.Handler();
		customHandler.postDelayed(updateTimerThread, 0);
	}
	
	// runs the scale animation repeatedly
	private Runnable updateTimerThread = new Runnable() {
		Animation scaleAnimation;
		
		public void run() {		
			if (scaleAnimation == null) scaleAnimation = AnimationUtils.loadAnimation(
					MainActivity.this, R.anim.scale_loginbutton);
			playBtn.startAnimation(scaleAnimation);
			customHandler.postDelayed(this, 6400);
		}
	};

	// start setup methods for each button in UI
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

	// set up touchlistener for facebook button
	private void setUpFacebookBtn() {
		facebookBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				switch (e.getAction()) {
				case MotionEvent.ACTION_DOWN:
					facebookBtn.setImageResource(R.drawable.title_facebook_button_active);
					break;

				case MotionEvent.ACTION_UP:
					facebookBtn.setImageResource(R.drawable.title_facebook_button);
					
					// fb user so pick friends
					if (userCredentials.isFbUser()) {

					// no fb user, so show connect dialog
					} else {
						if (loggedIn) {
							AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
							builder.setTitle(R.string.fb_connect_title)
								   .setMessage(R.string.fb_connect_message)
								   .setPositiveButton(R.string.alert_quit_yes, new OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											tryConnect = true;

											Session session = new Session(MainActivity.this);
											
											Session.OpenRequest openRequest = new Session.OpenRequest(MainActivity.this);
											openRequest.setPermissions(Arrays.asList("email"));
											openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
											session.openForRead(openRequest);
										}
								    })
								   .setNegativeButton(R.string.alert_quit_no, null);
							AlertDialog dialog = builder.create();
						    dialog.show();
						    StaticHelper.styleDialog(MainActivity.this, dialog);
							
						} else {
							Session session = new Session(MainActivity.this);
							Session.OpenRequest openRequest = new Session.OpenRequest(MainActivity.this);
							openRequest.setPermissions(Arrays.asList("email"));
							openRequest.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
							session.openForRead(openRequest);
						}
					}
					break;
				}
				return false;
			}
		});
	}

	// set up touchlistener for play button
	private void setUpPlayBtn() {
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
					setUpPlayButtonAnimation();
					if (loggedIn) {
						triggerPlayGame();
					} else {
						Toast.makeText(MainActivity.this, getResources().getString(R.string.login_necessary), Toast.LENGTH_SHORT)
							 .show();
					}
					break;
				}
				return true;
			}
		});
	}

	// set up touchlistener for shop button
	private void setUpShopBtn() {
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
					StaticHelper.continueMusic = true;
					Intent intent = new Intent(MainActivity.this, ShopActivity.class);
					startActivity(intent);
					break;
				}
				return true;
			}
		});
	}

	// set up touchlistener for sound button
	private void setUpSoundBtn() {
		soundBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (StaticHelper.soundOn) {
					soundBtn.setImageResource(R.drawable.title_sound_off);
					StaticHelper.backgroundMusicPlayer.stop();
					StaticHelper.soundOn = false;

				} else {
					soundBtn.setImageResource(R.drawable.title_sound_on);
				    StaticHelper.backgroundMusicPlayer = MediaPlayer.create(MainActivity.this, R.raw.themesong);
				    StaticHelper.backgroundMusicPlayer.setLooping(true);
				    StaticHelper.backgroundMusicPlayer.setVolume(100, 100);
					StaticHelper.backgroundMusicPlayer.start();
					StaticHelper.soundOn = true;
				}
			}
		});
	}

	// set up touchlistener for info button
	private void setUpInfoBtn() {
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
					StaticHelper.continueMusic = true;
					Intent intent = new Intent(MainActivity.this, InfoScreenActivity.class);
					startActivity(intent);
					break;
				}
				return true;
			}
		});
	}

	// set up touchlistener for accountmanager button
	private void setUpAccountmanagerBtn() {
		accountmanagerBtn.setVisibility(View.VISIBLE);
		accountmanagerBtn.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					accountmanagerBtn.setImageResource(R.drawable.title_change_button_active);
					break;

				case MotionEvent.ACTION_UP:
					accountmanagerBtn.setImageResource(R.drawable.title_change_button);
					StaticHelper.continueMusic = true;
					Intent intent = new Intent(MainActivity.this, AccountManagerActivity.class);
					startActivity(intent);
					break;
				}
				return true;
			}
		});
	}
	
	// set up touchlistener for selectgame button
	private void setUpSelectgameBtn() {
		selectGameBtn.setVisibility(View.VISIBLE);
		// TODO: is last world accessible?
		lastWorldAccessible = true;
		
		if (lastWorldAccessible) {
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
						if (lastWorldAccessible) {
							selectGameBtn.setImageResource(R.drawable.title_changegame_button_active);
						} else {
							selectGameBtn.setImageResource(R.drawable.title_changegame_warn_button_active);
						}
						break;
						
					case MotionEvent.ACTION_UP: 
						if (lastWorldAccessible) {
							selectGameBtn.setImageResource(R.drawable.title_changegame_button);
						} else {
							selectGameBtn.setImageResource(R.drawable.title_changegame_warn_button);
						}
						StaticHelper.continueMusic = true;
						Intent intent = new Intent(MainActivity.this, SelectGameActivity.class);
						startActivity(intent);
						break;
				}
				return true;
			}
		});
	}

	// set up touchlistener for characterframe
	private void setUpCharacterFrame() {
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
					Intent intent = null;
					if (loggedIn) {
						intent = new Intent(MainActivity.this, AccountManagerActivity.class);
					} else {
						intent = new Intent(MainActivity.this, CredentialScreenActivity.class);
					}
					StaticHelper.continueMusic = true;
					startActivity(intent);
					break;
				}
				return false;
			}
		});
	}
	
	// handle current credentials, decide how to login and start progress
	private void handleLogin() {
		String identifier = userCredentials.getIdentifier();		
		String accessToken = userCredentials.getAccessToken().getToken();
		String email = userCredentials.getEmail();
		final ImageView view = (ImageView) findViewById(R.id.characterFrameImageView);
		view.post(new Runnable() {
			  @Override public void run() {
			    view.setImageBitmap(Avatar.getAvatar(userCredentials.getAvatarString(), view.getWidth(), view.getHeight(), getResources()));
			  }
		});
		
		if (!identifier.equals("") && !accessToken.equals("") || !email.equals("")) { 
			if (userCredentials.getAccessToken().isExpired()) {
				progressDialog.show();
				new GameLoginAsyncTask(this, userCredentials, false, false).execute();
			} else {
				loggedIn = true;
				updateUi();
				if (userCredentials.getAvatarString() == null || userCredentials.getAvatarString() == "") {
					new GetCurrentGamesAsyncTask(this, userCredentials).execute();
					progressDialog.show();
				}
			}
			
		} else if (userCredentials.isEmailGenerated()) {
			progressDialog.show();
			new GameLoginAsyncTask(this, userCredentials, false, false).execute();
		} 
	}
	
    // facebook: handles result for login 
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }
    
    // facebook: login handling
    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        uiHelper.onSaveInstanceState(savedState);
    }
	
	// facebook: callback interface calls onSessionStateChange to handle session state
	@Override
	public void call(Session session, SessionState state, Exception exception) {
		onSessionStateChange(session, state, exception);	
	}
	
	// facebook: handles login/logout state of session
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if (session.isOpened()) {
			Log.d(TAG, "Facebook Session opened!");
			loggedIn = true;
			userCredentials.setFbUser(true);
			fetchFbData(session);
			
		} else if (state.isClosed()) {
			Log.d(TAG, "Facebook Session closed!");
			userCredentials.setFbUser(false);
			// password generated = attempt to connect fb account to local character failed = dont log out
			if (userCredentials.isPasswordGenerated()) {
				tryConnect = true;		// TODO: was = false - correct?
			} else {
				session.closeAndClearTokenInformation();	
				session.close();
				Session.setActiveSession(null);
			}
			updateUi();
		}
	}

	// facebook: start async request for user data
    private void fetchFbData(final Session session) {
        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
            	progressDialog.show();
                if (session == Session.getActiveSession()) {
                    if (user != null) {
                    	userCredentials.setFbPlayerId(user.getId());	
                    	userCredentials.setEmail(user.getProperty("email").toString());
                    	userCredentials.setFbAccessToken(session.getAccessToken());
                    }
                    
                    // try to connect character with facebook account
                    if (tryConnect) {
                    	new FacebookAccountAsyncTask(MainActivity.this, userCredentials, StaticHelper.FB_CONNECT_TASK).execute();
                   	// check if current facebook id is not used
                    } else {
                    	new FacebookAccountAsyncTask(MainActivity.this, userCredentials, StaticHelper.FB_ID_TASK).execute();
                    }
                }
                if (response.getError() != null) {
                	Log.d(TAG, "response error: " + response.getError().toString());
                }
            }
        });
        request.executeAsync();
    } 
    
    // handle click on playgame button
	private void triggerPlayGame() {
		String accessToken = userCredentials.getAccessToken().getToken();
		String tokenExpiration = userCredentials.getAccessToken().getExpireCode();
		String userId = userCredentials.getIdentifier();
		if (accessToken != null && tokenExpiration != null && !userCredentials.getAccessToken().isExpired()) {
			startGame(accessToken, tokenExpiration, userId);
		
		} else {
			progressDialog.show();
			new GameLoginAsyncTask(this, userCredentials, false, false).execute();
		}
	}

	// start webview activity to play game with necessary data
	private void startGame(String accessToken, String expiration, String userId) {
		Intent intent = new Intent(MainActivity.this, WackadooWebviewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("accessToken", accessToken);
		bundle.putString("expiration", expiration);
		bundle.putString("userId", userId);
		bundle.putString("hostname", userCredentials.getHostname());
		intent.putExtras(bundle);
		startActivity(intent);
	}

	// callback interface for GameLoginAsyncTask
	@Override
	public void loginCallback(boolean result, String accessToken, String expiration, String identifier, boolean restoreAccount, boolean refresh) {
		userCredentials.generateNewAccessToken(accessToken, expiration);
		userCredentials.setIdentifier(identifier);
		
		if (refresh) {
			return;
		}
		
		if (result) {
			loggedIn = true;
			new GetCurrentGamesAsyncTask(this, userCredentials).execute();	
			progressDialog.show();	
		} else {
			Toast.makeText(this, getResources().getString(R.string.login_failed_toast), Toast.LENGTH_LONG)
			     .show();
			updateUi();
		}
		// TODO: loggedIn if GetGames failed?
	}
	
	// callback interface for error in GameLoginAsyncTask
	@Override
	public void loginCallbackError(String error, boolean restoreAccount, boolean refresh) {
		loggedIn = false;
		updateUi();
	}
	
	// callback interface for GetCurrentGamesAsyncTask
	@Override
	public void getCurrentGamesCallback(ArrayList<GameInformation> games) {
		//TODO Remove lines before publishing
		//games.add(new GameInformation());
		//games.get(0).setDefaultGame(true);
		//games.get(0).setServer("https://192.168.178.94");
		if (userCredentials.getHostname() == "" || !isGameOnline(games, userCredentials.getGameId())) {
			for (int i = 0; i < games.size(); i++) {
				if (games.get(i).isDefaultGame()) {
					userCredentials.setHostname(games.get(i).getServer());
					userCredentials.setGameId(games.get(i).getId());
					new GetCharacterAsyncTask(this, userCredentials, games.get(i), true).execute();
				}
			}
		} else {
			ImageView view = (ImageView) findViewById(R.id.characterFrameImageView);
			view.setImageBitmap(Avatar.getAvatar(userCredentials.getAvatarString(), view.getWidth(), view.getHeight(), getResources()));
			Toast.makeText(this, getResources().getString(R.string.login_success_toast), Toast.LENGTH_LONG)
		     .show();
			updateUi();
		}
	}	
	
	// callback interface for GetCharacterAsyncTask
	@Override
	public void getCharacterCallback(GameInformation game, boolean createNew) {
		userCredentials.setUsername(game.getCharacter().getName());
		userCredentials.setPremiumExpiration(game.getCharacter().getPremiumExpiration());
		userCredentials.setAvatarString(game.getCharacter().getAvatarString());
		ImageView view = (ImageView) findViewById(R.id.characterFrameImageView);
		view.setImageBitmap(Avatar.getAvatar(game.getCharacter().getAvatarString(), view.getWidth(), view.getHeight(), getResources()));
		Toast.makeText(this, getResources().getString(R.string.login_success_toast), Toast.LENGTH_LONG)
	     .show();
		updateUi();
	}
	
	// check if a game in given list is online
	private boolean isGameOnline(ArrayList<GameInformation> games, int gameId) {
		boolean gameOnline = false;
		for (int i=0; i<games.size(); i++) {
			if (games.get(i).getId() == gameId) {
				gameOnline = true;
				break;
			}
		}
		return gameOnline;
	}

	// update views in UI depending on user logged in or not
	private void updateUi() {
		if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}

		if (loggedIn) {
			setUpAccountmanagerBtn();
			setUpSelectgameBtn();
			((RelativeLayout) findViewById(R.id.headerShopContainer)).setVisibility(View.VISIBLE);
			characterFrame.setText("");	
			((ImageView) findViewById(R.id.characterFrameImageView)).setVisibility(View.VISIBLE);
			facebookBtn.setVisibility(View.INVISIBLE);
			((TextView) findViewById(R.id.characterFrameTextview)).setText(userCredentials.getUsername());
			if (userCredentials.isFbUser()) {
				facebookBtn.setVisibility(View.INVISIBLE);
			} else {
				facebookBtn.setVisibility(View.VISIBLE);
			}
			
		} else {
			accountmanagerBtn.setVisibility(View.INVISIBLE);
			selectGameBtn.setVisibility(View.INVISIBLE);
			((RelativeLayout) findViewById(R.id.headerShopContainer)).setVisibility(View.INVISIBLE);
			characterFrame.setText(getResources().getString(R.string.credentials_headline));
			((ImageView) findViewById(R.id.characterFrameImageView)).setVisibility(View.INVISIBLE);
			facebookBtn.setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.characterFrameTextview)).setText("");
		}
	}

	// start automatic token refresh 
	@Override
	public void run() {
		if (userCredentials.getAccessToken().isExpired() && 
				(userCredentials.getUsername().length() > 0 || userCredentials.getEmail().length() > 0)) {
			new GameLoginAsyncTask(this, userCredentials, false, true).execute();
		}
		Log.d(TAG, "Token refresh handler");
		tokenHandler.postDelayed(this, 10000);		
	}

	// callback interface for FacebookAsyncTask
	@Override
	public void onFacebookTaskFinished(ResponseResult responseResult) {
		if (responseResult.isSuccess()) {
			String type = responseResult.getRequest();
			int responseCode = responseResult.getHttpStatusCode();
			
			// if facebook id was checked
			if (type.equals(StaticHelper.FB_ID_TASK)) {
				switch (responseCode) {
					case 200:
					case 304: 
						Log.d(TAG, "FACEBOOK ID - FOUND" + responseCode); 
						new FacebookLoginAsyncTask(this, userCredentials).execute();
						break;
						
					case 404:
						Log.d(TAG, "FACEBOOK ID - FREE TO USE" + responseCode);
						new FacebookLoginAsyncTask(this, userCredentials).execute();
						break;
						
					default: Log.d(TAG, "FACEBOOK ID - ERROR CODE: " + responseCode); progressDialog.dismiss(); break;
				}
				
			// if attempt to connect facebook account to character
			} else if (type.equals(StaticHelper.FB_CONNECT_TASK)) {
				tryConnect = false;
				if (responseCode == 200) {
					Log.d(TAG, "FACEBOOK CONNECT - SUCCESS" + responseCode);
					new FacebookLoginAsyncTask(this, userCredentials).execute();
					
				} else {
					progressDialog.dismiss();
					// close facebook session again, if connect accounts was not successful
					Session session = Session.getActiveSession();	
					if (session.isOpened()) {
						session.closeAndClearTokenInformation();	
						session.close();
						Session.setActiveSession(null);
					}
					userCredentials.setEmail("");
					switch (responseCode) {
						case 403: Toast.makeText(this, R.string.fb_character_already_connected, Toast.LENGTH_LONG).show(); break;
						case 409: Toast.makeText(this, R.string.fb_user_already_connected, Toast.LENGTH_LONG).show(); break;
						default: Toast.makeText(this, String.format(getString(R.string.fb_connect_error), responseCode), Toast.LENGTH_LONG).show(); break;
					}
				}
				
			// if facebook account was used to login
			} else if (type.equals(StaticHelper.FB_LOGIN_TASK)) {
				try {
					userCredentials = new UserCredentials(this);
					JSONObject jsonResponse = new JSONObject(responseResult.getMessage());
					userCredentials.generateNewAccessToken(jsonResponse.getString("access_token"), jsonResponse.getString("expires_in"));
					userCredentials.setIdentifier(jsonResponse.getString("user_identifer"));	
					loggedIn = true;
					new GetCurrentGamesAsyncTask(this, userCredentials).execute();	
					
				} catch (JSONException e) {
					Toast.makeText(this, getString(R.string.fb_task_failure), Toast.LENGTH_SHORT)
						 .show();
					e.printStackTrace();
				}
			}
			
		} else {
			Toast.makeText(this, getString(R.string.fb_task_failure), Toast.LENGTH_SHORT)
				 .show();
		}
	}

}