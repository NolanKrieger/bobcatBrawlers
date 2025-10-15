// package Screens;

// import Engine.*;
// import Game.GameState;
// import Game.ScreenCoordinator;
// import Maps.TitleScreenMap;
// import SpriteFont.SpriteFont;
// import Level.Map;


// import java.awt.*;
// import java.awt.image.BufferedImage;


// public class MapSelectScreen extends Screen {
//    protected ScreenCoordinator screenCoordinator;
//    protected Map background;
//    protected KeyLocker keyLocker = new KeyLocker();
//    protected SpriteFont titleLabel;
//    protected SpriteFont returnInstructionsLabel;
//    BufferedImage[] mapImages;


//    String[] maps = {"QuadMap", "CCE030Map", "BobcatMap"};


//    String[] mapFiles = {"QuadMap.png", "CCE030Map.png", "BobcatMap.png"};


//    Color[] colors={
//      Color.BLACK,
//      Color.RED,
//      Color.YELLOW,
//      new Color(135, 206, 235),
//      new Color(255, 0, 255),
//      new Color(28,28,132)
  
//    };



//    public MapSelectScreen(ScreenCoordinator screenCoordinator) {
//        this.screenCoordinator = screenCoordinator;
//    }


//    @Override
//    public void initialize() {
//        background = new TitleScreenMap();
//        background.setAdjustCamera(false);


//        titleLabel = new SpriteFont("Choose Your Character", 235, 30, "Arial", 32, Color.WHITE);
//        titleLabel.setOutlineColor(Color.BLACK);
//        titleLabel.setOutlineThickness(3);


//        returnInstructionsLabel = new SpriteFont("Press Space to return to the menu",20, ScreenManager.getScreenHeight() - 40, "Arial", 18, Color.WHITE);
//        returnInstructionsLabel.setOutlineColor(Color.BLACK);
//        returnInstructionsLabel.setOutlineThickness(2);


//        keyLocker.lockKey(Key.SPACE);


//        mapImages = new BufferedImage[mapFiles.length];
//        for (int i = 0; i < mapFiles.length; i++) {
//            mapImages[i] = ImageLoader.load("Images/Maps/" + mapFiles[i]);
//        }
//    }


//    @Override
//    public void update() {
//        background.update(null);


//        if (Keyboard.isKeyUp(Key.SPACE)) {
//            keyLocker.unlockKey(Key.SPACE);
//        }


//        if (!keyLocker.isKeyLocked(Key.SPACE) && Keyboard.isKeyDown(Key.SPACE)) {
//            screenCoordinator.setGameState(GameState.MENU);
//        }
//    }


//    @Override
//    public void draw(GraphicsHandler graphicsHandler) {
//        background.draw(graphicsHandler);


//        int screenWidth = ScreenManager.getScreenWidth();
//        int screenHeight = ScreenManager.getScreenHeight();


//        int slotWidth = screenWidth / mapImages.length;
//        int slotHeight = screenHeight - 100;


//        for (int i = 0; i < maps.length; i++) {
//            int x = i * slotWidth;
//            int y = 60;


//            graphicsHandler.drawFilledRectangle(x, y, slotWidth, slotHeight, colors[i]);


//            int imageBoxWidth = (int)(slotWidth * 0.9);
//            int imageBoxHeight = (int)(slotHeight * 0.85);
//            int imageBoxX = x + (slotWidth - imageBoxWidth) / 2;
//            int imageBoxY = y + 30;           


//            graphicsHandler.drawImage(mapImages[i], imageBoxX, imageBoxY, imageBoxWidth, imageBoxHeight);
          



//            SpriteFont nameLabel = new SpriteFont(maps[i], x + slotWidth / 2 - (maps[i].length() * 4), screenHeight - 60, "Arial", 18, Color.WHITE);
//            nameLabel.setOutlineColor(Color.BLACK);
//            nameLabel.setOutlineThickness(2);
//            nameLabel.draw(graphicsHandler);
//        }


//        titleLabel.draw(graphicsHandler);
//        returnInstructionsLabel.draw(graphicsHandler);
//    }
// }
