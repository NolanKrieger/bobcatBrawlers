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
import SpriteFont.SpriteFont;

import java.awt.*;


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
   protected SpriteFont WkeyDescription;
   protected SpriteFont AkeyDescription;
   protected SpriteFont SkeyDescription;
   protected SpriteFont DkeyDescription;
   protected SpriteFont ShootAttackDescription;
   protected SpriteFont ChangeAttackDescription;
   protected SpriteFont PunchAttackDescription;
   protected SpriteFont KickAttackDescription;
   protected SpriteFont returnInstructionsLabel;
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
       tutorialLabel = new SpriteFont("Tutorial", 600, 5, "Times New Roman", 30, Color.white);
       movementLabel = new SpriteFont("Movements", 250, 40, "Times New Roman", 26, Color.white);
       movementDescription = new SpriteFont("Use WASD Keys: ", 250, 78, "Times New Roman", 20, Color.white);
       attackLabel = new SpriteFont("Attacks", 950, 40, "Times New Roman",26, Color.white);
       underlinedLabel1 = new SpriteFont("_______", 600, 5, "Times New Roman", 30, Color.white);
       underlinedLabel2 = new SpriteFont("________", 250, 38, "Times New Roman", 30, Color.white);
       underlinedLabel3 = new SpriteFont("______", 950, 38, "Times New Roman", 30, Color.white);
       WkeyDescription = new SpriteFont("W-To Jump", 250, 107, "Times New Roman", 18, Color.white);
       AkeyDescription = new SpriteFont("A-To Move Left", 250, 130, "Times New Roman", 18, Color.white);
       SkeyDescription = new SpriteFont("S-To Crouch", 250, 154, "Times New Roman", 18, Color.white);
       DkeyDescription = new SpriteFont("D-To Move Right", 250, 178, "Times New Roman", 18, Color.white);
       ShootAttackDescription = new SpriteFont("Key E-To Shoot", 950, 78, "Times New Roman", 18, Color.white);
       ChangeAttackDescription = new SpriteFont("Key X-To Change Attacks", 950, 99, "Times New Roman", 18, Color.white);
       PunchAttackDescription = new SpriteFont("Key Q-To Punch", 950, 119, "Times New Roman", 18, Color.white);
       KickAttackDescription = new SpriteFont("Key R-To Kick", 950, 140, "Times New Roman", 18, Color.white);
       returnInstructionsLabel = new SpriteFont("Press Space to return to the menu", 20, 740, "Times New Roman", 18, Color.white);
       keyLocker.lockKey(Key.SPACE);
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
       attackLabel.draw(graphicsHandler);
       underlinedLabel1.draw(graphicsHandler);
       underlinedLabel2.draw(graphicsHandler);
       underlinedLabel3.draw(graphicsHandler); 
       WkeyDescription.draw(graphicsHandler);
       AkeyDescription.draw(graphicsHandler);
       SkeyDescription.draw(graphicsHandler);
       DkeyDescription.draw(graphicsHandler);
       ShootAttackDescription.draw(graphicsHandler);
       ChangeAttackDescription.draw(graphicsHandler);
       PunchAttackDescription.draw(graphicsHandler);
       KickAttackDescription.draw(graphicsHandler);
       returnInstructionsLabel.draw(graphicsHandler);
   }
}
