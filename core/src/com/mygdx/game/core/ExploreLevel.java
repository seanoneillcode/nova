package com.mygdx.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.WizardGame;
import com.mygdx.game.core.Level;
import com.badlogic.gdx.math.Rectangle;

public class ExploreLevel implements Level {
    
    private boolean isDone;
    private boolean isLoaded;
    private TextureRegion background;
    private String backgroundImage;
    private Rectangle goalRect;
    private Vector2 startPos;

    public ExploreLevel(String backgroundImage, Vector2 startPos, Rectangle goalRect) {
        this.backgroundImage = backgroundImage;
        this.goalRect = goalRect;
        this.isDone = false;
        this.startPos = startPos;
        reset();
    }

    public void load(WizardGame wizardGame) {
        background = new TextureRegion(new Texture(this.backgroundImage));
        isLoaded = true;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public boolean isDone() {
        return isDone;
    }

    public void reset() {
        background = null;
        isDone = false;
        isLoaded= false;
    }

    public void update(WizardGame wizardGame) {
        if (goalRect.overlaps(wizardGame.getPlayerRect())) {
            isDone = true;
        }
    }

    public void draw(SpriteBatch batch) {
        if (background != null) {
            batch.draw(background, 0, 0);
        }
    }

    public Vector2 getStartPos() {
        return startPos;
    }
}
