package com.sanha.swinggame.uis;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sanha.swinggame.SwingGame;
import com.sanha.swinggame.screens.GameScreen;
import com.sanha.swinggame.tools.Setting;

public class CartUI extends AbstractUI {
    private static final int GEM_LBL_FONT_SIZE = 40;
    private static final float GEM_IMG_SIZE = 30;

    //cart
    Cart cart;
    private Table cartTable;

    //labels and buttons
    private Image background;
    private Image gemImg;
    private Label gemLbl;
    private Button homeBtn;
    private Button unlockBtn;

    //label and button positions
    private float gemImgX;
    private float gemLblX;
    private float topHudY;

    //fonts
    private BitmapFont gemLblFont;

    public CartUI(final GameScreen screen, Viewport viewport, SwingGame game) {
        super(screen, viewport, game);
        cart = new Cart(this);
        cartTable = cart.getCartTable();

        gemImgX = 30;
        gemLblX = gemImgX + GEM_IMG_SIZE + 20;
        topHudY = height - 70;

        //create labels and buttons
        createBackground();
        createGemImg();
        createGemLbl();
        homeBtn = createHomeBtn(btn1X, BTN_Y, BTN_WIDTH, BTN_HEIGHT);
        unlockBtn = createUnlockBtn(btn4X, BTN_Y, BTN_WIDTH, BTN_HEIGHT);

        //add labels and buttons to stage
        addActor(background);
        addActor(gemImg);
        addActor(gemLbl);
        addActor(homeBtn);
        addActor(unlockBtn);
        addActor(cartTable);

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
    private Button createUnlockBtn(float x, float y, final float width, final float height) {
        final Button btn = new Button(new TextureRegionDrawable(game.atlas.findRegion("unlocked")));
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
                int selectedId = cart.getSelectedId();
                if (!Setting.isUnlocked(selectedId)) {
                    //if item is locked and have enough gem, unlock
                    if (cart.getItemPrice(selectedId) <= Setting.gemCount) {
                        Setting.setUnlocked(selectedId, true);
                        Setting.gemCount -= cart.getItemPrice(selectedId);
                        Setting.characterId = selectedId;
                        Setting.save();
                        cart.unlockIem(selectedId);
                        updateGemLbl(Setting.gemCount);
                        game.manager.get("audio/cash.mp3", Sound.class).play();
                    }
                    else {
                        game.manager.get("audio/error.mp3", Sound.class).play();
                    }
                }
            }
        });
        return btn;
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
    public void resetItemSelection() {
        cart.selectItem(Setting.characterId);
    }

    @Override
    public void dispose() {
        gemLblFont.dispose();
        super.dispose();
    }
    @Override
    protected void normalTransitionIn() {
        homeBtn.addAction(Actions.sequence(
                Actions.moveTo(btn1X, -BTN_HEIGHT),
                Actions.moveTo(btn1X, BTN_Y, GameScreen.TRANSITION_TIME)
        ));
        unlockBtn.addAction(Actions.sequence(
                Actions.moveTo(btn4X, -BTN_HEIGHT),
                Actions.moveTo(btn4X, BTN_Y, GameScreen.TRANSITION_TIME)
        ));
        cartTable.addAction(Actions.sequence(
                Actions.moveTo(0, height),
                Actions.moveTo(0, 0, GameScreen.TRANSITION_TIME)
        ));
    }
    @Override
    protected void normalTransitionOut() {
        homeBtn.addAction(Actions.moveTo(btn1X, -BTN_HEIGHT, GameScreen.TRANSITION_TIME));
        unlockBtn.addAction(Actions.moveTo(btn4X, -BTN_HEIGHT, GameScreen.TRANSITION_TIME));
        cartTable.addAction(Actions.moveTo(0, height, GameScreen.TRANSITION_TIME));
    }
}
