package Screens;

import Engine.*;
import Game.GameState;
import Game.ScreenCoordinator;
import Level.Map;
import Maps.TestMap;
import Players.Cat;
import Level.Player;
import Level.PlayerListener;
import GameObject.GameObject;
import Maps.TitleScreenMap;
import SpriteFont.SpriteFont;
import Screens.TutorialLoseScreen;

import java.awt.*;
import java.awt.image.BufferedImage;


public class TutorialScreen extends Screen implements PlayerListener {
   protected ScreenCoordinator screenCoordinator;
   protected Map background;
   protected Map titleBackground;
   protected Map testBackground;
   protected Player player;
   protected boolean showingTitleBackground = true;
   protected KeyLocker keyLocker = new KeyLocker();
   protected SpriteFont tutorialLabel;
   protected SpriteFont movementLabel;
   protected SpriteFont movementDescription;
   protected SpriteFont attackLabel;
   protected SpriteFont underlinedLabel1;
   protected SpriteFont underlinedLabel2;
   protected SpriteFont underlinedLabel3;
   protected SpriteFont rightArrowDescription;
   protected SpriteFont leftArrowDescription;
   protected SpriteFont downArrowDescription;
   protected SpriteFont upArrowDescription;
   protected SpriteFont keyDescription;
   protected SpriteFont returnInstructionsLabel;
   protected BufferedImage rightArrowImage;
   protected BufferedImage leftArrowImage;
   protected BufferedImage downArrowImage;
   protected BufferedImage upArrowImage;
   protected TutorialLoseScreen tutorialLoseScreen;
   protected boolean showingLevelLose = false;

   public TutorialScreen(ScreenCoordinator screenCoordinator) {
       this.screenCoordinator = screenCoordinator;
   }

   @Override
   public void onLevelCompleted() {
    //Nothing for this tutorial
   }
   @Override
   public void onDeath() {
    PlayLevelScreen connect = new PlayLevelScreen(screenCoordinator) {
        @Override
        public void resetLevel() {
            TutorialScreen.this.showingLevelLose = false;
            TutorialScreen.this.tutorialLoseScreen = null;
            TutorialScreen.this.initialize();
        }
        @Override
        public void goBackToMenu() {
            TutorialScreen.this.showingLevelLose = false;
            TutorialScreen.this.tutorialLoseScreen = null;
            TutorialScreen.this.screenCoordinator.setGameState(GameState.MENU);
        }
    };
    tutorialLoseScreen = new TutorialLoseScreen(connect);
    showingLevelLose = true;
   }
   @Override
   public void onHurt(GameObject source, int amount) {
       // tutorial doesn't need this functionality
   }
   @Override
   public void initialize() {
       testBackground = new TestMap();
       testBackground.setAdjustCamera(true);
       background = testBackground;
       showingTitleBackground = false;
       this.player = new Cat(background.getPlayerStartPosition().x, background.getPlayerStartPosition().y);
       this.player.setMap(background);
       this.player.addListener(this);
       tutorialLabel = new SpriteFont("Tutorial", 350, 5, "Times New Roman", 30, Color.white);
       movementLabel = new SpriteFont("Movements", 20, 40, "Times New Roman", 26, Color.white);
       movementDescription = new SpriteFont("Use Arrow Keys: ", 20, 78, "Times New Roman", 20, Color.white);
       attackLabel = new SpriteFont("Attacks", 218, 40, "Times New Roman",26, Color.white);
       underlinedLabel1 = new SpriteFont("_______", 350, 5, "Times New Roman", 30, Color.white);
       underlinedLabel2 = new SpriteFont("________", 20, 38, "Times New Roman", 30, Color.white);
       underlinedLabel3 = new SpriteFont("______", 217, 38, "Times New Roman", 30, Color.white);
       rightArrowDescription = new SpriteFont("- Moves you right", 50, 107, "Times New Roman", 18, Color.white);
       leftArrowDescription = new SpriteFont("- Moves you left", 50, 134, "Times New Roman", 18, Color.white);
       downArrowDescription = new SpriteFont("- Moves you down", 50, 161, "Times New Roman", 18, Color.white);
       upArrowDescription = new SpriteFont("- Moves you up", 50, 188, "Times New Roman", 18, Color.white);
       keyDescription = new SpriteFont("-Use WASD to move and jump", 20, 217, "Times New Roman", 18, Color.white);
       returnInstructionsLabel = new SpriteFont("Press Space to return to the menu", 20, 540, "Times New Roman", 18, Color.white);
       keyLocker.lockKey(Key.SPACE);

       try {
            rightArrowImage = ImageLoader.load("New Piskel-1.png (3).png");
            leftArrowImage = ImageLoader.load("New Piskel-1.png (1).png");
            downArrowImage = ImageLoader.load("New Piskel-1.png (2).png");
            upArrowImage = ImageLoader.load("New Piskel-1.png.png");
       } catch (RuntimeException e) {
            rightArrowImage = null;
            leftArrowImage = null;
            downArrowImage = null;
            upArrowImage = null;
       }
   }


   public void update() {
    if (showingLevelLose && tutorialLoseScreen != null) {
        tutorialLoseScreen.update();
        return;
    }
    if (player != null) {
        player.update();
    }
    background.update(player);
    
    if (Keyboard.isKeyUp(Key.SPACE)) {
        keyLocker.unlockKey(Key.SPACE);
    }
    if (!keyLocker.isKeyLocked(Key.SPACE) && Keyboard.isKeyDown(Key.SPACE)) {
        screenCoordinator.setGameState(GameState.MENU);
    }
}


   public void draw(GraphicsHandler graphicsHandler) {
       if (showingLevelLose && tutorialLoseScreen != null) {
           tutorialLoseScreen.draw(graphicsHandler);
           return;
       }
       background.draw(graphicsHandler);
       if (player != null) {
        player.draw(graphicsHandler);
       }
       tutorialLabel.draw(graphicsHandler);
       movementLabel.draw(graphicsHandler);
       movementDescription.draw(graphicsHandler);
       if (rightArrowImage != null) {
        int arrowX = 4;
        int arrowY = 95;
        graphicsHandler.drawImage(rightArrowImage, arrowX, arrowY, 50, 50);
       }
       if (leftArrowImage != null) {
        int arrowX = 15;
        int arrowY = 118;
        graphicsHandler.drawImage(leftArrowImage, arrowX, arrowY, 50, 50);
       }
       if (downArrowImage != null) {
        int arrowX = 7;
        int arrowY = 142;
        graphicsHandler.drawImage(downArrowImage, arrowX, arrowY, 50, 50);
       }
       if (upArrowImage != null) {
        int arrowX = 12;
        int arrowY = 181;
        graphicsHandler.drawImage(upArrowImage, arrowX, arrowY, 50, 50);
       } 
       attackLabel.draw(graphicsHandler);
       underlinedLabel1.draw(graphicsHandler);
       underlinedLabel2.draw(graphicsHandler);
       underlinedLabel3.draw(graphicsHandler);
       rightArrowDescription.draw(graphicsHandler);
       leftArrowDescription.draw(graphicsHandler);
       downArrowDescription.draw(graphicsHandler);
       upArrowDescription.draw(graphicsHandler);
       keyDescription.draw(graphicsHandler);
       returnInstructionsLabel.draw(graphicsHandler);
   }
}
