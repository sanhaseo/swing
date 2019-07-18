package com.sanha.swinggame.world;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.sanha.swinggame.SwingGame;
import com.sanha.swinggame.objects.Gem;
import com.sanha.swinggame.objects.Rope;
import com.sanha.swinggame.objects.Swinger;
import com.sanha.swinggame.objects.WeakRope;
import com.sanha.swinggame.objects.WeakerRope;
import com.sanha.swinggame.tools.Setting;

import java.util.Random;

public class GameWorld {
    private static final float PPM = 100;
    static final float WIDTH = SwingGame.WIDTH / PPM;
    public static final float HEIGHT = SwingGame.HEIGHT / PPM;

    private static final float GRAVITY = -10;

    private static final int NUM_ROPES_ONSCREEN = 5; //number of ropes on screen at any given time
    private static final float ROPE_Y_POS = HEIGHT * 0.8f;
    private static final float Y_OFFSET = HEIGHT * 0.2f;    //max vertical offset from default position
    private static final float MIN_LENGTH = 2.3f;  //min rope length
    private static final float MAX_LENGTH = 2.7f;  //max rope length

    private static final float GAMEOVER_HEIGHT = 0;

    private float ropeSpacing = WIDTH/NUM_ROPES_ONSCREEN;    //horizontal spacing between ropes
    private int numRopesTotal = NUM_ROPES_ONSCREEN * 2 - 1; //number of ropes that exist in the world at any given time
    private int minNumSegments = (int) Math.floor(MIN_LENGTH/Rope.SEGMENT_LENGTH);
    private int maxNumSegments = (int) Math.ceil(MAX_LENGTH/Rope.SEGMENT_LENGTH);

    private World world;
    private Array<Rope> ropes;
    private Swinger swinger;
    private Array<Gem> gems;

    private int lastRopeId; //last rope created
    private int prevRopeId;
    private int currentRopeId;
    private int gemCount;
    private boolean updateRopesAndGems;
    private boolean gameRunning;
    private boolean gameOver;
    private boolean executingHang;
    private Vector2 gemConsumePosition;

    private AssetManager manager;

    public GameWorld(AssetManager manager) {
        this.manager = manager;
        initVariables();
        createInitialRopesAndGems();
        createSwinger();
    }

    //***
    //update methods begin
    public void update(float dt) {
        swinger.update();
        updateConsumedGems();
        if (updateRopesAndGems) updateRopesAndGems();
        updateWeakRopes(dt);
        checkGameOver();
        executingHang = false;
        world.step(1 / 60f, 6, 2);
    }
    private void updateWeakRopes(float dt) {
        for (Rope rope : ropes) {
            if (rope instanceof WeakRope) ((WeakRope) rope).update(dt);
            else if (rope instanceof WeakerRope) ((WeakerRope) rope).update(dt);
        }
    }
    private void updateRopesInfo() {
        prevRopeId = currentRopeId;
        currentRopeId = swinger.ropeId();
        updateRopesAndGems = true;
    }
    private void updateRopesAndGems() {
        //number of ropes jumped in the last jump
        int n = currentRopeId - prevRopeId;

        //remove the first n ropes and gems
        for (int i=0; i<n; i++) {
            ropes.get(i).destroy();
            if (gems.get(i) != null) gems.get(i).destroy();
        }
        ropes.removeRange(0, n-1);
        gems.removeRange(0, n-1);

        //add n new ropes and gems
        for (int i=0; i<n; i++) {
            createRope(lastRopeId + 1);
            createGem(lastRopeId + 1);
        }

        updateRopesAndGems = false;
    }
    private void updateConsumedGems() {
        //destroy consumed gems
        for (int i=0; i<gems.size; i++) {
            Gem gem = gems.get(i);
            if (gem != null && gem.isConsumed()) {
                gem.destroy();
                gems.set(i, null);
            }
        }
    }
    //update methods end
    //***

    //***
    //control methods begin
    void pushSwinger() {
        swinger.push();
    }
    void jumpSwinger() {
        swinger.jump();
        playJumpSound();
    }
    void hangSwinger(Rope rope) {
        if (!executingHang) {
            swinger.hang(rope);
            updateRopesInfo();
            playHangSound();
            executingHang = true;
        }
    }
    void jumpAndHangSwinger(Rope rope) {
        if (gameRunning && !executingHang) {
            swinger.jumpAndHang(rope);
            updateRopesInfo();
            playHangSound();
            executingHang = true;
        }
    }
    void consumeGem(Gem gem) {
        if (gameRunning) {
            gemConsumePosition = new Vector2(gem.getBody().getPosition());

            gem.setConsumed(true);
            playGemSound();
            if (currentRopeId < 20) gemCount += 1;
            else if (currentRopeId < 40) gemCount += 2;
            else if (currentRopeId < 60) gemCount += 3;
            else gemCount += 4;
        }
    }
    private void checkGameOver() {
        if (swinger.getY() < GAMEOVER_HEIGHT && !gameOver) {
            gameOver = true;
            playFallSound();
        }
    }
    //control methods end
    //***

    //***
    //create game objects begin
    private void initVariables() {
        world = new World(new Vector2(0, GRAVITY), true);
        world.setContactListener(new WorldContactListener(this));
        ropes = new Array<Rope>(numRopesTotal);
        gems = new Array<Gem>(numRopesTotal);
        prevRopeId = 0;
        currentRopeId = 0;
        gemCount = 0;
        updateRopesAndGems = false;
        gameRunning = false;
        gameOver = false;
        executingHang = false;
        gemConsumePosition = null;
    }
    private void createInitialRopesAndGems() {
        for (int i=0; i<numRopesTotal; i++) {
            int id = i - NUM_ROPES_ONSCREEN + 1;
            createRope(id);
            createGem(id);
        }
    }
    private void createRope(int id) {
        Random rand = new Random();
        float x = (id + 0.5f) * ropeSpacing;
        float y = ROPE_Y_POS + (-1 + 2 * rand.nextFloat()) * Y_OFFSET;
        int numSegments = minNumSegments + rand.nextInt(maxNumSegments - minNumSegments);

        //create weak/fake rope with 1/n probability
        int n = 8;
        if (currentRopeId < 20) n = 8;
        else if (currentRopeId < 40) n = 4;
        else if (currentRopeId < 60) n = 1;
        if (rand.nextInt(n) == 0) {
            if (rand.nextInt(3) == 0) ropes.add(new WeakerRope(world, x, y, numSegments, id));
            else ropes.add(new WeakRope(world, x, y, numSegments, id));
        }
        else {
            ropes.add(new Rope(world, x, y, numSegments, id));
        }
        lastRopeId = id;
    }
    private void createSwinger() {
        swinger = new Swinger(world, ropes.get(NUM_ROPES_ONSCREEN-1));
    }
    private void createGem(int i) {
        float x = (i + 1) * ropeSpacing;
        gems.add(new Gem(world, x));
    }
    //create game objects end
    //***

    //***
    //play sound methods
    private void playJumpSound() {
        if (Setting.soundOn) manager.get("audio/jump.mp3", Sound.class).play();
    }
    private void playHangSound() {
        if (Setting.soundOn) manager.get("audio/NFF-menu-06-a.wav", Sound.class).play();
    }
    private void playFallSound() {
        if (Setting.soundOn) manager.get("audio/fall.mp3", Sound.class).play(0.3f);
    }
    private void playGemSound() {
        if (Setting.soundOn) manager.get("audio/NFF-coin.wav", Sound.class).play(0.3f);
    }
    //play sound methods
    //***

    //***
    //getters and setters
    Array<Rope> getRopes() {
        return ropes;
    }
    Swinger getSwinger() {
        return swinger;
    }
    Array<Gem> getGems() {
        return gems;
    }
    public int getScore() {
        return currentRopeId;
    }
    public int getGemCount() {
        return gemCount;
    }
    float getCurrentWorldPosition() {
        return currentRopeId * ropeSpacing;
    }
    public boolean isGameOver() {
        return gameOver;
    }
    public void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
    }
    public Vector2 getGemConsumePosition() {
        return gemConsumePosition;
    }
    public void resetGemConsumePosition() {
        gemConsumePosition = null;
    }
    //getters and setters
    //***

    public void dispose() {
        world.dispose();
    }

    //!!! TEST !!!
    public World getWorld() {
        return world;
    }
}
