package Screens;

import Level.Player;
import Engine.*;
import SpriteFont.SpriteFont;
import Game.ScoreboardNew;
import Game.Lives;
import java.awt.*;

// This is the class for the level lose screen
public class LevelLoseScreen extends Screen {
    protected SpriteFont loseMessage;
    protected SpriteFont scoreboardDisplay;
    protected SpriteFont instructions;
    protected KeyLocker keyLocker = new KeyLocker();
    protected PlayLevelScreen playLevelScreen;

    private static ScoreboardNew scoreboard = new ScoreboardNew();
    private static Lives lives = new Lives();

    public LevelLoseScreen(PlayLevelScreen playLevelScreen) {
        this.playLevelScreen = playLevelScreen;
    }

    @Override
    public void initialize() {
        int player1Health = playLevelScreen.getPlayer().getHealth();
        int player2Health = playLevelScreen.getPlayer2().getHealth();
        
        if (player1Health > 0) {
            // Player 1 wins - Player 2 loses a life
            scoreboard.addPlayer1Win();
            lives.losePlayer2Life();
            
            // If Player 2 still has lives, respawn immediately
            if (lives.getPlayer2Lives() > 0) {
                playLevelScreen.resetLevel();
                return;
            }
            
            loseMessage = new SpriteFont("Player 2 Out of Lives! Player 1 Wins!", 300, 300, "Arial", 30, Color.white);
            scoreboardDisplay = new SpriteFont("Scoreboard: " + scoreboard.toString() + " | Lives: " + lives.toString(), 350, 340, "Arial", 20, Color.white);

        } else if (player2Health > 0) {
            // Player 2 wins - Player 1 loses a life
            scoreboard.addPlayer2Win();
            lives.losePlayer1Life();
            
            // If Player 1 still has lives, respawn immediately
            if (lives.getPlayer1Lives() > 0) {
                playLevelScreen.resetLevel();
                return;
            }
            
            loseMessage = new SpriteFont("Player 1 Out of Lives! Player 2 Wins!", 300, 300, "Arial", 30, Color.white);
            scoreboardDisplay = new SpriteFont("Scoreboard: " + scoreboard.toString() + " | Lives: " + lives.toString(), 350, 340, "Arial", 20, Color.white);
            
        } else {
            loseMessage = new SpriteFont("You lose!", 520, 300, "Arial", 30, Color.white);
            scoreboardDisplay = null;
        }
        
        instructions = new SpriteFont("Press Space to go back to menu", 350, 380, "Arial", 20, Color.white);
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
        if (scoreboardDisplay != null) {
            scoreboardDisplay.draw(graphicsHandler);
        }
        instructions.draw(graphicsHandler);
    }
}
