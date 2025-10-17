package Screens;

import Engine.*;
import Game.GameState;
import Game.ScreenCoordinator;
import Level.Map;
import Maps.TitleScreenMap;
import SpriteFont.SpriteFont;

import java.awt.*;
import java.awt.image.BufferedImage;

public class MenuScreen extends Screen {
    protected ScreenCoordinator screenCoordinator;
    protected int currentMenuItemHovered = 0; // 0..3
    protected int menuItemSelected = -1;

    protected SpriteFont playGame;
    protected SpriteFont tutorial;
    protected SpriteFont characterSelect;
    protected SpriteFont characterSelect2;

    protected SpriteFont credits;

    // Fallback animated map
    protected Map background;

    // PNG background (optional)
    protected BufferedImage backgroundImage;
    protected String backgroundImagePath;

    protected int keyPressTimer;
    protected int pointerLocationX, pointerLocationY;
    protected KeyLocker keyLocker = new KeyLocker();

    // Old behavior (no PNG)
    public MenuScreen(ScreenCoordinator screenCoordinator) {
        this(screenCoordinator, null);
    }

    // New: pass a PNG path relative to Config.RESOURCES_PATH (no "Resources/" prefix!)
    // Example: "Images/menus/bobcat_brawlers_bg.png"
    public MenuScreen(ScreenCoordinator screenCoordinator, String backgroundImagePath) {
        this.screenCoordinator = screenCoordinator;
        this.backgroundImagePath = backgroundImagePath;
    }

    @Override
    public void initialize() {
        playGame = new SpriteFont("PLAY GAME", 200, 123, "Arial", 30, new Color(49, 207, 240));
        playGame.setOutlineColor(Color.black);
        playGame.setOutlineThickness(3);

        tutorial = new SpriteFont("TUTORIAL", 200, 223, "Arial", 30, new Color(49, 207, 240));
        tutorial.setOutlineColor(Color.black);
        tutorial.setOutlineThickness(3);

        characterSelect = new SpriteFont("CHARACTER SELECT", 200, 323, "Arial", 30, new Color(49, 207, 240));
        characterSelect.setOutlineColor(Color.black);
        characterSelect.setOutlineThickness(3);

        credits = new SpriteFont("CREDITS", 200, 423, "Arial", 30, new Color(49, 207, 240));
        credits.setOutlineColor(Color.black);
        credits.setOutlineThickness(3);

        // Fallback animated map
        background = new TitleScreenMap();
        background.setAdjustCamera(false);

        // Try to load PNG if a path was provided
        backgroundImage = null;
        if (backgroundImagePath != null && !backgroundImagePath.isEmpty()) {
            try {
                // IMPORTANT: Your ImageLoader expects a path RELATIVE to Config.RESOURCES_PATH
                // e.g., "Images/menus/bobcat_brawlers_bg.png"
                backgroundImage = ImageLoader.load(backgroundImagePath);
            } catch (RuntimeException e) {
                System.out.println("MenuScreen: PNG background failed to load, using map fallback. Path: " + backgroundImagePath);
            }
        }

        keyPressTimer = 0;
        menuItemSelected = -1;
        keyLocker.lockKey(Key.SPACE);
    }

    public void update() {
        // Keep map animations alive even if we draw an image
        background.update(null);

        if (Keyboard.isKeyDown(Key.DOWN) && keyPressTimer == 0) {
            keyPressTimer = 14; currentMenuItemHovered++;
        } else if (Keyboard.isKeyDown(Key.UP) && keyPressTimer == 0) {
            keyPressTimer = 14; currentMenuItemHovered--;
        } else {
            if (keyPressTimer > 0) keyPressTimer--;
        }

        if (currentMenuItemHovered > 3) currentMenuItemHovered = 0;
        else if (currentMenuItemHovered < 0) currentMenuItemHovered = 3;

        if (currentMenuItemHovered == 0) {
            playGame.setColor(new Color(255, 215, 0));
            tutorial.setColor(new Color(49, 207, 240));
            characterSelect.setColor(new Color(49, 207, 240));
            credits.setColor(new Color(49, 207, 240));
            pointerLocationX = 170; pointerLocationY = 130;
        } else if (currentMenuItemHovered == 1) {
            playGame.setColor(new Color(49, 207, 240));
            tutorial.setColor(new Color(255, 215, 0));
            characterSelect.setColor(new Color(49, 207, 240));
            credits.setColor(new Color(49, 207, 240));
            pointerLocationX = 170; pointerLocationY = 230;
        } else if (currentMenuItemHovered == 2) {
            playGame.setColor(new Color(49, 207, 240));
            tutorial.setColor(new Color(49, 207, 240));
            characterSelect.setColor(new Color(255, 215, 0));
            credits.setColor(new Color(49, 207, 240));
            pointerLocationX = 170; pointerLocationY = 330;
        } else {
            playGame.setColor(new Color(49, 207, 240));
            tutorial.setColor(new Color(49, 207, 240));
            characterSelect.setColor(new Color(49, 207, 240));
            credits.setColor(new Color(255, 215, 0));
            pointerLocationX = 170; pointerLocationY = 430;
        }

        if (Keyboard.isKeyUp(Key.SPACE)) keyLocker.unlockKey(Key.SPACE);
        if (!keyLocker.isKeyLocked(Key.SPACE) && Keyboard.isKeyDown(Key.SPACE)) {
            menuItemSelected = currentMenuItemHovered;
            if (menuItemSelected == 0) screenCoordinator.setGameState(GameState.CHARACTER_SELECT2);
            else if (menuItemSelected == 1) screenCoordinator.setGameState(GameState.TUTORIAL);
            else if (menuItemSelected == 2) screenCoordinator.setGameState(GameState.CHARACTER_SELECT);
            else if (menuItemSelected == 3) screenCoordinator.setGameState(GameState.CREDITS);
        }
    }

    public void draw(GraphicsHandler graphicsHandler) {
        if (backgroundImage != null) {
            int w = ScreenManager.getScreenWidth();
            int h = ScreenManager.getScreenHeight();
            graphicsHandler.drawImage(backgroundImage, 0, 0, w, h);
        } else {
            background.draw(graphicsHandler);
        }

        playGame.draw(graphicsHandler);
        tutorial.draw(graphicsHandler);
        characterSelect.draw(graphicsHandler);
        credits.draw(graphicsHandler);

        graphicsHandler.drawFilledRectangleWithBorder(
                pointerLocationX, pointerLocationY, 20, 20,
                new Color(49, 207, 240), Color.black, 2
        );
    }
}
