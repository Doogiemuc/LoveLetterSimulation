package org.loveletter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.loveletter.Players.Best;
import org.loveletter.Players.HighCard;
import org.loveletter.Players.HighProbability;
import org.loveletter.Players.LowCard;
import org.loveletter.Players.LowCardHighProbability;
import org.loveletter.Players.LowCardPrincess;
import org.loveletter.Players.Random;
import org.loveletter.Players.RandomPrincess;
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

        List<Player> playerPool = new ArrayList<Player>();
        playerPool.add(new Best());
        playerPool.add(new HighCard());
        playerPool.add(new LowCard());
        playerPool.add(new Random());
        playerPool.add(new HighProbability());
        playerPool.add(new RandomPrincess());
        playerPool.add(new LowCardPrincess());
        playerPool.add(new LowCardHighProbability());
        playerPool.add(new TestPlayer());
        
        HashMap<Player, Integer> wins = new HashMap<Player, Integer>();
        HashMap<Player, Integer> plays = new HashMap<Player, Integer>();
        for (Player p : playerPool) {
        	plays.put(p, 0);
        	wins.put(p, 0);        	
        }
        
        List<Player> players = new ArrayList<Player>();
        for (int i = 0; i < NUM_GAMES; i++) {
        	players.clear();
        	Collections.shuffle(playerPool);
        	for (int p = 0; p < 4; p++) {
        		players.add(playerPool.get(p));
        		plays.put(playerPool.get(p), plays.get(playerPool.get(p))+1);
        	}
        	            
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
        
        Collections.sort(playerPool, new Comparator<Player>() {
			@Override
			public int compare(Player p1, Player p2) {
				if (((double)wins.get(p1)) / plays.get(p1) < ((double)wins.get(p2)) / plays.get(p2))
					return 1;
				return -1;	
			}});
        
        StringBuffer buf = new StringBuffer();
        buf.append("Winners:\n");
        for (Player p : playerPool) {
            buf.append(" "+p+":\t");
            buf.append(((double)wins.get(p)) / plays.get(p) * 100);
            buf.append("%\n");
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