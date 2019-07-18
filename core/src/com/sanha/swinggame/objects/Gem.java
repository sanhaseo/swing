package com.sanha.swinggame.objects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.sanha.swinggame.world.GameWorld;
import com.sanha.swinggame.world.WorldContactListener;

import java.util.Random;

public class Gem {
    public static final float WIDTH = 0.3f;
    public static final float HEIGHT = 0.3f;
    private static final float MIN_Y = GameWorld.HEIGHT * 0.35f;
    private static final float MAX_Y = GameWorld.HEIGHT * 0.85f;

    private Body body;
    private World world;
    private float x;
    private float y;

    private boolean consumed;

    public Gem(World world, float x) {
        this.world = world;
        this.x = x;
        Random random = new Random();
        y = MIN_Y + (MAX_Y - MIN_Y) * random.nextFloat();
        consumed = false;

        defineBody();
    }

    public void destroy() {
        world.destroyBody(body);
    }

    public Body getBody() {
        return body;
    }
    public boolean isConsumed() {
        return consumed;
    }
    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }

    private void defineBody() {
        //shapes
        PolygonShape bodyShape = new PolygonShape();
        bodyShape.setAsBox(WIDTH/2, HEIGHT/2);
        //fixtures
        FixtureDef bodyFixtureDef = new FixtureDef();
        bodyFixtureDef.shape = bodyShape;
        bodyFixtureDef.filter.categoryBits = WorldContactListener.GEM_BIT;
        bodyFixtureDef.filter.maskBits = WorldContactListener.SWINGER_BIT;
        bodyFixtureDef.isSensor = true;
        //body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);

        body = world.createBody(bodyDef);
        body.createFixture(bodyFixtureDef).setUserData(this);
    }
}
