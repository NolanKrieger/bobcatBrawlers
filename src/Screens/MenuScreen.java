package Screens;

import Engine.*;
import Game.GameState;
import Game.ScreenCoordinator;
import Level.Map;
import Maps.TitleScreenMap;
import SpriteFont.SpriteFont;

import java.awt.*;

// This is the class for the main menu screen
public class MenuScreen extends Screen {
    protected ScreenCoordinator screenCoordinator;
    protected int currentMenuItemHovered = 0; // current menu item being "hovered" over
    protected int menuItemSelected = -1;
    protected SpriteFont playGame;
    protected SpriteFont tutorial;   // New tutorial option
    protected SpriteFont credits;
    protected Map background;
    protected int keyPressTimer;
    protected int pointerLocationX, pointerLocationY;
    protected KeyLocker keyLocker = new KeyLocker();

    public MenuScreen(ScreenCoordinator screenCoordinator) {
        this.screenCoordinator = screenCoordinator;
    }

    @Override
    public void initialize() {
        playGame = new SpriteFont("PLAY GAME", 200, 123, "Arial", 30, new Color(49, 207, 240));
        playGame.setOutlineColor(Color.black);
        playGame.setOutlineThickness(3);

        tutorial = new SpriteFont("TUTORIAL", 200, 223, "Arial", 30, new Color(49, 207, 240)); // Added the tutorial option to the main menu.
        tutorial.setOutlineColor(Color.black);
        tutorial.setOutlineThickness(3);

        credits = new SpriteFont("CREDITS", 200, 323, "Arial", 30, new Color(49, 207, 240));
        credits.setOutlineColor(Color.black);
        credits.setOutlineThickness(3);

        background = new TitleScreenMap();
        background.setAdjustCamera(false);
        keyPressTimer = 0;
        menuItemSelected = -1;
        keyLocker.lockKey(Key.SPACE);
    }

    public void update() {
        // update background map (to play tile animations)
        background.update(null);

        // navigate menu
        if (Keyboard.isKeyDown(Key.DOWN) && keyPressTimer == 0) {
            keyPressTimer = 14;
            currentMenuItemHovered++;
        } else if (Keyboard.isKeyDown(Key.UP) && keyPressTimer == 0) {
            keyPressTimer = 14;
            currentMenuItemHovered--;
        } else {
            if (keyPressTimer > 0) keyPressTimer--;
        }

        // loop selection (now 3 items: 0..2)
        if (currentMenuItemHovered > 2) {
            currentMenuItemHovered = 0;
        } else if (currentMenuItemHovered < 0) {
            currentMenuItemHovered = 2;
        }

        // set colors and pointer position
        if (currentMenuItemHovered == 0) {
            playGame.setColor(new Color(255, 215, 0));
            tutorial.setColor(new Color(49, 207, 240));
            credits.setColor(new Color(49, 207, 240));
            pointerLocationX = 170;
            pointerLocationY = 130;
        } else if (currentMenuItemHovered == 1) {
            playGame.setColor(new Color(49, 207, 240));
            tutorial.setColor(new Color(255, 215, 0));
            credits.setColor(new Color(49, 207, 240));
            pointerLocationX = 170;
            pointerLocationY = 230;
        } else { // currentMenuItemHovered == 2
            playGame.setColor(new Color(49, 207, 240));
            tutorial.setColor(new Color(49, 207, 240));
            credits.setColor(new Color(255, 215, 0));
            pointerLocationX = 170;
            pointerLocationY = 330;
        }

        // selection
        if (Keyboard.isKeyUp(Key.SPACE)) {
            keyLocker.unlockKey(Key.SPACE);
        }
        if (!keyLocker.isKeyLocked(Key.SPACE) && Keyboard.isKeyDown(Key.SPACE)) {
            menuItemSelected = currentMenuItemHovered;
            if (menuItemSelected == 0) {
                screenCoordinator.setGameState(GameState.LEVEL);
            } else if (menuItemSelected == 1) {
                screenCoordinator.setGameState(GameState.TUTORIAL); // NEW route
            } else if (menuItemSelected == 2) {
                screenCoordinator.setGameState(GameState.CREDITS);
            }
        }
    }

    public void draw(GraphicsHandler graphicsHandler) {
        background.draw(graphicsHandler);
        playGame.draw(graphicsHandler);
        tutorial.draw(graphicsHandler);  // NEW
        credits.draw(graphicsHandler);
        graphicsHandler.drawFilledRectangleWithBorder(
                pointerLocationX, pointerLocationY, 20, 20,
                new Color(49, 207, 240), Color.black, 2
        );
    }
}
