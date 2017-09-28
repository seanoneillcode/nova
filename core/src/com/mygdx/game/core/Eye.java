package com.mygdx.game.core;

import java.util.List;

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


public class Eye extends Enemy {

    private static final float SHOOT_COOLDOWN = 2.0f;
    private float shootCooldown;
    private Vector2 goalDirection;
    private WizardGame wizardGame;
    private float portalCooldown;

    public Eye(Texture texture, Vector2 pos, int health, float speed, String owner, WizardGame wizardGame) {
        super(texture,pos,health,speed, owner);
        shootCooldown = 0.4f;
        portalCooldown = 0.4f;
        pickNewLocation(pos);
        this.wizardGame = wizardGame;
    }

    public void update(Vector2 player) {
        float delta = Gdx.graphics.getDeltaTime();

        float newX = sprite.getX();
        float newY = sprite.getY();
        
        newX = newX + MathUtils.random(-0.2f, 0.2f);
        newY = newY + MathUtils.random(-0.2f, 0.2f);
        
        Bullet dangerBullet = getDangerBullet();
        if (dangerBullet != null) {
            this.speed = wizardGame.BULLET_SPEED * 1.5f;
            avoidBullets(dangerBullet);
            newX = newX + (delta * goalDirection.x * speed);
            newY = newY + (delta * goalDirection.y * speed);
        } else {
            this.speed = wizardGame.EYE_SPEED;
            if (shootCooldown < 0) {
                Vector2 pos = new Vector2(sprite.getX() + 4, sprite.getY() + 4);
                Vector2 dir = new Vector2(player.x - sprite.getX(), player.y - sprite.getY()).nor();
                Vector2 offset = dir.cpy().scl(20);
                wizardGame.createBullet(dir.scl(64), pos.add(offset).add(goalDirection), "enemy");
                shootCooldown = SHOOT_COOLDOWN;
            } else {
                shootCooldown = shootCooldown - delta;
            }
            portalCooldown = portalCooldown - delta;
            goalDirection = new Vector2();                
            pickNewLocation(player);            
            if (shootCooldown < (SHOOT_COOLDOWN * 0.6f)) {
                newX = newX + (delta * goalDirection.x * speed);
                newY = newY + (delta * goalDirection.y * speed);
            }
        }
        newX = MathUtils.clamp(newX, 0, 240);
        newY = MathUtils.clamp(newY, 0, 180);
        sprite.setPosition(newX, newY);
    }

    private void avoidBullets(Bullet bullet) {
        Vector2 vb = new Vector2(bullet.sprite.getX(), bullet.sprite.getY());
        Vector2 ve = new Vector2(sprite.getX(), sprite.getY());
        Vector2 vdiff = new Vector2(vb.x - ve.x, vb.y - ve.y).nor();
        vdiff.x = vdiff.x * -1;
        vdiff.y = vdiff.y * -1;
        goalDirection = vdiff.cpy();
    }

    private Bullet getDangerBullet() {
        Rectangle detection = new Rectangle(sprite.getX() - 40, sprite.getY() - 40, 80, 80);
        List<Bullet> incomingBullets = wizardGame.getCollidingBullets(detection, "enemy");
        if (incomingBullets.size() > 0) {
            return incomingBullets.get(0);
        }
        return null;
    }

    private void pickNewLocation(Vector2 player) {
        float distance = player.dst2(new Vector2(sprite.getX(),sprite.getY()));   
        if (distance > 17000) {
            Vector2 dir = new Vector2(player.x - sprite.getX(), player.y - sprite.getY()).nor();
            goalDirection = dir.cpy();
        }
    }

}
