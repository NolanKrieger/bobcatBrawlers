package Screens;

import Engine.GraphicsHandler;
import Engine.Screen;
import Game.GameState;
import Game.ScreenCoordinator;
import Level.Map;
import Engine.AudioPlayer;
import Level.Player;
import Level.Player2;
import Level.PlayerListener;
import Maps.BobcatMap;
import Maps.CCEMap;
import Maps.QuadMap;
import Maps.TestMap;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;
import Engine.ImageLoader;
import Players.*;
import GameObject.GameObject;

// import Utils.Point; (unused)

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
    private int hurtFlashTimerP1 = 0;
    private int hurtFlashTimerP2 = 0;
    private final int HURT_FLASH_MS = 400;
    // automatic scheduler disabled when using press-to-disable; set very large to avoid triggering
    private final int COOLDOWN_CYCLE_MS = Integer.MAX_VALUE;
    private final int DISABLE_DURATION_MS = 5000;
    private int cooldownTimer = 0;
    private int disableTimer = 0;
    private int disabledPlayer = 0;
    private int lastDisabledPlayer = 0;
    private boolean alternate = true;
    // track previous attacksEnabled state to detect transitions
    private boolean prevP1AttacksEnabled = true;
    private boolean prevP2AttacksEnabled = true;
    // Audio player for background music in this level
    private AudioPlayer bgMusicPlayer;
    // Audio player for lose sound effect
    private AudioPlayer loseSoundPlayer;

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
            case 3:
                this.map = new TestMap();
                break;
            default:
                this.map = new TestMap();
                break;
        }

        int p1Index = CharacterChooseScreen2.player1CharacterIndex;
        int p2Index = CharacterChooseScreen2.player2CharacterIndex;
        
    // Moving the player 2 start position to the right side of the level
    float p1StartX = map.getPlayerStartPosition().x;
    float p1StartY = map.getPlayerStartPosition().y;
    float p2X = Math.max(0, map.getEndBoundX() - 500); 
       switch (p1Index) {
          case 0: player = new AlexFighter(p1StartX, p1StartY); break;
      //     case 1: player = new Nicolini(p1StartX, p1StartY); break; // replace later with Nicolini
          case 2: player = new Boomer(p1StartX, p1StartY); break;
          //  case 3: player = new Cat(map.getPlayerStartPosition().x, map.getPlayerStartPosition().y); break;
         //   case 4: player = new Cat(map.getPlayerStartPosition().x, map.getPlayerStartPosition().y); break;
//case 5: player = new Cat(map.getPlayerStartPosition().x, map.getPlayerStartPosition().y); break;
         //   default: player = new Alex(map.getPlayerStartPosition().x, map.getPlayerStartPosition().y); break;
        }
        player.setMap(map);
        player.addListener(this);
        
        switch (p2Index) {
            // place player2 on the opposite side of the level (right side)
            case 0: player2 = new AlexFighter2(p2X, p1StartY); break;
         //   case 1: player2 = new Cat2(map.getPlayerStartPosition().x - 50, map.getPlayerStartPosition().y); break; // replace later with Nicolini2 etc.
            case 2: player2 = new Boomer2(p2X - 50, p1StartY); break;
        //    case 3: player2 = new Cat2(map.getPlayerStartPosition().x - 50, map.getPlayerStartPosition().y); break;
       //    case 4: player2 = new Cat2(map.getPlayerStartPosition().x - 50, map.getPlayerStartPosition().y); break;
       //     case 5: player2 = new Cat2(map.getPlayerStartPosition().x - 50, map.getPlayerStartPosition().y); break;
        //    default: player2 = new Cat2(map.getPlayerStartPosition().x - 50, map.getPlayerStartPosition().y); break;
        }
        player2.setMap(map);
        player2.addListener(this);
    // initialize previous attack-enabled flags
    prevP1AttacksEnabled = player.isAttacksEnabled();
    prevP2AttacksEnabled = player2.isAttacksEnabled();
        // Debug spawn removed: projectiles will be spawned by player input instead.
        // screens
        levelClearedScreen = new LevelClearedScreen();
        levelLoseScreen = new LevelLoseScreen(this);

       
        this.playLevelScreenState = PlayLevelScreenState.RUNNING;

        // Start background music for the level with a loop
        try {
            
            bgMusicPlayer = new AudioPlayer("Resources/Sounds/BackgroundMusic.wav");
            bgMusicPlayer.playLoop();
        } catch (Exception e) {
            System.out.println("Failed to start background music: " + e.getMessage());
        }

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
                // Scheduler for periodic attack disable (runs before players update so effect is immediate)
                cooldownTimer += 16; // frame delta approximation (matches other timers)
                if (disabledPlayer == 0) {
                    if (cooldownTimer >= COOLDOWN_CYCLE_MS) {
                        cooldownTimer = 0;
                        int pick;
                        if (alternate) {
                            pick = (lastDisabledPlayer == 1) ? 2 : 1;
                            lastDisabledPlayer = pick;
                        } else {
                            pick = (Math.random() < 0.5) ? 1 : 2;
                        }
                        disabledPlayer = pick;
                        disableTimer = DISABLE_DURATION_MS;
                        if (disabledPlayer == 1 && player != null) player.setAttacksEnabled(false);
                        else if (disabledPlayer == 2 && player2 != null) player2.setAttacksEnabled(false);
                        if (Engine.Debug.ENABLED) System.out.println("DEBUG: Disabled attacks for player" + disabledPlayer);
                    }
                } else {
                    disableTimer -= 16;
                    if (disableTimer <= 0) {
                        if (disabledPlayer == 1 && player != null) player.setAttacksEnabled(true);
                        else if (disabledPlayer == 2 && player2 != null) player2.setAttacksEnabled(true);
                        if (Engine.Debug.ENABLED) System.out.println("DEBUG: Re-enabled attacks for player" + disabledPlayer);
                        disabledPlayer = 0;
                        disableTimer = 0;
                    }
                }

                // update player 1 and detect if they just became unable to attack; heal the same player when disabled
                player.update();
                if (player != null) {
                    boolean now = player.isAttacksEnabled();
                    if (prevP1AttacksEnabled && !now) {
                        // player1 just became unable to attack — heal player1 by 2
                        player.heal(2);
                        if (Engine.Debug.ENABLED) System.out.println("DEBUG: Player1 disabled -> healed Player1 by 2");
                    }
                    prevP1AttacksEnabled = now;
                }

                // update player 2 and detect if they just became unable to attack; heal the same player when disabled
                player2.update();
                if (player2 != null) {
                    boolean now2 = player2.isAttacksEnabled();
                    if (prevP2AttacksEnabled && !now2) {
                        // player2 just became unable to attack — heal player2 by 2
                        player2.heal(2);
                        if (Engine.Debug.ENABLED) System.out.println("DEBUG: Player2 disabled -> healed Player2 by 2");
                    }
                    prevP2AttacksEnabled = now2;
                }
                map.update(player, player2);
                // decrement hurt flash timers
                if (hurtFlashTimerP1 > 0) hurtFlashTimerP1 = Math.max(0, hurtFlashTimerP1 - 16);
                if (hurtFlashTimerP2 > 0) hurtFlashTimerP2 = Math.max(0, hurtFlashTimerP2 - 16);

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
                        stopMusic();
                        goBackToMenu();
                    }
                }
                break;

            // wait on level lose screen to make a decision (either resets level or sends player back to main menu)
            case LEVEL_LOSE:
                levelLoseScreen.update();
                // keep the lose sound playing; do not stop sounds each frame here
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
                    // choose the overlay image based on how much damage the player has taken
                    BufferedImage img1 = p1Default;
                    int health = player.getHealth();
                    int maxHealth = player.getMaxHealth();

                    // damageTaken ranges from 0..p1JumpImages.length
                    int damageTaken = 0;
                    if (maxHealth > 0) {
                        damageTaken = Math.max(0, Math.min(p1JumpImages.length, maxHealth - health));
                    }
                    if (damageTaken > 0) {
                        int idx = damageTaken - 1;
                        if (idx >= 0 && idx < p1JumpImages.length && p1JumpImages[idx] != null) {
                            img1 = p1JumpImages[idx];
                        }
                    }

                    // draw the chosen image above the player
                    if (img1 != null) {
                        int screenX = Math.round(player.getX() - map.getCamera().getX());
                        int screenY = Math.round(player.getY() - map.getCamera().getY());
                        int imgX = screenX + (player.getWidth() - img1.getWidth()) / 2;
                        int imgY = screenY - img1.getHeight() - 4;
                        graphicsHandler.drawImage(img1, imgX, imgY);

                        // hurt flash overlay (brief red flash when onHurt is triggered)
                        if (hurtFlashTimerP1 > 0) {
                            int alpha = Math.round(255f * (hurtFlashTimerP1 / (float) HURT_FLASH_MS));
                            alpha = Math.max(0, Math.min(255, alpha));
                            try {
                                graphicsHandler.drawFilledRectangle(imgX, imgY, img1.getWidth(), img1.getHeight(), new Color(255, 0, 0, alpha));
                            } catch (Exception ignored) {
                                graphicsHandler.drawFilledRectangle(imgX, imgY, img1.getWidth(), img1.getHeight(), new Color(255, 0, 0));
                            }
                        }

                        // subtle health tint: green when full, shifting to red as health decreases
                        try {
                            float t = 0f;
                            if (maxHealth > 0) t = 1f - (health / (float) maxHealth); // 0.0 = full, 1.0 = empty
                            t = Math.max(0f, Math.min(1f, t));
                            int r = Math.round(255 * t);
                            int g = Math.round(255 * (1f - t));
                            int a = Math.round(120 * t); // stronger tint at low health
                            if (a < 30) a = 30; // keep a minimum subtle tint
                            graphicsHandler.drawFilledRectangle(imgX, imgY, img1.getWidth(), img1.getHeight(), new Color(r, g, 0, a));
                        } catch (Exception ignored) {
                            // fallback: do nothing if Color with alpha isn't supported
                        }
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
                        if (hurtFlashTimerP2 > 0) {
                            int alpha2 = Math.round(255f * (hurtFlashTimerP2 / (float) HURT_FLASH_MS));
                            if (alpha2 < 0) alpha2 = 0; if (alpha2 > 255) alpha2 = 255;
                            try {
                                graphicsHandler.drawFilledRectangle(imgX2, imgY2, img2.getWidth(), img2.getHeight(), new Color(255, 0, 0, alpha2));
                            } catch (Exception ignored) {
                                graphicsHandler.drawFilledRectangle(imgX2, imgY2, img2.getWidth(), img2.getHeight(), new Color(255, 0, 0));
                            }
                        }
                        // draw health tint based on current health (green -> red)
                        try {
                            int health2 = player2.getHealth();
                            int maxHealth2 = player2.getMaxHealth();
                            float t2 = 0f;
                            if (maxHealth2 > 0) t2 = 1f - (health2 / (float) maxHealth2);
                            t2 = Math.max(0f, Math.min(1f, t2));
                            int r2 = Math.round(255 * t2);
                            int g2 = Math.round(255 * (1f - t2));
                            int a2 = Math.round(120 * t2);
                            if (a2 < 30) a2 = 30;
                            graphicsHandler.drawFilledRectangle(imgX2, imgY2, img2.getWidth(), img2.getHeight(), new Color(r2, g2, 0, a2));
                        } catch (Exception ignored) {
                        }
                    }
                }
                // show a small text if this player's attacks are currently disabled
                if (player != null && !player.isAttacksEnabled()) { // for player 1 (show when player attacks are disabled)
                    int screenXp1 = Math.round(player.getX() - map.getCamera().getX());
                    int screenYp1 = Math.round(player.getY() - map.getCamera().getY());
                    Font offFont = new Font("Arial Black", Font.BOLD, 12);
                    String offText = "ATTACKS OFF";
                    int centerX1 = screenXp1 + player.getWidth() / 2;
                    int textWidth1 = graphicsHandler.getGraphics().getFontMetrics(offFont).stringWidth(offText);
                    int txtX1c = centerX1 - textWidth1 / 2;
                    int txtY1c = screenYp1 - 12;
                    graphicsHandler.drawString(offText, txtX1c, txtY1c, offFont, Color.BLACK);
                }
                if (player2 != null && !player2.isAttacksEnabled()) { // for player 2 (show when player2 attacks are disabled)
                    int screenXp2 = Math.round(player2.getX() - map.getCamera().getX());
                    int screenYp2 = Math.round(player2.getY() - map.getCamera().getY());
                    Font offFont = new Font("Arial Black", Font.BOLD, 12);
                    String offText = "ATTACKS OFF";
                    int centerX2 = screenXp2 + player2.getWidth() / 2;
                    int textWidth2 = graphicsHandler.getGraphics().getFontMetrics(offFont).stringWidth(offText);
                    int txtX2c = centerX2 - textWidth2 / 2;
                    int txtY2c = screenYp2 - 12;
                    graphicsHandler.drawString(offText, txtX2c, txtY2c, offFont, Color.BLACK);
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
            // stop level background music and play death sound
            stopMusic();
            try {
                loseSoundPlayer = new AudioPlayer("Resources/Sounds/Death.wav");
                loseSoundPlayer.play();
            } catch (Exception e) {
                System.out.println("Failed to play lose sound: " + e.getMessage());
            }
            levelLoseScreen.initialize();
        }
    }

    @Override
    public void onHurt(GameObject source, int amount) {
        // when any player is hurt, we simply let the draw() method read player.getHealth() each frame
        // If we wanted to mirror actual HP across players we would call player.damage(amount,false)/player2.damage(...)
        // For now, no extra action is necessary here.
    }

    public void resetLevel() {
        stopMusic();
        initialize();
    }

    public void goBackToMenu() {
        stopMusic();
        screenCoordinator.setGameState(GameState.MENU);
    }

    private void stopMusic() {
        try {
            if (bgMusicPlayer != null) {
                bgMusicPlayer.stop();
                bgMusicPlayer.close();
                bgMusicPlayer = null;
            }
            if (loseSoundPlayer != null) {
                loseSoundPlayer.stop();
                loseSoundPlayer.close();
                loseSoundPlayer = null;
            }
        } catch (Exception e) {
            System.out.println("Failed to stop music: " + e.getMessage());
        }
    }

    public Player getPlayer() {
        return player;
    }

    public Player2 getPlayer2() {
        return player2;
    }

    // This enum represents the different states this screen can be in
    private enum PlayLevelScreenState {
        RUNNING, LEVEL_COMPLETED, LEVEL_LOSE
    }
}
