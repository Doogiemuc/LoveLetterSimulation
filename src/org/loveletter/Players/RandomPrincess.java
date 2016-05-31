package org.loveletter.Players;

import java.util.Set;

import org.loveletter.Card;
import org.loveletter.Player;

/**
 * A player that always play's a random card and guesses Princess.
 */
public class RandomPrincess extends Player {
    
    /** random play, but will never play princess */
    @Override
    public Card chooseCardtoPlay() {
        assert(card1 != null && card2 != null);
        if (card1.value == Card.PRINCESS) return playCard2();
        if (card2.value == Card.PRINCESS) return playCard1();
        return rand.nextBoolean() ? playCard1() : playCard2();
    }
    
    /**
     * always returns a random other player
     */
    @Override
    public int getPlayerFor(int cardValue, Set<Integer> availablePlayerIds) {
        return getRandomPlayerId(availablePlayerIds);
    }

    /**
     * Guard: guess another player's card
     * @return value to guess (2-8). Guessing a Guard is not allowed
     */
    @Override
    public int guessCardValue() {
        return Card.PRINCESS;
    }
    
    @Override
    public void otherPlayerHasCard(int id, Card card) {
        //empty. RandomPlayer doesn't remember any cards :-)
    }
    
    @Override
    public void cardPlayed(int id, Card card) {
        //empty. RandomPlayer does not remember any cards.
    }
    
    @Override
    public String toString() {
        return "RandPG["+card1+"]";
    }

}
