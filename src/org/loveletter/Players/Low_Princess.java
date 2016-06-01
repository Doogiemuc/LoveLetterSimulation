package org.loveletter.Players;

import java.util.Set;

import org.loveletter.Card;
import org.loveletter.Player;

/**
 * Test Player
 */
public class Low_Princess extends Player {
    
    /** random play, but will never play princess */
    @Override
    public Card chooseCardtoPlay() {
        assert(card1 != null && card2 != null);
        
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
        return Card.PRINCESS;
    }
}
