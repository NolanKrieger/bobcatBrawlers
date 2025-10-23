package Level;

import GameObject.Rectangle;
import Engine.GraphicsHandler;
import java.awt.Color;

public class ProjectileAttack {
    private float x,y;
    private float vx, vy;
    private int width = 8;
    private int height = 8;
    private int damage;
    private int lifeMs;
    private int ageMs = 0;
    private boolean alive = true;
    private boolean fromPlayer;

    public ProjectileAttack(float x, float y, float vx, float vy, int damage, int lifeMs, boolean fromPlayer) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.damage = damage;
        this.lifeMs = lifeMs;
        this.fromPlayer = fromPlayer;
    }
    public void update(int dtMs, Map map, Player player) {
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
        if (!fromPlayer && player != null) {
            Rectangle projRect = new Rectangle(x, y, width, height);
            if (projRect.intersects(player.getBounds())) {
                if (Engine.Debug.ENABLED) System.out.println("DEBUG: Projectile hit player at x=" + x + " y=" + y + " dmg=" + damage);
                player.damage(damage);
                alive = false;
                return;
            }
        }
    }
    public void draw(GraphicsHandler g, Map map) {
        if (!alive) return;
        int screenX = Math.round(x - map.getCamera().getX());
        int screenY = Math.round(y - map.getCamera().getY());
        try {
            g.drawFilledRectangle(screenX, screenY, width, height, new Color(255, 140, 0, 200));
        } catch (Exception ignored) {
            g.drawFilledRectangle(screenX, screenY, width, height, new Color(255, 140, 0));
        }
    }
    public boolean isAlive() {return alive; }
}