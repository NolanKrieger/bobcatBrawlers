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

    public MeleeAttack(float x, float y, int width, int height, int damage, int durationMs) {
        this.bounds = new Rectangle(x, y, width, height);
        this.damage = damage;
        this.durationMs = durationMs;
    }
    public void update(int dtMs, Map map, Player player) {
        if (!alive) return;
        ageMs += dtMs;
        if (ageMs >= durationMs) {
            alive = false;
            return;
        }

        // check player
        if (player != null && bounds.intersects(player.getBounds())) {
            if (!singleHit || !hitTargets.contains(player)) {
                player.damage(damage);
                hitTargets.add(player);
            }
        }
    }
    public void draw(GraphicsHandler g, Map map) {
        if (!alive) return;
        int screenX = Math.round(bounds.getX() - map.getCamera().getX());
        int screenY = Math.round(bounds.getY() - map.getCamera().getY());
        g.drawFilledRectangle(screenX, screenY, bounds.getWidth(), bounds.getHeight(), new Color(255, 0,0, 120));
    }
    public Rectangle getBounds() {return bounds; }
    public boolean isAlive() {return alive; }

}