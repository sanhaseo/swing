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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sanha.swinggame.SwingGame;
import com.sanha.swinggame.screens.GameScreen;

public class ReadyUI extends AbstractUI {
    private static final float TITLE_IMG_WIDTH = 400;
    private static final float TITLE_IMG_HEIGHT = 150;
    private static final float TITLE_IMG_Y = 400;
    private static final int LBL_FONT_SIZE = 40;
    //private static final String LBL_TEXT = "tap to start";
    private static final String LBL_TEXT = "tap and hold to push\nrelease to jump";

    //labels and buttons
    private Image titleImg;
    private Label tapLbl;
    private Button cartBtn;
    private Button menuBtn;

    //label positions
    private float tapLblCenterX;
    private float tapLblCenterY;

    //fonts
    private BitmapFont tapLblFont;

    public ReadyUI(final GameScreen screen, Viewport viewport, SwingGame game) {
        super(screen, viewport, game);

        tapLblCenterX = width / 2;
        tapLblCenterY = height / 3;

        //labels and buttons
        createTitleImg();
        createTapLbl();
        cartBtn = createCartBtn(btn1X, BTN_Y, BTN_WIDTH, BTN_HEIGHT);
        menuBtn = createMenuBtn(btn4X, BTN_Y, BTN_WIDTH, BTN_HEIGHT);

        //add labels and buttons to stage
        addActor(titleImg);
        addActor(tapLbl);
        addActor(cartBtn);
        addActor(menuBtn);

        //add listener
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (screen.getCommand() == GameScreen.Command.NONE) {
                    screen.setCommand(GameScreen.Command.RUNNING);
                }
            }
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) return true;
                return false;
            }
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                return true;
            }
        });
    }

    private void createTitleImg() {
        titleImg = new Image(new TextureRegionDrawable(game.atlas.findRegion("title")));
        titleImg.setBounds(
                width / 2 - TITLE_IMG_WIDTH / 2,
                TITLE_IMG_Y,
                TITLE_IMG_WIDTH,
                TITLE_IMG_HEIGHT
        );
    }
    private void createTapLbl() {
        tapLblFont = generateLblFont(LBL_FONT_SIZE);
        textLayout.setText(tapLblFont, LBL_TEXT);
        tapLbl = createLbl(
                LBL_TEXT,
                tapLblCenterX - textLayout.width / 2,
                tapLblCenterY - textLayout.height / 2,
                textLayout.width,
                textLayout.height,
                tapLblFont,
                Color.WHITE
        );
        tapLbl.setAlignment(Align.center);
    }
    private Button createCartBtn(float x, float y, final float width, final float height) {
        final Button btn = new Button(new TextureRegionDrawable(game.atlas.findRegion("cart")));
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
                screen.setCommand(GameScreen.Command.CART);
            }
        });
        return btn;
    }
    private Button createMenuBtn(float x, float y, final float width, final float height) {
        final Button btn = new Button(new TextureRegionDrawable(game.atlas.findRegion("gear")));
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
                screen.setCommand(GameScreen.Command.MENU);
            }
        });
        return btn;
    }

    @Override
    public void dispose() {
        tapLblFont.dispose();
        super.dispose();
    }
    @Override
    protected void normalTransitionIn() {
        titleImg.addAction(Actions.sequence(
                Actions.moveTo(width / 2 - TITLE_IMG_WIDTH / 2, height),
                Actions.moveTo(width / 2 - TITLE_IMG_WIDTH / 2, TITLE_IMG_Y, GameScreen.TRANSITION_TIME)
        ));
        cartBtn.addAction(Actions.sequence(
                Actions.moveTo(btn1X, -BTN_HEIGHT),
                Actions.moveTo(btn1X, BTN_Y, GameScreen.TRANSITION_TIME)
        ));
        menuBtn.addAction(Actions.sequence(
                Actions.moveTo(btn4X, -BTN_HEIGHT),
                Actions.moveTo(btn4X, BTN_Y, GameScreen.TRANSITION_TIME)
        ));
    }
    @Override
    protected void normalTransitionOut() {
        titleImg.addAction(Actions.moveTo(width / 2 - TITLE_IMG_WIDTH / 2, height, GameScreen.TRANSITION_TIME));
        cartBtn.addAction(Actions.moveTo(btn1X, -BTN_HEIGHT, GameScreen.TRANSITION_TIME));
        menuBtn.addAction(Actions.moveTo(btn4X, -BTN_HEIGHT, GameScreen.TRANSITION_TIME));
    }
}
