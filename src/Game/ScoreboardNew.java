package Game;

/**
 * This class keeps track of the number of wins for each player
 */
public class ScoreboardNew {
    private int player1Wins = 0;
    private int player2Wins = 0;

    public void addPlayer1Win() {
        player1Wins++;
    }

    public void addPlayer2Win() {
        player2Wins++;
    }

    public int getPlayer1Wins() {
        return player1Wins;
    }

    public int getPlayer2Wins() {
        return player2Wins;
    }

    public void resetScoreboard() {
        player1Wins = 0;
        player2Wins = 0;
    }

    @Override
    public String toString() {
        return "Player 1: " + player1Wins + " | Player 2: " + player2Wins;
    }
}
