package org.loveletter;

import java.util.Set;

/**
 * This player always plays the card with the higher value
 */
public class HigherCardPlayer extends Player {
    
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
    public int getPlayerFor(int cardValue, Set<Integer> availablePlayerIds) {
        int size = availablePlayerIds.size(); 
        if (size == 0) return -1;
        //return random element from Set
        int countTo = rand.nextInt(size); 
        int counter = 0;
        for(Integer id : availablePlayerIds)
        {
            if (counter == countTo) return id;
            counter++;
        }
        Log.error("Did not find random player");
        return id;
    }

    @Override
    public int guessCardValue() {
        return 2+rand.nextInt(7);      // card value (2-8)
    }

    @Override
    public void otherPlayerHasCard(int id, Card card) {
        //HigherCardPlayer does not remember any cards.
    }

    @Override
    public void cardPlayed(int id, Card card) {
        //HigherCardPlayer does not remember any cards.
    }

    @Override
    public String toString() {
        return "High"+id+"["+card1+"]";
    }

}

