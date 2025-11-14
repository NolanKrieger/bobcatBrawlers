package Screens;

import Engine.*;
import Game.GameState;
import Game.ScreenCoordinator;
import Maps.TitleScreenMap;
import SpriteFont.SpriteFont;
import Level.Map;


import java.awt.*;
import java.awt.image.BufferedImage;


public class MapSelectScreen extends Screen {
   protected ScreenCoordinator screenCoordinator;
   protected Map background;
   protected KeyLocker keyLocker = new KeyLocker();
   protected SpriteFont titleLabel;
   protected SpriteFont returnInstructionsLabel;
   protected BufferedImage[] mapPreviewImages;

   private int currentCharacterHovered = 0;
   private int characterSelected = -1;
   public static int selectedMapIndex = -1;

   String[] characters = {"Quad", "CCE030", "The Bobcat", "Test Map"};


   Color[] colors={
     Color.BLACK,
     Color.RED,
     Color.YELLOW,
     new Color(135, 206, 235),
     new Color(255, 0, 255),
     new Color(28,28,132)
  
     
   };

   public MapSelectScreen(ScreenCoordinator screenCoordinator) {
       this.screenCoordinator = screenCoordinator;
   }


   @Override
   public void initialize() {
       // Reset lives whenever we enter map select (starting a fresh game)
       try {
           Screens.LevelLoseScreen.resetLives();
       } catch (Exception e) {
           System.out.println("Warning: Could not reset lives: " + e.getMessage());
       }
       
       background = new TitleScreenMap();
       background.setAdjustCamera(false);


       titleLabel = new SpriteFont("Choose Your Map", 235, 25, "Arial", 32, Color.WHITE);
       titleLabel.setOutlineColor(Color.BLACK);
       titleLabel.setOutlineThickness(3);


       returnInstructionsLabel = new SpriteFont("Press Space to choose map",20, ScreenManager.getScreenHeight() - 40, "Arial", 18, Color.WHITE);
       returnInstructionsLabel.setOutlineColor(Color.BLACK);
       returnInstructionsLabel.setOutlineThickness(2);

       keyLocker.lockKey(Key.SPACE);
       keyLocker.lockKey(Key.LEFT);
       keyLocker.lockKey(Key.RIGHT);

       // Load map preview images
       mapPreviewImages = new BufferedImage[characters.length];
       try {
           mapPreviewImages[0] = ImageLoader.load("mapImages/pixQuad.png");
       } catch (Exception e) {
           System.out.println("Warning: Could not load pixQuad.png: " + e.getMessage());
       }
       try {
           mapPreviewImages[1] = ImageLoader.load("mapImages/pixCCE.png");
       } catch (Exception e) {
           System.out.println("Warning: Could not load pixCCE.png: " + e.getMessage());
       }
       try {
           mapPreviewImages[2] = ImageLoader.load("mapImages/pixCat.png");
       } catch (Exception e) {
           System.out.println("Warning: Could not load pixCat.png: " + e.getMessage());
       }
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
    selectedMapIndex = characterSelected;
    screenCoordinator.setGameState(GameState.LEVEL);
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

        if (i == currentCharacterHovered) {

             graphicsHandler.drawFilledRectangleWithBorder(x, y, slotWidth, slotHeight, c, Color.WHITE, 5);
        } else {
            graphicsHandler.drawFilledRectangle(x, y, slotWidth, slotHeight, c);
        }

        // Draw map preview image if available
        if (mapPreviewImages != null && mapPreviewImages[i] != null) {
            int imageWidth = mapPreviewImages[i].getWidth();
            int imageHeight = mapPreviewImages[i].getHeight();
            
            // Scale to fit the slot while maintaining aspect ratio
            float scale = Math.min((float)slotWidth / imageWidth, (float)slotHeight / imageHeight) * 0.9f;
            int scaledWidth = (int)(imageWidth * scale);
            int scaledHeight = (int)(imageHeight * scale);
            
            int imageX = x + (slotWidth / 2) - (scaledWidth / 2);
            int imageY = y + (slotHeight / 2) - (scaledHeight / 2);
            
            graphicsHandler.drawImage(mapPreviewImages[i], imageX, imageY, scaledWidth, scaledHeight);
        }

           SpriteFont nameLabel = new SpriteFont(characters[i], x + slotWidth / 2 - (characters[i].length() * 4), screenHeight - 60, "Arial", 18, Color.WHITE);
           nameLabel.setOutlineColor(Color.BLACK);
           nameLabel.setOutlineThickness(2);
           nameLabel.draw(graphicsHandler);
       }


       titleLabel.draw(graphicsHandler);
       returnInstructionsLabel.draw(graphicsHandler);
   }
}

