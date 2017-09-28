package com.mygdx.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.core.Pwned;


public class Bullet implements Pwned {

    public float ttl;
    public Sprite sprite;
    public Vector2 dir;
    public String owner;
    private float rotation;

    public Bullet(Texture texture, Vector2 dir, Vector2 pos, String owner) {
        sprite = new Sprite(texture);
        sprite.setPosition(pos.x, pos.y);
        ttl = 2f;
        this.dir = dir;
        this.owner = owner;
        this.rotation = 0;
    }

    public boolean shouldRemove() {
        return ttl < 0;
    }

    public void update() {
        float delta = Gdx.graphics.getDeltaTime();
        ttl = ttl - delta;
        float newX = sprite.getX() + (delta * dir.x);
        float newY = sprite.getY() + (delta * dir.y);
        sprite.setPosition(newX, newY);
        rotation = rotation + 2f;
        if (rotation > 360f) {
            rotation = rotation - 360f;
        }
        sprite.rotate(rotation);
    }

    public String getOwner() {
        return owner;
    }
    public boolean isOwner(String other) {
        return this.owner.equals(other);
    }

}
