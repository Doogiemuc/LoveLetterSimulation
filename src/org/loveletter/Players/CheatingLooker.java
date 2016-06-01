package org.loveletter.Players;

import java.util.Set;

import org.loveletter.Card;
import org.loveletter.Player;

/**
 * This cheater looks at the players card instead of guessing
 */
public class CheatingLooker extends Player {
    
    /** random play, but will never play princess */
    @Override
    public Card chooseCardtoPlay() {
    	return card1.value < card2.value ? playCard1() : playCard2(); 
    }
    
    /**
     * always returns a random other player
     */
    @Override
    public Player getPlayerFor(int cardValue, Set<Player> availablePlayers) {
    	for (Player p : availablePlayers)
    		if (p.card1.value != Card.GUARD)
    			return p;
        return getRandomPlayer(availablePlayers);
    }

    /**
     * Guard: guess another player's card
     * @return value to guess (2-8). Guessing a Guard is not allowed
     */
    @Override
    public int guessCardValue(Player p) {
    	if (p.card1.value == Card.GUARD)
    		return -1;
        return p.card1.value;
    }
}
