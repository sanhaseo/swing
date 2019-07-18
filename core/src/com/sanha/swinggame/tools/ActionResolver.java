package com.sanha.swinggame.tools;

public interface ActionResolver {
    //Google Play Games Services API
    void signIn();
    void submitScore(int highScore);
    void unlockAchievement(String achievementId);
    void getLeaderboard();
    void getAchievements();
    boolean isSignedIn();

    //Ad handler
    void showBannerAd();
    void hideBannerAd();
    void loadInterstitialAd();
    void showInterstitialAd();
    //void showOrLoadInterstitialAd();
}
