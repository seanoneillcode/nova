package com.mygdx.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.lang.Math;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Interpolation;
import com.mygdx.game.core.Bullet;
import com.mygdx.game.core.Enemy;
import com.mygdx.game.core.Entity;
import com.mygdx.game.core.Archer;
import com.mygdx.game.core.Wizard;
import com.mygdx.game.core.Eye;
import com.mygdx.game.core.Damage;
import com.mygdx.game.core.Level;
import com.mygdx.game.core.Dialog;
import com.mygdx.game.core.Conversation;


public class WizardGame extends ApplicationAdapter {

    private static final int WORLD_WIDTH = 256;
    private static final int WORLD_HEIGHT = 256;

    private SpriteBatch batch;
    private TextureRegion wizard;
    private TextureRegion downWizard;
    private TextureRegion upWizard;
    private TextureRegion currentWizard;
    private TextureRegion abilityMenu;
    private TextureRegion slotASelect;
    private TextureRegion slotBSelect;
    private TextureRegion selectionPointer;
    private Sprite stabSprite;
    private Sprite blockSprite;
    private List<Damage> damages;
    private Texture skeleton;
    private Texture archer;
    private Texture eye;
    private Texture badWizard;
    private Texture bolt;
    private Texture charger;
    private Texture damageTex;
    private Texture playerDamageTex;
    private Sprite dashafter;
    private Vector2 playerPosition;
    private Vector2 hitboxOffset;
    private Vector2 hitboxPos;
    private Vector2 addSpeed;
    private float blockTimer;
    private Vector2 blockPos;
    private Vector2 blockOffset;
    private Vector2 dashMovement;
    private float dashTimer;
    private float hitboxTimer;
    private static final float PLAYER_SPEED = 40.0f;
    public static final float BULLET_SPEED = 60f;
    private static final float SKELETON_SPEED = 16f;
    private static final float ARCHER_SPEED = 32f;
    private static final float WIZARD_SPEED = 32f;
    public static final float EYE_SPEED = 36f;
    private static final float HITBOX_COOLDOWN = 0.15f;
    private static final float HITBOX_TIMER = 0.3f;
    private static final float DASH_TIMER = 2.0f;
    private static final float INITIAL_DASH = 2.4f;
    private static final float DASH_FRICTION = 0.92f;
    private static final float MAX_SHOOT_COOLDOWN = 0.8f;
    private static final float MAX_BLOCK_COOLDOWN = 0.5f;
    private static final float HURT_COOL_DOWN = 1f;
    private static final float WAIT_START_COOLDOWN = 2.0f;
    private static final float CHARGER_SPEED = 80f;
    private static final float DAMAGE_TTL = 1.0f;
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
    private float blockCooldown;

    private int numberOfSkeletons;
    private int numberOfWizards;
    private int numberOfArchers;
    private int numberOfEyes;
    private int numberOfChargers;
    private int wizardLife = 100;
    private int enemiesKilled = 0;
    private int level;

    private Rectangle actionRect;
    private boolean isAction;
    private Vector2 actionOffset;

    Sound wizardDeathSound;
    Sound wizardShootSound;
    Sound enemyDeathSound;
    Sound loopSound;
    Sound loopSound2;
    private ShapeRenderer shapeRenderer;

    private Vector2 lastDirection;
    private Vector2 dashafterPos;

    // gun block dash stab
    String slotA = "dash";
    String slotB = "stab";
    // String slotA = "gun";
    // String slotA = "block";
    private boolean pointerLock = false;
    private boolean hasWon = false;

    Preferences prefs;
    int previousHighScore;

    float hurtCooldown = 0;
    float waitStart = 0;
    private boolean started;
    private boolean soundEffectsOn = false;
    private boolean isMenuShown = false;
    private boolean showMenuLock = false;
    private boolean fullscreenLock = false;
    private Vector2 pointerPos;
    private String currentSlotSelection = "slotA";
    private int abilityIndex = 0;
    private List<String> abilities;

    private Vector2 cameraPos;
    private List<Level> levels;
    private int levelIndex;
    private Level currentLevel;

    private List<Conversation> conversations;
    private Conversation currentConversation;
    private int conversationIndex;
    private boolean dialogLock = false;
    private boolean dialogActive = false;
    private boolean keyDLock = false;

    @Override
	public void create () {
        prefs = Gdx.app.getPreferences("NovaGamePreferences");
        previousHighScore = prefs.getInteger("highscore");

        enemyDeathSound = Gdx.audio.newSound(Gdx.files.internal("death-scream.wav"));
        wizardShootSound = Gdx.audio.newSound(Gdx.files.internal("wizard-shoot.wav"));
        wizardDeathSound = Gdx.audio.newSound(Gdx.files.internal("wizard-hurt.wav"));
        loopSound = Gdx.audio.newSound(Gdx.files.internal("bad-loop.wav"));
        loopSound2 = Gdx.audio.newSound(Gdx.files.internal("bad-loop2.wav"));
        shapeRenderer = new ShapeRenderer();
        float w = Gdx.graphics.getWidth();
        float h = Gdx.graphics.getHeight();
        camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT * (h / w));
        camera.update();

		batch = new SpriteBatch();
        wizard = new TextureRegion(new Texture("spaceman.png"));
        upWizard = new TextureRegion(new Texture("spaceman-up.png"));
        downWizard = new TextureRegion(new Texture("spaceman-down.png"));
        damageTex = new Texture("damage.png");
        playerDamageTex = new Texture("player-damage.png");
        stabSprite = new Sprite(new Texture("stab.png"));
        stabSprite.setCenter(7,3);
        blockSprite = new Sprite(new Texture("sheild.png"));
        blockSprite.setCenter(2,8);
        currentWizard = wizard;
        bolt = new Texture("bolt.png");
        eye = new Texture("eye.png");
        skeleton = new Texture("skeleton.png");
        archer = new Texture("archer.png");
        charger = new Texture("charger.png");
        badWizard = new Texture("wizard.png");
        playerPosition = getRandomPosition();
        actionRect = new Rectangle(0,0,20,20);

        dashafter = new Sprite(new Texture("dash-after.png"));
        abilityMenu = new TextureRegion(new Texture("ability-select.png"));
        slotASelect = new TextureRegion(new Texture("slota-select.png"));
        slotBSelect = new TextureRegion(new Texture("slotb-select.png"));
        selectionPointer = new TextureRegion(new Texture("selection-pointer.png"));
        pointerPos = new Vector2();
        abilities = new ArrayList<String>();
        abilities.add("stab");
        abilities.add("block");
        abilities.add("dash");
        abilities.add("gun");

        levels = new ArrayList<Level>();
        levels.add(new Level.Builder("background-ship.png")
            .startPos(new Vector2(195,151))
            .goalRect(new Rectangle(304,90,24,80))
            .boundry(new Rectangle(120,90,310,64))
            .build());
        levels.add(new Level.Builder("background.png")
            .startPos(new Vector2(0,100))
            .skeletons(2)
            .boundry(new Rectangle(0,0,245,187))
            .build());
        levels.add(new Level.Builder("background.png")
            .skeletons(4)
            .boundry(new Rectangle(0,0,245,187))
            .build());
        levels.add(new Level.Builder("background.png")
            .skeletons(4)
            .archers(2)
            .boundry(new Rectangle(0,0,245,187))
            .build());
        levels.add(new Level.Builder("background.png")
            .eyes(2)
            .boundry(new Rectangle(0,0,245,187))
            .build());
        levels.add(new Level.Builder("background-other.png")
            .goalRect(new Rectangle(210,25,50,125))
            .boundry(new Rectangle(0,0,245,187))
            .build());
        levels.add(new Level.Builder("background.png")
            .startPos(new Vector2(0,100))
            .boundry(new Rectangle(0,0,245,187))
            .build());
        levels.add(new Level.Builder("background.png")
            .boundry(new Rectangle(0,0,245,187))
            .archers(2)
            .skeletons(4)
            .chargers(2)
            .build());
        levels.add(new Level.Builder("background.png")
            .boundry(new Rectangle(0,0,245,187))
            .eyes(2)
            .skeletons(6)
            .build());
        levels.add(new Level.Builder("background.png")
            .boundry(new Rectangle(0,0,245,187))
            .chargers(5)
            .build());
        levels.add(new Level.Builder("background.png")
            .boundry(new Rectangle(0,0,245,187))
            .wizards(1)
            .build());



        FileHandle handle = Gdx.files.internal("mavenpro-regular.ttf");
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(handle);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 12;
        font = generator.generateFont(parameter);
        font.setUseIntegerPositions(false);
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        bullets = new ArrayList<Bullet>();
        enemies = new ArrayList<Entity>();
        damages = new ArrayList<Damage>();
        hitboxOffset = new Vector2();
        blockPos = new Vector2();
        blockOffset = new Vector2();
        inputVector = new Vector2();
        lastDirection = new Vector2(1,0);
        dashMovement = new Vector2();
        if (soundEffectsOn) {
            loopSound.loop(0.5f);
            loopSound2.loop(0.5f);            
        }
        
        cameraPos = new Vector2();

        // max 210 cahracters safely
        conversations = new ArrayList<Conversation>();
        conversations.add(new Conversation.Builder()
            .dialog(
                new Dialog("Zapp! Can you hear me ?", 
                    new TextureRegion(new Texture("patches-portrait.png")), true)
            )
            .dialog(
                new Dialog("Yeah. Where is the fuel we need?", 
                    new TextureRegion(new Texture("bravo-portrait.png")), false)
            )
            .dialog(
                new Dialog("To the EAST. I'm picking up lifeforms, so be careful.", 
                    new TextureRegion(new Texture("patches-portrait.png")), true)
            )
            .dialog(
                new Dialog("There's no need to worry about me.", 
                    new TextureRegion(new Texture("bravo-portrait.png")), false)
            )
            .dialog(
                new Dialog("Don't flatter yourself, the only way off this moon is crystal fuel.", 
                    new TextureRegion(new Texture("patches-portrait.png")), true)
            )
            .dialog(
                new Dialog("I'll try not to die so...", 
                    new TextureRegion(new Texture("bravo-portrait.png")), false)
            )
            .build());

        conversations.add(new Conversation.Builder()
            .dialog(
                new Dialog("Oh, and Bravo", 
                    new TextureRegion(new Texture("patches-portrait.png")), true)
            )
            .dialog(
                new Dialog("What..!?", 
                    new TextureRegion(new Texture("bravo-portrait.png")), false)
            )
            .dialog(
                new Dialog("Before you head out, test your sword and booster (press 'D' or 'S')", 
                    new TextureRegion(new Texture("patches-portrait.png")), true)
            )
            .build());

        conversations.add(new Conversation.Builder()
            .dialog(
                new Dialog("What's this?", 
                    new TextureRegion(new Texture("bravo-portrait.png")), false)
            )
            .dialog(
                new Dialog("Looks like a disrupter. Open your inventory and select 'gun' to equip it", 
                    new TextureRegion(new Texture("patches-portrait.png")), true)
            )
            .dialog(
                new Dialog("Looks handy for those hard to reach creatures.", 
                    new TextureRegion(new Texture("bravo-portrait.png")), false)
            )
            .dialog(
                new Dialog("Where to next?", 
                    new TextureRegion(new Texture("bravo-portrait.png")), false)
            )
            .dialog(
                new Dialog("Keep going EAST", 
                    new TextureRegion(new Texture("patches-portrait.png")), true)
            )
            .build());

        conversations.add(new Conversation.Builder()
            .dialog(
                new Dialog("Who's there?", 
                    new TextureRegion(new Texture("bravo-portrait.png")), true)
            )
            .dialog(
                new Dialog("The Antagonist!!!!", 
                    new TextureRegion(new Texture("patches-portrait.png")), false)
            )
            .dialog(
                new Dialog("Prepare to die...", 
                    new TextureRegion(new Texture("patches-portrait.png")), false)
            )
            .build());



        resetGame();
	}

    private void resetGame() {
        waitStart = WAIT_START_COOLDOWN;
        wizardLife = 10;
        playerPosition = new Vector2(128, 128);
        cameraPos = playerPosition.cpy();
        enemies.clear();
        damages.clear();
        bullets.clear();
        enemiesKilled = 0;
        numberOfSkeletons = 2;
        numberOfArchers = 0;
        numberOfWizards = 0;
        numberOfChargers = 0;
        started = false;
        dashTimer = 
        hitboxTimer = -1.0f;
        blockTimer = -1f;
        level = 0;
        shootCooldown = -1;
        hasWon = false;
        blockCooldown = -1;
        levelIndex = -1;
        for (Level level : levels) {
            level.reset();
        }
        dialogActive = false;
        conversationIndex = 0;
        currentConversation = conversations.get(conversationIndex);
        currentConversation.reset();
    }

    private void loadNextLevel() {
        levelIndex++;
        if (levelIndex >= levels.size()) {
            levelIndex = 0;
            hasWon = true;
        } else {
            currentLevel = levels.get(levelIndex);        
            if (!currentLevel.isLoaded()) {
                currentLevel.load(this);
                Vector2 nextPos = currentLevel.getStartPos();
                if (nextPos != null) {
                    playerPosition = nextPos.cpy();
                    camera.position.x = playerPosition.x;
                    camera.position.y = playerPosition.y;
                }
            }
        }
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

    public void createBullet(Vector2 direction, Vector2 position, String owner) {
        Bullet b = new Bullet(bolt, direction, position, owner);
        bullets.add(b);
    }

    public void createDamage(Vector2 pos, Texture texture) {
        Damage d = new Damage(texture, pos.cpy(), DAMAGE_TTL);
        damages.add(d);
    }

    private Vector3 getLerpCamera() {
        Vector3 target = new Vector3(playerPosition.x, playerPosition.y, 0);
        final float speed = 2.0f * Gdx.graphics.getDeltaTime();
        float ispeed = 1.0f - speed;
        Vector3 cameraPosition = camera.position.cpy();
        cameraPosition.scl(ispeed);
        target.scl(speed);
        cameraPosition.add(target);
        return cameraPosition;
    }

	@Override
	public void render () {
        camera.position.set(getLerpCamera());
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        handleInput();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
        Vector2 offset = new Vector2(camera.position.x, camera.position.y);
        offset.x = (offset.x) - 128.0f;
        offset.y = (offset.y) - 104.0f;
        if (isMenuShown) {
            updateMenu();
            batch.draw(abilityMenu, 10 + offset.x, 60 + offset.y);
            batch.draw(slotASelect, 70 + (abilities.indexOf(slotA) * 44) + offset.x, 104 + offset.y);            
            batch.draw(slotBSelect, 70 + (abilities.indexOf(slotB) * 44) + offset.x, 74 + offset.y);
            batch.draw(selectionPointer, pointerPos.x  + offset.x, pointerPos.y + offset.y);
        } else {
            if (currentLevel != null) {
                currentLevel.draw(batch);
            }
            if (wizardLife > 0 && !hasWon && !dialogActive) {
                update();
                if (dashTimer > 0) {
                    dashafter.setColor(1, 1, 1, 1.0f * (dashTimer / DASH_TIMER));
                    dashafter.setPosition(dashafterPos.x, dashafterPos.y);
                    dashafter.draw(batch);
                }
                batch.draw(currentWizard, playerPosition.x, playerPosition.y);
                for (Bullet b : bullets) {
                    b.sprite.draw(batch);
                }
                for (Entity e : enemies) {
                    e.draw(batch);
                }
                for (Damage d : damages) {
                    d.draw(batch);
                }
                if (hitboxTimer >= HITBOX_COOLDOWN) {
                    stabSprite.draw(batch);               
                }
                if (blockTimer >= MAX_BLOCK_COOLDOWN * 0.5f) {
                    blockSprite.draw(batch);
                }
                if (isAction) {
                    batch.draw(upWizard, actionRect.x, actionRect.y);
                }


                font.draw(batch, "p" + (int)playerPosition.x + "," + (int)playerPosition.y
                    , 100.0f + offset.x, 180.0f + offset.y);

                font.draw(batch, "H " + wizardLife, 200.0f + offset.x, 180.0f + offset.y);
                font.draw(batch, "W " + (level + 1), 200f + offset.x, 168f + offset.y);
                font.draw(batch, slotA, 12 + offset.x, 178 + offset.y);
                font.draw(batch, slotB, 60 + offset.x, 178 + offset.y);

                if (!started) {
                    font.draw(batch, "starting in " + ((int)waitStart), offset.x + 62, offset.y + 150);
                    font.draw(batch, "SURVIVE 10 WAVES OF ENEMIES TO WIN", offset.x + 10, offset.y + 120);
                    font.draw(batch, "press 'enter' to changes slots", offset.x + 42, offset.y + 80);
                    font.draw(batch, "press 's' to use slot A", offset.x + 42, offset.y + 45);
                    font.draw(batch, "press 'd' to use slot B", offset.x + 42, offset.y + 25);
                }
            } else {
                if (dialogActive) {
                    batch.draw(currentWizard, playerPosition.x, playerPosition.y);
                    for (Entity e : enemies) {
                        e.draw(batch);
                    }
                    currentConversation.update();
                    Dialog currentDialog = currentConversation.getCurrentDialog();
                    font.draw(batch, currentDialog.getLine(), offset.x + 10, offset.y + 80);
                    Vector2 portraitOffset = new Vector2();
                    if (!currentDialog.isLeft()) {
                        portraitOffset = new Vector2(160, 0);
                    }
                    batch.draw(currentDialog.getImage(), offset.x + 10 + portraitOffset.x, offset.y + 90);
                } else {
                    if (hasWon) {
                        font.draw(batch, "YOU HAVE KILLED TO SURVIVE", offset.x + 10, offset.y + 158);
                        font.draw(batch, "PRESS SPACE TO PLAY AGAIN", offset.x + 30, offset.y + 68);
                    } else {
                        font.draw(batch, "YOU RAN OUT OF Health", offset.x + 80, offset.y + 158);
                        font.draw(batch, "YOU DESTROYED  " + enemiesKilled + "  ENEMIES", offset.x + 70, offset.y + 128);
                        font.draw(batch, "PRESS SPACE TO PLAY AGAIN", offset.x + 70, offset.y + 68);
                        if (enemiesKilled == previousHighScore) {
                            font.draw(batch, "NEW HIGH SCORE!", offset.x + 70, offset.y + 98);
                        } else {
                            font.draw(batch, "CURRENT HIGH SCORE is " + this.previousHighScore, offset.x + 70, offset.y + 98);
                        }
                    }                
                }
            }
        }

		batch.end();
        
        if (wizardLife > 0 && !isMenuShown) {
            shapeRenderer.begin(ShapeType.Filled);
            if (slotA.equals("dash") || slotB.equals("dash")) {
                float progress = 64 - ((dashTimer / DASH_TIMER) * 64);
                if (progress < 64) {
                    shapeRenderer.setColor(progress / 72, 0, 0, 1);
                } else {
                    if (progress > 64) {
                        progress = 64;
                    }
                    shapeRenderer.setColor(0.8f, 0.8f, 0, 1);
                }
                shapeRenderer.rect(24, screenHeight - 32, progress, 8);
            }
            if (slotA.equals("gun") || slotB.equals("gun")) {
                float progress = 64 - ((shootCooldown / MAX_SHOOT_COOLDOWN) * 64);
                if (progress < 64) {
                    shapeRenderer.setColor(progress / 72, 0, 0, 1);
                } else {
                    if (progress > 64) {
                        progress = 64;
                    }
                    shapeRenderer.setColor(0, 0.8f, 0.8f, 1);
                }
                shapeRenderer.rect(140, screenHeight - 32, progress, 8);
            }
            shapeRenderer.end();
        }
	}
	
	@Override
	public void dispose () {
		batch.dispose();
        font.dispose();
	}

    public Rectangle getPlayerRect() {
        return new Rectangle(playerPosition.x + 4, playerPosition.y + 4, 10, 10);
    }

    private void updateMenu() {
        if (currentSlotSelection.equals("slotA")) {
            pointerPos.y = 114;
        } else {
            pointerPos.y = 84;
        }
        pointerPos.x = 80 + (abilityIndex * 44);
    }

    private void update() {
        float delta = Gdx.graphics.getDeltaTime();
        time = time - delta;

        addSpeed.add(dashMovement.x, dashMovement.y);
        dashMovement.x = dashMovement.x * DASH_FRICTION;
        dashMovement.y = dashMovement.y * DASH_FRICTION;
        playerPosition.add(addSpeed);

        if (currentLevel != null) {
            Rectangle boundry = currentLevel.boundry;
            if (playerPosition.x < boundry.x) {
                playerPosition.x = boundry.x;
            }
            if (playerPosition.x > boundry.x + boundry.width) {
                playerPosition.x = boundry.x + boundry.width;
            }
            if (playerPosition.y < boundry.y) {
                playerPosition.y = boundry.y;
            }
            if (playerPosition.y > boundry.y + boundry.height) {
                playerPosition.y = boundry.y + boundry.height;
            }
        }
        Rectangle playerRectangle = getPlayerRect();

        Iterator<Bullet> iter = bullets.listIterator();
        while (iter.hasNext()) {
            Bullet bullet = iter.next();
            bullet.update();
            if (bullet.shouldRemove()) {
                iter.remove();
            } else {
                List<Entity> collidingEnemies = getCollidingEnemies(bullet.sprite.getBoundingRectangle(), bullet.getOwner());
                if (collidingEnemies.size() > 0) {
                    bullet.ttl = -1;
                }
                for (Entity entity : collidingEnemies) {
                    entity.takeDamage(1);
                    createDamage(entity.getPos(), damageTex);
                }
                if (playerRectangle.overlaps(bullet.sprite.getBoundingRectangle()) && !bullet.isOwner("player1")) {
                    bullet.ttl = -1;
                    hurtPlayer();
                    createDamage(playerPosition.cpy(), playerDamageTex);
                }    
            }
        }

        Iterator<Damage> iterd = damages.listIterator();
        while (iterd.hasNext()) {
            Damage d = iterd.next();
            d.update();
            if (d.shouldRemove()) {
                iterd.remove();
            }
        }


        if (hitboxTimer >= 0) {
            hitboxTimer = hitboxTimer - delta;
        }
        hitboxPos = playerPosition.cpy().add(hitboxOffset);
        stabSprite.setPosition(hitboxPos.x, hitboxPos.y);
        if (hitboxTimer >= HITBOX_COOLDOWN) {
            List<Entity> collidingEnemies = getCollidingEnemies(stabSprite.getBoundingRectangle(), "player1");
            for (Entity entity : collidingEnemies) {
                entity.takeDamage(1);
                createDamage(entity.getPos(), damageTex);
            }
        }

        if (isAction) {
            actionRect.x = playerPosition.x + actionOffset.x;
            actionRect.y = playerPosition.y + actionOffset.y;
            // GET colliding characters and items
        }

        if (blockTimer >= 0) {
            blockTimer = blockTimer - delta; 
        }
        blockPos = playerPosition.cpy().add(blockOffset);
        blockSprite.setPosition(blockPos.x, blockPos.y);
        if (blockTimer >= (MAX_BLOCK_COOLDOWN * 0.5f)) {
            // check collisions
            List<Entity> collidingEnemies = getCollidingEnemies(blockSprite.getBoundingRectangle(), "player1");
            for (Entity entity : collidingEnemies) {
                entity.handleBlock(addSpeed);
            }
            List<Bullet> collidingBullets = getCollidingBullets(blockSprite.getBoundingRectangle(), "player1");
            for (Bullet bullet : collidingBullets) {
                bullet.ttl = -1;
                // REBOUND LOGIC
                // bullet.dir.x = bullet.dir.x * -1;
                // bullet.dir.y = bullet.dir.y * -1;
                // bullet.update();
                // bullet.owner = "player1";
                // bullet.ttl = 2f;
            }
        }

        if (dashTimer >= 0) {
            dashTimer = dashTimer - delta;            
        }

        if (Math.abs(dashMovement.x) < 0.2f) {
            dashMovement.x = 0;
        }
        if (Math.abs(dashMovement.y) < 0.2f) {
            dashMovement.y = 0;
        }

        if (currentLevel != null) {
            currentLevel.update(this);
        }

        List<Entity> collidingEnemies = getCollidingEnemies(playerRectangle, "player1");
        if (collidingEnemies.size() > 0) {
            hurtPlayer();
            createDamage(playerPosition.cpy(), playerDamageTex);
        }
        for (Entity entity : collidingEnemies) {
            entity.takeDamage(1);
            createDamage(entity.getPos(), damageTex);
        }
        hurtCooldown = hurtCooldown - delta;

        Iterator<Entity> iter2 = enemies.listIterator();
        while (iter2.hasNext()) {
            Entity entity = iter2.next();
            entity.update(playerPosition);
            if (entity.shouldRemove()) {
                iter2.remove();
                enemiesKilled = enemiesKilled + 1;
                if (hurtCooldown < 0) {
                    playSound(enemyDeathSound);
                }
            }
        }
        if (currentLevel != null && currentLevel.isDone() && started) {
            loadNextLevel();
        }
        waitStart = waitStart - delta;
        if (waitStart < 0 && !started) {
            started = true;
            loadNextLevel();
        }
        checkForConversations();
    }

    private void checkForConversations() {
        Rectangle playerRectangle = getPlayerRect();
        if (levelIndex == 0 && conversationIndex == 0) {
            Rectangle startConvo = new Rectangle(190,115,50,30);
            if (playerRectangle.overlaps(startConvo)) {
                dialogActive = true;
            }
        }
        if (levelIndex == 0 && conversationIndex == 1) {
            Rectangle swordConvo = new Rectangle(250,90,50,64);
            if (playerRectangle.overlaps(swordConvo)) {
                dialogActive = true;
            }
        }
        if (levelIndex == 5 && conversationIndex == 2) {
            Rectangle swordConvo = new Rectangle(0,0,250,200);
            if (playerRectangle.overlaps(swordConvo)) {
                dialogActive = true;
            }
        }
        if (levelIndex == 5 && conversationIndex == 2) {
            Rectangle swordConvo = new Rectangle(0,0,250,200);
            if (playerRectangle.overlaps(swordConvo)) {
                dialogActive = true;
            }
        }
        if (levelIndex == 11 && conversationIndex == 3) {
            Rectangle swordConvo = new Rectangle(0,0,250,200);
            if (playerRectangle.overlaps(swordConvo)) {
                dialogActive = true;
            }
        }

    }

    private List<Entity> getCollidingEnemies(Rectangle rect, String owner) {
        List<Entity> colliding = new ArrayList<Entity>();
        for (Entity e : enemies) {
            if(!e.isOwner(owner) && rect.overlaps(e.getBoundingRectangle())) {
                colliding.add(e);
            }
        }
        return colliding;
    }

    public List<Bullet> getCollidingBullets(Rectangle rect, String owner) {
        List<Bullet> colliding = new ArrayList<Bullet>();
        for (Bullet e : bullets) {
            if(!e.isOwner(owner) && rect.overlaps(e.sprite.getBoundingRectangle())) {
                colliding.add(e);
            }
        }
        return colliding;
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

    private void createBlock(Vector2 offset) {
        blockTimer = MAX_BLOCK_COOLDOWN;
        blockOffset = offset.cpy();
    }

    private void action() {
        Vector2 offset = new Vector2();
        if (lastDirection.x < 0) {
            offset = offset.add(-16,0);
        }
        if (lastDirection.x > 0) {
            offset = offset.add(16,0);
        }
        if (lastDirection.y < 0) {
            offset = offset.add(0, 16);
        }
        if (lastDirection.y > 0) {
            offset = offset.add(0, -16);
        }
        actionOffset = offset.cpy();
    }

    private void useSlot(String slot) {
        if (slot.equals("block") && blockTimer < 0) {
            Vector2 offset = new Vector2();
            if (lastDirection.x < 0) {
                offset = offset.add(-16,0);
                blockSprite.setRotation(180);
            }
            if (lastDirection.x > 0) {
                offset = offset.add(16,0);
                blockSprite.setRotation(0);
            }
            if (lastDirection.y < 0) {
                offset = offset.add(0, 16);
                blockSprite.setRotation(90);
            }
            if (lastDirection.y > 0) {
                offset = offset.add(0, -16);
                blockSprite.setRotation(270);
            }
            if (lastDirection.x < 0 && lastDirection.y > 0) {
                blockSprite.setRotation(225);
            }
            if (lastDirection.x < 0 && lastDirection.y < 0) {
                blockSprite.setRotation(135);
            }
            if (lastDirection.x > 0 && lastDirection.y > 0) {
                blockSprite.setRotation(315);
            }
            if (lastDirection.x > 0 && lastDirection.y < 0) {
                blockSprite.setRotation(45);
            }
            offset.clamp(-16,16).add(6,0);
            createBlock(offset);
        }
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
                    shootCooldown = MAX_SHOOT_COOLDOWN;
                    createBullet(dir, offset, "player1");
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
            offset.clamp(-15,15);
            addHitbox(offset);
        }
        if (slot.equals("dash")) {
            if (dashTimer < 0) {
                dashMovement = lastDirection.cpy().scl(INITIAL_DASH);
                dashMovement.y = dashMovement.y * -1;
                dashTimer = DASH_TIMER;
                dashafterPos = playerPosition.cpy();
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

        if (isMenuShown) {
            if (!isUpPressed && !isDownPressed && !isLeftPressed && !isRightPressed) {
                pointerLock = false;
            }
            if (!pointerLock) {
                if (isLeftPressed) {
                    abilityIndex--;
                    pointerLock = true;
                }
                if (isRightPressed) {
                    abilityIndex++;
                    pointerLock = true;
                }
                if (abilityIndex < 0) {
                    abilityIndex = 0;
                }
                if (abilityIndex > 3) {
                    abilityIndex = 3;
                }
                if (isUpPressed || isDownPressed) {
                    pointerLock = true;
                    if (currentSlotSelection.equals("slotA")) {
                        currentSlotSelection = "slotB";
                    } else {
                        currentSlotSelection = "slotA";
                    }
                }
                if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                    pointerLock = true;
                    if (currentSlotSelection.equals("slotA")) {
                        slotA = abilities.get(abilityIndex);
                    } else {
                        slotB = abilities.get(abilityIndex);
                    }
                }
            }
        } else {
            shootCooldown = shootCooldown - Gdx.graphics.getDeltaTime();
            blockCooldown = blockCooldown - Gdx.graphics.getDeltaTime();
            boolean canChangeDir = hitboxTimer < 0;
            addSpeed = new Vector2();
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
                addSpeed.add(-actualSpeed, 0);
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
                addSpeed.add(actualSpeed, 0);
            }
            if (isUpPressed) {
                if (canChangeDir) {
                    inputVector.y = inputVector.y - 1;
                    lastDirection = inputVector.cpy();
                    currentWizard = upWizard;
                }
                addSpeed.add(0, actualSpeed);
            }
            if (isDownPressed) {
                if (canChangeDir) {
                    inputVector.y = inputVector.y + 1;
                    lastDirection = inputVector.cpy();
                    currentWizard = downWizard;
                }
                addSpeed.add(0, -actualSpeed);
            }
            addSpeed.clamp(-actualSpeed,actualSpeed);
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                useSlot(slotA); 
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D) && !keyDLock) {
                useSlot(slotB);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.E)) {
                action();
                isAction = true;
            } else {
                isAction = false;
            }

        }
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            if (wizardLife < 1 || hasWon) {
                resetGame();                
            }
        }
        
        if (Gdx.input.isKeyPressed(Input.Keys.ENTER)) {
            if (!showMenuLock) {
                showMenuLock = true;
                isMenuShown = !isMenuShown;
            }
        } else {
            showMenuLock = false;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.F)) {
            if (!dialogLock) {
                dialogLock = true;
                dialogActive = true;
            }
        } else {
            dialogLock = false;
        }        
        if (dialogActive && Gdx.input.isKeyPressed(Input.Keys.D)) {
            if (!keyDLock) {
                if (currentConversation.isFinished()) {
                    dialogActive = false;
                    currentConversation.reset();
                    conversationIndex++;
                    if (conversationIndex > conversations.size() - 1) {
                        conversationIndex = 0;
                    }
                    currentConversation = conversations.get(conversationIndex);
                } else {
                    currentConversation.handleInput();
                }
                keyDLock = true;
            }
        } else {
            keyDLock = false;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.H)) {
            if (!fullscreenLock) {
                fullscreenLock = true;
                switchToFullScreen();
            }
        } else {
            fullscreenLock = false;
        }
	}

    public boolean areEnemiesDead() {
        return enemies.size() < 1;
    }

    public void addWaveOfSkeletons(int numSkeletons, int numArchers, int numWizards,int numEyes, int numChargers) {
        for (int i = 0; i < numSkeletons; i++) {
            addSkeleton();
        }
        for (int i = 0; i < numArchers; i++) {
            addShootingEnemy();
        }
        for (int i = 0; i < numWizards; i++) {
            addWizardEnemy();
        }
        for (int i = 0; i < numEyes; i++) {
            addEye();
        }
        for (int i = 0; i < numChargers; i++) {
            addCharger();
        }
    }

    private void addShootingEnemy() {
        Archer e = new Archer(archer, getRandomEdge(), 1, ARCHER_SPEED, "enemy", this);
        enemies.add(e);
    }

    private void addWizardEnemy() {
        Wizard e = new Wizard(badWizard, getRandomEdge(), 1, WIZARD_SPEED, "enemy", this);
        enemies.add(e);
    }

    private void addSkeleton() {
        Enemy e = new Enemy(skeleton, getRandomEdge(), 1, SKELETON_SPEED, "enemy");
        enemies.add(e);
    }

    private void addCharger() {
        Enemy e = new Enemy(charger, getRandomEdge(), 1, CHARGER_SPEED, "enemy");
        enemies.add(e);
    }
    private void addEye() {
        Eye e = new Eye(eye, getRandomEdge(), 1, EYE_SPEED, "enemy", this);
        enemies.add(e);
    }

    private Vector2 getRandomEdge() {
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
        return pos;
    }

    private void switchToFullScreen() {
        Boolean fullScreen = Gdx.graphics.isFullscreen();
        Graphics.DisplayMode currentMode = Gdx.graphics.getDisplayMode();
        if (fullScreen == true) {
            Gdx.graphics.setWindowedMode((int)screenWidth, (int)screenHeight);
        } else {
            Gdx.graphics.setFullscreenMode(currentMode);
        }
    }

}
