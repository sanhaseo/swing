package com.sanha.swinggame.uis;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sanha.swinggame.SwingGame;
import com.sanha.swinggame.screens.GameScreen;

public class RunningUI extends AbstractUI {
    private static final float PAUSE_BTN_WIDTH = 70;
    private static final float PAUSE_BTN_HEIGHT = 70;
    private static final int SCORE_LBL_FONT_SIZE = 70;
    private static final int GEM_LBL_FONT_SIZE = 40;
    private static final float GEM_IMG_SIZE = 30;

    //labels and buttons
    private Image gemImg;
    private Label gemLbl;
    private Label scoreLbl;

    //label and button positions
    private float gemImgX;
    private float gemLblX;
    private float scoreLblCenterX;
    private float topHudY;

    //fonts
    private BitmapFont scoreLblFont;
    private BitmapFont gemLblFont;

    public RunningUI(final GameScreen screen, Viewport viewport, SwingGame game) {
        super(screen, viewport, game);

        gemImgX = 30;
        gemLblX = gemImgX + GEM_IMG_SIZE + 20;
        scoreLblCenterX = width / 2;
        float pauseBtnX = width - 30 - PAUSE_BTN_WIDTH;
        topHudY = height - 70;

        //labels and buttons
        createGemImg();
        createGemLbl();
        createScoreLbl();
        Button pauseBtn = createPauseBtn(pauseBtnX, topHudY, PAUSE_BTN_WIDTH, PAUSE_BTN_HEIGHT);

        //add buttons to stage
        addActor(gemImg);
        addActor(gemLbl);
        addActor(scoreLbl);
        addActor(pauseBtn);

        addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) return true;
                return false;
            }
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    screen.setCommand(GameScreen.Command.PAUSED);
                }
                return true;
            }
        });
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
                //Color.WHITE
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
    private Button createPauseBtn(float x, float y, final float width, final float height) {
        final Button btn = new Button(new TextureRegionDrawable(game.atlas.findRegion("pause")));
        btn.setBounds(x, y, width, height);
        btn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                screen.setCommand(GameScreen.Command.PAUSED);
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
        scoreLblFont.dispose();
        gemLblFont.dispose();
        super.dispose();
    }
    @Override
    protected void normalTransitionIn() {

    }
    @Override
    protected void normalTransitionOut() {

    }
}