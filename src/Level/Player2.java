package Level;

import Engine.GraphicsHandler;
import Engine.Key;
import Engine.KeyLocker;
import Engine.Keyboard;
import Game.GameState;
import GameObject.GameObject;
import GameObject.SpriteSheet;
import SpriteFont.SpriteFont;
import Utils.AirGroundState;
import Utils.Direction;

import java.awt.Color;
import java.util.ArrayList;

public abstract class Player2 extends GameObject {
        // amount of damage this player's attacks deal (default 1)
        protected int attackDamage = 1;
        public int getAttackDamage() {
            return attackDamage;
        }
    // values that affect player movement
    // these should be set in a subclass
    protected float walkSpeed = 0;
    protected float gravity = 0;
    protected float jumpHeight = 0;
    protected float jumpDegrade = 0;
    protected float terminalVelocityY = 0;
    protected float momentumYIncrease = 0;

    // values used to handle player movement
    protected float jumpForce = 0;
    protected float momentumY = 0;
    protected float moveAmountX, moveAmountY;
    protected float lastAmountMovedX, lastAmountMovedY;

    // values used to keep track of player's current state
    protected PlayerState playerState;
    protected PlayerState previousPlayerState;
    protected Direction facingDirection;
    protected AirGroundState airGroundState;
    protected AirGroundState previousAirGroundState;
    protected LevelState levelState;

   
    protected ArrayList<PlayerListener> listeners = new ArrayList<>();

    // define keys
    protected KeyLocker keyLocker = new KeyLocker();
    protected Key JUMP_KEY = Key.I;
    protected Key MOVE_LEFT_KEY = Key.J;
    protected Key MOVE_RIGHT_KEY = Key.L;
    protected Key CROUCH_KEY = Key.K;
    protected Key ATTACK_KEY = Key.U;


    // flags
    protected boolean isInvincible = false; // if true, player cannot be hurt by enemies (good for testing)
    // health system
    protected boolean attacksEnabled = true;
    public void setAttacksEnabled(boolean enabled) {
        this.attacksEnabled = enabled;
        if (!enabled) {
            keyLocker.unlockKey(ATTACK_KEY);
        }
    }
    public boolean isAttacksEnabled() { return attacksEnabled; }
    // press-to-disable support for Player2
    protected int disablePressCount = 0;
    protected boolean lastAttackKeyDown = false; // detect key-down edges
    protected int forcedDisableMs = 0; // remaining ms for forced disable
    protected static final int DISABLE_PRESS_LIMIT = 7;
    protected static final int FORCED_DISABLE_DURATION_MS = 5000; // 8 seconds
    protected int maxHealth = 20; // number of hits the player can take before dying
    protected int health = maxHealth;
    
    // Power-up system
    protected boolean speedBoostActive = false;
    protected boolean highJumpActive = false;
    protected int powerUpDurationMs = 0;
    protected float originalWalkSpeed;
    protected float originalJumpHeight;
    protected int selectedPowerUp = 0; // 0 = speed, 1 = high jump
    protected boolean powerUpSelectionVisible = false;
    protected int powerUpToggleCooldown = 0;
    
    boolean win = true;

    public Player2(SpriteSheet spriteSheet, float x, float y, String startingAnimationName) {
        super(spriteSheet, x, y, startingAnimationName);
        facingDirection = Direction.RIGHT;
        airGroundState = AirGroundState.AIR;
        previousAirGroundState = airGroundState;
        playerState = PlayerState.STANDING;
        previousPlayerState = playerState;
        levelState = LevelState.RUNNING;
    }
    
    // Initialize original values after subclass constructor sets the actual values
    public void initializePowerUpSystem() {
        if (originalWalkSpeed == 0) { // Only initialize once
            originalWalkSpeed = walkSpeed;
            originalJumpHeight = jumpHeight;
        }
    }

    public void update() {
        moveAmountX = 0;
        moveAmountY = 0;

        // if player is currently playing through level (has not won or lost)
        if (levelState == LevelState.RUNNING) {
            applyGravity();
            
            // Update power-up system
            updatePowerUps();

            // update player's state and current actions, which includes things like determining how much it should move each frame and if its walking or jumping
            do {
                previousPlayerState = playerState;
                handlePlayerState();
            } while (previousPlayerState != playerState);

            previousAirGroundState = airGroundState;

            // move player with respect to map collisions based on how much player needs to move this frame
            lastAmountMovedX = super.moveXHandleCollision(moveAmountX);
            lastAmountMovedY = super.moveYHandleCollision(moveAmountY);

            handlePlayerAnimation();

            updateLockedKeys();

            // Handle press-to-disable (count key-down edges of attack key) for Player2
            boolean attackKeyDownNowP2 = Keyboard.isKeyDown(ATTACK_KEY);
            if (attackKeyDownNowP2 && !lastAttackKeyDown) {
                disablePressCount++;
                if (Engine.Debug.ENABLED) System.out.println("DEBUG: Player2 disablePressCount=" + disablePressCount);
                if (disablePressCount >= DISABLE_PRESS_LIMIT) {
                    setAttacksEnabled(false);
                    forcedDisableMs = FORCED_DISABLE_DURATION_MS;
                    disablePressCount = 0;
                    if (Engine.Debug.ENABLED) System.out.println("DEBUG: Player2 attacks disabled by press-limit");
                }
            }
            lastAttackKeyDown = attackKeyDownNowP2;
            if (forcedDisableMs > 0) {
                forcedDisableMs = Math.max(0, forcedDisableMs - 16);
                if (forcedDisableMs == 0) {
                    setAttacksEnabled(true);
                    if (Engine.Debug.ENABLED) System.out.println("DEBUG: Player2 forced-disable expired; attacks re-enabled");
                }
            }

            // attack input for player 2: spawn a projectile when attack key is pressed
            if (attacksEnabled && Keyboard.isKeyDown(ATTACK_KEY) && !keyLocker.isKeyLocked(ATTACK_KEY)) {
                keyLocker.lockKey(ATTACK_KEY);
                int projW = 8; int projH = 8;
                float speed = 240f;
                float vx = facingDirection == Direction.RIGHT ? speed : -speed;
                float spawnX = facingDirection == Direction.RIGHT ? this.getX() + this.getWidth() + 4f : this.getX() - projW - 4f;
                float spawnY = this.getY() + (this.getHeight() / 2f) - (projH / 2f);
                try {
                    map.addProjectileAttack(new ProjectileAttack(spawnX, spawnY, vx, 0f, attackDamage, 4000, true, this));
                } catch (Exception e) {
                    if (Engine.Debug.ENABLED) System.out.println("DEBUG: Failed to spawn player2 projectile: " + e);
                }
            }

            if (this.getY() > map.getEndBoundY()) {
            if (levelState != LevelState.PLAYER_DEAD) {
                this.levelState = LevelState.PLAYER_DEAD;
                health = 0;
                win = false;
                System.out.println("Player has died by falling out of the level.");
                
                for (PlayerListener listener : listeners) {
                    listener.onDeath();
                    updatePlayerDead();
                }
            }
        }
            // update player's animation
            super.update();
        }

        

        // if player has beaten level
        else if (levelState == LevelState.LEVEL_COMPLETED) {
            updateLevelCompleted();
        }

        // if player has lost level
        else if (levelState == LevelState.PLAYER_DEAD) {
            updatePlayerDead();
        }
    }

    // add gravity to player, which is a downward force
    protected void applyGravity() {
        moveAmountY += gravity + momentumY;
    }

    // based on player's current state, call appropriate player state handling method
    protected void handlePlayerState() {
        switch (playerState) {
            case STANDING:
                playerStanding();
                break;
            case WALKING:
                playerWalking();
                break;
            case CROUCHING:
                playerCrouching();
                break;
            case JUMPING:
                playerJumping();
                break;
        }
    }

    // player STANDING state logic
    protected void playerStanding() {
        // if walk left or walk right key is pressed, player enters WALKING state
        if (Keyboard.isKeyDown(MOVE_LEFT_KEY) || Keyboard.isKeyDown(MOVE_RIGHT_KEY)) {
            playerState = PlayerState.WALKING;
        }

        // if jump key is pressed, player enters JUMPING state
        else if (Keyboard.isKeyDown(JUMP_KEY) && !keyLocker.isKeyLocked(JUMP_KEY)) {
            keyLocker.lockKey(JUMP_KEY);
            playerState = PlayerState.JUMPING;
        }

        // if crouch key is pressed, player enters CROUCHING state
        else if (Keyboard.isKeyDown(CROUCH_KEY)) {
            playerState = PlayerState.CROUCHING;
        }
    }

    // player WALKING state logic
    protected void playerWalking() {
        // if walk left key is pressed, move player to the left
        if (Keyboard.isKeyDown(MOVE_LEFT_KEY)) {
            moveAmountX -= walkSpeed;
            facingDirection = Direction.LEFT;
        }

        // if walk right key is pressed, move player to the right
        else if (Keyboard.isKeyDown(MOVE_RIGHT_KEY)) {
            moveAmountX += walkSpeed;
            facingDirection = Direction.RIGHT;
        } else if (Keyboard.isKeyUp(MOVE_LEFT_KEY) && Keyboard.isKeyUp(MOVE_RIGHT_KEY)) {
            playerState = PlayerState.STANDING;
        }

        // if jump key is pressed, player enters JUMPING state
        if (Keyboard.isKeyDown(JUMP_KEY) && !keyLocker.isKeyLocked(JUMP_KEY)) {
            keyLocker.lockKey(JUMP_KEY);
            playerState = PlayerState.JUMPING;
        }

        // if crouch key is pressed,
        else if (Keyboard.isKeyDown(CROUCH_KEY)) {
            playerState = PlayerState.CROUCHING;
        }
    }

    // player CROUCHING state logic
    protected void playerCrouching() {
        // if crouch key is released, player enters STANDING state
        if (Keyboard.isKeyUp(CROUCH_KEY)) {
            playerState = PlayerState.STANDING;
        }

        // if jump key is pressed, player enters JUMPING state
        if (Keyboard.isKeyDown(JUMP_KEY) && !keyLocker.isKeyLocked(JUMP_KEY)) {
            keyLocker.lockKey(JUMP_KEY);
            playerState = PlayerState.JUMPING;
        }
    }

    // player JUMPING state logic
    protected void playerJumping() {
        // if last frame player was on ground and this frame player is still on ground, the jump needs to be setup
        if (previousAirGroundState == AirGroundState.GROUND && airGroundState == AirGroundState.GROUND) {

            // sets animation to a JUMP animation based on which way player is facing
            currentAnimationName = facingDirection == Direction.RIGHT ? "JUMP_RIGHT" : "JUMP_LEFT";

            // player is set to be in air and then player is sent into the air
            airGroundState = AirGroundState.AIR;
            jumpForce = jumpHeight;
            if (jumpForce > 0) {
                moveAmountY -= jumpForce;
                jumpForce -= jumpDegrade;
                if (jumpForce < 0) {
                    jumpForce = 0;
                }
            }
        }

        // if player is in air (currently in a jump) and has more jumpForce, continue sending player upwards
        else if (airGroundState == AirGroundState.AIR) {
            if (jumpForce > 0) {
                moveAmountY -= jumpForce;
                jumpForce -= jumpDegrade;
                if (jumpForce < 0) {
                    jumpForce = 0;
                }
            }

            // allows you to move left and right while in the air
            if (Keyboard.isKeyDown(MOVE_LEFT_KEY)) {
                moveAmountX -= walkSpeed;
            } else if (Keyboard.isKeyDown(MOVE_RIGHT_KEY)) {
                moveAmountX += walkSpeed;
            }

            // if player is falling, increases momentum as player falls so it falls faster over time
            if (moveAmountY > 0) {
                increaseMomentum();
            }
        }

        // if player last frame was in air and this frame is now on ground, player enters STANDING state
        else if (previousAirGroundState == AirGroundState.AIR && airGroundState == AirGroundState.GROUND) {
            playerState = PlayerState.STANDING;
        }
    }

    // while player is in air, this is called, and will increase momentumY by a set amount until player reaches terminal velocity
    protected void increaseMomentum() {
        momentumY += momentumYIncrease;
        if (momentumY > terminalVelocityY) {
            momentumY = terminalVelocityY;
        }
    }

    protected void updateLockedKeys() {
        if (Keyboard.isKeyUp(JUMP_KEY)) {
            keyLocker.unlockKey(JUMP_KEY);
        }
        if (Keyboard.isKeyUp(ATTACK_KEY)) {
            keyLocker.unlockKey(ATTACK_KEY);
        }
    }

    // anything extra the player should do based on interactions can be handled here
    protected void handlePlayerAnimation() {
        if (playerState == PlayerState.STANDING) {
            // sets animation to a STAND animation based on which way player is facing
            this.currentAnimationName = facingDirection == Direction.RIGHT ? "STAND_RIGHT" : "STAND_LEFT";

            // handles putting goggles on when standing in water
            // checks if the center of the player is currently touching a water tile
            int centerX = Math.round(getBounds().getX1()) + Math.round(getBounds().getWidth() / 2f);
            int centerY = Math.round(getBounds().getY1()) + Math.round(getBounds().getHeight() / 2f);
            MapTile currentMapTile = map.getTileByPosition(centerX, centerY);
            if (currentMapTile != null && currentMapTile.getTileType() == TileType.WATER) {
                this.currentAnimationName = facingDirection == Direction.RIGHT ? "SWIM_STAND_RIGHT" : "SWIM_STAND_LEFT";
            }
        }
        else if (playerState == PlayerState.WALKING) {
            // sets animation to a WALK animation based on which way player is facing
            this.currentAnimationName = facingDirection == Direction.RIGHT ? "WALK_RIGHT" : "WALK_LEFT";
        }
        else if (playerState == PlayerState.CROUCHING) {
            // sets animation to a CROUCH animation based on which way player is facing
            this.currentAnimationName = facingDirection == Direction.RIGHT ? "CROUCH_RIGHT" : "CROUCH_LEFT";
        }
        else if (playerState == PlayerState.JUMPING) {
            // if player is moving upwards, set player's animation to jump. if player moving downwards, set player's animation to fall
            if (lastAmountMovedY <= 0) {
                this.currentAnimationName = facingDirection == Direction.RIGHT ? "JUMP_RIGHT" : "JUMP_LEFT";
            } else {
                this.currentAnimationName = facingDirection == Direction.RIGHT ? "FALL_RIGHT" : "FALL_LEFT";
            }
        }
    }

    @Override
    public void onEndCollisionCheckX(boolean hasCollided, Direction direction, MapEntity entityCollidedWith) { }

    @Override
    public void onEndCollisionCheckY(boolean hasCollided, Direction direction, MapEntity entityCollidedWith) {
        // if player collides with a map tile below it, it is now on the ground
        // if player does not collide with a map tile below, it is in air
        if (direction == Direction.DOWN) {
            if (hasCollided) {
                momentumY = 0;
                airGroundState = AirGroundState.GROUND;
            } else {
                playerState = PlayerState.JUMPING;
                airGroundState = AirGroundState.AIR;
            }
        }

        // if player collides with map tile upwards, it means it was jumping and then hit into a ceiling -- immediately stop upwards jump velocity
        else if (direction == Direction.UP) {
            if (hasCollided) {
                jumpForce = 0;
            }
        }
    }

    // other entities can call this method to hurt the player
    public void hurtPlayer(MapEntity mapEntity) {
        if (!isInvincible) {
            // if map entity is an enemy, kill player on touch
            if (mapEntity instanceof Enemy) {
                // take one hit
                damage(1, true);
            }
        }
    }

    //If health reaches zero, tell the player that they died
    public void damage(int amount, boolean notifyListeners) {
        if (isInvincible || levelState == LevelState.PLAYER_DEAD) return;
        health -= amount;
        if (health < 0) health = 0;
        if (notifyListeners) {
            for (PlayerListener listener : listeners) {
                listener.onHurt(this, amount);
            }
        }
        if (health == 0) {
            levelState = LevelState.PLAYER_DEAD;
            win = false;
            System.out.println("Player died");
            for (PlayerListener listener : listeners) {
                listener.onDeath();
                updatePlayerDead();
            }
        }
    }

    // convenience method: notify listeners by default
    public void damage(int amount) { damage(amount, true); }

    // Heal the player by the given amount (potential feature for a future sprint).
    public void heal(int amount) {
        if (amount <= 0) return;
        health += amount;
        if (health > maxHealth) health = maxHealth;
    }

    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }

    public boolean winner(){
        return win;
    }

    // other entities can call this to tell the player they beat a level
    public void completeLevel() {
        levelState = LevelState.LEVEL_COMPLETED;
    }

    // if player has beaten level, this will be the update cycle
    public void updateLevelCompleted() {
        // if player is not on ground, player should fall until it touches the ground
        if (airGroundState != AirGroundState.GROUND && map.getCamera().containsDraw(this)) {
            currentAnimationName = "FALL_RIGHT";
            applyGravity();
            increaseMomentum();
            super.update();
            moveYHandleCollision(moveAmountY);
        }
        // move player to the right until it walks off screen
        else if (map.getCamera().containsDraw(this)) {
            currentAnimationName = "WALK_RIGHT";
            super.update();
            moveXHandleCollision(walkSpeed);
        } else {
            // tell all player listeners that the player has finished the level
            for (PlayerListener listener : listeners) {
                listener.onLevelCompleted();
            }
        }
    }

    // if player has died, this will be the update cycle
    public void updatePlayerDead() {
        // change player animation to DEATH
        if (!currentAnimationName.startsWith("DEATH")) {
            if (facingDirection == Direction.RIGHT) {
                currentAnimationName = "DEATH_RIGHT";
            } else {
                currentAnimationName = "DEATH_LEFT";
            }
            super.update();
        }
        // if death animation not on last frame yet, continue to play out death animation
        else if (currentFrameIndex != getCurrentAnimation().length - 1) {
          super.update();
        }
        // if death animation on last frame (it is set up not to loop back to start), player should continually fall until it goes off screen
        else if (currentFrameIndex == getCurrentAnimation().length - 1) {
            if (map.getCamera().containsDraw(this)) {
                moveY(3);
            } else {
                // tell all player listeners that the player has died in the level
                for (PlayerListener listener : listeners) {
                    listener.onDeath();
                }
            }
        }
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public AirGroundState getAirGroundState() {
        return airGroundState;
    }

    public Direction getFacingDirection() {
        return facingDirection;
    }

    public void setFacingDirection(Direction facingDirection) {
        this.facingDirection = facingDirection;
    }

    public void setLevelState(LevelState levelState) {
        this.levelState = levelState;
    }

    public void addListener(PlayerListener listener) {
        listeners.add(listener);
    }

    // 👇 ADDED: expose whether Player2 is currently in the JUMPING state
    public boolean isJumpingState() {
        return playerState == PlayerState.JUMPING;
    }

    public void draw(GraphicsHandler graphicsHandler) {
        super.draw(graphicsHandler);
        // drawBounds(graphicsHandler, new Color(0, 255, 0, 100));
        
        // Draw current projectile name above Player 2 (use Player 2's projectile type)
        String projectileName = ProjectileAttack.getPlayer2ProjectileName();
        SpriteFont projectileLabel = new SpriteFont(projectileName, 
            getX() + (getWidth() / 2) - (projectileName.length() * 4), // Center the text above player
            getY() - 20, // Position 20 pixels above player
            "Arial", 12, Color.WHITE);
        projectileLabel.setOutlineColor(Color.BLACK);
        projectileLabel.setOutlineThickness(1);
        
        // Adjust position relative to camera (important for moving camera)
        if (map != null) {
            projectileLabel.setLocation(
                projectileLabel.getX() - map.getCamera().getX(),
                projectileLabel.getY() - map.getCamera().getY()
            );
        }
        
        projectileLabel.draw(graphicsHandler);
    }

    // Getters and setters for power-up system
    public float getWalkSpeed() {
        return walkSpeed;
    }
    
    public void setWalkSpeed(float walkSpeed) {
        this.walkSpeed = walkSpeed;
    }
    
    public float getJumpHeight() {
        return jumpHeight;
    }
    
    public void setJumpHeight(float jumpHeight) {
        this.jumpHeight = jumpHeight;
    }
    
    public float getGravity() {
        return gravity;
    }
    
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }
    
    // Power-up system methods
    private void updatePowerUps() {
        // Initialize original values if not done yet
        initializePowerUpSystem();
        
        // Handle power-up selection toggle cooldown
        if (powerUpToggleCooldown > 0) {
            powerUpToggleCooldown -= 16;
        }
        
        // Handle power-up selection (7 and 8 keys when selection is visible)
        if (powerUpSelectionVisible) {
            if (Keyboard.isKeyDown(Key.SEVEN)) {
                selectedPowerUp = 0; // Speed boost
                activateSpeedBoost();
                powerUpSelectionVisible = false;
            } else if (Keyboard.isKeyDown(Key.EIGHT)) {
                selectedPowerUp = 1; // High jump
                activateHighJump();
                powerUpSelectionVisible = false;
            }
        }
        
        // Update active power-ups
        if (powerUpDurationMs > 0) {
            powerUpDurationMs -= 16;
            if (powerUpDurationMs <= 0) {
                deactivatePowerUps();
            }
        }
    }
    
    private void activateSpeedBoost() {
        if (!speedBoostActive) {
            speedBoostActive = true;
            walkSpeed = originalWalkSpeed * 2.0f; // Double speed
            powerUpDurationMs = 8000; // 8 seconds
        }
    }
    
    private void activateHighJump() {
        if (!highJumpActive) {
            highJumpActive = true;
            jumpHeight = originalJumpHeight * 1.5f; // 50% higher jump
            powerUpDurationMs = 8000; // 8 seconds
        }
    }
    
    private void deactivatePowerUps() {
        speedBoostActive = false;
        highJumpActive = false;
        walkSpeed = originalWalkSpeed;
        jumpHeight = originalJumpHeight;
        powerUpDurationMs = 0;
    }
    
    // Getters for power-up state
    public boolean isPowerUpSelectionVisible() { return powerUpSelectionVisible; }
    public int getSelectedPowerUp() { return selectedPowerUp; }
    public boolean isSpeedBoostActive() { return speedBoostActive; }
    public boolean isHighJumpActive() { return highJumpActive; }
    public int getPowerUpRemainingMs() { return powerUpDurationMs; }
    
    // Method to show power-up selection (called automatically)
    public void showPowerUpSelection() {
        powerUpSelectionVisible = true;
    }
    
    // Method to hide power-up selection (called on timeout)
    public void hidePowerUpSelection() {
        powerUpSelectionVisible = false;
    }
}
