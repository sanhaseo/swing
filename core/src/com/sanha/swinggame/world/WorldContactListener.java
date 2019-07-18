package com.sanha.swinggame.world;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.sanha.swinggame.objects.Gem;
import com.sanha.swinggame.objects.Rope;
import com.sanha.swinggame.objects.Swinger;

public class WorldContactListener implements ContactListener {
    //fixture category bits
    public static final short SWINGER_BIT = 1;
    public static final short HANDLE_BIT = 2;
    public static final short GEM_BIT = 4;

    private GameWorld world;

    WorldContactListener(GameWorld world) {
        this.world = world;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture A = contact.getFixtureA();
        Fixture B = contact.getFixtureB();

        int contactDef = A.getFilterData().categoryBits | B.getFilterData().categoryBits;

        switch (contactDef) {
            //if swinger touches handle, hang on the handle
            case SWINGER_BIT | HANDLE_BIT:
                if (A.getFilterData().categoryBits == SWINGER_BIT) {
                    swingerHandleBeginContact(A, B);
                }
                else if (B.getFilterData().categoryBits == SWINGER_BIT) {
                    swingerHandleBeginContact(B, A);
                }
                break;
            //if swinger touches gem, consume gem
            case SWINGER_BIT | GEM_BIT:
                if (A.getFilterData().categoryBits == SWINGER_BIT) {
                    world.consumeGem((Gem) B.getUserData());
                }
                else if (B.getFilterData().categoryBits == SWINGER_BIT) {
                    world.consumeGem((Gem) A.getUserData());
                }
                break;
        }
    }

    private void swingerHandleBeginContact(Fixture swingerFixture, Fixture handleFixture) {
        Swinger swinger = (Swinger) swingerFixture.getUserData();
        Rope rope = (Rope) handleFixture.getUserData();
        if (rope.getId() > swinger.ropeId()) {  //make sure target rope id is greater than current rope id
            if (swinger.getState() == Swinger.State.JUMPING) {
                world.hangSwinger(rope);
            }
            else {
                world.jumpAndHangSwinger(rope);
            }
        }
    }

    //***
    //unused methods
    @Override
    public void endContact(Contact contact) {

    }
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
    //unused methods
    //***
}
