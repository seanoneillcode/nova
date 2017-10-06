package com.mygdx.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.WizardGame;
import com.badlogic.gdx.math.Vector2;

public interface Level {

    public void load(WizardGame wizardGame);
    public boolean isLoaded();
    public boolean isDone();
    public void reset();
    public void update(WizardGame wizardGame);
    public void draw(SpriteBatch batch);
    public Vector2 getStartPos();
}
