package com.mygdx.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.core.Entity;
import com.mygdx.game.core.Enemy;


public class Archer extends Enemy {

    private static final float SHOOT_COOLDOWN = 2.0f;
    private float shootCooldown;

    public Archer(Texture texture, Vector2 pos, int health, float speed) {
        super(texture,pos,health,speed);
        shootCooldown = SHOOT_COOLDOWN;
    }

    public void update(Vector2 player, BulletController bulletController) {
        Vector2 dir = new Vector2(player.x - sprite.getX(), player.y - sprite.getY()).nor();
        float delta = Gdx.graphics.getDeltaTime();

        Vector2 pos = new Vector2(sprite.getX(), sprite.getY());
        Vector2 offset = dir.cpy().scl(24);

        if (shootCooldown < 0) {
            bulletController.createBullet(dir.scl(64), pos.add(offset));
            shootCooldown = SHOOT_COOLDOWN;
        } else {
            shootCooldown = shootCooldown - delta;
        }
        //float newX = sprite.getX() + (delta * dir.x * speed);
        //float newY = sprite.getY() + (delta * dir.y * speed);
        
        //sprite.setPosition(newX, newY);
    }

}
