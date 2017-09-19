package com.mygdx.game.core;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.core.BulletController;

public interface Entity {
	public void update(Vector2 player, BulletController bulletController);
	public boolean shouldRemove();
	public void draw(SpriteBatch batch);
	public void takeDamage(int damage);
	public Rectangle getBoundingRectangle();
}
