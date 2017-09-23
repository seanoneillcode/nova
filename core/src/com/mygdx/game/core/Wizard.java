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
import com.mygdx.game.WizardGame;


public class Wizard extends Enemy {

    private static final float SHOOT_COOLDOWN = 2.0f;
    private float shootCooldown;
    private Vector2 goalDirection;
    private WizardGame wizardGame;

    public Wizard(Texture texture, Vector2 pos, int health, float speed, String owner, WizardGame wizardGame) {
        super(texture,pos,health,speed, owner);
        shootCooldown = 0.4f;
        pickNewLocation(pos);
        this.wizardGame = wizardGame;
    }

    public void update(Vector2 player) {
        float delta = Gdx.graphics.getDeltaTime();

        if (shootCooldown < 0) {
            Vector2 pos = new Vector2(sprite.getX() + 4, sprite.getY() + 4);
            Vector2 dir = new Vector2(player.x - sprite.getX(), player.y - sprite.getY()).nor();
            Vector2 offset = dir.cpy().scl(20);
            wizardGame.createBullet(dir.scl(64), pos.add(offset).add(goalDirection), "enemy");
            shootCooldown = SHOOT_COOLDOWN;
        } else {
            shootCooldown = shootCooldown - delta;
        }
        pickNewLocation(player);
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
            float xpos = MathUtils.random(0, 256);
            float ypos = MathUtils.random(0, 180);
            Vector2 pos = new Vector2(xpos,ypos);
            goalDirection = pos.cpy();
            sprite.setPosition(goalDirection.x, goalDirection.y);
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
