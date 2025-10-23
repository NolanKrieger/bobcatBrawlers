package Screens;

import Level.Player;
import Engine.*;
import SpriteFont.SpriteFont;

import java.awt.*;

// This is the class for the level lose screen
public class LevelLoseScreen extends Screen {
    protected SpriteFont loseMessage;
    protected SpriteFont instructions;
    protected KeyLocker keyLocker = new KeyLocker();
    protected PlayLevelScreen playLevelScreen;

    public LevelLoseScreen(PlayLevelScreen playLevelScreen) {
        this.playLevelScreen = playLevelScreen;
    }

    @Override
    public void initialize() {
        int player1Health = playLevelScreen.getPlayer().getHealth();
        int player2Health = playLevelScreen.getPlayer2().getHealth();
        
        
        
        if (player1Health > 0) {
            loseMessage = new SpriteFont("Player 2 Loses! Player 1 Wins!", 420, 300, "Arial", 30, Color.white);
        } else if (player2Health > 0) {
            loseMessage = new SpriteFont("Player 1 Loses! Player 2 Wins!", 420, 300, "Arial", 30, Color.white);
        } else {
            loseMessage = new SpriteFont("You lose!", 520, 300, "Arial", 30, Color.white);
        }
        
        instructions = new SpriteFont("Press Space to try again or Escape to go back to the main menu", 320, 330,"Arial", 20, Color.white);
        keyLocker.lockKey(Key.SPACE);
        keyLocker.lockKey(Key.ESC);
    }

    @Override
    public void update() {
        if (Keyboard.isKeyUp(Key.SPACE)) {
            keyLocker.unlockKey(Key.SPACE);
        }
        if (Keyboard.isKeyUp(Key.ESC)) {
            keyLocker.unlockKey(Key.ESC);
        }

        // if space is pressed, reset level. if escape is pressed, go back to main menu
        if (Keyboard.isKeyDown(Key.SPACE) && !keyLocker.isKeyLocked(Key.SPACE)) {
            playLevelScreen.resetLevel();
        } else if (Keyboard.isKeyDown(Key.ESC) && !keyLocker.isKeyLocked(Key.ESC)) {
            playLevelScreen.goBackToMenu();
        }
    }

    public void draw(GraphicsHandler graphicsHandler) {
        graphicsHandler.drawFilledRectangle(0, 0, ScreenManager.getScreenWidth(), ScreenManager.getScreenHeight(), Color.black);
        loseMessage.draw(graphicsHandler);
        instructions.draw(graphicsHandler);
    }
}
