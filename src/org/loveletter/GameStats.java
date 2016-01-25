package org.loveletter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * statistics of one game after is has finished.
 */
public class GameStats {
    public List<Card> initialStack;
    public List<Player> players;
    public int roundsPlayed = 0;
    public Set<String> winners;

    public GameStats(List<Card> initialStack, List<Player> players, int roundsPlayed, Set<String> winners) {
        this.initialStack = initialStack;
        this.players = players;
        this.roundsPlayed = roundsPlayed;
        this.winners = winners;
    }

    public GameStats(List<Card> initialStack, List<Player> players, int roundsPlayed) {
        Set<String> winners = new HashSet<String>(); 
        int highestValue = 0;
        for (Player player : players) {
            if (player.inGame && player.card1.value > highestValue) {
                winners = new HashSet<String>();
                winners.add(player.toString());
                highestValue = player.card1.value;
            } else
            if (player.inGame && player.card1.value == highestValue) {
                winners.add(player.toString());  // more than one winner
            }                
        }
        this.initialStack = initialStack;
        this.players = players;
        this.roundsPlayed = roundsPlayed;
        this.winners = winners;
    }

    /**
     * create a short one line summary of this game
     */
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("GameStats[");
        for (Card card : initialStack) {
            buf.append(card.value);
        }
        buf.append(", ");
        for (Player player : players) {
            buf.append(player.toString());
//            buf.append("(");
//            for (Card card : player.playedCards) {
//                buf.append(card.value);
//            }
//            buf.append(") ");
        }
        buf.append(", rounds="+roundsPlayed);
        buf.append(", winners="+winners);
        buf.append("]");
        return buf.toString();
    }

}
