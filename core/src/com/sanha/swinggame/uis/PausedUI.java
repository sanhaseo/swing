package com.sanha.swinggame.uis;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sanha.swinggame.SwingGame;
import com.sanha.swinggame.screens.GameScreen;

public class PausedUI extends AbstractUI {
    private static final int LBL_FONT_SIZE = 40;
    private static final String LBL_TEXT = "game paused";
    private static final int SCORE_LBL_FONT_SIZE = 70;
    private static final int GEM_LBL_FONT_SIZE = 40;
    private static final float GEM_IMG_SIZE = 40;

    //labels and buttons
    private Image background;
    private Image gemImg;
    private Label gemLbl;
    private Label scoreLbl;
    private Label pausedLbl;
    private Button homeBtn;
    private Button resumeBtn;

    //label positions
    private float gemImgX;
    private float gemLblX;
    private float scoreLblCenterX;
    private float topHudY;
    private float pausedLblCenterX;
    private float pausedLblCenterY;

    //fonts
    private BitmapFont pausedLblFont;
    private BitmapFont scoreLblFont;
    private BitmapFont gemLblFont;

    public PausedUI(final GameScreen screen, Viewport viewport, SwingGame game) {
        super(screen, viewport, game);

        gemImgX = 30;
        gemLblX = gemImgX + GEM_IMG_SIZE + 20;
        pausedLblCenterX = width / 2;
        pausedLblCenterY = height / 2;
        scoreLblCenterX = width / 2;
        topHudY = height - 70;

        //labels and buttons
        createBackground();
        createGemImg();
        createGemLbl();
        createScoreLbl();
        createPausedLbl();
        homeBtn = createHomeBtn(btn1X, BTN_Y, BTN_WIDTH, BTN_HEIGHT);
        resumeBtn = createResumeBtn(btn4X, BTN_Y, BTN_WIDTH, BTN_HEIGHT);

        //add labels and buttons to stage
        addActor(background);
        addActor(gemImg);
        addActor(gemLbl);
        addActor(scoreLbl);
        addActor(pausedLbl);
        addActor(homeBtn);
        addActor(resumeBtn);

        addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) return true;
                return false;
            }
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    screen.setCommand(GameScreen.Command.RUNNING);
                }
                return true;
            }
        });
    }

    private void createBackground() {
        background = new Image(new TextureRegionDrawable(game.atlas.findRegion("transparentDark10")));
        background.setBounds(0, 0, width, height);
    }
    private void createGemImg() {
        gemImg = new Image(new TextureRegionDrawable(game.atlas.findRegion("star_gold")));
        gemImg.setBounds(gemImgX, topHudY+15, GEM_IMG_SIZE, GEM_IMG_SIZE);
    }
    private void createGemLbl() {
        String text = "0";
        gemLblFont = generateLblFont(GEM_LBL_FONT_SIZE);
        textLayout.setText(gemLblFont, text);
        gemLbl = createLbl(
                text,
                gemLblX,
                topHudY+15,
                textLayout.width,
                textLayout.height,
                gemLblFont,
                new Color(1, 220f / 255, 60f/ 255, 1)
        );
    }
    private void createScoreLbl() {
        String text = "0";
        scoreLblFont = generateLblFont(SCORE_LBL_FONT_SIZE);
        textLayout.setText(scoreLblFont, text);
        scoreLbl = createLbl(
                text,
                scoreLblCenterX - textLayout.width / 2,
                topHudY,
                textLayout.width,
                textLayout.height,
                scoreLblFont,
                Color.WHITE
        );
    }
    private void createPausedLbl() {
        pausedLblFont = generateLblFont(LBL_FONT_SIZE);
        textLayout.setText(pausedLblFont, LBL_TEXT);
        pausedLbl = createLbl(
                LBL_TEXT,
                pausedLblCenterX - textLayout.width / 2,
                pausedLblCenterY - textLayout.height / 2,
                textLayout.width,
                textLayout.height,
                pausedLblFont,
                Color.WHITE
        );
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
    private Button createResumeBtn(float x, float y, final float width, final float height) {
        final Button btn = new Button(new TextureRegionDrawable(game.atlas.findRegion("forward")));
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
                screen.setCommand(GameScreen.Command.RUNNING);
            }
        });
        return btn;
    }

    public void updateScoreLbl(int score) {
        textLayout.setText(scoreLblFont, score+"");
        scoreLbl.setText(score+"");
        scoreLbl.setBounds(
                scoreLblCenterX - textLayout.width / 2,
                topHudY,
                textLayout.width,
                textLayout.height
        );
    }
    public void updateGemLbl(int gemCount) {
        textLayout.setText(gemLblFont, gemCount+"");
        gemLbl.setText(gemCount+"");
        gemLbl.setBounds(
                gemLblX,
                topHudY+15,
                textLayout.width,
                textLayout.height
        );
    }

    @Override
    public void dispose() {
        pausedLblFont.dispose();
        scoreLblFont.dispose();
        gemLblFont.dispose();
        super.dispose();
    }
    @Override
    protected void normalTransitionIn() {
        homeBtn.addAction(Actions.sequence(
                Actions.moveTo(btn1X, -BTN_HEIGHT),
                Actions.moveTo(btn1X, BTN_Y, GameScreen.TRANSITION_TIME)
        ));
        resumeBtn.addAction(Actions.sequence(
                Actions.moveTo(btn4X, -BTN_HEIGHT),
                Actions.moveTo(btn4X, BTN_Y, GameScreen.TRANSITION_TIME)
        ));
    }
    @Override
    protected void normalTransitionOut() {
        homeBtn.addAction(Actions.moveTo(btn1X, -BTN_HEIGHT, GameScreen.TRANSITION_TIME));
        resumeBtn.addAction(Actions.moveTo(btn4X, -BTN_HEIGHT, GameScreen.TRANSITION_TIME));
    }
}
