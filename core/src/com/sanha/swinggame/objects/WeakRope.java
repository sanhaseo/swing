package com.sanha.swinggame.objects;

import com.badlogic.gdx.physics.box2d.World;

public class WeakRope extends Rope {
    private static final float CUT_DELAY = 1.5f;

    private float timer;
    private boolean timerRunning;

    public WeakRope(World world, float x, float y, int numSegments, int id) {
        super(world, x, y, numSegments, id);

        timer = 0;
        timerRunning = false;
    }

    void startTimer() {
        timerRunning = true;
    }

    public void update(float dt) {
        if (timer > CUT_DELAY && pivotJoint != null) {
            world.destroyJoint(pivotJoint);
            pivotJoint = null;
        }
        else if (timerRunning) {
            timer += dt;
        }
    }
}
