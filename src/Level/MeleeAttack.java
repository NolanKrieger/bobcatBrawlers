package Level;

import GameObject.Rectangle;
import Engine.GraphicsHandler;
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;

public class MeleeAttack {
    private Rectangle bounds;
    private int damage;
    private int durationMs;
    private int ageMs = 0;
    private boolean alive = true;
    private boolean singleHit = true;
    private Set<Object> hitTargets = new HashSet<>();
    private Color attackColor; // Different colors for different attack types

    public MeleeAttack(float x, float y, int width, int height, int damage, int durationMs) {
        this.bounds = new Rectangle(x, y, width, height);
        this.damage = damage;
        this.durationMs = durationMs;
        
        // Set color based on damage to distinguish punch (1 damage) from kick (3 damage)
        if (damage == 1) {
            // Punch - red color
            this.attackColor = new Color(255, 0, 0, 120);
        } else if (damage == 3) {
            // Kick - orange color  
            this.attackColor = new Color(255, 165, 0, 120);
        } else {
            // Default - red color
            this.attackColor = new Color(255, 0, 0, 120);
        }
    }
    
    public void update(int dtMs, Map map, Player player, Player2 player2) {
        if (!alive) return;
        ageMs += dtMs;
        if (ageMs >= durationMs) {
            alive = false;
            return;
        }

        // check player 1
        if (player != null && bounds.intersects(player.getBounds())) {
            if (!singleHit || !hitTargets.contains(player)) {
                player.damage(damage);
                hitTargets.add(player);
            }
        }
        
        // check player 2
        if (player2 != null && bounds.intersects(player2.getBounds())) {
            if (!singleHit || !hitTargets.contains(player2)) {
                player2.damage(damage);
                hitTargets.add(player2);
            }
        }
    }
    
    public void draw(GraphicsHandler g, Map map) {
        if (!alive) return;
        int screenX = Math.round(bounds.getX() - map.getCamera().getX());
        int screenY = Math.round(bounds.getY() - map.getCamera().getY());
        g.drawFilledRectangle(screenX, screenY, bounds.getWidth(), bounds.getHeight(), attackColor);
    }
    
    public Rectangle getBounds() {return bounds; }
    public boolean isAlive() {return alive; }
}