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
   protected SpriteFont creditsLabel;
   protected SpriteFont createdByLabel;
   protected SpriteFont returnInstructionsLabel;


   public TutorialScreen(ScreenCoordinator screenCoordinator) {
       this.screenCoordinator = screenCoordinator;
   }


   @Override
   public void initialize() {
       // setup graphics on screen (background map, spritefont text)
       background = new TitleScreenMap();
       background.setAdjustCamera(false);
       creditsLabel = new SpriteFont("Tutorial", 15, 1, "Times New Roman", 30, Color.white);
       createdByLabel = new SpriteFont("Created by Lucas Deutsch", 130, 121, "Times New Roman", 20, Color.white);
       returnInstructionsLabel = new SpriteFont("Press Space to return to the menu", 20, 532, "Times New Roman", 30, Color.white);
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
       creditsLabel.draw(graphicsHandler);
       createdByLabel.draw(graphicsHandler);
       returnInstructionsLabel.draw(graphicsHandler);
   }
}
