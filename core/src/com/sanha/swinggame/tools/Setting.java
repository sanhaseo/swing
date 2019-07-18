package com.sanha.swinggame.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class Setting {
    public static int gamesPlayed;
    public static boolean musicOn;
    public static boolean soundOn;
    public static int highScore;
    public static int gemCount;
    public static int characterId;

    //characters unlocked
    private static boolean unlocked0;
    private static boolean unlocked1;
    private static boolean unlocked2;
    private static boolean unlocked3;
    private static boolean unlocked4;
    private static boolean unlocked5;
    private static boolean unlocked6;
    private static boolean unlocked7;
    private static boolean unlocked8;
    private static boolean unlocked9;

    private static Preferences preferences;

    public static void load() {
        preferences = Gdx.app.getPreferences("settings");
        gamesPlayed = preferences.getInteger("gamesPlayed", 0);
        musicOn = preferences.getBoolean("musicOn", true);
        soundOn = preferences.getBoolean("soundOn", true);
        highScore = preferences.getInteger("highScore", 0);
        gemCount = preferences.getInteger("gemCount", 0);
        characterId = preferences.getInteger("characterId", 0);

        unlocked0 = preferences.getBoolean("unlocked0", true);
        unlocked1 = preferences.getBoolean("unlocked1", false);
        unlocked2 = preferences.getBoolean("unlocked2", false);
        unlocked3 = preferences.getBoolean("unlocked3", false);
        unlocked4 = preferences.getBoolean("unlocked4", false);
        unlocked5 = preferences.getBoolean("unlocked5", false);
        unlocked6 = preferences.getBoolean("unlocked6", false);
        unlocked7 = preferences.getBoolean("unlocked7", false);
        unlocked8 = preferences.getBoolean("unlocked8", false);
        unlocked9 = preferences.getBoolean("unlocked9", false);
    }

    public static void save() {
        preferences.putInteger("gamesPlayed", gamesPlayed);
        preferences.putBoolean("musicOn", musicOn);
        preferences.putBoolean("soundOn", soundOn);
        preferences.putInteger("highScore", highScore);
        preferences.putInteger("gemCount", gemCount);
        preferences.putInteger("characterId", characterId);

        preferences.putBoolean("unlocked0", unlocked0);
        preferences.putBoolean("unlocked1", unlocked1);
        preferences.putBoolean("unlocked2", unlocked2);
        preferences.putBoolean("unlocked3", unlocked3);
        preferences.putBoolean("unlocked4", unlocked4);
        preferences.putBoolean("unlocked5", unlocked5);
        preferences.putBoolean("unlocked6", unlocked6);
        preferences.putBoolean("unlocked7", unlocked7);
        preferences.putBoolean("unlocked8", unlocked8);
        preferences.putBoolean("unlocked9", unlocked9);
        preferences.flush();
    }

    public static void updateHighScore(int score) {
        if (score > highScore) {
            highScore = score;
        }
    }

    public static boolean isUnlocked(int id) {
        //return preferences.getBoolean("unlocked"+id, false);
        switch (id) {
            case 0:
                return unlocked0;
            case 1:
                return unlocked1;
            case 2:
                return unlocked2;
            case 3:
                return unlocked3;
            case 4:
                return unlocked4;
            case 5:
                return unlocked5;
            case 6:
                return unlocked6;
            case 7:
                return unlocked7;
            case 8:
                return unlocked8;
            case 9:
                return unlocked9;
        }
        return false;
    }
    public static void setUnlocked(int id, boolean unlocked) {
        //preferences.putBoolean("unlocked"+id, unlocked);
        switch (id) {
            case 0:
                unlocked0 = unlocked;
                break;
            case 1:
                unlocked1 = unlocked;
                break;
            case 2:
                unlocked2 = unlocked;
                break;
            case 3:
                unlocked3 = unlocked;
                break;
            case 4:
                unlocked4 = unlocked;
                break;
            case 5:
                unlocked5 = unlocked;
                break;
            case 6:
                unlocked6 = unlocked;
                break;
            case 7:
                unlocked7 = unlocked;
                break;
            case 8:
                unlocked8 = unlocked;
                break;
            case 9:
                unlocked9 = unlocked;
                break;
        }
    }
}
