package MapEditor;

import Level.Tileset;
import Tilesets.CommonTileset;
import Tilesets.NewCommonTileset;

import java.util.ArrayList;

public class EditorTilesets {
    public static ArrayList<String> getTilesetNames() {
        return new ArrayList<String>() {{
            add("CommonTileset");
            add("NewCommonTileset");
        }};
    }

    public static Tileset getTilesetByName(String name) {
        switch (name) {
            case "CommonTileset":
                return new CommonTileset();
            case "NewCommonTileset":
                return new NewCommonTileset();
            default:
                throw new RuntimeException("Unrecognized tileset name");
        }
    }
}