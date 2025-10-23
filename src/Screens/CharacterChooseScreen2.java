package Screens;

import Engine.*;
import Game.GameState;
import Game.ScreenCoordinator;
import Maps.TitleScreenMap;
import SpriteFont.SpriteFont;
import Level.Map;
import java.awt.image.BufferedImage;

import GameObject.SpriteSheet;

import java.util.HashMap;

import java.awt.*;


public class CharacterChooseScreen2 extends Screen {
   protected ScreenCoordinator screenCoordinator;
   protected Map background;
   protected KeyLocker keyLocker = new KeyLocker();
   protected SpriteFont titleLabel;
   protected SpriteFont returnInstructionsLabel;
   BufferedImage[] characterImages;


   private int currentCharacterHovered = 0;
   private int characterSelected = -1;
   private int currentPlayer = 1;
   public static int player1CharacterIndex = -1;
   public static int player2CharacterIndex = -1;

   String[] characters = {"Alex", "Prof. Nicolini", "Boomer", "Chester", "Marie", "Judy"};




   Color[] colors={
     Color.BLACK,
     Color.RED,
     Color.YELLOW,
     new Color(135, 206, 235),
     new Color(255, 0, 255),
     new Color(28,28,132)
  
     
   };

   public CharacterChooseScreen2(ScreenCoordinator screenCoordinator) {
       this.screenCoordinator = screenCoordinator;
   }


   @Override
   public void initialize() {
       background = new TitleScreenMap();
       background.setAdjustCamera(false);


       titleLabel = new SpriteFont("Choose Your Character", 475, 25, "Arial", 32, Color.WHITE);
       titleLabel.setOutlineColor(Color.BLACK);
       titleLabel.setOutlineThickness(3);


       returnInstructionsLabel = new SpriteFont("Press Space to choose character",20, ScreenManager.getScreenHeight() - 40, "Arial", 18, Color.WHITE);
       returnInstructionsLabel.setOutlineColor(Color.BLACK);
       returnInstructionsLabel.setOutlineThickness(2);

       keyLocker.lockKey(Key.SPACE);
       keyLocker.lockKey(Key.LEFT);
       keyLocker.lockKey(Key.RIGHT);

       characterImages = new BufferedImage[characters.length];

    // For now, just add one sprite (Alex)
    SpriteSheet alexSheet = new SpriteSheet(ImageLoader.load("alexsprite5.png"), 24, 24);
    characterImages[0] = alexSheet.getSprite(0, 0);

    //Add more characters later:
    //SpriteSheet Boomer/etcSheet = new SpriteSheet(ImageLoader.load("alexsprite5.png"), 24, 24);
    //characterImages[1] = "boomer/etc"Sheet.getSprite(0, 0);

   }

       
       
   


   @Override
   public void update() {
       background.update(null);


       if (Keyboard.isKeyUp(Key.SPACE)) {
           keyLocker.unlockKey(Key.SPACE);
        }
       if (Keyboard.isKeyUp(Key.LEFT)) {
        keyLocker.unlockKey(Key.LEFT);
        }       
        if (Keyboard.isKeyUp(Key.RIGHT)) {
        keyLocker.unlockKey(Key.RIGHT);
        }
        

 if (!keyLocker.isKeyLocked(Key.LEFT) && Keyboard.isKeyDown(Key.LEFT)) {
    currentCharacterHovered--;
    if (currentCharacterHovered < 0) currentCharacterHovered = characters.length - 1;
    keyLocker.lockKey(Key.LEFT);
 }

if (!keyLocker.isKeyLocked(Key.RIGHT) && Keyboard.isKeyDown(Key.RIGHT)) {
    currentCharacterHovered++;
    if (currentCharacterHovered >= characters.length) currentCharacterHovered = 0;
    keyLocker.lockKey(Key.RIGHT);
}

if (!keyLocker.isKeyLocked(Key.SPACE) && Keyboard.isKeyDown(Key.SPACE)) {
    characterSelected = currentCharacterHovered;

    if (currentPlayer == 1) {
        player1CharacterIndex = characterSelected;
        currentPlayer = 2;
        titleLabel.setText("Player 2 Choose Your Character");
    } else if (currentPlayer == 2) {
        player2CharacterIndex = characterSelected;
        screenCoordinator.setGameState(GameState.LEVEL);
    }

    keyLocker.lockKey(Key.SPACE);
}
        
}




   @Override
   public void draw(GraphicsHandler graphicsHandler) {
       background.draw(graphicsHandler);


       int screenWidth = ScreenManager.getScreenWidth();
       int screenHeight = ScreenManager.getScreenHeight();


       int slotWidth = screenWidth / characters.length;
       int slotHeight = screenHeight - 100;
       int y = 60;

       for (int i = 0; i < characters.length; i++) {
        int x = i * slotWidth;
        Color c = colors[i];
    
<<<<<<< HEAD
        // Draw background slot
=======
>>>>>>> 24203b8c001ec348ea19aea823f68fcf8fa63878
        if (i == currentCharacterHovered) {
            graphicsHandler.drawFilledRectangleWithBorder(x, y, slotWidth, slotHeight, c, Color.WHITE, 5);
        } else {
            graphicsHandler.drawFilledRectangle(x, y, slotWidth, slotHeight, c);
        }
    
<<<<<<< HEAD
        // === NEW: Draw character sprite if available ===
=======
>>>>>>> 24203b8c001ec348ea19aea823f68fcf8fa63878
        if (characterImages != null && characterImages[i] != null) {
            int spriteWidth = characterImages[i].getWidth() * 5;   // scale ×5 like in Cat.java
            int spriteHeight = characterImages[i].getHeight() * 5;
    
            int spriteX = x + (slotWidth / 2) - (spriteWidth / 2);
            int spriteY = y + (slotHeight / 2) - (spriteHeight / 2) - 20;
    
            graphicsHandler.drawImage(characterImages[i], spriteX, spriteY, spriteWidth, spriteHeight);
        }
    
<<<<<<< HEAD
        // Draw name label
=======
>>>>>>> 24203b8c001ec348ea19aea823f68fcf8fa63878
        SpriteFont nameLabel = new SpriteFont(characters[i], x + slotWidth / 2 - (characters[i].length() * 4), screenHeight - 60, "Arial", 18, Color.WHITE);
        nameLabel.setOutlineColor(Color.BLACK);
        nameLabel.setOutlineThickness(2);
        nameLabel.draw(graphicsHandler);
    }


       titleLabel.draw(graphicsHandler);
       returnInstructionsLabel.draw(graphicsHandler);
   }
}

