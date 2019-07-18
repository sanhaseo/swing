package com.sanha.swinggame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.sanha.swinggame.screens.GameScreen;
import com.sanha.swinggame.tools.ActionResolver;
import com.sanha.swinggame.tools.Setting;

public class SwingGame extends Game {
	//used by all screens
	public static final float WIDTH = 1136;
	public static final float HEIGHT = 640;
	public ActionResolver actionResolver;
	public SpriteBatch batch;
	public AssetManager manager;
	public TextureAtlas atlas;

	public SwingGame(ActionResolver actionResolver) {
		this.actionResolver = actionResolver;
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		manager = new AssetManager();
		Setting.load();
		loadAssets();
		loadAtlas();

		setScreen(new GameScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		manager.dispose();
	}

	private void loadAssets() {
		manager.load("spritesheet.pack", TextureAtlas.class);
        manager.load("audio/NFF-menu-06-a.wav", Sound.class);
        manager.load("audio/NFF-coin.wav", Sound.class);
        manager.load("audio/jump.mp3", Sound.class);
        manager.load("audio/fall.mp3", Sound.class);
		manager.load("audio/cash.mp3", Sound.class);
		manager.load("audio/error.mp3", Sound.class);
		manager.load("audio/POL-cooking-mania-short.ogg", Music.class);

		manager.finishLoading();
	}

	private void loadAtlas() {
		atlas = manager.get("spritesheet.pack", TextureAtlas.class);
	}
}
