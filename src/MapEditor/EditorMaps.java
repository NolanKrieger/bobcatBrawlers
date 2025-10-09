package MapEditor;

import Level.Map;
import Maps.TestMap;
import Maps.TitleScreenMap;

import java.util.ArrayList;

public class EditorMaps {
    public static ArrayList<String> getMapNames() {
        return new ArrayList<String>() {{
            add("TestMap");
            add("TitleScreen");

            add("BobcatMap");
            add("CCEMap"); 
            add("QuadMap");

        }};
    }

    public static Map getMapByName(String mapName) {
        switch(mapName) {
            case "TestMap":
                return new TestMap();
            case "TitleScreen":
                return new TitleScreenMap();

            case "BobcatMap":
                return new Maps.BobcatMap();
            case "CCEMap":
                return new Maps.CCEMap();
            case "QuadMap":
                return new Maps.QuadMap();

            default:
                throw new RuntimeException("Unrecognized map name");
        }
    }
}
