package com.sanha.swinggame.uis;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sanha.swinggame.SwingGame;
import com.sanha.swinggame.screens.GameScreen;
import com.sanha.swinggame.tools.Setting;

public class MenuUI extends AbstractUI {
    private static final int TABLE_BTN_WIDTH = 90;
    private static final int TABLE_BTN_HEIGHT = 90;
    private static final int LBL_FONT_SIZE = 50;
    private static final float LBL_CELL_WIDTH = 250;

    //table
    private Table menuTable;
    private Label musicLbl;
    private Label soundLbl;

    //labels and buttons
    private Image background;
    private Button homeBtn;
    private BitmapFont menuLblFont;

    public MenuUI(final GameScreen screen, Viewport viewport, SwingGame game) {
        super(screen, viewport, game);

        menuLblFont = generateLblFont(LBL_FONT_SIZE);

        //create table
        menuTable = createMenuTable();

        //create labels and buttons
        createBackground();
        homeBtn = createHomeBtn(btn1X, BTN_Y, BTN_WIDTH, BTN_HEIGHT);

        //add labels and buttons to stage
        addActor(background);
        addActor(menuTable);
        addActor(homeBtn);

        addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) return true;
                return false;
            }
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    screen.setCommand(GameScreen.Command.READY);
                }
                return true;
            }
        });
    }

    private void createBackground() {
        background = new Image(new TextureRegionDrawable(game.atlas.findRegion("transparentDark10")));
        background.setBounds(0, 0, width, height);
    }
    private Button createHomeBtn(float x, float y, final float width, final float height) {
        final Button btn = new Button(new TextureRegionDrawable(game.atlas.findRegion("home")));
        btn.setBounds(x, y, width, height);
        btn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                btn.setSize(width * BTN_PRESSED_SCALE, height * BTN_PRESSED_SCALE);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                btn.setSize(width, height);
                screen.setCommand(GameScreen.Command.READY);
            }
        });
        return btn;
    }

    private Table createMenuTable() {
        Table table = new Table();

        musicLbl = createMusicLbl();
        soundLbl = createSoundLbl();
        Label leaderboardLbl = createLeaderboardLbl();
        Label achievementLbl = createAchievementLbl();
        Image musicBtn = createMusicBtn(TABLE_BTN_WIDTH, TABLE_BTN_HEIGHT);
        Image soundBtn = createSoundBtn(TABLE_BTN_WIDTH, TABLE_BTN_HEIGHT);
        Button leaderboardBtn = createLeaderboardBtn(TABLE_BTN_WIDTH, TABLE_BTN_HEIGHT);
        Button achievementBtn = createAchievementBtn(TABLE_BTN_WIDTH, TABLE_BTN_HEIGHT);

        table.setFillParent(true);
        table.padBottom(100);
        table.add(musicLbl).size(LBL_CELL_WIDTH, TABLE_BTN_HEIGHT).align(Align.left).padRight(100);
        table.add(musicBtn).size(TABLE_BTN_WIDTH, TABLE_BTN_HEIGHT).align(Align.right);
        table.row();
        table.add(soundLbl).size(LBL_CELL_WIDTH, TABLE_BTN_HEIGHT).align(Align.left).padRight(100);
        table.add(soundBtn).size(TABLE_BTN_WIDTH, TABLE_BTN_HEIGHT).align(Align.right);
        table.row();
        table.add(leaderboardLbl).size(LBL_CELL_WIDTH, TABLE_BTN_HEIGHT).align(Align.left).padRight(100);
        table.add(leaderboardBtn).size(TABLE_BTN_WIDTH, TABLE_BTN_HEIGHT).align(Align.right);
        table.row();
        table.add(achievementLbl).size(LBL_CELL_WIDTH, TABLE_BTN_HEIGHT).align(Align.left).padRight(100);
        table.add(achievementBtn).size(TABLE_BTN_WIDTH, TABLE_BTN_HEIGHT).align(Align.right);

        return table;
    }
    private Image createMusicBtn(final float width, final float height) {
        final Drawable musicOnDrawable = new TextureRegionDrawable(game.atlas.findRegion("musicOn"));
        final Drawable musicOffDrawable = new TextureRegionDrawable(game.atlas.findRegion("musicOff"));

        final Image btn;
        if (Setting.musicOn) {
            btn = new Image(musicOnDrawable);
        }
        else {
            btn = new Image(musicOffDrawable);
        }

        btn.setSize(width, height);
        btn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (Setting.musicOn) {
                    btn.setDrawable(musicOffDrawable);
                    musicLbl.setText("music: off");
                    Setting.musicOn = false;
                    screen.stopMusic();
                }
                else {
                    btn.setDrawable(musicOnDrawable);
                    musicLbl.setText("music: on");
                    Setting.musicOn = true;
                    screen.playMusic();
                }
                Setting.save();
            }
        });
        return btn;
    }
    private Image createSoundBtn(final float width, final float height) {
        final Drawable soundOnDrawable = new TextureRegionDrawable(game.atlas.findRegion("audioOn"));
        final Drawable soundOffDrawable = new TextureRegionDrawable(game.atlas.findRegion("audioOff"));

        final Image btn;
        if (Setting.soundOn) {
            btn = new Image(soundOnDrawable);
        }
        else {
            btn = new Image(soundOffDrawable);
        }

        btn.setSize(width, height);
        btn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //btn.setSize(width * BTN_PRESSED_SCALE, height * BTN_PRESSED_SCALE);
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                //btn.setSize(width, height);
                if (Setting.soundOn) {
                    btn.setDrawable(soundOffDrawable);
                    soundLbl.setText("sound: off");
                    Setting.soundOn = false;
                }
                else {
                    btn.setDrawable(soundOnDrawable);
                    soundLbl.setText("sound: on");
                    Setting.soundOn = true;
                }
                Setting.save();
            }
        });
        return btn;
    }
    private Button createLeaderboardBtn(final float width, final float height) {
        final Button btn = new Button(new TextureRegionDrawable(game.atlas.findRegion("leaderboardsComplex")));
        btn.setSize(width, height);
        btn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (game.actionResolver.isSignedIn()) game.actionResolver.getLeaderboard();
                else game.actionResolver.signIn();
            }
        });
        return btn;
    }
    private Button createAchievementBtn(final float width, final float height) {
        final Button btn = new Button(new TextureRegionDrawable(game.atlas.findRegion("trophy")));
        btn.setSize(width, height);
        btn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (game.actionResolver.isSignedIn()) game.actionResolver.getAchievements();
                else game.actionResolver.signIn();
            }
        });
        return btn;
    }
    private Label createMusicLbl() {
        String text;
        text = Setting.musicOn ? "music: on" : "music: off";

        GlyphLayout layout = new GlyphLayout();
        layout.setText(menuLblFont, text);

        //create label
        Label label = new Label(text, new Label.LabelStyle(menuLblFont, Color.WHITE));
        label.setSize(layout.width, layout.height);

        return label;
    }
    private Label createSoundLbl() {
        String text;
        text = Setting.soundOn ? "sound: on" : "sound: off";

        GlyphLayout layout = new GlyphLayout();
        layout.setText(menuLblFont, text);

        //create label
        Label label = new Label(text, new Label.LabelStyle(menuLblFont, Color.WHITE));
        label.setSize(layout.width, layout.height);

        return label;
    }
    private Label createLeaderboardLbl() {
        String text = "leaderboard";

        GlyphLayout layout = new GlyphLayout();
        layout.setText(menuLblFont, text);

        //create label
        Label label = new Label(text, new Label.LabelStyle(menuLblFont, Color.WHITE));
        label.setSize(layout.width, layout.height);

        return label;
    }
    private Label createAchievementLbl() {
        String text = "achievement";

        GlyphLayout layout = new GlyphLayout();
        layout.setText(menuLblFont, text);

        //create label
        Label label = new Label(text, new Label.LabelStyle(menuLblFont, Color.WHITE));
        label.setSize(layout.width, layout.height);

        return label;
    }

    @Override
    public void dispose() {
        menuLblFont.dispose();
        super.dispose();
    }
    @Override
    protected void normalTransitionIn() {
        menuTable.addAction(Actions.sequence(
                Actions.moveTo(0, height),
                Actions.moveTo(0, 0, GameScreen.TRANSITION_TIME)
        ));
        homeBtn.addAction(Actions.sequence(
                Actions.moveTo(btn1X, -BTN_HEIGHT),
                Actions.moveTo(btn1X, BTN_Y, GameScreen.TRANSITION_TIME)
        ));
    }
    @Override
    protected void normalTransitionOut() {
        menuTable.addAction(Actions.moveTo(0, height, GameScreen.TRANSITION_TIME));
        homeBtn.addAction(Actions.moveTo(btn1X, -BTN_HEIGHT, GameScreen.TRANSITION_TIME));
    }
}
