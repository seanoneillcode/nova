package com.mygdx.game.core;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public interface Entity extends Pwned {
	public void update(Vector2 player);
	public boolean shouldRemove();
	public void draw(SpriteBatch batch);
	public void takeDamage(int damage);
	public Rectangle getBoundingRectangle();
	public void handleBlock(Vector2 push);
	public Vector2 getPos();
}
