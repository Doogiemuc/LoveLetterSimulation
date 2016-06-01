package org.loveletter.Players;

import java.util.Set;

import org.loveletter.Card;
import org.loveletter.Player;

/**
 * Play lowest card, guess from memory else highest
 */
public class LowBrain extends Player {
    
    /** Testing */
    @Override
    public Card chooseCardtoPlay() {
        assert(card1 != null && card2 != null);
                        
        // Play lower card
        return card1.value < card2.value ? playCard1() : playCard2(); 
    }
    
    /**
     * Return player with known card else returns a random other player
     */
    @Override
    public int getPlayerFor(int cardValue, Set<Integer> availablePlayerIds) {
    	int r = getPlayerWithHighestCard(availablePlayerIds);
    	if (r != -1)
    		return r;
    	return getRandomPlayerId(availablePlayerIds);    	
    }

    /**
     * Guard: guess another player's card
     * @return value to guess (2-8). Guessing a Guard is not allowed
     */
    @Override
    public int guessCardValue(int playerId) {
        //----- if we know a players card, then guess it and throw him out
        Card knownCard = knownCards.get(playerId);
        if (knownCard != null) { 
        	if (knownCard.value == Card.GUARD)
        		return -1;
            return knownCard.value;
        }
        
    	// Guess highest card that is left at least once
    	for (int i = Card.PRINCESS; i > Card.GUARD; i--) {
    		if (getCardsLeft(i) > 0)
    				return i;
    	}
        return -1;
    }
}
