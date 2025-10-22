package Screens;

import Engine.GraphicsHandler;
import Engine.Screen;
import Game.GameState;
import Game.ScreenCoordinator;
import Level.Map;
import Level.Player;
import Level.Player2;
import Level.PlayerListener;
import Maps.BobcatMap;
import Maps.CCEMap;
import Maps.QuadMap;
import Maps.TestMap;
import java.awt.image.BufferedImage;
import Engine.ImageLoader;
import Players.*;
import GameObject.GameObject;

import Utils.Point;

// This class is for when the platformer game is actually being played
public class PlayLevelScreen extends Screen implements PlayerListener {
    protected ScreenCoordinator screenCoordinator;
    protected Map map;
    protected Player player;
    protected Player2 player2;

    protected PlayLevelScreenState playLevelScreenState;
    protected int screenTimer;
    protected LevelClearedScreen levelClearedScreen;
    protected LevelLoseScreen levelLoseScreen;
    protected boolean levelCompletedStateChangeStart;

    // Overhead health bar images 
    // Player 1 Health bar
    private BufferedImage p1Default;
    private BufferedImage[] p1JumpImages = new BufferedImage[4];
    

    // // Player 2 Health bar
    private BufferedImage p2Default;
    private BufferedImage[] p2JumpImages = new BufferedImage[4];





    public PlayLevelScreen(ScreenCoordinator screenCoordinator) {
        this.screenCoordinator = screenCoordinator;
    }

    // Safely load an image by filename. It returns null if the image is missin
    private BufferedImage safeLoadImage(String filename) {
        try {
            return ImageLoader.load(filename);
        } catch (RuntimeException e) {
            System.out.println("Optional image not found: " + filename);
            return null;
        }
    }

    public void initialize() {

        // Load the selected map based on MapSelectScreen selection
        int mapIndex = MapSelectScreen.selectedMapIndex;
        switch (mapIndex) {
            case 0:
                this.map = new QuadMap();
                break;
            case 1:
                this.map = new CCEMap();
                break;
            case 2:
                this.map = new BobcatMap();
                break;
            default:
                this.map = new TestMap();
                break;
        }

        int p1Index = CharacterChooseScreen2.player1CharacterIndex;
        int p2Index = CharacterChooseScreen2.player2CharacterIndex;
        
        switch (p1Index) {
            case 0: player = new Cat(map.getPlayerStartPosition().x, map.getPlayerStartPosition().y); break;
       //     case 1: player = new Nicolini(map.getPlayerStartPosition().x, map.getPlayerStartPosition().y); break; // replace later with Nicolini
         //   case 2: player = new Boomer(map.getPlayerStartPosition().x, map.getPlayerStartPosition().y); break;
          //  case 3: player = new Cat(map.getPlayerStartPosition().x, map.getPlayerStartPosition().y); break;
         //   case 4: player = new Cat(map.getPlayerStartPosition().x, map.getPlayerStartPosition().y); break;
//case 5: player = new Cat(map.getPlayerStartPosition().x, map.getPlayerStartPosition().y); break;
         //   default: player = new Alex(map.getPlayerStartPosition().x, map.getPlayerStartPosition().y); break;
        }
        player.setMap(map);
        player.addListener(this);
        
        switch (p2Index) {
            case 0: player2 = new Cat2(map.getPlayerStartPosition().x - 50, map.getPlayerStartPosition().y); break;
         //   case 1: player2 = new Cat2(map.getPlayerStartPosition().x - 50, map.getPlayerStartPosition().y); break; // replace later with Nicolini2 etc.
        //    case 2: player2 = new Cat2(map.getPlayerStartPosition().x - 50, map.getPlayerStartPosition().y); break;
        //    case 3: player2 = new Cat2(map.getPlayerStartPosition().x - 50, map.getPlayerStartPosition().y); break;
       //    case 4: player2 = new Cat2(map.getPlayerStartPosition().x - 50, map.getPlayerStartPosition().y); break;
       //     case 5: player2 = new Cat2(map.getPlayerStartPosition().x - 50, map.getPlayerStartPosition().y); break;
        //    default: player2 = new Cat2(map.getPlayerStartPosition().x - 50, map.getPlayerStartPosition().y); break;
        }
        player2.setMap(map);
        player2.addListener(this);
        // screens
        levelClearedScreen = new LevelClearedScreen();
        levelLoseScreen = new LevelLoseScreen(this);

       
        this.playLevelScreenState = PlayLevelScreenState.RUNNING;

        // load health overlay images for player 1 (stages 1-5)
        p1Default = safeLoadImage("PlayerHealth.png");
        for (int i = 0; i < p1JumpImages.length; i++) {
            p1JumpImages[i] = safeLoadImage("PlayerHealth" + (i+1) + ".png");
        }

        // load health overlay images for player 2 (stages 1..10)
        p2Default = safeLoadImage("PlayerHealthPlayer2.png");
        for (int i = 0; i < p2JumpImages.length; i++) {
            p2JumpImages[i] = safeLoadImage("PlayerHealthPlayer2_" + (i+1) + ".png");
        }
    }

    public void update() {
        // based on screen state, perform specific actions
        switch (playLevelScreenState) {
            // if level is "running" update player and map to keep game logic for the platformer level going
            case RUNNING:
                player.update();
                player2.update();
                map.update(player);

                // --- Detect "jump start" edges and advance image (max 5) ---

                // Health overlay is driven by each player's current health; no jump-based image switching

                break;

            // if level has been completed, bring up level cleared screen
            case LEVEL_COMPLETED:
                if (levelCompletedStateChangeStart) {
                    screenTimer = 130;
                    levelCompletedStateChangeStart = false;
                } else {
                    levelClearedScreen.update();
                    screenTimer--;
                    if (screenTimer == 0) {
                        goBackToMenu();
                    }
                }
                break;

            // wait on level lose screen to make a decision (either resets level or sends player back to main menu)
            case LEVEL_LOSE:
                levelLoseScreen.update();
                break;
        }
    }

    public void draw(GraphicsHandler graphicsHandler) {
        // based on screen state, draw appropriate graphics
        switch (playLevelScreenState) {
            case RUNNING:
                map.draw(graphicsHandler);

                // --- Player 1 ---
                player.draw(graphicsHandler);
                if (player != null) {
                    BufferedImage img1 = p1Default;
                    if (player != null) {
                        // map player's health to one of the 10 overlay images so each hit advances one image
                        int health = player.getHealth();
                        int maxHealth = player.getMaxHealth();
                        int idx1 = -1;
                        if (maxHealth > 0) {
                            //maxHealth - health
                            int damageTaken = Math.max(0, Math.min(p1JumpImages.length, maxHealth - health));
                            if (damageTaken > 0) {
                                idx1 = damageTaken - 1;
                            }
                        }
                        if (idx1 >= 0 && idx1 < p1JumpImages.length && p1JumpImages[idx1] != null) {
                            img1 = p1JumpImages[idx1];
                        }
                    }
                    if (img1 != null) {
                        int screenX = Math.round(player.getX() - map.getCamera().getX());
                        int screenY = Math.round(player.getY() - map.getCamera().getY());
                        int imgX = screenX + (player.getWidth() - img1.getWidth()) / 2;
                        int imgY = screenY - img1.getHeight() - 4;
                        graphicsHandler.drawImage(img1, imgX, imgY);
                    }
                }

                // --- Player 2 ---
                player2.draw(graphicsHandler);
                if (player2 != null) {
                    BufferedImage img2 = p2Default;
                    if (player2 != null) {
                        int health2 = player2.getHealth();
                        int maxHealth2 = player2.getMaxHealth();
                        int idx2 = -1;
                        if (maxHealth2 > 0) {
                            int damageTaken2 = Math.max(0, Math.min(p2JumpImages.length, maxHealth2 - health2));
                            if (damageTaken2 > 0) {
                                idx2 = damageTaken2 - 1;
                            }
                        }
                        if (idx2 >= 0 && idx2 < p2JumpImages.length && p2JumpImages[idx2] != null) {
                            img2 = p2JumpImages[idx2];
                        }
                    }
                    if (img2 != null) {
                        int screenX2 = Math.round(player2.getX() - map.getCamera().getX());
                        int screenY2 = Math.round(player2.getY() - map.getCamera().getY());
                        int imgX2 = screenX2 + (player2.getWidth() - img2.getWidth()) / 2;
                        int imgY2 = screenY2 - img2.getHeight() - 4;
                        graphicsHandler.drawImage(img2, imgX2, imgY2);
                    }
                }
                break;

            case LEVEL_COMPLETED:
                levelClearedScreen.draw(graphicsHandler);
                break;

            case LEVEL_LOSE:
                levelLoseScreen.draw(graphicsHandler);
                break;
        }
    }

    public PlayLevelScreenState getPlayLevelScreenState() {
        return playLevelScreenState;
    }

    @Override
    public void onLevelCompleted() {
        if (playLevelScreenState != PlayLevelScreenState.LEVEL_COMPLETED) {
            playLevelScreenState = PlayLevelScreenState.LEVEL_COMPLETED;
            levelCompletedStateChangeStart = true;
        }
    }

    @Override
    public void onDeath() {
        if (playLevelScreenState != PlayLevelScreenState.LEVEL_LOSE) {
            playLevelScreenState = PlayLevelScreenState.LEVEL_LOSE;
        }
    }

    @Override
    public void onHurt(GameObject source, int amount) {
        // when any player is hurt, we simply let the draw() method read player.getHealth() each frame
        // If we wanted to mirror actual HP across players we would call player.damage(amount,false)/player2.damage(...)
        // For now, no extra action is necessary here.
    }

    public void resetLevel() {
        initialize();
    }

    public void goBackToMenu() {
        screenCoordinator.setGameState(GameState.MENU);
    }

    // This enum represents the different states this screen can be in
    private enum PlayLevelScreenState {
        RUNNING, LEVEL_COMPLETED, LEVEL_LOSE
    }
}
