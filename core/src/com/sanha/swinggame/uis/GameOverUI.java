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
import com.sanha.swinggame.tools.Setting;

public class GameOverUI extends AbstractUI {
    private static final int GEM_LBL_FONT_SIZE = 50;
    private static final float GEM_IMG_SIZE = 30;
    private static final int SCORE_LBL_FONT_SIZE = 120;
    private static final int HIGHSCORE_LBL_FONT_SIZE = 40;

    //labels and buttons
    private Image background;
    private Image gemImg;
    private Label gemLbl;
    private Label scoreLbl;
    private Label highScoreLbl;
    private Button homeBtn;
    private Button retryBtn;

    //label positions
    private float gemImgX;
    private float gemLblX;
    private float gemLblY;
    private float scoreLblCenterX;
    private float scoreLblCenterY;
    private float highScoreLblCenterX;
    private float highScoreLblCenterY;

    //fonts
    private BitmapFont gemLblFont;
    private BitmapFont scoreLblFont;
    private BitmapFont highScoreLblFont;

    public GameOverUI(final GameScreen screen, Viewport viewport, SwingGame game) {
        super(screen, viewport, game);

        gemImgX = width / 2 - 50;
        gemLblX = gemImgX + GEM_IMG_SIZE + 20;
        gemLblY = height / 2 - 10;
        scoreLblCenterX = width / 2;
        scoreLblCenterY = height * 3 / 4 - 50;
        highScoreLblCenterX = width / 2;
        highScoreLblCenterY = height / 2 - 50;

        //labels and buttons
        createBackground();
        createGemImg();
        createGemLbl();
        createScoreLbl();
        createHighScoreLbl();
        homeBtn = createHomeBtn(btn1X, BTN_Y, BTN_WIDTH, BTN_HEIGHT);
        retryBtn = createRetryBtn(btn4X, BTN_Y, BTN_WIDTH, BTN_HEIGHT);

        //add labels and buttons to stage
        addActor(background);
        addActor(gemImg);
        addActor(gemLbl);
        addActor(scoreLbl);
        addActor(highScoreLbl);
        addActor(homeBtn);
        addActor(retryBtn);

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
    private void createGemImg() {
        gemImg = new Image(new TextureRegionDrawable(game.atlas.findRegion("star_gold")));
        gemImg.setBounds(gemImgX, gemLblY+15, GEM_IMG_SIZE, GEM_IMG_SIZE);
    }
    private void createGemLbl() {
        String text = "0";
        gemLblFont = generateLblFont(GEM_LBL_FONT_SIZE);
        textLayout.setText(gemLblFont, text);
        gemLbl = createLbl(
                text,
                gemLblX,
                gemLblY+15,
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
                scoreLblCenterY - textLayout.height / 2,
                textLayout.width,
                textLayout.height,
                scoreLblFont,
                Color.WHITE
        );
    }
    private void createHighScoreLbl() {
        String text = "best : 0";
        highScoreLblFont = generateLblFont(HIGHSCORE_LBL_FONT_SIZE);
        textLayout.setText(highScoreLblFont, text);
        highScoreLbl = createLbl(
                text,
                highScoreLblCenterX - textLayout.width / 2,
                highScoreLblCenterY - textLayout.height / 2,
                textLayout.width,
                textLayout.height,
                highScoreLblFont,
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
    private Button createRetryBtn(float x, float y, final float width, final float height) {
        final Button btn = new Button(new TextureRegionDrawable(game.atlas.findRegion("return")));
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

    public void updateGemLbl(int gemCount) {
        textLayout.setText(gemLblFont, gemCount+"");
        gemLbl.setText(gemCount+"");
        gemLbl.setBounds(
                gemLblX,
                gemLblY+15,
                textLayout.width,
                textLayout.height
        );
    }
    public void updateScoreLbl(int score) {
        textLayout.setText(scoreLblFont, score+"");
        scoreLbl.setText(score+"");
        scoreLbl.setBounds(
                scoreLblCenterX - textLayout.width / 2,
                scoreLblCenterY - textLayout.height / 2,
                textLayout.width,
                textLayout.height
        );
    }
    public void updateHighScoreLbl() {
        textLayout.setText(highScoreLblFont, "best : "+Setting.highScore);
        highScoreLbl.setText("best : "+Setting.highScore);
        highScoreLbl.setBounds(
                highScoreLblCenterX - textLayout.width / 2,
                highScoreLblCenterY - textLayout.height / 2,
                textLayout.width,
                textLayout.height
        );
    }

    @Override
    public void dispose() {
        gemLblFont.dispose();
        scoreLblFont.dispose();
        highScoreLblFont.dispose();
        super.dispose();
    }
    @Override
    protected void normalTransitionIn() {
        homeBtn.addAction(Actions.sequence(
                Actions.moveTo(btn1X, -BTN_HEIGHT),
                Actions.moveTo(btn1X, BTN_Y, GameScreen.TRANSITION_TIME)
        ));
        retryBtn.addAction(Actions.sequence(
                Actions.moveTo(btn4X, -BTN_HEIGHT),
                Actions.moveTo(btn4X, BTN_Y, GameScreen.TRANSITION_TIME)
        ));
    }
    @Override
    protected void normalTransitionOut() {
        homeBtn.addAction(Actions.moveTo(btn1X, -BTN_HEIGHT, GameScreen.TRANSITION_TIME));
        retryBtn.addAction(Actions.moveTo(btn4X, -BTN_HEIGHT, GameScreen.TRANSITION_TIME));
    }
}
