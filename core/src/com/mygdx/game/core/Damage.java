package com.mygdx.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Damage {
    public Sprite sprite;
    private float ttl;

    public Damage(Texture texture, Vector2 pos, float ttl) {
        this.sprite = new Sprite(texture);
        sprite.setPosition(pos.x, pos.y);
        this.ttl = ttl;
    }

    public boolean shouldRemove() {
        return ttl < 0;
    }

    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        ttl = ttl - delta;
    }

    public void draw(SpriteBatch batch) {
        sprite.setColor(1, 1, 1, (ttl));
        sprite.draw(batch);
    }

}
