package com.mygdx.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.WizardGame;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.math.Polygon;

public class Level {
    
    private boolean isDone;
    private boolean isLoaded;
    private TextureRegion background;
    private String backgroundImage;
    public Rectangle goalRect;
    public Vector2 startPos;
    public Integer skeletons;
    public Integer archers;
    public Integer wizards;
    public Integer eyes;
    public Integer chargers;
    public Rectangle boundry;
    public Map map;

    public Level(String backgroundImage) {
        this.backgroundImage = backgroundImage;
        this.isDone = false;
        this.map = new TmxMapLoader().load("map01.tmx");
        reset();
    }

    public void load(WizardGame wizardGame) {
        background = new TextureRegion(new Texture(this.backgroundImage));
        if (boundry == null) {
            boundry = new Rectangle(0,0,background.getRegionWidth(), background.getRegionHeight());
        }
        wizardGame.addWaveOfSkeletons(skeletons,archers,wizards,eyes,chargers);
        MapLayer layer = map.getLayers().get("paths");
        MapObjects mapObjects = layer.getObjects();
        MapObject pathObject = mapObjects.get("path");
        PolygonMapObject polygonPath = (PolygonMapObject) pathObject;
        // Polygon poly = polygonPath.getPolygon();
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
        if (wizardGame.areEnemiesDead()) {
            isDone = true;
        }
        if (goalRect != null) {
            isDone = isDone && goalRect.overlaps(wizardGame.getPlayerRect());
        }
    }

    public void draw(SpriteBatch batch) {
        if (background != null) {
            batch.draw(background, 0, 0);
        }
    }

    public void dispose () {
        if (map != null) {
            map.dispose();
        }
    }

    public Vector2 getStartPos() {
        return startPos;
    }

    public static class Builder {
        
        String backgroundImage;
        Vector2 startPos;
        Rectangle goalRect;
        Integer skeletons = 0;
        Integer archers = 0;
        Integer wizards = 0;
        Integer eyes = 0;
        Integer chargers = 0;
        Rectangle boundry;

        public Builder(String backgroundImage) {
            this.backgroundImage = backgroundImage;
        }

        public Builder startPos(Vector2 startPos) {
            this.startPos = startPos;
            return this;
        }

        public Builder goalRect(Rectangle goalRect) {
            this.goalRect = goalRect;
            return this;
        }

        public Builder skeletons(Integer skeletons) {
            this.skeletons = skeletons;
            return this;
        }

        public Builder archers(Integer archers) {
            this.archers = archers;
            return this;
        }

        public Builder wizards(Integer wizards) {
            this.wizards = wizards;
            return this;
        }

        public Builder eyes(Integer eyes) {
            this.eyes = eyes;
            return this;
        }

        public Builder chargers(Integer chargers) {
            this.chargers = chargers;
            return this;
        }

        public Builder boundry(Rectangle boundry) {
            this.boundry = boundry;
            return this;
        }

        public Level build() {
            Level level = new Level(this.backgroundImage);
            if (goalRect != null) {
                level.goalRect = this.goalRect;
            }
            if (startPos != null) {
                level.startPos = this.startPos;
            }
            if (boundry != null) {
                level.boundry = this.boundry;
            }
            level.skeletons = this.skeletons;
            level.archers = this.archers;
            level.wizards = this.wizards;
            level.eyes = this.eyes;
            level.chargers = this.chargers;
            return level;
        }
    }
}
