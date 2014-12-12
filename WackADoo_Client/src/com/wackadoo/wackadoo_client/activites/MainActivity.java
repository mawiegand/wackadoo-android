package com.wackadoo.wackadoo_client.activites;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.adjust.sdk.Adjust;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.analytics.SampleHelper;
import com.wackadoo.wackadoo_client.helper.Avatar;
import com.wackadoo.wackadoo_client.helper.CustomProgressDialog;
import com.wackadoo.wackadoo_client.helper.SoundManager;
import com.wackadoo.wackadoo_client.helper.StaticHelper;
import com.wackadoo.wackadoo_client.interfaces.CharacterCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.CurrentGamesCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.FacebookTaskCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.GameLoginCallbackInterface;
import com.wackadoo.wackadoo_client.model.AdjustProperties;
import com.wackadoo.wackadoo_client.model.GameInformation;
import com.wackadoo.wackadoo_client.model.ResponseResult;
import com.wackadoo.wackadoo_client.model.UserCredentials;
import com.wackadoo.wackadoo_client.tasks.FacebookAccountAsyncTask;
import com.wackadoo.wackadoo_client.tasks.FacebookLoginAsyncTask;
import com.wackadoo.wackadoo_client.tasks.GameLoginAsyncTask;
import com.wackadoo.wackadoo_client.tasks.GetCharacterAsyncTask;
import com.wackadoo.wackadoo_client.tasks.GetCurrentGamesAsyncTask;

public class MainActivity extends Activity implements GameLoginCallbackInterface, FacebookTaskCallbackInterface, 
		CurrentGamesCallbackInterface, CharacterCallbackInterface, StatusCallback {

	private static final String TAG = MainActivity.class.getSimpleName();
	private static final int UPDATE_TOKEN_TIMER = 10000;
	
	private ImageButton playBtn, accountmanagerBtn, selectGameBtn, facebookBtn, shopBtn, soundBtn, infoBtn;
	private Button characterFrame;
	private boolean lastWorldAccessible, loggedIn, tryConnect;
	private UserCredentials userCredentials;
	private CustomProgressDialog progressDialog;
	private Handler mTokenHandler;
	private UiLifecycleHelper uiHelper;		
	private SoundManager soundManager;
	
	private SampleHelper sample;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //facebook lifecycleHelper to keep track of the session
	    uiHelper = new UiLifecycleHelper(this, this);
		uiHelper.onCreate(savedInstanceState);
        
	    // create and set up player for background music
		soundManager = SoundManager.getInstance(this);
	    
	    setUpUi();
	    setUpButtons();
	   
	    // set up handlers
		mTokenHandler = new android.os.Handler();
	
		// Setup Tracking
		sample = SampleHelper.getInstance(getApplicationContext());
		registerComponentCallbacks(sample);
		sample.setServerSide(false);
		sample.setAppToken("wad-rt82-fhjk-18");
	}
	
	@Override
	public void onResume() {
        super.onResume();
        uiHelper.onResume();
        sample = SampleHelper.getInstance(getApplicationContext());
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(sample, filter);
        
        // adjust tracking
        Adjust.onResume(this);
        
        // get updated userCredentials
        userCredentials = new UserCredentials(getApplicationContext());
        
        String userId = userCredentials.getIdentifier();
        
        sample = SampleHelper.getInstance(getApplicationContext());
        if (sample.isTrackingStoped()) {
    		sample.startTracking();
    		sample.setUserId(userId);
    		sample.setFacebookId(userCredentials.getFbPlayerId());
    		sample.track("session_start", "session");
        	sample.startAutoPing();
        	
			if (userId.isEmpty() == false) {
				AdjustProperties adjustProps = AdjustProperties
						.getInstance(getApplicationContext());
				int logins = adjustProps.loginsForUser(userId);
				if (logins < 2) {
					adjustProps.incrementLoginCountForUser(userId);
					if (adjustProps.loginsForUser(userId) >= 2) {
						Adjust.trackEvent("ikc6km");
					}
				}
			}
        }
        
        // warning if no internet connection
		if (!StaticHelper.isOnline(this)) {
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
		soundManager.setContinueMusic(false);
		if (soundManager.isSoundOn() && !soundManager.isPlaying()) {
			soundManager.start();
		}
		
		// restart threads
		mTokenHandler.postDelayed(updateTokenThread, UPDATE_TOKEN_TIMER);
    }
	
    @Override
    public void onPause() {
        super.onPause();
	    uiHelper.onPause();		
	    Adjust.onPause();
	   
	    unregisterReceiver(sample);
	    mTokenHandler.removeCallbacks(updateTokenThread);
	    
	    if (!soundManager.isContinueMusic() && soundManager.isPlaying()) {
	    	soundManager.pause();
	    }
	    
	    if (progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
	    uiHelper.onDestroy();	
	    
	    // PSIORI Stops tracking to free ressources
	    // Stops the autoPing timer as well
	    sample.stopTracking();	
    }

	// set up interface elements
    private void setUpUi() {
    	playBtn = (ImageButton) findViewById(R.id.loginButton);
	    accountmanagerBtn = (ImageButton) findViewById(R.id.accountmanagerButton);
	    selectGameBtn = (ImageButton) findViewById(R.id.chooseworldButton);
	    shopBtn = (ImageButton) findViewById(R.id.shopButton);
	    facebookBtn = (ImageButton) findViewById(R.id.facebookButton);
	    soundBtn = (ImageButton) findViewById(R.id.title_sound_button);
	    infoBtn = (ImageButton) findViewById(R.id.title_info_button);
	    characterFrame = (Button) findViewById(R.id.characterFrame);
	    
	    // set up standard server communication dialog
	    progressDialog = new CustomProgressDialog(this);
	    
		// test variable for attempt to connect to facebook
		tryConnect = false;
		
	    // sound is on by default
		soundManager.setSoundOn(true);
	    
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
    
	//  thread for automatic token refresh
	private Runnable updateTokenThread = new Runnable() {
		public void run() {	
			if (userCredentials.getAccessToken().isExpired() && 
					(userCredentials.getUsername().length() > 0 || userCredentials.getEmail().length() > 0)) {
				new GameLoginAsyncTask(MainActivity.this, userCredentials, false, true).execute();
			}
			mTokenHandler.postDelayed(this, UPDATE_TOKEN_TIMER);
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
		OnTouchListener touchListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				switch (e.getAction()) {
				case MotionEvent.ACTION_DOWN:
					facebookBtn.setImageResource(R.drawable.btn_facebook_active);
					break;

				case MotionEvent.ACTION_UP:
					facebookBtn.setImageResource(R.drawable.btn_facebook);
					
					// warning if no internet connection
					if (!StaticHelper.isOnline(MainActivity.this)) {
						Toast.makeText(MainActivity.this, getResources().getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
					} else {
						if (loggedIn) {
							AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
							builder.setTitle(R.string.fb_connect_title)
								   .setMessage(R.string.fb_connect_message)
								   .setPositiveButton(R.string.alert_quit_yes, new OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											tryConnect = true;
											
											Session session = Session.getActiveSession();
											if (session == null) { 
												session = new Session(MainActivity.this);
												Session.setActiveSession(session);
											}
											
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
							Session session = Session.getActiveSession();
							if (session == null) { 
								session = new Session(MainActivity.this);
								Session.setActiveSession(session);
							}
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
		};
		// make clickable area bigger by enabling click on outer container
		facebookBtn.setOnTouchListener(touchListener);
		((RelativeLayout) findViewById(R.id.facebookButtonContainer)).setOnTouchListener(touchListener);
	}

	// set up touchlistener for play button
	private void setUpPlayBtn() {
		// start animation of glance
		playBtn.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					playBtn.setImageResource(R.drawable.btn_play_active);
					break;

				case MotionEvent.ACTION_UP:
					playBtn.setImageResource(R.drawable.btn_play);
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
		OnTouchListener touchListener = new View.OnTouchListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					shopBtn.setImageResource(R.drawable.btn_shop_active);
					break;

				case MotionEvent.ACTION_UP:
					shopBtn.setImageResource(R.drawable.btn_shop);
					soundManager.setContinueMusic(true);
					Intent intent = new Intent(MainActivity.this, ShopActivity.class);
					startActivity(intent);
					break;
				}
				return true;
			}
		};
		// make clickable area bigger by enabling click on outer container
		shopBtn.setOnTouchListener(touchListener);
		((RelativeLayout) findViewById(R.id.headerShopContainer)).setOnTouchListener(touchListener);
	}

	// set up touchlistener for sound button
	private void setUpSoundBtn() {
		soundBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (soundManager.isSoundOn()) {
					soundBtn.setImageResource(R.drawable.ic_sound_off);
					soundManager.setSoundOn(false);
					soundManager.pause();

				} else {
					soundBtn.setImageResource(R.drawable.ic_sound_on);
					soundManager.setSoundOn(true);
					soundManager.start();
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
					infoBtn.setImageResource(R.drawable.btn_info_active);
					break;

				case MotionEvent.ACTION_UP:
					infoBtn.setImageResource(R.drawable.btn_info);
					soundManager.setContinueMusic(true);
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
		OnTouchListener touchListener = new View.OnTouchListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					accountmanagerBtn.setImageResource(R.drawable.btn_change_active);
					break;

				case MotionEvent.ACTION_UP:
					accountmanagerBtn.setImageResource(R.drawable.btn_change);
					soundManager.setContinueMusic(true);
					Intent intent = new Intent(MainActivity.this, AccountManagerActivity.class);
					startActivity(intent);
					break;
				}
				return true;
			}
		};
		// make clickable area bigger by enabling click on outer container
		accountmanagerBtn.setOnTouchListener(touchListener);
		((RelativeLayout) findViewById(R.id.accountmanagerButtonVerticalContainer)).setOnTouchListener(touchListener);
	}
	
	// set up touchlistener for selectgame button
	private void setUpSelectgameBtn() {
		selectGameBtn.setVisibility(View.VISIBLE);
		lastWorldAccessible = true;		// TODO: is last world accessible?
		
		if (lastWorldAccessible) {
			selectGameBtn.setImageResource(R.drawable.btn_changegame);
		} else {
			selectGameBtn.setImageResource(R.drawable.btn_changegame_warn);
		}
		
		OnTouchListener touchListener = new View.OnTouchListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN: 
						if (lastWorldAccessible) {
							selectGameBtn.setImageResource(R.drawable.btn_changegame_active);
						} else {
							selectGameBtn.setImageResource(R.drawable.btn_changegame_warn_active);
						}
						break;
						
					case MotionEvent.ACTION_UP: 
						if (lastWorldAccessible) {
							selectGameBtn.setImageResource(R.drawable.btn_changegame);
						} else {
							selectGameBtn.setImageResource(R.drawable.btn_changegame_warn);
						}
						soundManager.setContinueMusic(true);
						Intent intent = new Intent(MainActivity.this, SelectGameActivity.class);
						startActivity(intent);
						break;
				}
				return true;
			}
		};
		// make clickable area bigger by enabling click on outer container
		selectGameBtn.setOnTouchListener(touchListener);
		((RelativeLayout) findViewById(R.id.chooseworldButtonVerticalContainer)).setOnTouchListener(touchListener);
	}

	// set up touchlistener for characterframe
	private void setUpCharacterFrame() {
		characterFrame.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: 
					characterFrame.setBackgroundResource(R.drawable.btn_character_frame_active);
					break;
					
				case MotionEvent.ACTION_UP: 
					characterFrame.setBackgroundResource(R.drawable.btn_character_frame);
					Intent intent = null;
					if (loggedIn) {
						intent = new Intent(MainActivity.this, AccountManagerActivity.class);
					} else {
						intent = new Intent(MainActivity.this, CredentialScreenActivity.class);
					}
					soundManager.setContinueMusic(true);
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
		String email = userCredentials.getEmail();
		final ImageView view = (ImageView) findViewById(R.id.characterFrameImageView);
		view.post(new Runnable() {
			  @Override public void run() {
			    view.setImageBitmap(Avatar.getAvatar(userCredentials.getAvatarString(), view.getWidth(), view.getHeight(), getResources()));
			  }
		});
		
		// just portable account, we don't want a second task for fb users (their GamesTask is started in onSessionStateChange)
		if (!identifier.equals("") || !email.equals("") && !userCredentials.isFbUser()) { 
			if (userCredentials.getAccessToken().isExpired()) {
				progressDialog.show();
				new GameLoginAsyncTask(this, userCredentials, false, false).execute();
			} else {
				loggedIn = true;
				
				// PSIORI track sign_in
				SampleHelper helper = SampleHelper.getInstance(getApplicationContext());
				helper.setFacebookId(userCredentials.getFbPlayerId());
				helper.setUserId(identifier);
				helper.track("sign_in", "account", null);
				
				updateUi();
				if (userCredentials.getAvatarString() == null || userCredentials.getAvatarString() == "") {
					new GetCurrentGamesAsyncTask(this, userCredentials).execute();
					progressDialog.show();
				}
			}
			
		// local account
		} else if (!userCredentials.getUsername().equals("")) {
			progressDialog.show();
			new GameLoginAsyncTask(this, userCredentials, false, false).execute();
			
		} 
		else {
			loggedIn = false;
			updateUi();
		}
	}
	
    // facebook: handles result for login 
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
        
        // play store dialog stops background music, so prepare to start again
     	soundManager.prepare();
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
		if (state.isOpened()) {
			loggedIn = true;
			userCredentials.setFbUser(true);
			
			// get new accesstoken if expired or attempt to connect was made
			if (userCredentials.getAccessToken().isExpired() || tryConnect) {
				fetchFbData(session);
			
			// get games and character if there not already on device
			} else if (userCredentials.getAvatarString() == null || userCredentials.getAvatarString() == "") {
				new GetCurrentGamesAsyncTask(this, userCredentials).execute();
				progressDialog.show();
				
			// set character picture to imageview
			} else {
				final ImageView view = (ImageView) findViewById(R.id.characterFrameImageView);
				view.postDelayed(new Runnable() {
					@Override public void run() {
						if (view.getWidth() == 0 || view.getHeight() == 0) return;
						view.setImageBitmap(Avatar.getAvatar(
								userCredentials.getAvatarString(), 
								view.getWidth(), 
								view.getHeight(), 
								getResources()));
					}
				}, 50);
			}
			
		} else if (state.isClosed()) {
			if (session != null) {
				session.closeAndClearTokenInformation();	
				session.close();
				Session.setActiveSession(null);
			}
			userCredentials.setFbUser(false);
			// attempt to connect fb account to local character failed = dont log out
			if (tryConnect = true) {
				tryConnect = false;		
			} else {
				session.closeAndClearTokenInformation();	
				session.close();
				Session.setActiveSession(null);
			}
		}
		updateUi();
	}

	// facebook: start async request for user data
    private void fetchFbData(final Session session) {
        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {
            @Override
            public void onCompleted(GraphUser user, Response response) {
            	progressDialog.show();
                if (session == Session.getActiveSession()) {
                    if (user != null) {
                    	try {
	                    	userCredentials.setEmail(user.getProperty("email").toString());
	                    	userCredentials.setFbPlayerId(user.getId());	
	                    	userCredentials.getAccessToken().setFbToken(session.getAccessToken());
                    	} catch (Exception e) {
                    		if (progressDialog.isShowing()) {
                    			progressDialog.dismiss();
                    		}
                    		Toast.makeText(MainActivity.this, getString(R.string.error_server_communication), Toast.LENGTH_SHORT)
                    			 .show();
                    		return;
                    	}
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
                	if (StaticHelper.debugEnabled) {
                		Log.d(TAG, "facebook fetchData error: " + response.getError().toString());
                	}
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

	// start webview activity with necessary data
	private void startGame(String accessToken, String expiration, String userId) {
		soundManager.setContinueMusic(true);
		
		Intent intent = new Intent(MainActivity.this, WebviewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("accessToken", accessToken);
		bundle.putString("expiration", expiration);
		bundle.putString("userId", userId);
		bundle.putString("hostname", userCredentials.getHtmlHost());
		bundle.putString("uniqueTrackingToken", userCredentials.getClientCredentials().getDeviceInformation().getUniqueTrackingToken());
		intent.putExtras(bundle);
		
		startActivity(intent);
		finish();
		
		// PSIORI track enter game
		SampleHelper sHelper = SampleHelper.getInstance(getApplicationContext());
		sHelper.setModule(String.valueOf(userCredentials.getGameId()));
		sHelper.setUserId(userId);
		sHelper.setFacebookId(userCredentials.getFbPlayerId());
		sHelper.track("session_update", "session", null);
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
			
			if (!progressDialog.isShowing()) {
				progressDialog.show();	
			}
			
			// sample tracking
			SampleHelper helper = SampleHelper.getInstance(getApplicationContext());
			String fbId = userCredentials.getFbPlayerId();
			helper.setFacebookId(fbId);
			helper.setUserId(identifier);
			helper.track("sign_in", "account", null);
			
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
		if (error.equals("invalid_grant")) {
			Toast.makeText(this, getResources().getString(R.string.login_invalid_grant), Toast.LENGTH_LONG)
				 .show();
		} else {
			Toast.makeText(this, getResources().getString(R.string.login_failed_toast), Toast.LENGTH_LONG)
				 .show();
		}
		loggedIn = false;
		updateUi();
	}
	
	// callback interface for GetCurrentGamesAsyncTask
	@Override
	public void getCurrentGamesCallback(boolean result, ArrayList<GameInformation> games) {
		if (StaticHelper.localTest) {
			games = new ArrayList<GameInformation>();
			games.add(new GameInformation());
			games.get(0).setDefaultGame(true);
			games.get(0).setGameHost(getString(R.string.localTestPath));
			games.get(0).setHtmlHost(getString(R.string.localTestPath));
			userCredentials.setHtmlHost(games.get(0).getHtmlHost());
			userCredentials.setGameHost(games.get(0).getGameHost());
			userCredentials.setGameId(games.get(0).getId());
			new GetCharacterAsyncTask(this, userCredentials, games.get(0), true).execute();
		}
		
		if (result) {
			if (userCredentials.getGameHost() == "" || !isGameOnline(games, userCredentials.getGameId())) {
				for (int i = 0; i < games.size(); i++) {
					if (games.get(i).isDefaultGame()) {
						userCredentials.setGameHost(games.get(i).getGameHost());
						userCredentials.setHtmlHost(games.get(i).getHtmlHost());
						userCredentials.setGameId(games.get(i).getId());
						new GetCharacterAsyncTask(this, userCredentials, games.get(i), true).execute();
					}
				}
			} else {
				ImageView view = (ImageView) findViewById(R.id.characterFrameImageView);
				view.setImageBitmap(Avatar.getAvatar(userCredentials.getAvatarString(), view.getWidth(), view.getHeight(), getResources()));
				Toast.makeText(this, getResources().getString(R.string.login_success_toast), Toast.LENGTH_SHORT)
					 .show();
				updateUi();
			}
		} else {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			Toast.makeText(this, getResources().getString(R.string.error_server_communication), Toast.LENGTH_SHORT)
				 .show();
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
		Toast.makeText(this, getResources().getString(R.string.login_success_toast), Toast.LENGTH_SHORT)
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
						if (StaticHelper.debugEnabled) {
							Log.d(TAG, "FACEBOOK ID - FOUND" + responseCode); 
						}
						new FacebookLoginAsyncTask(this, userCredentials).execute();
						break;
						
					case 404:
						if (StaticHelper.debugEnabled) {
							Log.d(TAG, "FACEBOOK ID - FREE TO USE" + responseCode);
						}
						new FacebookLoginAsyncTask(this, userCredentials).execute();
						break;
						
					default: 
						if (StaticHelper.debugEnabled) {
							Log.d(TAG, "FACEBOOK ID - ERROR CODE: " + responseCode); progressDialog.dismiss();
							break;
						}
				}
				
			// if attempt to connect facebook account to character
			} else if (type.equals(StaticHelper.FB_CONNECT_TASK)) {
				tryConnect = false;
				if (responseCode == 200) {
					if (StaticHelper.debugEnabled) {
						Log.d(TAG, "FACEBOOK CONNECT - SUCCESS" + responseCode);
					}
					new FacebookLoginAsyncTask(this, userCredentials).execute();
					
				} else {
					if (progressDialog.isShowing()) {
						progressDialog.dismiss();
					}
					// close facebook session again, if connect accounts was not successful
					Session session = Session.getActiveSession();	
					if (session != null) {
						if (session.isOpened()) {
							session.closeAndClearTokenInformation();	
							session.close();
							Session.setActiveSession(null);
						}
					}
					// set credentials to local account again, because fb account couldn't be connected
					userCredentials.setEmail("");
					userCredentials.setFbUser(false);
					userCredentials.setGeneratedEmail(true);
					userCredentials.setGeneratedPassword(true);
					
					switch (responseCode) {
						case 403: Toast.makeText(this, R.string.fb_character_already_connected, Toast.LENGTH_LONG).show(); break;
						case 409: Toast.makeText(this, R.string.fb_user_already_connected, Toast.LENGTH_LONG).show(); break;
						default: 
							Toast.makeText(this, String.format(getString(R.string.fb_connect_error), responseCode), Toast.LENGTH_LONG)
								 .show(); 
							break;
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
					
					String userId = userCredentials.getIdentifier();
					String fbId = userCredentials.getFbPlayerId();

					// PSIORI track sign_in
					SampleHelper helper = SampleHelper.getInstance(getApplicationContext());
					helper.setFacebookId(fbId);
					helper.setUserId(userId);
					helper.track("sign_in", "account", null);
					
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
			// close possible facebook session
			Session session = Session.getActiveSession();
			if (session != null) {
				session.closeAndClearTokenInformation();	
				session.close();
				Session.setActiveSession(null);
			}
		}
	}

}
