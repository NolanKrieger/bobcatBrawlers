package Game;

/**
 * This class keeps track of the number of lives for each player
 */
public class Lives {
    private int player1Lives = 3;
    private int player2Lives = 3;

    public void losePlayer1Life() {
        if (player1Lives > 0) {
            player1Lives--;
        }
    }

    public void losePlayer2Life() {
        if (player2Lives > 0) {
            player2Lives--;
        }
    }

    public int getPlayer1Lives() {
        return player1Lives;
    }

    public int getPlayer2Lives() {
        return player2Lives;
    }

    public void resetLives() {
        player1Lives = 3;
        player2Lives = 3;
    }

    @Override
    public String toString() {
        return "Player 1: " + player1Lives + " | Player 2: " + player2Lives;
    }
}
