package com.mygdx.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.WizardGame;
import com.mygdx.game.core.Level;

public class WaveLevel implements Level {
    
    private boolean isDone;
    private boolean isLoaded;
    private TextureRegion background;
    private String backgroundImage;
    private int numSkel;
    private int numArch;
    private int numWiz;
    private int numEye;
    private int numChar;

    public WaveLevel(String backgroundImage, int numSkel, int numArch, int numWiz, int numEye, int numChar) {
        this.backgroundImage = backgroundImage;
        this.numSkel = numSkel;
        this.numArch = numArch;
        this.numWiz = numWiz;
        this.numEye = numEye;
        this.numChar = numChar;
        reset();
    }

    public void load(WizardGame wizardGame) {
        background = new TextureRegion(new Texture(this.backgroundImage));
        isLoaded = true;
        wizardGame.addWaveOfSkeletons(numSkel,numArch,numWiz,numEye,numChar);
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
        isDone = wizardGame.areEnemiesDead();
    }

    public void draw(SpriteBatch batch) {
        if (background != null) {
            batch.draw(background, 0, 0);
        }
    }
}
