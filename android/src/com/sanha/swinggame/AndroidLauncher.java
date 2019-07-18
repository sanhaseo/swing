package com.sanha.swinggame;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.GameHelper;
import com.sanha.swinggame.tools.ActionResolver;

public class AndroidLauncher extends AndroidApplication implements ActionResolver{
    private final int SHOW_BANNER_AD = 0;
    private final int HIDE_BANNER_AD = 1;
	private static final String AD_UNIT_ID_BANNER = "ca-app-pub-5107342017593810/2102403798";
	private static final String AD_UNIT_ID_INTERSTITIAL = "ca-app-pub-5107342017593810/7510287921";

	private GameHelper gameHelper;
    private AdView bannerAdView;
    private InterstitialAd interstitialAd;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useWakelock = true;

		gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
		gameHelper.enableDebugLog(false);
		GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener() {
			@Override
			public void onSignInFailed() {

			}

			@Override
			public void onSignInSucceeded() {

			}
		};
		gameHelper.setup(gameHelperListener);

		//***
		//define layout params for:
		//1. relative layout
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT
		);
		//2. game view
		RelativeLayout.LayoutParams gameViewParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT
		);
		gameViewParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		gameViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		//3. banner ad view
		RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT
		);
		adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		adParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		//layout params
		//***

		//***
		//create relative layout
		RelativeLayout layout = new RelativeLayout(this);
		layout.setLayoutParams(params);
		//create game view
		View gameView = initializeForView(new SwingGame(this), config);
		gameView.setLayoutParams(gameViewParams);
		//create banner ad view
        bannerAdView = new AdView(this);
        bannerAdView.setAdSize(AdSize.SMART_BANNER);
        bannerAdView.setAdUnitId(AD_UNIT_ID_BANNER);
        bannerAdView.loadAd(new AdRequest.Builder().build());
        bannerAdView.setBackgroundColor(Color.TRANSPARENT);
        //create interstitial ad
		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId(AD_UNIT_ID_INTERSTITIAL);
		//***

		//add views to the layout
		layout.addView(gameView);
		layout.addView(bannerAdView, adParams);
		setContentView(layout);

		MobileAds.initialize(this, "ca-app-pub-5107342017593810~9505239330");
	}

	@Override
	protected void onStart() {
		super.onStart();
		gameHelper.onStart(this);
	}
	@Override
	protected void onStop() {
		super.onStop();
		gameHelper.onStop();
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		gameHelper.onActivityResult(requestCode, resultCode, data);
	}

	//***
	//Google Play Games Services API
	@Override
	public void signIn() {
		try {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					gameHelper.beginUserInitiatedSignIn();
				}
			});
		} catch (Exception e) {
		}
	}
	@Override
	public void submitScore(int highScore) {
		if (isSignedIn()) {
			Games.Leaderboards.submitScore(
					gameHelper.getApiClient(),
					getString(R.string.leaderboard_highscores),
					highScore
			);
		}
	}
	@Override
	public void unlockAchievement(String achievementId) {
		Games.Achievements.unlock(gameHelper.getApiClient(), achievementId);
	}
	@Override
	public void getLeaderboard() {
		if (isSignedIn()) {
			startActivityForResult(
					Games.Leaderboards.getLeaderboardIntent(gameHelper.getApiClient(),
					getString(R.string.leaderboard_highscores)),
					1
			);
		}
		else {
			signIn();
		}
	}
	@Override
	public void getAchievements() {
		if (isSignedIn()) {
			startActivityForResult(
					Games.Achievements.getAchievementsIntent(gameHelper.getApiClient()),
					1
			);
		}
		else {
			signIn();
		}
	}
	@Override
	public boolean isSignedIn() {
		return gameHelper.isSignedIn();
	}
	//Google Play Games Services API
	//***

    //***
    //Ad handler
    @Override
    public void showBannerAd() {
        handler.sendEmptyMessage(SHOW_BANNER_AD);
    }
    @Override
    public void hideBannerAd() {
        handler.sendEmptyMessage(HIDE_BANNER_AD);
    }
	@Override
	public void loadInterstitialAd() {
		try {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (!interstitialAd.isLoaded()) {
						interstitialAd.loadAd(new AdRequest.Builder().build());
					}
				}
			});
		} catch (Exception e) {
		}
	}
	@Override
	public void showInterstitialAd() {
		try {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (interstitialAd.isLoaded()) {
						interstitialAd.show();
					}
					else {
						interstitialAd.loadAd(new AdRequest.Builder().build());
					}
				}
			});
		} catch (Exception e) {
		}
	}
    //Ad handler
    //***

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_BANNER_AD:
                    bannerAdView.setVisibility(View.VISIBLE);
                    break;
                case HIDE_BANNER_AD:
                    bannerAdView.setVisibility(View.GONE);
                    break;
            }
        }
    };
}
