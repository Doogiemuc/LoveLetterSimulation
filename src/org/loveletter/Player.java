package org.loveletter;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for a love letter player.
 */
public abstract  class Player {

    /** position of this player at the table */
    int id;
    
    /** The (first) card that this player has in hand */
    Card card1;
    
    /** the card that was just drawn */
    Card card2;

    /** is this player still in the game */
    boolean inGame;
    
    /** cards that this player has already played. highest index is played last */
    List<Card> playedCards;
    
    public Player(int id, Card firstCard) {
        this.id = id;
        this.card1 = firstCard;
        this.card2 = null;
        this.inGame = true;
        this.playedCards = new ArrayList<Card>();
    }
    
    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public void drawCard(Card card) {
        this.card2 = card;
    }

    public boolean hasCardValue(int val) {
        if (card1.value == val) return true;
        if (card2 != null && card2.value == val) return true;
        return false;
    }

    protected Card playCard1() {
        Card chosenCard = this.card1;
        card1 = card2;
        card2 = null;
        playedCards.add(chosenCard);
        return chosenCard;
    }

    protected Card playCard2() {
        Card chosenCard = this.card2;
        card2 = null;
        playedCards.add(chosenCard);
        return chosenCard;
    }

    /**
     * check if this player is currently guarded
     * @return true if the last played card was a maid.
     */
    public boolean isGuarded() {
        if (playedCards.size() == 0) return false;
        return playedCards.get(playedCards.size()-1).value == Card.MAID;
    }
    
    /**
     * IF player has countess and also king or prince,
     * THEN he must play the countess.
     * This method will check and also play the countess if necessary.
     * @return true if player actually had to play the countess
     */
    public boolean mustPlayCountess() {
        // if card2 is the countess, then swap the cards so that card1 is the countess
        if (card2.value == Card.COUNTESS) {
            Card countess = card2;
            card2 = card1;
            card1 = countess;
        }
        // if card1 is the countess (either with or without swaping)
        // and the other card is king or prince, 
        // then you must play the countess
        if ( card1.value == Card.COUNTESS && 
            (card2.value == Card.KING || card2.value == Card.PRINCE)) {
            playCard1();
            Log.traceAppend(" must play contess.");
            return true;
        }
        return false;
    }
    
    //----- implement these methods in sublcasses! -----------
    
    /**
     * Which card do you want to play?
     * @return either card1 or card2
     */
    public abstract Card chooseCardtoPlay();

    
    /**
     * Get an id of another player. Only called for cards where current player needs 
     * to choose another player. 
     * Player must not return his own id!
     * @param cardValue either guard, priest, baron, prince or king
     */
    public abstract int getOtherPlayerFor(int cardValue);
  /*  implement like this:
          
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
    
    /**
     * Guard: guess another player's card
     * This method will be called after player has chosen to play a guard.
     * @return value to guess (2-8). Guessing a Guard(1) is not allowed
     */
    public abstract int guessCardValue();
    
    
    
    /**
     * let this player know the card of another player
     * @param id index of other player
     * @param value card value of other player
     */
    public abstract void otherPlayerHasCard(int id, int value);


    @Override
    public String toString() {
        return "P"+id+"["+card1+"]";
    }

}