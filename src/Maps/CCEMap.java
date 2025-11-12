package Maps;

import Engine.GraphicsHandler;
import Engine.ImageLoader;
import GameObject.Sprite;
import Level.Map;
import Tilesets.CommonTileset;
import Utils.Colors;
import Utils.Point;

// Represents the map that is used as a background for the main menu and credits menu screen
public class CCEMap extends Map {

    private Sprite cat;

    public CCEMap() {
        super("CCEMap.txt", new CommonTileset());
        this.playerStartPosition = getMapTile(6, 10).getLocation();
        try {
            // Point catLocation = getMapTile(15, 2).getLocation().subtractX(24).subtractY(6);
            // cat = new Sprite(ImageLoader.loadSubImage("Cat.png", Colors.MAGENTA, 0, 0, 24, 24));
            // cat.setScale(3);
            // cat.setLocation(catLocation.x, catLocation.y);
        } catch (Exception e) {
            System.out.println("Warning: Could not load cat sprite for CCEMap: " + e.getMessage());
            cat = null;
        }
    }

    @Override
    public void draw(GraphicsHandler graphicsHandler) {
        super.draw(graphicsHandler);
        if (cat != null) {
            cat.draw(graphicsHandler);
        }
    }

}
