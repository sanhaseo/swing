package com.sanha.swinggame.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.sanha.swinggame.SwingGame;
import com.sanha.swinggame.tools.ActionResolver;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Swing Game";
		config.width = (int) SwingGame.WIDTH;
		config.height = (int) SwingGame.HEIGHT;

		ActionResolver actionResolver = new ActionResolver() {
			@Override
			public void signIn() {}
			@Override
			public void submitScore(int highScore) {}
			@Override
			public void unlockAchievement(String achievementId) {}
			@Override
			public void getLeaderboard() {}
			@Override
			public void getAchievements() {}
			@Override
			public boolean isSignedIn() {
				return false;
			}
			@Override
			public void showBannerAd() {}
			@Override
			public void hideBannerAd() {}
			@Override
			public void loadInterstitialAd() {}
			@Override
			public void showInterstitialAd() {}
        };

		new LwjglApplication(new SwingGame(actionResolver), config);
	}
}
