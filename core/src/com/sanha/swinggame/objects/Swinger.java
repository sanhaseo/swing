package com.sanha.swinggame.objects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WeldJoint;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.sanha.swinggame.world.WorldContactListener;

public class Swinger {
    public enum State {HANGING, PUSHING, JUMPING}
    private enum Command {NONE, HANG, PUSH, JUMP, JUMP_AND_HANG}

//    public static final float WIDTH = 0.6f;
//    public static final float HEIGHT = 0.6f;
//    private static final float DENSITY = 0.5f;
    public static final float WIDTH = 0.65f;
    public static final float HEIGHT = 0.65f;
    private static final float DENSITY = 0.43f;
    private static final float INITIAL_PUSH_FORCE = 0;
    private static final float PUSH_FORCE = 3.6f;

    private Body body;
    private WeldJoint joint;

    private State state;
    private World world;
    private Rope rope;  //rope on which this swinger is hanging
    private Command nextCommand;    //command for next update

    public Swinger(World world, Rope rope) {
        this.world = world;
        this.rope = rope;

        state = State.HANGING;
        nextCommand = Command.NONE;

        defineBody();
        defineJoint(rope.getHandle());
        body.applyForceToCenter(INITIAL_PUSH_FORCE, 0, true);
    }

    public void update() {
        switch (nextCommand) {
            case HANG:
                executeHang();
                break;
            case PUSH:
                executePush();
                break;
            case JUMP:
                executeJump();
                break;
            case JUMP_AND_HANG:
                executeJumpAndHang();
                break;
        }
    }

    //***
    //push methods begin
    public void push() {
        if (state == State.HANGING && nextCommand != Command.JUMP_AND_HANG) {
            state = State.PUSHING;
            nextCommand = Command.PUSH;
        }
    }
    private void executePush() {
        body.applyForceToCenter(PUSH_FORCE, 0, true);
    }
    //push methods end
    //***

    //***
    //jump methods begin
    public void jump() {
        if (state != State.JUMPING) {
            nextCommand = Command.JUMP;
        }
    }
    private void executeJump() {
        //break joint
        if (joint != null) {
            world.destroyJoint(joint);
            joint = null;
            body.setLinearVelocity(body.getLinearVelocity().x * 1.5f, body.getLinearVelocity().y * 1.5f);
            state = State.JUMPING;
            nextCommand = Command.NONE;
        }
    }
    //jump methods end
    //***

    //***
    //hang methods begin
    public void hang(Rope rope) {
        if (nextCommand != Command.HANG) {
            this.rope = rope;
            nextCommand = Command.HANG;
            if (rope instanceof WeakRope) ((WeakRope) rope).startTimer();
            else if (rope instanceof WeakerRope) ((WeakerRope) rope).startTimer();
        }
    }
    private void executeHang() {
        defineJoint(rope.getHandle());
        state = State.HANGING;
        nextCommand = Command.NONE;
    }
    //hang methods end
    //***

    //***
    //jump-and-hang methods begin
    //jump from current rope and immediately hang on the other rope
    public void jumpAndHang(Rope rope) {
        if (nextCommand != Command.HANG) {
            this.rope = rope;
            nextCommand = Command.JUMP_AND_HANG;
            if (rope instanceof WeakRope) ((WeakRope) rope).startTimer();
            else if (rope instanceof WeakerRope) ((WeakerRope) rope).startTimer();
        }
    }
    private void executeJumpAndHang() {
        if (joint != null) {
            world.destroyJoint(joint);
            defineJoint(rope.getHandle());
            state = State.HANGING;
            nextCommand = Command.NONE;
        }
    }
    //jump-and-hang methods end
    //***

    //***
    //getters
    public Body getBody() {
        return body;
    }
    public float getY() {
        return body.getPosition().y;
    }
    public State getState() {
        return state;
    }
    public int ropeId() {
        return rope.getId();
    }
    //getters
    //***

    //***
    //body and joint definitions begin
    private void defineBody() {
        //shapes
        PolygonShape bodyShape = new PolygonShape();
        bodyShape.setAsBox(WIDTH/2, HEIGHT/2);
        //fixtures
        FixtureDef bodyFixtureDef = new FixtureDef();
        bodyFixtureDef.shape = bodyShape;
        bodyFixtureDef.density = DENSITY;
        bodyFixtureDef.filter.categoryBits = WorldContactListener.SWINGER_BIT;
        bodyFixtureDef.filter.maskBits =
                WorldContactListener.HANDLE_BIT |
                WorldContactListener.GEM_BIT;
        bodyFixtureDef.filter.groupIndex = 1;
        //body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(rope.getHandle().getWorldCenter());

        body = world.createBody(bodyDef);
        body.createFixture(bodyFixtureDef).setUserData(this);
    }
    private void defineJoint(Body handle) {
        WeldJointDef weldJointDef = new WeldJointDef();
        weldJointDef.bodyA = body;
        weldJointDef.bodyB = handle;
        weldJointDef.localAnchorA.set(0, 0);
        weldJointDef.localAnchorB.set(0, 0);
        weldJointDef.referenceAngle = handle.getAngle() - body.getAngle();
        joint = (WeldJoint) world.createJoint(weldJointDef);
    }
    //body and joint definitions end
    //***

    //!!! TEST !!!
//    private void printState(String method) {
//        switch (state) {
//            case HANGING:
//                Gdx.app.log("HANGING", method);
//                break;
//            case PUSHING:
//                Gdx.app.log("PUSHING", method);
//                break;
//            case JUMPING:
//                Gdx.app.log("JUMPING", method);
//                break;
//        }
//    }
}
