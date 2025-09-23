package Screens;


import Engine.*;
import Game.GameState;
import Game.ScreenCoordinator;
import Level.Map;
import Maps.TitleScreenMap;
import SpriteFont.SpriteFont;


import java.awt.*;


public class TutorialScreen extends Screen {
   protected ScreenCoordinator screenCoordinator;
   protected Map background;
   protected KeyLocker keyLocker = new KeyLocker();
   protected SpriteFont tutorialLabel;
   protected SpriteFont movementLabel;
   protected SpriteFont movementDescription;
   protected SpriteFont attackLabel;
   protected SpriteFont underlinedLabel;
   protected SpriteFont returnInstructionsLabel;


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
       returnInstructionsLabel = new SpriteFont("Press Space to return to the menu", 20, 540, "Times New Roman", 18, Color.white);
       keyLocker.lockKey(Key.SPACE);
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
       attackLabel.draw(graphicsHandler);
       underlinedLabel.draw(graphicsHandler);
       returnInstructionsLabel.draw(graphicsHandler);
   }
}
