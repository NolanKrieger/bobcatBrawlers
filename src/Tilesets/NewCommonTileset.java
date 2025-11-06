package Tilesets;

import Builders.FrameBuilder;
import Builders.MapTileBuilder;
import Engine.ImageLoader;
import GameObject.Frame;
import Level.TileType;
import Level.Tileset;
import java.util.ArrayList;

public class NewCommonTileset extends Tileset {
    public NewCommonTileset() {
        super(ImageLoader.load("newCommonTileset-2.png"), 16, 16, 3); // Adjust scale if needed
    }

    @Override
    public ArrayList<MapTileBuilder> defineTiles() {
        ArrayList<MapTileBuilder> mapTiles = new ArrayList<>();

        // Example: Add tiles based on newCommonTileset.png layout
        // Row 0, Col 0: New roof tile
        //first frame stair to right
        Frame rightearFrame = new FrameBuilder(getSubImage(4, 4))
                .withScale(tileScale)
                .build();

        MapTileBuilder rightearTile = new MapTileBuilder(rightearFrame)
                .withTileType(TileType.NOT_PASSABLE);

        mapTiles.add(rightearTile);

        Frame leftearFrame = new FrameBuilder(getSubImage(0, 1))
                .withScale(tileScale)
                .build();

        MapTileBuilder leftearTile = new MapTileBuilder(leftearFrame)
                .withTileType(TileType.PASSABLE);

        mapTiles.add(leftearTile);

        Frame topleftFrame = new FrameBuilder(getSubImage(0, 2))
             .withScale(tileScale)
             .build();
        
        MapTileBuilder topleftTile = new MapTileBuilder(topleftFrame)
            .withTileType(TileType.PASSABLE);

        mapTiles.add(topleftTile);

        Frame toprightFrame = new FrameBuilder(getSubImage(2, 1))
        .withScale(tileScale)
                .build();
        MapTileBuilder toprightTile = new MapTileBuilder(toprightFrame)
        .withTileType(TileType.NOT_PASSABLE);
        mapTiles.add(toprightTile);

        Frame faceFrame = new FrameBuilder(getSubImage(3, 1))
        .withScale(tileScale)
        .build();
        MapTileBuilder faceTile = new MapTileBuilder(faceFrame)
        .withTileType(TileType.NOT_PASSABLE);
        mapTiles.add(faceTile);

        Frame backFrame = new FrameBuilder(getSubImage(3, 2))
        .withScale(tileScale)
        .build();
        MapTileBuilder backTile = new MapTileBuilder(backFrame)
        .withTileType(TileType.NOT_PASSABLE);
        mapTiles.add(backTile);

        Frame blindFrame = new FrameBuilder(getSubImage(3, 3))
        .withScale(tileScale)
        .build();
        MapTileBuilder blindTile = new MapTileBuilder(blindFrame)
        .withTileType(TileType.NOT_PASSABLE);
        mapTiles.add(blindTile);

        Frame eggFrame = new FrameBuilder(getSubImage(3, 4))
        .withScale(tileScale)
        .build();
        MapTileBuilder eggTile = new MapTileBuilder(eggFrame)
        .withTileType(TileType.NOT_PASSABLE);
        mapTiles.add(eggTile);

        Frame lefteyeFrame = new FrameBuilder(getSubImage(3, 5))
                .withScale(tileScale)
        .build();
        MapTileBuilder lefteyeTile = new MapTileBuilder(lefteyeFrame)
        .withTileType(TileType.NOT_PASSABLE);
        mapTiles.add(lefteyeTile);

        Frame righteyeFrame = new FrameBuilder(getSubImage(4, 2))
        .withScale(tileScale)
        .build();
        MapTileBuilder righteyeTile = new MapTileBuilder(righteyeFrame)
        .withTileType(TileType.PASSABLE);
            mapTiles.add(righteyeTile);



            Frame idkFrame = new FrameBuilder(getSubImage(4, 3))
            .withScale(tileScale)
            .build();
            MapTileBuilder idkTile = new MapTileBuilder(idkFrame)
            .withTileType(TileType.PASSABLE);
            mapTiles.add(idkTile);
    
            Frame idnFrame = new FrameBuilder(getSubImage(4, 1))
                    .withScale(tileScale)
            .build();
            MapTileBuilder idnTile = new MapTileBuilder(idnFrame)
            .withTileType(TileType.NOT_PASSABLE);
            mapTiles.add(idnTile);
    
            Frame idFrame = new FrameBuilder(getSubImage(4, 0))
            .withScale(tileScale)
            .build();
            MapTileBuilder idTile = new MapTileBuilder(idFrame)
            .withTileType(TileType.NOT_PASSABLE);
                mapTiles.add(idTile);




        // Add more tiles here following the same pattern
        // Use getSubImage(row, col) for each tile in newCommonTileset.png
        // Assign appropriate TileType (PASSABLE, NOT_PASSABLE, WATER, etc.)

        return mapTiles;
    }
}