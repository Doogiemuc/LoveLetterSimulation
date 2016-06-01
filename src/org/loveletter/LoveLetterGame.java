package org.loveletter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.loveletter.Players.Best;
import org.loveletter.Players.CheatingLooker;
import org.loveletter.Players.High;
import org.loveletter.Players.HighCard;
import org.loveletter.Players.HighProbability;
import org.loveletter.Players.LowBrain;
import org.loveletter.Players.LowCard;
import org.loveletter.Players.LowCardHigh;
import org.loveletter.Players.LowCardHighProbability;
import org.loveletter.Players.LowCardPrincess;
import org.loveletter.Players.MaidLowCardHigh;
import org.loveletter.Players.MaidLowCardPrincess;
import org.loveletter.Players.Random;
import org.loveletter.Players.RandomPrincess;
import org.loveletter.Players.TestPlayer;

/**
 * Simulate Love Letter card game
 *
 */
public class LoveLetterGame {
    public static final int NUM_GAMES = 100000;
            
    public static void main(String[] args) {
        Log.logTRACE = false;
        Log.info("running ...");

        // Create a pool of players from which random players are selected for a game
        List<Player> playerPool = new ArrayList<Player>();
        
        // Real players        
        playerPool.add(new Best());
        playerPool.add(new HighCard());
        playerPool.add(new LowCard());
        playerPool.add(new Random());
        playerPool.add(new HighProbability());
        playerPool.add(new RandomPrincess());
        playerPool.add(new LowCardPrincess());
        playerPool.add(new LowCardHighProbability());
        playerPool.add(new MaidLowCardPrincess());
        playerPool.add(new MaidLowCardHigh());
        playerPool.add(new High());
        playerPool.add(new LowCardHigh());
        playerPool.add(new LowBrain());
        playerPool.add(new TestPlayer());
        
        // Cheaters -- Do not include in fair comparison
        //playerPool.add(new CheatingLooker());
        
        // Initialize statistics
        HashMap<Player, Integer> wins = new HashMap<Player, Integer>();
        HashMap<Player, Integer> plays = new HashMap<Player, Integer>();
        for (Player p : playerPool) {
        	plays.put(p, 0);
        	wins.put(p, 0);        	
        }
        
        List<Player> players = new ArrayList<Player>();
        for (int i = 0; i < NUM_GAMES; i++) {
        	
        	// Pick four random players from the pool for a game
        	players.clear();
        	Collections.shuffle(playerPool);
        	for (int p = 0; p < 4; p++) {
        		players.add(playerPool.get(p));
        		plays.put(playerPool.get(p), plays.get(playerPool.get(p))+1);
        	}
        	
        	// Play the game
            Board board = new Board(players);
            Log.traceAppend(board.getBoardShort());
            while (board.nextPlayer())
                Log.traceAppend(board.getBoardShort());
            Log.trace(board.getBoardShort());
            Log.info(i+": "+board.gameStats.toString());
            for (Player player : board.gameStats.winners) {
                wins.put(player, wins.get(player)+1);
            }
        }
        
        // Sort playerPool by winratio
        Collections.sort(playerPool, new Comparator<Player>() {
			@Override
			public int compare(Player p1, Player p2) {
				if (((double)wins.get(p1)) / plays.get(p1) < ((double)wins.get(p2)) / plays.get(p2))
					return 1;
				return -1;	
			}});
        
        // Output table of winners
        StringBuffer buf = new StringBuffer();
        buf.append("Winners:\n");
        for (Player p : playerPool) {
            buf.append(" "+pad(p+":", 25));
            buf.append(((double)wins.get(p)) / plays.get(p) * 100);
            buf.append("%\n");
        }
        Log.info(buf.toString());
        
    }
    
	public static String pad(String s, int i) {
		while (s.length() <= i)
			s+= ' ';
		return s;
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