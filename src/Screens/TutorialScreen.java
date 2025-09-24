package Screens;


import Engine.*;
import Game.GameState;
import Game.ScreenCoordinator;
import Level.Map;
import Maps.TitleScreenMap;
import SpriteFont.SpriteFont;


import java.awt.*;
import java.awt.image.BufferedImage;


public class TutorialScreen extends Screen {
   protected ScreenCoordinator screenCoordinator;
   protected Map background;
   protected KeyLocker keyLocker = new KeyLocker();
   protected SpriteFont tutorialLabel;
   protected SpriteFont movementLabel;
   protected SpriteFont movementDescription;
   protected SpriteFont attackLabel;
   protected SpriteFont underlinedLabel;
   protected SpriteFont rightArrowDescription;
   protected SpriteFont leftArrowDescription;
   protected SpriteFont downArrowDescription;
   protected SpriteFont upArrowDescription;
   protected SpriteFont returnInstructionsLabel;
   protected BufferedImage rightArrowImage;
   protected BufferedImage leftArrowImage;
   protected BufferedImage downArrowImage;
   protected BufferedImage upArrowImage;

   public TutorialScreen(ScreenCoordinator screenCoordinator) {
       this.screenCoordinator = screenCoordinator;
   }


   @Override
   public void initialize() {
       // setup graphics on screen (background map, spritefont text)
       background = new TitleScreenMap();
       background.setAdjustCamera(false);
       tutorialLabel = new SpriteFont("Tutorial", 350, 5, "Times New Roman", 30, Color.white);
       movementLabel = new SpriteFont("Movements", 175, 100, "Times New Roman", 26, Color.white);
       movementDescription = new SpriteFont("Use Arrow Keys: ", 20, 135, "Times New Roman", 20, Color.white);
       attackLabel = new SpriteFont("Attacks", 550, 100, "Times New Roman",26, Color.white);
       underlinedLabel = new SpriteFont("_______", 350, 5, "Times New Roman", 30, Color.white);
       rightArrowDescription = new SpriteFont("- Moves you right", 50, 170, "Times New Roman", 18, Color.white);
       leftArrowDescription = new SpriteFont("- Moves you left", 50, 202, "Times New Roman", 18, Color.white);
       downArrowDescription = new SpriteFont("- Moves you down", 50, 233, "Times New Roman", 18, Color.white);
       upArrowDescription = new SpriteFont("- Moves you up", 50, 262, "Times New Roman", 18, Color.white);
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
       background.update(null);


       if (Keyboard.isKeyUp(Key.SPACE)) {
           keyLocker.unlockKey(Key.SPACE);
       }


       // if space is pressed, go back to main menu
       if (!keyLocker.isKeyLocked(Key.SPACE) && Keyboard.isKeyDown(Key.SPACE)) {
           screenCoordinator.setGameState(GameState.MENU);
       }
   }


   public void draw(GraphicsHandler graphicsHandler) {
       background.draw(graphicsHandler);
       tutorialLabel.draw(graphicsHandler);
       movementLabel.draw(graphicsHandler);
       movementDescription.draw(graphicsHandler);
       if (rightArrowImage != null) {
        int arrowX = 4;
        int arrowY = 160;
        graphicsHandler.drawImage(rightArrowImage, arrowX, arrowY, 50, 50);
       }
       if (leftArrowImage != null) {
        int arrowX = 15;
        int arrowY = 187;
        graphicsHandler.drawImage(leftArrowImage, arrowX, arrowY, 50, 50);
       }
       if (downArrowImage != null) {
        int arrowX = 7;
        int arrowY = 214;
        graphicsHandler.drawImage(downArrowImage, arrowX, arrowY, 50, 50);
       }
       if (upArrowImage != null) {
        int arrowX = 12;
        int arrowY = 255;
        graphicsHandler.drawImage(upArrowImage, arrowX, arrowY, 50, 50);
       } 
       attackLabel.draw(graphicsHandler);
       underlinedLabel.draw(graphicsHandler);
       rightArrowDescription.draw(graphicsHandler);
       leftArrowDescription.draw(graphicsHandler);
       downArrowDescription.draw(graphicsHandler);
       upArrowDescription.draw(graphicsHandler);
       returnInstructionsLabel.draw(graphicsHandler);
   }
}
