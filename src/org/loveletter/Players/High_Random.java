package org.loveletter.Players;

import java.util.Set;

import org.loveletter.Card;
import org.loveletter.Player;

/**
 * This player always plays the card with the higher value
 */
public class High_Random extends Player {
    
    /** Always chose card with <b>higher</b> value, unless it is the princess. */
    @Override
    public Card chooseCardtoPlay() {
        if (card1.value == Card.PRINCESS) return playCard2();
        if (card2.value == Card.PRINCESS) return playCard1();
        //TODO: should HigherCardPlaer also never play 7COUNTESS ?
        return card1.value > card2.value ? playCard1() : playCard2(); 
    }

    /** get random id of other players. */
    @Override
    public Player getPlayerFor(int cardValue, Set<Player> availablePlayers) {
        return getRandomPlayer(availablePlayers);
    }

    @Override
    public int guessCardValue(Player p) {
        return 2+rand.nextInt(7);      // card value (2-8)
    }
}

