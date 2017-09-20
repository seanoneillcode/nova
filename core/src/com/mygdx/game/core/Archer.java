package com.mygdx.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.core.Entity;
import com.mygdx.game.core.Enemy;
import com.badlogic.gdx.math.MathUtils;


public class Archer extends Enemy {

    private static final float SHOOT_COOLDOWN = 2.0f;
    private float shootCooldown;
    private Vector2 goalDirection;

    public Archer(Texture texture, Vector2 pos, int health, float speed) {
        super(texture,pos,health,speed);
        shootCooldown = 0.4f;
        pickNewLocation(pos);
    }

    public void update(Vector2 player, BulletController bulletController) {
        float delta = Gdx.graphics.getDeltaTime();

        if (shootCooldown < 0) {
            Vector2 pos = new Vector2(sprite.getX() + 6, sprite.getY() + 6);
            Vector2 dir = new Vector2(player.x - sprite.getX(), player.y - sprite.getY()).nor();
            Vector2 offset = dir.cpy().scl(16);
            bulletController.createBullet(dir.scl(64), pos.add(offset).add(goalDirection));
            shootCooldown = SHOOT_COOLDOWN;
            pickNewLocation(player);
        } else {
            shootCooldown = shootCooldown - delta;
        }
        if (shootCooldown < (SHOOT_COOLDOWN * 0.6f)) {
            float newX = sprite.getX() + (delta * goalDirection.x * speed);
            float newY = sprite.getY() + (delta * goalDirection.y * speed);
            
            newX = MathUtils.clamp(newX, 0, 240);
            newY = MathUtils.clamp(newY, 0, 180);

            sprite.setPosition(newX, newY);
        }    
    }

    private void pickNewLocation(Vector2 player) {
        // Vector2 pos = new Vector2(MathUtils.random(16, 224), MathUtils.random(16, 224));
        float distance = player.dst2(new Vector2(sprite.getX(),sprite.getY()));
        if (distance < 4800) {
            Vector2 dir = new Vector2(player.x - sprite.getX(), player.y - sprite.getY()).nor();
            dir.x = dir.x * -1;
            dir.y = dir.y * -1;
            goalDirection = dir.cpy();
        } else {
            if (distance > 17000) {
                Vector2 dir = new Vector2(player.x - sprite.getX(), player.y - sprite.getY()).nor();
                goalDirection = dir.cpy();
            } else {
                goalDirection = new Vector2();                
            }
        }
    }

}
