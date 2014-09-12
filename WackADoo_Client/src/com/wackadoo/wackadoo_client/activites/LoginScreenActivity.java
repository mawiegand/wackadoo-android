package com.wackadoo.wackadoo_client.activites;

import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.Toast;

import com.fivedlab.sample.sample_java.Sample;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wackadoo.wackadoo_client.R;
import com.wackadoo.wackadoo_client.analytics.AutoPing;
import com.wackadoo.wackadoo_client.interfaces.CreateAccountCallbackInterface;
import com.wackadoo.wackadoo_client.interfaces.GameLoginCallbackInterface;
import com.wackadoo.wackadoo_client.model.ClientCredentials;
import com.wackadoo.wackadoo_client.model.DeviceInformation;
import com.wackadoo.wackadoo_client.model.UserCredentials;
import com.wackadoo.wackadoo_client.tasks.GameLoginAsyncTask;

public class LoginScreenActivity extends Activity implements
		CreateAccountCallbackInterface, GameLoginCallbackInterface {

	private static final String TAG = LoginScreenActivity.class.getSimpleName();

	private ImageButton playBtn, accountmanagerBtn, chooseworldBtn,
			facebookBtn, shopBtn, soundBtn, infoBtn, characterFrame;
	private boolean soundOn, lastWorldAccessible;
	private AnimationDrawable playButtonAnimation;
	private UserCredentials userCredentials;
	private Handler customHandler;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loginscreen);

		playBtn = (ImageButton) findViewById(R.id.loginButton);
		accountmanagerBtn = (ImageButton) findViewById(R.id.accountmanagerButton);
		chooseworldBtn = (ImageButton) findViewById(R.id.chooseworldButton);
		shopBtn = (ImageButton) findViewById(R.id.shopButton);
		facebookBtn = (ImageButton) findViewById(R.id.facebookButton);
		soundBtn = (ImageButton) findViewById(com.wackadoo.wackadoo_client.R.id.title_sound_button);
		infoBtn = (ImageButton) findViewById(R.id.title_info_button);
		characterFrame = (ImageButton) findViewById(R.id.characterFrame);

		userCredentials = new UserCredentials(this.getApplicationContext());

		// sound is off by default
		soundOn = false;

		// Start tracking
		Sample.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.ZZZZZ"));
		Sample.setServerSide(false);
		Sample.setAppToken("wad-rt82-fhjk-18");
		AutoPing.getInstance().startAutoPing();

		setUpButtons();
		setUpPlayButtonAnimation();
		triggerLogin();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		AutoPing.getInstance().stopAutoPing();
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
			customHandler.postDelayed(this, 4000);
		}
	};

	private void setUpButtons() {
		setUpPlayBtn();
		setUpShopBtn();
		setUpAccountmanagerBtn();
		setUpChooseworldBtn();
		setUpFacebookBtn();
		setUpSoundBtn();
		setUpInfoBtn();
		setUpCharacterFrame();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.userCredentials = new UserCredentials(getApplicationContext());
		playBtn.setEnabled(true);
		shopBtn.setEnabled(true);
		infoBtn.setEnabled(true);
		// playButtonAnimation.start();
		// TODO: If(user not logged in && credentials available)
		// -> Login
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setUpFacebookBtn() {
		facebookBtn.setEnabled(true);
		facebookBtn.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				switch (e.getAction()) {
				case MotionEvent.ACTION_DOWN:
					facebookBtn
							.setImageResource(R.drawable.title_facebook_button_active);
					break;

				case MotionEvent.ACTION_UP:
					facebookBtn
							.setImageResource(R.drawable.title_facebook_button);
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
				// playBtn.setEnabled(false);
				// Intent intent = new Intent(LoginScreenActivity.this,
				// WackadooWebviewActivity.class);
				// startActivity(intent);
				triggerPlayGame();
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
				Intent intent = new Intent(LoginScreenActivity.this,
						ShopActivity.class);
				startActivity(intent);
				shopBtn.setEnabled(false);
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
				Intent intent = new Intent(LoginScreenActivity.this,
						InfoScreenActivity.class);
				startActivity(intent);
			}
		});
	}

	private void setUpAccountmanagerBtn() {
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
				Intent intent = new Intent(LoginScreenActivity.this,
						AccountManagerActivity.class);
				startActivity(intent);
			}
		});
	}

	private void setUpChooseworldBtn() {
		chooseworldBtn.setEnabled(true);

		// TODO: is last world accessible?
		lastWorldAccessible = true;

		if (lastWorldAccessible) {
			chooseworldBtn.setImageResource(R.drawable.title_changegame_button);
		} else {
			chooseworldBtn
					.setImageResource(R.drawable.title_changegame_warn_button);
		}

		chooseworldBtn.setOnTouchListener(new View.OnTouchListener() {
			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (lastWorldAccessible) {
						chooseworldBtn
								.setImageResource(R.drawable.title_changegame_button_active);
					} else {
						chooseworldBtn
								.setImageResource(R.drawable.title_changegame_warn_button_active);
					}
					break;

				case MotionEvent.ACTION_UP:
					if (lastWorldAccessible) {
						chooseworldBtn
								.setImageResource(R.drawable.title_changegame_button);
					} else {
						chooseworldBtn
								.setImageResource(R.drawable.title_changegame_warn_button);
					}
					break;
				}
				return false;
			}
		});

		chooseworldBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginScreenActivity.this,
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
					characterFrame
							.setImageResource(R.drawable.title_character_frame_active);
					break;

				case MotionEvent.ACTION_UP:
					characterFrame
							.setImageResource(R.drawable.title_character_frame);
					break;
				}
				return false;
			}
		});

		characterFrame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginScreenActivity.this,
						CredentialScreenActivity.class);
				startActivity(intent);
				infoBtn.setEnabled(false);
			}
		});

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
		if (identifier.length() > 0 || accessToken.length() > 0
				|| email.length() > 0) {
			if (userCredentials.getAccessToken().isExpired()) {
				new GameLoginAsyncTask(this, getApplicationContext(),
						this.userCredentials).execute();
			}
		} else {
			Toast.makeText(getApplicationContext(),
					getResources().getString(R.string.login_failed_toast),
					Toast.LENGTH_LONG).show();
		}
	}

	private void triggerPlayGame() {
		String accessToken = this.userCredentials.getAccessToken().getToken();
		String tokenExpiration = this.userCredentials.getAccessToken()
				.getExpireCode();
		String userId = this.userCredentials.getClientID();
		if (accessToken != null && tokenExpiration != null
				&& !this.userCredentials.getAccessToken().isExpired()) {
			Log.d(TAG, "START GAME CALLED!");
			startGame(accessToken, tokenExpiration, userId);
		} else {
			Log.d(TAG, "GAMELOGIN ASYNCTASK!");
			new GameLoginAsyncTask(this, getApplicationContext(),
					userCredentials).execute();
		}
	}

	private void startGame(String accessToken, String expiration, String userId) {
		Intent intent = new Intent(LoginScreenActivity.this,
				WackadooWebviewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("accessToken", accessToken);
		bundle.putString("expiration", expiration);
		bundle.putString("userId", userId);
		intent.putExtras(bundle);
		startActivity(intent);
	}

	@Override
	public void onRegistrationCompleted(String identifier, String clientID,
			String nickname) {
		userCredentials.setIdentifier(identifier);
		userCredentials.setClientID(clientID);
		userCredentials.setUsername(nickname);
		this.triggerLogin();
	}

	@Override
	public void loginCallback(String accessToken, String expiration) {
		this.userCredentials.generateNewAccessToken(accessToken, expiration);
		Toast.makeText(getApplicationContext(),
				getResources().getString(R.string.login_success_toast),
				Toast.LENGTH_LONG).show();
	}
}