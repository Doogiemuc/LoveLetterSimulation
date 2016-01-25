package org.loveletter;

import java.util.Random;

/**
 * A player that always play's a random card.
 */
public class RandomPlayer extends Player {
    Random rand = new Random(System.currentTimeMillis());


    /**
     * Creat a new player that randomly chooses a card.
     * @param id
     * @param firstCard
     */
    public RandomPlayer(int id, Card firstCard) {
        super(id, firstCard);
    }

    /** random play, but will never play princess */
    @Override
    public Card chooseCardtoPlay() {
        if (card2 == null) return card1;   // should never happen
        if (card1.value == Card.PRINCESS) return playCard2();
        if (card2.value == Card.PRINCESS) return playCard1();
        return rand.nextBoolean() ? playCard1() : playCard2();
    }
    
    @Override
    public void otherPlayerHasCard(int id, int value) {
        //empty. RandomPlayer doesn't remember any cards :-)
    }
    
    /**
     * get id of other player for cards where current player needs to choose another player
     * @param cardValue either guard, priest, baron, prince or king
     * @return
     */
    public int getOtherPlayerFor(int cardValue) {
        return getAnotherPlayersIdxRand();
        
  /*      
        switch (cardValue) {
        case Card.GUARD:
            break;
        case Card.PRIEST:
            break;
        case Card.BARON:
            break;
        case Card.PRINCE:
            break;
        case Card.KING:
            break;
        default:
            Log.error("Wrong card Value in getOtherPlayerFor: "+cardValue);
            break;
        }
    */
    }
    
    /**
     * Guard: guess another player's card
     * @return value to guess (2-8). Guessing a Guard is not allowed
     */
    public int guessCardValue() {
        return 2+rand.nextInt(7);      // card value (2-8)
    }
    
    /**
     * randomly get Id of <b>another</b> player
     * @return id of another player
     */
    private int getAnotherPlayersIdxRand() {
        while (true) {
            int otherId = rand.nextInt(Board.getNumPlayers());
            if (otherId != this.id) return otherId;
        }
    }

    
}
