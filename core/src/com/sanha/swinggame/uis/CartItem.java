package com.sanha.swinggame.uis;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.sanha.swinggame.tools.Setting;

class CartItem extends Group {
    private static final float WIDTH = 100;
    private static final float HEIGHT = 120;
    private static final float CHAR_IMG_SIZE = 80; //character image size
    private static final float CHECKED_CHAR_IMG_SIZE = 100; //selected character image size
    private static final float GEM_IMG_SIZE = 25;
    private static final float LOCK_IMG_SIZE = 80;
    private static final int GEM_LBL_FONT_SIZE = 30;

    private CartUI cartUI;
    private int price;
    private int id;

    //labels and images
    private Image charImg;
    private Image gemImg;
    private Image lockImg;
    private Label gemLbl;

    //fonts
    private BitmapFont gemLblFont;

    CartItem(final CartUI cartUI, final int id, int price) {
        this.cartUI = cartUI;
        this.id = id;
        this.price = price;

        charImg = new Image(new TextureRegionDrawable(cartUI.game.atlas.findRegion("char"+id)));
        gemImg = new Image(new TextureRegionDrawable(cartUI.game.atlas.findRegion("star_gold")));
        lockImg = new Image(new TextureRegionDrawable(cartUI.game.atlas.findRegion("locked")));

        gemLblFont = cartUI.generateLblFont(GEM_LBL_FONT_SIZE);

        createGroup();
        setSize(WIDTH, HEIGHT);

        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                cartUI.cart.selectItem(id);
                if (Setting.isUnlocked(id)) {
                    Setting.characterId = id;
                    Setting.save();
                }
            }
        });
    }

    public int getId() {
        return id;
    }
    int getPrice() {
        return price;
    }

    void select() {
        charImg.setBounds(
                (WIDTH - CHECKED_CHAR_IMG_SIZE) / 2,
                GEM_IMG_SIZE + (HEIGHT - CHECKED_CHAR_IMG_SIZE) / 2,
                CHECKED_CHAR_IMG_SIZE,
                CHECKED_CHAR_IMG_SIZE
        );
    }
    void unselect() {
        charImg.setBounds(
                (WIDTH - CHAR_IMG_SIZE) / 2,
                GEM_IMG_SIZE + (HEIGHT - CHAR_IMG_SIZE) / 2,
                CHAR_IMG_SIZE,
                CHAR_IMG_SIZE
        );
    }
    void unlock() {
        gemImg.setVisible(false);
        gemLbl.setVisible(false);
        lockImg.setVisible(false);
    }

    private void createGroup() {
        //character image
        charImg.setBounds(
                (WIDTH - CHAR_IMG_SIZE) / 2,
                GEM_IMG_SIZE + (HEIGHT - CHAR_IMG_SIZE) / 2,
                CHAR_IMG_SIZE,
                CHAR_IMG_SIZE
        );
        //gem image
        gemImg.setBounds(0, 0, GEM_IMG_SIZE, GEM_IMG_SIZE);
        //gem label
        createGemLbl();
        gemLbl.setPosition(GEM_IMG_SIZE + 20, 0);
        //lock image
        lockImg.setBounds(
                (WIDTH - LOCK_IMG_SIZE) / 2,
                GEM_IMG_SIZE + (HEIGHT - LOCK_IMG_SIZE) / 2,
                LOCK_IMG_SIZE,
                LOCK_IMG_SIZE
        );

        addActor(charImg);
        addActor(gemImg);
        addActor(gemLbl);
        addActor(lockImg);
    }
    private void createGemLbl() {
        String text = price+"";
        cartUI.textLayout.setText(gemLblFont, text);
        gemLbl = cartUI.createLbl(
                text,
                0,
                0,
                cartUI.textLayout.width,
                cartUI.textLayout.height,
                gemLblFont,
                new Color(1, 220f / 255, 60f/ 255, 1)
        );
    }
}
