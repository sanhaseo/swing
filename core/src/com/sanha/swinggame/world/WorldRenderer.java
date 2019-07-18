package com.sanha.swinggame.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sanha.swinggame.SwingGame;
import com.sanha.swinggame.objects.Gem;
import com.sanha.swinggame.objects.Rope;
import com.sanha.swinggame.objects.Swinger;
import com.sanha.swinggame.objects.WeakRope;
import com.sanha.swinggame.objects.WeakerRope;
import com.sanha.swinggame.tools.Setting;

import java.util.Random;

import box2dLight.PointLight;
import box2dLight.RayHandler;

public class WorldRenderer {
    private SwingGame game;
    private GameWorld world;
    private OrthographicCamera cam;
    private Viewport viewport;

    //particle and light effect variables
    private ParticleEffectPool gemEffectPool;
    private Array<ParticleEffectPool.PooledEffect> gemEffects;
    private RayHandler rayHandler;
    private PointLight pointLight1;
    private PointLight pointLight2;

    //!!! TEST !!!
    //Box2DDebugRenderer renderer;

    public WorldRenderer(SwingGame game, GameWorld world) {
        this.game = game;
        cam = new OrthographicCamera();
        viewport = new FitViewport(GameWorld.WIDTH, GameWorld.HEIGHT, cam);

        //particle effect
        gemEffects = new Array<ParticleEffectPool.PooledEffect>();
        ParticleEffect gemEffect = new ParticleEffect();
        gemEffect.load(Gdx.files.internal("gem_particles.p"), Gdx.files.internal(""));
        gemEffectPool = new ParticleEffectPool(gemEffect, 1, 5);

        setWorld(world);

        //!!! TEST !!!
        //renderer = new Box2DDebugRenderer();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void render(float dt) {
        float currentPositoin = cam.position.x - viewport.getWorldWidth() / 2;
        float targetPosition = world.getCurrentWorldPosition();
        if (targetPosition > currentPositoin) {
            cam.position.x += (targetPosition - currentPositoin) / 10;
        }
        cam.update();
        game.batch.setProjectionMatrix(cam.combined);
        game.batch.begin();
        renderRopes();
        renderGems();
        renderSwinger();
        renderGemParticles(dt);
        game.batch.end();

        updateAndRenderLights();

        //!!! TEST !!!
        //renderer.render(world.getWorld(), cam.combined);
    }

    public void setWorld(GameWorld world) {
        this.world = world;
        cam.position.set(viewport.getWorldWidth()/2, viewport.getWorldHeight()/2, 0);
        setLights(world);
    }

    private void renderRopes() {
        Array<Rope> ropes = world.getRopes();
        for (Rope rope : ropes) {
            renderRope(rope);
        }
    }
    private void renderRope(Rope rope) {
        //textures
        TextureRegion pivotTexture = game.atlas.findRegion("grey_tickGrey");
        TextureRegion segmentTexture = game.atlas.findRegion("grey_sliderVertical");
        TextureRegion handleTexture;
        if (rope instanceof WeakRope) {
            handleTexture = game.atlas.findRegion("yellow_circle");
        }
        else if (rope instanceof WeakerRope) {
            handleTexture = game.atlas.findRegion("red_circle");
        }
        else {
            handleTexture = game.atlas.findRegion("grey_circle");
        }

        //render segments
        Array<Body> segments = rope.getSegments();
        for (Body segment : segments) {
            game.batch.draw(
                    segmentTexture,                                     //region
                    segment.getPosition().x - Rope.SEGMENT_WIDTH / 2,   //x
                    segment.getPosition().y - Rope.SEGMENT_LENGTH / 2,  //y
                    Rope.SEGMENT_WIDTH / 2,                             //originX
                    Rope.SEGMENT_LENGTH / 2,                            //originY
                    Rope.SEGMENT_WIDTH,                                 //width
                    Rope.SEGMENT_LENGTH,                                //height
                    1,                                                  //scaleX
                    1,                                                  //scaleY
                    segment.getAngle() * MathUtils.radiansToDegrees     //rotation
            );
        }

        //render pivot
        game.batch.draw(
                pivotTexture,
                rope.getPivot().getPosition().x - Rope.PIVOT_RADIUS,
                rope.getPivot().getPosition().y - Rope.PIVOT_RADIUS,
                Rope.PIVOT_RADIUS * 2,
                Rope.PIVOT_RADIUS * 2
        );

        //render handle
        game.batch.draw(
                handleTexture,
                rope.getHandle().getPosition().x - Rope.HANDLE_RADIUS,
                rope.getHandle().getPosition().y - Rope.HANDLE_RADIUS,
                Rope.HANDLE_RADIUS * 2,
                Rope.HANDLE_RADIUS * 2
        );
    }
    private void renderSwinger() {
        TextureRegion region = game.atlas.findRegion("char"+ Setting.characterId);
        Swinger swinger = world.getSwinger();
        game.batch.draw(
                region,                                                     //region
                swinger.getBody().getPosition().x - Swinger.WIDTH / 2,      //x
                swinger.getBody().getPosition().y - Swinger.HEIGHT / 2,     //y
                Swinger.WIDTH / 2,                                          //originX
                Swinger.HEIGHT / 2,                                         //originY
                Swinger.WIDTH,                                              //width
                Swinger.HEIGHT,                                             //height
                1,                                                          //scaleX
                1,                                                          //scaleY
                swinger.getBody().getAngle() * MathUtils.radiansToDegrees   //rotation
        );
    }
    private void renderGems() {
        Array<Gem> gems = world.getGems();
        for (int i=0; i<gems.size; i++) {
            if (gems.get(i) != null) {
                renderGem(gems.get(i));
            }
        }
    }
    private void renderGem(Gem gem) {
        TextureRegion region = game.atlas.findRegion("star_gold");
        game.batch.draw(
                region,
                gem.getBody().getPosition().x - Gem.WIDTH / 2,
                gem.getBody().getPosition().y - Gem.HEIGHT / 2,
                Gem.WIDTH,
                Gem.HEIGHT
        );
    }
    private void renderGemParticles(float dt) {
        Vector2 pos = world.getGemConsumePosition();
        if (pos != null) {
            ParticleEffectPool.PooledEffect effect = gemEffectPool.obtain();
            effect.setPosition(pos.x, pos.y);
            gemEffects.add(effect);
            world.resetGemConsumePosition();
        }

        for (int i=gemEffects.size-1; i>=0; i--) {
            ParticleEffectPool.PooledEffect effect = gemEffects.get(i);
            effect.draw(game.batch, dt);
            if (effect.isComplete()) {
                effect.free();
                gemEffects.removeIndex(i);
            }
        }
    }
    private void updateAndRenderLights() {
        rayHandler.setCombinedMatrix(cam);
        //parallax effect
        float light1x = cam.position.x - (cam.position.x + cam.viewportWidth/2)* 0.5f % cam.viewportWidth;
        float light2x = light1x + cam.viewportWidth;
        pointLight1.setPosition(light1x, cam.viewportHeight - 0.5f);
        pointLight2.setPosition(light2x, cam.viewportHeight - 0.5f);
        rayHandler.updateAndRender();
    }

    private void setLights(GameWorld world) {
        if (rayHandler != null) rayHandler.dispose();
        rayHandler = new RayHandler(world.getWorld());
        rayHandler.setAmbientLight(0.8f);

        //set random light color
        Random rand = new Random();
        int x = rand.nextInt(5);
        Color color;
        switch (x) {
            case 0:
                color = Color.SKY;
                break;
            case 1:
                color = Color.TAN;
                break;
            case 2:
                color = Color.OLIVE;
                break;
            case 3:
                color = Color.SALMON;
                break;
            case 4:
            default:
                color = Color.WHITE;
                break;
        }
        pointLight1 = new PointLight(rayHandler, 150, color, 5.5f, 0, 0);
        pointLight2 = new PointLight(rayHandler, 150, color, 5.5f, 0, 0);
    }

    public void dispose() {
        disposeParticleEffects();
        rayHandler.dispose();
    }
    private void disposeParticleEffects() {
        for (int i=gemEffects.size-1; i>=0; i--) {
            gemEffects.get(i).free();
        }
        gemEffects.clear();
    }
}
