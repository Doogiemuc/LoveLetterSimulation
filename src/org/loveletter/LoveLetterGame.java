package org.loveletter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.loveletter.Players.HighProbability;
import org.loveletter.Players.LowCardHighProbability;
import org.loveletter.Players.LowCardPrincess;
import org.loveletter.Players.LowCard;
import org.loveletter.Players.TestPlayer;

/**
 * Simulate Love Letter card game
 *
 */
public class LoveLetterGame {
    public static final int NUM_GAMES = 10000;
            
    public static void main(String[] args) {
        Log.logTRACE = false;
        Log.info("running ...");

        List<Player> players = new ArrayList<Player>();
        //players.add(new BestPlayer());
        //players.add(new HigherCardPlayer());
        players.add(new LowCard());
        //players.add(new RandomPlayer());
        //players.add(new RandomPlayer());        
        //players.add(new RandomPlayer());
        players.add(new HighProbability());
        //players.add(new RandomPrincessGuesser());
        players.add(new LowCardPrincess());
        players.add(new LowCardHighProbability());
        //players.add(new TestPlayer());
        
        assert(players.size() >= 3 && players.size() <= 4); //Extra rules apply for other player numbers
        HashMap<Player, Integer> wins = new HashMap<Player, Integer>();
        for (Player p : players)
        	wins.put(p, 0);
        
        for (int i = 0; i < NUM_GAMES; i++) {
            Board board = new Board(players);
            Log.traceAppend(board.getBoardShort());
            while (board.nextPlayer()) {
                Log.traceAppend(board.getBoardShort());
            }
            Log.trace(board.getBoardShort());
            Log.info(i+": "+board.gameStats.toString());
            for (Player player : board.gameStats.winners) {
                wins.put(player, wins.get(player)+1);
            }
        }
        
        StringBuffer buf = new StringBuffer();
        buf.append("Winners:");
        for (Player p : players) {
            buf.append(" "+p+":");
            buf.append(((double)wins.get(p)) / NUM_GAMES * 100);
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