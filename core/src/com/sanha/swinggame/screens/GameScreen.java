package com.sanha.swinggame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sanha.swinggame.SwingGame;
import com.sanha.swinggame.tools.Setting;
import com.sanha.swinggame.uis.AbstractUI;
import com.sanha.swinggame.uis.CartUI;
import com.sanha.swinggame.uis.GameOverUI;
import com.sanha.swinggame.uis.MenuUI;
import com.sanha.swinggame.uis.PausedUI;
import com.sanha.swinggame.uis.ReadyUI;
import com.sanha.swinggame.uis.RunningUI;
import com.sanha.swinggame.world.GameWorld;
import com.sanha.swinggame.world.WorldInputListener;
import com.sanha.swinggame.world.WorldRenderer;

public class GameScreen extends ScreenAdapter {
    private static final int GAMES_PER_AD = 4;  //number of games played for each interstitial ad shown
    public static final float TRANSITION_TIME = 0.2f;
    private static final float WIDTH = SwingGame.WIDTH;
    private static final float HEIGHT = SwingGame.HEIGHT;

    private enum State {READY, RUNNING, PAUSED, GAMEOVER, CART, MENU}
    public enum Command {
        NONE,
        READY,
        RUNNING,
        PAUSED,
        CART,
        MENU
    }

    private SwingGame game;

    private State state;
    private Command command;    //cammand for next update

    private OrthographicCamera guiCam;
    private Viewport viewport;
    private GameWorld world;
    private WorldRenderer renderer;
    private Music music;

    //UIs
    private ReadyUI readyUI;
    private RunningUI runningUI;
    private PausedUI pausedUI;
    private GameOverUI gameOverUI;
    private CartUI cartUI;
    private MenuUI menuUI;

    public GameScreen(SwingGame game) {
        initVariables(game);

        state = State.READY;
        command = Command.NONE;
        Gdx.input.setInputProcessor(readyUI);
        readyUI.transitionIn(AbstractUI.Transition.FADE);
    }

    //***
    //update methods begin
    private void update(float dt) {
        switch (state) {
            case READY:
                updateReady(dt);
                break;
            case RUNNING:
                updateRunning(dt);
                break;
            case PAUSED:
                updatePaused();
                break;
            case GAMEOVER:
                updateGameOver(dt);
                break;
            case CART:
                updateCart(dt);
                break;
            case MENU:
                updateMenu(dt);
                break;
        }
    }
    private void updateReady(float dt) {
        world.update(dt);

        switch (command) {
            case RUNNING:
                transitionReadyToRunning();
                break;
            case CART:
                transitionReadyToCart();
                break;
            case MENU:
                transitionReadyToMenu();
                break;
        }
        command = Command.NONE;
    }
    private void updateRunning(float dt) {
        world.update(dt);
        runningUI.updateScoreLbl(world.getScore());
        runningUI.updateGemLbl(world.getGemCount());

        if (world.isGameOver()) {
            transitionRunningToGameOver();
        }
        else if (command == Command.PAUSED) {
            transitionRunningToPaused();
        }
        command = Command.NONE;
    }
    private void updatePaused() {
        switch (command) {
            case READY:
                transitionPausedToReady();
                break;
            case RUNNING:
                transitionPausedToRunning();
                break;
        }
        command = Command.NONE;
    }
    private void updateGameOver(float dt) {
        world.update(dt);

        if (command == Command.READY) {
            transitionGameOverToReady();
        }
        command = Command.NONE;
    }
    private void updateCart(float dt) {
        world.update(dt);

        if (command == Command.READY) {
            transitionCartToReady();
        }
        command = Command.NONE;
    }
    private void updateMenu(float dt) {
        world.update(dt);

        if (command == Command.READY) {
            transitionMenuToReady();
        }
        command = Command.NONE;
    }
    //update methods end
    //***

    //***
    //draw methods begin
    private void draw(float dt) {
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render(dt);

        game.batch.setProjectionMatrix(guiCam.combined);
        switch (state) {
            case READY:
                drawReadyUI();
                break;
            case RUNNING:
                drawRunningUI();
                break;
            case PAUSED:
                drawPausedUI();
                break;
            case GAMEOVER:
                drawGameOverUI();
                break;
            case CART:
                drawGameCartUI();
                break;
            case MENU:
                drawGameMenuUI();
                break;
        }
    }
    private void drawReadyUI() {
        readyUI.act();
        readyUI.draw();
    }
    private void drawRunningUI() {
        runningUI.act();
        runningUI.draw();
    }
    private void drawPausedUI() {
        pausedUI.act();
        pausedUI.draw();
    }
    private void drawGameOverUI() {
        gameOverUI.act();
        gameOverUI.draw();
    }
    private void drawGameCartUI() {
        cartUI.act();
        cartUI.draw();
    }
    private void drawGameMenuUI() {
        menuUI.act();
        menuUI.draw();
    }
    //draw methods end
    //***

    private void transitionReadyToRunning() {
        if (Setting.gamesPlayed % GAMES_PER_AD == GAMES_PER_AD-1) {    //preload interstitial ad
            game.actionResolver.loadInterstitialAd();
        }
        readyUI.transitionOut(AbstractUI.Transition.NORMAL);
        readyUI.addAction(Actions.sequence(
                Actions.delay(TRANSITION_TIME),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        state = State.RUNNING;
                        InputMultiplexer multiplexer = new InputMultiplexer();
                        multiplexer.addProcessor(runningUI);
                        multiplexer.addProcessor(new WorldInputListener(world));
                        Gdx.input.setInputProcessor(multiplexer);
                        world.setGameRunning(true);
                    }
                })));
        runningUI.transitionIn(AbstractUI.Transition.INSTANT);
        game.actionResolver.hideBannerAd();
    }
    private void transitionReadyToCart() {
        readyUI.transitionOut(AbstractUI.Transition.NORMAL);
        readyUI.addAction(Actions.sequence(
                Actions.delay(TRANSITION_TIME),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        state = State.CART;
                        cartUI.updateGemLbl(Setting.gemCount);
                        cartUI.resetItemSelection();
                        Gdx.input.setInputProcessor(cartUI);
                    }
                })));
        cartUI.transitionIn(AbstractUI.Transition.NORMAL);
    }
    private void transitionReadyToMenu() {
        readyUI.transitionOut(AbstractUI.Transition.NORMAL);
        readyUI.addAction(
                Actions.sequence(Actions.delay(TRANSITION_TIME),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                state = State.MENU;
                                Gdx.input.setInputProcessor(menuUI);
                            }
                        })));
        menuUI.transitionIn(AbstractUI.Transition.NORMAL);
    }
    private void transitionRunningToPaused() {
        runningUI.transitionOut(AbstractUI.Transition.INSTANT);
        state = State.PAUSED;
        pausedUI.updateScoreLbl(world.getScore());
        pausedUI.updateGemLbl(world.getGemCount());
        Gdx.input.setInputProcessor(pausedUI);
        pausedUI.transitionIn(AbstractUI.Transition.NORMAL);
        game.actionResolver.showBannerAd();
    }
    private void transitionRunningToGameOver() {
        //display interstitial ad every 3 games
        if (Setting.gamesPlayed % GAMES_PER_AD == GAMES_PER_AD-1) {
            game.actionResolver.showInterstitialAd();
        }

        runningUI.transitionOut(AbstractUI.Transition.INSTANT);
        state = State.GAMEOVER;
        Setting.gamesPlayed++;
        Setting.updateHighScore(world.getScore());
        Setting.gemCount += world.getGemCount();
        Setting.save();

        if (game.actionResolver.isSignedIn()) {
            int score = world.getScore();
            game.actionResolver.submitScore(score);

            //unlock achievements
            if (Setting.gamesPlayed >= 10) game.actionResolver.unlockAchievement("CgkIo5CqufodEAIQBw");
            if (Setting.gamesPlayed >= 100) game.actionResolver.unlockAchievement("CgkIo5CqufodEAIQCA");
            if (Setting.gamesPlayed >= 500) game.actionResolver.unlockAchievement("CgkIo5CqufodEAIQCw");

            if (score >= 10) game.actionResolver.unlockAchievement("CgkIo5CqufodEAIQAQ");
            if (score >= 20) game.actionResolver.unlockAchievement("CgkIo5CqufodEAIQAg");
            if (score >= 30) game.actionResolver.unlockAchievement("CgkIo5CqufodEAIQAw");
            if (score >= 40) game.actionResolver.unlockAchievement("CgkIo5CqufodEAIQBQ");
            if (score >= 50) game.actionResolver.unlockAchievement("CgkIo5CqufodEAIQBg");
            if (score >= 60) game.actionResolver.unlockAchievement("CgkIo5CqufodEAIQCQ");
            if (score >= 70) game.actionResolver.unlockAchievement("CgkIo5CqufodEAIQCg");
        }

        gameOverUI.updateGemLbl(world.getGemCount());
        gameOverUI.updateScoreLbl(world.getScore());
        gameOverUI.updateHighScoreLbl();
        Gdx.input.setInputProcessor(gameOverUI);
        gameOverUI.transitionIn(AbstractUI.Transition.NORMAL);
        game.actionResolver.showBannerAd();
    }
    private void transitionPausedToReady() {
        pausedUI.transitionOut(AbstractUI.Transition.FADE);
        pausedUI.addAction(
                Actions.sequence(Actions.delay(TRANSITION_TIME),
                        Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                state = State.READY;
                                Gdx.input.setInputProcessor(readyUI);
                                world.dispose();
                                world = new GameWorld(game.manager);
                                renderer.setWorld(world);
                            }
                        })));
        readyUI.transitionIn(AbstractUI.Transition.FADE);
    }
    private void transitionPausedToRunning() {
        pausedUI.transitionOut(AbstractUI.Transition.NORMAL);
        pausedUI.addAction(Actions.sequence(
                Actions.delay(TRANSITION_TIME),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        state = State.RUNNING;
                        InputMultiplexer multiplexer = new InputMultiplexer();
                        multiplexer.addProcessor(runningUI);
                        multiplexer.addProcessor(new WorldInputListener(world));
                        Gdx.input.setInputProcessor(multiplexer);
                    }
                })));
        runningUI.transitionIn(AbstractUI.Transition.NORMAL);
        game.actionResolver.hideBannerAd();
    }
    private void transitionGameOverToReady() {
        gameOverUI.transitionOut(AbstractUI.Transition.FADE);
        gameOverUI.addAction(Actions.sequence(
                Actions.delay(TRANSITION_TIME),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        state = State.READY;
                        Gdx.input.setInputProcessor(readyUI);
                        world.dispose();
                        world = new GameWorld(game.manager);
                        renderer.setWorld(world);
                    }
                })));
        readyUI.transitionIn(AbstractUI.Transition.FADE);
    }
    private void transitionCartToReady() {
        cartUI.transitionOut(AbstractUI.Transition.NORMAL);
        cartUI.addAction(Actions.sequence(
                Actions.delay(TRANSITION_TIME),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        state = State.READY;
                        Gdx.input.setInputProcessor(readyUI);
                    }
                })));
        readyUI.transitionIn(AbstractUI.Transition.NORMAL);
    }
    private void transitionMenuToReady() {
        menuUI.transitionOut(AbstractUI.Transition.NORMAL);
        menuUI.addAction(Actions.sequence(
                Actions.delay(TRANSITION_TIME),
                Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        state = State.READY;
                        Gdx.input.setInputProcessor(readyUI);
                    }
                })));
        readyUI.transitionIn(AbstractUI.Transition.NORMAL);
    }

    public Command getCommand() {
        return command;
    }
    public void setCommand(Command command) {
        this.command = command;
    }
    public void playMusic() {
        music.play();
    }
    public void stopMusic() {
        music.stop();
    }

    private void initVariables(SwingGame game) {
        this.game = game;
        guiCam = new OrthographicCamera();
        viewport = new FitViewport(WIDTH, HEIGHT, guiCam);
        guiCam.position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0);
        world = new GameWorld(game.manager);
        renderer = new WorldRenderer(game, world);
        loadMusic();
        Gdx.input.setCatchBackKey(true);

        //UIs
        readyUI = new ReadyUI(this, viewport, game);
        runningUI = new RunningUI(this, viewport, game);
        pausedUI = new PausedUI(this, viewport, game);
        gameOverUI = new GameOverUI(this, viewport, game);
        cartUI = new CartUI(this, viewport, game);
        menuUI = new MenuUI(this, viewport, game);

        if (!game.actionResolver.isSignedIn()) game.actionResolver.signIn();
    }
    private void loadMusic() {
        music = game.manager.get("audio/POL-cooking-mania-short.ogg", Music.class);
        music.setLooping(true);
        music.setVolume(0.1f);
        if (Setting.musicOn) music.play();
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw(delta);
    }

    @Override
    public void pause() {
        if (state == State.RUNNING) transitionRunningToPaused();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        renderer.resize(width, height);
    }

    @Override
    public void dispose() {
        readyUI.dispose();
        runningUI.dispose();
        pausedUI.dispose();
        gameOverUI.dispose();
        cartUI.dispose();
        menuUI.dispose();
        world.dispose();
        renderer.dispose();
    }
}
