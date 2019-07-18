package com.sanha.swinggame.objects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.badlogic.gdx.utils.Array;
import com.sanha.swinggame.world.WorldContactListener;

import java.util.Random;

public class Rope {
    public static final float SEGMENT_LENGTH = 0.4f;
    public static final float SEGMENT_WIDTH = 0.05f;
    public static final float PIVOT_RADIUS = 0.1f;
    public static final float HANDLE_RADIUS = 0.25f;
    private static final float ROPE_DENSITY = 2;
    private static final float HANDLE_DENSITY = 0.7f;
    private static final float MAX_INITIAL_FORCE = 70;

    private Body pivot;
    private Array<Body> segments;
    private Body handle;
    RevoluteJoint pivotJoint;
    private Array<RevoluteJoint> joints;
    private WeldJoint handleJoint;

    World world;
    private float x;
    private float y;
    private int numSegments;
    private int id;

    public Rope(World world, float x, float y, int numSegments, int id) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.numSegments = numSegments;
        this.id = id;

        definePivot();
        defineSegments();
        defineHandle();
        definePivotJoint();
        defineJoints();
        defineHandleJoint();

        applyRandomSwing();
    }

    public void destroy() {
        if (pivotJoint != null) world.destroyJoint(pivotJoint);
        world.destroyJoint(handleJoint);
        for (RevoluteJoint joint : joints) {
            world.destroyJoint(joint);
        }

        world.destroyBody(pivot);
        world.destroyBody(handle);
        for (Body segment : segments) {
            world.destroyBody(segment);
        }
    }

    private void applyRandomSwing() {
        Random rand = new Random();
        float force = (-1 + 2 * rand.nextFloat()) * MAX_INITIAL_FORCE;
        handle.applyForceToCenter(force, 0, true);
    }

    //***
    //getters
    public int getId() {
        return id;
    }
    public Body getPivot() {
        return pivot;
    }
    public Array<Body> getSegments() {
        return segments;
    }
    public Body getHandle() {
        return handle;
    }
    //getters
    //***

    //***
    //rope body definitions begin
    private void definePivot() {
        //shape
        CircleShape shape = new CircleShape();
        shape.setRadius(PIVOT_RADIUS);
        //fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.maskBits = 0;
        //body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(new Vector2(x, y));

        pivot = world.createBody(bodyDef);
        pivot.createFixture(fixtureDef);
    }
    private void defineSegments() {
        segments = new Array<Body>(numSegments);

        for (int i=0; i<numSegments; i++) {
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(SEGMENT_WIDTH/2, SEGMENT_LENGTH/2);
            //fixture
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.density = ROPE_DENSITY;
            fixtureDef.filter.maskBits = 0;
            //body
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            float posY = y - SEGMENT_LENGTH/2 - i * SEGMENT_LENGTH;
            bodyDef.position.set(new Vector2(x, posY));

            Body segment = world.createBody(bodyDef);
            segment.createFixture(fixtureDef);

            segments.add(segment);
        }
    }
    private void defineHandle() {
        CircleShape shape = new CircleShape();
        shape.setRadius(HANDLE_RADIUS);
        //fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = HANDLE_DENSITY;
        fixtureDef.filter.categoryBits = WorldContactListener.HANDLE_BIT;
        fixtureDef.filter.maskBits = WorldContactListener.SWINGER_BIT;
        fixtureDef.isSensor = true;
        //body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        Vector2 pos = segments.get(numSegments-1).getWorldCenter().add(0, -SEGMENT_LENGTH/2);
        bodyDef.position.set(pos);

        handle = world.createBody(bodyDef);
        handle.createFixture(fixtureDef).setUserData(this);
    }
    private void definePivotJoint() {
        RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
        revoluteJointDef.initialize(pivot, segments.first(), pivot.getWorldCenter());
        pivotJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
    }
    private void defineJoints() {
        joints = new Array<RevoluteJoint>(numSegments-1);
        for (int i=0; i<numSegments-1; i++) {
            RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
            revoluteJointDef.initialize(segments.get(i), segments.get(i+1), segments.get(i).getWorldCenter().add(0, -SEGMENT_LENGTH/2));
            revoluteJointDef.enableLimit = true;
            revoluteJointDef.upperAngle = 20 * MathUtils.degreesToRadians;
            revoluteJointDef.lowerAngle = -20 * MathUtils.degreesToRadians;
            RevoluteJoint joint = (RevoluteJoint) world.createJoint(revoluteJointDef);
            joints.add(joint);
        }
    }
    private void defineHandleJoint() {
        WeldJointDef weldJointDef = new WeldJointDef();
        weldJointDef.initialize(segments.get(numSegments-1), handle, handle.getWorldCenter());
        handleJoint = (WeldJoint) world.createJoint(weldJointDef);
    }
    //rope body definitions end
    //***
}
