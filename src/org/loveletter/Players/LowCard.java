package org.loveletter.Players;

import java.util.Set;

import org.loveletter.Card;
import org.loveletter.Player;

/**
 * This player always plays the card with the lower value
 */
public class LowCard extends Player {
    
    /** Always chose card with <b>lower</b> value. */
    @Override
    public Card chooseCardtoPlay() {
        return card1.value < card2.value ? playCard1() : playCard2(); 
    }

    /** get random id of other players. */
    @Override
    public int getPlayerFor(int cardValue, Set<Integer> availablePlayerIds) {
    	return getRandomPlayerId(availablePlayerIds);
    }

    @Override
    public int guessCardValue(int playerId) {
        return 2+rand.nextInt(7);      // card value (2-8)
    }
}

