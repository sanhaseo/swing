package com.sanha.swinggame.world;

import com.badlogic.gdx.InputAdapter;
import com.sanha.swinggame.objects.Swinger;

public class WorldInputListener extends InputAdapter {
    private GameWorld world;

    public WorldInputListener(GameWorld world) {
        this.world = world;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Swinger.State swingerState = world.getSwinger().getState();
        if (swingerState == Swinger.State.HANGING) {
            world.pushSwinger();
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Swinger.State swingerState = world.getSwinger().getState();
        if (swingerState == Swinger.State.PUSHING) {
            world.jumpSwinger();
        }
        return true;
    }
}
