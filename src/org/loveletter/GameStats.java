package org.loveletter;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Statistics of one game after is has finished.
 */
public class GameStats {
    List<Card> initialStack;
    List<Player> players;
    int roundsPlayed = 0;
    Set<Player> winners = new HashSet<Player>();
    Map<Player, List<Card>> playedCards;

    public GameStats(List<Card> initialStack, List<Player> players, int roundsPlayed, Map<Player, List<Card>> playedCards) {
        int highestValue = 0;
        for (Player player : players) {
            if (player.inGame && player.card1.value > highestValue) {
                winners.clear();
                winners.add(player);
                highestValue = player.card1.value;
            } else
            if (player.inGame && player.card1.value == highestValue) {
                winners.add(player);  // more than one winner
            }                
        }
        this.initialStack = initialStack;
        this.players = players;
        this.roundsPlayed = roundsPlayed;
        this.playedCards = playedCards;
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
            buf.append("(");
            for (Card card : playedCards.get(player)) {
                buf.append(card.value);
            }
            buf.append("), ");
        }
        buf.append("rounds="+roundsPlayed);
        buf.append(", winners="+winners);
        buf.append("]");
        return buf.toString();
    }

}
