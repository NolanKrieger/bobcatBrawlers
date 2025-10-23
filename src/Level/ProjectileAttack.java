package Level;

import GameObject.Rectangle;
import GameObject.GameObject;
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
    private GameObject owner; // which player fired this projectile (null if from enemy)

    public ProjectileAttack(float x, float y, float vx, float vy, int damage, int lifeMs, boolean fromPlayer, GameObject owner) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.damage = damage;
        this.lifeMs = lifeMs;
        this.fromPlayer = fromPlayer;
        this.owner = owner;
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
                        player2.damage(damage);
                        alive = false;
                        return;
                    }
                }
                // owner is player2
                else if (owner == player2 && player != null) {
                    if (projRect.intersects(player.getBounds())) {
                        if (Engine.Debug.ENABLED) System.out.println("DEBUG: Projectile from player hit player at x=" + x + " y=" + y + " dmg=" + damage + " healthBefore=" + player.getHealth());
                        player.damage(damage);
                        alive = false;
                        return;
                    }
                }
            }
        } else {
            // from enemy: hit whichever player it intersects
            if (player != null && projRect.intersects(player.getBounds())) {
                if (Engine.Debug.ENABLED) System.out.println("DEBUG: Enemy projectile hit player at x=" + x + " y=" + y + " dmg=" + damage + " healthBefore=" + player.getHealth());
                player.damage(damage);
                alive = false;
                return;
            }
            if (player2 != null && projRect.intersects(player2.getBounds())) {
                if (Engine.Debug.ENABLED) System.out.println("DEBUG: Enemy projectile hit player2 at x=" + x + " y=" + y + " dmg=" + damage + " healthBefore=" + player2.getHealth());
                player2.damage(damage);
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