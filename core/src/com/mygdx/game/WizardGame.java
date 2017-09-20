package com.mygdx.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.lang.Math;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.game.core.Bullet;
import com.mygdx.game.core.BulletController;
import com.mygdx.game.core.Enemy;
import com.mygdx.game.core.Entity;
import com.mygdx.game.core.Archer;


public class WizardGame extends ApplicationAdapter implements BulletController {

    private static final int WORLD_WIDTH = 256;
    private static final int WORLD_HEIGHT = 256;

    private SpriteBatch batch;
    private TextureRegion wizard;
    private TextureRegion downWizard;
    private TextureRegion upWizard;
    private TextureRegion currentWizard;
    private Sprite stabSprite;
    private Texture skeleton;
    private Texture archer;
    private Texture bolt;
    private Vector2 playerPosition;
    private Vector2 hitboxOffset;
    private Vector2 hitboxPos;
    private Vector2 dashMovement;
    private float dashTimer;
    private float hitboxTimer;
    private static final float PLAYER_SPEED = 32.0f;
    private static final float BULLET_SPEED = 80f;
    private static final float SKELETON_SPEED = 32f;
    private static final float HITBOX_COOLDOWN = 0.25f;
    private static final float HITBOX_TIMER = 0.5f;
    private static final float DASH_TIMER = 2.0f;
    private static final float INITIAL_DASH = 2.4f;
    private static final float DASH_FRICTION = 0.92f;
    private static final float MAX_COOLDOWN = 0.2f;
    private static final float HURT_COOL_DOWN = 1f;
    private static final float WAIT_START_COOLDOWN = 2.0f;
    private BitmapFont font;

    private float screenWidth;
    private float screenHeight;
    private float time;
    private OrthographicCamera camera;

    private boolean isRight = true;

    private Vector2 inputVector;

    private List<Bullet> bullets;
    private List<Entity> enemies;
    private float shootCooldown;

    private int numberOfSkeletons;
    private int numberOfArchers;
    private int wizardLife = 100;
    private int enemiesKilled = 0;

    Sound wizardDeathSound;
    Sound wizardShootSound;
    Sound enemyDeathSound;
    Sound loopSound;
    Sound loopSound2;
    private ShapeRenderer shapeRenderer;

    private Vector2 lastDirection;

    String slotA = "dash";
    String slotB = "stab";

    Preferences prefs;
    int previousHighScore;

    float hurtCooldown = 0;
    float waitStart = 0;
    private boolean started;
    private boolean soundEffectsOn = false;

    @Override
	public void create () {
        prefs = Gdx.app.getPreferences("WizardGamePreferences");
        previousHighScore = prefs.getInteger("highscore");

        enemyDeathSound = Gdx.audio.newSound(Gdx.files.internal("death-scream.wav"));
        wizardShootSound = Gdx.audio.newSound(Gdx.files.internal("wizard-shoot.wav"));
        wizardDeathSound = Gdx.audio.newSound(Gdx.files.internal("wizard-hurt.wav"));
        //loopSound = Gdx.audio.newSound(Gdx.files.internal("bad-loop.wav"));
        //loopSound.loop(1.0f);
        // loopSound2 = Gdx.audio.newSound(Gdx.files.internal("bad-loop2.wav"));
        //loopSound2.loop(1.0f);
        shapeRenderer = new ShapeRenderer();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT * (h / w));
        camera.position.set(camera.viewportWidth / 2f, camera.viewportHeight / 2f, 0);
        camera.update();

		batch = new SpriteBatch();
        wizard = new TextureRegion(new Texture("wizard.png"));
        upWizard = new TextureRegion(new Texture("wizard-up.png"));
        downWizard = new TextureRegion(new Texture("wizard-down.png"));
        stabSprite = new Sprite(new Texture("stab.png"));
        stabSprite.setCenter(7,3);
        currentWizard = wizard;
        bolt = new Texture("bolt.png");
        skeleton = new Texture("skeleton.png");
        archer = new Texture("archer.png");
        playerPosition = getRandomPosition();

        FileHandle handle = Gdx.files.internal("MavenPro-regular.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(handle);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 10;
        font = generator.generateFont(parameter);
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        bullets = new ArrayList<Bullet>();
        enemies = new ArrayList<Entity>();
        hitboxOffset = new Vector2();
        inputVector = new Vector2();
        lastDirection = new Vector2(1,0);
        dashMovement = new Vector2();
        resetGame();
	}

    private Vector2 getRandomPosition() {
        float xpos = MathUtils.random(0, screenWidth);
        float ypos = MathUtils.random(0, screenHeight);
        return new Vector2(xpos, ypos);
    }

    private void playSound(Sound sound) {
        if (!soundEffectsOn) {
            return;
        }
        if (sound.equals(wizardDeathSound)) {
            sound.play(1.0f);
        }
        if (sound.equals(wizardShootSound)) {
            sound.play(0.3f, MathUtils.random(0.8f,1.2f), 0.5f);
        }
        if (sound.equals(enemyDeathSound)) {
            sound.play(0.3f, MathUtils.random(0.8f,1.2f), 0.5f);
        }
    }

    public void createBullet(Vector2 direction, Vector2 position) {
        Bullet b = new Bullet(bolt, direction, position);
        bullets.add(b);
    }


	@Override
	public void render () {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        // shapeRenderer.setProjectionMatrix(camera.combined);
        handleInput();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
        if (wizardLife > 0) {
            update();
            batch.draw(currentWizard, playerPosition.x, playerPosition.y);
            for (Bullet b : bullets) {
                b.sprite.draw(batch);
            }
            for (Entity e : enemies) {
                e.draw(batch);
            }
            if (hitboxTimer >= HITBOX_COOLDOWN) {
                stabSprite.draw(batch);               
            }
            font.draw(batch, "HEALTH : " + wizardLife, 180, 180);
            font.draw(batch, "DESTROYED : " + enemiesKilled, 4, 180);
        } else {
            font.draw(batch, "YOU RAN OUT OF Health", 80, 158);
            font.draw(batch, "YOU DESTROYED  " + enemiesKilled + "  ENEMIES", 70, 128);
            font.draw(batch, "PRESS SPACE TO PLAY AGAIN", 70, 68);
            if (enemiesKilled == previousHighScore) {
                font.draw(batch, "NEW HIGH SCORE!", 70, 98);
            } else {
                font.draw(batch, "CURRENT HIGH SCORE is " + this.previousHighScore, 70, 98);
            }
        }

		batch.end();

        if (wizardLife > 0) {
            if (slotA.equals("dash") || slotB.equals("dash")) {
                shapeRenderer.begin(ShapeType.Filled);
                float progress = 64 - ((dashTimer / DASH_TIMER) * 64);
                if (progress < 64) {
                    shapeRenderer.setColor(progress / 72, 0, 0, 1);
                } else {
                    shapeRenderer.setColor(0.8f, 0.8f, 0, 1);
                }
                shapeRenderer.rect((screenWidth * 0.5f) - 32, screenHeight - 32, progress, 8);
                shapeRenderer.end();
            }            
        }
	}
	
	@Override
	public void dispose () {
		batch.dispose();
        font.dispose();
	}

    private void update() {
        float delta = Gdx.graphics.getDeltaTime();
        time = time - delta;
        if (playerPosition.x < 0) {
            playerPosition.x = 0;
        }
        if (playerPosition.x > screenWidth) {
            playerPosition.x = screenWidth;
        }
        if (playerPosition.y < 0) {
            playerPosition.y = 0;
        }
        if (playerPosition.y > screenHeight) {
            playerPosition.y = screenHeight;
        }
        Rectangle playerRectangle = new Rectangle(playerPosition.x + 4, playerPosition.y + 4, 10, 10);

        Iterator<Bullet> iter = bullets.listIterator();
        while (iter.hasNext()) {
            Bullet bullet = iter.next();
            bullet.update();
            if (bullet.shouldRemove()) {
                iter.remove();
            }
            if (enemyCollision(bullet.sprite.getBoundingRectangle())) {
                bullet.ttl = -1;
            }
            if (playerRectangle.overlaps(bullet.sprite.getBoundingRectangle())) {
                bullet.ttl = -1;
                hurtPlayer();
            }
        }

        if (hitboxTimer >= 0) {
            hitboxTimer = hitboxTimer - delta;
        }
        hitboxPos = playerPosition.cpy().add(hitboxOffset);
        stabSprite.setPosition(hitboxPos.x, hitboxPos.y);
        if (hitboxTimer >= HITBOX_COOLDOWN) {
            if (enemyCollision(stabSprite.getBoundingRectangle())) {
                //hitboxTimer = -1;                
            //    hitboxCooldown = HITBOX_COOLDOWN;
            }            
        }



        // dashMovement
        playerPosition.add(dashMovement.x, dashMovement.y);
        dashMovement.x = dashMovement.x * DASH_FRICTION;
        dashMovement.y = dashMovement.y * DASH_FRICTION;

        if (dashTimer >= 0) {
            dashTimer = dashTimer - delta;            
        }

        if (Math.abs(dashMovement.x) < 0.2f) {
            dashMovement.x = 0;
        }
        if (Math.abs(dashMovement.y) < 0.2f) {
            dashMovement.y = 0;
        }

        
        if (enemyCollision(playerRectangle)) {
            hurtPlayer();
        }
        hurtCooldown = hurtCooldown - delta;

        Iterator<Entity> iter2 = enemies.listIterator();
        while (iter2.hasNext()) {
            Entity entity = iter2.next();
            entity.update(playerPosition, this);
            if (entity.shouldRemove()) {
                iter2.remove();
                enemiesKilled = enemiesKilled + 1;
                if (hurtCooldown < 0) {
                    playSound(enemyDeathSound);
                }
            }
        }
        if (enemies.size() < 1 && started) {
            numberOfSkeletons++;
            numberOfArchers = numberOfSkeletons / 3;
            addWaveOfSkeletons();
        }
        waitStart = waitStart - delta;
        if (waitStart < 0 && !started) {
            started = true;
            addWaveOfSkeletons();
        }
    }

    private boolean enemyCollision(Rectangle rect) {
        boolean isColliding = false;
        for (Entity e : enemies) {
            if(rect.overlaps(e.getBoundingRectangle())) {
                e.takeDamage(1);
                isColliding = true;
            }
        }
        return isColliding;
    }

    private void hurtPlayer() {
        wizardLife = wizardLife - 1;
        playSound(wizardDeathSound);
        hurtCooldown = HURT_COOL_DOWN;
        if (wizardLife < 1) {
            if (enemiesKilled > previousHighScore) {
                prefs.putInteger("highscore", enemiesKilled);
                previousHighScore = enemiesKilled;
            }
        }
    }


    private void resetGame() {
        waitStart = WAIT_START_COOLDOWN;
        wizardLife = 100;
        playerPosition = new Vector2(128, 128);
        enemies.clear();
        bullets.clear();
        enemiesKilled = 0;
        numberOfSkeletons = 3;
        numberOfArchers = 1;
        started = false;
        dashTimer = 
        hitboxTimer = -1.0f;
    }


    private void useSlot(String slot) {
        if (slot.equals("gun")) {
            Vector2 offset = playerPosition.cpy();
            Vector2 dir = new Vector2();

            if (lastDirection.x < 0) {
                offset = offset.add(-5, 2);
                dir = dir.add(-BULLET_SPEED, 0);
            }
            if (lastDirection.x > 0) {
                offset = offset.add(15, 2);
                dir = dir.add(BULLET_SPEED, 0);
            }
            if (lastDirection.y < 0) {
                offset = offset.add(3, 15);
                dir = dir.add(0, BULLET_SPEED);
            }
            if (lastDirection.y > 0) {
                offset = offset.add(3, -8);
                dir = dir.add(0, -BULLET_SPEED);
            }    
            if (offset != null && dir != null) {
                if (shootCooldown < 0) {
                    shootCooldown = MAX_COOLDOWN;
                    createBullet(dir, offset);
                    playSound(wizardShootSound);
                }
            }
        }
        if (slot.equals("stab")) {
            // image is 14 x 6
            Vector2 offset = new Vector2();
            if (lastDirection.x < 0) {
                offset = offset.add(-15,0);
                stabSprite.setRotation(180);
            }
            if (lastDirection.x > 0) {
                offset = offset.add(15,0);
                stabSprite.setRotation(0);
            }
            if (lastDirection.y < 0) {
                offset = offset.add(0, 15);
                stabSprite.setRotation(90);
            }
            if (lastDirection.y > 0) {
                offset = offset.add(0, -15);
                stabSprite.setRotation(270);
            }
            if (lastDirection.x < 0 && lastDirection.y > 0) {
                stabSprite.setRotation(225);
            }
            if (lastDirection.x < 0 && lastDirection.y < 0) {
                stabSprite.setRotation(135);
            }
            if (lastDirection.x > 0 && lastDirection.y > 0) {
                stabSprite.setRotation(315);
            }
            if (lastDirection.x > 0 && lastDirection.y < 0) {
                stabSprite.setRotation(45);
            }            
            addHitbox(offset);
        }
        if (slot.equals("dash")) {
            if (dashTimer < 0) {
                dashMovement = lastDirection.cpy().scl(INITIAL_DASH);
                dashMovement.y = dashMovement.y * -1;
                dashTimer = DASH_TIMER;
            }
        }
    }

    private void addHitbox(Vector2 offset) {
        if (hitboxTimer < 0) {
            hitboxTimer = HITBOX_TIMER;
            hitboxOffset = offset.cpy();
        }
    }

	private void handleInput() {
        float actualSpeed = PLAYER_SPEED * Gdx.graphics.getDeltaTime();

		boolean isLeftPressed = Gdx.input.isKeyPressed(Input.Keys.LEFT);
        boolean isRightPressed = Gdx.input.isKeyPressed(Input.Keys.RIGHT);
        boolean isUpPressed = Gdx.input.isKeyPressed(Input.Keys.UP);
        boolean isDownPressed = Gdx.input.isKeyPressed(Input.Keys.DOWN);

        inputVector.x = 0;
        inputVector.y = 0;

        shootCooldown = shootCooldown - Gdx.graphics.getDeltaTime();
        boolean canChangeDir = hitboxTimer < 0;
        if (!canChangeDir) {
            actualSpeed = 0;
        }
        if (isLeftPressed) {
            if (canChangeDir) {
                if (isRight) {
                    wizard.flip(true, false);
                }
                isRight = false;
                inputVector.x = inputVector.x - 1;
                lastDirection = inputVector.cpy();
                currentWizard = wizard;
            }
            playerPosition.add(-actualSpeed, 0);
        }
        if (isRightPressed) {
            if (canChangeDir) {
                if (!isRight) {
                    wizard.flip(true, false);
                }
                isRight = true;   
                inputVector.x = inputVector.x + 1;
                lastDirection = inputVector.cpy();
                currentWizard = wizard;
            }
            playerPosition.add(actualSpeed, 0);
        }
        if (isUpPressed) {
            if (canChangeDir) {
                inputVector.y = inputVector.y - 1;
                lastDirection = inputVector.cpy();
                currentWizard = upWizard;
            }
            playerPosition.add(0, actualSpeed);
        }
        if (isDownPressed) {
            if (canChangeDir) {
                inputVector.y = inputVector.y + 1;
                lastDirection = inputVector.cpy();
                currentWizard = downWizard;
            }
            playerPosition.add(0, -actualSpeed);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            useSlot(slotA); 
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            useSlot(slotB);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        if (wizardLife < 1 && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            resetGame();
        }
	}

    private void addWaveOfSkeletons() {
        for (int i = 0; i < numberOfSkeletons; i++) {
            addSkeleton();
        }
        for (int i = 0; i < numberOfArchers; i++) {
            addShootingEnemy();
        }
    }

    private void addShootingEnemy() {
        Vector2 pos = null;
        pos = new Vector2(MathUtils.random( 0, 240), MathUtils.random(0, 180));
        Archer e = new Archer(archer, pos, 1, SKELETON_SPEED);
        enemies.add(e);
    }

    private void addSkeleton() {
        Vector2 pos = null;
        switch (MathUtils.random(3)) {
            case 0:
                pos = new Vector2(MathUtils.random(0, 256), 0);
                break;
            case 1:
                pos = new Vector2(MathUtils.random(0, 256), 256);
                break;
            case 2:
                pos = new Vector2(0, MathUtils.random(0, 256));
                break;
            case 3:
                pos = new Vector2(256, MathUtils.random(0, 256));
                break;
        }
        Enemy e = new Enemy(skeleton, pos, 1, SKELETON_SPEED);
        enemies.add(e);
    }

}
