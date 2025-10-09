package Maps;

// import Enemies.BugEnemy;
// import Enemies.DinosaurEnemy;
import Engine.ImageLoader;
import EnhancedMapTiles.EndLevelBox;
import EnhancedMapTiles.HorizontalMovingPlatform;
import GameObject.Rectangle;
import Level.*;
// import NPCs.Walrus;
import Tilesets.CommonTileset;
import Utils.Direction;

import java.util.ArrayList;

// Represents a test map to be used in a level
public class QuadMap extends Map {

    public QuadMap() {
        super("quad_map.txt", new CommonTileset());
        this.playerStartPosition = getMapTile(2, 11).getLocation();
    }

}
