
package Players;
import Engine.Keyboard;
import Engine.Key;

import Builders.FrameBuilder;
import Engine.GraphicsHandler;
import Engine.ImageLoader;
import GameObject.Frame;
import GameObject.ImageEffect;
import GameObject.SpriteSheet;
import Level.Player;

import java.util.HashMap;

// This is the class for the Cat player character
// basically just sets some values for physics and then defines animations

public class Nicolini extends Player {
        private boolean shieldActive = false;
        private boolean shieldUsed = false;
        private int shieldCooldownMs = 0;
        private static final int SHIELD_DURATION_MS = 15000; // 15 seconds
    private static final String SHIELD_IMAGE = "shield.png";
    private transient java.awt.image.BufferedImage shieldImage = null;

    public Nicolini(float x, float y) {
        super(new SpriteSheet(ImageLoader.load("nicolinisprite.png"), 24, 24), x, y, "STAND_RIGHT");
        gravity = 1.0f;
        terminalVelocityY = 8f;
        jumpHeight = 16f;
        jumpDegrade = .5f;
        walkSpeed = 5.0f;
        momentumYIncrease = .5f;
        try {
            shieldImage = ImageLoader.load(SHIELD_IMAGE);
        } catch (Exception ignored) {}
    }

    @Override
    public void update() {
                super.update();
                // Activate shield on 'C' key if available (only once per game)
                if (Keyboard.isKeyDown(Key.C) && !shieldActive && !shieldUsed) {
                        shieldActive = true;
                        shieldUsed = true;
                        shieldCooldownMs = SHIELD_DURATION_MS;
                }
                // Shield timer
                if (shieldActive) {
                        shieldCooldownMs -= 16;
                        if (shieldCooldownMs <= 0) {
                                shieldActive = false;
                        }
                }
    }

    @Override
    public void draw(GraphicsHandler graphicsHandler) {
                super.draw(graphicsHandler);
                // Draw shield if active (to the right of the character)
                if (shieldActive && shieldImage != null) {
                        int shieldX = Math.round(getX() - map.getCamera().getX() + getWidth());
                        int shieldY = Math.round(getY() - map.getCamera().getY());
                        graphicsHandler.drawImage(shieldImage, shieldX, shieldY, getWidth(), getHeight());
                }
        // Optionally: draw shield uses left above player
        /*SpriteFont shieldLabel = new SpriteFont("Shields: " + shieldUsesLeft, shieldX, shieldY - 20, "Arial", 12, Color.CYAN);
        shieldLabel.draw(graphicsHandler);*/
    }

    // Reflect projectiles if shield is active (call from collision logic)
    public boolean canReflectProjectiles() {
        return shieldActive;
    }

    // Reset shield uses when a heart/life is lost
    @Override
    public void damage(int amount, boolean notifyListeners) {
        int oldHearts = getHealth() / (getMaxHealth() / 3);
        super.damage(amount, notifyListeners);
        int newHearts = getHealth() / (getMaxHealth() / 3);
        if (newHearts < oldHearts) {
        }
    }

    @Override
    public HashMap<String, Frame[]> loadAnimations(SpriteSheet spriteSheet) {
        // ...existing code...
        return new HashMap<String, Frame[]>() {{
            put("STAND_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(1, 3))
                            .withScale(5)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });
            put("STAND_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(1, 3))
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });
            put("WALK_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(3, 0), 14)
                            .withScale(5)
                            .withBounds(8, 9, 10, 10)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(4, 0), 14)
                            .withScale(5)
                            .withBounds(8, 9, 10, 10)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(3, 0), 14)
                            .withScale(5)
                            .withBounds(8, 9, 10, 10)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(4, 0), 14)
                            .withScale(5)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });
            put("WALK_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(3, 0), 14)
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(8, 9, 10, 10)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(4, 0), 14)
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(8, 9, 10, 10)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(3, 0), 14)
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(8, 9, 10, 10)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(4, 0), 14)
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });
            put("JUMP_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(1, 1))
                            .withScale(5)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });
            put("JUMP_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(1, 1))
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });
            put("FALL_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(1, 1))
                            .withScale(5)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });
            put("FALL_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(1, 1))
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });
            put("CROUCH_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(1, 1))
                            .withScale(5)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });
            put("CROUCH_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(1, 1))
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });
            put("DEATH_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(1, 2), 8)
                            .withScale(5)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(1, 2), 8)
                            .withScale(5)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(1, 2), -1)
                            .withScale(5)
                            .build()
            });
            put("DEATH_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(1, 2), 8)
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(1, 2), 8)
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(1, 2), -1)
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .build()
            });
            put("PUNCH_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(1, 0))
                            .withScale(5)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });
            put("PUNCH_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(1, 2))
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });
        }};
    }
}
