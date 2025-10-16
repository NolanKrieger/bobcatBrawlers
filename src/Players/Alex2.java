package Players;

import Builders.FrameBuilder;
import Engine.ImageLoader;
import GameObject.Frame;
import GameObject.ImageEffect;
import GameObject.SpriteSheet;
import Level.Player2;


import java.util.HashMap;

public class Alex2 extends Player2 {

    public Alex2(float x, float y) {
        super(new SpriteSheet(ImageLoader.load("catplusalex.jpg"), 16, 16), x, y, "STAND_RIGHT");

        gravity = 0.6f;
        terminalVelocityY = 8f;
        walkSpeed = 1.6f;
        jumpHeight = 13.5f;
        jumpDegrade = 0.5f;
    }

    private static FrameBuilder fb(SpriteSheet s, int col, int row) {
        return new FrameBuilder(s.getSprite(col, row))
                .withScale(3)
                .withBounds(3, 2, 10, 14);
    }

    @Override
    public HashMap<String, Frame[]> loadAnimations(SpriteSheet s) {
        return new HashMap<String, Frame[]>() {{

            put("STAND_RIGHT", new Frame[] {
                fb(s, 0, 0).build()
            });

            put("WALK_RIGHT", new Frame[] {
                fb(s, 1, 0).build(),
                fb(s, 2, 0).build(),
                fb(s, 3, 0).build()
            });

            put("JUMP_RIGHT", new Frame[] {
                fb(s, 1, 6).build()
            });

            put("FALL_RIGHT", new Frame[] {
                fb(s, 2, 6).build()
            });

            put("CROUCH_RIGHT", new Frame[] {
                fb(s, 0, 5).build()
            });

            put("IDLE2_RIGHT", new Frame[] {
                fb(s, 0, 1).build()
            });

            put("STAND_TO_WALK_RIGHT", new Frame[] {
                fb(s, 0, 2).build(),
                fb(s, 0, 3).build(),
                fb(s, 0, 4).build()
            });

            put("STAND_LEFT", new Frame[] {
                fb(s, 0, 0).withImageEffect(ImageEffect.FLIP_HORIZONTAL).build()
            });

            put("WALK_LEFT", new Frame[] {
                fb(s, 1, 0).withImageEffect(ImageEffect.FLIP_HORIZONTAL).build(),
                fb(s, 2, 0).withImageEffect(ImageEffect.FLIP_HORIZONTAL).build(),
                fb(s, 3, 0).withImageEffect(ImageEffect.FLIP_HORIZONTAL).build()
            });

            put("JUMP_LEFT", new Frame[] {
                fb(s, 1, 6).withImageEffect(ImageEffect.FLIP_HORIZONTAL).build()
            });

            put("FALL_LEFT", new Frame[] {
                fb(s, 2, 6).withImageEffect(ImageEffect.FLIP_HORIZONTAL).build()
            });

            put("CROUCH_LEFT", new Frame[] {
                fb(s, 0, 5).withImageEffect(ImageEffect.FLIP_HORIZONTAL).build()
            });

            put("IDLE2_LEFT", new Frame[] {
                fb(s, 0, 1).withImageEffect(ImageEffect.FLIP_HORIZONTAL).build()
            });

            put("STAND_TO_WALK_LEFT", new Frame[] {
                fb(s, 0, 2).withImageEffect(ImageEffect.FLIP_HORIZONTAL).build(),
                fb(s, 0, 3).withImageEffect(ImageEffect.FLIP_HORIZONTAL).build(),
                fb(s, 0, 4).withImageEffect(ImageEffect.FLIP_HORIZONTAL).build()
            });
        }};
    }
}
