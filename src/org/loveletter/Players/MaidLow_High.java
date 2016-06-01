package org.loveletter.Players;

import java.util.Set;

import org.loveletter.Card;
import org.loveletter.Player;

/**
 * Play Maid, else lowest card. Guess highest card left.
 */
public class MaidLow_High extends Player {
    
    /** Testing */
    @Override
    public Card chooseCardtoPlay() {
        assert(card1 != null && card2 != null);
                
        // Play maid if have one
        if (hasCardValue(Card.MAID)>0) 
        	return playValue(Card.MAID);          
                
        // Play lower card
        return card1.value < card2.value ? playCard1() : playCard2(); 
    }
    
    /**
     * always returns a random other player
     */
    @Override
    public Player getPlayerFor(int cardValue, Set<Player> availablePlayers) {
        return getRandomPlayer(availablePlayers);
    }

    /**
     * Guard: guess another player's card
     * @return value to guess (2-8). Guessing a Guard is not allowed
     */
    @Override
    public int guessCardValue(Player p) {
    	// Guess highest card that is left at least once
    	for (int i = Card.PRINCESS; i > Card.GUARD; i--) {
    		if (getCardsLeft(i) > 0)
    				return i;
    	}
        return -1;
    }
}
