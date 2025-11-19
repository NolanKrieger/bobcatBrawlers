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
    private static float[] projectileDamage = {1.0f, 1.5f, 2.0f};
    private static float[] projectileSpeed = {400f, 300f, 260f};
    private static boolean xKeyWasPressed = false; // For player 1
    private static boolean nKeyWasPressed = false; // For player 2
    // Audio for firing projectile (loaded once)
    private static AudioPlayer projectileSound = new AudioPlayer("Resources/Sounds/Shooting.wav");
    
    private float x,y;
    private float vx, vy;
    private int width = 27;
    private int height = 27;
    private float damage; // Changed to float to support fractional damage
    private float damagePerSecond = 5.0f; // Damage-over-time rate
    private int dotDurationMs = 0; // Duration of DOT effect in milliseconds
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
        
        this.projectileImage = ImageLoader.load(projectileImages[projectileType]);
        // Use projectile type damage for both player and enemy projectiles
        this.damage = projectileDamage[projectileType];
        
        // play firing sound when projectile is used
        try {
            if (fromPlayer && projectileSound != null) projectileSound.play();
        } catch (Exception e) {
            if (Engine.Debug.ENABLED) System.out.println("DEBUG: Failed to play projectile sound: " + e);
        }
    }

        // Overloaded constructor: allow explicit image and damage (useful for enemy hazards)
        public ProjectileAttack(float x, float y, float vx, float vy, int damage, int lifeMs, boolean fromPlayer, GameObject owner, String imageOverride) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.lifeMs = lifeMs;
            this.fromPlayer = fromPlayer;
            this.owner = owner;
            this.ageMs = 0;
            this.alive = true;
            //  image override
            try {
                this.projectileImage = ImageLoader.load(imageOverride);
            } catch (RuntimeException e) {
                int projectileType = player1ProjectileType;
                this.projectileImage = ImageLoader.load(projectileImages[projectileType]);
            }
            // Use the provided damage value directly
            this.damage = damage;
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
    
    public static float getPlayer1ProjectileSpeed() {
        return projectileSpeed[player1ProjectileType];
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
    
    public static float getPlayer2ProjectileSpeed() {
        return projectileSpeed[player2ProjectileType];
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
        // Shield collision rectangles
        Rectangle shieldRect1 = null;
        Rectangle shieldRect2 = null;
        if (player instanceof Players.Nicolini && ((Players.Nicolini)player).canReflectProjectiles()) {
            float shieldX = player.getX() + player.getWidth();
            float shieldY = player.getY();
            shieldRect1 = new Rectangle(shieldX, shieldY, player.getWidth(), player.getHeight());
        }
        if (player2 instanceof Players.Nicolini2 && ((Players.Nicolini2)player2).canReflectProjectiles()) {
            float shieldX = player2.getX() - player2.getWidth();
            float shieldY = player2.getY();
            shieldRect2 = new Rectangle(shieldX, shieldY, player2.getWidth(), player2.getHeight());
        }
        // If projectile is from a player, it can hit either player (including owner/self)
        if (fromPlayer) {
            if (shieldRect1 != null && projRect.intersects(shieldRect1)) {
                vx = -vx;
                if (Engine.Debug.ENABLED) System.out.println("DEBUG: Projectile reflected by player1 shield at x=" + x + " y=" + y);
                return;
            }
            if (shieldRect2 != null && projRect.intersects(shieldRect2)) {
                vx = -vx;
                if (Engine.Debug.ENABLED) System.out.println("DEBUG: Projectile reflected by player2 shield at x=" + x + " y=" + y);
                return;
            }
            if (player != null && projRect.intersects(player.getBounds())) {
                if (Engine.Debug.ENABLED) System.out.println("DEBUG: Player hit by own projectile at x=" + x + " y=" + y + " dmg=" + damage);
                player.damage(Math.round(damage));
                alive = false;
                return;
            }
            if (player2 != null && projRect.intersects(player2.getBounds())) {
                if (Engine.Debug.ENABLED) System.out.println("DEBUG: Player2 hit by own projectile at x=" + x + " y=" + y + " dmg=" + damage);
                player2.damage(Math.round(damage));
                alive = false;
                return;
            }
        } else {
            // from enemy: hit whichever player it intersects
            if (shieldRect1 != null && projRect.intersects(shieldRect1)) {
                vx = -vx;
                if (Engine.Debug.ENABLED) System.out.println("DEBUG: Enemy projectile reflected by player1 shield at x=" + x + " y=" + y);
                return;
            }
            if (shieldRect2 != null && projRect.intersects(shieldRect2)) {
                vx = -vx;
                if (Engine.Debug.ENABLED) System.out.println("DEBUG: Enemy projectile reflected by player2 shield at x=" + x + " y=" + y);
                return;
            }
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
    
    public float getDamagePerSecond() { return damagePerSecond; }
    public int getDotDurationMs() { return dotDurationMs; }
}