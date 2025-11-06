package Level;

import GameObject.Rectangle;
import GameObject.GameObject;
import Engine.GraphicsHandler;
import Engine.ImageLoader;
import Engine.Keyboard;
import Engine.Key;
import Engine.AudioPlayer;
import java.awt.Color;
import java.awt.image.BufferedImage;

public class ProjectileAttack {
    // Static variables for projectile type cycling - separate for each player
    private static int player1ProjectileType = 0;
    private static int player2ProjectileType = 0;
    private static String[] projectileImages = {"PencilPixel.png", "BurritoPixel.png", "ComputerPixel.png"};
    private static String[] projectileNames = {"Pencil", "Burrito", "Computer"}; // Display names for UI
    private static float[] projectileDamage = {0.5f, 0.5f, 1.0f}; // PencilPixel=0.25, BurritoPixel=0.5, ComputerPixel=1.0
    private static boolean xKeyWasPressed = false; // For player 1
    private static boolean nKeyWasPressed = false; // For player 2
    // Audio for firing projectile (loaded once)
    private static AudioPlayer projectileSound = new AudioPlayer("Resources/Sounds/Shooting.wav");
    
    private float x,y;
    private float vx, vy;
    private int width = 27;
    private int height = 27;
    private float damage; // Changed to float to support fractional damage
    private int lifeMs;
    private int ageMs = 0;
    private boolean alive = true;
    private boolean fromPlayer;
    private GameObject owner; // which player fired this projectile (null if from enemy)
    private BufferedImage projectileImage;

    public ProjectileAttack(float x, float y, float vx, float vy, int damage, int lifeMs, boolean fromPlayer, GameObject owner) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.lifeMs = lifeMs;
        this.fromPlayer = fromPlayer;
        this.owner = owner;
        
        // Determine which player's projectile type to use based on owner
        int projectileType;
        if (owner instanceof Player) {
            // Player 1
            projectileType = player1ProjectileType;
        } else if (owner instanceof Player2) {
            // Player 2
            projectileType = player2ProjectileType;
        } else {
            // Default to player 1 type if owner is null or unknown
            projectileType = player1ProjectileType;
        }
        
        // Use damage and image based on the appropriate player's projectile type
        this.damage = projectileDamage[projectileType];
        this.projectileImage = ImageLoader.load(projectileImages[projectileType]);
        // play firing sound when projectile is spawned by a player
        try {
            if (fromPlayer && projectileSound != null) projectileSound.play();
        } catch (Exception e) {
            if (Engine.Debug.ENABLED) System.out.println("DEBUG: Failed to play projectile sound: " + e);
        }
    }
    
    // Static method to check for 'X' and 'N' key presses and cycle projectile types
    public static void updateProjectileTypes() {
        // Handle Player 1 ('X' key)
        boolean xKeyIsPressed = Keyboard.isKeyDown(Key.X);
        if (xKeyIsPressed && !xKeyWasPressed) {
            player1ProjectileType = (player1ProjectileType + 1) % projectileImages.length;
            System.out.println("Player 1 projectile changed to: " + projectileImages[player1ProjectileType] + 
                             " (Damage: " + projectileDamage[player1ProjectileType] + ")");
        }
        xKeyWasPressed = xKeyIsPressed;
        
        // Handle Player 2 ('N' key)
        boolean nKeyIsPressed = Keyboard.isKeyDown(Key.N);
        if (nKeyIsPressed && !nKeyWasPressed) {
            player2ProjectileType = (player2ProjectileType + 1) % projectileImages.length;
            System.out.println("Player 2 projectile changed to: " + projectileImages[player2ProjectileType] + 
                             " (Damage: " + projectileDamage[player2ProjectileType] + ")");
        }
        nKeyWasPressed = nKeyIsPressed;
    }
    
    // Static getter methods for player-specific projectile info
    public static String getPlayer1ProjectileImage() {
        return projectileImages[player1ProjectileType];
    }
    
    public static String getPlayer1ProjectileName() {
        return projectileNames[player1ProjectileType];
    }
    
    public static float getPlayer1ProjectileDamage() {
        return projectileDamage[player1ProjectileType];
    }
    
    public static String getPlayer2ProjectileImage() {
        return projectileImages[player2ProjectileType];
    }
    
    public static String getPlayer2ProjectileName() {
        return projectileNames[player2ProjectileType];
    }
    
    public static float getPlayer2ProjectileDamage() {
        return projectileDamage[player2ProjectileType];
    }
    
    public void update(int dtMs, Map map, Player player, Level.Player2 player2) {
        if (!alive) return;

        x += vx * dtMs / 1000f;
        y += vy * dtMs / 1000f;

        ageMs += dtMs;
        if (ageMs >= lifeMs) {
            alive = false;
            return;
        }
        boolean hitTile = false;
        float cx = x + width / 2f;
        float cy = y + height / 2f;
        MapTile[] samples = new MapTile[] {
            map.getTileByPosition(cx, cy),
            map.getTileByPosition(x + 1, y + 1), 
            map.getTileByPosition(x + width - 1, y + 1),
            map.getTileByPosition(x + 1, y + height - 1),
            map.getTileByPosition(x + width - 1, y + height - 1)
        };
        for (MapTile t : samples) {
            if (t != null && t.getTileType() == TileType.NOT_PASSABLE) {
                hitTile = true;
                break;
            }
        }
        if (hitTile) {
            alive = false;
            if (Engine.Debug.ENABLED) System.out.println("DEBUG: Projectile died on tile collision at x=" + x + " y=" + y);
            return;
        }
        Rectangle projRect = new Rectangle(x, y, width, height);
        // If projectile is from a player, it should hit the other player (not its owner)
        if (fromPlayer) {
            if (owner != null) {
                // owner is player (player1)
                if (owner == player && player2 != null) {
                    if (projRect.intersects(player2.getBounds())) {
                        if (Engine.Debug.ENABLED) System.out.println("DEBUG: Projectile from player hit player2 at x=" + x + " y=" + y + " dmg=" + damage);
                        player2.damage(Math.round(damage));
                        alive = false;
                        return;
                    }
                }
                // owner is player2
                else if (owner == player2 && player != null) {
                    if (projRect.intersects(player.getBounds())) {
                        if (Engine.Debug.ENABLED) System.out.println("DEBUG: Projectile from player hit player at x=" + x + " y=" + y + " dmg=" + damage + " healthBefore=" + player.getHealth());
                        player.damage(Math.round(damage));
                        alive = false;
                        return;
                    }
                }
            }
        } else {
            // from enemy: hit whichever player it intersects
            if (player != null && projRect.intersects(player.getBounds())) {
                if (Engine.Debug.ENABLED) System.out.println("DEBUG: Enemy projectile hit player at x=" + x + " y=" + y + " dmg=" + damage + " healthBefore=" + player.getHealth());
                player.damage(Math.round(damage));
                alive = false;
                return;
            }
            if (player2 != null && projRect.intersects(player2.getBounds())) {
                if (Engine.Debug.ENABLED) System.out.println("DEBUG: Enemy projectile hit player2 at x=" + x + " y=" + y + " dmg=" + damage + " healthBefore=" + player2.getHealth());
                player2.damage(Math.round(damage));
                alive = false;
                return;
            }
        }
    }
    public void draw(GraphicsHandler g, Map map) {
        if (!alive) return;
        int screenX = Math.round(x - map.getCamera().getX());
        int screenY = Math.round(y - map.getCamera().getY());
        
        // Draw the PencilPixel.png image instead of a colored rectangle
        if (projectileImage != null) {
            g.drawImage(projectileImage, screenX, screenY, width, height);
        } else {
            // Fallback to the original rectangle if image fails to load
            try {
                g.drawFilledRectangle(screenX, screenY, width, height, new Color(255, 140, 0, 200));
            } catch (Exception ignored) {
                g.drawFilledRectangle(screenX, screenY, width, height, new Color(255, 140, 0));
            }
        }
    }
    public boolean isAlive() {return alive; }
}