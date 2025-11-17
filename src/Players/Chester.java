package Players;

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
public class Chester extends Player {

    public Chester(float x, float y) {
        super(new SpriteSheet(ImageLoader.load("chestersprite.png"), 24, 24), x, y, "STAND_RIGHT");
        gravity = 1.0f;
        terminalVelocityY = 8f;
        jumpHeight = 10f;
        jumpDegrade = .5f;
        walkSpeed = 3.0f;
        momentumYIncrease = .5f;
                // Chester deals more damage than the other characters
                this.attackDamage = 2;
    }

    

    public void update() {
        super.update();
    }

    public void draw(GraphicsHandler graphicsHandler) {
        super.draw(graphicsHandler);
        //drawBounds(graphicsHandler, new Color(255, 0, 0, 170));
    }

    @Override
    public HashMap<String, Frame[]> loadAnimations(SpriteSheet spriteSheet) {
        return new HashMap<String, Frame[]>() {{
            put("STAND_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(0, 0))
                            .withScale(5)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });

            put("STAND_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(0, 0))
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });

            put("WALK_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(0, 0), 14)
                            .withScale(5)
                            .withBounds(8, 9, 10, 10)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(1, 0), 14)
                            .withScale(5)
                            .withBounds(8, 9, 10, 10)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(2, 0), 14)
                            .withScale(5)
                            .withBounds(8, 9, 10, 10)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(3, 0), 14)
                            .withScale(5)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });

            put("WALK_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(0, 0), 14)
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(8, 9, 10, 10)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(1, 0), 14)
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(8, 9, 10, 10)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(2, 0), 14)
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(8, 9, 10, 10)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(3, 0), 14)
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });

            put("JUMP_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(0, 0))
                            .withScale(5)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });

            put("JUMP_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(0, 0))
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });

            put("FALL_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(3, 0))
                            .withScale(5)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });

            put("FALL_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(3, 0))
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });

            put("CROUCH_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(3, 0))
                            .withScale(5)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });

            put("CROUCH_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(3, 0))
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });

            put("DEATH_RIGHT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(1, 0), 8)
                            .withScale(5)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(1, 0), 8)
                            .withScale(5)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(1, 0), -1)
                            .withScale(5)
                            .build()
            });

            put("DEATH_LEFT", new Frame[] {
                    new FrameBuilder(spriteSheet.getSprite(1, 0), 8)
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(1, 0), 8)
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .build(),
                    new FrameBuilder(spriteSheet.getSprite(1, 0), -1)
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
                    new FrameBuilder(spriteSheet.getSprite(1, 0))
                            .withScale(5)
                            .withImageEffect(ImageEffect.FLIP_HORIZONTAL)
                            .withBounds(8, 9, 10, 10)
                            .build()
            });
        }};
    }
}
