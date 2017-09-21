package com.mygdx.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.core.Entity;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.core.BulletController;

public class Enemy implements Entity {
    public Sprite sprite;
    public int health;
    public float speed;
    private Vector2 mov;

    public Enemy(Texture texture, Vector2 pos, int health, float speed) {
        this.sprite = new Sprite(texture);
        sprite.setPosition(pos.x, pos.y);
        this.health = health;
        this.speed = speed;
        mov = new Vector2();
    }

    public boolean shouldRemove() {
        return health < 1;
    }

    public void update(Vector2 player, BulletController bulletController) {
        Vector2 dir = new Vector2(player.x - sprite.getX(), player.y - sprite.getY()).nor();
        float delta = Gdx.graphics.getDeltaTime();
        mov = new Vector2((delta * dir.x * speed), (delta * dir.y * speed));
        float newX = sprite.getX() + mov.x;
        float newY = sprite.getY() + mov.y;
        sprite.setPosition(newX, newY);
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public void takeDamage(int damage) {
        health = health - 1;
    }

    public Rectangle getBoundingRectangle() {
        return sprite.getBoundingRectangle();
    }

    public void handleBlock(Vector2 push) {
        float newX = sprite.getX() - mov.x - mov.x + push.x;
        float newY = sprite.getY() - mov.y - mov.y + push.y;
        sprite.setPosition(newX, newY);
    }
}
