package com.sanha.swinggame.uis;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sanha.swinggame.SwingGame;
import com.sanha.swinggame.screens.GameScreen;

public abstract class AbstractUI extends Stage {
    public enum Transition {NORMAL, FADE, INSTANT}

    private static final String FONT = "fonts/AGENCYB.TTF";
    static final float BTN_WIDTH = 100;
    static final float BTN_HEIGHT = 100;
    static final float BTN_Y = 100;
    static final float BTN_PRESSED_SCALE = 1.1f;

    float width;
    float height;

    //button parameters
    float btn1X;
    float btn4X;

    //font variables
    private FreeTypeFontGenerator generator;
    GlyphLayout textLayout;

    SwingGame game;
    GameScreen screen;

    AbstractUI(GameScreen screen, Viewport viewport, SwingGame game) {
        super(viewport, game.batch);
        this.screen = screen;
        this.game = game;

        initVariables();
    }

    public void transitionIn(Transition transition) {
        switch (transition) {
            case NORMAL:
                normalTransitionIn();
                addAction(Actions.alpha(1));
                break;
            case FADE:
                normalTransitionIn();
                addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(GameScreen.TRANSITION_TIME)));
                break;
            case INSTANT:
                addAction(Actions.alpha(1));
                break;
        }
    }
    public void transitionOut(Transition transition) {
        switch (transition) {
            case NORMAL:
                normalTransitionOut();
                break;
            case FADE:
                normalTransitionOut();
                addAction(Actions.fadeOut(GameScreen.TRANSITION_TIME));
                break;
            case INSTANT:
                break;
        }
    }
    BitmapFont generateLblFont(int fontSize) {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = fontSize;
        parameter.borderColor = Color.BLACK;
        parameter.borderWidth = 2;
        return generator.generateFont(parameter);
    }
    Label createLbl(String text, float x, float y, float width, float height, BitmapFont font, Color color) {
        Label label = new Label(text, new Label.LabelStyle(font, color));
        label.setBounds(x, y, width, height);
        return label;
    }

    protected abstract void normalTransitionIn();
    protected abstract void normalTransitionOut();

    public void dispose() {
        generator.dispose();
        super.dispose();
    }

    private void initVariables() {
        width = SwingGame.WIDTH;
        height = SwingGame.HEIGHT;

        generator = new FreeTypeFontGenerator(Gdx.files.internal(FONT));
        textLayout = new GlyphLayout();

        int hDiv = 4;   //horizontal division
        btn1X = 0.5f * width / hDiv - BTN_WIDTH / 2;
        btn4X = 3.5f * width / hDiv - BTN_WIDTH / 2;
    }
}
