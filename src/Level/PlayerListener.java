package Level;
import GameObject.GameObject;

// Other classes can use this interface to listen for events from the Player class
public interface PlayerListener {
    void onLevelCompleted();
    void onDeath();
    // called when a player takes damage
    void onHurt(GameObject source, int amount);
}
