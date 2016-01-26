package org.loveletter;

import java.util.ArrayList;
import java.util.List;

/**
 * Simulate Love Letter card game
 *
 */
public class LoveLetterGame {
    public static final int NUM_GAMES = 100;
    
    public static final int NUM_PLAYERS = 4;
    
    public static void main(String[] args) {
        Log.logTRACE = false;
        Log.info("running ...");

        List<Player> players = new ArrayList<Player>();
        players.add(new BestPlayer());
        players.add(new HigherCardPlayer());
        players.add(new RandomPlayer());
        players.add(new RandomPlayer());
        
        for (int i = 0; i < NUM_GAMES; i++) {
            Board board = new Board(players);
            Log.trace(board.getBoardShort());
            while (board.nextPlayer()) {
                Log.trace(board.getBoardShort());
            }
            Log.trace(board.getBoardShort());
            Log.info(i+": "+board.gameStats.toString());
        }
    }

}

/*

  The cards
  8-PRINCESS    When played you loose
  7-COUNTESS    Must be played when you also have the king or prince
  6-KING        Exchange cards with another player
  5-PRINCE      Choose a player that must discard his card
  4-MAID        You are save until your next turn.
  3-BARON       Compare cards with another player. Lower card is out.
  2-PRIEST      Look at another players hand
  1-GUARD       Try to uess another players hand
*/