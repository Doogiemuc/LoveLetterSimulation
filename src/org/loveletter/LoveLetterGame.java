package org.loveletter;

import java.util.ArrayList;
import java.util.List;

/**
 * Simulate Love Letter card game
 *
 */
public class LoveLetterGame {
    public static final int NUM_GAMES = 1000;
        
    static int[] winner;
    
    public static void main(String[] args) {
        Log.logTRACE = false;
        Log.info("running ...");

        // TODO: This only is correct for 3 and 4 players (for two players extra rules apply)
        List<Player> players = new ArrayList<Player>();
        //players.add(new BestPlayer());
        //players.add(new HigherCardPlayer());
        players.add(new RandomPlayer());
        players.add(new RandomPlayer());
        players.add(new RandomPlayer());
        players.add(new RandomPlayer());
        
        winner = new int[players.size()];       
        
        for (int i = 0; i < NUM_GAMES; i++) {
            Board board = new Board(players);
            Log.traceAppend(board.getBoardShort());
            while (board.nextPlayer()) {
                Log.traceAppend(board.getBoardShort());
            }
            Log.trace(board.getBoardShort());
            Log.info(i+": "+board.gameStats.toString());
            for (Player player : board.gameStats.winners) {
                winner[player.id]++;
            }
        }
        
        StringBuffer buf = new StringBuffer();
        buf.append("Winners:");
        for (int i = 0; i < players.size(); i++) {
            buf.append(" "+players.get(i)+":");
            buf.append(((double)winner[i]) / NUM_GAMES * 100);
            buf.append("%");
        }
        Log.info(buf.toString());
        
    }

}

/*

  The cards
  8-PRINCESS    When played you loose.
  7-COUNTESS    Must be played when you also have the king or prince.
  6-KING        Exchange cards with another player.
  5-PRINCE      Choose a player that must discard his card and draw a new card.
  4-MAID        You are save until your next turn.
  3-BARON       Compare cards with another player. Lower card is out.
  2-PRIEST      Look at another players hand.
  1-GUARD       Try to guess another players hand. Cannot name GUARD
*/