package Maps;

// import Enemies.BugEnemy;
// import Enemies.DinosaurEnemy;
import Engine.GraphicsHandler;
import Engine.ImageLoader;
import Engine.ScreenManager;
import EnhancedMapTiles.EndLevelBox;
import EnhancedMapTiles.HorizontalMovingPlatform;
import GameObject.Rectangle;
import Level.*;
// import NPCs.Walrus;
import Tilesets.CommonTileset;
import Utils.Direction;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

// Represents a test map to be used in a level
public class QuadMap extends Map {

    private BufferedImage backgroundImage;

    public QuadMap() {
        super("quad_map.txt", new CommonTileset());
        this.playerStartPosition = getMapTile(2, 11).getLocation();
        
      
    }

    @Override
    public void draw(GraphicsHandler graphicsHandler) {
        // Draw the background image first
        if (backgroundImage != null) {
            graphicsHandler.drawImage(backgroundImage, 0, 0, ScreenManager.getScreenWidth(), ScreenManager.getScreenHeight());
        }
        
        // Then draw the normal map elements on top
        super.draw(graphicsHandler);
    }

}
